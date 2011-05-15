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
 * The <CODE>RepeatSource</CODE> holds a collection of lines that will
 * be repeated while the count is not zero.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class RepeatSource extends TextSource
{
	/**
	 * Constructs a <CODE>RepeatSource</CODE> instance.
	 * 
	 * @param 	count			The number of times to repeat.
	 */
	public RepeatSource (int count)
	{
		this.count = count;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Line nextLine ()
	{
		while (count > 0) {
			Line line = super.nextLine ();
		
			if (line != null) return (line);
			
			--count;
			reset ();
		}
		return (null);
	}
	
	/**
	 * The number of times to repeat
	 */
	private int			count;
}