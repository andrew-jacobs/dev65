/*
 * Copyright (C),2005 Andrew John Jacobs.
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
 * A <CODE>Token</CODE> instance represents a significant series of characters
 * extracted from the source code.
 * 
 * @author Andrew Jacobs
 * @version	$Id$
 */
public class Token
{	
	/**
	 * Constructs a <CODE>Token</CODE> instance that has an associated value.
	 * 
	 * @param kind			Identifies the type of <CODE>Token</CODE>.
	 * @param text			The text string this was parsed from.
	 * @param value			An associated value <CODE>Object</CODE>.
	 */
	public Token (final TokenKind kind, final String text, final Object value)
	{
		this.kind  = kind;
		this.text  = text;
		this.value = value;
	}
	
	/**
	 * Constructs a <CODE>Token</CODE> instance that has no associated
	 * value.
	 * 
	 * @param kind			Identifies the type of <CODE>Token</CODE>.
	 * @param text			The text string this was parsed from.
	 */
	public Token (final TokenKind kind, final String text)
	{
		this (kind, text, null);
	}
	
	/**
	 * Returns the 'kind' classifier associated with the <CODE>Token</CODE>.
	 * 
	 * @return The <CODE>TokenKind</CODE> of this <CODE>Token</CODE>.
	 */
	public TokenKind getKind ()
	{
		return (kind);
	}
	
	/**
	 * Returns the text (or pseudo-text) associated with the <CODE>Token
	 * </CODE>.
	 * 
	 * @return The text string value associated with this <CODE>Token</CODE>.
	 */
	public String getText ()
	{
		return (text);
	}
	
	/**
	 * Returns the value object associated with the <CODE>Token</CODE>.
	 * 
	 * @return The value object.
	 */
	public Object getValue ()
	{
		return (value);
	}
	
	/**
	 * Converts the state of the <CODE>Token</CODE> into a displayable
	 * string format.
	 * 
	 * @return The <CODE>Token</CODE> state expressed as a string.
	 */
	public final String toString ()
	{
		return (getClass ().getName () + " {" + toDebug () + "}");
	}
	
	/**
	 * Converts the state of the instance to printable string.
	 * 
	 * @return	The instance state as a debugging string.
	 */
	protected String toDebug ()
	{
		return ("kind=" + kind + " text=" + text + " value=" + ((value == null) ? "null" : value));
	}
	
	/**
	 * The classifying <CODE>TokenKind</CODE> instance.
	 */
	private final TokenKind		kind;
	
	/**
	 * The text string from which this <CODE>Token</CODE> characterises.
	 */
	private final String		text;
	
	/**
	 * An optional underlying value <CODE>Object</CODE>.
	 */
	private final Object		value;
}