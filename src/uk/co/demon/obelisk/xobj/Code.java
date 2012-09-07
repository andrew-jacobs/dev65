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
 * The <CODE>Code</CODE> class holds a seried of generated bytes.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Code extends Part
{
	/**
	 * Constructs a <CODE>Code</CODE> instance.
	 */
	public Code (final Module module)
	{
		this.module = module;
	}
	
	/**
	 * Adds a byte value to the current code string.
	 * 
	 * @param 	value			The value to add.
	 */
	public void addByte (long value)
	{
		data.append (Hex.toHex (value, module.getByteSize () / 4));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString ()
	{
		return (data.toString());
	}
	
	/**
	 * A buffer containing the code bytes.
	 */
	private StringBuffer	data	= new StringBuffer ();
	
	private final Module	module;
}