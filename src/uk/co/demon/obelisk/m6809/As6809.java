/*
 * Copyright (C),2017 Andrew John Jacobs.
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

package uk.co.demon.obelisk.m6809;

import java.util.Hashtable;

import uk.co.demon.obelisk.xasm.Assembler;
import uk.co.demon.obelisk.xasm.MemoryModelByte;
import uk.co.demon.obelisk.xasm.Opcode;
import uk.co.demon.obelisk.xasm.Pass;
import uk.co.demon.obelisk.xasm.Token;
import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Hex;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Value;

public class As6809 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As6809 ().run (args);
	}

	/**
	 * A <CODE>Token</CODE> representing the '#' character.
	 */
	protected final Token 	HASH
		= new Token (KEYWORD, "#");

	/**
	 * A <CODE>Token</CODE> representing the '[' character.
	 */
	protected final Token 	LBRACKET
		= new Token (KEYWORD, "[");

	/**
	 * A <CODE>Token</CODE> representing the ']' character.
	 */
	protected final Token 	RBRACKET
		= new Token (KEYWORD, "]");

	/**
	 * A <CODE>Token</CODE> representing the A register.
	 */
	protected final Token 	A
		= new Token (KEYWORD, "A");

	/**
	 * A <CODE>Token</CODE> representing the B register.
	 */
	protected final Token 	B
		= new Token (KEYWORD, "B");

	/**
	 * A <CODE>Token</CODE> representing the D register.
	 */
	protected final Token 	D
		= new Token (KEYWORD, "D");

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
	 * A <CODE>Token</CODE> representing the S register.
	 */
	protected final Token 	S
		= new Token (KEYWORD, "S");
	
	/**
	 * A <CODE>Token</CODE> representing the U register.
	 */
	protected final Token 	U
		= new Token (KEYWORD, "U");
	
	/**
	 * A <CODE>Token</CODE> representing the PC register.
	 */
	protected final Token 	PC
		= new Token (KEYWORD, "PC");
	
	protected final Opcode ADC = new Opcode (KEYWORD, "ADC")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x89 : 0xc9, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x99 : 0xd9, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb9 : 0xf9, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa9 : 0xe9, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			else
				error (ERR_MISSING_A_OR_B);
			
			return (true);
		}
	};

	
	protected final Opcode BCC = new Opcode (KEYWORD, "BCC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x24, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BCS = new Opcode (KEYWORD, "BCS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x25, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BEQ = new Opcode (KEYWORD, "BEQ")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x27, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BGE = new Opcode (KEYWORD, "BGE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2c, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BGT = new Opcode (KEYWORD, "BGT")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2e, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BHI = new Opcode (KEYWORD, "BHI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x22, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BLE = new Opcode (KEYWORD, "BLE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2f, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BLS = new Opcode (KEYWORD, "BLS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x23, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BLO = new Opcode (KEYWORD, "BLO")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x23, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BLT = new Opcode (KEYWORD, "BLT")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2d, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BMI = new Opcode (KEYWORD, "BMI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2b, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BNE = new Opcode (KEYWORD, "BNE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x26, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BPL = new Opcode (KEYWORD, "BPL")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2a, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BRA = new Opcode (KEYWORD, "BRA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x20, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BRN = new Opcode (KEYWORD, "BRN")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x20, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BSR = new Opcode (KEYWORD, "BSR")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x8d, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BVC = new Opcode (KEYWORD, "BVC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x28, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode BVS = new Opcode (KEYWORD, "BVS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x29, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBCC = new Opcode (KEYWORD, "LBCC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x24, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBCS = new Opcode (KEYWORD, "LBCS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x25, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBEQ = new Opcode (KEYWORD, "LBEQ")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x27, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBGE = new Opcode (KEYWORD, "LBGE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2c, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBGT = new Opcode (KEYWORD, "LBGT")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2e, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBHI = new Opcode (KEYWORD, "LBHI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x22, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBLE = new Opcode (KEYWORD, "LBLE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2f, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBLS = new Opcode (KEYWORD, "LBLS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x23, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBLT = new Opcode (KEYWORD, "LBLT")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2d, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBMI = new Opcode (KEYWORD, "LBMI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2b, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBNE = new Opcode (KEYWORD, "LBNE")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x26, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBPL = new Opcode (KEYWORD, "LBPL")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x2a, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBRA = new Opcode (KEYWORD, "LBRA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x20, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBRN = new Opcode (KEYWORD, "LBRN")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x20, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBSR = new Opcode (KEYWORD, "LBSR")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x8d, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBVC = new Opcode (KEYWORD, "LBVC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x28, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LBVS = new Opcode (KEYWORD, "LBVS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genRelative (0x29, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	
	
	
	/**
	 * Constructs an <CODE>As8080</CODE> instance and initialises the object
	 * module.
	 */
	protected As6809 ()
	{
		super (new Module ("6809", true));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}
	
	protected void startUp ()
	{
		// Directives
		addToken (APPEND);
		addToken (BSS);
		addToken (BYTE);
		addToken (CODE);
		addToken (DATA);
		addToken (DBYTE);
		addToken (DCB);
		addToken (ELSE);
		addToken (END);
		addToken (ENDIF);
		addToken (ENDM);
		addToken (ENDR);
		addToken (EQU);
		addToken (ERROR);
		addToken (EXITM);
		addToken (EXTERN);
		addToken (GLOBAL);
		addToken (IF);
		addToken (IFABS);
		addToken (IFNABS);
		addToken (IFREL);
		addToken (IFNREL);
		addToken (IFDEF);
		addToken (IFNDEF);
		addToken (INCLUDE);
		addToken (INSERT);
		addToken (LIST);
		addToken (LONG);
		addToken (MACRO);
		addToken (NOLIST);
		addToken (ORG);
		addToken (PAGE);
		addToken (REPEAT);
		addToken (SET);
		addToken (SPACE);
		addToken (TITLE);
		addToken (WARN);
		addToken (WORD);
		
		// Opcodes
		addToken (ABX);
		addToken (ADC);
		addToken (ADD);
		addToken (AND);
		addToken (ASL);
		addToken (ASR);
		addToken (BCC);
		addToken (BCS);
		addToken (BEQ);
		addToken (BGE);
		addToken (BGT);
		addToken (BHI);
		addToken (BHS);
		addToken (BIT);
		addToken (BLE);
		addToken (BLO);
		addToken (BLS);
		addToken (BLT);
		addToken (BMI);
		addToken (BNE);
		addToken (BPL);
		addToken (BRA);
		addToken (BRN);
		addToken (BSR);
		addToken (BVC);
		addToken (BVS);
		addToken (CLR);
		addToken (CMP);
		addToken (COM);
		addToken (CWAI);
		addToken (DAA);
		addToken (DEC);
		addToken (EOR);
		addToken (EXG);
		addToken (INC);
		addToken (JMP);
		addToken (JSR);
		addToken (LBCC);
		addToken (LBCS);
		addToken (LBEQ);
		addToken (LBGE);
		addToken (LBGT);
		addToken (LBHI);
		addToken (LBLE);
		addToken (LBLO);
		addToken (LBLS);
		addToken (LBLT);
		addToken (LBMI);
		addToken (LBNE);
		addToken (LBPL);
		addToken (LBRA);
		addToken (LBRN);
		addToken (LBSR);
		addToken (LBVC);
		addToken (LBVS);
		addToken (LD);
		addToken (LEA);
		addToken (LSL);
		addToken (LSR);
		addToken (MUL);
		addToken (NEG);
		addToken (NOP);
		addToken (OR);
		addToken (PSH);
		addToken (PUL);
		addToken (ROL);
		addToken (ROR);
		addToken (RTI);
		addToken (RTS);
		addToken (SBC);
		addToken (SEX);
		addToken (ST);
		addToken (SUB);
		addToken (SWI);
		addToken (SYNC);
		addToken (TFR);
		addToken (TST);
		
		// Registers
		addToken (A);
		addToken (B);
		addToken (D);
		addToken (X);
		addToken (Y);
		addToken (U);
		addToken (S);
		addToken (PC);

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
	protected void startPass ()
	{
		super.startPass ();
		
		title = "Portable Motorola 6800 Assembler [17.08]";
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected Token readToken ()
	{
		return (scanToken ());
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
			output.append ("        ");
			output.append (Hex.toHex (addr.resolve (null, null), 8));
			output.append (addr.isAbsolute() ? "  " : "' ");
			output.append ("        ");
			output.append (lineType);
			output.append (' ');
			break;
			
		case ' ':
			output.append ("        ");
			output.append ("        ");
			output.append ("  ");
			output.append ("        ");
			output.append (lineType);
			output.append (' ');
			break;
		
		default:
			if (isActive () && (addr != null) && ((getLabel () != null) || (lineType == ':') || (byteCount > 0))) {
				output.append (Hex.toHex (addr.resolve (null, null), 6));
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
				output.append ("                          ");
				output.append (lineType);
				output.append (' ');
			}
		}
		
		return (output.toString ());
	}

	private static final String	ERR_CHAR_TERM
		= "Unterminated character constant";

	private static final String	ERR_STRING_TERM
		= "Unterminated string constant";

	private static final String	ERR_ILLEGAL_ADDR
		= "Illegal addressing mode";

	private static final String ERR_TEXT_TOO_LONG_FOR_IMMD
		= "Text literal is too long to be used in an immediate expression";

	private static final String ERR_MISSING_EXPRESSION
		= "Missing expression";

	private static final String ERR_EXPECTED_X
		= "Expected X index";

	private static final String ERR_MISSING_A_OR_B
		= "Missing A or B accumulator";
	
	/**
	 * Represents an invalid addressing mode.
	 */
	private static final int	UNKN	= 0;

	/**
	 * Represents the immediate addressing mode.
	 */
	private static final int	IMMD	= 1;

	/**
	 * Represents the direct page addressing mode.
	 */
	private static final int	DRCT	= 2;

	/**
	 * Represents the extended page addressing mode.
	 */
	private static final int	EXTD	= 3;

	/**
	 * Represents the indexed addressing mode.
	 */
	private static final int	INDX	= 4;

	/**
	 * A constant value used in calculations.
	 */
	private static final Value	ZERO		= new Value (null, 0);
	
	/**
	 * A constant value used in relative address calculations.
	 */
	private static final Value	TWO			= new Value (null, 2);
	
	/**
	 * A constant value used in bit shift calculations.
	 */
	private static final Value	EIGHT		= new Value (null, 8);
	
	/**
	 * A <CODE>Hashtable</CODE> of keyword tokens to speed up classification.
	 */
	private Hashtable<String, Token> tokens	= new Hashtable<String, Token> ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to format output.
	 */
	private StringBuffer		output 	= new StringBuffer ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to build up new tokens.
	 */
	private StringBuffer		buffer	= new StringBuffer ();
	
	/**
	 * The argument.
	 */
	private Expr			arg;
	
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
		case '[':	return (LBRACKET);
		case ']':	return (RBRACKET);

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
	
	private int parseMode ()
	{
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
			else
				arg = parseImmd ();
			
			return (IMMD);
		}
		
		// Handle extended indirect [..]
		if (token == LBRACKET) {
			
			// TODO
		}
		
		// Handle <..
		if (token == LT) {
			token = nextRealToken ();
			arg = parseExpr ();
			
			if (arg == null)
				error (ERR_MISSING_EXPRESSION);
			
			return (DRCT);
		}
		
		// Handle >..
		if (token == GT) {
			token = nextRealToken ();
			arg = parseExpr ();
			
			if (arg == null)
				error (ERR_MISSING_EXPRESSION);
			
			return (EXTD);
		}
		
		if (token == A) {
			
		}
		
		// Extract address or default to zero
		if (token != COMMA)
			arg = parseExpr ();
		else
			arg = ZERO;
		
		// Handle ,PC ,X|Y|U|S ,-X|Y|U|S ,--X|Y|U|S ,X|Y|U|S+ ,X|Y|U|S++
		if (token == COMMA) {
			Token		register;
			
			token = nextRealToken ();
			if (token == PC) {
				token = nextRealToken ();
				//
				return (0);
			}
			
			if (token == MINUS) {
				token = nextRealToken ();
				// auto dec
				if (token == MINUS) {
					token = nextRealToken ();
					// auto dec2
				}
			}
			
			if ((token == X)|(token == Y)|(token == U)|(token == S)) {
				register = token;
				token = nextRealToken ();
			}
			else
				error ("Expected X, Y, U or S");
					l
			if (token == PLUS) {
				token = nextRealToken ();
				// auto inc
				if (token == PLUS) {
					token = nextRealToken ();
					// auto inc2
				}
			}
		}
	
		// Handle ..
		if (arg.isAbsolute()) {
			int addr = (int) arg.resolve (null, null);
			
			return (((addr & 0xff00) == 0) ? DRCT : EXTD);
		}
		return (EXTD);
	}
	
	/**
	 * Parses the data value for an immediate addressing mode to allow short
	 * string literals as well as numbers.
	 * 
	 * @return	An expression containing the immediate value.
	 */
	private Expr parseImmd ()
	{
		if (token != null) {
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
		return (null);
	}

	private void genInherent (int opcode)
	{
		addByte (opcode);
	}
	
	private void genDirect (int opcode, final Expr expr)
	{
		addByte (opcode);
		addByte (expr);
	}
	private void genExtended (int opcode, final Expr expr)
	{
		addByte (opcode);
		addWord (expr);
	}

	private void genRelative (int opcode, final Expr expr)
	{
		Expr			origin = getOrigin ();
		
		if (origin != null) {
			addByte (opcode);
			
			Expr dist = Expr.sub (expr, Expr.add (origin, TWO));
			if (getPass () == Pass.FINAL) {
				if (dist.isAbsolute () && ((dist.resolve () < -128) || (dist.resolve () > 127)))
					error ("Relative branch is out of range");
			}
			addByte (dist);
		}
		else
			error ("No active section");
	}
}