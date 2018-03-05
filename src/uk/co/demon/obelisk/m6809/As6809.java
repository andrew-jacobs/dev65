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
	 * A <CODE>Token</CODE> representing the CC register.
	 */
	protected final Token 	CC
		= new Token (KEYWORD, "CC");

	/**
	 * A <CODE>Token</CODE> representing the D register.
	 */
	protected final Token 	D
		= new Token (KEYWORD, "D");

	/**
	 * A <CODE>Token</CODE> representing the DP register.
	 */
	protected final Token 	DP
		= new Token (KEYWORD, "DP");

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

	/**
	 * A <CODE>Token</CODE> representing the PC register.
	 */
	protected final Token 	PCR
		= new Token (KEYWORD, "PCR");
	
	protected final Opcode ABX	= new Opcode (KEYWORD, "ABX")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x3a); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode ADCA = new Opcode (KEYWORD, "ADCA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x89, arg); break;
					case DRCT:	genDirect 	(0x00, 0x99, arg); break;
					case INDX:	genIndexed	(0x00, 0xa9, arg); break;
					case EXTD:	genExtended (0x00, 0xb9, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode ADCB = new Opcode (KEYWORD, "ADCB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc9, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd9, arg); break;
					case INDX:	genIndexed	(0x00, 0xe9, arg); break;
					case EXTD:	genExtended (0x00, 0xf9, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ADDA = new Opcode (KEYWORD, "ADDA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x8b, arg); break;
					case DRCT:	genDirect 	(0x00, 0x9b, arg); break;
					case INDX:	genIndexed 	(0x00, 0xab, arg); break;
					case EXTD:	genExtended (0x00, 0xbb, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ADDB = new Opcode (KEYWORD, "ADDB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xcb, arg); break;
					case DRCT:	genDirect 	(0x00, 0xdb, arg); break;
					case INDX:	genIndexed 	(0x00, 0xeb, arg); break;
					case EXTD:	genExtended (0x00, 0xfb, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ADDD = new Opcode (KEYWORD, "ADDD")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x00, 0xc3, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd3, arg); break;
					case INDX:	genIndexed  (0x00, 0xe3, arg); break;
					case EXTD:	genExtended (0x00, 0xf3, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ANDA = new Opcode (KEYWORD, "ANDA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x84, arg); break;
					case DRCT:	genDirect 	(0x00, 0x94, arg); break;
					case INDX:	genIndexed 	(0x00, 0xa4, arg); break;
					case EXTD:	genExtended (0x00, 0xb4, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ANDB = new Opcode (KEYWORD, "ANDB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc4, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd4, arg); break;
					case INDX:	genIndexed	(0x00, 0xe4, arg); break;
					case EXTD:	genExtended (0x00, 0xf4, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ANDCC = new Opcode (KEYWORD, "ANDCC")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x1c, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ASL 	= new Opcode (KEYWORD, "ASL")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x08, arg); break;
					case INDX:	genIndexed	(0x00, 0x68, arg); break;
					case EXTD:	genExtended (0x00, 0x78, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ASLA	= new Opcode (KEYWORD, "ASLA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x48); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode ASLB	= new Opcode (KEYWORD, "ASLB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent(0x00, 0x58); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode ASR 	= new Opcode (KEYWORD, "ASR")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x07, arg); break;
					case INDX:	genIndexed 	(0x00, 0x67, arg); break;
					case EXTD:	genExtended (0x00, 0x77, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ASRA	= new Opcode (KEYWORD, "ASRA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x47); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode ASRB	= new Opcode (KEYWORD, "ASRB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x57); break;
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
					case EXTD:  genRelative (0x00, 0x24, arg); break;
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
					case EXTD:  genRelative (0x00, 0x25, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					return (true);
				}
			};
	
	protected final Opcode BHS = new Opcode (KEYWORD, "BHS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:
					case EXTD:  genRelative (0x00, 0x24, arg); break;
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
					case EXTD:  genRelative (0x00, 0x27, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2c, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2e, arg); break;
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
					case EXTD:  genRelative (0x00, 0x22, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					return (true);
				}
			};
	
	protected final Opcode BITA = new Opcode (KEYWORD, "BITA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x85, arg); break;
					case DRCT:	genDirect 	(0x00, 0x95, arg); break;
					case INDX:	genIndexed	(0x00, 0xa5, arg); break;
					case EXTD:	genExtended (0x00, 0xb5, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode BITB = new Opcode (KEYWORD, "BITB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc5, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd5, arg); break;
					case INDX:	genIndexed	(0x00, 0xe5, arg); break;
					case EXTD:	genExtended (0x00, 0xf5, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2f, arg); break;
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
					case EXTD:  genRelative (0x00, 0x23, arg); break;
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
					case EXTD:  genRelative (0x00, 0x25, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2d, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2b, arg); break;
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
					case EXTD:  genRelative (0x00, 0x26, arg); break;
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
					case EXTD:  genRelative (0x00, 0x2a, arg); break;
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
					case EXTD:  genRelative (0x00, 0x20, arg); break;
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
					case EXTD:  genRelative (0x00, 0x21, arg); break;
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
					case EXTD:  genRelative (0x00, 0x8d, arg); break;
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
					case EXTD:  genRelative (0x00, 0x28, arg); break;
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
					case EXTD:  genRelative (0x00, 0x29, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					return (true);
				}
			};
			
	protected final Opcode CLR 	= new Opcode (KEYWORD, "CLR")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x0f, arg); break;
					case INDX:	genIndexed	(0x00, 0x6f, arg); break;
					case EXTD:	genExtended (0x00, 0x7f, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode CLRA	= new Opcode (KEYWORD, "CLRA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x4f); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode CLRB	= new Opcode (KEYWORD, "CLRB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x5f); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
		
	protected final Opcode CMPA = new Opcode (KEYWORD, "CMPA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x81, arg); break;
					case DRCT:	genDirect 	(0x00, 0x91, arg); break;
					case INDX:	genIndexed 	(0x00, 0xa1, arg); break;
					case EXTD:	genExtended (0x00, 0xb1, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode CMPB = new Opcode (KEYWORD, "CMPB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc1, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd1, arg); break;
					case INDX:	genIndexed	(0x00, 0xe1, arg); break;
					case EXTD:	genExtended (0x00, 0xf1, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode CMPD = new Opcode (KEYWORD, "CMPD")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x10, 0x83, arg); break;
					case DRCT:	genDirect 	(0x10, 0x93, arg); break;
					case INDX:	genIndexed  (0x10, 0xa3, arg); break;
					case EXTD:	genExtended (0x10, 0xb3, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode CMPS = new Opcode (KEYWORD, "CMPS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x11, 0x8c, arg); break;
					case DRCT:	genDirect 	(0x11, 0x9c, arg); break;
					case INDX:	genIndexed  (0x11, 0xac, arg); break;
					case EXTD:	genExtended (0x11, 0xbc, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode CMPU = new Opcode (KEYWORD, "CMPU")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x11, 0x83, arg); break;
					case DRCT:	genDirect 	(0x11, 0x93, arg); break;
					case INDX:	genIndexed  (0x11, 0xa3, arg); break;
					case EXTD:	genExtended (0x11, 0xb3, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode CMPX = new Opcode (KEYWORD, "CMPX")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x00, 0x8c, arg); break;
					case DRCT:	genDirect 	(0x00, 0x9c, arg); break;
					case INDX:	genIndexed  (0x00, 0xac, arg); break;
					case EXTD:	genExtended (0x00, 0xbc, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode CMPY = new Opcode (KEYWORD, "CMPY")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x10, 0x8c, arg); break;
					case DRCT:	genDirect 	(0x10, 0x9c, arg); break;
					case INDX:	genIndexed  (0x10, 0xac, arg); break;
					case EXTD:	genExtended (0x10, 0xbc, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
			
	protected final Opcode COM 	= new Opcode (KEYWORD, "COM")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x03, arg); break;
					case INDX:	genIndexed	(0x00, 0x63, arg); break;
					case EXTD:	genExtended (0x00, 0x73, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode COMA	= new Opcode (KEYWORD, "COMA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x43); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode COMB	= new Opcode (KEYWORD, "COMB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x53); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode CWAI = new Opcode (KEYWORD, "CWAI")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x3c, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode DAA	= new Opcode (KEYWORD, "DAA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x19); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode DEC 	= new Opcode (KEYWORD, "DEC")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x0a, arg); break;
					case INDX:	genIndexed	(0x00, 0x6a, arg); break;
					case EXTD:	genExtended (0x00, 0x7a, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode DECA	= new Opcode (KEYWORD, "DECA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x4a); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode DECB	= new Opcode (KEYWORD, "DECB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x5a); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode EORA = new Opcode (KEYWORD, "EORA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x88, arg); break;
					case DRCT:	genDirect 	(0x00, 0x98, arg); break;
					case INDX:	genIndexed 	(0x00, 0xa8, arg); break;
					case EXTD:	genExtended (0x00, 0xb8, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode EORB = new Opcode (KEYWORD, "EORB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc8, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd8, arg); break;
					case INDX:	genIndexed 	(0x00, 0xe8, arg); break;
					case EXTD:	genExtended (0x00, 0xf8, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode INC 	= new Opcode (KEYWORD, "INC")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x0c, arg); break;
					case INDX:	genIndexed 	(0x00, 0x6c, arg); break;
					case EXTD:	genExtended (0x00, 0x7c, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode INCA	= new Opcode (KEYWORD, "INCA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x4c); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode INCB	= new Opcode (KEYWORD, "INCB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x5c); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode JMP 	= new Opcode (KEYWORD, "JMP")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x0e, arg); break;
					case INDX:	genIndexed 	(0x00, 0x6e, arg); break;
					case EXTD:	genExtended (0x00, 0x7e, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode JSR 	= new Opcode (KEYWORD, "JSR")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x9d, arg); break;
					case INDX:	genIndexed 	(0x00, 0xad, arg); break;
					case EXTD:	genExtended (0x00, 0xbd, arg); break;
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
					case EXTD:  genLongRelative (0x10,0x24, arg); break;
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
					case EXTD:  genLongRelative (0x10,0x25, arg); break;
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
					case EXTD:  genLongRelative (0x10,0x27, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2c, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2e, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x22, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					return (true);
				}
			};
	
	protected final Opcode LBHS = new Opcode (KEYWORD, "LBHS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:
					case EXTD:  genLongRelative (0x10, 0x24, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2f, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					return (true);
				}
			};
	
	protected final Opcode LBLO = new Opcode (KEYWORD, "LBLO")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:
					case EXTD:  genLongRelative (0x10, 0x25, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x23, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2d, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2b, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x26, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x2a, arg); break;
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
					case EXTD:  genLongRelative (0x00, 0x16, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x21, arg); break;
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
					case EXTD:  genLongRelative (0x00, 0x17, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x28, arg); break;
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
					case EXTD:  genLongRelative (0x10, 0x29, arg); break;
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
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x86, arg); break;
					case DRCT:	genDirect 	(0x00, 0x96, arg); break;
					case INDX:	genIndexed 	(0x00, 0xa6, arg); break;
					case EXTD:	genExtended (0x00, 0xb6, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode LDB = new Opcode (KEYWORD, "LDB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc6, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd6, arg); break;
					case INDX:	genIndexed 	(0x00, 0xe6, arg); break;
					case EXTD:	genExtended (0x00, 0xf6, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode LDD = new Opcode (KEYWORD, "LDD")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x00, 0xcc, arg); break;
					case DRCT:	genDirect 	(0x00, 0xdc, arg); break;
					case INDX:	genIndexed 	(0x00, 0xec, arg); break;
					case EXTD:	genExtended (0x00, 0xfc, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
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
					case IMMD:	genExtended	(0x10, 0xce, arg); break;
					case DRCT:	genDirect 	(0x10, 0xde, arg); break;
					case INDX:	genIndexed 	(0x10, 0xee, arg); break;
					case EXTD:	genExtended (0x10, 0xfe, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LDU = new Opcode (KEYWORD, "LDU")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x00, 0xce, arg); break;
					case DRCT:	genDirect 	(0x00, 0xde, arg); break;
					case INDX:	genIndexed 	(0x00, 0xee, arg); break;
					case EXTD:	genExtended (0x00, 0xfe, arg); break;
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
					case IMMD:	genExtended (0x00, 0x8e, arg); break;
					case DRCT:	genDirect 	(0x00, 0x9e, arg); break;
					case INDX:	genIndexed 	(0x00, 0xae, arg); break;
					case EXTD:	genExtended (0x00, 0xbe, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LDY = new Opcode (KEYWORD, "LDY")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genExtended (0x10, 0x8e, arg); break;
					case DRCT:	genDirect 	(0x10, 0x9e, arg); break;
					case INDX:	genIndexed 	(0x10, 0xae, arg); break;
					case EXTD:	genExtended (0x10, 0xbe, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LEAS = new Opcode (KEYWORD, "LEAS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INDX:	genIndexed 	(0x00, 0x32, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LEAU = new Opcode (KEYWORD, "LEAU")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INDX:	genIndexed 	(0x00, 0x33, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LEAX = new Opcode (KEYWORD, "LEAX")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INDX:	genIndexed 	(0x00, 0x30, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LEAY = new Opcode (KEYWORD, "LEAY")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INDX:	genIndexed 	(0x00, 0x31, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode LSL 	= new Opcode (KEYWORD, "LSL")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x08, arg); break;
					case INDX:	genIndexed	(0x00, 0x68, arg); break;
					case EXTD:	genExtended (0x00, 0x78, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode LSLA	= new Opcode (KEYWORD, "LSLA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x48); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode LSLB	= new Opcode (KEYWORD, "LSLB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x58); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode LSR 	= new Opcode (KEYWORD, "LSR")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x04, arg); break;
					case INDX:	genIndexed	(0x00, 0x64, arg); break;
					case EXTD:	genExtended (0x00, 0x74, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode LSRA	= new Opcode (KEYWORD, "LSRA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x44); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode LSRB	= new Opcode (KEYWORD, "LSRB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x54); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode MUL	= new Opcode (KEYWORD, "MUL")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x3d); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode NEG 	= new Opcode (KEYWORD, "NEG")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x00, arg); break;
					case INDX:	genIndexed	(0x00, 0x60, arg); break;
					case EXTD:	genExtended (0x00, 0x70, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode NEGA	= new Opcode (KEYWORD, "NEGA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x40); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode NEGB	= new Opcode (KEYWORD, "NEGB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x50); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode NOP	= new Opcode (KEYWORD, "NOP")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x12); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode ORA = new Opcode (KEYWORD, "ORA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x8a, arg); break;
					case DRCT:	genDirect 	(0x00, 0x9a, arg); break;
					case INDX:	genIndexed 	(0x00, 0xaa, arg); break;
					case EXTD:	genExtended (0x00, 0xba, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode ORB = new Opcode (KEYWORD, "ORB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xca, arg); break;
					case DRCT:	genDirect 	(0x00, 0xda, arg); break;
					case INDX:	genIndexed	(0x00, 0xea, arg); break;
					case EXTD:	genExtended (0x00, 0xfa, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};

	protected final Opcode ORCC = new Opcode (KEYWORD, "ORCC")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x1a, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
// PSH
// PUL

	protected final Opcode ROL 	= new Opcode (KEYWORD, "ROL")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x09, arg); break;
					case INDX:	genIndexed	(0x00, 0x69, arg); break;
					case EXTD:	genExtended (0x00, 0x79, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
			
					return (true);
				}
			};
	
	protected final Opcode ROLA	= new Opcode (KEYWORD, "ROLA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x49); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode ROLB	= new Opcode (KEYWORD, "ROLB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x59); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
				
	protected final Opcode ROR 	= new Opcode (KEYWORD, "ROR")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x06, arg); break;
					case INDX:	genIndexed	(0x00, 0x66, arg); break;
					case EXTD:	genExtended (0x00, 0x76, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
			
					return (true);
				}
			};

	protected final Opcode RORA	= new Opcode (KEYWORD, "RORA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x46); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode RORB	= new Opcode (KEYWORD, "RORB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x56); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode RTI	= new Opcode (KEYWORD, "RTI")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x3b); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode RTS	= new Opcode (KEYWORD, "RTS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x39); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode SBCA = new Opcode (KEYWORD, "SBCA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x82, arg); break;
					case DRCT:	genDirect 	(0x00, 0x92, arg); break;
					case INDX:	genIndexed	(0x00, 0xa2, arg); break;
					case EXTD:	genExtended (0x00, 0xb2, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode SBCB = new Opcode (KEYWORD, "SBCB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc2, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd2, arg); break;
					case INDX:	genIndexed	(0x00, 0xe2, arg); break;
					case EXTD:	genExtended (0x00, 0xf2, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
		
	protected final Opcode SEX	= new Opcode (KEYWORD, "SEX")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x1d); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
			
	protected final Opcode STA 	= new Opcode (KEYWORD, "STA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x97, arg); break;
					case INDX:	genIndexed	(0x00, 0xa7, arg); break;
					case EXTD:	genExtended (0x00, 0xb7, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STB 	= new Opcode (KEYWORD, "STB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0xd7, arg); break;
					case INDX:	genIndexed	(0x00, 0xe7, arg); break;
					case EXTD:	genExtended (0x00, 0xf7, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STD 	= new Opcode (KEYWORD, "STD")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0xdd, arg); break;
					case INDX:	genIndexed	(0x00, 0xed, arg); break;
					case EXTD:	genExtended (0x00, 0xfd, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STS 	= new Opcode (KEYWORD, "STS")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x10, 0xdf, arg); break;
					case INDX:	genIndexed	(0x10, 0xef, arg); break;
					case EXTD:	genExtended (0x10, 0xff, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STU 	= new Opcode (KEYWORD, "STU")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0xdf, arg); break;
					case INDX:	genIndexed	(0x00, 0xef, arg); break;
					case EXTD:	genExtended (0x00, 0xff, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STX 	= new Opcode (KEYWORD, "STX")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x9f, arg); break;
					case INDX:	genIndexed	(0x00, 0xaf, arg); break;
					case EXTD:	genExtended (0x00, 0xbf, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode STY 	= new Opcode (KEYWORD, "STY")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x10, 0x9f, arg); break;
					case INDX:	genIndexed	(0x10, 0xaf, arg); break;
					case EXTD:	genExtended (0x10, 0xbf, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode SUBA = new Opcode (KEYWORD, "SUBA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x80, arg); break;
					case DRCT:	genDirect 	(0x00, 0x90, arg); break;
					case INDX:	genIndexed	(0x00, 0xa0, arg); break;
					case EXTD:	genExtended (0x00, 0xb0, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};

	protected final Opcode SUBB = new Opcode (KEYWORD, "SUBB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0xc0, arg); break;
					case DRCT:	genDirect 	(0x00, 0xd0, arg); break;
					case INDX:	genIndexed	(0x00, 0xe0, arg); break;
					case EXTD:	genExtended (0x00, 0xf0, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode SUBD = new Opcode (KEYWORD, "SUBD")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case IMMD:	genDirect 	(0x00, 0x83, arg); break;
					case DRCT:	genDirect 	(0x00, 0x93, arg); break;
					case INDX:	genIndexed	(0x00, 0xa3, arg); break;
					case EXTD:	genExtended (0x00, 0xb3, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode SWI	= new Opcode (KEYWORD, "SWI")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x3f); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
			
	protected final Opcode SWI2	= new Opcode (KEYWORD, "SWI2")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x10, 0x3f); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
			
	protected final Opcode SWI3	= new Opcode (KEYWORD, "SWI3")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x11, 0x3f); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
			
	protected final Opcode SYNC	= new Opcode (KEYWORD, "SYNC")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x13); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
			
// TFR
			
	protected final Opcode TST 	= new Opcode (KEYWORD, "TST")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case DRCT:	genDirect 	(0x00, 0x0d, arg); break;
					case INDX:	genIndexed	(0x00, 0x6d, arg); break;
					case EXTD:	genExtended (0x00, 0x7d, arg); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
		
					return (true);
				}
			};
	
	protected final Opcode TSTA	= new Opcode (KEYWORD, "TSTA")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x4d); break;
					default:
						error (ERR_ILLEGAL_ADDR);
					}
					
					return (true);
				}
			};
	
	protected final Opcode TSTB	= new Opcode (KEYWORD, "TSTB")
			{
				@Override
				public boolean compile ()
				{
					token = nextRealToken ();
					
					switch (parseMode ()) {
					case INHR:	genInherent (0x00, 0x5d); break;
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
		addToken (ADCA);
		addToken (ADCB);
		addToken (ADDA);
		addToken (ADDB);
		addToken (ADDD);
		addToken (ANDA);
		addToken (ANDB);
		addToken (ANDCC);
		addToken (ASL);
		addToken (ASLA);
		addToken (ASLB);
		addToken (ASR);
		addToken (ASRA);
		addToken (ASRB);
		addToken (BCC);
		addToken (BCS);
		addToken (BEQ);
		addToken (BGE);
		addToken (BGT);
		addToken (BHI);
		addToken (BHS);
		addToken (BITA);
		addToken (BITB);
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
		addToken (CLRA);
		addToken (CLRB);
		addToken (CMPA);
		addToken (CMPB);
		addToken (CMPD);
		addToken (CMPS);
		addToken (CMPU);
		addToken (CMPX);
		addToken (CMPY);		
		addToken (COM);
		addToken (COMA);
		addToken (COMB);
		addToken (CWAI);
		addToken (DAA);
		addToken (DEC);
		addToken (DECA);
		addToken (DECB);
		addToken (EORA);
		addToken (EORB);
//		addToken (EXG);
		addToken (INC);
		addToken (INCA);
		addToken (INCB);
		addToken (JMP);
		addToken (JSR);
		addToken (LBCC);
		addToken (LBCS);
		addToken (LBEQ);
		addToken (LBGE);
		addToken (LBGT);
		addToken (LBHI);
		addToken (LBHS);
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
		addToken (LDA);
		addToken (LDB);
		addToken (LDD);
		addToken (LDS);
		addToken (LDU);
		addToken (LDX);
		addToken (LDY);
		addToken (LEAS);
		addToken (LEAU);
		addToken (LEAX);
		addToken (LEAY);
		addToken (LSL);
		addToken (LSLA);
		addToken (LSLB);
		addToken (LSR);
		addToken (LSRA);
		addToken (LSRB);
		addToken (MUL);
		addToken (NEG);
		addToken (NEGA);
		addToken (NEGB);
		addToken (NOP);
		addToken (ORA);
		addToken (ORB);
		addToken (ORCC);
//		addToken (PSH);
//		addToken (PUL);
		addToken (ROL);
		addToken (ROLA);
		addToken (ROLB);
		addToken (ROR);
		addToken (RORA);
		addToken (RORB);
		addToken (RTI);
		addToken (RTS);
		addToken (SBCA);
		addToken (SBCB);
		addToken (SEX);
		addToken (STA);
		addToken (STB);
		addToken (STD);
		addToken (STS);
		addToken (STU);
		addToken (STX);
		addToken (STY);
		addToken (SUBA);
		addToken (SUBB);
		addToken (SUBD);
		addToken (SWI);
		addToken (SWI2);
		addToken (SWI3);
		addToken (SYNC);
//		addToken (TFR);
		addToken (TST);
		addToken (TSTA);
		addToken (TSTB);
		
		// Registers
		addToken (A);
		addToken (B);
		addToken (CC);
		addToken (D);
		addToken (DP);
		addToken (X);
		addToken (Y);
		addToken (U);
		addToken (S);
		addToken (PC);
		addToken (PCR);

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
		
		title = "Portable Motorola 6809 Assembler [18.03]";
		
		directPage = 0x0000;
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
	 * Represent the inherent addressing mode
	 */
	private static final int	INHR	= 4;

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
	 * A constant value used in relative address calculations.
	 */
	private static final Value	THREE		= new Value (null, 3);
	
	/**
	 * A constant value used in relative address calculations.
	 */
	private static final Value	FOUR		= new Value (null, 4);
	
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
	
	/*
	 * The address mode post byte
	 */
	private int				postByte;
	
	/**
	 * The direct page address
	 */
	private int				directPage;
	
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
		boolean forceByte = false;
		boolean forceWord = false;
		
		if (token == EOL) return (INHR);
		
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
		
		if (token == A) {
			
		}
		
		// Handle <.. and >..
		if (token == LT) {
			forceByte = true;
			token = nextRealToken ();
		}
		else if (token == GT) {
			forceWord = true;
			token = nextRealToken ();
		}
				
		// Handle extended indirect [..]
		if (token == LBRACKET) {
			
			// TODO
			
			return (INDX);
		}
		
		// Extract address or default to zero
		if (token != COMMA)
			arg = parseExpr ();
		else
			arg = ZERO;
		
		// Handle ,PCR ,X|Y|U|S ,-X|Y|U|S ,--X|Y|U|S ,X|Y|U|S+ ,X|Y|U|S++
		if (token == COMMA) {
			Token		register;
			
			// ,PCR
			token = nextRealToken ();
			if (token == PCR) {
				Expr dist = Expr.sub (arg, getOrigin ());
				if (dist.isAbsolute() && (dist.resolve () >= -128) && (dist.resolve () <= 127))
					postByte = 0x9c; 
				else
					postByte = 0x9d;
				
				return (INDX);
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

			if (token == PLUS) {
				token = nextRealToken ();
				// auto inc
				if (token == PLUS) {
					token = nextRealToken ();
					// auto inc2
				}
			}
			return (INDX);
		}
	
		// Handle ..
		if (forceByte) return (DRCT);
		if (forceWord) return (EXTD);
		
		if (arg.isAbsolute()) {
			int addr = (int) arg.resolve (null, null);
			
			if ((addr & 0xff00) == directPage) return (DRCT);
		}
		return (EXTD);
	}
	
	private void parseIndex ()
	{
		
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

	private void genInherent (int prefix, int opcode)
	{
		if (prefix != 0)
			addByte (prefix);
		addByte (opcode);
	}
	
	private void genDirect (int prefix, int opcode, final Expr expr)
	{
		if (prefix != 0)
			addByte (prefix);
		addByte (opcode);
		addByte (expr);
	}
	
	private void genExtended (int prefix, int opcode, final Expr expr)
	{
		if (prefix != 0)
			addByte (prefix);
		addByte (opcode);
		addWord (expr);
	}
	
	private void genRelative (int prefix, int opcode, final Expr expr)
	{
		Expr			origin = getOrigin ();
		
		if (origin != null) {
			if (prefix != 0)
				addByte (prefix);
			addByte (opcode);
			
			Expr dist = Expr.sub (expr, Expr.add (origin, (prefix != 0) ? THREE : TWO));
			if (getPass () == Pass.FINAL) {
				if (dist.isAbsolute () && ((dist.resolve () < -128) || (dist.resolve () > 127)))
					error ("Relative branch is out of range");
			}
			addByte (dist);
		}
		else
			error ("No active section");
	}
	
	private void genLongRelative (int prefix, int opcode, final Expr expr)
	{
		Expr			origin = getOrigin ();
		
		if (origin != null) {
			if (prefix != 0)
				addByte (prefix);
			addByte (opcode);
			
			Expr dist = Expr.sub (expr, Expr.add (origin, (prefix != 0) ? FOUR : THREE));
			if (getPass () == Pass.FINAL) {
				if (dist.isAbsolute () && ((dist.resolve () < -32768) || (dist.resolve () > 32767)))
					error ("Relative branch is out of range");
			}
			addWord (dist);
		}
		else
			error ("No active section");
	}
	
	private void genIndexed (int prefix, int opcode, final Expr expr)
	{
		if (prefix != 0)
			addByte (prefix);
		addByte (opcode);
		addByte (postByte);
		
		if (expr != null) {
			
		}
	}
}