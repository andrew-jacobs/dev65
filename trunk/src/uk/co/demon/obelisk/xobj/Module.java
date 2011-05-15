/*
 * Copyright (C),2005-2007 Andrew John Jacobs.
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A <CODE>Module</CODE> instance contains the code generated during the
 * assembly of one source file.
 * 
 * @author Andrew Jacobs
 * @version	$Id$
 */
public final class Module
{
	/**
	 * Constructs a <CODE>Module</CODE> for the given target.
	 * 
	 * @param 	target			The target architecture
	 * @param 	bigEndian		The endianness of the target.
	 */
	public Module (final String target, boolean bigEndian)
	{
		this.target    = target;
		this.bigEndian = bigEndian;
	}
	
	/**
	 * Provides access to the name of the module.
	 * 
	 * @return	The module name.
	 */
	public String getName ()
	{
		return (name);
	}
	
	/**
	 * Changes the name of the module.
	 * 
	 * @param 	name			The new module name.
	 */
	public void setName (final String name)
	{
		this.name = name;
	}
	
	/**
	 * Determines the endianness of the module.
	 * 
	 * @return	<CODE>true</CODE> if big endian.
	 */
	public boolean isBigEndian ()
	{
		return (bigEndian);
	}
	
	/**
	 * Provides access to a vector of global symbol names.
	 * 
	 * @return	The global symbol vector.
	 */
	public Vector getGlobals ()
	{
		Vector		result = new Vector ();
		
		Enumeration cursor = globals.keys ();
		while (cursor.hasMoreElements ())
			result.add (cursor.nextElement());
		
		return (result);
	}
	
	/**
	 * Provides access to a vector of sections
	 * 
	 * @return	The section vector.
	 */
	public Vector getSections ()
	{
		return (sections);
	}
	
	/**
	 * Locates a <CODE>Section</CODE> with the given name.
	 * 
	 * @param 	name		The required section name.
	 * @return	The matching section.
	 */
	public Section findSection (final String name)
	{
		Section		section;
		
		for (int index = 0; index < sections.size (); ++index) {
			section = (Section) sections.elementAt (index);
			
			if (section.isRelative () && section.getName ().equals (name))
				return (section);
		}
		
		section = new Section (this, name);
		sections.add (section);
		return (section);
	}
	
	/**
	 * Locates a <CODE>Section</CODE> with the given name and start
	 * address.
	 * 
	 * @param 	name		The required section name.
	 * @param	start		The start address of the section.
	 * @return	The matching section.
	 */
	public Section findSection (final String name, int start)
	{
		Section		section;
		
		for (int index = 0; index < sections.size (); ++index) {
			section = (Section) sections.elementAt (index);
			
			if (section.isAbsolute () && (section.getStart () == start) && section.getName ().equals (name)) 
				return (section);
		}
		
		section = new Section (this, name, start);
		sections.add (section);
		return (section);
	}

	/**
	 * Adds a global symbol to the modules export list/
	 * 
	 * @param 	name		The name of the symbol.
	 * @param 	expr		The value of the symbol.
	 */
	public void addGlobal (final String name, Expr expr)
	{
		globals.put (name, expr);
	}
	
	/**
	 * Fetchs the expression defining a global symbol.
	 * 
	 * @param 	name		The name of the symbol
	 * @return	The related expression.
	 */
	public Expr getGlobal (final String name)
	{
		return ((Expr) globals.get (name));
	}
	
	/**
	 * Clears all the data from the sections in this module.
	 */
	public void clear ()
	{
		for (int index = 0; index < sections.size (); ++index)
			((Section)(sections.elementAt (index))).clear ();
		
		globals.clear ();
	}
	
	/**
	 * Converts the module into an XML string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		StringBuffer	buffer = new StringBuffer ();
		
		buffer.append ("<module");
		buffer.append (" target='" + target + "' ");
		buffer.append (" endian='" + (bigEndian ? "big" : "little")+ "'");
		buffer.append (" name='" + name + "'>");
		for (int index = 0; index < sections.size (); ++index)
			buffer.append (sections.elementAt (index).toString ());
		
		for (Enumeration cursor = globals.keys (); cursor.hasMoreElements();) {
			String	name	= (String) cursor.nextElement ();
			Expr	expr	= (Expr) globals.get (name);
			
			buffer.append ("<gbl>" + name + expr + "</gbl>");
		}
		buffer.append ("</module>");
		
		return (buffer.toString());
	}
	
	/**
	 * The name of this module.
	 */
	private String			name		= null;
	
	/**
	 * The name of the target architecture.
	 */
	private final String	target;
	
	/**
	 * The endianness of the target.
	 */
	private final boolean	bigEndian;
	
	/**
	 * The set of sections defined in the module.
	 */
	private Vector 			sections	= new Vector ();
	
	/**
	 * The set of exported symbols.
	 */
	private Hashtable		globals		= new Hashtable ();
}