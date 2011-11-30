/*
 * Copyright (C),2011 Andrew John Jacobs.
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

package uk.co.demon.obelisk.xasm;

import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Section;

/**
 * Implements a memory model where each memory location can contain a short.
 * 
 * @author	Andrew Jacobs
 * @version	$Id$
 */
public class MemoryModelShort extends MemoryModel
{
	/**
	 * Constructs a <CODE>MemoryModelShort</CODE> instance.
	 * 
	 * @param 	errorHandler		The error handler used to report problems.
	 */
	public MemoryModelShort (final ErrorHandler errorHandler)
	{
		super (errorHandler);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getByte (int index)
	{
		return (bytes [index]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addByte (final Module module, Section section, final Expr expr)
	{
		if (expr.isRelative ()) {
			if (section != null) {
				section.addByte (expr);
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
			}
			else
				error (Error.ERR_NO_SECTION);
		}
		else
			addByte (module, section, expr.resolve (null, null));
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public void addWord (final Module module, Section section, final Expr expr)
	{
		if (expr.isRelative ()) {
			if (section != null) {
				section.addWord (expr);
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
			}
			else
				error (Error.ERR_NO_SECTION);
		}
		else
			addWord (module, section, expr.resolve (null, null));
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public void addLong (final Module module, Section section, final Expr expr)
	{
		if (expr.isRelative ()) {
			if (section != null) {
				section.addLong (expr);
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
				if (byteCount < bytes.length)
					bytes [byteCount++] = 0;
			}
			else
				error (Error.ERR_NO_SECTION);
		}
		else
			addLong (module, section, expr.resolve (null, null));
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public void addByte (final Module module, Section section, long value)
	{
		if (section != null) {
			section.addByte (value);
			if (byteCount < bytes.length)
				bytes [byteCount++] = (short)(value & 0xffff);
		}
		else
			error (Error.ERR_NO_SECTION);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public void addWord (final Module module, Section section, long value)
	{
		if (section != null) {
			section.addWord (value);
			if (module.isBigEndian()) {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 16) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >>  0) & 0xffff);				
			}
			else {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >>  0) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 16) & 0xffff);
			}
		}
		else
			error (Error.ERR_NO_SECTION);
	}
	
	/**
	 * {@inheritDoc} 
	 */
	public void addLong (final Module module, Section section, long value)
	{
		if (section != null) {
			section.addLong (value);
			if (module.isBigEndian()) {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 48) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 32) & 0xffff);				
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 16) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >>  0) & 0xffff);				
			}
			else {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >>  0) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 16) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 32) & 0xffff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (short)((value >> 48) & 0xffff);				
			}
		}
		else
			error (Error.ERR_NO_SECTION);
	}
	
	/**
	 * Captured data used to generate the listing.
	 */
	protected short []		bytes = new short [9];
}
