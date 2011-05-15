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

package uk.co.demon.obelisk.xlnk;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import uk.co.demon.obelisk.xapp.Application;
import uk.co.demon.obelisk.xapp.Option;
import uk.co.demon.obelisk.xobj.BinaryExpr;
import uk.co.demon.obelisk.xobj.Byte;
import uk.co.demon.obelisk.xobj.Code;
import uk.co.demon.obelisk.xobj.Evaluatable;
import uk.co.demon.obelisk.xobj.Expr;
import uk.co.demon.obelisk.xobj.Extern;
import uk.co.demon.obelisk.xobj.Hex;
import uk.co.demon.obelisk.xobj.Library;
import uk.co.demon.obelisk.xobj.Long;
import uk.co.demon.obelisk.xobj.Module;
import uk.co.demon.obelisk.xobj.Parser;
import uk.co.demon.obelisk.xobj.Part;
import uk.co.demon.obelisk.xobj.Section;
import uk.co.demon.obelisk.xobj.SectionMap;
import uk.co.demon.obelisk.xobj.SymbolMap;
import uk.co.demon.obelisk.xobj.UnaryExpr;
import uk.co.demon.obelisk.xobj.Word;

/**
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class Linker extends Application
{
	/**
	 * {@inheritDoc}
	 */
	protected void startUp ()
	{
		super.startUp ();
		
		createAreas ();
		
		if (!hex.isPresent() && !bin.isPresent() && !s19.isPresent()) {
			error ("No output format selected (-bin, -hex or -s19).");
			setFinished (true);
			return;
		}

		if ((hex.isPresent () && bin.isPresent ()) ||
			(bin.isPresent () && s19.isPresent ()) ||
			(s19.isPresent () && hex.isPresent ())) {
			error ("Only one output format can be selected at a time.");
			setFinished (true);
		}
		else {
			Area		area;
			int			hi	= Integer.MIN_VALUE;
			int			lo	= Integer.MAX_VALUE;
			
			if ((area = (Area) areas.get (".code")) != null) {
				if (area.getHiAddr() > hi) hi = area.getHiAddr ();
				if (area.getLoAddr() < lo) lo = area.getLoAddr ();
			}
			if ((area = (Area) areas.get (".data")) != null) {
				if (area.getHiAddr() > hi) hi = area.getHiAddr ();
				if (area.getLoAddr() < lo) lo = area.getLoAddr ();
			}
			
			if (hex.isPresent ())
				target = new HexTarget (lo, hi);
			else if (bin.isPresent ())
				target = new BinTarget (lo, hi);
			else if (s19.isPresent ())
				target = new S19Target (lo, hi, getAddrSize ());
		}
		
		if (getArguments ().length == 0) {
			System.err.println ("Error: No object or library files specified");
			setFinished (true);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void execute ()
	{
		String []		arguments = getArguments();
		
		// Stage I - Load all the modules and libraries
		for (int index = 0; index < arguments.length; ++index) {
			if (arguments [index].endsWith (".obj")) {
				Object object = Parser.parse (arguments [index]);
	
//	System.err.println ("Parsed: " + object);
	
				if ((object != null) && (object instanceof Module)) {
					if (!modules.contains (object))
						modules.add (object);
					else
						warning ("Module '" + arguments [index] + "' specified more than once");
				}
				else {
					error ("Invalid object file '" + arguments [index] + "'");
					setFinished (true);
				}
			}
			else if (arguments [index].endsWith (".lib")) {
				Object object = Parser.parse (arguments [index]);
				
				if ((object != null) && (object instanceof Library)) {
					if (!libraries.contains (object))
						libraries.add (object);
					else
						warning ("Library '" + arguments [index] + "' specified more than once");
				}
				else {
					error ("Invalid library file '" + arguments [index] + "'");
					setFinished (true);
				}
			}
			else {
				error ("Unrecognized file type for '" + arguments [index] + "'");
				setFinished (true);
			}
		}
		
		if (this.isFinished ()) return;
		
		// Stage II - process all the modules that must be linked
		for (int index = 0; index < modules.size (); ++index)
			processModule ((Module) modules.elementAt (index));
				
		// Stage III - process libaries for any required modules
		int		moduleCount;
		do {
			moduleCount = modules.size ();
			for (int index = 0; index < libraries.size(); ++index)
				processLibrary ((Library) libraries.elementAt (index)); 
		} while (modules.size() != moduleCount);
		
		if (refs.size() > 0) {
			Enumeration cursor = refs.keys();
			while (cursor.hasMoreElements ()) {
				String key 		= (String) cursor.nextElement ();
				Module module 	= (Module) refs.get (key); 
				error ("Undefined symbol: " + key + " in " + module.getName());
			}
			
			setFinished (true);
			return;
		}

		// Stage IV - Sort sections by type and size
		for (int index = 0; index < modules.size (); ++index) {
			Vector sections = ((Module) modules.elementAt (index)).getSections();
			
			for (int count = 0; count < sections.size (); ++count) {
				Section section = (Section) sections.elementAt (count);
				
				Vector vec = section.isAbsolute () ? abs : rel;
	
				boolean handled = false;
				for (int position = 0; position < vec.size (); ++position) {
					Section	other = (Section) vec.elementAt (position);
					
					if (other.getSize () < section.getSize()) {
						vec.insertElementAt(section, position);
						handled = true;
						break;
					}
				}
				if (!handled) vec.add (section);
			}
		}
		
		// Stage V - Fit sections into available memory
		for (int index = 0; index < abs.size (); ++index) {
			Section		section = (Section) abs.elementAt (index);
			int 		base 	= fitSection (section);
		
			if (base == -1) return;
			sectionMap.setBaseAddress (section, base);
		}

		for (int index = 0; index < rel.size (); ++index) {
			Section		section = (Section) rel.elementAt (index);
			int 		base 	= fitSection (section);
		
			if (base == -1) return;
			sectionMap.setBaseAddress (section, base);
		}
		
		// Stage VI - Calculate all the global symbol addresses
		for (int index = 0; index < modules.size (); ++index) {
			Module 		module = (Module) modules.elementAt (index);
			Vector		globals = module.getGlobals ();
			
			for (int count = 0; count < globals.size (); ++count) {
				String 	symbol 	= (String) globals.elementAt (count);
				Expr	expr	= module.getGlobal (symbol);
				
				symbolMap.addAddress (symbol, expr.resolve (sectionMap, symbolMap));
			}
		}
		
		// Stage VII - Copy code to target fixing cross references
		for (int index = 0; index < modules.size (); ++index)
			fixUp ((Module) modules.elementAt (index));	
		
		// Figure out output filenames
		File		objectFile;
		File		mapFile;
		
		if (output.isPresent ()) {
			String		filename = output.getValue ();
			
			objectFile = new File (filename);
			mapFile = new File (filename.substring (0, filename.lastIndexOf ('.')) + ".map");
		}
		else {
			String		filename = getArguments ()[0];

			if (hex.isPresent ())
				objectFile = new File (filename.substring (0, filename.lastIndexOf ('.')) + ".hex");
			else
				objectFile = new File (filename.substring (0, filename.lastIndexOf ('.')) + ".bin");

			mapFile = new File (filename.substring (0, filename.lastIndexOf ('.')) + ".map");
		}
		
		writeMap (mapFile);
		target.writeTo (objectFile);
		
		setFinished (true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void cleanUp ()
	{
		if (errors > 0) System.exit (1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected String describeArguments ()
	{
		return (" <object/library file> ...");
	}
	
	/**
	 * Displays an error message.
	 * 
	 * @param message		The error message.
	 */
	protected void error (final String message)
	{
		System.err.print ("Error: ");
		System.err.println (message);
		
		++errors;
	}
	
	/**
	 * Displays a warning message
	 * 
	 * @param message		The warning message.
	 */
	protected void warning (final String message)
	{
		System.err.print ("Warn: ");
		System.err.println (message);
	}
	
	/**
	 * Creates the areas for default sections.
	 */
	protected void createAreas ()
	{
		// Created real areas
		if (code.getValue() != null) {
			if (code.getValue().contains ("-"))
				addArea (".code", code.getValue ());
		}
		if (data.getValue() != null) {
			if (data.getValue ().contains("-"))
				addArea (".data", data.getValue ());
		}
		if (bss.getValue()  != null) {
			if (bss.getValue ().contains("-"))
				addArea (".bss",  bss.getValue ());
		}

		// If no data area defined alias it to the code.
		if (!areas.containsKey(".data"))
			areas.put (".data", areas.get(".code"));
	}
	
	/**
	 * Creates and adds a named area to the area collection.
	 * 
	 * @param name			The area name.
	 * @param location		Its location definition.
	 */
	protected final void addArea (final String name, final String location)
	{
		Area			area = new Area (location);
		
		areas.put (name, area);
	}
	
	/**
	 * Gets the size of target memory addresses as a number of bits
	 * (e.g. 16, 24 or 32).
	 * 
	 * @return	The target memory address size.
	 */
	protected abstract int getAddrSize ();

	/**
	 * Option for specifiying target code areas.
	 */
	private Option			code
		= new Option ("-code", "Code region(s)", "<regions>");
	
	/**
	 * Option for specifiying target data areas.
	 */
	private Option			data
		= new Option ("-data", "Data region(s)", "<regions>");

	/**
	 * Option for specifiying target bss areas.
	 */
	private Option			bss
		= new Option ("-bss", "BSS region(s)", "<regions>");
	
	/**
	 * Option for specifiying hex output format.
	 */
	private Option			hex
		= new Option ("-hex", "Generate HEX output");

	/**
	 * Option for specifiying binary output format.
	 */
	private Option			bin
		= new Option ("-bin", "Generate binary output");

	/**
	 * Option for specifiying Motorola S19 output format.
	 */
	private Option			s19
		= new Option ("-s19", "Generate Motorola S19 output");

	/**
	 * Option for specifiying output file.
	 */
	private Option			output
		= new Option ("-output", "Output file", "<file>");

	/**
	 * The set of modules to be linked.
	 */
	private Vector			modules		= new Vector ();
	
	/**
	 * Library modules that can be scanned.
	 */
	private Vector			libraries	= new Vector ();
	
	/**
	 * Symbols yet to be defined and the originating module.
	 */
	private Hashtable		refs		= new Hashtable ();
	
	/**
	 * Defined symbols and the module that defines them.
	 */
	private Hashtable		defs		= new Hashtable ();

	/**
	 * The set of absolute sections in size order (biggest first)
	 */
	private Vector			abs 		= new Vector ();
	
	/**
	 * The set of relative sections in size order (biggest first)
	 */
	private Vector			rel 		= new Vector ();
	
	/**
	 * The .CODE, .DATA and .BSS memory areas
	 */
	private Hashtable		areas		= new Hashtable ();
	
	private SectionMap		sectionMap 	= new SectionMap ();
	
	private SymbolMap		symbolMap	= new SymbolMap ();
	
	private Target			target;
	
	private int				errors		= 0;
	
	/**
	 * Process a code module.
	 * 
	 * @param module
	 */
	private void processModule (Module module)
	{
		Vector			sections = module.getSections ();

		// Look for symbol references
		for (int index = 0; index < sections.size(); ++index) {
			Section			section = (Section) sections.elementAt (index);
	
			Vector			parts = section.getParts ();
			for (int count = 0; count < parts.size (); ++count) {
				Part			part = (Part) parts.elementAt (count);
				
				if (part instanceof Evaluatable)
					processExpression (((Evaluatable) part).getExpr (), module);
			}
		}
		
		// Process symbol definitions
		Vector			globals = module.getGlobals ();
		for (int index = 0; index < globals.size (); ++index) {
			String			symbol = (String) globals.elementAt (index);
			
			if (!defs.containsKey (symbol)) {
				defs.put (symbol, module);
				refs.remove (symbol);
			}
			else {
				error ("Redefinition of symbol '" + symbol + "' in module '" + module.getName() + "'");
				setFinished (true);
			}
		}
	}
	
	private void processLibrary (Library library)
	{
		Module []		modules	= library.getModules();
		
		// Process each module in the library
		for (int count = 0; count < modules.length; ++count) {
			Module			module 	= modules [count];
			Vector			globals = module.getGlobals();
			
			// Looking for globals that match referenced symbols
			for (int index = 0; index < globals.size (); ++index) {
				String			symbol = (String) globals.elementAt (index);

				if (refs.containsKey (symbol)) {
					this.modules.add (module);
					processModule (module);
					break;
				}
			}
		}
	}
	
	private void processExpression (Expr expr, Module module)
	{
		if (expr instanceof Extern) {
			String		name = ((Extern) expr).getName ();

			if (!refs.containsKey (name))
				refs.put (name, module);
		}
		else if (expr instanceof UnaryExpr)
			processExpression (((UnaryExpr) expr).getExp (), module);
		else if (expr instanceof BinaryExpr) {
			processExpression (((BinaryExpr) expr).getLhs (), module);
			processExpression (((BinaryExpr) expr).getRhs (), module);
		}
	}
	
	private int fitSection (Section section)
	{
		Area		area = (Area) areas.get (section.getName());
		
		if (area == null) {
			error ("No memory area has been allocated for " + section.getName());
			setFinished (true);
			return (-1);
		}
		
		return (area.fitSection (section));
	}
	
	private void fixUp (Module module)
	{
		Vector 		sections = module.getSections();
		
		for (int index = 0; index < sections.size (); ++index) {
			Section		section = (Section) sections.elementAt (index);
			Vector		parts	= section.getParts();
			int			addr 	= sectionMap.baseAddressOf (section);
			
			for (int count = 0; count < parts.size (); ++count) {
				Part 		part = (Part) parts.elementAt(count);
				
				if (part instanceof Code) {
					String		value = part.toString();
			
					for (int digit = 0; digit < value.length (); digit += 2) {
						int hi = "0123456789ABCDEF".indexOf (value.charAt (digit + 0));
						int lo = "0123456789ABCDEF".indexOf (value.charAt (digit + 1));
						
						target.store (addr, (hi << 4) | lo);
						++addr;
					}
				}
				else if (part instanceof Evaluatable) {
					Expr expr = ((Evaluatable) part).getExpr ();
					int	 value = expr.resolve (sectionMap, symbolMap);
					
					if (part instanceof Byte) {
						target.store (addr, value);
						addr += 1;
					}
					else if (part instanceof Word) {
						storeWord (addr, value, module.isBigEndian ());
						addr += 2;
					}
					else if (part instanceof Long) {
						storeLong (addr, value, module.isBigEndian ());
						addr += 3;
					}
				}
			}
		}
	}
	
	private void storeWord (int addr, int value, boolean bigEndian)
	{
		if (bigEndian) {
			target.store (addr + 0, (value & 0xff00) >> 8);
			target.store (addr + 1, (value & 0x00ff) >> 0);
		}
		else {
			target.store (addr + 1, (value & 0xff00) >> 8);
			target.store (addr + 0, (value & 0x00ff) >> 0);
		}
	}

	private void storeLong (int addr, int value, boolean bigEndian)
	{
		if (bigEndian) {
			target.store (addr + 0, (value & 0xff000000) >> 24);
			target.store (addr + 1, (value & 0x00ff0000) >> 16);
			target.store (addr + 2, (value & 0x0000ff00) >>  8);
			target.store (addr + 3, (value & 0x000000ff) >>  0);
		}
		else {
			target.store (addr + 3, (value & 0xff000000) >> 24);
			target.store (addr + 2, (value & 0x00ff0000) >> 16);
			target.store (addr + 1, (value & 0x0000ff00) >>  8);
			target.store (addr + 0, (value & 0x000000ff) >>  0);
		}
	}
	
	/**
	 * Writes a sorted list of symbols and address to the map file.
	 * 
	 * @param 	file			The <CODE>File</CODE> to write the map to.
	 */
	private void writeMap (File file)
	{
		try {
			PrintWriter		writer = new PrintWriter (file);
			
			writer.println ("Symbol Map\n");
			
			Object [] symbols = symbolMap.getSymbols ().toArray();
			Arrays.sort (symbols);
			
			for (int index = 0; index < symbols.length; ++index) {
				String symbol = (String) symbols [index];
				writer.println (symbol + "  " + Hex.toHex (symbolMap.addressOf(symbol), 8));
			}

			writer.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: A serious error occurred while writing the map file");
		}
	}
}