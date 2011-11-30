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
 * The <CODE>MemoryModelByte</CODE> class implements a <CODE>MemoryModel</CODE>
 * for an 8-bit byte.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public class MemoryModelByte extends MemoryModel
{
	/**
	 * Constructs a <CODE>MemoryModelByte</CODE> that uses the indicated
	 * <CODE>ErrorHandler</CODE> to report problems.
	 * 
	 * @param errorHandler	The <CODE>ErrorHandler</CODE> instance.
	 */
	public MemoryModelByte (final ErrorHandler errorHandler)
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
				bytes [byteCount++] = (byte)(value & 0xff);
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
					bytes [byteCount++] = (byte)((value >> 8) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 0) & 0xff);				
			}
			else {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 0) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 8) & 0xff);
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
					bytes [byteCount++] = (byte)((value >> 24) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 16) & 0xff);				
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 8) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 0) & 0xff);				
			}
			else {
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 0) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 8) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 16) & 0xff);
				if (byteCount < bytes.length)
					bytes [byteCount++] = (byte)((value >> 24) & 0xff);				
			}
		}
		else
			error (Error.ERR_NO_SECTION);
	}
	
	/**
	 * Captured data used to generate the listing.
	 */
	protected byte []		bytes = new byte [9];
}
