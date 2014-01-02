/*
 * Copyright (C),2005-2014 Andrew John Jacobs.
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

/**
 * A linker target format that creates Motorola S19 files.
 * 
 * @author	Andrew Jacobs
 * @version	$Id$
 */
abstract class SRecordTarget extends CachedTarget
{
	/**
	 * {@inheritDoc}
	 */
	public void writeTo (File file)
	{
		int			bytes;
		int			total;
		int			count = 0;
		
		try {
			PrintWriter		writer = new PrintWriter (file);
			
			// Generate code records
			for (int index = 0; index < size; index += 32) {
				long addr = start + index;
				writer.print ('S');
				total = 0;

				bytes = size - index;
				if (bytes > 32) bytes = 32;
					
				switch (addrSize) {
				case 16:
					writer.print('1');
					writer.print(Hex.toHex (bytes + 3, 2));				
					total += (bytes + 3);
					writer.print(Hex.toHex(addr, 4));
					total += (addr >>  8) & 0xff;
					total += (addr >>  0) & 0xff;
					break;
					
				case 24:
					writer.print('2');
					writer.print(Hex.toHex (bytes + 4, 2));				
					total += (bytes + 4);
					writer.print(Hex.toHex(addr, 6));
					total += (addr >> 16) & 0xff;
					total += (addr >>  8) & 0xff;
					total += (addr >>  0) & 0xff;
					break;
					
				case 32:
					writer.print('3');
					writer.print(Hex.toHex (bytes + 5, 2));				
					total += (bytes + 5);
					writer.print(Hex.toHex(addr, 8));
					total += (addr >> 24) & 0xff;
					total += (addr >> 16) & 0xff;
					total += (addr >>  8) & 0xff;
					total += (addr >>  0) & 0xff;
					break;
				}
								
				for (int offset = 0; offset < bytes; ++offset) {
					writer.print (Hex.toHex (code [index + offset], 2));
					total += code [index + offset];
				}
				writer.print(Hex.toHex (~total & 0xff, 2));
				
				writer.println ();
				++count;
			}
			
			// Generate record count
			total = 0;
			writer.print ('S');
			writer.print ('5');
			writer.print ("05");
			total += 5;
			
			writer.print (Hex.toHex (count, 8));
			total += (count >> 24) & 0xff;
			total += (count >> 16) & 0xff;
			total += (count >>  8) & 0xff;
			total += (count >>  0) & 0xff;
		
			writer.print (Hex.toHex (~total & 0xff, 2));
			writer.println ();
			
			// Generate termination record
			total = 0;
			writer.print ('S');
			
			switch (addrSize) {
			case 16:
				writer.print('9');
				writer.print("03");				
				total += 3;
				writer.print(Hex.toHex (start, 4));
				total += (start >>  8) & 0xff;
				total += (start >>  0) & 0xff;
				break;
				
			case 24:
				writer.print('8');
				writer.print("04");				
				total += 4;
				writer.print(Hex.toHex (start, 6));
				total += (start >> 16) & 0xff;
				total += (start >>  8) & 0xff;
				total += (start >>  0) & 0xff;
				break;
				
			case 32:
				writer.print('7');
				writer.print("05");				
				total += 5;
				writer.print(Hex.toHex (start, 8));
				total += (start >> 24) & 0xff;
				total += (start >> 16) & 0xff;
				total += (start >>  8) & 0xff;
				total += (start >>  0) & 0xff;
				break;
			}
		
			writer.print (Hex.toHex (~total & 0xff, 2));
			writer.println ();

			writer.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: A serious error occurred writing the object module.");
		}
	}
	
	/**
	 * Constructs a <CODE>HexTarget</CODE> that will generate data for
	 * the indicated address range.
	 * 
	 * @param start			Start of data area.
	 * @param end			End of data area.
	 */
	protected SRecordTarget (int start, int end, int addrSize)
	{
		this (start, end, addrSize, 8);
	}
		
	/**
	 * Constructs a <CODE>HexTarget</CODE> that will generate data for
	 * the indicated address range.
	 * 
	 * @param start			Start of data area.
	 * @param end			End of data area.
	 */
	protected SRecordTarget (long start, long end, int addrSize, int byteSize)
	{
		super (start, end, byteSize);
		
		this.addrSize = addrSize;
	}

	/**
	 * Size of the address field.
	 */
	private int 			addrSize;
}