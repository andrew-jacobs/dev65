/*
 * Copyright (C),2013-2018 Andrew John Jacobs.
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

package uk.co.demon.obelisk.icl430;

import uk.co.demon.obelisk.xlnk.Linker;

/**
 * The <CODE>LkIcl430</CODE> provides access to the base <CODE>Linker</CODE> code
 * for the ICO 4-30 suite.
 *
 * @author 	Andrew Jacobs
 */
public final class LkIcl430 extends Linker
{
	/**
	 * Main program entry point.
	 * 
	 * @param arguments		Command line arguments.
	 */
	public static void main (String arguments [])
	{
		new LkIcl430 ().run (arguments);
	}

	/**
	 * Constructs a <CODE>Lk65</CODE> instance.
	 */
	protected LkIcl430 ()
	{
		super (8);
	}
		
	/**
	 * {@inheritDoc}
	 */
	protected int getAddrSize ()
	{
		return (16);
	}
}