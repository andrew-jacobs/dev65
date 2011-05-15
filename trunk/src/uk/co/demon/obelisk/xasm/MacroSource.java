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

import java.util.Vector;

/**
 * The <CODE>MacroSource</CODE> class provides macro expansion capability
 * to <CODE>TextSource</CODE>.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class MacroSource extends TextSource
{
	/**
	 * Constructs a <CODE>MacroSource</CODE> templace instance.
	 * 
	 * @param 	arguments		The list of argument names.
	 */
	public MacroSource (final Vector arguments)
	{
		this.arguments = arguments;
	}
	
	/**
	 * Creates a copy of a template with values to expand
	 * 
	 * @param 	instance		Macro instance counter.
	 * @param 	values			Argument values.
	 * @return	A copy of the configured <CODE>MacroSource</CODE>.
	 */
	public MacroSource invoke (int instance, final Vector values)
	{
		return (new MacroSource (this, instance, values));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Line nextLine ()
	{
		Line	line = super.nextLine ();
		
		if (line != null) {
			String		text = line.getText ();
			
			// Replace instance number
			text = text.replace ("\\?", Integer.toString (instance));
			
			// Replace names arguments
			for (int index = 0; index < arguments.size (); ++index) {
				String arg = (String) arguments.elementAt (index);
				String val = "";
				
				if (index < values.size ())
					val = (String) values.elementAt (index);
				
				text = text.replace (arg, val);
			}
			
			// Replace numbered arguments 0-9
			for (int index = 0; index <= 9; ++index) {
				String arg = "\\" + Integer.toString (index);
				String val = "";
				
				if (index < values.size ())
					val = (String) values.elementAt (index);
				
				text = text.replace (arg, val);
			}
		
			line = new Line (line.getFileName(), line.getLineNumber (), text);
		}
		return (line);
	}
	
	/**
	 * Constructs a <CODE>MacroSource</CODE> instance ready to be expanded.
	 * 
	 * @param 	template		The template <CODE>MacroSource</CODE>.
	 * @param 	instance		The macro instance counter.
	 * @param 	values			The argument values.
	 */
	protected MacroSource (MacroSource template, int instance, Vector values)
	{
		super (template);
		
		this.arguments = template.arguments;
		this.instance  = instance;
		this.values    = values;
	}
	
	/**
	 * The macro argument names.
	 */
	private Vector			arguments;
	
	/**
	 * The macro instance number.
	 */
	private int				instance;
	
	/**
	 * The macro argument values.
	 */
	private Vector			values;	
}