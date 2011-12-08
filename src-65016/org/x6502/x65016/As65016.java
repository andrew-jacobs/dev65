/*
 * Copyright (C),2011 Andrew John Jacobs.
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

package org.x6502.x65016;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Stack;

import uk.co.demon.obelisk.xasm.Assembler;
import uk.co.demon.obelisk.xasm.ErrorHandler;
import uk.co.demon.obelisk.xasm.MemoryModelShort;
import uk.co.demon.obelisk.xasm.Pass;
import uk.co.demon.obelisk.xasm.Opcode;
import uk.co.demon.obelisk.xasm.Token;
import uk.co.demon.obelisk.xasm.TokenKind;
import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Hex;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Section;
import uk.co.demon.obelisk.xobj.Value;

/**
 * The <CODE>As65016</CODE> provides the base <CODE>Assembler</CODE> with an
 * understanding of 65xx family assembler conventions.
 *
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class As65016 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As65016 ().run (args);
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
			setSection (page0);
			return (false);
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
	 * A <CODE>Token</CODE> representing the '[' character
	 */
	protected final Token 	LBRACKET
		= new Token (KEYWORD, "[");
	
	/**
	 * A <CODE>Token</CODE> representing the ']' character
	 */
	protected final Token 	RBRACKET
		= new Token (KEYWORD, "]");
	
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
	 * An extended <CODE>Opcode</CODE> class used to compile RMB and SMB
	 * instructions.
	 */
	protected final class BitOperation extends Opcode
	{
		/**
		 * Constructs a <CODE>BitOpcode</CODE> instance.
		 * 
		 * @param kind			Identifies the type of <CODE>Token</CODE>.
		 * @param text			The text string this was parsed from.
		 */
		public BitOperation (final TokenKind kind, final String text, int opcode)
		{
			super (kind, text, false);
			
			this.opcode = opcode;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token = nextRealToken ();
			
			Expr		addr = parseExpr ();
			
			if (getOrigin () != null) {
				addByte (opcode);
				addByte (addr);
			}
			else
				error ("No active section");
			
			return (true);
		}

		private int			opcode;
	}
	
	/**
	 * An extended <CODE>Opcode</CODE> class used to compile BBR and BBS
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
		public BitBranch (final TokenKind kind, final String text, int opcode)
		{
			super (kind, text, false);
			
			this.opcode = opcode;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token = nextRealToken ();
			
			Expr		addr = parseExpr ();
			
			if (token == COMMA)
				token = nextRealToken ();
			else
				error ("Expected comma");
			
			Expr		jump   = parseExpr ();				
			Expr		origin = getOrigin ();
			
			if (origin != null) {
				addByte (opcode);
				addByte (addr);				
				addByte (Expr.sub (jump, Expr.add (origin, THREE)));
			}
			else
				error ("No active section");
			
			return (true);
		}

		private int			opcode;
	}
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADC instruction.
	 */
	protected final Opcode 	ADC		= new Opcode (KEYWORD, "ADC")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x0069, arg);	break;
			case DPAG:	genDpag (0x0065, arg);	break;
			case ABSL:	genAbsl (0x006D, arg);	break;
			case DPGX:	genDpag (0x0075, arg);	break;
			case ABSX:	genAbsl (0x007D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x0079, arg);	break;
			case INDX:	genDpag (0x0061, arg);	break;
			case INDY:	genDpag (0x0071, arg); 	break;
			case INDI:	genDpag (0x0072, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x0029, arg);	break;
			case DPAG:	genDpag (0x0025, arg);	break;
			case ABSL:	genAbsl (0x002D, arg);	break;
			case DPGX:	genDpag (0x0035, arg);	break;
			case ABSX:	genAbsl (0x003D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x0039, arg);	break;
			case INDX:	genDpag (0x0021, arg);	break;
			case INDY:	genDpag (0x0031, arg); 	break;
			case INDI:	genDpag (0x0032, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x000A);	break;
			case DPAG:	genDpag (0x0006, arg);	break;
			case ABSL:	genAbsl (0x000E, arg);	break;
			case DPGX:	genDpag (0x0016, arg); 	break;
			case ABSX:	genAbsl (0x001E, arg);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR0 instruction.
	 */
	protected final Opcode 	BBR0	= new BitBranch (KEYWORD, "BBR0", 0x000F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR1 instruction.
	 */
	protected final Opcode 	BBR1	= new BitBranch (KEYWORD, "BBR1", 0x001F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR2 instruction.
	 */
	protected final Opcode 	BBR2	= new BitBranch (KEYWORD, "BBR2", 0x002F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR3 instruction.
	 */
	protected final Opcode 	BBR3	= new BitBranch (KEYWORD, "BBR3", 0x003F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR4 instruction.
	 */
	protected final Opcode 	BBR4	= new BitBranch (KEYWORD, "BBR4", 0x004F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR5 instruction.
	 */
	protected final Opcode 	BBR5	= new BitBranch (KEYWORD, "BBR5", 0x005F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR6 instruction.
	 */
	protected final Opcode 	BBR6	= new BitBranch (KEYWORD, "BBR6", 0x006F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBR7 instruction.
	 */
	protected final Opcode 	BBR7	= new BitBranch (KEYWORD, "BBR7", 0x007F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS0 instruction.
	 */
	protected final Opcode 	BBS0	= new BitBranch (KEYWORD, "BBS0", 0x008F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS1 instruction.
	 */
	protected final Opcode 	BBS1	= new BitBranch (KEYWORD, "BBS1", 0x009F);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS2 instruction.
	 */
	protected final Opcode 	BBS2	= new BitBranch (KEYWORD, "BBS2", 0x00AF);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS3 instruction.
	 */
	protected final Opcode 	BBS3	= new BitBranch (KEYWORD, "BBS3", 0x00BF);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS4 instruction.
	 */
	protected final Opcode 	BBS4	= new BitBranch (KEYWORD, "BBS4", 0x00CF);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS5 instruction.
	 */
	protected final Opcode 	BBS5	= new BitBranch (KEYWORD, "BBS5", 0x00DF);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS6 instruction.
	 */
	protected final Opcode 	BBS6	= new BitBranch (KEYWORD, "BBS6", 0x00EF);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the BBS7 instruction.
	 */
	protected final Opcode 	BBS7	= new BitBranch (KEYWORD, "BBS7", 0x00FF);
	

	/**
	 * An <CODE>Opcode</CODE> that handles the BCC instruction.
	 */
	protected final Opcode 	BCC		= new Opcode (KEYWORD, "BCC")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0090, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x00B0, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x00F0, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0024, arg); 	break;
			case ABSL:	genAbsl	(0x002C, arg); 	break;
			case IMMD:	genImmd (0x0039, arg); 	break;
			case DPGX:	genDpag (0x0034, arg);	break;	
			case ABSX:	genAbsl (0x003C, arg);	break;
				
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0030, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x00D0, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0010, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0080, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x0000, arg);	break;
			case IMPL:	genImpl (0x0000);		break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0050, arg, false);
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:
				genRel (0x0070, arg, false);
				break;
				
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the CLC instruction.
	 */
	protected final Opcode 	CLC		= new Opcode (KEYWORD, "CLC")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0018);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00D8);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0058);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00B8);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00C9, arg);	break;
			case DPAG:	genDpag (0x00C5, arg);	break;
			case ABSL:	genAbsl (0x00CD, arg);	break;
			case DPGX:	genDpag (0x00D5, arg);	break;
			case ABSX:	genAbsl (0x00DD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x00D9, arg);	break;
			case INDX:	genDpag (0x00C1, arg);	break;
			case INDY:	genDpag (0x00D1, arg); 	break;
			case INDI:	genDpag (0x00D2, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00E0, arg);	break;
			case DPAG:	genDpag (0x00E4, arg);	break;
			case ABSL:	genAbsl (0x00EC, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00C0, arg);	break;
			case DPAG:	genDpag (0x00C4, arg);	break;
			case ABSL:	genAbsl (0x00CC, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x00C6, arg);	break;
			case ABSL:	genAbsl (0x00CE, arg);	break;
			case DPGX:	genDpag (0x00D6, arg);	break;
			case ABSX:	genAbsl (0x00DE, arg);	break;
			case ACCM:	genImpl (0x003A);	break;

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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00CA);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0088);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x0049, arg);	break;
			case DPAG:	genDpag (0x0045, arg);	break;
			case ABSL:	genAbsl (0x004D, arg);	break;
			case DPGX:	genDpag (0x0055, arg);	break;
			case ABSX:	genAbsl (0x005D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x0059, arg);	break;
			case INDX:	genDpag (0x0041, arg);	break;
			case INDY:	genDpag (0x0051, arg); 	break;
			case INDI:	genDpag (0x0052, arg);	break;
				
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x00E6, arg);	break;
			case ABSL:	genAbsl (0x00EE, arg);	break;
			case DPGX:	genDpag (0x00F6, arg);	break;
			case ABSX:	genAbsl (0x00FE, arg);	break;
			case ACCM:	genImpl (0x001A);			break;
				
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00E8);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00C8);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:	genAbsl (0x004C, arg);	break;
			case INDI:	genIndi	(0x006C, arg, true);	break;
			case INDX:	genAbsl (0x007C, arg);	break;
							
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:
			case ABSL:	genAbsl (0x0020, arg);	break;
							
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00A9, arg);	break;
			case DPAG:	genDpag (0x00A5, arg);	break;
			case ABSL:	genAbsl (0x00AD, arg);	break;
			case DPGX:	genDpag (0x00B5, arg);	break;
			case ABSX:	genAbsl (0x00BD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x00B9, arg);	break;
			case INDX:	genDpag (0x00A1, arg);	break;
			case INDY:	genDpag (0x00B1, arg); 	break;
			case INDI:	genDpag (0x00B2, arg);	break;

			default:
				error (ERR_ILLEGAL_ADDR);
			}
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00A2, arg);	break;
			case DPAG:	genDpag (0x00A6, arg);	break;
			case ABSL:	genAbsl (0x00AE, arg);	break;
			case DPGY:	genDpag (0x00B6, arg);	break;
			case ABSY:	genAbsl (0x00BE, arg);	break;

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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00A0, arg);	break;
			case DPAG:	genDpag (0x00A4, arg);	break;
			case ABSL:	genAbsl (0x00AC, arg);	break;
			case DPGX:	genDpag (0x00B4, arg);	break;
			case ABSX:	genAbsl (0x00BC, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x004A);	break;
			case DPAG:	genDpag (0x0046, arg);	break;
			case ABSL:	genAbsl (0x004E, arg);	break;
			case DPGX:	genDpag (0x0056, arg); 	break;
			case ABSX:	genAbsl (0x005E, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00EA);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x0009, arg);	break;
			case DPAG:	genDpag (0x0005, arg);	break;
			case ABSL:	genAbsl (0x000D, arg);	break;
			case DPGX:	genDpag (0x0015, arg);	break;
			case ABSX:	genAbsl (0x001D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x0019, arg);	break;
			case INDX:	genDpag (0x0001, arg);	break;
			case INDY:	genDpag (0x0011, arg); 	break;
			case INDI:	genDpag (0x0012, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0048);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0008);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PHX instruction.
	 */
	protected final Opcode 	PHX		= new Opcode (KEYWORD, "PHX")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00DA);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PHY instruction.
	 */
	protected final Opcode 	PHY		= new Opcode (KEYWORD, "PHY")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x005A);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0068);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0028);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PLX instruction.
	 */
	protected final Opcode 	PLX		= new Opcode (KEYWORD, "PLX")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00FA);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the PLY instruction.
	 */
	protected final Opcode 	PLY		= new Opcode (KEYWORD, "PLY")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x007A);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB0 instruction.
	 */
	protected final Opcode 	RMB0	= new BitOperation (KEYWORD, "RMB0", 0x0007);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB1 instruction.
	 */
	protected final Opcode 	RMB1	= new BitOperation (KEYWORD, "RMB1", 0x0017);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB2 instruction.
	 */
	protected final Opcode 	RMB2	= new BitOperation (KEYWORD, "RMB2", 0x0027);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB3 instruction.
	 */
	protected final Opcode 	RMB3	= new BitOperation (KEYWORD, "RMB3", 0x0037);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB4 instruction.
	 */
	protected final Opcode 	RMB4	= new BitOperation (KEYWORD, "RMB4", 0x0047);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB5 instruction.
	 */
	protected final Opcode 	RMB5	= new BitOperation (KEYWORD, "RMB5", 0x0057);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB6 instruction.
	 */
	protected final Opcode 	RMB6	= new BitOperation (KEYWORD, "RMB6", 0x0067);

	/**
	 * An <CODE>Opcode</CODE> that handles the RMB7 instruction.
	 */
	protected final Opcode 	RMB7	= new BitOperation (KEYWORD, "RMB7", 0x0077);

	/**
	 * An <CODE>Opcode</CODE> that handles the ROL instruction.
	 */
	protected final Opcode 	ROL		= new Opcode (KEYWORD, "ROL")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x002A);	break;
			case DPAG:	genDpag (0x0026, arg);	break;
			case ABSL:	genAbsl (0x002E, arg);	break;
			case DPGX:	genDpag (0x0036, arg); 	break;
			case ABSX:	genAbsl (0x003E, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:
			case ACCM:	genImpl (0x006A);	break;
			case DPAG:	genDpag (0x0066, arg);	break;
			case ABSL:	genAbsl (0x006E, arg);	break;
			case DPGX:	genDpag (0x0076, arg); 	break;
			case ABSX:	genAbsl (0x007E, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0040);	break;
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0060);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMMD:	genImmd (0x00E9, arg);	break;
			case DPAG:	genDpag (0x00E5, arg);	break;
			case ABSL:	genAbsl (0x00ED, arg);	break;
			case DPGX:	genDpag (0x00F5, arg);	break;
			case ABSX:	genAbsl (0x00FD, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x00F9, arg);	break;
			case INDX:	genDpag (0x00E1, arg);	break;
			case INDY:	genDpag (0x00F1, arg); 	break;
			case INDI:	genDpag (0x00F2, arg);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SEC instruction.
	 */
	protected final Opcode 	SEC		= new Opcode (KEYWORD, "SEC")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0038);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00F8);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0078);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB0 instruction.
	 */
	protected final Opcode 	SMB0	= new BitOperation (KEYWORD, "SMB0", 0x0087);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB1 instruction.
	 */
	protected final Opcode 	SMB1	= new BitOperation (KEYWORD, "SMB1", 0x0097);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB2 instruction.
	 */
	protected final Opcode 	SMB2	= new BitOperation (KEYWORD, "SMB2", 0x00A7);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB3 instruction.
	 */
	protected final Opcode 	SMB3	= new BitOperation (KEYWORD, "SMB3", 0x00B7);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB4 instruction.
	 */
	protected final Opcode 	SMB4	= new BitOperation (KEYWORD, "SMB4", 0x00C7);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB5 instruction.
	 */
	protected final Opcode 	SMB5	= new BitOperation (KEYWORD, "SMB5", 0x00D7);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB6 instruction.
	 */
	protected final Opcode 	SMB6	= new BitOperation (KEYWORD, "SMB6", 0x00E7);

	/**
	 * An <CODE>Opcode</CODE> that handles the SMB7 instruction.
	 */
	protected final Opcode 	SMB7	= new BitOperation (KEYWORD, "SMB7", 0x00F7);

	/**
	 * An <CODE>Opcode</CODE> that handles the STA instruction.
	 */
	protected final Opcode 	STA		= new Opcode (KEYWORD, "STA")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0085, arg);	break;
			case ABSL:	genAbsl (0x008D, arg);	break;
			case DPGX:	genDpag (0x0095, arg);	break;
			case ABSX:	genAbsl (0x009D, arg);	break;
			case DPGY:
			case ABSY:	genAbsl (0x0099, arg);	break;
			case INDX:	genDpag (0x0081, arg);	break;
			case INDY:	genDpag (0x0091, arg); 	break;
			case INDI:	genDpag (0x0092, arg);	break;

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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00DB);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0086, arg);	break;
			case ABSL:	genAbsl (0x008E, arg);	break;
			case DPGY:	genDpag (0x0096, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0084, arg);	break;
			case ABSL:	genAbsl (0x008C, arg);	break;
			case DPGX:	genDpag (0x0094, arg);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the STZ instruction.
	 */
	protected final Opcode 	STZ		= new Opcode (KEYWORD, "STZ")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0064, arg);	break;
			case ABSL:	genAbsl (0x009C, arg);	break;
			case DPGX:	genDpag (0x0074, arg);	break;
			case ABSX:	genAbsl (0x009E, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00AA);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00A8);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TRB instruction.
	 */
	protected final Opcode 	TRB		= new Opcode (KEYWORD, "TRB")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0014, arg);	break;
			case ABSL:	genAbsl (0x001C, arg);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the TSB instruction.
	 */
	protected final Opcode 	TSB		= new Opcode (KEYWORD, "TSB")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case DPAG:	genDpag (0x0004, arg);	break;
			case ABSL:	genAbsl (0x000C, arg);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00BA);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x008A);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x009A);	break;
			
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
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x0098);	break;
			
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the WAI instruction.
	 */
	protected final Opcode 	WAI		= new Opcode (KEYWORD, "WAI")
	{
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			switch (parseMode ()) {
			case IMPL:	genImpl (0x00CB);	break;
			
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
		public boolean compile ()
		{
			int index = loopIndex++;
			
			loops.push (new Integer (index));
			
			if (getPass () == Pass.FIRST) {
				loopAddr.add (getOrigin ());
				endAddr.add (null);
			}
			return (true);
		}
	};

	/**
	 * An <CODE>Opcode</CODE> that handles the UNTIL structured assembly
	 * command.
	 */
	protected final Opcode	UNTIL	= new Opcode (KEYWORD, "UNTIL")
	{
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
		public boolean compile ()
		{
			int index = loopIndex++;
			
			loops.push (new Integer (index));
			
			if (getPass () == Pass.FIRST) {
				loopAddr.add (getOrigin ());
				endAddr.add (null);
			}
			
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
	
	protected final ErrorHandler	errorHandler
		= new ErrorHandler ()
		{
			public void error (final String message)
			{
				this.error (message);
			}
			
			public void warning (final String message)
			{
				this.warning (message);
			}				
		};


	/**
	 * Constructs an <CODE>As65</CODE> instance and initialises the object
	 * module.
	 */
	protected As65016 ()
	{
		super (new Module ("65016", false, 16));

		setMemoryModel (new MemoryModelShort (errorHandler));

		// Directives
		addToken (BSS);
		addToken (BYTE);
		addToken (DBYTE);
		addToken (WORD);
		addToken (LONG);
		addToken (SPACE);
		addToken (CODE);
		addToken (DATA);
		addToken (BSS);
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

		// Opcodes & Registers
		addToken (A);
		addToken (ADC);
		addToken (AND);
		addToken (ASL);
		addToken (BBR0);
		addToken (BBR1);
		addToken (BBR2);
		addToken (BBR3);
		addToken (BBR4);
		addToken (BBR5);
		addToken (BBR6);
		addToken (BBR7);
		addToken (BBS0);
		addToken (BBS1);
		addToken (BBS2);
		addToken (BBS3);
		addToken (BBS4);
		addToken (BBS5);
		addToken (BBS6);
		addToken (BBS7);
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
		addToken (CLC);
		addToken (CLD);
		addToken (CLI);
		addToken (CLV);
		addToken (CMP);
		addToken (CPX);
		addToken (CPY);
		addToken (DEC);
		addToken (DEX);
		addToken (DEY);
		addToken (EOR);
		addToken (HI);
		addToken (INC);
		addToken (INX);
		addToken (INY);
		addToken (JMP);
		addToken (JSR);
		addToken (LO);
		addToken (LDA);
		addToken (LDX);
		addToken (LDY);
		addToken (LSR);
		addToken (NOP);
		addToken (ORA);
		addToken (PHA);
		addToken (PHP);
		addToken (PHX);
		addToken (PHY);
		addToken (PLA);
		addToken (PLP);
		addToken (PLX);
		addToken (PLY);
		addToken (RMB0);
		addToken (RMB1);
		addToken (RMB2);
		addToken (RMB3);
		addToken (RMB4);
		addToken (RMB5);
		addToken (RMB6);
		addToken (RMB7);
		addToken (ROL);
		addToken (ROR);
		addToken (RTI);
		addToken (RTS);
		addToken (SBC);
		addToken (SEC);
		addToken (SED);
		addToken (SEI);
		addToken (SMB0);
		addToken (SMB1);
		addToken (SMB2);
		addToken (SMB3);
		addToken (SMB4);
		addToken (SMB5);
		addToken (SMB6);
		addToken (SMB7);
		addToken (STA);
		addToken (STP);
		addToken (STX);
		addToken (STY);
		addToken (STZ);
		addToken (TAX);
		addToken (TAY);
		addToken (TRB);
		addToken (TSB);
		addToken (TSX);
		addToken (TXA);
		addToken (TXS);
		addToken (TYA);
		addToken (WAI);
		addToken (X);
		addToken (Y);

		// Structured Assembly
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
		output.setLength (0);
		
		switch (lineType) {
		case '=':
			output.append ("          ");
			output.append (Hex.toHex (addr.resolve (null, null), 8));
			output.append (addr.isAbsolute() ? "  " : "' ");
			output.append ("        ");
			output.append (lineType);
			output.append (' ');
			break;
		
		default:
			if ((addr != null) && ((getLabel () != null) || (lineType == ':') || (memory.getByteCount () > 0))) {
				output.append (Hex.toHex (addr.resolve (null, null), 8));
				output.append (addr.isAbsolute() ? "  " : "' ");
	
				for (int index = 0; index < 4; ++index) {
					if (index < memory.getByteCount ())
						output.append (Hex.toHex (memory.getByte (index), 4));
					else
						output.append ("    ");
				}
				output.append ((memory.getByteCount () > 4) ? "> " : "  ");
				output.append (lineType);
				output.append (' ');
			}
			else {
				output.append ("                            ");
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
						
		page0 		= getModule ().findSection (".page0");
		
		ifIndex 	= 0;
		loopIndex 	= 0;
		
		title 		= "Portable 65016 Assembler - V1.0 (2011-11-19)";
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
		case '[':	return (LBRACKET);
		case ']':	return (RBRACKET);
		case ',':	return (COMMA);
		case ':':	return (COLON);

		case '!':
			{
				if (peekChar () == '=') {
					nextChar ();
					return (NE);
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
	
	private static final String ERR_EXPECTED_CLOSING_PARENTHESIS
		= "Expected closing parenthesis";

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
	 * A constant value used in shifts.
	 */
	private static final Value	EIGHT 		= new Value (null, 8);
	
	/**
	 * A constant value used in bank number calculations.
	 */
	private static final Value 	SIXTEEN		= new Value (null, 16);
	
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
				
		// Handle (..,X) (..),Y and (..)
		if (token == LPAREN) {
			token = nextRealToken ();
			arg = parseExpr ();
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
		if ((arg = parseExpr ()) == null)
			return (UNKN);
	
		if (token == COMMA) {
			token = nextRealToken ();
			if (token == X) {
				token = nextRealToken ();				
				return ((arg.isAbsolute() && isByteAddress (arg.resolve (null, null))) ? DPGX : ABSX);
			}
			if (token == Y) {
				token = nextRealToken ();
				return ((arg.isAbsolute() && isByteAddress (arg.resolve (null, null))) ? DPGY : ABSY);
			}
			error (ERR_EXPECTED_X_OR_Y);
			return (UNKN);
		}			
		if (arg.isAbsolute()) {
			int addr = (int) arg.resolve (null, null);
			
			return (((addr & 0xffff0000) == 0) ? DPAG : ABSL);
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
		else
			return (parseExpr ());
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
	 */
	protected void genImmd (int opcode, Expr expr)
	{
		addByte (opcode);
		addByte (expr);
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
			if (isLong)
				addWord (Expr.sub (expr, Expr.add (origin, THREE)));
			else
				addByte (Expr.sub (expr, Expr.add (origin, TWO)));
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
			if (condition == EQ) genRel (0x00F0, target, false);
			if (condition == NE) genRel (0x00D0, target, false);
			if (condition == CC) genRel (0x0090, target, false);
			if (condition == CS) genRel (0x00B0, target, false);
			if (condition == PL) genRel (0x0010, target, false);
			if (condition == MI) genRel (0x0030, target, false);
			if (condition == VC) genRel (0x0050, target, false);
			if (condition == VS) genRel (0x0070, target, false);
		}
		else {
			Expr skipOver = Expr.add (getOrigin (), FIVE);
			
			if (condition == EQ) genRel (0x00D0, skipOver, false);
			if (condition == NE) genRel (0x00F0, skipOver, false);
			if (condition == CC) genRel (0x00B0, skipOver, false);
			if (condition == CS) genRel (0x0090, skipOver, false);
			if (condition == PL) genRel (0x0030, skipOver, false);
			if (condition == MI) genRel (0x0010, skipOver, false);
			if (condition == VC) genRel (0x0070, skipOver, false);
			if (condition == VS) genRel (0x0050, skipOver, false);
						
			genAbsl (0x4C, target);
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
		
		if (offset.isAbsolute ())
			return (isByte (offset.resolve (null, null)));
 
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
	 * A <CODE>Hashtable</CODE> of keyword tokens to speed up classification.
	 */
	private Hashtable<String, Token> tokens	= new Hashtable<String, Token> ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to format output.
	 */
	private StringBuffer			output 	= new StringBuffer ();
	
	/**
	 * The default page0 section.
	 */
	private Section					page0;
	
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
	 * Determines if a value can be represented by a byte.
	 * 
	 * @param value			The value to be tested.
	 * @return <CODE>true</CODE> if the value is a byte, <CODE>false</CODE> otherwise.
	 */
	private boolean isByte (long value)
	{
		long	masked = value & 0xffffffffffff0000L;
		
		return ((masked == 0x0000000000000000L) || (masked == 0xffffffffffff0000L));
	}
	
	/**
	 * Determines if an address can be represented by a byte.
	 * 
	 * @param value			The value to be tested.
	 * @return <CODE>true</CODE> if the value is a byte, <CODE>false</CODE> otherwise.
	 */
	private boolean isByteAddress (long value)
	{
		long	masked = value & 0xffffffffffff0000L;
		
		return (masked == 0x0000000000000000L);
	}
}