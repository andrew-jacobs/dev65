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
 * The <CODE>MemoryModule</CODE> class defines standard methods for storing
 * data into a <CODE>Section</CODE> within a <CODE>Module</CODE> independent
 * of the actual byte size.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class MemoryModel
{
	/**
	 * Adds a byte value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	expr		The expression defining the value.
	 */
	public abstract void addByte (final Module module, Section section, final Expr expr);
	
	/**
	 * Adds a word value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	expr		The expression defining the value.
	 */
	public abstract void addWord (final Module module, Section section, final Expr expr);
	
	/**
	 * Adds a long value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	expr		The expression defining the value.
	 */
	public abstract void addLong (final Module module, Section section, final Expr expr);
	
	/**
	 * Adds a literal byte value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	value		The literal value.
	 */
	public abstract void addByte (final Module module, Section section, long value);
	
	/**
	 * Adds a literal word value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	value		The literal value.
	 */
	public abstract void addWord (final Module module, Section section, long value);
	
	/**
	 * Adds a literal long value to the output memory area.
	 * 
	 * @param	module		The <CODE>Module</CODE> containing the output.
	 * @param	section		The <CODE>Section</CODE> containing the output.
	 * @param	value		The literal value.
	 */
	public abstract void addLong (final Module module, Section section, long value);
	
	/**
	 * Clears any stored data.
	 */
	public void clear ()
	{
		byteCount = 0;
	}
	
	/**
	 * Returns the current byte count.
	 * 
	 * @return	The byte count.
	 */
	public int getByteCount ()
	{
		return (byteCount);
	}
	
	/**
	 * Fetches the byte value at the specified index.
	 * 
	 * @param	index		The index into memory.
	 * @return	The value of the indexed byte.
	 */
	public abstract int getByte (int index);	

	/**
	 * The number of bytes in the memory area.
	 */
	protected int 			byteCount;
	
	/**
	 * Constructs a <CODE>MemoryModel</CODE> that uses the indicated
	 * <CODE>ErrorHandler</CODE> to report problems.
	 * 
	 * @param errorHandler	The <CODE>ErrorHandler</CODE> instance.
	 */
	protected MemoryModel (final ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
		
		clear ();
	}
	
	/**
	 * Reports an error message.
	 * 
	 * @param message		The error message string.
	 */
	protected void error (final String message)
	{
		errorHandler.error (message);
	}
	
	/**
	 * Reports a warning message.
	 * 
	 * @param message		The warning message string.
	 */
	protected void warning (final String message)
	{
		errorHandler.warning (message);
	}
	
	/**
	 * The <CODE>ErrorHandler</CODE> used to report errors and warnings.
	 */
	private final ErrorHandler	errorHandler;
}
