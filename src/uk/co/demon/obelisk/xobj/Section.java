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

import java.util.Vector;

/**
 * The <CODE>Section</CODE> class represents a target memory area (e.g.
 * PAGE0, BSS, CODE or DATA) in the object module. <CODE>Section</CODE>
 * instance can be relative or absolute and are internally comprised of
 * <CODE>Part</CODE> instances that describle byte values or expressions.
 * 
 * @author Andrew Jacobs
 * @version	$Id$
 */
public class Section
{
	/**
	 * Constructs a <CODE>Module</CODE> instance with the given name.
	 * 
	 * @param 	module			The owning <CODE>Module</CODE>.		
	 * @param 	name			The section name.
	 */
	public Section (final Module module, final String name)
	{
		this.module = module;
		this.name   = name;
		
		relative = true;
		clear ();
	}
	
	/**
	 * Constructs a <CODE>Module</CODE> instance with the given name
	 * and start address.
	 * 
	 * @param 	module			The owning <CODE>Module</CODE>.		
	 * @param 	name			The section name.
	 * @param	start			The start address.
	 */
	public Section (final Module module, final String name, int start)
	{
		this.module = module;
		this.name   = name;
		this.start	= start;
		
		relative = false;
		clear ();
	}
	
	/**
	 * Provides access to the section name.
	 * 
	 * @return	The section name.
	 */
	public String getName ()
	{
		return (name);
	}
	
	/**
	 * Determines in the section is relative.
	 * 
	 * @return	A flag indicating if the section is relative.
	 */
	public boolean isRelative ()
	{
		return (relative);
	}
	
	/**
	 * Determines in the section is absolute.
	 * 
	 * @return	A flag indicating if the section is absolute.
	 */
	public boolean isAbsolute ()
	{
		return (!relative);
	}
	
	/**
	 * Provides access to the start address of the section.
	 * 
	 * @return	The start address of an absolute section.
	 */
	public int getStart ()
	{
		return (start);
	}
	
	/**
	 * Provides access to the section size.
	 * 
	 * @return	The size of the section.
	 */
	public int getSize ()
	{
		return (size);
	}
	
	/**
	 * Calculate the origin (address) of the next byte to be added to
	 * the section. The original will either be an absolute address or
	 * a relative offset.
	 * 
	 * @return	The origin of for the next byte.
	 */
	public Value getOrigin ()
	{
		return (new Value (relative ? this : null, start + size));
	}
	
	/**
	 * Sets the origin of the current section to a specific address.
	 * This operation creates a new section based on the the current
	 * but starting at a specific address.
	 * 
	 * @param 	origin			The starting address for the section.
	 * @return	The section representing the target address.
	 */
	public Section setOrigin (int origin)
	{
		return (module.findSection (name, origin));
	}
	
	/**
	 * Resets a section and clears out all its contents.
	 */
	public void clear ()
	{
		size = 0;
		parts.clear();
	}
	
	/**
	 * Adds a byte to the current section.
	 * 
	 * @param 	expr			An expression yielding a byte value.
	 */
	public void addByte (Expr expr)
	{
		if (expr.isAbsolute ())
			addByte (expr.resolve (null, null));
		else {
			parts.add (new Byte (expr));
			++size;
		}
	}

	/**
	 * Adds a word to the current section.
	 * 
	 * @param 	expr			An expression yielding a word value.
	 */
	public void addWord (Expr expr)
	{
		if (expr.isAbsolute ())
			addWord (expr.resolve (null, null));
		else {
			parts.add (new Word (expr));
			size += 2;
		}
	}

	/**
	 * Adds a long to the current section.
	 * 
	 * @param 	expr			An expression yielding a long value.
	 */
	public void addLong (Expr expr)
	{
		if (expr.isAbsolute ())
			addLong (expr.resolve (null, null));
		else {
			parts.add (new Long (expr));
			size += 4;
		}
	}

	/**
	 * Adds a constant byte value to the current section.
	 * 
	 * @param 	value			The byte value to add.
	 */
	public void addByte (int value)
	{
		if ((parts.isEmpty ()) || !(parts.lastElement() instanceof Code))
			parts.add (new Code ());
		
		((Code)(parts.lastElement())).addByte (value);
		++size;
	}
	
	/**
	 * Adds a constant word value to the current section.
	 * 
	 * @param 	value			The word value to add.
	 */
	public void addWord (int value)
	{
		if ((parts.isEmpty ()) || !(parts.lastElement() instanceof Code))
			parts.add (new Code ());
		
		if (module.isBigEndian ()) {
			((Code)(parts.lastElement())).addByte ((value & 0xff00) >> 8);
			((Code)(parts.lastElement())).addByte ((value & 0x00ff) >> 0);
		}
		else {
			((Code)(parts.lastElement())).addByte ((value & 0x00ff) >> 0);
			((Code)(parts.lastElement())).addByte ((value & 0xff00) >> 8);
		}
		size += 2;		
	}

	/**
	 * Adds a constant long value to the current section.
	 * 
	 * @param 	value			The long value to add.
	 */
	public void addLong (int value)
	{
		if ((parts.isEmpty ()) || !(parts.lastElement() instanceof Code))
			parts.add (new Code ());
		
		if (module.isBigEndian ()) {
			((Code)(parts.lastElement())).addByte ((value & 0xff000000) >> 24);
			((Code)(parts.lastElement())).addByte ((value & 0x00ff0000) >> 16);
			((Code)(parts.lastElement())).addByte ((value & 0x0000ff00) >>  8);
			((Code)(parts.lastElement())).addByte ((value & 0x000000ff) >>  0);
		}
		else {
			((Code)(parts.lastElement())).addByte ((value & 0x000000ff) >>  0);
			((Code)(parts.lastElement())).addByte ((value & 0x0000ff00) >>  8);
			((Code)(parts.lastElement())).addByte ((value & 0x00ff0000) >> 16);
			((Code)(parts.lastElement())).addByte ((value & 0xff000000) >> 24);
		}
		size += 4;		
	}
	
	/**
	 * Converts the module into an XML string. Sections containing no data
	 * return an empty string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		if (parts.size () > 0) {
			StringBuffer	buffer	= new StringBuffer ();
			
			buffer.append ("<section name='" + name + "'");
			if (!relative)
				buffer.append (" addr='" + Hex.toHex (start, 8) + "' size='" + size + "'");
			buffer.append (">");
			for (int index = 0; index < parts.size (); ++index)
				buffer.append (parts.elementAt (index).toString ());
			buffer.append ("</section>");
			
			return (buffer.toString ());
		}
		return ("");
	}
	
	/**
	 * Provides access to the parts.
	 * 
	 * @return	A vector of section parts.
	 */
	public Vector getParts ()
	{
		return (parts);
	}
	
	/**
	 * Sets the start address of the section and marks it as absolute.
	 * 
	 * @param 	start			The start address of the section.
	 */
	protected void setStart (int start)
	{
		this.start = start;
		
		relative = false;
	}
	
	/**
	 * Sets the size of the section.
	 * 
	 * @param size				The section size.
	 */
	protected void setSize (int size)
	{
		this.size = size;
	}
	
	/**
	 * A reference to the containing <CODE>Module</CODE>.
	 */
	private final Module		module;
	
	/**
	 * The section name string.
	 */
	private final String		name;
	
	/**
	 * A flag indicating if the section is relative or absolute.
	 */
	private boolean				relative;
	
	/**
	 * The start address of an absolute section.
	 */
	private int					start;
	
	/**
	 * The size of the section.
	 */
	private int					size;
	
	/**
	 * The set of constituent parts that make up is section.
	 */
	private	Vector				parts		= new Vector ();
}