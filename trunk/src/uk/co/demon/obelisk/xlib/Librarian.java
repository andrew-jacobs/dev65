/*
 * Copyright (C),2005-2007 Andrew John Jacobs.
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

package uk.co.demon.obelisk.xlib;

import java.io.FileOutputStream;
import java.io.PrintStream;

import uk.co.demon.obelisk.xapp.Application;
import uk.co.demon.obelisk.xapp.Option;
import uk.co.demon.obelisk.xobj.Library;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Parser;

/**
 * The librarian application allows multiple object modules to be
 * packaged up in a single library file.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class Librarian extends Application
{
	/**
	 * {@inheritDoc}
	 */
	protected void startUp ()
	{
		super.startUp ();
		
		int optionCount = 0;
		
		if (listOption.isPresent ()) ++optionCount;
		if (createOption.isPresent ()) ++optionCount;
		if (updateOption.isPresent ()) ++optionCount;
		if (removeOption.isPresent ()) ++optionCount;
		
		if (optionCount == 0) {
			error ("No action (create/update/remove/list) was selected");
			setFinished (true);
			return;
		}
		
		if (optionCount > 1) {
			error ("Only one action (create/update/remove/list) can be selected at a time");
			setFinished (true);
			return;
		}
		
		String [] arguments = getArguments ();
		
		if (arguments.length < 1) {
			error ("No library was specified.");
			setFinished (true);
			return;
		}
		libraryName = arguments [0];
		                         
		if (!listOption.isPresent() && (arguments.length == 1)) {
			error ("No object modules specified on the command line");
			setFinished (true);
			return;
		}
		
		if (createOption.isPresent ())
			library = new Library ();
		else {
			try {
				library = (Library) Parser.parse (libraryName);
			}
			catch (Exception error) {
				error ("Serious I/O error while opening library");
				setFinished (true);
				return;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void execute ()
	{
		if (listOption.isPresent ()) {
			list ();
			return;
		}
		
		if (createOption.isPresent ()) create ();
		else if (updateOption.isPresent()) update ();
		else if (removeOption.isPresent()) remove ();
		
		try {
			PrintStream		stream
				= new PrintStream (new FileOutputStream (libraryName));
			
			stream.println ("<?xml version='1.0'?>" + library);
			stream.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: Could not write library");
			System.exit (1);
		}

	
		setFinished (true);
	}

	/**
	 * {@inheritDoc}
	 */
	protected String describeArguments ()
	{
		return ("library [objects ...]");
	}
	
	private Option			createOption
		= new Option ("-create", "Create a library");
	
	private Option			updateOption
		= new Option ("-update", "Update objects in a library");
	
	private Option			removeOption
		= new Option ("-remove", "Removed objects from a library");

	private Option			listOption
		= new Option ("-list", "Lists objects in a library");

	private String			libraryName;
	
	private Library			library;
	
	private void create ()
	{
		String [] arguments = getArguments ();

		for (int index = 1; index < arguments.length; ++index) {
			try {
				Module module = (Module) Parser.parse (arguments [index]);
				library.addModule (module);
				System.out.println ("Added " + arguments [index]);
			}
			catch (Exception error) {
				error ("Could not open module '" + arguments [index] + "'");
			}
		}
	}
	
	private void update ()
	{
		String [] arguments = getArguments ();

		for (int index = 1; index < arguments.length; ++index) {
			try {
				Module module = (Module) Parser.parse (arguments [index]);
				if (library.updateModule (module))
					System.out.println ("Updated " + arguments [index]);
				else
					System.out.println ("Added " + arguments [index]);
			}
			catch (Exception error) {
				error ("Could not open module '" + arguments [index] + "'");
			}
		}		
	}
	
	private void remove ()
	{
		String [] arguments = getArguments ();

		for (int index = 1; index < arguments.length; ++index) {
			try {
				Module module = (Module) Parser.parse (arguments [index]);
				if (library.removeModule (module))
					System.out.println ("Removed " + arguments [index]);
				else
					warning ("Module '" + arguments [index] + "' not found");
			}
			catch (Exception error) {
				error ("Could not open module '" + arguments [index] + "'");
			}
		}
	}
	
	private void list ()
	{
		Module [] modules = library.getModules ();
		
		for (int index = 0; index < modules.length; ++index)
			System.out.println (modules [index].getName());
		
		setFinished (true);
	}
	
	/**
	 * Displays an error message.
	 * 
	 * @param message		The error message.
	 */
	private void error (final String message)
	{
		System.err.print ("Error: ");
		System.err.println (message);
	}
	
	/**
	 * Displays a warning message
	 * 
	 * @param message		The warning message.
	 */
	private void warning (final String message)
	{
		System.err.print ("Warn: ");
		System.err.println (message);
	}
}
