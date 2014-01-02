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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * A <CODE>SymbolMap</CODE> holds the details of where symbols have been
 * placed in memory.
 * 

 * @author Andrew Jacobs
 * @version	$Id$
 */
public final class SymbolMap
{
	/**
	 * Adds an entry to the lookup table for the given symbol and address.
	 * 
	 * @param 	name			The symbol name.
	 * @param 	value			Its memory address.
	 */
	public void addAddress (final String name, long value)
	{
//		System.out.println ("Placing " + name + " at " + value);
		map.put (name, new java.lang.Long (value));
	}
	
	/**
	 * Looks up the address allocated to the given symbol.
	 * 
	 * @param 	name			The target symbol name.
	 * @return	The associated memory address.
	 */
	public long addressOf (final String name)
	{
		return (map.get (name).longValue ());
	}
	
	/**
	 * Returns a vector containing all the symbol names in the map
	 * 
	 * @return	A vector of symbol names.
	 */
	public Vector<String> getSymbols ()
	{
		Vector<String>	symbols = new Vector<String> ();
		Enumeration<String>	cursor 	= map.keys ();
		
		while (cursor.hasMoreElements ())
			symbols.add (cursor.nextElement());
		
		return (symbols);
	}
	
	/**
	 * A map of symbol name to address as an Integer.
	 */
	private Hashtable<String, java.lang.Long> map
		= new Hashtable<String, java.lang.Long> (); 
}