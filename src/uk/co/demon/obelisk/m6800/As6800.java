/*
 * Copyright (C),2013-2014 Andrew John Jacobs.
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

package uk.co.demon.obelisk.m6800;

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

/**
 * The <CODE>As6800</CODE> provides the base <CODE>Assembler</CODE> with an
 * understanding of Motorola 6800 family assembler conventions.
 *
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class As6800 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As6800 ().run (args);
	}

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
	 * A <CODE>Token</CODE> representing the B register.
	 */
	protected final Token 	B
		= new Token (KEYWORD, "B");

	/**
	 * A <CODE>Token</CODE> representing the X register.
	 */
	protected final Token 	X
		= new Token (KEYWORD, "X");
	
	protected final Opcode ABA = new Opcode (KEYWORD, "ABA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x1b);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
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
			return (true);
		}
	};
	
	protected final Opcode ADD = new Opcode (KEYWORD, "ADD")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x8b : 0xcb, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x9b : 0xdb, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xbb : 0xfb, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xab : 0xeb, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode AND = new Opcode (KEYWORD, "AND")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x84 : 0xc4, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x94 : 0xd4, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb4 : 0xf4, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa4 : 0xe4, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode ASL = new Opcode (KEYWORD, "ASL")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x48 : 0x58);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x78, arg); break;
			case INDX:	genDirect (0x68, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode ASR = new Opcode (KEYWORD, "ASR")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x47 : 0x57);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x77, arg); break;
			case INDX:	genDirect (0x67, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
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
	
	protected final Opcode BIT = new Opcode (KEYWORD, "BIT")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x85 : 0xc5, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x95 : 0xd5, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb5 : 0xf5, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa5 : 0xe5, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
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
	
	protected final Opcode CBA = new Opcode (KEYWORD, "CBA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x11);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode CLC = new Opcode (KEYWORD, "CLC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0c);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode CLI = new Opcode (KEYWORD, "CLI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0e);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode CLR = new Opcode (KEYWORD, "CLR")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x4f : 0x5f);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x7f, arg); break;
			case INDX:	genDirect (0x6f, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode CLV = new Opcode (KEYWORD, "CLV")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0a);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode CMP = new Opcode (KEYWORD, "CMP")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x81 : 0xc1, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x91 : 0xd1, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb1 : 0xf1, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa1 : 0xe1, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode COM = new Opcode (KEYWORD, "COM")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x43 : 0x53);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x73, arg); break;
			case INDX:	genDirect (0x63, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode CPX = new Opcode (KEYWORD, "CPX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case IMMD:	genExtended (0x8c, arg); break;
			case DRCT:	genDirect (0x9c, arg); break;
			case EXTD:  genExtended (0xbc, arg); break;
			case INDX:	genDirect (0xac, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode DAA = new Opcode (KEYWORD, "DAA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x19);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode DEC = new Opcode (KEYWORD, "DEC")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x4a : 0x5a);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x7a, arg); break;
			case INDX:	genDirect (0x6a, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode DES = new Opcode (KEYWORD, "DES")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x34);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode DEX = new Opcode (KEYWORD, "DEX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x09);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode EOR = new Opcode (KEYWORD, "EOR")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x88 : 0xc8, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x98 : 0xd8, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb8 : 0xf8, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa8 : 0xe8, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode INC = new Opcode (KEYWORD, "INC")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x4c : 0x5c);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x7c, arg); break;
			case INDX:	genDirect (0x6c, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode INS = new Opcode (KEYWORD, "INS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x31);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode INX = new Opcode (KEYWORD, "INX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x08);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode JMP = new Opcode (KEYWORD, "JMP")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genExtended (0x7e, arg); break;
			case INDX:	genDirect (0x6e, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode JSR = new Opcode (KEYWORD, "JSR")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:  genExtended (0xbd, arg); break;
			case INDX:	genDirect (0xad, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LDA = new Opcode (KEYWORD, "LDA")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x86 : 0xc6, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x96 : 0xd6, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb6 : 0xf6, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa6 : 0xe6, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode LDS = new Opcode (KEYWORD, "LDS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case IMMD:	genExtended (0x8e, arg); break;
			case DRCT:	genDirect (0x9e, arg); break;
			case EXTD:  genExtended (0xbe, arg); break;
			case INDX:	genDirect (0xae, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LDX = new Opcode (KEYWORD, "LDX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			switch (parseMode ()) {
			case IMMD:	genExtended (0xce, arg); break;
			case DRCT:	genDirect (0xde, arg); break;
			case EXTD:  genExtended (0xfe, arg); break;
			case INDX:	genDirect (0xee, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode LSR = new Opcode (KEYWORD, "LSR")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x44 : 0x54);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x74, arg); break;
			case INDX:	genDirect (0x64, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode NEG = new Opcode (KEYWORD, "NEG")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x40 : 0x50);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x70, arg); break;
			case INDX:	genDirect (0x60, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode NOP = new Opcode (KEYWORD, "NOP")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x01);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode ORA = new Opcode (KEYWORD, "ORA")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x8a : 0xca, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x9a : 0xda, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xba : 0xfa, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xaa : 0xea, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode PSH = new Opcode (KEYWORD, "PSH")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x36 : 0x37);
			}
			else {
				token = acc;
				error ("Expected A or B");
			}
			return (true);
		}
	};
	
	protected final Opcode PUL = new Opcode (KEYWORD, "PUL")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x32 : 0x33);
			}
			else {
				token = acc;
				error ("Expected A or B");
			}
			return (true);
		}
	};
	
	protected final Opcode ROL = new Opcode (KEYWORD, "ROL")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x49 : 0x59);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x79, arg); break;
			case INDX:	genDirect (0x69, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode ROR = new Opcode (KEYWORD, "ROR")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x46 : 0x56);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x76, arg); break;
			case INDX:	genDirect (0x66, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode RTI = new Opcode (KEYWORD, "RTI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x3b);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode RTS = new Opcode (KEYWORD, "RTS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x39);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode SBA = new Opcode (KEYWORD, "SBA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x10);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode SBC = new Opcode (KEYWORD, "SBC")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x82 : 0xc2, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x92 : 0xd2, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb2 : 0xf2, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa2 : 0xe2, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode SEC = new Opcode (KEYWORD, "SEC")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0d);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode SEI = new Opcode (KEYWORD, "SEI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0f);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode SEV = new Opcode (KEYWORD, "SEV")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x0b);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode STA = new Opcode (KEYWORD, "STA")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case DRCT:	genDirect ((acc == A) ? 0x97 : 0xd7, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb7 : 0xf7, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa7 : 0xe7, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode STS = new Opcode (KEYWORD, "STS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();

			switch (parseMode ()) {
			case DRCT:	genDirect (0x9f, arg); break;
			case EXTD:	genExtended (0xbf, arg); break;
			case INDX:	genDirect (0xaf, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode STX = new Opcode (KEYWORD, "STX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();

			switch (parseMode ()) {
			case DRCT:	genDirect (0xd7, arg); break;
			case EXTD:	genExtended (0xff, arg); break;
			case INDX:	genDirect (0xef, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode SUB = new Opcode (KEYWORD, "SUB")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				switch (parseMode ()) {
				case IMMD:	genDirect ((acc == A) ? 0x80 : 0xc0, arg); break;
				case DRCT:	genDirect ((acc == A) ? 0x90 : 0xd0, arg); break;
				case EXTD:	genExtended ((acc == A) ? 0xb0 : 0xf0, arg); break;
				case INDX:	genDirect ((acc == A) ? 0xa0 : 0xe0, arg); break;
				default:
					error (ERR_ILLEGAL_ADDR);
				}
			}
			return (true);
		}
	};
	
	protected final Opcode SWI = new Opcode (KEYWORD, "SWI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x3f);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TAB = new Opcode (KEYWORD, "TAB")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x16);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TAP = new Opcode (KEYWORD, "TAP")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x06);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TBA = new Opcode (KEYWORD, "TBA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x17);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TPA = new Opcode (KEYWORD, "TPA")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x07);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TST = new Opcode (KEYWORD, "TST")
	{
		@Override
		public boolean compile ()
		{
			Token acc = nextRealToken ();
			if ((acc == A) || (acc == B)) {
				token = nextRealToken ();
				
				genInherent ((acc == A) ? 0x4d : 0x5d);
				return (true);				
			}
			token = acc;
			
			switch (parseMode ()) {
			case DRCT:
			case EXTD:	genExtended (0x7d, arg); break;
			case INDX:	genDirect (0x6d, arg); break;
			default:
				error (ERR_ILLEGAL_ADDR);
			}
			return (true);
		}
	};
	
	protected final Opcode TSX = new Opcode (KEYWORD, "TSX")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x30);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode TXS = new Opcode (KEYWORD, "TXS")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x35);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	protected final Opcode WAI = new Opcode (KEYWORD, "WAI")
	{
		@Override
		public boolean compile ()
		{
			token = nextRealToken ();
			
			if (token == EOL)
				genInherent (0x3e);
			else
				error (ERR_ILLEGAL_ADDR);

			return (true);
		}
	};
	
	/**
	 * Constructs an <CODE>As8080</CODE> instance and initialises the object
	 * module.
	 */
	protected As6800 ()
	{
		super (new Module ("6800", true));
		
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
		addToken (ABA);
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
		addToken (BIT);
		addToken (BLE);
		addToken (BLS);
		addToken (BLT);
		addToken (BMI);
		addToken (BNE);
		addToken (BPL);
		addToken (BRA);
		addToken (BSR);
		addToken (BVC);
		addToken (BVS);
		addToken (CBA);
		addToken (CLC);
		addToken (CLI);
		addToken (CLR);
		addToken (CLV);
		addToken (CMP);
		addToken (COM);
		addToken (CPX);
		addToken (DAA);
		addToken (DEC);
		addToken (DES);
		addToken (DEX);
		addToken (EOR);
		addToken (INC);
		addToken (INS);
		addToken (INX);
		addToken (JMP);
		addToken (JSR);
		addToken (LDA);
		addToken (LDS);
		addToken (LDX);
		addToken (LSR);
		addToken (NEG);
		addToken (NOP);
		addToken (ORA);
		addToken (PSH);
		addToken (PUL);
		addToken (ROL);
		addToken (ROR);
		addToken (RTI);
		addToken (RTS);
		addToken (SBA);
		addToken (SBC);
		addToken (SEC);
		addToken (SEI);
		addToken (SEV);
		addToken (STA);
		addToken (STS);
		addToken (STX);
		addToken (SUB);
		addToken (SWI);
		addToken (TAB);
		addToken (TAP);
		addToken (TBA);
		addToken (TPA);
		addToken (TST);
		addToken (TSX);
		addToken (TXS);
		addToken (WAI);

		// Registers
		addToken (A);
		addToken (B);
		addToken (X);

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
		
		title = "Portable Motorola 6800 Assembler [14.10]";
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

	private static final String	ERR_SYNTAX
		= "Syntax error";

	private static final String	ERR_CHAR_TERM
		= "Unterminated character constant";

	private static final String	ERR_STRING_TERM
		= "Unterminated string constant";

	private static final String	ERR_ILLEGAL_ADDR
	= "Illegal addressing mode";

	private static final String ERR_UNEXPECTED_TEXT
		= "Unexpected text after instruction";

	private static final String ERR_TEXT_TOO_LONG_FOR_IMMD
	= "Text literal is too long to be used in an immediate expression";

	private static final String ERR_MISSING_EXPRESSION
	= "Missing expression";

	private static final String ERR_EXPECTED_X
	= "Expected X index";

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
	private Expr				arg;
	
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
		
		// Handle X
		if (token == X) {
			token = nextRealToken ();

			arg = ZERO;
			return (INDX);
		}
		
		// Handle ,X
		if (token == COMMA) {
			token = nextRealToken ();

			arg = ZERO;
			if (token == X)
				token = nextRealToken ();
			else
				error (ERR_EXPECTED_X);
			
			return (INDX);
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
		
		// Handle .. ..,X
		arg = parseExpr ();

		if (arg == null)
			error (ERR_MISSING_EXPRESSION);
		
		if (token == COMMA) {
			token = nextRealToken ();
			if (token == X) {
				token = nextRealToken ();				
				return (INDX);
			}

			error (ERR_EXPECTED_X);
			return (UNKN);
		}
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
			addByte (Expr.sub (expr, Expr.add (origin, TWO)));
		}
		else
			error ("No active section");
	}
	
}