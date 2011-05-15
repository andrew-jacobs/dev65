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
 * Instances of the <CODE>Pass</CODE> class are used to indicate the phase the
 * assembler is in.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Pass
{
	/**
	 * A static instance used to indicate the FIRST pass.
	 */
	public static final Pass		FIRST
		= new Pass (1);
	
	/**
	 * A static instance used to indicate the INTERMEDIATE pass.
	 */
	public static final Pass		INTERMEDIATE
		= new Pass (2);
	
	/**
	 * A static instance used to indicate the FINAL pass.
	 */
	public static final Pass		FINAL
		= new Pass (3);
	
	/**
	 * Returns a number representing the pass.
	 * 
	 * @return	The pass number.
	 */
	public int getNumber ()
	{
		return (number);
	}
	
	/**
	 * The pass number.
	 */
	private final int			number;

	/**
	 * Constructs a <CODE>Pass</CODE> instance.
	 * 
	 * @param 	number			The pass number.
	 */
	private Pass (int number)
	{
		this.number = number;
	}
}