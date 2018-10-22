/*
 * Copyright (C),2005-2018 Andrew John Jacobs.
 *
 * This program is provided free of charge for educational purposes
 *
 * Redistribution and use in binary form without modification, is permitted
 * provided that the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS 'AS IS' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.demon.obelisk.m740;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import uk.co.demon.obelisk.xapp.Option;
import uk.co.demon.obelisk.xasm.Assembler;
import uk.co.demon.obelisk.xasm.Error;
import uk.co.demon.obelisk.xasm.MemoryModelByte;
import uk.co.demon.obelisk.xasm.Opcode;
import uk.co.demon.obelisk.xasm.Pass;
import uk.co.demon.obelisk.xasm.Token;
import uk.co.demon.obelisk.xasm.TokenKind;
import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Hex;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Value;

/**
 * The <CODE>As740</CODE> provides the base <CODE>Assembler</CODE> with an
 * understanding of MELPS 740 assembler conventions.
 *
 * @author 	Andrew Jacobs
 */
public final class As740 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As740 ().run (args);
	}

	/**
	 * An <CODE>Opcode</CODE> that handles .PAGE0 directives.
	 */
	protected final Opcode	PAGE0		= new Opcode (KEYWORD, ".PAGE0")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			setSection (".page0");
			return (false);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> that handles .ADDR directives
	 */
	protected final Opcode 		ADDR	= new Opcode (KEYWORD, ".ADDR")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			do {
				token = nextRealToken ();
				Expr expr = parseExpr ();
				
				if (expr != null)
					addAddr (expr);
				else
					error (Error.ERR_INVALID_EXPRESSION);
			} while (token == COMMA);
			
			if (token != EOL) error (Error.ERR_INVALID_EXPRESSION); 
			return (true);
		}
	};

	/**
	 * A <CODE>Token</CODE> representing the '#' character.
	 */
	protected final Token 	HASH
		= new Token (KEYWORD, "#");

	/**
	 * A <CODE>Token</CODE> representing the A register.
	 */
	protected final Token 	A
		= new Token (KEYWORD, "A");

	/**
	 * A <CODE>Token</CODE> representing the S register.
	 */
	protected final Token 	S
		= new Token (KEYWORD, "S");

	/**
	 * A <CODE>Token</CODE> representing the X register.
	 */
	protected final Token 	X
		= new Token (KEYWORD, "X");

	/**
	 * A <CODE>Token</CODE> representing the Y register.
	 */
	protected final Token 	Y
		= new Token (KEYWORD, "Y");

	/**
	 * A <CODE>Token</CODE> representing the ON keyword.
	 */
	protected final Token 	ON
		= new Token (KEYWORD, "ON");

	/**
	 * A <CODE>Token</CODE> representing the OFF keyword.
	 */
	protected final Token 	OFF
		= new Token (KEYWORD, "OFF");

	/**
	 * A <CODE>Token</CODE> representing the EQ keyword.
	 */
	protected final Token 	EQ
		= new Token (KEYWORD, "EQ");

	/**
	 * A <CODE>Token</CODE> representing the NE keyword.
	 */
	protected final Token 	NE
		= new Token (KEYWORD, "NE");

	/**
	 * A <CODE>Token</CODE> representing the CC keyword.
	 */
	protected final Token 	CC
		= new Token (KEYWORD, "CC");
	
	/**
	 * A <CODE>Token</CODE> representing the CS keyword.
	 */
	protected final Token 	CS
		= new Token (KEYWORD, "CS");
	
	/**
	 * A <CODE>Token</CODE> representing the PL keyword.
	 */
	protected final Token 	PL
		= new Token (KEYWORD, "PL");

	/**
	 * A <CODE>Token</CODE> representing the MI keyword.
	 */
	protected final Token 	MI
		= new Token (KEYWORD, "MI");

	/**
	 * A <CODE>Token</CODE> representing the VC keyword.
	 */
	protected final Token 	VC
		= new Token (KEYWORD, "VC");

	/**
	 * A <CODE>Token</CODE> representing the VS keyword.
	 */
	protected final Token 	VS
		= new Token (KEYWORD, "VS");
	
	/**
	 * An extended <CODE>Opcode</CODE> class used to compile BBC and BBS
	 * instructions.
	 */
	protected final class BitBranch extends Opcode
	{
		/**
		 * Constructs a <CODE>BitOpcode</CODE> instance.
		 * 
		 * @param kind			Identifies the type of <CODE>Token</CODE>.
		 * @param text			The text string this was parsed from.
		 */
		public BitBranch (final TokenKind kind, final String text, int opcodeA, int opcodeM)
		{
			super (kind, text, false);
			
			this.opcodeA = new Value (null, opcodeA);
			this.opcodeM = new Value (null, opcodeM);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token = nextRealToken ();
			
			Expr 		bit = parseExpr ();
			
			if (token == COMMA)
				token = nextRealToken ();
			else
				error ("Expected a comma");
			
			if (token == A) {
				if (nextRealToken () == COMMA)
					token = nextRealToken ();
				else
					error ("Expected a comma");
				
				Expr		jump = parseExpr ();
				Expr		origin = getOrigin ();
				
				if (origin != null) {
					addByte (Expr.add(Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeA));
					addByte (Expr.sub (jump, Expr.add (origin, TWO)));
				}
				else
					error ("No active section");
				
			}
			else {
				Expr		addr = parseExpr ();
				
				if (token == COMMA)
					token = nextRealToken ();
				else
					error ("Expected a comma");
				
				Expr		jump = parseExpr ();
				Expr		origin = getOrigin ();
				
				if (origin != null) {
					addByte (Expr.add(Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeM));
					addByte (addr);
					addByte (Expr.sub (jump, Expr.add (origin, THREE)));
				}
				else
					error ("No active section");
			}
			
			return (true);
		}

		private Value		opcodeA;
		
		private Value		opcodeM;
	}
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADC instruction.
	 */
	protected final Opcode 	ADC		= new Opcode (KEYWORD, "ADC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x69, arg, bitsA);	break;
			case DPAG:	genDpag (0x65, arg);	break;
			case DPGX:	genDpag (0x75, arg);	break;
			case ABSL:	genAbsl (0x6D, arg);	break;
			case ABSX:	genAbsl (0x7D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x79, arg);	break;
			case INDX:	genDpag (0x61, arg);	break;
			case INDY:	genDpag (0x71, arg); 	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the AND instruction.
	 */
	protected final Opcode 	AND		= new Opcode (KEYWORD, "AND")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x29, arg, bitsA);	break;
			case DPAG:	genDpag (0x25, arg);	break;
			case ABSX:	genAbsl (0x3D, arg);	break;
			case ABSL:	genAbsl (0x2D, arg);	break;
			case DPGX:	genDpag (0x35, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x39, arg);	break;
			case INDX:	genDpag (0x21, arg);	break;
			case INDY:	genDpag (0x31, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ASL instruction.
	 */
	protected final Opcode 	ASL		= new Opcode (KEYWORD, "ASL")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x0A);	break;
			case DPAG:	genDpag (0x06, arg);	break;
			case DPGX:	genDpag (0x16, arg); 	break;
			case ABSL:	genAbsl (0x0E, arg);	break;
			case ABSX:	genAbsl (0x1E, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBC instruction.
	 */
	protected final Opcode 	BBC	= new BitBranch (KEYWORD, "BBC", 0x13, 0x17);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS instruction.
	 */
	protected final Opcode 	BBS	= new BitBranch (KEYWORD, "BBS", 0x03, 0x07);
	

	/**
	 * An <CODE>Opcode</CODE> that handles the BCC instruction.
	 */
	protected final Opcode 	BCC		= new Opcode (KEYWORD, "BCC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x90, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BCS instruction.
	 */
	protected final Opcode 	BCS		= new Opcode (KEYWORD, "BCS")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0xB0, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BEQ instruction.
	 */
	protected final Opcode 	BEQ		= new Opcode (KEYWORD, "BEQ")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0xF0, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BIT instruction.
	 */
	protected final Opcode 	BIT		= new Opcode (KEYWORD, "BIT")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x24, arg); break;
			case ABSL:	genAbsl	(0x2C, arg); break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BMI instruction.
	 */
	protected final Opcode 	BMI		= new Opcode (KEYWORD, "BMI")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x30, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BNE instruction.
	 */
	protected final Opcode 	BNE		= new Opcode (KEYWORD, "BNE")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0xD0, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BPL instruction.
	 */
	protected final Opcode 	BPL		= new Opcode (KEYWORD, "BPL")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x10, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BRA instruction.
	 */
	protected final Opcode 	BRA		= new Opcode (KEYWORD, "BRA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x80, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BRK instruction.
	 */
	protected final Opcode 	BRK		= new Opcode (KEYWORD, "BRK")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00, arg, 8);	break;
			case IMPL:	genImpl (0x00);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BVC instruction.
	 */
	protected final Opcode 	BVC		= new Opcode (KEYWORD, "BVC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x50, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BVS instruction.
	 */
	protected final Opcode 	BVS		= new Opcode (KEYWORD, "BVS")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x70, arg, false);
				break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLB instruction.
	 */
	protected final Opcode 	CLB		= new Opcode (KEYWORD, "CLB")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			Expr bit = parseExpr ();
			
			if (token != COMMA)
				error (ERR_EXPECTED_COMMA);
			
			switch (parseMode ()) {
			case ACCM:
				addByte (Expr.add (Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeA));
				break;
			
			case DPAG:
				addByte (Expr.add (Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeM));
				addByte (arg);
				break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
		
		private Value 	opcodeA = new Value (null, 0x1b);
		
		private Value 	opcodeM = new Value (null, 0x1f);
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLC instruction.
	 */
	protected final Opcode 	CLC		= new Opcode (KEYWORD, "CLC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x18);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLD instruction.
	 */
	protected final Opcode 	CLD		= new Opcode (KEYWORD, "CLD")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xD8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLI instruction.
	 */
	protected final Opcode 	CLI		= new Opcode (KEYWORD, "CLI")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x58);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLI instruction.
	 */
	protected final Opcode 	CLT		= new Opcode (KEYWORD, "CLT")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x12);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLV instruction.
	 */
	protected final Opcode 	CLV		= new Opcode (KEYWORD, "CLV")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xB8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CMP instruction.
	 */
	protected final Opcode 	CMP		= new Opcode (KEYWORD, "CMP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xC9, arg, bitsA);	break;
			case DPAG:	genDpag (0xC5, arg);	break;
			case DPGX:	genDpag (0xD5, arg);	break;
			case ABSL:	genAbsl (0xCD, arg);	break;
			case ABSX:	genAbsl (0xDD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0xD9, arg);	break;
			case INDX:	genDpag (0xC1, arg);	break;
			case INDY:	genDpag (0xD1, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the COM instruction.
	 */
	protected final Opcode 	COM		= new Opcode (KEYWORD, "COM")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x44, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CPX instruction.
	 */
	protected final Opcode 	CPX		= new Opcode (KEYWORD, "CPX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xE0, arg, bitsI);	break;
			case DPAG:	genDpag (0xE4, arg);	break;
			case ABSL:	genAbsl (0xEC, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CPY instruction.
	 */
	protected final Opcode 	CPY		= new Opcode (KEYWORD, "CPY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xC0, arg, bitsI);	break;
			case DPAG:	genDpag (0xC4, arg);	break;
			case ABSL:	genAbsl (0xCC, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the DEC instruction.
	 */
	protected final Opcode 	DEC		= new Opcode (KEYWORD, "DEC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case ACCM:	genImpl (0x1A);			break;
			case DPAG:	genDpag (0xC6, arg);	break;
			case DPGX:	genDpag (0xD6, arg);	break;
			case ABSL:	genAbsl (0xCE, arg);	break;
			case ABSX:	genAbsl (0xDE, arg);	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the DEX instruction.
	 */
	protected final Opcode 	DEX		= new Opcode (KEYWORD, "DEX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xCA);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the DEY instruction.
	 */
	protected final Opcode 	DEY		= new Opcode (KEYWORD, "DEY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x88);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the DIV instruction.
	 */
	protected final Opcode 	DIV		= new Opcode (KEYWORD, "DIV")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPGX:	genDpag (0xE2, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the EOR instruction.
	 */
	protected final Opcode 	EOR		= new Opcode (KEYWORD, "EOR")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x49, arg, bitsA);	break;
			case DPAG:	genDpag (0x45, arg);	break;
			case DPGX:	genDpag (0x55, arg);	break;
			case ABSL:	genAbsl (0x4D, arg);	break;
			case ABSX:	genAbsl (0x5D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x59, arg);	break;
			case INDX:	genDpag (0x41, arg);	break;
			case INDY:	genDpag (0x51, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the INC instruction.
	 */
	protected final Opcode 	INC		= new Opcode (KEYWORD, "INC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case ACCM:	genImpl (0x3A);			break;
			case DPAG:	genDpag (0xE6, arg);	break;
			case ABSL:	genAbsl (0xEE, arg);	break;
			case DPGX:	genDpag (0xF6, arg);	break;
			case ABSX:	genAbsl (0xFE, arg);	break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the INX instruction.
	 */
	protected final Opcode 	INX		= new Opcode (KEYWORD, "INX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xE8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the INY instruction.
	 */
	protected final Opcode 	INY		= new Opcode (KEYWORD, "INY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xC8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the JMP instruction.
	 */
	protected final Opcode 	JMP		= new Opcode (KEYWORD, "JMP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:	genAbsl (0x4C, arg);	break;
			
			case INDI:
				{
					if (arg.isAbsolute () && ((arg.resolve () & 0xff00) == 0x0000))
						genDpag (0xb2, arg);
					else
						genAbsl	(0x6c, arg);							
				}
				break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the JSR instruction.
	 */
	protected final Opcode 	JSR		= new Opcode (KEYWORD, "JSR")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:	
				{
					if (arg.isAbsolute () && ((arg.resolve () & 0xff00) == 0xff00))
						genDpag (0x22, arg);
					else
						genAbsl (0x20, arg);
					break;
				}
			
			case INDI:	genDpag (0x02, arg);	break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the LDA instruction.
	 */
	protected final Opcode 	LDA		= new Opcode (KEYWORD, "LDA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xA9, arg, bitsA);	break;
			case DPAG:	genDpag (0xA5, arg);	break;
			case DPGX:	genDpag (0xB5, arg);	break;
			case ABSL:	genAbsl (0xAD, arg);	break;
			case ABSX:	genAbsl (0xBD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0xB9, arg);	break;
			case INDX:	genDpag (0xA1, arg);	break;
			case INDY:	genDpag (0xB1, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the LDM instruction.
	 */
	protected final Opcode 	LDM		= new Opcode (KEYWORD, "LDM")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == HASH) {
				token = nextRealToken ();
				Expr	data = parseExpr ();
				
				if (token != COMMA)
					error (ERR_EXPECTED_COMMA);
				
				switch (parseMode ()) {
				case DPAG:
					addByte (0x32);
					addByte (data);
					addByte (arg);
					break;
					
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the LDX instruction.
	 */
	protected final Opcode 	LDX		= new Opcode (KEYWORD, "LDX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xA2, arg, bitsI);	break;
			case DPAG:	genDpag (0xA6, arg);	break;
			case DPGY:	genDpag (0xB6, arg);	break;
			case ABSL:	genAbsl (0xAE, arg);	break;
			case ABSY:	genAbsl (0xBE, arg);	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the LDY instruction.
	 */
	protected final Opcode	LDY		= new Opcode (KEYWORD, "LDY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xA0, arg, bitsI);	break;
			case DPAG:	genDpag (0xA4, arg);	break;
			case DPGX:	genDpag (0xB4, arg);	break;
			case ABSL:	genAbsl (0xAC, arg);	break;
			case ABSX:	genAbsl (0xBC, arg);	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the LSR instruction.
	 */
	protected final Opcode 	LSR		= new Opcode (KEYWORD, "LSR")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x4A);	break;
			case DPAG:	genDpag (0x46, arg);	break;
			case DPGX:	genDpag (0x56, arg); 	break;
			case ABSL:	genAbsl (0x4E, arg);	break;
			case ABSX:	genAbsl (0x5E, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the DIV instruction.
	 */
	protected final Opcode 	MUL		= new Opcode (KEYWORD, "MUL")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPGX:	genDpag (0x62, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the NOP instruction.
	 */
	protected final Opcode 	NOP		= new Opcode (KEYWORD, "NOP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xEA);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ORA instruction.
	 */
	protected final Opcode 	ORA		= new Opcode (KEYWORD, "ORA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x09, arg, bitsA);	break;
			case DPAG:	genDpag (0x05, arg);	break;
			case DPGX:	genDpag (0x15, arg);	break;
			case ABSL:	genAbsl (0x0D, arg);	break;
			case ABSX:	genAbsl (0x1D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x19, arg);	break;
			case INDX:	genDpag (0x01, arg);	break;
			case INDY:	genDpag (0x11, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PHA instruction.
	 */
	protected final Opcode 	PHA		= new Opcode (KEYWORD, "PHA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x48);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PHP instruction.
	 */
	protected final Opcode 	PHP		= new Opcode (KEYWORD, "PHP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x08);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PLA instruction.
	 */
	protected final Opcode 	PLA		= new Opcode (KEYWORD, "PLA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x68);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PLP instruction.
	 */
	protected final Opcode 	PLP		= new Opcode (KEYWORD, "PLP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x28);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ROL instruction.
	 */
	protected final Opcode 	ROL		= new Opcode (KEYWORD, "ROL")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x2A);	break;
			case DPAG:	genDpag (0x26, arg);	break;
			case DPGX:	genDpag (0x36, arg); 	break;
			case ABSL:	genAbsl (0x2E, arg);	break;
			case ABSX:	genAbsl (0x3E, arg);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ROR instruction.
	 */
	protected final Opcode 	ROR		= new Opcode (KEYWORD, "ROR")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x6A);	break;
			case DPAG:	genDpag (0x66, arg);	break;
			case DPGX:	genDpag (0x76, arg); 	break;
			case ABSL:	genAbsl (0x6E, arg);	break;
			case ABSX:	genAbsl (0x7E, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the RRF instruction.
	 */
	protected final Opcode 	RRF		= new Opcode (KEYWORD, "RRF")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x82, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the RTI instruction.
	 */
	protected final Opcode 	RTI		= new Opcode (KEYWORD, "RTI")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x40);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the RTS instruction.
	 */
	protected final Opcode 	RTS		= new Opcode (KEYWORD, "RTS")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x60);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SBC instruction.
	 */
	protected final Opcode 	SBC		= new Opcode (KEYWORD, "SBC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0xE9, arg, bitsA);	break;
			case DPAG:	genDpag (0xE5, arg);	break;
			case DPGX:	genDpag (0xF5, arg);	break;
			case ABSL:	genAbsl (0xED, arg);	break;
			case ABSX:	genAbsl (0xFD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0xF9, arg);	break;
			case INDX:	genDpag (0xE1, arg);	break;
			case INDY:	genDpag (0xF1, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SEB instruction.
	 */
	protected final Opcode 	SEB		= new Opcode (KEYWORD, "SEB")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			Expr bit = parseExpr ();
			
			if (token != COMMA)
				error (ERR_EXPECTED_COMMA);
			
			switch (parseMode ()) {
			case ACCM:
				addByte (Expr.add (Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeA));
				break;
			case DPAG:
				addByte (Expr.add (Expr.mul (Expr.and (bit, SEVEN), THIRTY_TWO), opcodeM));
				addByte (arg);
				break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
		
		private Value 	opcodeA = new Value (null, 0x0b);
		
		private Value 	opcodeM = new Value (null, 0x0f);
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SEC instruction.
	 */
	protected final Opcode 	SEC		= new Opcode (KEYWORD, "SEC")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x38);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SED instruction.
	 */
	protected final Opcode 	SED		= new Opcode (KEYWORD, "SED")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xF8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SEI instruction.
	 */
	protected final Opcode 	SEI		= new Opcode (KEYWORD, "SEI")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x78);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SET instruction.
	 */
	protected final Opcode 	SET		= new Opcode (KEYWORD, "SET")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x32);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the STA instruction.
	 */
	protected final Opcode 	STA		= new Opcode (KEYWORD, "STA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x85, arg);	break;
			case DPGX:	genDpag (0x95, arg);	break;
			case ABSL:	genAbsl (0x8D, arg);	break;
			case ABSX:	genAbsl (0x9D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x99, arg);	break;
			case INDX:	genDpag (0x81, arg);	break;
			case INDY:	genDpag (0x91, arg); 	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the STP instruction.
	 */
	protected final Opcode 	STP		= new Opcode (KEYWORD, "STP")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x42);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the STX instruction.
	 */
	protected final Opcode 	STX		= new Opcode (KEYWORD, "STX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x86, arg);	break;
			case DPGY:	genDpag (0x96, arg);	break;
			case ABSL:	genAbsl (0x8E, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the STY instruction.
	 */
	protected final Opcode 	STY		= new Opcode (KEYWORD, "STY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x84, arg);	break;
			case DPGX:	genDpag (0x94, arg);	break;
			case ABSL:	genAbsl (0x8C, arg);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TAX instruction.
	 */
	protected final Opcode 	TAX		= new Opcode (KEYWORD, "TAX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xAA);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TAY instruction.
	 */
	protected final Opcode 	TAY		= new Opcode (KEYWORD, "TAY")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xA8);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TST instruction.
	 */
	protected final Opcode 	TST		= new Opcode (KEYWORD, "TST")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x64, arg);	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TSX instruction.
	 */
	protected final Opcode 	TSX		= new Opcode (KEYWORD, "TSX")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xBA);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TXA instruction.
	 */
	protected final Opcode 	TXA		= new Opcode (KEYWORD, "TXA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x8A);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TXS instruction.
	 */
	protected final Opcode 	TXS		= new Opcode (KEYWORD, "TXS")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x9A);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TYA instruction.
	 */
	protected final Opcode 	TYA		= new Opcode (KEYWORD, "TYA")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x98);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the WAI instruction.
	 */
	protected final Opcode 	WIT		= new Opcode (KEYWORD, "WIT")
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0xC2);	break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the IF structured assembly
	 * command.
	 */
	protected final Opcode	IF		= new Opcode (KEYWORD, "IF")
	{
		@Override
		public boolean compile ()
		{
			int index = ifIndex++;
			
			ifs.push (new Integer (index));
			
			if (getPass () == Pass.FIRST) {
				elseAddr.add (null);
				endifAddr.add (null);
			}

			token = nextRealToken ();
			
			Expr	target = (Expr) elseAddr.elementAt (index);
		
			if (target == null) {
				target = (Expr) endifAddr.elementAt (index);
				if (target == null)	target = getOrigin ();
			}
			
			if (token == EQ) genBranch (NE, target);
			else if (token == NE) genBranch (EQ, target);
			else if (token == CC) genBranch (CS, target);
			else if (token == CS) genBranch (CC, target);
			else if (token == PL) genBranch (MI, target);
			else if (token == MI) genBranch (PL, target);
			else if (token == VC) genBranch (VS, target);
			else if (token == VS) genBranch (VC, target);
			else
				error (ERR_INVALID_CONDITIONAL);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ELSE structured assembly
	 * command.
	 */
	protected final Opcode	ELSE	= new Opcode (KEYWORD, "ELSE")
	{
		@Override
		public boolean compile ()
		{
			if (ifs.size () > 0) {
				int index = ((Integer) ifs.peek ()).intValue ();
	
				Expr	target = (Expr) endifAddr.elementAt (index);
				
				if (target == null) target = getOrigin ();

				genJump (target);
				elseAddr.set (index, getSection ().getOrigin ());
			}
			else
				error (ERR_NO_ACTIVE_IF);
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ENDIF structured assembly
	 * command.
	 */
	protected final Opcode	ENDIF	= new Opcode (KEYWORD, "ENDIF")
	{
		@Override
		public boolean compile ()
		{
			if (ifs.size () > 0) {
				int index = ((Integer) ifs.pop ()).intValue ();

				endifAddr.set(index, getSection ().getOrigin ());
			}
			else
				error (ERR_NO_ACTIVE_IF);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the REPEAT structured assembly
	 * command.
	 */
	protected final Opcode	REPEAT	= new Opcode (KEYWORD, "REPEAT")
	{
		@Override
		public boolean compile ()
		{
			int index = loopIndex++;
			
			loops.push (new Integer (index));
			
			if (getPass () == Pass.FIRST) {
				loopAddr.add (getSection ().getOrigin ());
				endAddr.add (null);
			}
			else
				loopAddr.set (index, getSection ().getOrigin ());
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the UNTIL structured assembly
	 * command.
	 */
	protected final Opcode	UNTIL	= new Opcode (KEYWORD, "UNTIL")
	{
		@Override
		public boolean compile ()
		{
			if (loops.size () > 0) {
				int index = ((Integer) loops.pop ()).intValue ();

				token = nextRealToken ();

				Expr target = (Expr) loopAddr.get (index);
				if (target == null) target = getOrigin ();

				if (token == EQ) genBranch (NE, target);
				else if (token == NE) genBranch (EQ, target);
				else if (token == CC) genBranch (CS, target);
				else if (token == CS) genBranch (CC, target);
				else if (token == PL) genBranch (MI, target);
				else if (token == MI) genBranch (PL, target);
				else if (token == VC) genBranch (VS, target);
				else if (token == VS) genBranch (VC, target);
				else
					error (ERR_INVALID_CONDITIONAL);
			
				endAddr.set(index, getSection ().getOrigin ());
			}
			else
				error (ERR_NO_ACTIVE_REPEAT);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the FOREVER structured assembly
	 * command.
	 */
	protected final Opcode	FOREVER	= new Opcode (KEYWORD, "FOREVER")
	{
		@Override
		public boolean compile ()
		{
			if (loops.size () > 0) {
				int index = ((Integer) loops.pop ()).intValue ();

				Expr target = (Expr) loopAddr.get (index);
				if (target == null) target = getOrigin ();
				
				genJump (target);
				
				endAddr.set(index, getSection ().getOrigin ());
			}
			else
				error (ERR_NO_ACTIVE_REPEAT);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the WHILE structured assembly
	 * command.
	 */
	protected final Opcode	WHILE	= new Opcode (KEYWORD, "WHILE")
	{
		@Override
		public boolean compile ()
		{
			int index = loopIndex++;
			
			loops.push (new Integer (index));
			
			if (getPass () == Pass.FIRST) {
				loopAddr.add (getSection ().getOrigin ());
				endAddr.add (null);
			}
			else
				loopAddr.set (index, getSection ().getOrigin ());
			
			token = nextRealToken ();

			Expr target = (Expr) endAddr.get (index);
			if (target == null) target = getOrigin ();

			if (token == EQ) genBranch (NE, target);
			else if (token == NE) genBranch (EQ, target);
			else if (token == CC) genBranch (CS, target);
			else if (token == CS) genBranch (CC, target);
			else if (token == PL) genBranch (MI, target);
			else if (token == MI) genBranch (PL, target);
			else if (token == VC) genBranch (VS, target);
			else if (token == VS) genBranch (VC, target);
			else
				error (ERR_INVALID_CONDITIONAL);

			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the ENDW structured assembly
	 * command.
	 */
	protected final Opcode	ENDW	= new Opcode (KEYWORD, "ENDW")
	{
		@Override
		public boolean compile ()
		{
			if (loops.size () > 0) {
				int index = ((Integer) loops.pop ()).intValue ();

				Expr target = (Expr) loopAddr.get (index);
				if (target == null) target = getOrigin ();
				
				genJump (target);
				
				endAddr.set(index, getSection ().getOrigin ());
			}
			else
				error (ERR_NO_ACTIVE_WHILE);
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CONTINUE structured assembly
	 * command.
	 */
	protected final Opcode	CONT	= new Opcode (KEYWORD, "CONTINUE")
	{
		@Override
		public boolean compile ()
		{
			if (loops.size () > 0) {
				int index = ((Integer) loops.peek ()).intValue ();
				
				token = nextRealToken ();

				Expr target = (Expr) loopAddr.get (index);
				if (target == null) target = getOrigin ();
				
				if (token == EOL) genJump (target);
				else if (token == EQ) genBranch (token, target);
				else if (token == NE) genBranch (token, target);
				else if (token == CC) genBranch (token, target);
				else if (token == CS) genBranch (token, target);
				else if (token == PL) genBranch (token, target);
				else if (token == MI) genBranch (token, target);
				else if (token == VC) genBranch (token, target);
				else if (token == VS) genBranch (token, target);
				else
					error (ERR_INVALID_CONDITIONAL);
			}
			else
				error (ERR_NO_ACTIVE_LOOP);
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the BREAK structured assembly
	 * command.
	 */
	protected final Opcode	BREAK	= new Opcode (KEYWORD, "BREAK")
	{
		@Override
		public boolean compile ()
		{
			if (loops.size () > 0) {
				int index = ((Integer) loops.peek ()).intValue ();
				
				token = nextRealToken ();

				Expr target = (Expr) endAddr.get (index);
				if (target == null) target = getOrigin ();
				
				if (token == EOL) genJump (target);
				else if (token == EQ) genBranch (token, target);
				else if (token == NE) genBranch (token, target);
				else if (token == CC) genBranch (token, target);
				else if (token == CS) genBranch (token, target);
				else if (token == PL) genBranch (token, target);
				else if (token == MI) genBranch (token, target);
				else if (token == VC) genBranch (token, target);
				else if (token == VS) genBranch (token, target);
				else
					error (ERR_INVALID_CONDITIONAL);
			}
			else
				error (ERR_NO_ACTIVE_LOOP);
			
			return (true);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> like .BYTE but which sets the high bit of
	 * every generated byte.
	 */
	protected final Opcode A2STR 	= new Opcode (KEYWORD, ".A2STR")
	{
		@Override
		public boolean compile ()
		{
			do {
				token = nextRealToken ();
				if (token.getKind () == STRING) {
					String value = token.getText();
					
					for (int index = 0; index < value.length (); ++index)
						addByte (value.charAt (index) | 0x80);
					
					token = nextRealToken ();
				}
				else {
					Expr expr = parseExpr ();
					
					if (expr != null)
						addByte (Expr.or(expr, HI_BIT));
					else
						error (Error.ERR_INVALID_EXPRESSION);
				}
			} while (token == COMMA);
			
			if (token != EOL) error (Error.ERR_INVALID_EXPRESSION); 
			return (true);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> that generates a string with the hi bit of the
	 * final byte set.
	 */
	protected final Opcode HSTR = new Opcode (KEYWORD, ".HSTR")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token.getKind () == STRING) {
				String value = token.getText();
				
				for (int index = 0; index < value.length ();) {
					char	ch = value.charAt (index++);
					
					addByte (ch | ((index < value.length ()) ? 0x00 : 0x80));
				}
				
				token = nextRealToken ();				
			}
			else
				error (".HSTR must have a string argument");
			
			return (true);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> that generates a Pascal style string.
	 */
	protected final Opcode PSTR = new Opcode (KEYWORD, ".PSTR")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token.getKind () == STRING) {
				String value = token.getText();
				
				if (value.length() > 255) {
					error ("String is too long for a Pascal string");
					return (true);
				}
				
				addByte (value.length ());
				for (int index = 0; index < value.length (); ++index)
					addByte (value.charAt (index));
				
				token = nextRealToken ();				
			}
			else
				error (".PSTR must have a string argument");

			return (true);
		}
	};
	
	protected class Jump extends Opcode
	{
		public Jump (String opcode, Token flag)
		{
			super (KEYWORD, opcode);
			
			this.flag = flag;
		}
		
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			Expr expr = parseExpr ();
			if (expr != null)
				if (flag != null)
					genBranch (flag, expr);
				else
					genJump (expr);
			else
				error (ERR_MISSING_EXPRESSION);
			
			return (true);
		}
		
		private final Token flag;
	}
	
	protected final Opcode 	JCC		= new Jump ("JCC", CC);
	protected final Opcode 	JCS		= new Jump ("JCS", CS);
	protected final Opcode 	JEQ		= new Jump ("JEQ", EQ);
	protected final Opcode 	JMI		= new Jump ("JMI", MI);
	protected final Opcode 	JNE		= new Jump ("JNE", NE);
	protected final Opcode 	JPL		= new Jump ("JPL", PL);
	protected final Opcode 	JVC		= new Jump ("JVC", VC);
	protected final Opcode 	JVS		= new Jump ("JVS", VS);
	protected final Opcode 	JPA		= new Jump ("JPA", null);
	
	
	/**
	 * Constructs an <CODE>As65</CODE> instance and initialises the object
	 * module.
	 */
	protected As740 ()
	{
		super (new Module ("MELPS-740", false));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}
	
	protected void startUp ()
	{
		// Directives
		addToken (ADDR);
		addToken (BSS);
		addToken (BYTE);
		addToken (DBYTE);
		addToken (WORD);
		addToken (LONG);
		addToken (SPACE);
		addToken (ALIGN);
		addToken (DCB);
		addToken (CODE);
		addToken (DATA);
		addToken (PAGE0);
		addToken (ORG);
		addToken (super.ELSE);
		addToken (END);
		addToken (super.ENDIF);
		addToken (ENDM);
		addToken (ENDR);
		addToken (EQU);
		addToken (EXITM);
		addToken (EXTERN);
		addToken (GLOBAL);
		addToken (super.IF);
		addToken (IFABS);
		addToken (IFNABS);
		addToken (IFREL);
		addToken (IFNREL);
		addToken (IFDEF);
		addToken (IFNDEF);
		addToken (INCLUDE);
		addToken (APPEND);
		addToken (INSERT);
		addToken (MACRO);
		addToken (ON);
		addToken (OFF);
		addToken (super.REPEAT);
		addToken (SET);
		addToken (LIST);
		addToken (NOLIST);
		addToken (PAGE);
		addToken (TITLE);
		addToken (ERROR);
		addToken (WARN);
		
		addToken (A2STR);
		addToken (HSTR);
		addToken (PSTR);
		
		// Functions
		addToken (STRLEN);
		addToken (HI);
		addToken (LO);
		addToken (super.BANK);

		// Opcodes & Registers
		addToken (A);
		addToken (ADC);
		addToken (AND);
		addToken (ASL);
		addToken (BBC);
		addToken (BBS);
		addToken (BCC);
		addToken (BCS);
		addToken (BEQ);
		addToken (BIT);
		addToken (BMI);
		addToken (BNE);
		addToken (BPL);
		addToken (BRA);
		addToken (BRK);
		addToken (BVC);
		addToken (BVS);
		addToken (CLB);
		addToken (CLC);
		addToken (CLD);
		addToken (CLI);
		addToken (CLT);
		addToken (CLV);
		addToken (CMP);
		addToken (COM);
		addToken (CPX);
		addToken (CPY);
		addToken (DEC);
		addToken (DEX);
		addToken (DEY);
		addToken (DIV);
		addToken (EOR);
		addToken (HI);
		addToken (INC);
		addToken (INX);
		addToken (INY);
		addToken (JMP);
		addToken (JSR);
		addToken (LO);
		addToken (LDA);
		addToken (LDM);
		addToken (LDX);
		addToken (LDY);
		addToken (LSR);
		addToken (MUL);
		addToken (NOP);
		addToken (ORA);
		addToken (PHA);
		addToken (PHP);
		addToken (PLA);
		addToken (PLP);
		addToken (ROL);
		addToken (ROR);
		addToken (RRF);
		addToken (RTI);
		addToken (RTS);
		addToken (SBC);
		addToken (SEB);
		addToken (SEC);
		addToken (SED);
		addToken (SEI);
		addToken (SET);
		addToken (STA);
		addToken (STP);
		addToken (STX);
		addToken (STY);
		addToken (TAX);
		addToken (TAY);
		addToken (TST);
		addToken (TSX);
		addToken (TXA);
		addToken (TXS);
		addToken (TYA);
		addToken (WIT);
		addToken (X);
		addToken (Y);

		// Structured Assembly
		if (!traditionalOption.isPresent()) {
			addToken (IF);
			addToken (ELSE);
			addToken (ENDIF);
			addToken (REPEAT);
			addToken (UNTIL);
			addToken (FOREVER);
			addToken (WHILE);
			addToken (ENDW);
			addToken (CONT);
			addToken (BREAK);
			addToken (EQ);
			addToken (NE);		
			addToken (CC);
			addToken (CS);
			addToken (PL);
			addToken (MI);
			addToken (VC);
			addToken (VS);
			
			// Expanding jumps
			addToken (JCC);
			addToken (JCS);
			addToken (JEQ);
			addToken (JMI);
			addToken (JNE);
			addToken (JPL);
			addToken (JVC);
			addToken (JVS);
			addToken (JPA);
		}
		
		super.startUp ();
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean isSupportedPass (final Pass pass)
	{
		return (true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String formatListing ()
	{
		int			byteCount = memory.getByteCount ();
		
		output.setLength (0);
		
		switch (lineType) {
		case '=':
			output.append ("         ");
			if (addr == null)
				error ("Addr is null");
			output.append (Hex.toHex (addr.resolve (null, null), 8));
			output.append (addr.isAbsolute() ? "  " : "' ");
			output.append ("        ");
			output.append (lineType);
			output.append (' ');
			break;
			
		case ' ':
			output.append ("         ");
			output.append ("        ");
			output.append ("  ");
			output.append ("        ");
			output.append (lineType);
			output.append (' ');
			break;
		
		default:
			if (isActive () && (addr != null) && ((getLabel () != null) || (lineType == ':') || (byteCount > 0))) {
				long value = addr.resolve ();
				output.append (Hex.toHex(value >> 16, 2));
				output.append (":");
				output.append (Hex.toHex (value, 4));
				output.append (addr.isAbsolute() ? "  " : "' ");
	
				for (int index = 0; index < 8; ++index) {
					if (index < byteCount) {
						int code = memory.getByte (index);
						
						if (code >= 0)
							output.append (Hex.toHex (code, 2));
						else
							output.append ("??");
					}
					else
						output.append ("  ");
				}
				output.append ((byteCount > 8) ? "> " : "  ");
				output.append (lineType);
				output.append (' ');
			}
			else {
				output.append ("                           ");
				output.append (lineType);
				output.append (' ');
			}
		}
		
		return (output.toString ());
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void startPass ()
	{
		super.startPass ();
		
		bitsA		= 8;
		bitsI		= 8;
		
		sections.put (".page0", getModule ().findSection (".page0"));
		
		ifIndex 	= 0;
		loopIndex 	= 0;
		
		title 		= "Portable MELPS 740 Assembler [18.10]";
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void endPass ()
	{
		if (ifs.size () > 0)
			error (ERR_UNTERMINATED_IFS);
		
		if (loops.size () > 0)
			error (ERR_UNTERMINATED_LOOPS);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Token readToken ()
	{
		return (scanToken ());
	}

	/**
	 * A <CODE>StringBuffer</CODE> used to build up new tokens.
	 */
	private StringBuffer	buffer	= new StringBuffer ();

	/**
	 * Extracts the next <CODE>Token</CODE> from the source line and
	 * classifies it.
	 *
	 * @return	The next <CODE>Token</CODE>.
	 */
	private Token scanToken ()
	{
		int				value = 0;

		// Handle tail comments
		if (peekChar () == ';') return (EOL);

		buffer.setLength (0);
		char ch = nextChar ();

		if (ch == '\0') return (EOL);

		// Handle white space
		if (isSpace (ch)) {
			while (isSpace (peekChar ()))
				nextChar ();
			return (WS);
		}

		// Handle characters
		switch (ch) {
		case '#':	return (HASH);
		case '^':	return (BINARYXOR);
		case '-':	return (MINUS);
		case '+':	return (PLUS);
		case '*':
			{
				if (peekChar () == '=') {
					nextChar ();
					return (ORG);
				}
				return (TIMES);
			}
			
		case '/':	return (DIVIDE);

		case ';':
			{
				// Consume characters after the start of a comment
				while (nextChar () != '\0') ;
				return (EOL);
			}

		case '%':
			{
				if (isBinary (peekChar ())) {
					buffer.append ('%');
					do {
						ch = nextChar ();
						buffer.append (ch);
						value = (value << 1) + (ch - '0');
					} while (isBinary (peekChar ()));
					return (new Token (NUMBER, buffer.toString (), new Integer (value)));
				}
				else
					return (MODULO);
			}

		case '@':
			{
				if (isOctal (peekChar ())) {
					buffer.append ('@');
					do {
						ch = nextChar ();
						buffer.append (ch);
						
						value = (value << 3) + (ch - '0');
					} while (isOctal (peekChar ()));
					return (new Token (NUMBER, buffer.toString (), new Integer (value)));
				}
				return (ORIGIN);
			}

		case '$':
			{
				if (isHexadecimal (peekChar ())) {
					buffer.append ('$');
					do {
						ch = nextChar ();
						buffer.append (ch);

						value = value << 4;
						if ((ch >= 'A') && (ch <= 'F'))
							value += ch - 'A' + 10;
						else if ((ch >= 'a') && (ch <= 'f'))
							value += ch - 'a' + 10;
						else
							value += ch - '0';
					} while (isHexadecimal (peekChar ()));
					return (new Token (NUMBER, buffer.toString (), new Integer (value)));
				}
				return (ORIGIN);
			}

		case '.':
			{
				if (isAlphanumeric (peekChar ()))
					break;
				return (ORIGIN);
			}

		case '~':	return (COMPLEMENT);
		case '=':	return (super.EQ);

		case '(':	return (LPAREN);
		case ')':	return (RPAREN);
		case ',':	return (COMMA);
		case ':':	return (COLON);

		case '!':
			{
				if (peekChar () == '=') {
					nextChar ();
					return (super.NE);
				}
				return (LOGICALNOT);
			}

		case '&':
			{
				if (peekChar () == '&') {
					nextChar ();
					return (LOGICALAND);
				}
				return (BINARYAND);
			}

		case '|':
			{
				if (peekChar () == '|') {
					nextChar ();
					return (LOGICALOR);
				}
				return (BINARYOR);
			}

		case '<':
			{
				switch (peekChar ()) {
				case '=':	nextChar (); return (LE);
				case '<':	nextChar (); return (LSHIFT);
				}
				return (LT);
			}

		case '>':
			{
				switch (peekChar ()) {
				case '=':	nextChar (); return (GE);
				case '>':	nextChar (); return (RSHIFT);
				}
				return (GT);
			}
		}

		// Handle numbers
		if (isDecimal (ch)) {
			value = ch - '0';
			while (isDecimal (peekChar ())) {
				ch = nextChar ();
				buffer.append (ch);
				value = value * 10 + (ch - '0');
			}
			return (new Token (NUMBER, buffer.toString (), new Integer (value)));
		}

		// Handle Symbols
		if ((ch == '.') || (ch == '_') || isAlpha (ch)) {
			buffer.append (ch);
			ch = peekChar ();
			while ((ch == '_') || isAlphanumeric (ch)) {
				buffer.append (nextChar ());
				ch = peekChar ();
			}
			String symbol = buffer.toString ();
			
			Token	opcode = (Token) tokens.get (symbol.toUpperCase ());

			if (opcode != null)
				return (opcode);
			else
				return (new Token (SYMBOL, symbol));
		}

		// Character Literals
		if (ch == '\'') {
			ch = nextChar ();
			while ((ch != '\0') && (ch != '\'')) {
				value <<= 8;
				if (ch == '\\') {
					switch (peekChar ()) {
					case '\t':	value |= '\t';
								nextChar ();
								break;
	
					case '\b':	value |= '\b';
								nextChar ();
								break;
	
					case '\r':	value |= '\r';
								nextChar ();
								break;
	
					case '\n':	value |= '\n';
								nextChar ();
								break;
	
					case '\\':	value |= '\\';
								nextChar ();
								break;
	
					case '\'':	value |= '\'';
								nextChar ();
								break;
	
					case '\"':	value |= '\"';
								nextChar ();
								break;
	
					default:	value |= ch;
					}
				}
				else
					value |= ch;
				
				ch = nextChar ();
			}

			if (ch != '\'')	error (ERR_CHAR_TERM);
			
			return (new Token (NUMBER, "#CHAR", new Integer (value)));
		}

		// Strings
		if (ch == '\"') {
			while (((ch = nextChar ()) != '\0') && (ch != '\"')) {
				if (ch == '\\') {
					switch (peekChar ()) {
					case 't':	buffer.append ('\t');
								nextChar ();
								continue;

					case 'b':	buffer.append ('\b');
								nextChar ();
								continue;

					case 'r':	buffer.append ('\r');
								nextChar ();
								continue;

					case 'n':	buffer.append ('\n');
								nextChar ();
								continue;

					case '\'':	buffer.append ('\'');
								nextChar ();
								continue;

					case '\"':	buffer.append ('\"');
								nextChar ();
								continue;

					case '\\':	buffer.append ('\\');
								nextChar ();
								continue;
					}
					buffer.append (ch);
				}
				else
					buffer.append (ch);
			}

			if (ch != '\"') error (ERR_STRING_TERM);
			return (new Token (STRING, buffer.toString ()));
		}

		// Anything else.
		buffer.append (ch);
		return (new Token (UNKNOWN, buffer.toString ()));
	}

	private static final String	ERR_CHAR_TERM
		= "Unterminated character constant";

	private static final String	ERR_STRING_TERM
		= "Unterminated string constant";

	private static final String	ERR_ILLEGAL_ADDR
		= "Illegal addressing mode";

	private static final String ERR_TEXT_TOO_LONG_FOR_IMMD
		= "Text literal is too long to be used in an immediate expression";

	private static final String ERR_NO_ACTIVE_IF
		= "No active IF for ELSE/ENDIF";
	
	private static final String ERR_NO_ACTIVE_REPEAT
		= "No active REPEAT for UNTIL";

	private static final String ERR_NO_ACTIVE_WHILE
		= "No active WHILE for ENDW";
	
	private static final String ERR_NO_ACTIVE_LOOP
		= "No active REPEAT or WHILE for BREAK";
	
	private static final String ERR_INVALID_CONDITIONAL
		= "Invalid conditional flag";
	
	private static final String ERR_EXPECTED_COMMA
		= "Expected a comma";
	
	private static final String ERR_UNTERMINATED_IFS
		= "Unterminated IF statement(s) in source code";
	
	private static final String ERR_UNTERMINATED_LOOPS
		= "Unterminated REPEAT/WHILE statement(s) in source code";
	
	private static final String ERR_EXPECTED_X
		= "Expected X index";
	
	private static final String ERR_EXPECTED_Y
		= "Expected Y index";

	private static final String ERR_EXPECTED_X_OR_Y
		= "Expected either X or Y index";

	private static final String ERR_EXPECTED_CLOSING_BRACKET
		= "Expected closing bracket";
	
	private static final String ERR_EXPECTED_CLOSING_PARENTHESIS
		= "Expected closing parenthesis";
	
	private static final String ERR_MISSING_EXPRESSION
		= "Missing expression";

	/**
	 * Represents an invalid addressing mode.
	 */
	private static final int	UNKN		= 0;

	/**
	 * Represents the implied addressing mode.
	 * <PRE>PHA</PRE>
	 */
	private static final int	IMPL		= 1;

	/**
	 * Represents the immediate addressing mode.
	 * <PRE>LDA #$55</PRE>
	 */
	private static final int	IMMD		= 2;

	/**
	 * Represents the accumulator addressing mode.
	 * <PRE>LSR A</PRE>
	 */
	private static final int	ACCM		= 3;

	/**
	 * Represents the direct page addressing mode.
	 * <PRE>LDA <$55</PRE>
	 */
	private static final int	DPAG		= 4;

	/**
	 * Represents the direct page indexed addressing mode.
	 * <PRE>LDA <$55,X</PRE>
	 */
	private static final int	DPGX		= 5;

	/**
	 * Represents the direct page indexed addressing mode.
	 * <PRE>LDA <$55,Y</PRE>
	 */
	private static final int	DPGY		= 6;

	/**
	 * Represents the absolute addressing mode.
	 * <PRE>LDA $5555</PRE>
	 */
	private static final int	ABSL		= 7;

	/**
	 * Represents the absolute indexed addressing mode.
	 * <PRE>LDA $5555,X</PRE>
	 */
	private static final int	ABSX		= 8;

	/**
	 * Represents the absolute indexed addressing mode.
	 * <PRE>LDA $5555,Y</PRE>
	 */
	private static final int	ABSY		= 9;

	/**
	 * Represents the indirect addressing mode.
	 * <PRE>LDA ($55)</PRE>
	 */
	private static final int	INDI		= 10;

	/**
	 * Represents the indexed indirect addressing mode.
	 * <PRE>LDA ($55,X)</PRE>
	 */
	private static final int	INDX		= 11;

	/**
	 * Represents the indirect indexed addressing mode.
	 * <PRE>LDA ($55),Y</PRE>
	 */
	private static final int	INDY		= 12;
		
	/**
	 * A constant value used in relative address calculations.
	 */
	private static final Value	TWO			= new Value (null, 2);
	
	/**
	 * A constant value used in long relative address calculations.
	 */
	private static final Value 	THREE		= new Value (null, 3);
	
	/**
	 * A constant value used in skip over branches.
	 */
	private static final Value 	FIVE		= new Value (null, 5);
	
	/**
	 * A constant value used in bit number extraction.
	 */
	private static final Value	SEVEN 		= new Value (null, 7);
	
	/**
	 * A constant value used in shifts.
	 */
	private static final Value	EIGHT 		= new Value (null, 8);
	
	/**
	 * A constant value used in bank number calculations.
	 */
	private static final Value 	SIXTEEN		= new Value (null, 16);
	
	/**
	 * A constant value used in bit instruction generation.
	 */
	private static final Value 	THIRTY_TWO	= new Value (null, 32);
	
	/**
	 * A constant value used in Apple ][ string generation.
	 */
	private static final Value 	HI_BIT		= new Value (null, 0x80);
	
	/**
	 * A constant value used in bank number calculations.
	 */
	private static final Value	BANK		= new Value (null, 0x00ff0000);
	
	/**
	 * A constant value used in bank offset calculations.
	 */
	private static final Value 	OFFSET		= new Value (null, 0x0000ffff);

	/**
	 * The argument.
	 */
	private Expr			arg;
	
	/**
	 * A flag indicating the number of bits in the A register.
	 */
	private int				bitsA;
	
	/**
	 * A flag indicating the number of bits in the X and Y registers.
	 */
	private int				bitsI;
	
	/**
	 * Determines the addressing mode used by the instruction.
	 *
	 * @return	The addressing mode.
	 */
	private int parseMode ()
	{
		token = nextRealToken ();

		if (token == EOL) return (IMPL);

		// Handle Accumulator
		if (token == A) {
			token = nextRealToken ();
			arg = null;
			return (ACCM);
		}

		// Handle Immediate
		if (token == HASH) {
			token = nextRealToken ();
			if (token == LT) {
				token = nextRealToken ();
				arg = parseImmd ();
			}
			else if (token == GT) {
				token = nextRealToken ();
				arg = Expr.shr (parseImmd (), EIGHT);
			}
			else if (token == BINARYXOR) {
				token = nextRealToken ();
				arg = Expr.shr (parseImmd (), SIXTEEN);
			}
			else
				arg = parseImmd ();
			
			return (IMMD);
		}

		// Handle <.. <..,X <..,Y
		if (token == LT) {
			token = nextRealToken ();
			arg = parseExpr ();
			
			if (arg == null)
				error (ERR_MISSING_EXPRESSION);
			
			if (token == COMMA) {
				token = nextRealToken ();
				if (token == X) {
					token = nextRealToken ();
					return (DPGX);
				}
				if (token == Y) {
					token = nextRealToken ();
					return (DPGY);
				}
				error (ERR_EXPECTED_X_OR_Y);
				return (UNKN);
			}
			return (DPAG);
		}
		
		// Handle (..,X) (..),Y, (..,S),Y and (..)
		if (token == LPAREN) {
			token = nextRealToken ();
			arg = parseExpr ();

			if (arg == null)
				error (ERR_MISSING_EXPRESSION);
			
			if (token == COMMA) {
				token = nextRealToken ();
				if (token == X) {
					token = nextRealToken ();
					if (token == RPAREN) {
						token = nextRealToken ();
						return (INDX);
					}
					error (ERR_EXPECTED_CLOSING_PARENTHESIS);
					return (UNKN);
				}
				error (ERR_EXPECTED_X);
				return (UNKN);
			}
			if (token == RPAREN) {
				token = nextRealToken ();
				if (token == COMMA) {
					token = nextRealToken ();
					if (token == Y) {
						token = nextRealToken ();
						return (INDY);
					}
					error (ERR_EXPECTED_Y);
					return (UNKN);
				}
				return (INDI);
			}
			return (UNKN);
		}
		
		// Handle |.., |..,X and |..,Y or !.., !..,X and !..,Y
		if ((token == BINARYOR) || (token == LOGICALNOT)) {
			token = nextRealToken ();
			arg = parseExpr ();

			if (arg == null)
				error (ERR_MISSING_EXPRESSION);
			
			if (token == COMMA) {
				token = nextRealToken ();
				if (token == X) {
					token = nextRealToken ();
					return (ABSX);
				}
				if (token == Y) {
					token = nextRealToken ();
					return (ABSY);
				}
				error (ERR_EXPECTED_X_OR_Y);
				return (UNKN);				
			}
			return (ABSL);
		}

		// Handle .. ..,X and ..,Y
		arg = parseExpr ();

		if (arg == null) {
			error (ERR_MISSING_EXPRESSION);
			return (ABSL);
		}
		
		if (token == COMMA) {
			token = nextRealToken ();
			if (token == X) {
				token = nextRealToken ();				
				return ((arg.isAbsolute() && isByteAddress ((int) arg.resolve (null, null))) ? DPGX : ABSX);
			}
			if (token == Y) {
				token = nextRealToken ();
				return ((arg.isAbsolute() && isByteAddress ((int) arg.resolve (null, null))) ? DPGY : ABSY);
			}
			error (ERR_EXPECTED_X_OR_Y);
			return (UNKN);
		}
		if (arg.isAbsolute()) {
			long addr = arg.resolve ();
			
			return (((addr & 0xff00) == 0) ? DPAG : ABSL);
		}
		else
			return (ABSL);
	}

	/**
	 * Parses the data value for an immediate addressing mode to allow short
	 * string literals as well as numbers.
	 * 
	 * @return	An expression containing the immediate value.
	 */
	private Expr parseImmd ()
	{
		if (token.getKind () == STRING) {
			String text = token.getText();
			
			if (text.length () > 4)
				error (ERR_TEXT_TOO_LONG_FOR_IMMD);
			
			int		value = 0;
			
			for (int index = 0; index < text.length (); ++index)
				value = (value << 8) | text.charAt (index);
			
			token = nextRealToken ();
			
			return (new Value (null, value));
		}
		else {
			Expr	result = parseExpr ();
			
			if (result == null)
				error (ERR_MISSING_EXPRESSION);

			return (result);
		}
	}
	
	/**
	 * Generate the code for an implied instruction.
	 * 
	 * @param opcode		The opcode byte.
	 */
	protected void genImpl (int opcode)
	{
		addByte (opcode);
	}

	/**
	 * Generate the code for an immediate instruction.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The immediate value.
	 * @param isLong		Determines if an 8 or 16 bit value.
	 */
	protected void genImmd (int opcode, Expr expr, int bits)
	{
		addByte (opcode);
		switch (bits) {
		case 8:		addByte (expr); break;
		case 16:	addWord (expr); break;
		case 32:	addLong (expr); break;
		}
	}

	/**
	 * Generate the code for an instruction with a direct page address.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The address expression.
	 */
	protected void genDpag (int opcode, Expr expr)
	{
		addByte (opcode);
		addByte (arg);
	}

	/**
	 * Generate the code for an instruction with a absolute address.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The address expression.
	 */
	protected void genAbsl (int opcode, Expr expr)
	{
		addByte (opcode);
		addWord (expr);
	}

	/**
	 * Generate the code for an instruction with in indirect address.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The address expression.
	 * @param isLong		Determines if an 8 or 16 bit value.
	 */
	protected void genIndi (int opcode, Expr expr, boolean isLong)
	{
		if (isLong)
			genAbsl (opcode, expr);
		else
			genDpag (opcode, expr);
	}

	/**
	 * Generate the code for an instruction with a relative address.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The address expression.
	 * @param isLong		Determines if an 8 or 16 bit value.
	 */
	protected void genRel (int opcode, final Expr expr, boolean isLong)
	{
		Expr			origin = getOrigin ();
		
		if (origin != null) {
			addByte (opcode);
			if (isLong) {
				Expr dist = Expr.sub (expr, Expr.add (origin, THREE));
				if (getPass () == Pass.FINAL) {
					if (dist.isAbsolute () && ((dist.resolve () < -32768) || (dist.resolve () > 32767)))
						error ("Relative branch is out of range");
				}
				addWord (dist);
			}
			else {
				Expr dist = Expr.sub (expr, Expr.add (origin, TWO));
				
				if (getPass () == Pass.FINAL) {
					if (dist.isAbsolute () && ((dist.resolve () < -128) || (dist.resolve () > 127)))
						error ("Relative branch is out of range");
				}
				addByte (dist);
			}
		}
		else
			error ("No active section");
	}
	
	/**
	 * Generate the code for an instruction with a long address.
	 * 
	 * @param opcode		The opcode byte.
	 * @param expr			The address expression.
	 */
	protected void genLong (int opcode, final Expr expr)
	{
		addByte (opcode);
		addAddr (expr);
	}
	
	/**
	 * Generates a conditional branch to the target location
	 * using relative instructions if possible.
	 * 
	 * @param condition		The condition causing the branch.
	 * @param target		The target address.
	 */
	protected void genBranch (Token condition, final Expr target)
	{
		if (isShortDistance (target)) {
			if (condition == EQ) genRel (0xF0, target, false);
			if (condition == NE) genRel (0xD0, target, false);
			if (condition == CC) genRel (0x90, target, false);
			if (condition == CS) genRel (0xB0, target, false);
			if (condition == PL) genRel (0x10, target, false);
			if (condition == MI) genRel (0x30, target, false);
			if (condition == VC) genRel (0x50, target, false);
			if (condition == VS) genRel (0x70, target, false);
		}
		else {
			Expr skipOver = Expr.add (getOrigin (), FIVE);
			
			if (condition == EQ) genRel (0xD0, skipOver, false);
			if (condition == NE) genRel (0xF0, skipOver, false);
			if (condition == CC) genRel (0xB0, skipOver, false);
			if (condition == CS) genRel (0x90, skipOver, false);
			if (condition == PL) genRel (0x30, skipOver, false);
			if (condition == MI) genRel (0x10, skipOver, false);
			if (condition == VC) genRel (0x70, skipOver, false);
			if (condition == VS) genRel (0x50, skipOver, false);
						
			genRel (0x80, target, true);
		}
	}
	
	/**
	 * Generates a jump to a target address using BRA if supported
	 * and within range.
	 * 
	 * @param target		The target address.
	 */
	protected void genJump (final Expr target)
	{
		if (hasShortBranch () && isShortDistance (target))
			genRel (0x80, target, false);
		else
			genAbsl (0x4C, target);
	}
	
	/**
	 * Determines if a target address is within relative branch
	 * range.
	 * 
	 * @param target		The target address.
	 * @return <CODE>true</CODE> if the target address is near.
	 */
	protected boolean isShortDistance (final Expr target)
	{
		Expr	offset 	= Expr.sub (target, Expr.add (getOrigin (), TWO));
		
		if (offset.isAbsolute ()) {
			int	distance = (int) offset.resolve (null, null);
			
			return ((distance >= -128) && (distance <= 127));
		}
 
		return (false);	
	}
	
	/**
	 * Determines if the current processor supports the BRA opcode.
	 * 
	 * @return	<CODE>true</CODE> if BRA is supported.
	 */
	protected boolean hasShortBranch ()
	{
		return (true);
	}
	
	/**
	 * Generate the series of bytes for a 24 bit address.
	 * 
	 * @param 	expr			An expression.
	 */
	protected void addAddr (final Expr expr)
	{
		addWord (Expr.and (expr, OFFSET));
		addByte (Expr.shr (Expr.and (expr, BANK), SIXTEEN));
	}
		
	/**
	 * The <CODE>Option</CODE> instance use to detect <CODE>-traditional</CODE>
	 */
	private Option				traditionalOption
		= new Option ("-traditional",	"Disables structured directives");

	/**
	 * A <CODE>Hashtable</CODE> of keyword tokens to speed up classification.
	 */
	private Hashtable<String, Token> tokens	= new Hashtable<String, Token> ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to format output.
	 */
	private StringBuffer			output 	= new StringBuffer ();
	
	private int						ifIndex;
	
	private int						loopIndex;
	
	private Stack<Integer>			ifs			= new Stack<Integer> ();
	
	private Stack<Integer>			loops		= new Stack<Integer> ();
	
	private Vector<Value>			elseAddr 	= new Vector<Value> ();
	
	private Vector<Value>			endifAddr	= new Vector<Value> ();
	
	private Vector<Value>			loopAddr	= new Vector<Value> ();
	
	private Vector<Value>			endAddr		= new Vector<Value> ();
	
	/**
	 * Adds a token to the hash table indexed by its text in UPPER case.
	 * 
	 * @param token			The <CODE>Token</CODE> to add.
	 */
	private void addToken (final Token token)
	{
		tokens.put (token.getText ().toUpperCase (), token);
	}
	
	/**
	 * Determines if an address can be represented by a byte.
	 * 
	 * @param value			The value to be tested.
	 * @return <CODE>true</CODE> if the value is a byte, <CODE>false</CODE> otherwise.
	 */
	private boolean isByteAddress (int value)
	{
		switch (value & 0xffffff00) {
		case 0x00000000:	return (true);
		default:			return (false);
		}
	}
}