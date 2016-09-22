/*
 * Copyright (C),2016 Andrew John Jacobs.
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

package uk.co.demon.obelisk.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public final class Hex2C
{
	/**
	 * @param args
	 */
	public static void main (String [] args)
	{
		new Hex2C ().run ();
	}
	
	protected Hex2C ()
	{
		reader = new BufferedReader (new InputStreamReader (System.in));
		writer = new BufferedWriter (new OutputStreamWriter (System.out));
	}
	
	protected void run ()
	{
		String		line;
		
		try {
			while ((line = reader.readLine ()) != null) {
				for (int index = 0; index < line.length (); index += 2) {
					writer.append ("0x");
					writer.append (line.substring (index, index + 2));
					writer.append (',');
				}
				writer.append ('\n');
			}
			writer.flush ();
		}
		catch (IOException error) {
			System.err.println ("Error while processing file.");
		}
	}

	private BufferedReader		reader;
	
	private BufferedWriter		writer;
}