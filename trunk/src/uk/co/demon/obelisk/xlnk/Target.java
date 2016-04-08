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

import java.io.File;

/**
 * Interface implemented by all output formats.
 * 
 * @author	Andrew Jacobs
 * @version	$Id$
 */
abstract class Target
{
	/**
	 * Stores the given byte value at the indicated address.
	 * 
	 * @param	addr		Where to store.
	 * @param 	value		What to store.
	 */
	public abstract void store (long addr, long value);
	
	/**
	 * Write the store data content to the indicated file.
	 * 
	 * @param 	file		File to write output to.
	 */
	public abstract void writeTo (File file);
	
	/**
	 * Constructs a <CODE>Target</CODE> with a given byte size.
	 * 
	 * @param	byteSize	The size of a byte in bits.
	 */
	protected Target (int byteSize)
	{
		this.byteSize = byteSize;
	}
	
	/**
	 * Returns the target's byte size (in bits).
	 * 
	 * @return	The byte size (in bits).
	 */
	protected int getByteSize ()
	{
		return (byteSize);
	}
	
	/**
	 * The number of bits in a byte, normally 8.
	 */
	private final int byteSize;
}