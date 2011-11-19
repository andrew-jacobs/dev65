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

package uk.co.demon.obelisk.xasm;

import java.util.Vector;

/**
 * The <CODE>TextSource</CODE> class provides the storage for text held
 * for either macros or repeat sections.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class TextSource implements Source
{
	/**
	 * Add a <CODE>Line</CODE> to the collection managed by this instance.
	 * 
	 * @param 	line			The <CODE>Line</CODE> to be added.
	 */
	public void addLine (final Line line)
	{
		lines.add (line);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Line nextLine ()
	{
		if (nextLine < lines.size ())
			return (lines.elementAt (nextLine++));
		
		return (null);
	}
	
	/**
	 * Constructs a <CODE>TextSource</CODE> instance.
	 */
	protected TextSource ()
	{
		reset ();
	}
	
	/**
	 * Constructs a <CODE>TextSource</CODE>  instance by copying a template.
	 * 
	 * @param 	template		The template instance.
	 */
	protected TextSource (TextSource template)
	{
		lines = template.lines;
		
		reset ();
	}
	
	/**
	 * Repositions the next line marker to the start if the collection
	 */
	protected void reset ()
	{
		nextLine = 0;
	}
	
	/**
	 * A <CODE>Vector</CODE> of stored source lines.
	 */
	private Vector<Line>	lines		= new Vector<Line> ();
	
	/**
	 * The index of the next line to be read.
	 */
	private int				nextLine;
}