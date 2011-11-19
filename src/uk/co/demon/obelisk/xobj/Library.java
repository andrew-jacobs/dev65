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

import java.util.Vector;

/**
 * A <CODE>Library</CODE> instance contains a complete code library.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Library
{
	public Library ()
	{ }
	
	public void clear ()
	{
		modules.clear ();
	}
	
	public void addModule (Module module)
	{
		modules.add (module);
	}
	
	public boolean updateModule (Module module)
	{
		for (int index = 0; index < modules.size (); ++index) {
			Module target = modules.elementAt (index);
			
			if (target.getName().equals (module.getName())) {
				modules.set (index, module);
				return (true);
			}
		}
		modules.add (module);
		return (false);
	}
	
	public boolean removeModule (Module module)
	{
		for (int index = 0; index < modules.size (); ++index) {
			Module target = modules.elementAt (index);
			
			if (target.getName().equals (module.getName())) {
				modules.remove (index);
				return (true);
			}
		}
		return (false);
	}
	
	public Module [] getModules ()
	{
		Module [] result = new Module [modules.size ()];
		
		modules.copyInto (result);
		return (result);
	}
	
	/**
	 * Converts the module into an XML string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		StringBuffer	buffer = new StringBuffer ();
		
		buffer.append ("<library>");
		for (int index = 0; index < modules.size (); ++index)
			buffer.append ((modules.elementAt(index)).toString ());
		buffer.append ("</library>");
		
		return (buffer.toString ());
	}

	/**
	 * All the modules in the library.
	 */
	private Vector<Module>		modules = new Vector<Module> ();
}
