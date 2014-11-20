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

package uk.co.demon.obelisk.scmp;

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

public class AsScmp extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new AsScmp ().run (args);
	}

	protected class DisplacementOpcode extends Opcode
	{
		public DisplacementOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			int		bits = 0;
			Expr	expr = ZERO;
			
			token	= nextRealToken ();
			
			if (token == AT) {
				bits |= 0x40;
				token = nextRealToken ();
			}
			
			if (token == LPAREN) {
				token = nextRealToken ();
				if (token == P0)
					bits |= 0x00;
				else if (token == P1)
					bits |= 0x01;
				else if (token == P2)
					bits |= 0x02;
				else if (token == P3)
					bits |= 0x03;
				else {
					error (ERR_POINTER_REGISTER);
					return (true);
				}
				token = nextRealToken ();
				if (token != RPAREN) {
					error (ERR_SYNTAX);
					return (true);
				}
				token = nextRealToken ();
			}
			else {
				expr = parseExpr ();
				if (token == LPAREN) {
					token = nextRealToken ();
					if (token == P0)
						bits |= 0x00;
					else if (token == P1)
						bits |= 0x01;
					else if (token == P2)
						bits |= 0x02;
					else if (token == P3)
						bits |= 0x03;
					else {
						error (ERR_POINTER_REGISTER);
						return (true);
					}
					token = nextRealToken ();
					if (token != RPAREN) {
						error (ERR_SYNTAX);
						return (true);
					}
					token = nextRealToken ();
				}
				else {
					expr = Expr.sub (expr, Expr.add (getOrigin (), TWO));
				}
			}
			
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			if (!isByte (expr))
				error (ERR_INVALID_DISPLACMENT);
			
			addByte (opcode | bits);
			addByte (expr);
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class MemoryOpcode extends Opcode
	{
		public MemoryOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			int		bits = 0;
			Expr	expr = ZERO;
			
			token	= nextRealToken ();
			
			if (token == LPAREN) {
				token = nextRealToken ();
				if (token == P0)
					bits |= 0x00;
				else if (token == P1)
					bits |= 0x01;
				else if (token == P2)
					bits |= 0x02;
				else if (token == P3)
					bits |= 0x03;
				else {
					error (ERR_POINTER_REGISTER);
					return (true);
				}
				token = nextRealToken ();
				if (token != RPAREN) {
					error (ERR_SYNTAX);
					return (true);
				}
				token = nextRealToken ();
			}
			else {
				expr = parseExpr ();
				if (token == LPAREN) {
					token = nextRealToken ();
					if (token == P0)
						bits |= 0x00;
					else if (token == P1)
						bits |= 0x01;
					else if (token == P2)
						bits |= 0x02;
					else if (token == P3)
						bits |= 0x03;
					else {
						error (ERR_POINTER_REGISTER);
						return (true);
					}
					token = nextRealToken ();
					if (token != RPAREN) {
						error (ERR_SYNTAX);
						return (true);
					}
					token = nextRealToken ();
				}
				else {
					expr = Expr.sub (expr, Expr.add (getOrigin (), TWO));
				}
			}
			
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
		
			if (!isByte (expr))
				error (ERR_INVALID_DISPLACMENT);
			
			addByte (opcode | bits);
			addByte (expr);
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class PointerOpcode extends Opcode
	{
		public PointerOpcode (TokenKind kind, String text, int opcode)
		{
			super (kind, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			int		bits = 0;
			
			token	= nextRealToken ();
			if (token == P0)
				bits |= 0x00;
			else if (token == P1)
				bits |= 0x01;
			else if (token == P2)
				bits |= 0x02;
			else if (token == P3)
				bits |= 0x03;
			else {
				error (ERR_POINTER_REGISTER);
				return (true);
			}
			
			token	= nextRealToken ();
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			addByte (opcode | bits);
			return (true);
		}
		
		private final int opcode;
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

	/**
	 * Constructs an <CODE>AsScmp</CODE> instance and initialises the object
	 * module.
	 */
	protected AsScmp ()
	{
		super (new Module ("SC/MP", false));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}
	
	/**
	 * A <CODE>Token</CODE> representing the @symbol.
	 */
	protected final Token 	AT
		= new Token (KEYWORD, "@");
	
	/**
	 * A <CODE>Token</CODE> representing the P0 register.
	 */
	protected final Token 	P0
		= new Token (KEYWORD, "P0");

	/**
	 * A <CODE>Token</CODE> representing the P1 register.
	 */
	protected final Token 	P1
		= new Token (KEYWORD, "P1");

	/**
	 * A <CODE>Token</CODE> representing the P2 register.
	 */
	protected final Token 	P2
		= new Token (KEYWORD, "P2");

	/**
	 * A <CODE>Token</CODE> representing the P3 register.
	 */
	protected final Token 	P3
		= new Token (KEYWORD, "P3");

	/**
	 * An <CODE>Opcode</CODE> that handles the ADD instruction.
	 */
	protected final Opcode 	ADD		= new DisplacementOpcode (KEYWORD, "ADD", 0xf0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ADE instruction.
	 */
	protected final Opcode 	ADE		= new ImpliedOpcode (KEYWORD, "ADE", 0x70);

	/**
	 * An <CODE>Opcode</CODE> that handles the ADI instruction.
	 */
	protected final Opcode 	ADI		= new ImmediateOpcode (KEYWORD, "ADI", 0xf4);

	/**
	 * An <CODE>Opcode</CODE> that handles the AND instruction.
	 */
	protected final Opcode 	AND		= new DisplacementOpcode (KEYWORD, "AND", 0xd0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ANE instruction.
	 */
	protected final Opcode 	ANE		= new ImpliedOpcode (KEYWORD, "ANE", 0x50);

	/**
	 * An <CODE>Opcode</CODE> that handles the ANI instruction.
	 */
	protected final Opcode 	ANI		= new ImmediateOpcode (KEYWORD, "ANI", 0xd4);

	/**
	 * An <CODE>Opcode</CODE> that handles the CAD instruction.
	 */
	protected final Opcode 	CAD		= new DisplacementOpcode (KEYWORD, "CAD", 0xf8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the CAE instruction.
	 */
	protected final Opcode 	CAE		= new ImpliedOpcode (KEYWORD, "CAE", 0x78);

	/**
	 * An <CODE>Opcode</CODE> that handles the CAI instruction.
	 */
	protected final Opcode 	CAI		= new ImmediateOpcode (KEYWORD, "CAI", 0xfc);

	/**
	 * An <CODE>Opcode</CODE> that handles the CAS instruction.
	 */
	protected final Opcode 	CAS		= new ImpliedOpcode (KEYWORD, "CAS", 0x07);

	/**
	 * An <CODE>Opcode</CODE> that handles the CCL instruction.
	 */
	protected final Opcode 	CCL		= new ImpliedOpcode (KEYWORD, "CCL", 0x02);

	/**
	 * An <CODE>Opcode</CODE> that handles the CAE instruction.
	 */
	protected final Opcode 	CSA		= new ImpliedOpcode (KEYWORD, "CSA", 0x06);

	/**
	 * An <CODE>Opcode</CODE> that handles the DAD instruction.
	 */
	protected final Opcode 	DAD		= new DisplacementOpcode (KEYWORD, "DAD", 0xe8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DAE instruction.
	 */
	protected final Opcode 	DAE		= new ImpliedOpcode (KEYWORD, "DAE", 0x68);

	/**
	 * An <CODE>Opcode</CODE> that handles the DAI instruction.
	 */
	protected final Opcode 	DAI		= new ImmediateOpcode (KEYWORD, "DAI", 0xec);

	/**
	 * An <CODE>Opcode</CODE> that handles the DINT instruction.
	 */
	protected final Opcode 	DINT	= new ImpliedOpcode (KEYWORD, "DINT", 0x04);

	/**
	 * An <CODE>Opcode</CODE> that handles the DLD instruction.
	 */
	protected final Opcode 	DLD		= new MemoryOpcode (KEYWORD, "DLD", 0xb8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the DLY instruction.
	 */
	protected final Opcode 	DLY		= new ImmediateOpcode (KEYWORD, "DLY", 0x8f);

	/**
	 * An <CODE>Opcode</CODE> that handles the HALT instruction.
	 */
	protected final Opcode 	HALT	= new ImpliedOpcode (KEYWORD, "HALT", 0x00);

	/**
	 * An <CODE>Opcode</CODE> that handles the IEN instruction.
	 */
	protected final Opcode 	IEN		= new ImpliedOpcode (KEYWORD, "IEN", 0x05);

	/**
	 * An <CODE>Opcode</CODE> that handles the ILD instruction.
	 */
	protected final Opcode 	ILD		= new MemoryOpcode (KEYWORD, "ILD", 0xa8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JMP instruction.
	 */
	protected final Opcode 	JMP		= new MemoryOpcode (KEYWORD, "JMP", 0x90);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JNZ instruction.
	 */
	protected final Opcode 	JNZ		= new MemoryOpcode (KEYWORD, "JNZ", 0x9c);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JP instruction.
	 */
	protected final Opcode 	JP		= new MemoryOpcode (KEYWORD, "JP", 0x94);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the JZ instruction.
	 */
	protected final Opcode 	JZ		= new MemoryOpcode (KEYWORD, "JZ", 0x98);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LD instruction.
	 */
	protected final Opcode 	LD		= new DisplacementOpcode (KEYWORD, "LD", 0xc0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the LDE instruction.
	 */
	protected final Opcode 	LDE		= new ImpliedOpcode (KEYWORD, "LDE", 0x40);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ACI instruction.
	 */
	protected final Opcode 	LDI		= new ImmediateOpcode (KEYWORD, "LDI", 0xc4);

	/**
	 * An <CODE>Opcode</CODE> that handles the NOP instruction.
	 */
	protected final Opcode 	NOP		= new ImpliedOpcode (KEYWORD, "NOP", 0x08);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the OR instruction.
	 */
	protected final Opcode 	OR		= new DisplacementOpcode (KEYWORD, "OR", 0xd8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ORE instruction.
	 */
	protected final Opcode 	ORE		= new ImpliedOpcode (KEYWORD, "ORE", 0x58);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ORI instruction.
	 */
	protected final Opcode 	ORI		= new ImmediateOpcode (KEYWORD, "ORI", 0xdc);

	/**
	 * An <CODE>Opcode</CODE> that handles the RR instruction.
	 */
	protected final Opcode 	RR		= new ImpliedOpcode (KEYWORD, "RR", 0x1e);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the RRL instruction.
	 */
	protected final Opcode 	RRL		= new ImpliedOpcode (KEYWORD, "RRL", 0x1f);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SCL instruction.
	 */
	protected final Opcode 	SCL		= new ImpliedOpcode (KEYWORD, "SCL", 0x03);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SIO instruction.
	 */
	protected final Opcode 	SIO		= new ImpliedOpcode (KEYWORD, "SIO", 0x19);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SR instruction.
	 */
	protected final Opcode 	SR		= new ImpliedOpcode (KEYWORD, "SR", 0x1c);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the SRL instruction.
	 */
	protected final Opcode 	SRL		= new ImpliedOpcode (KEYWORD, "SRL", 0x1d);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the ST instruction.
	 */
	protected final Opcode 	ST		= new DisplacementOpcode (KEYWORD, "ST", 0xc8);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XAE instruction.
	 */
	protected final Opcode 	XAE		= new ImpliedOpcode (KEYWORD, "XAE", 0x01);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XOR instruction.
	 */
	protected final Opcode 	XOR		= new DisplacementOpcode (KEYWORD, "XOR", 0xe0);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XPAH instruction.
	 */
	protected final Opcode 	XPAH	= new PointerOpcode (KEYWORD, "XPAH", 0x34);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XPAL instruction.
	 */
	protected final Opcode 	XPAL	= new PointerOpcode (KEYWORD, "XPAL", 0x30);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XPPC instruction.
	 */
	protected final Opcode 	XPPC	= new PointerOpcode (KEYWORD, "XPPC", 0x3c);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XRE instruction.
	 */
	protected final Opcode 	XRE		= new ImpliedOpcode (KEYWORD, "XRE", 0x60);
	
	/**
	 * An <CODE>Opcode</CODE> that handles the XRI instruction.
	 */
	protected final Opcode 	XRI		= new ImmediateOpcode (KEYWORD, "XRI", 0xe4);

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
		addToken (ADD);
		addToken (ADE);
		addToken (ADI);
		addToken (AND);
		addToken (ANE);
		addToken (ANI);
		addToken (CAD);
		addToken (CAE);
		addToken (CAI);
		addToken (CAS);
		addToken (CCL);
		addToken (CSA);
		addToken (DAD);
		addToken (DAE);
		addToken (DAI);
		addToken (DINT);
		addToken (DLD);
		addToken (DLY);
		addToken (HALT);
		addToken (IEN);
		addToken (ILD);
		addToken (JMP);
		addToken (JNZ);
		addToken (JP);
		addToken (JZ);
		addToken (LD);
		addToken (LDE);
		addToken (LDI);
		addToken (NOP);
		addToken (OR);
		addToken (ORE);
		addToken (ORI);
		addToken (RR);
		addToken (RRL);
		addToken (SCL);
		addToken (SIO);
		addToken (SR);
		addToken (SRL);
		addToken (ST);
		addToken (XAE);
		addToken (XOR);
		addToken (XPAH);
		addToken (XPAL);
		addToken (XPPC);
		addToken (XRE);
		addToken (XRI);

		// Registers
		addToken (P0);
		addToken (P1);
		addToken (P2);
		addToken (P3);
		
		addToken (LO);
		addToken (HI);

		super.startUp ();
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected boolean isSupportedPass (final Pass pass)
	{
		return (true); //pass != Pass.INTERMEDIATE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void startPass ()
	{
		super.startPass ();
		
		title = "Portable National Semiconductor SC/MP Assembler [14.11]";
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
	
	private static final String ERR_UNEXPECTED_TEXT
		= "Unexpected text after instruction";

	private static final String ERR_TEXT_TOO_LONG_FOR_IMMD
		= "Text literal is too long to be used in an immediate expression";
	
	private static final String ERR_MISSING_EXPRESSION
		= "Missing expression";
	
	private static final String ERR_POINTER_REGISTER
		= "Missing pointer register (P0,P1,P2 or P3)";
	
	private static final String ERR_INVALID_DISPLACMENT
		= "Invalid displacement value";

	/**
	 * A constant value used in calculations.
	 */
	private static final Value	ZERO		= new Value (null, 0);
	
	/**
	 * A constant value used in calculations.
	 */
	private static final Value	TWO			= new Value (null, 2);
	
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
		case '@':	return (AT);
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
	
		case '$':	return (ORIGIN);
		case '.':
		{
			if (isAlphanumeric (peekChar ()))
				break;
			return (ORIGIN);
		}

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
			
		case 'B': case 'b':
			{
				if (peekChar () == '\'') {
					nextChar ();
					while (isBinary (peekChar ()))
						buffer.append (nextChar ());
					
					String text = buffer.toString ();
					if (text.length () == 0) {
						error ("Invalid binary constant");
						return (new Token (UNKNOWN, text));
					}
					
					for (int index = 0; index < text.length (); ++index) {
						value = value * 2 + (text.charAt (index) - '0');
					}
					return (new Token (NUMBER, text, new Integer (value)));				
				}
				break;
			}
		
		case 'O': case 'o':
			{
				if (peekChar () == '\'') {
					nextChar ();
					while (isOctal (peekChar ()))
						buffer.append (nextChar ());
					
					String text = buffer.toString ();
					if (text.length () == 0) {
						error ("Invalid octal constant");
						return (new Token (UNKNOWN, text));
					}
					
					for (int index = 0; index < text.length (); ++index) {
						value = value * 8 + (text.charAt (index) - '0');
					}
					return (new Token (NUMBER, text, new Integer (value)));				
				}
				break;
			}
		
		case 'D': case 'd':
			{
				if (peekChar () == '\'') {
					nextChar ();
					while (isDecimal (peekChar ()))
						buffer.append (nextChar ());
					
					String text = buffer.toString ();
					if (text.length () == 0) {
						error ("Invalid decimal constant");
						return (new Token (UNKNOWN, text));
					}
					
					for (int index = 0; index < text.length (); ++index) {
						value = value * 10 + (text.charAt (index) - '0');
					}
					return (new Token (NUMBER, text, new Integer (value)));				
				}
				break;
			}
	
		case 'X': case 'x':
			{
				if (peekChar () == '\'') {
					nextChar ();
					while (isHexadecimal (peekChar ()))
						buffer.append (nextChar ());
					
					if (buffer.length () == 0) {
						error ("Invalid hexadecimal constant");
						return (new Token (UNKNOWN, buffer.toString ()));
					}
					
					String text = buffer.toString ();
					for (int index = 0; index < text.length (); ++index) {
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
				break;
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
			if (text.matches ("[0-9]+")) {
				for (int index = 0; index < text.length (); ++index) {
					value = value * 10 + (text.charAt (index) - '0');
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
	
	private boolean isByte (Expr expr)
	{
		if (expr.isAbsolute ()) {
			if (getPass() != Pass.FIRST) {
				long value = expr.resolve (null, null) & 0xffffffffffffff80L;
			
				return ((value == 0x0000000000000000L) || (value == 0xffffffffffffff80L));
			}
		}
		return (true);
	}
}