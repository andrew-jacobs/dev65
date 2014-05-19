/*
 * Copyright (C),2014 Andrew John Jacobs.
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

package uk.co.demon.obelisk.w65xx;

import uk.co.demon.obelisk.xemu.Emulator;
import uk.co.demon.obelisk.xobj.Hex;

public class Em65 extends Emulator
{


	
	/**
	 * The <CODE>CPU6502</CODE> implements opcodes and addressing modes common
	 * to all the 8-bit 65xx family.
	 */
	protected abstract class CPU6502
	{
		public abstract int step ();
		
		public abstract void trace ();
		
		
		protected abstract class AddressMode
		{
			public AddressMode (int size)
			{
				this.size = size;
			}
			
			public final int getSize ()
			{
				return (size);
			}
			
			public abstract void decode ();
			
			public abstract String forPC (int pc);
			
			protected final int size;
		}
		
		protected final AddressMode IMP = new AddressMode (1)
		{
			public void decode ()
			{
				extra = 0;
				EA = 0;
				setPC (PC + 1);
			}
		
			public String forPC (int pc)
			{
				return ("");
			}
		};
		
		protected final AddressMode STK = new AddressMode (1)
		{
			public void decode ()
			{
				extra = 0;
				EA = 0;
				setPC (PC + 1);
			}
		
			public String forPC (int pc)
			{
				return ("");
			}
		};
		
		protected final AddressMode ACC = new AddressMode (1)
		{
			public void decode ()
			{
				extra = 0;
				EA = 0;
				setPC (PC + 1);
			}
		
			public String forPC (int pc)
			{
				return ("A");
			}
		};
		
		protected final AddressMode IMM = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				EA = toWord (PC + 1);
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (toWord (pc + 1));
				
				return ("#$" + Hex.toHex (mem, 2));
			}
		};
		
		protected final AddressMode REL = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				int off = read (PC + 1);
				if ((off & 0x80) != 0) off |= 0xffffff00;
				setPC (PC + 2);
				EA = toWord (PC + off);
			}
			
			public String forPC (int pc)
			{
				int	mem = read (toWord (pc + 1));
				if ((mem & 0x80) != 0) mem |= 0xffffff00;
				
				pc = (pc + 2) + mem;
				
				return ("$" + Hex.toHex (pc, 4));
			}
		};
		
		protected final AddressMode ZPG = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1);
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("$" + Hex.toHex (mem, 2));
			}
		};
		
		protected final AddressMode ZPX = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1);
				EA = toByte (EA + X);
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("$" + Hex.toHex (mem, 2) + ",X");
			}
		};
		
		protected final AddressMode ZPY = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1);
				EA = toByte (EA + Y);
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("$" + Hex.toHex (mem, 2) + ",Y");
			}
		};
		
		protected final AddressMode ABS = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1) | (read (PC + 2) << 8);
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("$" + Hex.toHex (mem, 4));
			}
		};
		
		protected final AddressMode ABX = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				int MA = read (PC + 1) | (read (PC + 2) << 8);
				EA = toWord (MA + X);
				if (((EA ^ MA) & 0xff00) != 0) ++extra;
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1) | (read (pc + 2) << 8);
				
				return ("$" + Hex.toHex (mem, 4) + ",X");
			}
		};
		
		protected final AddressMode ABY = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				int MA = read (PC + 1) | (read (PC + 2) << 8);
				EA = toWord (MA + Y);
				if (((EA ^ MA) & 0xff00) != 0) ++extra;
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1) | (read (pc + 2) << 8);
				
				return ("$" + Hex.toHex (mem, 4) + ",Y");
			}
		};
		
		protected final AddressMode IND = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1) | (read (PC + 2) << 8);
				// Implement NMOS bug
				if ((EA & 0xff) == 0xff)
					EA = read (EA) | (read (EA & 0xff00) << 8);
				else
					EA = read (EA) | (read (EA + 1) << 8);
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("($" + Hex.toHex (mem, 4) + ")");
			}
		};
		
		protected final AddressMode IZX = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				int IA = toByte (read (PC + 1) + X);
				EA = read (IA) | (read (toByte(IA + 1)) << 8);
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("($" + Hex.toHex (mem, 2) + ",X)");
			}
		};
		
		protected final AddressMode IZY = new AddressMode (2)
		{
			public void decode ()
			{
				extra = 0;
				int IA = read (PC + 1);
				int MA = read (IA) | (read (toByte(IA + 1)) << 8);
				EA = toWord (MA + Y);
				if (((EA ^ MA) & 0xff00) != 0) ++extra;
				setPC (PC + 2);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("($" + Hex.toHex (mem, 2) + "),Y");
			}
		};
	
		protected abstract class Instruction
		{
			public Instruction (final String opcode)
			{
				this.opcode = opcode;
			}
			
			public final String getOpcode ()
			{
				return (opcode);
			}
			
			public abstract void execute ();
			
			private final String opcode;
		}
		
		protected final Instruction ADC = new Instruction ("ADC")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int	val = A + mem + (P & F_C);
				int ovr = (A ^ val) & (mem ^ val);
				int flg = P;
				
				if ((P & F_D) == F_D) {
					// Decimal mode
					if ((val & 0x00f) > 0x09) val += 0x06;
					if ((val & 0x0f0) > 0x90) val += 0x60;			
				}
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((ovr & 0x080) == 0x080) flg |= F_V; else flg &= ~F_V;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);
			}
		};
			
		protected final Instruction AND = new Instruction ("AND")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = A & mem;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);			
			}
		};
		
		protected final Instruction ASL = new Instruction ("ASL")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = mem << 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction ASLA = new Instruction ("ASL")
		{
			public void execute ()
			{
				int val = A << 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction BCC = new Instruction ("BCC")
		{
			public void execute ()
			{
				if ((P & F_C) == 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BCS = new Instruction ("BCS")
		{
			public void execute ()
			{
				if ((P & F_C) != 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BEQ = new Instruction ("BEQ")
		{
			public void execute ()
			{
				if ((P & F_Z) != 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BIT = new Instruction ("BIT")
		{
			public void execute ()
			{
				int mem	= read (EA);
				int	flg = P;
				
				if ((mem & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((mem & 0x040) == 0x040) flg |= F_V; else flg &= ~F_V;
				if ((mem & A) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setP (flg);				 
			}
		};
		
		protected final Instruction BMI = new Instruction ("BMI")
		{
			public void execute ()
			{
				if ((P & F_N) != 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}		
			}
		};
		
		protected final Instruction BNE = new Instruction ("BNE")
		{
			public void execute ()
			{
				if ((P & F_Z) == 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BPL = new Instruction ("BPL")
		{
			public void execute ()
			{
				if ((P & F_N) == 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BRK = new Instruction ("BRK")
		{
			public void execute ()
			{
			}
		};
			
		protected final Instruction BVC = new Instruction ("BVC")
		{
			public void execute ()
			{
				if ((P & F_V) == 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction BVS = new Instruction ("BVS")
		{
			public void execute ()
			{
				if ((P & F_V) != 0) {
					if (((PC ^ EA) & 0xff00) != 0) ++extra;
					setPC (EA);
					++extra;
				}
			}
		};
		
		protected final Instruction CLC = new Instruction ("CLC")
		{
			public void execute ()
			{
				setP (P & ~F_C);
			}
		};
		
		protected final Instruction CLD = new Instruction ("CLD")
		{
			public void execute ()
			{
				setP (P & ~F_D);
			}
		};
		
		protected final Instruction CLI = new Instruction ("CLI")
		{
			public void execute ()
			{
				setP (P & ~F_I);
			}
		};
		
		protected final Instruction CLV = new Instruction ("CLV")
		{
			public void execute ()
			{
				setP (P & ~F_V);
			}
		};
		
		protected final Instruction CMP = new Instruction ("CMP")
		{
			public void execute ()
			{
			}
		};
		
		protected final Instruction CPX = new Instruction ("CPX")
		{
			public void execute ()
			{
			}
		};
		
		protected final Instruction CPY = new Instruction ("CPY")
		{
			public void execute ()
			{}
		};
		
		protected final Instruction DEC = new Instruction ("DEC")
		{
			public void execute ()
			{
				int val = read (EA) - 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction DEX = new Instruction ("DEX")
		{
			public void execute ()
			{
				int val = X - 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (val);
				setP (flg);
			}
		};
		
		protected final Instruction DEY = new Instruction ("DEY")
		{
			public void execute ()
			{
				int val = Y - 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setY (val);
				setP (flg);
			}
		};
		
		protected final Instruction EOR = new Instruction ("EOR")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = A ^ mem;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);			
			}
		};
		
		protected final Instruction INC = new Instruction ("INC")
		{
			public void execute ()
			{
				int val = read (EA) + 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction INX = new Instruction ("INX")
		{
			public void execute ()
			{
				int val = X + 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (val);
				setP (flg);
			}
		};
		
		protected final Instruction INY = new Instruction ("INY")
		{
			public void execute ()
			{
				int val = Y + 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setY (val);
				setP (flg);
			}
		};
		
		protected final Instruction JMP = new Instruction ("JMP")
		{
			public void execute ()
			{
				setPC (EA);
			}
		};
		
		protected final Instruction JSR = new Instruction ("JSR")
		{
			public void execute ()
			{
				int adr = toWord (PC - 1);
				
				write (0x0100 + S, toByte (adr >> 8));
				setS (S - 1);
				write (0x0100 + S, toByte (adr));
				setS (S - 1);
			}
		};
		
		protected final Instruction LDA = new Instruction ("LDA")
		{
			public void execute ()
			{
				int mem = read (EA);
				int flg = P;
				
				if ((mem & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((mem & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (mem);
				setP (flg);
			}
		};
		
		protected final Instruction LDX = new Instruction ("LDX")
		{
			public void execute ()
			{
				int mem = read (EA);
				int flg = P;
				
				if ((mem & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((mem & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (mem);
				setP (flg);
			}
		};
		
		protected final Instruction LDY = new Instruction ("LDY")
		{
			public void execute ()
			{
				int mem = read (EA);
				int flg = P;
				
				if ((mem & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((mem & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setY (mem);
				setP (flg);
			}
		};
		
		protected final Instruction LSR = new Instruction ("LSR")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = mem >>> 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((mem & 0x001) == 0x001) flg |= F_C; else flg &= ~F_C;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction LSRA = new Instruction ("LSR")
		{
			public void execute ()
			{
				int val = A >>> 1;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((A   & 0x001) == 0x001) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction NOP = new Instruction ("NOP")
		{
			public void execute ()
			{}
		};
		
		protected final Instruction ORA = new Instruction ("ORA")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = A | mem;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);			
			}
		};

		protected final Instruction PHA = new Instruction ("PHA")
		{
			public void execute ()
			{
				write (0x0100 + S, A);
				setS (S - 1);
			}
		};
		
		protected final Instruction PHP = new Instruction ("PHP")
		{
			public void execute ()
			{
				write (0x0100 + S, P);
				setS (S - 1);
			}
		};
		
		protected final Instruction PLA = new Instruction ("PLA")
		{
			public void execute ()
			{
				setS (S + 1);
				
				int val = read (0x0100 + S);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction PLP = new Instruction ("PLP")
		{
			public void execute ()
			{
				setS (S + 1);
				setP (read (0x0100 + S));
			}
		};
		
		protected final Instruction ROL = new Instruction ("ROL")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = (mem << 1) | (P & F_C);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction ROLA = new Instruction ("ROL")
		{
			public void execute ()
			{
				int val = (A << 1) | (P & F_C);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((val & 0x100) == 0x100) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction ROR = new Instruction ("ROR")
		{
			public void execute ()
			{
				int	mem = read (EA);
				int val = (mem >>> 1) | ((P & F_C) << 7);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((mem & 0x001) == 0x001) flg |= F_C; else flg &= ~F_C;
				
				write (EA, val);
				setP (flg);
			}
		};
		
		protected final Instruction RORA = new Instruction ("ROR")
		{
			public void execute ()
			{
				int val = (A >>> 1) | ((P & F_C) << 7);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				if ((A   & 0x001) == 0x001) flg |= F_C; else flg &= ~F_C;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction RTI = new Instruction ("RTI")
		{
			public void execute ()
			{}
		};
		
		protected final Instruction RTS = new Instruction ("RTS")
		{
			public void execute ()
			{
				setS (S + 1);
				int lo = read (0x0100 + S);
				setS (S + 1);
				int hi = read (0x0100 + S);
				
				setPC (((hi << 8) | lo) + 1);
			}
		};
		
		protected final Instruction SBC = new Instruction ("SBC")
		{
			public void execute ()
			{}
		};
		
		protected final Instruction SEC = new Instruction ("SEC")
		{
			public void execute ()
			{
				setP (P | F_C);
			}
		};
		
		protected final Instruction SED = new Instruction ("SED")
		{
			public void execute ()
			{
				setP (P | F_D);
			}
		};
		
		protected final Instruction SEI = new Instruction ("SEI")
		{
			public void execute ()
			{
				setP (P | F_I);
			}
		};
		
		protected final Instruction STA = new Instruction ("STA")
		{
			public void execute ()
			{
				write (EA, A);
			}
		};
		
		protected final Instruction STX = new Instruction ("STX")
		{
			public void execute ()
			{
				write (EA, X);
			}
		};
		
		protected final Instruction STY = new Instruction ("STY")
		{
			public void execute ()
			{
				write (EA, Y);
			}
		};
		
		protected final Instruction TAX = new Instruction ("TAX")
		{
			public void execute ()
			{
				int	val = A;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (val);
				setP (flg);
			}
		};
		
		protected final Instruction TAY = new Instruction ("TAY")
		{
			public void execute ()
			{
				int	val = A;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setY (val);
				setP (flg);
			}
		};
		
		protected final Instruction TSX = new Instruction ("TSX")
		{
			public void execute ()
			{
				int	val = S;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (val);
				setP (flg);
			}
		};
		
		protected final Instruction TXA = new Instruction ("TXA")
		{
			public void execute ()
			{
				int	val = X;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction TXS = new Instruction ("TXS")
		{
			public void execute ()
			{
				setS (X);
			}
		};
		
		protected final Instruction TYA = new Instruction ("TYA")
		{
			public void execute ()
			{
				int	val = Y;
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);
			}
		};
		
		/**
		 * Extra cycles added during address decoding.
		 */
		protected int extra;
	}
	
	/**
	 * Defines the behaviour of a standard NMOS 6502 processor.
	 */
	protected class MOS6502 extends CPU6502
	{
		
		@Override
		public int step ()
		{
			int opcode = read (PC);
			
			addressModes [opcode].decode ();
			instructions [opcode].execute ();
			
			return (cycles [opcode] + extra);
		}
		
		@Override
		public void trace ()
		{
			int opcode = read (PC);
			int size = addressModes [opcode].getSize ();
			
			System.out.print (Hex.toHex (PC, 4));
			
			for (int offset = 0; offset < 3; ++offset) {
				if (offset < size)
					System.out.print (" " + Hex.toHex (read (PC + offset), 2));
				else
					System.out.print ("   ");
			}
			
			System.out.print (instructions [opcode].getOpcode () + " "
					+ addressModes [opcode].forPC (PC));
			
			System.out.println ();
			
			step ();
		}
		
		
		protected final AddressMode [] addressModes = new AddressMode [] {
/* 0 */		IMM, IZX, IMP, IMP, IMP, ZPG, ZPG, IMP, STK, IMM, ACC, IMP, IMP, ABS, ABS, IMP,
/* 1 */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* 2 */		ABS, IZX, IMP, IMP, ZPG, ZPG, ZPG, IMP, STK, IMM, ACC, IMP, ABS, ABS, ABS, IMP,
/* 3 */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* 4 */		IMP, IZX, IMP, IMP, IMP, ZPG, ZPG, IMP, STK, IMM, ACC, IMP, ABS, ABS, ABS, IMP,
/* 5 */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* 6 */		IMP, IZX, IMP, IMP, IMP, ZPG, ZPG, IMP, STK, IMM, ACC, IMP, IND, ABS, ABS, IMP,
/* 7 */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* 8 */		IMP, IZX, IMP, IMP, IMP, ZPG, ZPG, IMP, IMP, IMP, IMP, IMP, ABS, ABS, ABS, IMP,
/* 9 */		REL, IZY, IMP, IMP, ZPG, ZPX, ZPY, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* A */		IMM, IZX, IMM, IMP, ZPX, ZPG, ZPG, IMP, IMP, IMM, IMP, IMP, ABS, ABS, ABS, IMP,
/* B */		REL, IZY, IMP, IMP, ZPG, ZPX, ZPY, IMP, IMP, ABY, IMP, IMP, ABX, ABX, ABY, IMP,
/* C */		IMM, IZX, IMP, IMP, ZPX, ZPG, ZPG, IMP, IMP, IMM, IMP, IMP, ABS, ABS, ABS, IMP,
/* D */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
/* E */		IMM, IZX, IMP, IMP, ZPG, ZPG, ZPG, IMP, IMP, IMM, IMP, IMP, ABS, ABS, ABS, IMP,
/* F */		REL, IZY, IMP, IMP, IMP, ZPX, ZPX, IMP, IMP, ABY, IMP, IMP, IMP, ABX, ABX, IMP,
		};
		                                        
		protected final Instruction [] instructions = new Instruction [] {
/* 0 */		BRK, ORA, NOP, NOP, NOP, ORA, ASL, NOP, PHP, ORA, ASLA,NOP, NOP, ORA, ASL, NOP,
/* 1 */		BPL, ORA, NOP, NOP, NOP, ORA, ASL, NOP, CLC, ORA, NOP, NOP, NOP, ORA, ASL, NOP,
/* 2 */		JSR, AND, NOP, NOP, BIT, AND, ROL, NOP, PLP, AND, ROLA,NOP, BIT, AND, ROL, NOP,
/* 3 */		BMI, AND, NOP, NOP, NOP, AND, ROL, NOP, SEC, AND, NOP, NOP, NOP, AND, ROL, NOP,
/* 4 */		RTI, EOR, NOP, NOP, NOP, EOR, LSR, NOP, PHA, EOR, LSRA,NOP, JMP, EOR, LSR, NOP,
/* 5 */		BVC, EOR, NOP, NOP, NOP, EOR, LSR, NOP, CLI, EOR, NOP, NOP, NOP, EOR, LSR, NOP,
/* 6 */		RTS, ADC, NOP, NOP, NOP, ADC, ROR, NOP, PLA, ADC, RORA,NOP, JMP, ADC, ROR, NOP,
/* 7 */		BVS, ADC, NOP, NOP, NOP, ADC, ROR, NOP, SEI, ADC, NOP, NOP, NOP, ADC, ROR, NOP,
/* 8 */		NOP, STA, NOP, NOP, STY, STA, STX, NOP, DEY, NOP, TXA, NOP, STY, STA, STX, NOP,
/* 9 */		BCC, STA, NOP, NOP, STY, STA, STX, NOP, TYA, STA, TXS, NOP, NOP, STA, NOP, NOP,
/* A */		LDY, LDA, LDX, NOP, LDY, LDA, LDX, NOP, TAY, LDA, TAX, NOP, LDY, LDA, LDX, NOP,
/* B */		BCS, LDA, NOP, NOP, LDY, LDA, LDX, NOP, CLV, LDA, TSX, NOP, LDY, LDA, LDX, NOP,
/* C */		CPY, CMP, NOP, NOP, CPY, CMP, DEC, NOP, INY, CMP, DEX, NOP, CPY, CMP, DEC, NOP,
/* D */		BNE, CMP, NOP, NOP, NOP, CMP, DEC, NOP, CLD, CMP, NOP, NOP, NOP, CMP, DEC, NOP,
/* E */		CPX, SBC, NOP, NOP, CPX, CPX, INC, NOP, INX, SBC, NOP, NOP, CPX, SBC, INC, NOP,
/* F */		BEQ, SBC, NOP, NOP, NOP, NOP, INC, NOP, SED, SBC, NOP, NOP, NOP, SBC, INC, NOP
		};
		
		protected final int [] cycles = new int [] {
			7, 6, 0, 8,  3, 3, 5, 5,  3, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			6, 6, 0, 8,  3, 3, 5, 5,  4, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7,
			
			6, 6, 0, 8,  3, 3, 5, 5,  3, 2, 2, 2,  3, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			6, 6, 0, 8,  3, 3, 5, 5,  4, 2, 2, 2,  5, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			
			2, 6, 2, 6,  3, 3, 3, 3,  2, 2, 2, 2,  4, 4, 4, 4, 
			2, 6, 0, 6,  4, 4, 4, 4,  2, 5, 2, 5,  5, 5, 5, 5, 
			2, 6, 2, 6,  3, 3, 3, 3,  2, 2, 2, 2,  4, 4, 4, 4, 
			2, 5, 0, 5,  4, 4, 4, 4,  2, 4, 2, 4,  4, 4, 4, 4, 
			
			2, 6, 2, 8,  3, 3, 5, 5,  2, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			2, 6, 2, 8,  3, 3, 5, 5,  2, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
		};
	}
		                                  
	/**
	 * Defines the behaviour of a Western Design Center 65C02 processor.
	 */
	protected class WDC65C02 extends CPU6502
	{
		@Override
		public int step ()
		{
			int opcode = read (PC);
			
			addressModes [opcode].decode ();
			instructions [opcode].execute ();
			
			return (cycles [opcode] + extra);
		}
		
		@Override
		public void trace ()
		{
			int opcode = read (PC);
			int size = addressModes [opcode].getSize ();
			
			System.out.print (Hex.toHex (PC, 4));
			
			for (int offset = 0; offset < 3; ++offset) {
				if (offset < size)
					System.out.print (" " + Hex.toHex (read (PC + offset), 2));
				else
					System.out.print ("   ");
			}
			
			System.out.print (instructions [opcode].getOpcode () + " "
					+ addressModes [opcode].forPC (PC));
			
			System.out.println ();
			
			step ();
		}
		
		protected int	MA;
		
		
		protected final AddressMode IND = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1) | (read (PC + 2) << 8);
				EA = read (EA) | (read (EA + 1) << 8);
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("($" + Hex.toHex (mem, 4) + ")");
			}
		};
		
		protected final AddressMode IAX = new AddressMode (3)
		{
			public void decode ()
			{
				extra = 0;
				EA = (read (PC + 1) | (read (PC + 2) << 8)) + X;
				EA = read (EA) | (read (EA + 1) << 8);
				setPC (PC + 3);
			}
			
			public String forPC (int pc)
			{
				int mem = read (pc + 1);
				
				return ("($" + Hex.toHex (mem, 4) + ",X)");
			}
		};
		
		protected final AddressMode IZP = new AddressMode (2)
		{

			@Override
			public void decode ()
			{
				extra = 0;
				EA = read (PC + 1);
				EA = read (EA) | (read (toByte (EA + 1)) << 8);
				
				setPC (PC + 2);
			}

			@Override
			public String forPC (int pc)
			{
				int mem = read (pc);
				
				return ("($" + Hex.toHex (mem, 3) + ")");
			}
		};
		
		protected final AddressMode BRS = new AddressMode (2)
		{
			@Override
			public void decode ()
			{
				EA = read (PC + 1);
				setPC (PC + 2);
			}

			@Override
			public String forPC (int pc)
			{
				int mem	= read (pc + 1);
				
				return ("$" + Hex.toHex (mem, 2));
			}
		};
		
		protected final AddressMode BRL = new AddressMode (3)
		{
			@Override
			public void decode ()
			{
				MA = read (PC + 1);
				EA = read (PC + 2);
				if ((EA & 0x80) != 0) EA |= 0xffffff00;
				EA = (PC + 3) + EA;
				setPC (PC + 3);
			}

			@Override
			public String forPC (int pc)
			{
				MA = read (PC + 1);
				EA = read (PC + 2);
				if ((EA & 0x80) != 0) EA |= 0xffffff00;
				EA = (PC + 3) + EA;
				
				return (" $" + Hex.toHex (MA, 2) + ",$" + Hex.toHex (EA, 4));
			}
		};
		
		protected final class BranchOnBitReset extends Instruction
		{
			public BranchOnBitReset (final String opcode, int mask)
			{
				super (opcode);
				this.mask = mask;
			}
			
			public void execute ()
			{
				if ((read (MA) & mask) == 0)
					setPC (EA);
			}
			
			public final int mask;
		}
		
		protected final Instruction BBR0 = new BranchOnBitReset ("BBR0", 1 << 0);
		protected final Instruction BBR1 = new BranchOnBitReset ("BBR1", 1 << 1);
		protected final Instruction BBR2 = new BranchOnBitReset ("BBR2", 1 << 2);
		protected final Instruction BBR3 = new BranchOnBitReset ("BBR3", 1 << 3);
		protected final Instruction BBR4 = new BranchOnBitReset ("BBR4", 1 << 4);
		protected final Instruction BBR5 = new BranchOnBitReset ("BBR5", 1 << 5);
		protected final Instruction BBR6 = new BranchOnBitReset ("BBR6", 1 << 6);
		protected final Instruction BBR7 = new BranchOnBitReset ("BBR7", 1 << 7);
		
		protected final class BranchOnBitSet extends Instruction
		{
			public BranchOnBitSet (final String opcode, int mask)
			{
				super (opcode);
				this.mask = mask;
			}
			
			public void execute ()
			{
				if ((read (MA) & mask) != 0)
					setPC (EA);
			}
			
			public final int mask;
		}
		
		protected final Instruction BBS0 = new BranchOnBitSet ("BBS0", 1 << 0);
		protected final Instruction BBS1 = new BranchOnBitSet ("BBS1", 1 << 1);
		protected final Instruction BBS2 = new BranchOnBitSet ("BBS2", 1 << 2);
		protected final Instruction BBS3 = new BranchOnBitSet ("BBS3", 1 << 3);
		protected final Instruction BBS4 = new BranchOnBitSet ("BBS4", 1 << 4);
		protected final Instruction BBS5 = new BranchOnBitSet ("BBS5", 1 << 5);
		protected final Instruction BBS6 = new BranchOnBitSet ("BBS6", 1 << 6);
		protected final Instruction BBS7 = new BranchOnBitSet ("BBS7", 1 << 7);
		
		protected final Instruction BRA = new Instruction ("BRA")
		{
			public void execute ()
			{
				setPC (EA);
				++extra;
			}
		};
		
		protected final Instruction DECA = new Instruction ("DEC")
		{
			public void execute ()
			{
				int val = A - 1;
				int flg = P;
				
				if ((val & 0x80) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0xff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction INCA = new Instruction ("INC")
		{
			public void execute ()
			{
				int val = A + 1;
				int flg = P;
				
				if ((val & 0x80) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0xff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setA (val);
				setP (flg);
			}
		};
		
		protected final Instruction PHX = new Instruction ("PHX")
		{
			public void execute ()
			{
				write (0x0100 + S, X);
				setS (S - 1);
			}
		};
		
		protected final Instruction PHY = new Instruction ("PHY")
		{
			public void execute ()
			{
				write (0x0100 + S, Y);
				setS (S - 1);
			}
		};
		
		protected final Instruction PLX = new Instruction ("PLX")
		{
			public void execute ()
			{
				setS (S + 1);
				
				int val = read (0x0100 + S);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setX (val);
				setP (flg);
			}
		};
		
		protected final Instruction PLY = new Instruction ("PLY")
		{
			public void execute ()
			{
				setS (S + 1);
				
				int val = read (0x0100 + S);
				int flg = P;
				
				if ((val & 0x080) == 0x080) flg |= F_N; else flg &= ~F_N;
				if ((val & 0x0ff) == 0x000) flg |= F_Z; else flg &= ~F_Z;
				
				setY (val);
				setP (flg);
			}
		};
		
		protected final class ResetMemoryBit extends Instruction
		{
			public ResetMemoryBit (final String opcode, int mask)
			{
				super (opcode);
				this.mask = mask;
			}
			
			public void execute ()
			{
				write (EA, read (EA) & ~mask);
			}
			
			public final int mask;
		}
		
		protected final Instruction RMB0 = new ResetMemoryBit ("RMB0", 1 << 0);
		protected final Instruction RMB1 = new ResetMemoryBit ("RMB1", 1 << 1);
		protected final Instruction RMB2 = new ResetMemoryBit ("RMB2", 1 << 2);
		protected final Instruction RMB3 = new ResetMemoryBit ("RMB3", 1 << 3);
		protected final Instruction RMB4 = new ResetMemoryBit ("RMB4", 1 << 4);
		protected final Instruction RMB5 = new ResetMemoryBit ("RMB5", 1 << 5);
		protected final Instruction RMB6 = new ResetMemoryBit ("RMB6", 1 << 6);
		protected final Instruction RMB7 = new ResetMemoryBit ("RMB7", 1 << 7);
		
		protected final class SetMemoryBit extends Instruction
		{
			public SetMemoryBit (final String opcode, int mask)
			{
				super (opcode);
				this.mask = mask;
			}
			
			public void execute ()
			{
				write (EA, read (EA) | mask);
			}
			
			public final int mask;
		}
		
		protected final Instruction SMB0 = new SetMemoryBit ("SMB0", 1 << 0);
		protected final Instruction SMB1 = new SetMemoryBit ("SMB1", 1 << 1);
		protected final Instruction SMB2 = new SetMemoryBit ("SMB2", 1 << 2);
		protected final Instruction SMB3 = new SetMemoryBit ("SMB3", 1 << 3);
		protected final Instruction SMB4 = new SetMemoryBit ("SMB4", 1 << 4);
		protected final Instruction SMB5 = new SetMemoryBit ("SMB5", 1 << 5);
		protected final Instruction SMB6 = new SetMemoryBit ("SMB6", 1 << 6);
		protected final Instruction SMB7 = new SetMemoryBit ("SMB7", 1 << 7);
		
		protected final Instruction STP = new Instruction ("STP")
		{
			public void execute ()
			{
				;
			}
		};
		
		protected final Instruction STZ = new Instruction ("STZ")
		{
			public void execute ()
			{
				write (EA, 0);
			}
		};
		
		protected final Instruction TRB = new Instruction ("TRB")
		{
			public void execute ()
			{
			}
		};
		
		protected final Instruction TSB = new Instruction ("TSB")
		{
			public void execute ()
			{
			}
		};
		
		protected final Instruction WAI = new Instruction ("WAI")
		{
			public void execute ()
			{
			}
		};
		
		protected final AddressMode [] addressModes = new AddressMode [] {
/* 0 */		IMM, IZX, IMP, IMP, ZPG, ZPG, ZPG, BRS, STK, IMM, ACC, IMP, ABS, ABS, ABS, BRL,
/* 1 */		REL, IZY, IZP, IMP, ZPG, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, ABS, ABX, ABX, BRL,
/* 2 */		ABS, IZX, IMP, IMP, ZPG, ZPG, ZPG, BRS, STK, IMM, ACC, IMP, ABS, ABS, ABS, BRL,
/* 3 */		REL, IZY, IZP, IMP, ZPG, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, ABX, ABX, ABX, BRL,
/* 4 */		IMP, IZX, IMP, IMP, IMP, ZPG, ZPG, BRS, STK, IMM, ACC, IMP, ABS, ABS, ABS, BRL,
/* 5 */		REL, IZY, IZP, IMP, IMP, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, IMP, ABX, ABX, BRL,
/* 6 */		IMP, IZX, IMP, IMP, ZPG, ZPG, ZPG, BRS, STK, IMM, ACC, IMP, IND, ABS, ABS, BRL,
/* 7 */		REL, IZY, IZP, IMP, ZPX, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, IMP, ABX, ABX, BRL,
/* 8 */		REL, IZX, IMP, IMP, IMP, ZPG, ZPG, BRS, IMP, IMP, IMP, IMP, ABS, ABS, ABS, BRL,
/* 9 */		REL, IZY, IZP, IMP, ZPG, ZPX, ZPY, BRS, IMP, ABY, IMP, IMP, IAX, ABX, ABX, BRL,
/* A */		IMM, IZX, IMM, IMP, ZPX, ZPG, ZPG, BRS, IMP, IMM, IMP, IMP, ABS, ABS, ABS, BRL,
/* B */		REL, IZY, IZP, IMP, ZPG, ZPX, ZPY, BRS, IMP, ABY, IMP, IMP, ABX, ABX, ABY, BRL,
/* C */		IMM, IZX, IMP, IMP, ZPX, ZPG, ZPG, BRS, IMP, IMM, IMP, IMP, ABS, ABS, ABS, BRL,
/* D */		REL, IZY, IZP, IMP, IMP, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, IMP, ABX, ABX, BRL,
/* E */		IMM, IZX, IMP, IMP, ZPG, ZPG, ZPG, BRS, IMP, IMM, IMP, IMP, ABS, ABS, ABS, BRL,
/* F */		REL, IZY, IZP, IMP, IMP, ZPX, ZPX, BRS, IMP, ABY, IMP, IMP, IMP, ABX, ABX, BRL,
		};
		                                        
		protected final Instruction [] instructions = new Instruction [] {
/* 0 */		BRK, ORA, NOP, NOP, TSB, ORA, ASL, RMB0,PHP, ORA, ASLA,NOP, TSB, ORA, ASL, BBR0,
/* 1 */		BPL, ORA, ORA, NOP, TRB, ORA, ASL, RMB1,CLC, ORA, NOP, NOP, TRB, ORA, ASL, BBR1,
/* 2 */		JSR, AND, NOP, NOP, BIT, AND, ROL, RMB2,PLP, AND, ROLA,NOP, BIT, AND, ROL, BBR2,
/* 3 */		BMI, AND, AND, NOP, BIT, AND, ROL, RMB3,SEC, AND, NOP, NOP, BIT, AND, ROL, BBR3,
/* 4 */		RTI, EOR, NOP, NOP, NOP, EOR, LSR, RMB4,PHA, EOR, LSRA,NOP, JMP, EOR, LSR, BBR4,
/* 5 */		BVC, EOR, EOR, NOP, NOP, EOR, LSR, RMB5,CLI, EOR, PHY, NOP, NOP, EOR, LSR, BBR5,
/* 6 */		RTS, ADC, NOP, NOP, STZ, ADC, ROR, RMB6,PLA, ADC, RORA,NOP, JMP, ADC, ROR, BBR6,
/* 7 */		BVS, ADC, ADC, NOP, STZ, ADC, ROR, RMB7,SEI, ADC, PLY, NOP, JMP, ADC, ROR, BBR7,
/* 8 */		BRA, STA, NOP, NOP, STY, STA, STX, SMB0,DEY, NOP, TXA, NOP, STY, STA, STX, BBS0,
/* 9 */		BCC, STA, STA, NOP, STY, STA, STX, SMB1,TYA, STA, TXS, NOP, STZ, STA, STZ, BBS1,
/* A */		LDY, LDA, LDX, NOP, LDY, LDA, LDX, SMB2,TAY, LDA, TAX, NOP, LDY, LDA, LDX, BBS2,
/* B */		BCS, LDA, LDA, NOP, LDY, LDA, LDX, SMB3,CLV, LDA, TSX, NOP, LDY, LDA, LDX, BBS3,
/* C */		CPY, CMP, NOP, NOP, CPY, CMP, DEC, SMB4,INY, CMP, DEX, WAI, CPY, CMP, DEC, BBS4,
/* D */		BNE, CMP, CMP, NOP, NOP, CMP, DEC, SMB5,CLD, CMP, PHX, STP, NOP, CMP, DEC, BBS5,
/* E */		CPX, SBC, NOP, NOP, CPX, CPX, INC, SMB6,INX, SBC, NOP, NOP, CPX, SBC, INC, BBS6,
/* F */		BEQ, SBC, SBC, NOP, NOP, NOP, INC, SMB7,SED, SBC, PLX, NOP, NOP, SBC, INC, BBS7
		};
		
		protected final int [] cycles = new int [] {
			7, 6, 0, 8,  3, 3, 5, 5,  3, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			6, 6, 0, 8,  3, 3, 5, 5,  4, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7,
			
			6, 6, 0, 8,  3, 3, 5, 5,  3, 2, 2, 2,  3, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			6, 6, 0, 8,  3, 3, 5, 5,  4, 2, 2, 2,  5, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			
			2, 6, 2, 6,  3, 3, 3, 3,  2, 2, 2, 2,  4, 4, 4, 4, 
			2, 6, 0, 6,  4, 4, 4, 4,  2, 5, 2, 5,  5, 5, 5, 5, 
			2, 6, 2, 6,  3, 3, 3, 3,  2, 2, 2, 2,  4, 4, 4, 4, 
			2, 5, 0, 5,  4, 4, 4, 4,  2, 4, 2, 4,  4, 4, 4, 4, 
			
			2, 6, 2, 8,  3, 3, 5, 5,  2, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
			2, 6, 2, 8,  3, 3, 5, 5,  2, 2, 2, 2,  4, 4, 6, 6, 
			2, 5, 0, 8,  4, 4, 6, 6,  2, 4, 2, 7,  4, 4, 7, 7, 
		};
	}
	
	protected static int	F_N = 1 << 7;
	protected static int	F_V	= 1 << 6;
	protected static int	F_B	= 1 << 4;
	protected static int	F_D = 1 << 3;
	protected static int	F_I	= 1 << 2;
	protected static int	F_Z = 1 << 1;
	protected static int	F_C	= 1 << 0;
	
	
	protected int			A =	0;
	
	protected int			X = 0;
	
	protected int			Y =	0;
	
	protected int			S =	0;
	
	protected int			P = 0x20;
	
	protected int			PC = 0;
	
	protected int			EA;
	
	protected CPU6502		cpu;
	
	@Override
	protected void startUp ()
	{
		cpu = new MOS6502 ();
	}
		
	@Override
	protected void execute ()
	{
		// TODO Auto-generated method stub
		
	}
	
	protected void setA (int value)
	{
		A = toByte (value);
	}
	
	protected void setX (int value)
	{
		X = toByte (value);
	}

	protected void setY (int value)
	{
		Y = toByte (value);
	}

	protected void setS (int value)
	{
		S = toByte (value);
	}
	
	protected void setP (int value)
	{
		P = toByte (value | 0x30);
	}

	protected void setPC (int value)
	{
		PC = toWord (value);
	}

	protected int read (int addr)
	{
		return (0);
	}
	
	protected void write (int addr, int value)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int step ()
	{
		return (cpu.step ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void trace ()
	{
		cpu.trace ();
	}

	/**
	 * Ensure a value is in a valid byte (0x00-0xff) range.
	 * 
	 * @param	value			An arbitrary value
	 * @return	The byte portion of the value.
	 */
	protected static int toByte (int value)
	{
		return (value & 0xff);
	}
	
	/**
	 * Ensure a value is in a valid word (0x0000-0xffff) range.
	 * 
	 * @param	value			An arbitrary value
	 * @return	The word portion of the value.
	 */
	protected static int toWord (int value)
	{
		return (value & 0xffff);
	}
}