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

package uk.co.demon.obelisk.i8008;

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

public final class As8008 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As8008 ().run (args);
	}
	
	/**
	 * Constructs an <CODE>As8008</CODE> instance and initialises the object
	 * module.
	 */
	protected As8008 ()
	{
		super (new Module ("8008", false));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}

	protected class ImpliedOpcode extends Opcode
	{
		public ImpliedOpcode (String text, int opcode)
		{
			super (KEYWORD, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			
			addByte (opcode);

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class ImmediateOpcode extends Opcode
	{
		public ImmediateOpcode (String text, int opcode)
		{
			super (KEYWORD, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseExpr ();
			
			if (expr != null) {
				addByte (Expr.or(new Value (null, opcode), Expr.and(expr, new Value (null, 0x0f))));
			}
			else
				error (ERR_MISSING_EXPRESSION);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class JumpOpcode extends Opcode
	{
		public JumpOpcode (String text, int opcode)
		{
			super (KEYWORD, text);
			
			this.opcode = opcode;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseExpr ();
			
			if (expr != null) {
				Expr	msb = Expr.and (Expr.shr(expr, new Value (null, 8)), new Value (null, 0xff));
				
				addByte (Expr.or(new Value (null, opcode), msb));
				addByte (expr);
			}
			else
				error (ERR_MISSING_EXPRESSION);
			
			return (true);
		}
		
		private final int opcode;
	}

	protected final Opcode	ADI	= new ImmediateOpcode ("ADI", 0x04);
	
	protected final Opcode 	JMP	= new JumpOpcode ("JMP", 0x44);
	
	protected final Opcode	RAL	= new ImpliedOpcode ("RAL", 0x12);

	protected final Opcode	RAR	= new ImpliedOpcode ("RAR", 0x1a);
	
	protected final Opcode	RET	= new ImpliedOpcode ("RET", 0x07);
	
	protected final Opcode	RLC	= new ImpliedOpcode ("RLC", 0x02);
	
	protected final Opcode	RRC	= new ImpliedOpcode ("RRC", 0x0a);
	
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
//		addToken (ADD);
		addToken (ADI);
//		addToken (ADM);
//		addToken (BBL);
//		addToken (CLB);
//		addToken (CLC);
//		addToken (CMA);
//		addToken (CMC);
//		addToken (DAA);
//		addToken (DAC);
//		addToken (DCL);
//		addToken (FIM);
//		addToken (FIN);
//		addToken (IAC);
//		addToken (INC);
//		addToken (ISZ);
		addToken (JMP);
//		addToken (JIN);
//		addToken (JMS);
//		addToken (JUN);
//		addToken (KBP);
//		addToken (LD);
//		addToken (LDM);
//		addToken (NOP);
		addToken (RAL);
		addToken (RAR);
		addToken (RLC);
		addToken (RRC);
//		addToken (RD0);
//		addToken (RD1);
//		addToken (RD2);
//		addToken (RD3);
//		addToken (RDM);
//		addToken (RDR);
//		addToken (SBM);
//		addToken (SRC);
//		addToken (STC);
//		addToken (SUB);
//		addToken (TCC);
//		addToken (TCS);
//		addToken (WMP);
//		addToken (WPM);
//		addToken (WRM);
//		addToken (WRR);
//		addToken (WR0);
//		addToken (WR1);
//		addToken (WR2);
//		addToken (WR3);
//		addToken (XCH);
		
//		symbols.put ("NC", new Value (null, 0x0));
//		symbols.put ("TZ", new Value (null, 0x1));
//		symbols.put ("T0", new Value (null, 0x1));
//		symbols.put ("TN", new Value (null, 0x9));
//		symbols.put ("T1", new Value (null, 0x9));
//		symbols.put ("CN", new Value (null, 0x2));
//		symbols.put ("C1", new Value (null, 0x2));
//		symbols.put ("CZ", new Value (null, 0xa));
//		symbols.put ("C0", new Value (null, 0xa));
//		symbols.put ("AZ", new Value (null, 0x4));
//		symbols.put ("A0", new Value (null, 0x4));
//		symbols.put ("AN", new Value (null, 0xc));
//		symbols.put ("A1", new Value (null, 0xc));
		
		super.startUp ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSupportedPass (final Pass pass)
	{
		return (pass != Pass.INTERMEDIATE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startPass ()
	{
		super.startPass ();
		
		title = "Portable Intel 8008 Assembler [17.10]";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Token readToken ()
	{
		return (scanToken ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	
	private static final String ERR_UNEXPECTED_TEXT
		= "Unexpected text after instruction";

	private static final String ERR_MISSING_EXPRESSION
		= "Missing expression";
	
	private static final String ERR_EXPECTED_REGISTER
		= "Expected register number (0-15)";
	
	private static final String ERR_EXPECTED_CONDITION
		= "Expected condition code";

	private static final String ERR_EXPECTED_REGISTER_PAIR
		= "Expected register pair number (0-7)";
	
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
}
