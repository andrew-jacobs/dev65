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

package uk.co.demon.obelisk.xlnk;

/**
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
final class Region
{
	public Region (final String start, final String end)
	{
		this (parseAddr (start), parseAddr (end));
	}
	
	public int getStart ()
	{
		return (start);
	}
	
	public int getEnd ()
	{
		return (end);
	}
	
	public int getSize ()
	{
		return (end - start + 1);
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
	public Region split (int addr)
	{
		Region	tail = new Region (addr, end);
		
		end = addr - 1;
		return (tail);
	}
	
	/**
	 * Constructs a <CODE>Region</CODE> for the indicated address
	 * range.
	 * 
	 * @param start			The start of the region.
	 * @param end			The end of the region.
	 */
	protected Region (int start, int end)
	{
		this.start = start;
		this.end   = end;
	}
	
	/**
	 * The start of the region.
	 */
	private int			start;
	
	/**
	 * The end of the region.
	 */
	private int			end;
	
	/**
	 * Parses an address expressed in hex, oct, bin or decimal.
	 * 
	 * @param 	addr		The address string
	 * @return	The parsed address.
	 */
	private static int parseAddr (final String addr)
	{
		switch (addr.charAt(0)) {
		case '%': 	return (Integer.parseInt(addr.substring (1), 2));
		case '@': 	return (Integer.parseInt(addr.substring (1), 8));
		case '$': 	return (Integer.parseInt(addr.substring (1), 16));
		default:	return (Integer.parseInt(addr));
		}
	}
}