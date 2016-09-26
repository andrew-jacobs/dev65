/*
 * Copyright (C),2014-2016 Andrew John Jacobs.
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

package uk.co.demon.obelisk.cdp1802;

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
 * The <CODE>As1802</CODE> provides the base <CODE>Assembler</CODE> with an
 * understanding of RCA CDP 1802 assembler conventions.
 *
 * @author 	Andrew Jacobs
 */
public final class As1802 extends Assembler
{
	/**
	 * Processes the command line and executes the assembler.
	 *
	 * @param args			The command line argument strings
	 */
	public static void main(String[] args)
	{
		new As1802 ().run (args);
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
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			addByte (opcode);
			return (true);
		}
		
		private final int opcode;
	}

	protected class RegisterOpcode extends Opcode
	{
		public RegisterOpcode (String text, int opcode, boolean notZero)
		{
			super (KEYWORD, text);
			
			this.opcode = opcode;
			this.notZero = notZero;
		}
		
		/**
		 * {@inheritDoc}
		 */
		public boolean compile ()
		{
			token	= nextRealToken ();
			Expr expr = parseExpr ();
			long reg;
			
			if ((expr != null) && expr.isAbsolute () && ((reg = expr.resolve ()) >= 0) && (reg <= 15)) {
				if (notZero && (reg == 0))
					error (ERR_REGISTER_ZERO);
				
				addByte (opcode | (int) reg);
				token	= nextRealToken ();
			}
			else
				error (ERR_EXPECTED_REGISTER);
			
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
		
		private final boolean notZero;
		
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
	
	protected class ShortBranchOpcode extends Opcode
	{
		public ShortBranchOpcode (String text, int opcode)
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
			Expr expr = parseImmd ();
			Expr origin = getOrigin ();
			
			if (expr != null) {
				addByte (opcode);
				addByte (expr);
				
				if (getPass () == Pass.FINAL) {
					expr = Expr.and (Expr.xor (origin, expr), HIGH_BYTES);
					if (expr.isAbsolute ()) {
						if (expr.resolve () != 0)
							error ("Invalid short branch. Target address on different page");
					}
					else {
						// Assert branch to same page in object code.
					}
				}
			}

			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	protected class LongBranchOpcode extends Opcode
	{
		public LongBranchOpcode (String text, int opcode)
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

	protected class IOOpcode extends Opcode
	{
		public IOOpcode (String text, int opcode)
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
			long reg;
			
			if ((expr != null) && expr.isAbsolute () && ((reg = expr.resolve ()) >= 0) && (reg <= 7)) {
				addByte (opcode | (int) reg);
				token	= nextRealToken ();
			}
			else
				error ("Expected a port value 0-7");
			
			if (token != EOL)
				error (ERR_UNEXPECTED_TEXT);
			
			return (true);
		}
		
		private final int opcode;
	}
	
	/**
	 * Constructs an <CODE>As1802</CODE> instance and initialises the object
	 * module.
	 */
	protected As1802 ()
	{
		super (new Module ("1802", true));
		
		setMemoryModel (new MemoryModelByte (errorHandler));
	}
	
	protected final Opcode	ADD  = new ImpliedOpcode ("ADD", 0xf4);

	protected final Opcode	ADI  = new ImmediateOpcode ("ADI", 0xfc);
	
	protected final Opcode	ADC  = new ImpliedOpcode ("ADC", 0xf4);

	protected final Opcode	ADCI = new ImmediateOpcode ("ADCI", 0x7c);
	
	protected final Opcode	AND  = new ImpliedOpcode ("AND", 0xf2);

	protected final Opcode	ANI  = new ImmediateOpcode ("ANI", 0xfa);
	
	protected final Opcode	B1   = new ShortBranchOpcode ("B1", 0x34);
	
	protected final Opcode	B2   = new ShortBranchOpcode ("B2", 0x35);
	
	protected final Opcode	B3   = new ShortBranchOpcode ("B3", 0x36);
	
	protected final Opcode	B4   = new ShortBranchOpcode ("B4", 0x37);
	
	protected final Opcode	BDF  = new ShortBranchOpcode ("BDF", 0x33);
	
	protected final Opcode	BGE  = new ShortBranchOpcode ("BGE", 0x33);
	
	protected final Opcode	BL   = new ShortBranchOpcode ("BL", 0x3b);
	
	protected final Opcode	BM   = new ShortBranchOpcode ("BM", 0x3b);
	
	protected final Opcode	BN1  = new ShortBranchOpcode ("BN1", 0x3c);
	
	protected final Opcode	BN2  = new ShortBranchOpcode ("BN2", 0x3d);
	
	protected final Opcode	BN3  = new ShortBranchOpcode ("BN3", 0x3e);
	
	protected final Opcode	BN4  = new ShortBranchOpcode ("BN4", 0x3f);
	
	protected final Opcode	BNF  = new ShortBranchOpcode ("BNF", 0x3b);
	
	protected final Opcode	BNQ  = new ShortBranchOpcode ("BNQ", 0x39);
	
	protected final Opcode	BNZ  = new ShortBranchOpcode ("BNZ", 0x3a);
	
	protected final Opcode	BPZ  = new ShortBranchOpcode ("BPZ", 0x33);
	
	protected final Opcode	BQ   = new ShortBranchOpcode ("BQ", 0x39);
	
	protected final Opcode	BR   = new ShortBranchOpcode ("BR", 0x30);
	
	protected final Opcode	BZ   = new ShortBranchOpcode ("BZ", 0x32);
	
	protected final Opcode	DIS  = new ImpliedOpcode ("DIS", 0x71);
	
	protected final Opcode 	DEC  = new RegisterOpcode ("DEC", 0x20, false);
	
	protected final Opcode	IDL  = new ImpliedOpcode ("IDL", 0x00);
	
	protected final Opcode	INC  = new RegisterOpcode ("INC", 0x10, false);
	
	protected final Opcode 	INP  = new IOOpcode ("INP", 0x68);
	
	protected final Opcode	IRX  = new ImpliedOpcode ("IRX", 0x60);
	
	protected final Opcode	GHI  = new RegisterOpcode ("GHI", 0x90, false);
	
	protected final Opcode	GLO  = new RegisterOpcode ("GLO", 0x80, false);
	
	protected final Opcode	LBDF = new LongBranchOpcode ("LBDF", 0xc3);

	protected final Opcode	LBR	 = new LongBranchOpcode ("LBR", 0xc0);

	protected final Opcode	LBZ	 = new LongBranchOpcode ("LBZ", 0xc2);

	protected final Opcode	LBNF = new LongBranchOpcode ("LBNF", 0xcb);

	protected final Opcode	LBNQ = new LongBranchOpcode ("LBNQ", 0xc9);

	protected final Opcode	LBNZ = new LongBranchOpcode ("LBNZ", 0xca);

	protected final Opcode	LBQ	 = new LongBranchOpcode ("LBQ", 0xc1);

	protected final Opcode	LDA  = new RegisterOpcode ("LDA", 0x40, false);
	
	protected final Opcode	LDI  = new ImmediateOpcode ("LDI", 0xf8);

	protected final Opcode	LDN  = new RegisterOpcode ("LDN", 0x00, true);
	
	protected final Opcode	LDX  = new ImpliedOpcode ("LDX", 0xf0);

	protected final Opcode	LDXA = new ImpliedOpcode ("LDXA", 0x72);
	
	protected final Opcode	LSIE = new ImpliedOpcode ("LSIE", 0xcc);

	protected final Opcode	LSDF = new ImpliedOpcode ("LSDF", 0xcf);

	protected final Opcode	LSKP = new ImpliedOpcode ("LSKP", 0xc8);

	protected final Opcode	LSNF = new ImpliedOpcode ("LSNF", 0xc7);

	protected final Opcode	LSQ = new ImpliedOpcode ("LSQ", 0xcd);

	protected final Opcode	LSNQ = new ImpliedOpcode ("LSNQ", 0xc5);

	protected final Opcode	LSNZ = new ImpliedOpcode ("LSNZ", 0xc6);

	protected final Opcode	LSZ = new ImpliedOpcode ("LSZ", 0xce);

	protected final Opcode	MARK = new ImpliedOpcode ("MARK", 0x79);
	
	protected final Opcode	NBR  = new ShortBranchOpcode ("NBR", 0x38);
	
	protected final Opcode	NLBR = new LongBranchOpcode ("NLBR", 0xc8);

	protected final Opcode	NOP  = new ImpliedOpcode ("NOP", 0xc4);
	
	protected final Opcode	OR   = new ImpliedOpcode ("OR", 0xf1);

	protected final Opcode	ORI  = new ImmediateOpcode ("ORI", 0xf9);

	protected final Opcode 	OUT  = new IOOpcode ("OUT", 0x60);
	
	protected final Opcode	PHI  = new RegisterOpcode ("PHI", 0xb0, false);
	
	protected final Opcode	PLO  = new RegisterOpcode ("PLO", 0xa0, false);

	protected final Opcode	REQ  = new ImpliedOpcode ("REQ", 0x70);
	
	protected final Opcode	RET  = new ImpliedOpcode ("RET", 0x60);
	
	protected final Opcode	RSHL = new ImpliedOpcode ("RSHL", 0x7e);
	
	protected final Opcode	RSHR = new ImpliedOpcode ("RSHR", 0x76);
	
	protected final Opcode	SAV  = new ImpliedOpcode ("SAV", 0x78);
	
	protected final Opcode	SD   = new ImpliedOpcode ("SD", 0xf5);

	protected final Opcode	SDB  = new ImpliedOpcode ("SDB", 0x75);

	protected final Opcode	SDBI = new ImmediateOpcode ("SDBI", 0x7d);
	
	protected final Opcode	SDI  = new ImmediateOpcode ("SDI", 0xfd);
	
	protected final Opcode	SEP  = new RegisterOpcode ("SEP", 0xd0, false);

	protected final Opcode	SEQ  = new ImpliedOpcode ("SEQ", 0x7b);
	
	protected final Opcode	SEX  = new RegisterOpcode ("SEX", 0xe0, false);

	protected final Opcode	SHL  = new ImpliedOpcode ("SHL", 0xfe);

	protected final Opcode	SHLC = new ImpliedOpcode ("SHLC", 0x7e);

	protected final Opcode	SHR  = new ImpliedOpcode ("SHR", 0xf6);

	protected final Opcode	SHRC = new ImpliedOpcode ("SHRC", 0x76);

	protected final Opcode	SKP  = new ImpliedOpcode ("SKP", 0x38);

	protected final Opcode	SM   = new ImpliedOpcode ("SM", 0xf7);

	protected final Opcode	SMB  = new ImpliedOpcode ("SMB", 0x77);
	
	protected final Opcode	SMBI = new ImmediateOpcode ("SMBI", 0x7f);
	
	protected final Opcode	SMI  = new ImmediateOpcode ("SMI", 0xff);
	
	protected final Opcode 	STR  = new RegisterOpcode ("STR", 0x50, false);

	protected final Opcode	STXD = new ImpliedOpcode ("STXD", 0x73);

	protected final Opcode	XOR  = new ImpliedOpcode ("XOR", 0xf3);

	protected final Opcode	XRI  = new ImmediateOpcode ("XRI", 0xfb);

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
		addToken (ADC);
		addToken (ADCI);
		addToken (ADD);
		addToken (ADI);
		addToken (AND);
		addToken (ANI);
		addToken (B1);
		addToken (B2);
		addToken (B3);
		addToken (B4);
		addToken (BDF);
		addToken (BGE);
		addToken (BL);
		addToken (BM);
		addToken (BN1);
		addToken (BN2);
		addToken (BN3);
		addToken (BN4);
		addToken (BNF);
		addToken (BNQ);
		addToken (BNZ);
		addToken (BPZ);
		addToken (BQ);
		addToken (BR);
		addToken (BZ);
		addToken (DEC);
		addToken (DIS);
		addToken (IDL);
		addToken (INC);
		addToken (INP);
		addToken (IRX);
		addToken (GHI);
		addToken (GLO);
		addToken (LBDF);
		addToken (LBR);
		addToken (LBNF);
		addToken (LBNQ);
		addToken (LBNZ);
		addToken (LBQ);
		addToken (LBZ);
		addToken (LDA);
		addToken (LDI);
		addToken (LDN);
		addToken (LDX);
		addToken (LDXA);
		addToken (LSIE);
		addToken (LSDF);
		addToken (LSKP);
		addToken (LSNF);
		addToken (LSNQ);
		addToken (LSNZ);
		addToken (LSQ);
		addToken (LSZ);
		addToken (MARK);
		addToken (NBR);
		addToken (NLBR);
		addToken (NOP);
		addToken (OR);
		addToken (ORI);
		addToken (OUT);
		addToken (PHI);
		addToken (PLO);
		addToken (REQ);
		addToken (RET);
		addToken (RSHL);
		addToken (RSHR);
		addToken (SAV);
		addToken (SD);
		addToken (SDB);
		addToken (SDBI);
		addToken (SDI);
		addToken (SEP);
		addToken (SEQ);
		addToken (SEX);
		addToken (SM);
		addToken (SMB);
		addToken (SMBI);
		addToken (SMI);
		addToken (SHL);
		addToken (SHR);
		addToken (SHLC);
		addToken (SHRC);
		addToken (STR);
		addToken (STXD);
		addToken (XOR);
		addToken (XRI);
		
		// Functions
		addToken (HI);
		addToken (LO);
		addToken (STRLEN);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSupportedPass (Pass pass)
	{
		return (pass != Pass.INTERMEDIATE);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void startPass ()
	{
		super.startPass ();
		
		title = "Portable RCA CDP 1802 Assembler [16.09]";
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Token readToken ()
	{
		return (scanToken ());
	}
	
	private static final Value HIGH_BYTES = new Value (null, 0xffffff00);
		
	private static final String ERR_REGISTER_ZERO
		= "This opcode cannot be used with register zero";

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

	private static final String ERR_EXPECTED_REGISTER
		= "A register name was expected after the opcode";

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
				
				if (text.length () > 2)
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

}