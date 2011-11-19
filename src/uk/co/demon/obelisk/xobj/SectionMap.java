/*
 * Copyright (C),2005-2006 Andrew John Jacobs.
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

/**
 * A <CODE>SectionMap</CODE> holds the details of where <CODE>Section</CODE>
 * instances have been placed in memory.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class SectionMap
{
	/**
	 * Determines the base address of the given <CODE>Section</CODE>.
	 * 
	 * @param 	section			The target <CODE>Section</CODE>
	 * @return	The base address of the section.
	 */
	public long baseAddressOf (Section section)
	{
		return ((map.get (section)).longValue ());
	}
	
	public void setBaseAddress (Section section, long addr)
	{
//		System.out.println (">> Placing " + section.getName() +
//			" (" + section.getSize () + ") at " + Hex.toHex (addr, 8));
//		System.out.println ("Code:" + section);
		
		map.put (section, new java.lang.Long (addr));
	}
	
	/**
	 * A mapping table for section to base address as an Integer.
	 */
	private Hashtable<Section, java.lang.Long>	map
		= new Hashtable<Section, java.lang.Long> ();
}
