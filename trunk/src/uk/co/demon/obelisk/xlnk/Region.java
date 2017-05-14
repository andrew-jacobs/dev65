/*
 * Copyright (C),2005-2016 Andrew John Jacobs.
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

import uk.co.demon.obelisk.xobj.Hex;

/**
 * The <CODE>Region</CODE> class contains the lower and upper address of a
 * memory block from which areas are consumed as code and data modules are
 * processed. 
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
final class Region
{
	/**
	 * Constructs a <CODE>Region</CODE> to represent the indicated memory
	 * area range.
	 * 
	 * @param start			The start address of the memory block.
	 * @param end			The end address of the memory block.
	 */
	public Region (final String start, final String end)
	{
		this (parseAddr (start), parseAddr (end));
	}
	
	/**
	 * Returns the start address of the <CODE>Region</CODE>.
	 * 
	 * @return The start address of the <CODE>Region</CODE>
	 */
	public long getStart ()
	{
		return (start);
	}
	
	/**
	 * Returns the end address of the <CODE>Region</CODE>.
	 * 
	 * @return The end address of the <CODE>Region</CODE>
	 */
	public long getEnd ()
	{
		return (end);
	}

	/**
	 * Returns the remaining size of the <CODE>Region</CODE>.
	 * 
	 * @return The remaining size of the <CODE>Region</CODE>.
	 */
	public int getSize ()
	{
		return ((start <= end) ? (int)(end - start + 1) : 0);
	}

	/**
	 * Reduces the region by reserving a number of bytes at its start.
	 * 
	 * @param 	size		The amount to reserve.
	 */
	public void reserve (int size)
	{
		start += size;
	}
	
	/**
	 * Splits a region at a given address returning the tail instance.
	 * 
	 * @param 	addr		Where to split.
	 * @return	The tail region.
	 */
	public Region split (long addr)
	{
		Region	tail = new Region (addr, end);
		
		end = addr - 1;
		return (tail);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString ()
	{
		return ("$" + Hex.toHex(start,8) + "-$" + Hex.toHex (end, 8));
	}
	
	/**
	 * Constructs a <CODE>Region</CODE> for the indicated address
	 * range.
	 * 
	 * @param start			The start of the region.
	 * @param end			The end of the region.
	 */
	protected Region (long start, long end)
	{
		this.start = start;
		this.end   = end;
	}
	
	/**
	 * The start of the region.
	 */
	private long		start;
	
	/**
	 * The end of the region.
	 */
	private long		end;
	
	/**
	 * Parses an address expressed in hex, oct, bin or decimal.
	 * 
	 * @param 	addr		The address string
	 * @return	The parsed address.
	 */
	private static long parseAddr (final String addr)
	{
		switch (addr.charAt(0)) {
		case '%': 	return (Long.parseLong(addr.substring (1), 2));
		case '@': 	return (Long.parseLong(addr.substring (1), 8));
		case '$': 	return (Long.parseLong(addr.substring (1), 16));
		default:	return (Long.parseLong(addr));
		}
	}
}