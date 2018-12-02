/*
 * Copyright (C),2018 Andrew John Jacobs.
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

package uk.co.demon.obelisk.kenbak;

import java.util.Hashtable;

import uk.co.demon.obelisk.xasm.Assembler;
import uk.co.demon.obelisk.xasm.MemoryModelByte;
import uk.co.demon.obelisk.xasm.Opcode;
import uk.co.demon.obelisk.xasm.Pass;
import uk.co.demon.obelisk.xasm.Token;
import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Oct;
import uk.co.demon.obelisk.xobj.Value;

public final class AsKb1  extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new AsKb1 ().run (args);
	}
	
	protected class SimpleOpcode extends Opcode
	{
		public SimpleOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = opcode;
		}
		
		@Override
		public boolean compile() {
			token = nextRealToken ();
			
			addByte (opcode);
			
			return (true);
		}
		
		private int			opcode;
	}
	
	protected class ArithOpcode extends Opcode
	{
		public ArithOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = opcode;
		}
		
		@Override
		public boolean compile()
		{
			Token 		reg;
			int			mode;
			
			token = nextRealToken ();
			
			if ((token == A) || (token == B) || (token == X)) {
				reg = token;
				
				token = nextRealToken ();
				if ((mode = parseMode ()) > 0) {
					if (reg == A) addByte (opcode | 0000 | mode);
					if (reg == B) addByte (opcode | 0100 | mode);
					if (reg == X) addByte (opcode | 0200 | mode);
					
					addByte (arg);
				}
				else
					error ("Missing operand");
			}
			else
				error ("Expected register A,B or X");
			
			return (true);
		}
		
		private int			opcode;
		
	}
	
	protected class LogicOpcode extends Opcode
	{
		public LogicOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = opcode;
		}
		
		@Override
		public boolean compile() {
			int			mode;
			
			token = nextRealToken ();
			if ((mode = parseMode ()) > 0) {
				addByte (opcode | mode);
				addByte (arg);
			}
			else
				error ("Missing operand");
			
			return (true);
		}
		
		private int			opcode;
		
	}
	
	protected class ShiftOpcode extends Opcode
	{
		public ShiftOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = opcode;
		}
		
		@Override
		public boolean compile() {
			Token		reg;
			
			token = nextRealToken ();
			if ((token == A) || (token == B)) {
				reg = token;
				token = nextRealToken ();
				if (((arg = parseExpr ()) != null) && arg.isAbsolute()) {
					switch ((int) arg.resolve ()) {
					case 1:	addByte (opcode | 0010); break;
					case 2:	addByte (opcode | 0020); break;
					case 3:	addByte (opcode | 0030); break;
					case 4:	addByte (opcode | 0000); break;
					
					default:
						error ("Invalid number of places");
					}
				}
				else
					error ("Expected number of places");
			}
			else
				error ("Expected register A or B");
			
			return (true);
		}
		
		private int			opcode;
		
	}
	
	protected class BitOpcode extends Opcode
	{
		public BitOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = new Value (null, opcode);
		}
		
		@Override
		public boolean compile() {
			Expr		bit;
			
			token = nextRealToken ();
			if ((bit = parseExpr ()) != null) {
				if ((arg = parseExpr ()) != null) {
					addByte (Expr.or (opcode, Expr.shl (Expr.and (bit, SEVEN), THREE)));
					addByte (arg);
				}
				else
					error ("Expected memory address");
			}
			else
				error ("Expected bit number");
			
			return (true);
		}
		
		private final Value	SEVEN	= new Value (null, 7);
		
		private final Value THREE	= new Value (null, 3);
		
		private Value		opcode;
	}

	protected class JumpOpcode extends Opcode
	{
		public JumpOpcode (String name, int opcode)
		{
			super (KEYWORD, name);
			
			this.opcode = opcode;
		}
		
		@Override
		public boolean compile() {
			int			reg  = 0300;
			int			cond = 0000;
			
			token = nextRealToken ();
			
			if ((token == A) || (token == B) || (token == X)) {
				if (token == A) reg = 0000;
				if (token == B) reg = 0100;
				if (token == X) reg = 0200;
				
				token = nextRealToken ();
				
				if (token == NE)
					cond = 0003;
				else if (token == EQ)
					cond = 0004;
				else if (token == LT)
					cond = 0005;
				else if (token == GT)
					cond = 0006;
				else if (token == GE)
					cond = 0007;
				else {
					error ("Expected <>, =, <, > or >=");
					return (true);
				}
				
				token = nextRealToken ();
				if ((token.getKind() == NUMBER) && (((Integer) token.getValue ()).intValue() == 0))
					token = nextRealToken ();
				else
					error ("Expected comparison against 0");
			}
			
			if ((arg = parseExpr ()) != null) {
				addByte (opcode | reg | cond);
				addByte (arg);
			}
			else
				error ("Expected target address");
			
			return (true);
		}
		
		private int			opcode;
	}
	
	protected static final int		IMM		= 0003;

	protected static final int		DIR		= 0004;
	
	protected static final int		IND		= 0005;
	
	protected static final int		IDX		= 0006;
	
	protected static final int		INX		= 0007;

	protected final Opcode	ADD		= new ArithOpcode ("ADD", 0000);
	
	protected final Opcode	SUB		= new ArithOpcode ("SUB", 0010);

	protected final Opcode	LOAD	= new ArithOpcode ("LOAD", 0020);

	protected final Opcode	STORE	= new ArithOpcode ("STORE", 0030);
	
	protected final Opcode	AND		= new LogicOpcode ("AND", 0320);
	
	protected final Opcode	OR		= new LogicOpcode ("OR", 0300);
	
	protected final Opcode	LNEG	= new LogicOpcode ("LNEG", 0330);
	
	protected final Opcode	JPD		= new JumpOpcode ("JPD", 0040);

	protected final Opcode	JPI		= new JumpOpcode ("JPI", 0050);
	
	protected final Opcode	JMD		= new JumpOpcode ("JMD", 0060);
	
	protected final Opcode	JMI		= new JumpOpcode ("JMI", 0070);
	
	protected final Opcode	SFTL	= new ShiftOpcode ("SFTL", 0201);
	
	protected final Opcode	SFTR	= new ShiftOpcode ("SFTR", 0001);
	
	protected final Opcode	ROTL	= new ShiftOpcode ("ROTL", 0101);
	
	protected final Opcode	ROTR	= new ShiftOpcode ("ROTR", 0301);
	
	protected final Opcode	HALT	= new SimpleOpcode ("HALT", 0000);
	
	protected final Opcode 	NOOP	= new SimpleOpcode ("NOOP", 0200);
	
	protected final Opcode	SET0	= new BitOpcode ("SET0", 0002);
	
	protected final Opcode	SET1	= new BitOpcode ("SET1", 0102);
	
	protected final Opcode	SKP0	= new BitOpcode ("SKP0", 0202);
	
	protected final Opcode	SKP1	= new BitOpcode ("SKP1", 0302);
	
	protected final Token	A		= new Token (KEYWORD, "A");
	
	protected final Token	B		= new Token (KEYWORD, "B");
	
	protected final Token	X		= new Token (KEYWORD, "X");
	
	protected Expr		arg;
	
	/**
	 * Constructs an <CODE>AsKenbak</CODE> instance and initialises the object
	 * module.
	 */
	protected AsKb1 ()
	{
		super (new Module ("Kenbak", false));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}

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
		addToken (AND);
		addToken (HALT);
		addToken (JMD);
		addToken (JMI);
		addToken (JPD);
		addToken (JPI);
		addToken (LNEG);
		addToken (LOAD);
		addToken (NOOP);
		addToken (OR);
		addToken (ROTL);
		addToken (ROTR);
		addToken (SET0);
		addToken (SET1);
		addToken (SFTL);
		addToken (SFTR);
		addToken (SKP0);
		addToken (SKP1);
		addToken (STORE);
		addToken (SUB);
		
		addToken (A);
		addToken (B);
		addToken (X);
		
		super.startUp ();
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
	 * A <CODE>StringBuffer</CODE> used to build up new tokens.
	 */
	private StringBuffer			buffer	= new StringBuffer ();
	
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
		
		title = "Portable KENBAK-1 Assembler [18.12]";
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
			output.append (Oct.toOct (addr.resolve (null, null), 8));
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
				output.append (Oct.toOct (addr.resolve (null, null), 3));
				output.append (addr.isAbsolute() ? "  " : "' ");
	
				for (int index = 0; index < 4; ++index) {
					if (index < byteCount) {
						int code = memory.getByte (index);
						
						if (code >= 0)
							output.append (Oct.toOct (code, 3) + " ");
						else
							output.append ("??");
					}
					else
						output.append ("    ");
				}
				output.append ((byteCount > 8) ? "> " : "  ");
				output.append ("   ");
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

	/**
	 * Adds a token to the hash table indexed by its text in UPPER case.
	 * 
	 * @param token			The <CODE>Token</CODE> to add.
	 */
	private void addToken (final Token token)
	{
		tokens.put (token.getText ().toUpperCase (), token);
	}

	private static final String	ERR_CHAR_TERM
		= "Unterminated character constant";

	private static final String	ERR_STRING_TERM
		= "Unterminated string constant";
	
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
				case '>':	nextChar (); return (NE);
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

	protected int parseMode ()
	{
		// =<expr>
		if (token == EQ) {
			token = nextRealToken ();
			arg = parseExpr ();
			return (IMM);
		}
		
		// (expr) or (expr),X
		if (token == LPAREN) {
			token = nextRealToken ();
			arg = parseExpr ();
			
			if (token == RPAREN) {
				token = nextRealToken ();
				if (token == COMMA) {
					token = nextRealToken ();
					if (token == X) {
						token = nextRealToken ();
						return (INX);
					}
					else {
						error ("Expected X register");
						return (-1);
					}
				}
				else
					return (IND);
			}
			else {
				error ("Expected closing parenthesis");
				return (-1);
			}
		}

		// expr or expr,X
		arg = parseExpr ();
		if (token == COMMA) {
			token = nextRealToken ();
			if (token == X) {
				token = nextRealToken ();
				return (IDX);
			}
			else {
				error ("Expected X register");
				return (-1);
			}
		}
		else
			return (DIR);
	}
}