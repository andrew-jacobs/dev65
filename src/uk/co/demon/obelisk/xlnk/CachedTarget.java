/*
 * Copyright (C),2006-2011 Andrew John Jacobs.
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
 * The linker performs the final relocation and code fix up. It emits the
 * sections in a random order so the <CODE>CachedTarget</CODE> class stores
 * bytes as they are generated in an array so that the final output can be
 * written in order and in one piece.
 * 
 * @author	Andrew Jacobs
 * @version	$Id$
 */
abstract class CachedTarget extends Target
{
	/**
	 * {@inheritDoc}
	 */
	public void store (long addr, long value)
	{
		if ((start <= addr) && (addr <= end))
			code [(int)(addr - start)] = (int) value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public abstract void writeTo (File file);
	
	/**
	 * The start address of the memory area.
	 */
	protected long			start;
	
	/**
	 * The end address of the memory area.
	 */
	protected long			end;
	
	/**
	 * The size of the memory area.
	 */
	protected int			size;
	
	/**
	 * The data comprising the linked code.
	 */
	protected int [] 		code;

	/**
	 * Constructs a <CODE>CachedTarget</CODE> that will capture and store
	 * code for the given memory range.
	 * 
	 * @param 	start			The start address of the output code.
	 * @param 	end				The end address of the output code.
	 */
	protected CachedTarget (long start, long end, int byteSize)
	{
		super (byteSize);
		
		this.start = start;
		this.end   = end;
		size = (int)(end - start + 1);
		
		code = new int [size];
	}
}