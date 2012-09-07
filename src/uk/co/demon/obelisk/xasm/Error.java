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

package uk.co.demon.obelisk.xasm;

/**
 * The <CODE>Error</CODE> class contains all the error message strings used by
 * the <CODE>Assembler</CODE>.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Error
{
	public static final String	WRN_LABEL_IGNORED
	 	= "This statement cannot be labelled";

	public static final String	ERR_UNKNOWN_OPCODE
		= "Unknown opcode or directive";
	
	public static final String	ERR_NO_SECTION
		= "No active section";
	
	public static final String	ERR_CONSTANT_EXPR
		= "Constant expression required";
	
	public static final String	ERR_NO_OPEN_IF
		= ".ELSE or .ENDIF with no matching .IF";
	
	public static final String	ERR_CLOSING_PAREN
		= "Closing parenthesis missing in expression";
	
	public static final String	ERR_NO_GLOBAL
		= "A local label must be preceded by normal label";
	
	public static final String	ERR_UNDEF_SYMBOL
		= "Undefined symbol: ";
	
	public static final String	ERR_LABEL_REDEFINED
		= "Label as already been defined: ";
	
	public static final String	ERR_FAILED_TO_FIND_FILE
		= "Failed to find specified file";
	
	public static final String	ERR_EXPECTED_QUOTED_FILENAME
		= "Expected quoted filename";
	
	public static final String	ERR_INSERT_IO_ERROR
		= "I/O error while inserting binary data";
	
	public static final String	ERR_INVALID_EXPRESSION
		= "Invalid expression";
	
	public static final String	WRN_LABEL_IS_A_RESERVED_WORD
		= "This label is a reserved word";
	
	public static final String	ERR_EXPECTED_QUOTED_MESSAGE
		= "Expected quoted message string";

	private Error ()
	{ }
}