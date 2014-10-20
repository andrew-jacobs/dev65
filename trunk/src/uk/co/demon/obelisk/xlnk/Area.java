/*
 * Copyright (C),2006-2014 Andrew John Jacobs.
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

package uk.co.demon.obelisk.xlnk;

import java.util.Vector;

import uk.co.demon.obelisk.xobj.Section;

/**
 * The <CODE>Area</CODE> class keeps a record of all the memory areas where
 * a type of section can be placed. As objects are assigned to absolute
 * locations the memory area list is updated to keep track of the remaining
 * space.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
final class Area
{
	/**
	 * Constructs an <CODE>Area</CODE> given
	 * a character string containing a list of memory address pairs (e.g.
	 * '$FF00-$FDFF,$FF00-$FFFF').
	 * 
	 * @param 	location		The memory address pairs.
	 */
	public Area (final String location)
	{
		String [] pairs = location.split (",");
		for (int index = 0; index < pairs.length; ++index) {
			String [] addrs = pairs [index].split ("-");
			
			if (addrs.length != 2) {
				System.err.println ("Invalid address pair (" + pairs [index] + ")");
				System.exit (1);
			}
			Region region = new Region (addrs [0], addrs [1]);
		
			boolean handled = false;
			for (int position = 0; position < regions.size (); ++position) {
				Region other = regions.elementAt (position);
				
				if (region.getStart () < other.getStart()) {
					regions.insertElementAt (region, position);
					handled = true;
					break;
				}
			}
			if (!handled) regions.add (region);
		}
	}
	
	/**
	 * Provides access to a <CODE>Vector</CODE> of the current free regions.
	 * 
	 * @return	The free regions left for this section type.
	 */
	public Vector<Region> getRegions ()
	{
		return (regions);
	}
	
	/**
	 * Determines the lowest free memory address for this <CODE>Area</CODE>.
	 *  
	 * @return	The lowest free memory address.
	 */
	public long getLoAddr ()
	{
		return (((Region) regions.firstElement ()).getStart ());
	}
	
	/**
	 * Determines the highest free memory address for this <CODE>Area</CODE>.
	 *  
	 * @return	The highest free memory address.
	 */
	public long getHiAddr ()
	{
		return (((Region) regions.lastElement()).getEnd ());
	}
	
	/**
	 * Attempts to fit the given <CODE>Section</CODE> into the first
	 * suitable <CODE>Region<CODE> controlled by this <CODE>Area</CODE>.
	 * 
	 * @param 	section			The <CODE>Section</CODE> to be fitted.
	 * @return	The address where the <CODE>Section</CODE> was placed.
	 */
	public long fitSection (Section section)
	{
		long			addr = -1;
		int				size = section.getSize ();
				
		if (section.isAbsolute()) {
			addr = section.getStart ();
			
			// Find the region that contains the section
			for (int index = 0; index < regions.size (); ++index) {
				Region region = (Region) regions.elementAt (index);
				
				if ((region.getStart () <= addr) &&	((addr + size) <= region.getEnd ())) {
					if (region.getStart () == addr)
						region.reserve (size);
					else {
						region = region.split (addr);
						regions.insertElementAt (region, index + 1);
						region.reserve (size);
					}
				}
			}
		}
		else {
			for (int index = 0; index < regions.size (); ++index) {
				Region region = (Region) regions.elementAt (index);
				
				// Find first region large enough to hold section
				if (region.getSize () >= size) {
					addr = region.getStart ();
					region.reserve (size);
					break;
				}
			}
		}
		return (addr);
	}
	
	/**
	 * Contains the free <CODE>Region</CODE> list in increase address
	 * order.
	 */
	private Vector<Region>		regions		= new Vector<Region> ();
}