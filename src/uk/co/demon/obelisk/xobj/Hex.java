/*
 * Copyright (C),2005-2011 Andrew John Jacobs.
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
package uk.co.demon.obelisk.xobj;
 
/**
 * A utility class for hexadecimal string conversion.
 * 
 * @author 	Andrew Jacobs
 */
public abstract class Hex
{
	/**
	 * Converts a value to a Hex string of a given length.
	 * 
	 * @param 	value			The value to convert.
	 * @param 	length			The required length.
	 * @return	The hex string value.
	 */
	public static String toHex (long value, int length)
	{
		buffer.setLength (0);
	
		switch (length) {
		case 8:	buffer.append (HEX.charAt((int)((value >> 28) & 0x0f)));
		case 7:	buffer.append (HEX.charAt((int)((value >> 24) & 0x0f)));
		case 6:	buffer.append (HEX.charAt((int)((value >> 20) & 0x0f)));
		case 5:	buffer.append (HEX.charAt((int)((value >> 16) & 0x0f)));
		case 4:	buffer.append (HEX.charAt((int)((value >> 12) & 0x0f)));
		case 3:	buffer.append (HEX.charAt((int)((value >>  8) & 0x0f)));
		case 2:	buffer.append (HEX.charAt((int)((value >>  4) & 0x0f)));
		case 1:	buffer.append (HEX.charAt((int)((value >>  0) & 0x0f)));
		}
		
		return (buffer.toString ());
	}
	
	/**
	 * Constant string used in hex conversion.
	 */
	private static final String	HEX				= "0123456789ABCDEF";
	
	/**
	 * String buffer used to construct values
	 */
	private static StringBuffer	buffer			= new StringBuffer ();
}