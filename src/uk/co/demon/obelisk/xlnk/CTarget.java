/*
 * Copyright (C),2005-2019 Andrew John Jacobs.
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
import java.io.PrintWriter;

import uk.co.demon.obelisk.xobj.Hex;

public class CTarget extends CachedTarget
{
	/**
	 * Constructs a <CODE>CTarget</CODE> that will generate code for
	 * the indicated address range.
	 * 
	 * @param start			Start of data area.
	 * @param end			End of data area.
	 */
	public CTarget (long start, long end)
	{
		this (start, end, 8);
	}
		
	/**
	 * Constructs a <CODE>CTarget</CODE> that will generate code for
	 * the indicated address range.
	 * 
	 * @param start			Start of data area.
	 * @param end			End of data area.
	 */
	public CTarget (long start, long end, int byteSize)
	{
		super (start, end, byteSize);
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeTo (File file)
	{
		try {
			PrintWriter		writer = new PrintWriter (file);
			int				span  = getByteSize () / 4;
			
			for (int index = 0; index < size; index += 16) {
				for (int offset = 0; offset < 16; ++offset) {
					if ((index + offset) < size) {
						if (offset == 0) writer.print ("\t");
						
						writer.print ("0x" + Hex.toHex (code [index + offset], span) + ", ");
					}
					else
						break;
				}
				writer.println ();
			}
			writer.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: A serious error occurred writing the object module.");
		}
	}
}