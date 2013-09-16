/*
 * Copyright (C),2013 Andrew John Jacobs.
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

package uk.co.demon.obelisk.i8080;

import java.util.Hashtable;

import uk.co.demon.obelisk.xasm.Assembler;
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
 * The <CODE>As8080</CODE> provides the base <CODE>Assembler</CODE> with an
 * understanding of Intel 8080 family assembler conventions.
 *
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class As8080 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As8080 ().run (args);
	}

	
	protected class ImpliedOpcode extends Opcode
	{
		public ImpliedOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			addByte (opcode);
			return (true);
		}
		
		private final int opcode;
	}
			
	protected class RegisterOpcode extends Opcode
	{
		public RegisterOpcode (TokenKind kind, String text, int opcode, int shift)
		{
			super (kind, text);
			
			this.opcode = opcode;
			this.shift = shift;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			
			if (token == A) addByte (opcode | (0x07 << shift));
			else if (token == B) addByte (opcode | (0x00 << shift));
			else if (token == C) addByte (opcode | (0x01 << shift));
			else if (token == D) addByte (opcode | (0x02 << shift));
			else if (token == E) addByte (opcode | (0x03 << shift));
			else if (token == H) addByte (opcode | (0x04 << shift));
			else if (token == L) addByte (opcode | (0x05 << shift));
			else if (token == M) addByte (opcode | (0x06 << shift));
			else
				error (ERR_MISSING_REGISTER);
			
			if (nextRealToken () != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
		
		private final int shift;
	}
			
	protected class RegisterPairSPOpcode extends Opcode
	{
		public RegisterPairSPOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			
			if (token == B) addByte (opcode | 0x00);
			else if (token == D) addByte (opcode | 0x10);
			else if (token == H) addByte (opcode | 0x20);
			else if (token == SP) addByte (opcode | 0x30);
			else
				error (ERR_MISSING_REGISTER);
			
			if (nextRealToken () != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}

	protected class RegisterPairPSWOpcode extends Opcode
	{
		public RegisterPairPSWOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			
			if (token == B) addByte (opcode | 0x00);
			else if (token == D) addByte (opcode | 0x10);
			else if (token == H) addByte (opcode | 0x20);
			else if (token == PSW) addByte (opcode | 0x30);
			else
				error (ERR_MISSING_REGISTER_PAIR_PSW);
			
			if (nextRealToken () != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}

	protected class ImmediateOpcode extends Opcode
	{
		public ImmediateOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseImmd ();
			
			if (expr != null) {
				addByte (opcode);
				addByte (expr);
			}

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}

	protected class ImmediatePairOpcode extends Opcode
	{
		public ImmediatePairOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			int		reg = 0;
			
			if (token == B) reg = 0x00 << 4;
			else if (token == D) reg = 0x01 << 4;
			else if (token == H) reg = 0x02 << 4;
			else if (token == SP) reg = 0x03 << 4;
			else {
				error (ERR_MISSING_REGISTER_PAIR_SP);
				return (true);
			}
			
			if ((token = nextRealToken ()) != COMMA) {
				error (ERR_SYNTAX);
				return (true);
			}
			
			token = nextRealToken ();			
			Expr expr = parseImmd ();
			
			if (expr != null) {
				addByte (opcode | reg);
				addWord (expr);
			}

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}

	protected class MoveRegisterOpcode extends Opcode
	{
		public MoveRegisterOpcode ()
		{
			super (KEYWORD, "MOV");
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			int		opcode	= 0x40;
			Token 	src;
			Token	dst;
			
			dst = token	= nextRealToken ();
			
			if (token == A) opcode |= (0x07 << 3);
			else if (token == B) opcode |= (0x00 << 3);
			else if (token == C) opcode |= (0x01 << 3);
			else if (token == D) opcode |= (0x02 << 3);
			else if (token == E) opcode |= (0x03 << 3);
			else if (token == H) opcode |= (0x04 << 3);
			else if (token == L) opcode |= (0x05 << 3);
			else if (token == M) opcode |= (0x06 << 3);
			else {
				error (ERR_MISSING_REGISTER);
				return (true);
			}
			
			if ((token = nextRealToken ()) != COMMA) {
				error (ERR_SYNTAX);
				return (true);
			}
			
			src = token	= nextRealToken ();
			
			if (token == A) opcode |= (0x07 << 0);
			else if (token == B) opcode |= (0x00 << 0);
			else if (token == C) opcode |= (0x01 << 0);
			else if (token == D) opcode |= (0x02 << 0);
			else if (token == E) opcode |= (0x03 << 0);
			else if (token == H) opcode |= (0x04 << 0);
			else if (token == L) opcode |= (0x05 << 0);
			else if (token == M) opcode |= (0x06 << 0);
			else {
				error (ERR_MISSING_REGISTER);
				return (true);
			}
			
			if ((src == M) && (dst == M)) {
				error ("MOV M,M is not allowed");
				return (true);
			}
			else
				addByte (opcode);
			
			if (nextRealToken () != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
	}

	protected class MoveImmediateOpcode extends Opcode
	{
		public MoveImmediateOpcode ()
		{
			super (KEYWORD, "MVI");
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			int		opcode	= 0x06;
			
			token = nextRealToken ();
			
			if (token == A) opcode |= (0x07 << 3);
			else if (token == B) opcode |= (0x00 << 3);
			else if (token == C) opcode |= (0x01 << 3);
			else if (token == D) opcode |= (0x02 << 3);
			else if (token == E) opcode |= (0x03 << 3);
			else if (token == H) opcode |= (0x04 << 3);
			else if (token == L) opcode |= (0x05 << 3);
			else if (token == M) opcode |= (0x06 << 3);
			else {
				error (ERR_MISSING_REGISTER);
				return (true);
			}
			
			if ((token = nextRealToken ()) != COMMA) {
				error (ERR_SYNTAX);
				return (true);
			}
			
			token	= nextRealToken ();
			Expr expr = parseImmd ();
			
			if (expr != null) {
				addByte (opcode);
				addByte (expr);
			}
			
			if (nextRealToken () != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
	}

	protected class RestartOpcode extends Opcode
	{
		public RestartOpcode ()
		{
			super (KEYWORD, "RST");
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseImmd ();
			
			if ((expr != null) && expr.isAbsolute ()) {
				long	value = expr.resolve (null, null);
				
				if ((0 <= value) && (value <= 7))
					addByte (0xc7 | ((int) value << 3));
				else {
					error ("Invalid RST value - must be 0-7");
					return (true);		
				}
			}

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
	}

	protected class AccumulatorIndirectOpcode extends Opcode
	{
		public AccumulatorIndirectOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			
			if (token == B) addByte (opcode | 0x00);
			else if (token == D) addByte (opcode | 0x10);
			else {
				error ("Missing register (B or D)");
				return (true);
			}

			token = nextRealToken ();

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class AddressOpcode extends Opcode
	{
		public AddressOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseImmd ();
			
			if (expr != null) {
				addByte (opcode);
				addWord (expr);
			}

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	/**
	 * Constructs an <CODE>As8080</CODE> instance and initialises the object
	 * module.
	 */
	protected As8080 ()
	{
		super (new Module ("8080", false));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}
	
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
	 * A <CODE>Token</CODE> representing the C register.
	 */
	protected final Token 	C
		= new Token (KEYWORD, "C");

	/**
	 * A <CODE>Token</CODE> representing the D register.
	 */
	protected final Token 	D
		= new Token (KEYWORD, "D");

	/**
	 * A <CODE>Token</CODE> representing the E register.
	 */
	protected final Token 	E
		= new Token (KEYWORD, "E");

	/**
	 * A <CODE>Token</CODE> representing the H register.
	 */
	protected final Token 	H
		= new Token (KEYWORD, "H");

	/**
	 * A <CODE>Token</CODE> representing the L register.
	 */
	protected final Token 	L
		= new Token (KEYWORD, "L");

	/**
	 * A <CODE>Token</CODE> representing the M register.
	 */
	protected final Token 	M
		= new Token (KEYWORD, "M");

	/**
	 * A <CODE>Token</CODE> representing the PSW register.
	 */
	protected final Token 	PSW
		= new Token (KEYWORD, "PSW");

	/**
	 * A <CODE>Token</CODE> representing the SP register.
	 */
	protected final Token 	SP
		= new Token (KEYWORD, "SP");

	/**
	 * An <CODE>Opcode</CODE> that handles the ACI instruction.
	 */
	protected final Opcode 	ACI		= new ImmediateOpcode (KEYWORD, "ACI", 0xce);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADC instruction.
	 */
	protected final Opcode 	ADC		= new RegisterOpcode (KEYWORD, "ADC", 0x88, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADD instruction.
	 */
	protected final Opcode 	ADD		= new RegisterOpcode (KEYWORD, "ADD", 0x80, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADI instruction.
	 */
	protected final Opcode 	ADI		= new ImmediateOpcode (KEYWORD, "ADI", 0xc6);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ANA instruction.
	 */
	protected final Opcode 	ANA		= new RegisterOpcode (KEYWORD, "ANA", 0xa0, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ANI instruction.
	 */
	protected final Opcode 	ANI		= new ImmediateOpcode (KEYWORD, "ANI", 0xe6);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CALL instruction.
	 */
	protected final Opcode 	CALL	= new AddressOpcode (KEYWORD, "CALL", 0xcd);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CC instruction.
	 */
	protected final Opcode 	CC		= new AddressOpcode (KEYWORD, "CC", 0xdc);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CM instruction.
	 */
	protected final Opcode 	CM		= new AddressOpcode (KEYWORD, "CM", 0xfc);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CMA instruction.
	 */
	protected final Opcode 	CMA		= new ImpliedOpcode (KEYWORD, "CMA", 0x2f);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CMC instruction.
	 */
	protected final Opcode 	CMC		= new ImpliedOpcode (KEYWORD, "CMC", 0x3f);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CMP instruction.
	 */
	protected final Opcode 	CMP		= new RegisterOpcode (KEYWORD, "CMP", 0xb8, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CNC instruction.
	 */
	protected final Opcode 	CNC		= new AddressOpcode (KEYWORD, "CNC", 0xd4);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CNZ instruction.
	 */
	protected final Opcode 	CNZ		= new AddressOpcode (KEYWORD, "CNZ", 0xc4);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CP instruction.
	 */
	protected final Opcode 	CP		= new AddressOpcode (KEYWORD, "CP", 0xf4);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CPE instruction.
	 */
	protected final Opcode 	CPE		= new AddressOpcode (KEYWORD, "CPE", 0xec);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CPI instruction.
	 */
	protected final Opcode 	CPI		= new ImmediateOpcode (KEYWORD, "CPI", 0xfe);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CPO instruction.
	 */
	protected final Opcode 	CPO		= new AddressOpcode (KEYWORD, "CPO", 0xe4);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CZ instruction.
	 */
	protected final Opcode 	CZ		= new AddressOpcode (KEYWORD, "CZ", 0xcc);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DAA instruction.
	 */
	protected final Opcode 	DAA		= new ImpliedOpcode (KEYWORD, "DAA", 0x27);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DAD instruction.
	 */
	protected final Opcode 	DAD		= new RegisterPairSPOpcode (KEYWORD, "DAD", 0x09);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DCR instruction.
	 */
	protected final Opcode 	DCR		= new RegisterOpcode (KEYWORD, "DCR", 0x05, 3);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DCX instruction.
	 */
	protected final Opcode 	DCX		= new RegisterPairSPOpcode (KEYWORD, "DCX", 0x0b);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DI instruction.
	 */
	protected final Opcode 	DI		= new ImpliedOpcode (KEYWORD, "DI", 0xf3);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the EI instruction.
	 */
	protected final Opcode 	EI		= new ImpliedOpcode (KEYWORD, "EI", 0xfb);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the HLT instruction.
	 */
	protected final Opcode 	HLT		= new ImpliedOpcode (KEYWORD, "HLT", 0x76);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the IN instruction.
	 */
	protected final Opcode 	IN		= new ImmediateOpcode (KEYWORD, "IN", 0xdd);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the INR instruction.
	 */
	protected final Opcode 	INR		= new RegisterOpcode (KEYWORD, "INR", 0x04, 3);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the INX instruction.
	 */
	protected final Opcode 	INX		= new RegisterPairSPOpcode (KEYWORD, "INX", 0x03);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JC instruction.
	 */
	protected final Opcode 	JC		= new AddressOpcode (KEYWORD, "JC", 0xda);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JM instruction.
	 */
	protected final Opcode 	JM		= new AddressOpcode (KEYWORD, "JM", 0xfa);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JMP instruction.
	 */
	protected final Opcode 	JMP		= new AddressOpcode (KEYWORD, "JMP", 0xc3);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JNC instruction.
	 */
	protected final Opcode 	JNC		= new AddressOpcode (KEYWORD, "JNC", 0xd2);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JNZ instruction.
	 */
	protected final Opcode 	JNZ		= new AddressOpcode (KEYWORD, "JNZ", 0xc2);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JP instruction.
	 */
	protected final Opcode 	JP		= new AddressOpcode (KEYWORD, "JP", 0xf2);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JPE instruction.
	 */
	protected final Opcode 	JPE		= new AddressOpcode (KEYWORD, "JPE", 0xea);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JPO instruction.
	 */
	protected final Opcode 	JPO		= new AddressOpcode (KEYWORD, "JPO", 0xe2);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JZ instruction.
	 */
	protected final Opcode 	JZ		= new AddressOpcode (KEYWORD, "JZ", 0xca);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LDA instruction.
	 */
	protected final Opcode 	LDA		= new AddressOpcode (KEYWORD, "LDA", 0x3a);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LDAX instruction.
	 */
	protected final Opcode 	LDAX	= new AccumulatorIndirectOpcode (KEYWORD, "LDAX", 0x0a);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LHLD instruction.
	 */
	protected final Opcode 	LHLD	= new AddressOpcode (KEYWORD, "LHLD", 0x2a);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LXI instruction.
	 */
	protected final Opcode 	LXI		= new ImmediatePairOpcode (KEYWORD, "LXI", 0x01);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the MOV instruction.
	 */
	protected final Opcode 	MOV		= new MoveRegisterOpcode ();
		
	/**
	 * An <CODE>Opcode</CODE> that handles the MVI instruction.
	 */
	protected final Opcode 	MVI		= new MoveImmediateOpcode ();
	
	/**
	 * An <CODE>Opcode</CODE> that handles the NOP instruction.
	 */
	protected final Opcode 	NOP		= new ImpliedOpcode (KEYWORD, "NOP", 0x00);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ORA instruction.
	 */
	protected final Opcode 	ORA		= new RegisterOpcode (KEYWORD, "ORA", 0xb0, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ORI instruction.
	 */
	protected final Opcode 	ORI		= new ImmediateOpcode (KEYWORD, "ORI", 0xf6);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the OUT instruction.
	 */
	protected final Opcode 	OUT		= new ImmediateOpcode (KEYWORD, "OUT", 0xd3);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the PCHL instruction.
	 */
	protected final Opcode 	PCHL	= new ImpliedOpcode (KEYWORD, "PCHL", 0xe9);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the POP instruction.
	 */
	protected final Opcode 	POP		= new RegisterPairPSWOpcode (KEYWORD, "POP", 0xc1);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the PUSH instruction.
	 */
	protected final Opcode 	PUSH	= new RegisterPairPSWOpcode (KEYWORD, "PUSH", 0xc5);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RAL instruction.
	 */
	protected final Opcode 	RAL		= new ImpliedOpcode (KEYWORD, "RAL", 0x17);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RAR instruction.
	 */
	protected final Opcode 	RAR		= new ImpliedOpcode (KEYWORD, "RAR", 0x1f);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RC instruction.
	 */
	protected final Opcode 	RC		= new ImpliedOpcode (KEYWORD, "RC", 0xd8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CMA instruction.
	 */
	protected final Opcode 	RET		= new ImpliedOpcode (KEYWORD, "RET", 0xc9);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RIM instruction.
	 */
	protected final Opcode 	RIM		= new ImpliedOpcode (KEYWORD, "RIM", 0x20);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RLC instruction.
	 */
	protected final Opcode 	RLC		= new ImpliedOpcode (KEYWORD, "RLC", 0x07);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RM instruction.
	 */
	protected final Opcode 	RM		= new ImpliedOpcode (KEYWORD, "RM", 0xf8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RNC instruction.
	 */
	protected final Opcode 	RNC		= new ImpliedOpcode (KEYWORD, "RNC", 0xd0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RNZ instruction.
	 */
	protected final Opcode 	RNZ		= new ImpliedOpcode (KEYWORD, "RNZ", 0xc0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RP instruction.
	 */
	protected final Opcode 	RP		= new ImpliedOpcode (KEYWORD, "RP", 0xf0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RPE instruction.
	 */
	protected final Opcode 	RPE		= new ImpliedOpcode (KEYWORD, "RPE", 0xe8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RPO instruction.
	 */
	protected final Opcode 	RPO		= new ImpliedOpcode (KEYWORD, "RPO", 0xe0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RRC instruction.
	 */
	protected final Opcode 	RRC		= new ImpliedOpcode (KEYWORD, "RRC", 0x0f);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RST instruction.
	 */
	protected final Opcode 	RST		= new RestartOpcode ();
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RZ instruction.
	 */
	protected final Opcode 	RZ		= new ImpliedOpcode (KEYWORD, "RZ", 0xc8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SBB instruction.
	 */
	protected final Opcode 	SBB		= new RegisterOpcode (KEYWORD, "SBB", 0x98, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SBI instruction.
	 */
	protected final Opcode 	SBI		= new ImmediateOpcode (KEYWORD, "SBI", 0xde);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SHLD instruction.
	 */
	protected final Opcode 	SHLD	= new AddressOpcode (KEYWORD, "SHLD", 0x22);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SIM instruction.
	 */
	protected final Opcode 	SIM		= new ImpliedOpcode (KEYWORD, "SIM", 0x30);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SPHL instruction.
	 */
	protected final Opcode 	SPHL	= new ImpliedOpcode (KEYWORD, "SPHL", 0xf9);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the STA instruction.
	 */
	protected final Opcode 	STA		= new AddressOpcode (KEYWORD, "STA", 0x32);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the STAX instruction.
	 */
	protected final Opcode 	STAX	= new AccumulatorIndirectOpcode (KEYWORD, "STAX", 0x02);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the STC instruction.
	 */
	protected final Opcode 	STC	= new ImpliedOpcode (KEYWORD, "STC", 0x37);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SUB instruction.
	 */
	protected final Opcode 	SUB		= new RegisterOpcode (KEYWORD, "SUB", 0x90, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SUI instruction.
	 */
	protected final Opcode 	SUI		= new ImmediateOpcode (KEYWORD, "SUI", 0xd6);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XCHG instruction.
	 */
	protected final Opcode 	XCHG	= new ImpliedOpcode (KEYWORD, "XCHG", 0xeb);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XRA instruction.
	 */
	protected final Opcode 	XRA		= new RegisterOpcode (KEYWORD, "XRA", 0xa8, 0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XRI instruction.
	 */
	protected final Opcode 	XRI		= new ImmediateOpcode (KEYWORD, "XRI", 0xee);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XTHL instruction.
	 */
	protected final Opcode 	XTHL	= new ImpliedOpcode (KEYWORD, "XTHL", 0xe3);
	
	/**
	 * {@inheritDoc}
	 */
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
		addToken (ACI);
		addToken (ADC);
		addToken (ADD);
		addToken (ADI);
		addToken (ANA);
		addToken (ANI);
		addToken (CALL);
		addToken (CC);
		addToken (CM);
		addToken (CMA);
		addToken (CMC);
		addToken (CMP);
		addToken (CNC);
		addToken (CNZ);
		addToken (CP);
		addToken (CPE);
		addToken (CPI);
		addToken (CPO);
		addToken (CZ);
		addToken (DAA);
		addToken (DAD);
		addToken (DCR);
		addToken (DCX);
		addToken (DI);
		addToken (EI);
		addToken (HLT);
		addToken (IN);
		addToken (INR);
		addToken (INX);
		addToken (JC);
		addToken (JM);
		addToken (JMP);
		addToken (JNC);
		addToken (JNZ);
		addToken (JP);
		addToken (JPE);
		addToken (JPO);
		addToken (JZ);
		addToken (LDA);
		addToken (LDAX);
		addToken (LXI);
		addToken (LHLD);
		addToken (MOV);
		addToken (MVI);
		addToken (NOP);
		addToken (ORA);
		addToken (ORI);
		addToken (OUT);
		addToken (PCHL);
		addToken (POP);
		addToken (PUSH);
		addToken (RAL);
		addToken (RAR);
		addToken (RC);
		addToken (RET);
		addToken (RIM);
		addToken (RLC);
		addToken (RM);
		addToken (RNC);
		addToken (RNZ);
		addToken (RP);
		addToken (RPE);
		addToken (RPO);
		addToken (RRC);
		addToken (RST);
		addToken (RZ);
		addToken (SBB);
		addToken (SBI);
		addToken (SHLD);
		addToken (SIM);
		addToken (SPHL);
		addToken (STA);
		addToken (STAX);
		addToken (STC);
		addToken (SUB);
		addToken (SUI);
		addToken (XCHG);
		addToken (XRA);
		addToken (XRI);
		addToken (XTHL);
		
		// Registers
		addToken (A);
		addToken (B);
		addToken (C);
		addToken (D);
		addToken (E);
		addToken (H);
		addToken (L);
		addToken (M);
		
		addToken (PSW);
		addToken (SP);

		super.startUp ();
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected boolean isSupportedPass (final Pass pass)
	{
		return (pass != Pass.INTERMEDIATE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void startPass ()
	{
		super.startPass ();
		
		title = "Portable Intel 8080/8085 Assembler - V1.0.0 (2013-09-16)";
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
					if (index < byteCount)
						output.append (Hex.toHex (memory.getByte (index), 2));
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
	
	private static final String ERR_UNEXPECTED_TEXT
		= "Unexpected text after instruction";

	private static final String ERR_TEXT_TOO_LONG_FOR_IMMD
		= "Text literal is too long to be used in an immediate expression";

	private static final String ERR_MISSING_EXPRESSION
		= "Missing expression";
	
	private static final String ERR_MISSING_REGISTER
		= "Missing register (A,B,C,D,E,H,L or M)";
	
	private static final String ERR_MISSING_REGISTER_PAIR_SP
		= "Missing register pair (B,D,H or SP)";

	private static final String ERR_MISSING_REGISTER_PAIR_PSW
		= "Missing register pair (B,D,H or PSW)";
	
	/**
	 * A <CODE>Hashtable</CODE> of keyword tokens to speed up classification.
	 */
	private Hashtable<String, Token> tokens	= new Hashtable<String, Token> ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to format output.
	 */
	private StringBuffer			output 	= new StringBuffer ();
	
	/**
	 * A <CODE>StringBuffer</CODE> used to build up new tokens.
	 */
	private StringBuffer			buffer	= new StringBuffer ();
	
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
		int	value = 0;
		
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
		
		switch (ch) {
		case '^':	return (BINARYXOR);
		case '-':	return (MINUS);
		case '+':	return (PLUS);
		case '*':	return (TIMES);
		
		case '/':	return (DIVIDE);
		case '%':	return (MODULO);

		case ';':
			{
				// Consume characters after the start of a comment
				while (nextChar () != '\0') ;
				return (EOL);
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
		
		// Handle numbers and symbols
		if ((ch == '.') || (ch == '_') || isAlphanumeric (ch)) {
			buffer.append (ch);
			ch = peekChar ();
			while ((ch == '.') || (ch == '_') || isAlphanumeric (ch)) {
				buffer.append (nextChar());
				ch = peekChar ();
			}
			
			String text = buffer.toString ();
			
			if (text.matches ("[0-1]+(b|B)")) {
				for (int index = 0; index < text.length () - 1; ++index) {
					value = value * 2 + (text.charAt (index) - '0');
				}
				return (new Token (NUMBER, text, new Integer (value)));
			}
			if (text.matches ("[0-7]+(o|O)")) {
				for (int index = 0; index < text.length () - 1; ++index) {
					value = value * 8 + (text.charAt (index) - '0');
				}
				return (new Token (NUMBER, text, new Integer (value)));				
			}
			if (text.matches ("[0-9]+")) {
				for (int index = 0; index < text.length (); ++index) {
					value = value * 10 + (text.charAt (index) - '0');
				}
				return (new Token (NUMBER, text, new Integer (value)));				
			}
			if (text.matches ("[0-9][0-9a-fA-f]*(h|H)")) {
				for (int index = 0; index < text.length () - 1; ++index) {
					ch = text.charAt (index);
					if (('a' <= ch) && (ch <= 'f'))
						value = value * 16 + (ch - 'a') + 10;
					else if (('A' <= ch) && (ch <= 'F'))
						value = value * 16 + (ch - 'A') + 10;
					else
						value = value * 16 + (ch - '0');
				}
				return (new Token (NUMBER, text, new Integer (value)));				
			}
			
			Token	opcode = (Token) tokens.get (text.toUpperCase ());

			if (opcode != null)	return (opcode);
			
			return (new Token (SYMBOL, text));
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
}
