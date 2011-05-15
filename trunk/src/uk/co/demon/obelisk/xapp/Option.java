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

package uk.co.demon.obelisk.xapp;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The <CODE>Option</CODE> class provide a basic command line processing
 * capability. Instances of <CODE>Option</CODE> define the keywords to look
 * for and the presence of associated parameters. The application should pass
 * the entire set of command line strings received from <CODE>main</CODE> to
 * the processing function. The state of any option referenced by the strings
 * is updated and any remaining strings are returned to the caller.
 *
 * @author	Andrew Jacobs
 * @version	$Id$
 */
public final class Option
{
	/**
	 * Constructs a <CODE>Option</CODE> instance for an option that has an
	 * associated parameter value (e.g. -output &lt;file&gt;).
	 *
	 * @param	name			The name of the option (e.g. -output).
	 * @param	description		A description of the options purpose.
	 * @param	parameter		A description of the required parameter or
	 *							<CODE>null</CODE> if none allowed.
	 */
	public Option (final String	name, final String description, final String parameter)
	{
		this.name		 = name;
		this.description = description;
		this.parameter   = parameter;
		
		options.addElement (this);	
	}
	
	/**
	 * Constructs a <CODE>Option</CODE> instance for an option that does not
	 * have a parameter.
	 *
	 * @param	name			The name of the option (e.g. -output).
	 * @param	description		A description of the options purpose.
	 */
	public Option (final String	name, final String description)
	{
		this (name, description, null);
	}
	
	/**
	 * After processing indicates if the <CODE>Option</CODE> was present on
	 * the command line.
	 *
	 * @return	<CODE>true</CODE> if the <CODE>Option</CODE> was present.
	 */
	public final boolean isPresent ()
	{
		return (present);
	}
	
	/**
	 * After processing allows access to the value of provided parameter.
	 *
	 * @return	The value supplied as a parameter or <CODE>null</CODE> if
	 *			the option was not present.
	 */
	public String getValue ()
	{
		return (value);
	}
	
	/**
	 * Converts the instance data members to a <CODE>String</CODE> representation
	 * that can be displayed for debugging purposes.
	 *
	 * @return 	The object's <CODE>String</CODE> representation.
	 */ 
	public String toString ()
	{
		return (getClass ().getName () + "[" + toDebug () + "]");
	}
	
	/**
	 * Processes the command line arguments to extract options and parameter
	 * values. A -help option is automatically added to allow the user to
	 * print out all the available options.
	 *
	 * @param	arguments		The command line arguments passed to <CODE>main</CODE>.
	 * @return	The remaining command line arguments after options have been
	 *			removed.
	 */
	public static String [] processOptions (final String arguments [])
	{
		int				index;
		String			remainder [];
		
		// Attempt to match options with command line
		for (index = 0; index < arguments.length; ++index) {
			Enumeration cursor = options.elements ();
			boolean matched = false;
			
			while (cursor.hasMoreElements ()) {
				Option option = (Option) cursor.nextElement ();
				
				if (matched = arguments [index].equals (option.name)) {
					option.present = true;
					
					if (option.parameter != null)
						option.value = arguments [++index];
					break;
				}
			}
				
			if (!matched) break;			
		}

		// Copy the tail of the argument list to a new array
		remainder = new String [arguments.length - index];
		for (int count = 0; index < arguments.length;)
			remainder [count++] = arguments [index++];
		
		return (remainder);
	}
	
	/**
	 * Returns a string describing the available command line options.
	 * 
	 * @return	A string describing the command line options.
	 */
	public static String listOptions ()
	{
		StringBuffer	buffer = new StringBuffer ();
		Enumeration		cursor = options.elements ();
		
		while (cursor.hasMoreElements ()) {
			Option option = (Option) cursor.nextElement();
			
			if (buffer.length () == 0) buffer.append (' ');
			
			buffer.append ('[');
			buffer.append (option.name);
			if (option.parameter != null) {
				buffer.append (' ');
				buffer.append (option.parameter);
			}
			buffer.append (']');
		}
		
		return (buffer.toString ());
	}
	
	/**
	 * Prints out a description of the options and their parameters.
	 */
	public static void describeOptions ()
	{
		String 		spaces = "                                            ";
		Enumeration cursor = options.elements ();
		
		while (cursor.hasMoreElements ()) {
			Option option = (Option) cursor.nextElement ();
		
			if (option.parameter != null)
				System.err.println ("    "
					+ (option.name + " " + option.parameter + spaces).substring (0, 16)
					+ " " + option.description);
			else
				System.err.println ("    "
					+ (option.name + spaces).substring (0, 16)
					+ " " + option.description);
		}
	}

	/**
	 * Converts the instance's member values to <CODE>String</CODE> representations
	 * and concatenates them all together. This function is used by toString and
	 * may be overridden in derived classes.
	 *
	 * @return	The object's <CODE>String</CODE> representation.
	 */
	protected String toDebug ()
	{
		StringBuffer		buffer 	= new StringBuffer ();
		
		buffer.append ("name="
			+ ((name != null) ? name : "null"));
		buffer.append (",description="
			+ ((description != null) ? description : "null"));
		buffer.append (",parameter="
			+ ((parameter != null) ? parameter : "null"));
		buffer.append (",present=" + present);
		
		return (buffer.toString ());
	}
	
	/**
	 * The set of all defined <CODE>Option</CODE> instances.
	 */
	private static Vector	options		= new Vector ();
	
	/**
	 * The name of the option (including any leading dash).
	 */
	private String			name;
	
	/**
	 * A brief description of the purpose.
	 */
	private String			description;
	
	/**
	 * A brief description of the parameter (if any).
	 */
	private String			parameter;
	
	/**
	 * A flag to indicate that the option was present.
	 */
	private boolean			present;
	
	/**
	 * The actual value provided on the command line.
	 */
	private String			value = null;
}