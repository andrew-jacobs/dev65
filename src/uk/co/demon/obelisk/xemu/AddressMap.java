/*
 * Copyright (C),2014 Andrew John Jacobs.
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

package uk.co.demon.obelisk.xemu;

/**
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public class AddressMap implements Addressable
{
	public AddressMap (int mask)
	{
		map = new Addressable [(this.mask = mask) + 1];
	}
	
	public void add (AddressRange range)
	{
		int		base = range.getBase ();
		int		size = range.getSize ();
		
		for (int offset = 0; offset < size; ++offset)
			map [base + offset] = range;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read (int address)
	{
		return (map [address & mask].read (address));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write (int address, int value)
	{
		map [address & mask].write (address, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load (int address, int value)
	{
		map [address & mask].load (address, value);
	}
	
	private final int mask;
	
	private Addressable []	map;
}