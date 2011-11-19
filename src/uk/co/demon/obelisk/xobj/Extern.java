/*
 * Copyright (C),2005-2011 Andrew John Jacobs.
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

package uk.co.demon.obelisk.xobj;

/**
 * An <CODE>Extern</CODE> instance represents a names external symbol that
 * could reside in any section.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Extern extends Expr
{
	/**
	 * Constructs an <CODE>Extern</CODE> instance.
	 * 
	 * @param 	name			The name of the symbol.
	 */
	public Extern (final String name)
	{
		this.name = name;
	}
	
	/**
	 * Provides access to the symbol name.
	 * 
	 * @return	The symbol name.
	 */
	public String getName ()
	{
		return (name);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAbsolute ()
	{
		return (false);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isExternal (Section section)
	{
		return (true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long resolve (SectionMap sections, SymbolMap symbols)
	{
		if ((sections == null) && (symbols == null))
			return (0);
		
		return (symbols.addressOf (name));
	}
	
	/**
	 * Converts the module into an XML string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		return ("<ext>" + name + "</ext>");
	}

	/**
	 * The name of the external symbol.
	 */
	private final String		name;
}