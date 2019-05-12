/*
 * Copyright (C),2005-2018 Andrew John Jacobs.
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
 * A utility class for octal string conversion.
 * 
 * @author 	Andrew Jacobs
 */
public abstract class Oct
{
	/**
	 * Converts a value to a Hex string of a given length.
	 * 
	 * @param 	value			The value to convert.
	 * @param 	length			The required length.
	 * @return	The hex string value.
	 */
	public static String toOct (long value, int length)
	{
		buffer.setLength (0);
	
		switch (length) {
		case 8:	buffer.append (OCT.charAt((int)((value >> 21) & 0x07)));
		case 7:	buffer.append (OCT.charAt((int)((value >> 18) & 0x07)));
		case 6:	buffer.append (OCT.charAt((int)((value >> 15) & 0x07)));
		case 5:	buffer.append (OCT.charAt((int)((value >> 12) & 0x07)));
		case 4:	buffer.append (OCT.charAt((int)((value >>  9) & 0x07)));
		case 3:	buffer.append (OCT.charAt((int)((value >>  6) & 0x07)));
		case 2:	buffer.append (OCT.charAt((int)((value >>  3) & 0x07)));
		case 1:	buffer.append (OCT.charAt((int)((value >>  0) & 0x07)));
		}
		
		return (buffer.toString ());
	}
	
	/**
	 * Constant string used in octal conversion.
	 */
	private static final String	OCT				= "01234567";
	
	/**
	 * String buffer used to construct values
	 */
	private static StringBuffer	buffer			= new StringBuffer ();
}