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

package uk.co.demon.obelisk.xobj;

/**
 * The <CODE>Value</CODE> class represents an absolute value or a relative
 * offset (such as an address).
 *
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Value extends Expr
{
	/**
	 * Constructs a <CODE>Value</CODE> instance from the given <CODE>Section
	 * </CODE> (which may be <CODE>null</CODE>) and integer value.
	 * 
	 * @param 	section				The relative section or <CODE>null</CODE>.
	 * @param 	value				An integer value.
	 */
	public Value (final Section section, int value)
	{
		this.section = section;
		this.value   = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isAbsolute ()
	{
		return (section == null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isExternal (Section section)
	{
		return (section != this.section);
	}
	
	/**
	 * Provides access to the section.
	 * 
	 * @return	The relative section or <CODE>null</CODE>.
	 */
	public Section getSection ()
	{
		return (section);
	}
	
	/**
	 * Provides access to the integer part of the value.
	 * 
	 * @return	The integer value.
	 */
	public int getValue ()
	{
		return (value);
	}

	/**
	 * {@inheritDoc}
	 */
	public int resolve (SectionMap sections, SymbolMap symbols)
	{
		if ((section == null) || ((sections == null) && (symbols == null)))
			return (value);
		
		return (sections.baseAddressOf (section) + value);
	}
	
	/**
	 * Converts the module into an XML string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		if (section != null)
			return ("<val sect='" + section.getName () + "'>" + value + "</val>");
		else
			return ("<val>" + value + "</val>");
	}
	
	/**
	 * The relative <CODE>section</CODE> for relocatable values.
	 */
	private	final Section	section;
	
	/**
	 * The absolute or relative offset value.
	 */
	private final int		value;
}