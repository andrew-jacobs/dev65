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

/**
 * The <CODE>Application</CODE> class provides a basic application framework
 * suitable for any program. Derived classes extend its functionality and
 * specialise it to a particular task.
 *
 * @author	Andrew Jacobs
 * @version	$Id$
 */
public abstract class Application
{
	/**
	 * Returns the current <CODE>Application</CODE> instance.
	 *
	 * @return	The <CODE>Application</CODE> instance.
	 */
	public static Application getApplication ()
	{
		return (application);
	}

	/**
	 * Causes the <CODE>Application</CODE> to process it's command line arguments
	 * and begin the execution cycle.
	 *
	 * @param	arguments		The array of command line arguments.
	 */
	public void run (
	String			arguments [])
	{
		this.arguments = Option.processOptions (arguments);
		
		startUp ();
		while (!finished)
			execute ();
		cleanUp ();
	}

	/**
	 * Provides access to the flag used to determine <CODE>Application</CODE>
	 * execution.
	 *
	 * @return	<CODE>true</CODE> if execution is finished, <CODE>false</CODE>
	 *			otherwise.
	 */
	public final boolean isFinished ()
	{
		return (finished);
	}

	/**
	 * Allows the caller to change the value of the finished flag.
	 *
	 * @param	finished			The new value for the flag.
	 */
	public final void setFinished (final boolean finished)
	{
		this.finished = finished;
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
	 * Constructs an <CODE>Application</CODE> instance and records it.
	 */
	protected Application ()
	{
		application = this;
	}

	/**
	 * Provides an <CODE>Application</CODE> with a chance to perform any
	 * initialisation. This implementation initialises the system and user
	 * preferences. Derived classes may extend the functionality.
	 */
	protected void startUp ()
	{
		if (helpOption.isPresent ()) {
			System.err.println ("Usage:\n    java " + this.getClass ().getName ()
					+ Option.listOptions () + describeArguments ());
			System.err.println ();
			System.err.println ("Options:");
			Option.describeOptions ();
			System.exit (1);
		}
	}

	/**
	 * The <CODE>execute</CODE> method should perform one program execution
	 * cycle. The method is called repeatedly until the finished flag is set.
	 */
	protected abstract void execute ();

	/**
	 * Provides an <CODE>Application</CODE> with a chance to perform any
	 * closing actions. This implementation ensures that preference settings
	 * are flushed. Derived classes may extend the functionality.
	 */
	protected void cleanUp ()
	{ }
	
	/**
	 * Provides a text description of the arguments expected after the options
	 * (if any), for example "file ...". This method should be overridden in a
	 * derived class requiring a non-empty argument list.
	 * 
	 * @return	A description of the expected application arguments.
	 */
	protected String describeArguments ()
	{
		return ("");
	}

	/**
	 * Provides access to the command line arguments after any processing
	 * has been applied.
	 *
	 * @return	The command line arguments.
	 */
	protected final String [] getArguments ()
	{
		return (arguments);
	}

	/**
	 * Returns the path for the system preferences. If the value is <CODE>null
	 * </CODE> (as in this default implementation) then no preferences will be
	 * initialised.
	 *
	 * @return	The path name for system preferences, <CODE>null</CODE> if none
	 * 			are required.
	 */
	protected String getSystemPreferencesRoot ()
	{
		return (null);
	}

	/**
	 * Returns the path for the user preferences. If the value is <CODE>null
	 * </CODE> then no preferences will be initialised. The default
	 * implementation is to copy the system references path.
	 *
	 * @return	The path name for user preferences, <CODE>null</CODE> if none
	 * 			are required.
	 */
	protected String getUserPreferencesRoot ()
	{
		return (getSystemPreferencesRoot ());
	}
	
	/**
	 * Converts the instance's member values to <CODE>String</CODE> representations
	 * and concatenates them all together. This function is used by toString and
	 * may be overriden in derived classes.
	 *
	 * @return	The object's <CODE>String</CODE> representation.
	 */
	protected String toDebug ()
	{
		StringBuffer		buffer 	= new StringBuffer ();
		
		buffer.append ("arguments=");
		if (arguments != null) {
			buffer.append ('[');
			for (int index = 0; index != arguments.length; ++index) {
				if (index != 0) buffer.append (',');
				
				if (arguments [index] != null)
					buffer.append ("\"" + arguments [index]+ "\"");
				else
					buffer.append ("null");
			}
			buffer.append (']');
		}
		else
			buffer.append ("null");
			
		buffer.append (",finished=" + finished);
		
		return (buffer.toString ());
	}

	/**
	 * The one and only <CODE>Application</CODE> instance.
	 */
	private static Application	application 		= null;

	/**
	 * The <CODE>Option</CODE> instance use to detect <CODE>-help</CODE>
	 */
	private Option				helpOption
		= new Option ("-help",	"Displays help information");

	/**
	 * The command line arguments after processing.
	 */
	private String				arguments []		= null;
	
	/**
	 * A <CODE>boolean</CODE> flag to indicate that we are done
	 */
	private boolean				finished 			= false;
}