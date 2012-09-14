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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * The <CODE>FileSource</CODE> class implements a <CODE>Source</CODE> that
 * reads from a file.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class FileSource implements Source
{
	/**
	 * Constructs a <CODE>FileSource</CODE> instance.
	 * 
	 * @param 	fileName		The name of the source file.
	 * @param 	stream			The <CODE>FileInputStream</CODE> attached to it.
	 */
	public FileSource (final String fileName, FileInputStream stream)
	{
		this.reader 	= new BufferedReader (
							new InputStreamReader (stream,
								Charset.forName ("ISO-8859-1")));
		this.fileName 	= fileName;
		this.lineNumber = 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Line nextLine ()
	{
		try {
			String			text = reader.readLine();
		
			if (text != null)
				return (new Line (fileName, ++lineNumber, text));
		}
		catch (IOException error) {
			;
		}
		return (null);
	}

	/**
	 * The <CODE>BufferedReader</CODE> to get source lines from.
	 */
	private BufferedReader		reader;
	
	/**
	 * The name of the file being read.
	 */
	private final String		fileName;
	
	/**
	 * The current line number in the file.
	 */
	private int					lineNumber;
}