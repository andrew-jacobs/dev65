/*
 * Copyright (C),2006 Andrew John Jacobs.
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
import java.io.FileOutputStream;

/**
 * The <CODE>BinTarget</CODE> class performs the final output of a linked
 * module as a single binary file.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public class BinTarget extends CachedTarget
{
	/**
	 * Constructs a <CODE>BinTarget</CODE> that will contain code for the
	 * indicated memory range.
	 * 
	 * @param 	start			The start address of the output code.
	 * @param 	end				The end address of the output code.
	 */
	public BinTarget (long start, long end)
	{
		this (start, end, 8);
	}
		
	/**
	 * Constructs a <CODE>BinTarget</CODE> that will contain code for the
	 * indicated memory range.
	 * 
	 * @param 	start			The start address of the output code.
	 * @param 	end				The end address of the output code.
	 */
	public BinTarget (long start, long end, int byteSize)
	{
		super (start, end, byteSize);
	}
		
	/**
	 * {@inheritDoc}
	 */
	public void writeTo (File file)
	{
		try {
			FileOutputStream	stream = new FileOutputStream (file);
			
			byte [] bytes = new byte [code.length * (getByteSize () / 8)];
			int offset = 0;
			
			switch (getByteSize ()) {
			case 8:
					for (int index = 0; index < code.length; ++index) {
						bytes [offset++] = (byte) code [index];
					}
					break;
					
			case 16:
				for (int index = 0; index < code.length; ++index) {
					bytes [offset++] = (byte) ((code [index] >> 8) & 0xff);
					bytes [offset++] = (byte) ((code [index] >> 0) & 0xff);
				}
				break;
					
			// TODO More sizes	
			}
			
			stream.write (bytes);
			stream.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: A serious error occurred while writing the object file.");
		}
	}
}
