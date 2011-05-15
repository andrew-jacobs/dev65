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
 * The abstract <CODE>Opcode</CODE> class provides a means to associate a
 * <CODE>Token</CODE> with its compile time effect.
 * 
 * @author Andrew Jacobs
 * @version	$Id$
 */
public abstract class Opcode extends Token
{
	/**
	 * Constructs an <CODE>Opcode</CODE> instance.
	 * 
	 * @param kind			Identifies the type of <CODE>Token</CODE>.
	 * @param text			The text string this was parsed from.
	 * @param alwaysActive	Marks an opcode that controls conditional compilation.
	 */
	public Opcode (final TokenKind kind, final String text, boolean alwaysActive)
	{
		super (kind, text);
		
		this.alwaysActive = alwaysActive;
	}
	
	/**
	 * Constructs an <CODE>Opcode</CODE> instance.
	 * 
	 * @param kind			Identifies the type of <CODE>Token</CODE>.
	 * @param text			The text string this was parsed from.
	 */
	public Opcode (final TokenKind kind, final String text)
	{
		this (kind, text, false);
	}

	/**
	 * Determines if this <CODE>Opcode</CODE> is active regardless of the 
	 * current conditional compilation state.
	 * 
	 * @return	<CODE>true</CODE> if this <CODE>Opcode</CODE> should be processed.
	 */
	public final boolean isAlwaysActive ()
	{
		return (alwaysActive);
	}
	
	/**
	 * Performs the compilation effect of the <CODE>Opcode</CODE>.
	 * 
	 * @return	<CODE>true</CODE> if the <CODE>Opcode</CODE> can have a label.
	 */
	public abstract boolean compile ();
	
	/**
	 * A flag indicating an <CODE>Opcode</CODE> which is always processed.
	 */
	private final boolean		alwaysActive;
}