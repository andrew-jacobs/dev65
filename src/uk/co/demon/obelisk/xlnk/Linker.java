/*
 * Copyright (C),2005-2020 Andrew John Jacobs.
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
 * The <CODE>Linker</CODE> class implements the framework for a generic
 * linker customised by derived classes to match specific processors.
 *
 * @author 	Andrew Jacobs
 */
public abstract class Linker extends Application
{
	protected Linker (int byteSize)
	{
		this.byteSize = byteSize;
		
		byteMask = (1L << byteSize) - 1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void startUp ()
	{
		super.startUp ();
		
		createAreas ();
		
		int 	count = 0;
		if (hex.isPresent ()) ++count;
		if (ihx.isPresent ()) ++count;
		if (bin.isPresent ()) ++count;
		if (wdc.isPresent ()) ++count;
		if (s19.isPresent ()) ++count;
		if (s28.isPresent ()) ++count;
		if (s37.isPresent ()) ++count;
		if (dmp.isPresent ()) ++count;
		if (cdo.isPresent ()) ++count;
		
		if (count == 0) {
			error ("No output format selected (-bin, -hex, -ihx, -s19, -dmp, -c, -wdc, -s28 or -s37).");
			setFinished (true);
			return;
		}
		else if (count > 1) {
			error ("Only one output format can be selected at a time.");
			setFinished (true);
		}
		else {
			Area		area;
			long		hi	= 0x00000000L;
			long		lo	= 0xffffffffL;
			
			if ((area = (Area) areas.get (".code")) != null) {
				if (area.getHiAddr() > hi) hi = area.getHiAddr ();
				if (area.getLoAddr() < lo) lo = area.getLoAddr ();
			}
			if ((area = (Area) areas.get (".data")) != null) {
				if (area.getHiAddr() > hi) hi = area.getHiAddr ();
				if (area.getLoAddr() < lo) lo = area.getLoAddr ();
			}
			
			if (hex.isPresent ())
				target = new HexTarget (lo, hi, byteSize);
			else if (ihx.isPresent ())
				target = new IntelHexTarget (lo, hi, byteSize);
			else if (bin.isPresent ())
				target = new BinTarget (lo, hi, byteSize);
			else if (wdc.isPresent ())
				target = new WDCTarget (lo, hi, byteSize);
			else if (s19.isPresent ())
				target = new S19Target (lo, hi, byteSize);
			else if (s28.isPresent ())
				target = new S28Target (lo, hi, byteSize);
			else if (s37.isPresent ())
				target = new S37Target (lo, hi, byteSize);
			else if (dmp.isPresent ())
				target = new DumpTarget (lo, hi, byteSize);
			else if (cdo.isPresent ())
				target = new CTarget (lo, hi, byteSize);
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
	
				if ((object != null) && (object instanceof Module)) {
					if (!modules.contains (object))
						modules.add ((Module) object);
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
						libraries.add ((Library) object);
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
			processModule (modules.elementAt (index));
				
		// Stage III - process libraries for any required modules
		int		moduleCount;
		do {
			moduleCount = modules.size ();
			for (int index = 0; index < libraries.size(); ++index)
				processLibrary (libraries.elementAt (index)); 
		} while (modules.size() != moduleCount);
		
		if (refs.size() > 0) {
			Enumeration<String> cursor = refs.keys();
			while (cursor.hasMoreElements ()) {
				String key 		= cursor.nextElement ();
				Module module 	= refs.get (key); 
				error ("Undefined symbol: " + key + " in " + module.getName());
			}
			
			setFinished (true);
			return;
		}

		// Stage IV - Sort sections by type and size
		for (int index = 0; index < modules.size (); ++index) {
			Vector<Section> sections = modules.elementAt (index).getSections();
			
			for (int count = 0; count < sections.size (); ++count) {
				Section section = sections.elementAt (count);
				
				Vector<Section> vec = section.isAbsolute () ? abs : rel;
	
				boolean handled = false;
				for (int position = 0; position < vec.size (); ++position) {
					Section	other = vec.elementAt (position);
					
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
			Section		section = abs.elementAt (index);
			long 		base 	= fitSection (section);
		
			if (base == -1) return;
			sectionMap.setBaseAddress (section, base);
		}

		for (int index = 0; index < rel.size (); ++index) {
			Section		section = rel.elementAt (index);
			long 		base 	= fitSection (section);
		
			if (base == -1) {
				error ("Failed to fit section '" + section.getName() + "' in module '" + section.getModule().getName () + "'");
				setFinished (true);
				return;
			}
			sectionMap.setBaseAddress (section, base);
		}
		
		// Stage VI - Calculate all the global symbol addresses
		for (int index = 0; index < modules.size (); ++index) {
			Module 		module = modules.elementAt (index);
			Vector<String> globals = module.getGlobals ();
			
			for (int count = 0; count < globals.size (); ++count) {
				String 	symbol 	= globals.elementAt (count);
				Expr	expr	= module.getGlobal (symbol);
				
				symbolMap.addAddress (symbol, expr.resolve (sectionMap, symbolMap));
			}
		}
		
		// Stage VII - Copy code to target fixing cross references
		for (int index = 0; index < modules.size (); ++index)
			fixUp (modules.elementAt (index));	
		
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
	 * The number of bits in a byte.
	 */
	private final int byteSize;
	
	/**
	 * A mask pattern used to strip a long down to a byte value.
	 */
	private final long byteMask;

	/**
	 * Option for specifying target code areas.
	 */
	private Option			code
		= new Option ("-code", "Code region(s)", "<regions>");
	
	/**
	 * Option for specifying target data areas.
	 */
	private Option			data
		= new Option ("-data", "Data region(s)", "<regions>");

	/**
	 * Option for specifying target bss areas.
	 */
	private Option			bss
		= new Option ("-bss", "BSS region(s)", "<regions>");
	
	/**
	 * Option for specifying hex output format.
	 */
	private Option			hex
		= new Option ("-hex", "Generate HEX output");

	/**
	 * Option for specifying Intel hex output format.
	 */
	private Option			ihx
		= new Option ("-ihx", "Generate Intel HEX output");

	/**
	 * Option for specifying binary output format.
	 */
	private Option			bin
		= new Option ("-bin", "Generate binary output");

	/**
	 * Option for specifying WDC binary output format.
	 */
	private Option			wdc
		= new Option ("-wdc", "Generate WDC binary output");

	/**
	 * Option for specifying Motorola S19 output format.
	 */
	private Option			s19
		= new Option ("-s19", "Generate Motorola S19 output");

	/**
	 * Option for specifying Motorola S28 output format.
	 */
	private Option			s28
		= new Option ("-s28", "Generate Motorola S28 output");

	/**
	 * Option for specifying Motorola S37 output format.
	 */
	private Option			s37
		= new Option ("-s37", "Generate Motorola S37 output");

	/**
	 * Option for specifying Dump HEX output format.
	 */
	private Option			dmp
		= new Option ("-dmp", "Generate Dump HEX output");

	/**
	 * Option for specifying C data output format.
	 */
	private Option			cdo
		= new Option ("-c", "Generate C data output");

	/**
	 * Option for specifying output file.
	 */
	private Option			output
		= new Option ("-output", "Output file", "<file>");

	/**
	 * The set of modules to be linked.
	 */
	private Vector<Module>		modules
		= new Vector<Module> ();
	
	/**
	 * Library modules that can be scanned.
	 */
	private Vector<Library>		libraries
		= new Vector<Library> ();
	
	/**
	 * Symbols yet to be defined and the originating module.
	 */
	private Hashtable<String, Module> refs
		= new Hashtable<String, Module> ();
	
	/**
	 * Defined symbols and the module that defines them.
	 */
	private Hashtable<String, Module> defs
		= new Hashtable<String, Module> ();

	/**
	 * The set of absolute sections in size order (biggest first)
	 */
	private Vector<Section>		abs
		= new Vector<Section> ();
	
	/**
	 * The set of relative sections in size order (biggest first)
	 */
	private Vector<Section>		rel
		= new Vector<Section> ();
	
	/**
	 * The .CODE, .DATA and .BSS memory areas
	 */
	private Hashtable<String, Area>	areas
		= new Hashtable<String, Area> ();
	
	/**
	 * The section map for the program being linked.
	 */
	private SectionMap		sectionMap 	= new SectionMap ();
	
	/**
	 * The symbol map for the program being linked.
	 */
	private SymbolMap		symbolMap	= new SymbolMap ();
	
	/**
	 * The <CODE>Target</CODE> class instance used to create the output file.
	 */
	private Target			target;
	
	/**
	 * The number of error encountered during linking.
	 */
	private int				errors		= 0;
	
	/**
	 * Process a code module looking for symbol references and definitions.
	 * 
	 * @param module		The <CODE>Module</CODE> to be processed.
	 */
	private void processModule (Module module)
	{
		Vector<Section>	sections = module.getSections ();

		// Look for symbol references
		for (int index = 0; index < sections.size(); ++index) {
			Section			section = sections.elementAt (index);
	
			Vector<Part>		parts = section.getParts ();
			for (int count = 0; count < parts.size (); ++count) {
				Part			part = (Part) parts.elementAt (count);
				
				if (part instanceof Evaluatable)
					processExpression (((Evaluatable) part).getExpr (), module);
			}
		}
		
		// Process symbol definitions
		Vector<String>	globals = module.getGlobals ();
		for (int index = 0; index < globals.size (); ++index) {
			String			symbol = globals.elementAt (index);
			
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
	
	/**
	 * Process a library looking for modules that define symbols referenced
	 * else where.
	 * 
	 * @param library		The <CODE>Library</CODE> to be processed.	
	 */
	private void processLibrary (Library library)
	{
		Module []		modules	= library.getModules();
		
		// Process each module in the library
		for (int count = 0; count < modules.length; ++count) {
			Module			module 	= modules [count];
			Vector<String>	globals = module.getGlobals();
			
			// Ignore modules that have already been tagged
			if (!this.modules.contains (module)) {
				// Looking for globals that match referenced symbols
				for (int index = 0; index < globals.size (); ++index) {
					String			symbol = globals.elementAt (index);
	
					if (refs.containsKey (symbol)) {
						this.modules.add (module);
						processModule (module);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Process an expression looking for external symbol references.
	 * 
	 * @param expr			The <CODE>Expr</CODE> to process.
	 * @param module		The containing <CODE>Module</CODE>.
	 */
	private void processExpression (Expr expr, Module module)
	{
		if (expr instanceof Extern) {
			String		name = ((Extern) expr).getName ();

			if (!refs.containsKey (name) && !defs.containsKey (name))
				refs.put (name, module);
		}
		else if (expr instanceof UnaryExpr)
			processExpression (((UnaryExpr) expr).getExp (), module);
		else if (expr instanceof BinaryExpr) {
			processExpression (((BinaryExpr) expr).getLhs (), module);
			processExpression (((BinaryExpr) expr).getRhs (), module);
		}
	}
	
	/**
	 * Attempts to fit a <CODE>Section</CODE> within an appropriate memory
	 * <CODE>Area</CODE>.
	 * 
	 * @param section		The <CODE>Section</CODE> to be fitted.
	 * @return The address where the <CODE>Section</CODE> was placed.
	 */
	private long fitSection (Section section)
	{
		Area		area = (Area) areas.get (section.getName());
		
		if (area == null) {
			error ("No memory area has been allocated for " + section.getName());
			setFinished (true);
			return (-1);
		}
		
		return (area.fitSection (section));
	}
	
	/**
	 * Re-processes a <CODE>Module</CODE> to fix up any expressions that depend
	 * on external symbols.
	 * 
	 * @param module		The <CODE>Module</CODE> to be processed.
	 */
	private void fixUp (Module module)
	{
		Vector<Section>	sections = module.getSections();
		
		for (int index = 0; index < sections.size (); ++index) {
			Section		section = sections.elementAt (index);
			Vector<Part> parts	= section.getParts();
			long		addr 	= sectionMap.baseAddressOf (section);
			
			for (int count = 0; count < parts.size (); ++count) {
				Part 		part = parts.elementAt(count);
				
				if (part instanceof Code) {
					String		value = part.toString();
					int			span  = module.getByteSize () / 4;
			
					for (int digit = 0; digit < value.length (); digit += span) {						
						target.store (addr,
								Integer.parseInt (value.substring (digit, digit + span), 16));
						++addr;
					}
				}
				else if (part instanceof Evaluatable) {
					Expr expr = ((Evaluatable) part).getExpr ();
					long value = expr.resolve (sectionMap, symbolMap);
					
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
	
	/**
	 * Stores a word value in the <CODE>Target</CODE> in an appropriate
	 * byte order.
	 * 
	 * @param addr			The memory address to store at.
	 * @param value			The value to be written.
	 * @param bigEndian		The endianess of the code.
	 */
	private void storeWord (long addr, long value, boolean bigEndian)
	{
		if (bigEndian) {
			target.store (addr + 0, (value >> (1 * byteSize)) & byteMask);
			target.store (addr + 1, (value >> (0 * byteSize)) & byteMask);
		}
		else {
			target.store (addr + 1, (value >> (1 * byteSize)) & byteMask);
			target.store (addr + 0, (value >> (0 * byteSize)) & byteMask);
		}
	}

	/**
	 * Stores a long value in the <CODE>Target</CODE> in an appropriate
	 * byte order.
	 * 
	 * @param addr			The memory address to store at.
	 * @param value			The value to be written.
	 * @param bigEndian		The endianess of the code.
	 */
	private void storeLong (long addr, long value, boolean bigEndian)
	{
		if (bigEndian) {
			target.store (addr + 0, (value >> (3 * byteSize)) & byteMask);
			target.store (addr + 1, (value >> (2 * byteSize)) & byteMask);
			target.store (addr + 2, (value >> (1 * byteSize)) & byteMask);
			target.store (addr + 3, (value >> (0 * byteSize)) & byteMask);
		}
		else {
			target.store (addr + 3, (value >> (3 * byteSize)) & byteMask);
			target.store (addr + 2, (value >> (2 * byteSize)) & byteMask);
			target.store (addr + 1, (value >> (1 * byteSize)) & byteMask);
			target.store (addr + 0, (value >> (0 * byteSize)) & byteMask);
		}
	}
	
	/**
	 * Writes a sorted list of symbols and address to the map file.
	 * 
	 * @param file			The <CODE>File</CODE> to write the map to.
	 */
	private void writeMap (File file)
	{
		try {
			PrintWriter		writer = new PrintWriter (file);
			
			writer.println ("Global Symbol Map\n");
			
			Object [] symbols = symbolMap.getSymbols ().toArray();
			Arrays.sort (symbols);
			
			for (int index = 0; index < symbols.length; ++index) {
				String symbol = (String) symbols [index];
				writer.println (pad(symbol, 16) + "  " + Hex.toHex (symbolMap.addressOf(symbol), 8)
						+ " in " + defs.get (symbol).getName ());
			}
			
			writer.println ("\n\nSections:\n");
			
			Object [] sections = sectionMap.getSections ().toArray ();

			boolean		swapped;
			do {
				swapped = false;
				
				for (int index = 0; index < sections.length - 1; ++index) {
					if (sectionMap.baseAddressOf ((Section) sections [index]) >
						sectionMap.baseAddressOf ((Section) sections [index+1])) {
							Object temp = sections [index];
							sections [index] = sections [index+1];
							sections [index+1] = temp;
							
							swapped = true;
						}
				}
			} while (swapped);
			
			String	lastName	= "";
			
			for (int index = 0; index < sections.length; ++index) {
				Section section = (Section) sections [index];
				
				if (!section.getName ().equals (lastName)) {
					lastName = section.getName ();
					
					writer.print (pad (lastName, 16) + " : ");
				}
				else
					writer.print (pad ("", 19));
				
				long	addr = sectionMap.baseAddressOf (section);
				int		size = section.getSize ();
				
				writer.print (Hex.toHex (addr, 8));
				writer.print (" - ");
				writer.print (Hex.toHex (addr + size - 1, 8));
				writer.print (" in ");
				writer.print (section.getModule().getName ());
				
				writer.println ();
			}
			
			writer.close ();
		}
		catch (Exception error) {
			System.err.println ("Error: A serious error occurred while writing the map file");
		}
	}
	
	private final String pad (String str, int len)
	{
		final String spaces = "                                                                                              ";
		
		return  ((str + spaces).substring (0, len));
	}
}