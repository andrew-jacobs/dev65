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

public abstract class MemoryModel
{
	public abstract void addByte (final Module module, Section section, final Expr expr);
	public abstract void addWord (final Module module, Section section, final Expr expr);
	public abstract void addLong (final Module module, Section section, final Expr expr);
	public abstract void addByte (final Module module, Section section, long value);
	public abstract void addWord (final Module module, Section section, long value);
	public abstract void addLong (final Module module, Section section, long value);
	
	public void clear ()
	{
		byteCount = 0;
	}
	
	public int getByteCount ()
	{
		return (byteCount);
	}
	
	public abstract int getByte (int index);	
		
	protected int 			byteCount;
	
	protected MemoryModel (final ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
		
		clear ();
	}
	
	protected void error (final String message)
	{
		errorHandler.error (message);
	}
	
	protected void warning (final String message)
	{
		errorHandler.warning (message);
	}
	
	private final ErrorHandler	errorHandler;
}
