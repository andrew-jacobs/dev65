/*
 * Copyright (C),2005 Andrew John Jacobs.
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

/**
 * The <CODE>Line</CODE> class holds a line of source text and the details of
 * where it originally came from.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Line
{
	/**
	 * Construct a <CODE>Line</CODE> instance.
	 * 
	 * @param 	fileName		The name of the source file.
	 * @param 	lineNumber		The corresponding line number.
	 * @param 	text			The actual source text.
	 */
	public Line (final String fileName, int lineNumber, final String text)
	{
		this.fileName   = fileName;
		this.lineNumber = lineNumber;
		this.text		= text;
	}
	
	/**
	 * Provides access to the originating filename.
	 * 
	 * @return	The filename.
	 */
	public String getFileName ()
	{
		return (fileName);
	}
	
	/**
	 * Provides access to the line number.
	 * 
	 * @return	The line number.
	 */
	public int getLineNumber ()
	{
		return (lineNumber);
	}
	
	/**
	 * Provides access to the source text.
	 * 
	 * @return	The source text.
	 */
	public String getText ()
	{
		return (text);
	}
	
	/**
	 * The name of the file this line is from.
	 */
	private final String		fileName;
	
	/**
	 * Its line number.
	 */
	private final int			lineNumber;
	
	/**
	 * The actual line of text itself.
	 */
	private final String		text;
}