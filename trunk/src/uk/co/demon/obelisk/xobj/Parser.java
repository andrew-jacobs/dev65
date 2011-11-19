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

package uk.co.demon.obelisk.xobj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The <CODE>Parser</CODE> class converts an XML file back into either an
 * object module or library.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Parser
{
	/**
	 * Parse an object or library file and convert back into Java objects.
	 * 
	 * @param 	fileName		The name of the file to process.
	 * @return	The parsed object.
	 */
	public static Object parse (final String fileName)
	{
		try {
			stack.clear ();
			getParser ().parse (new FileInputStream (fileName), handler);
		}
		catch (FileNotFoundException error) {
			return (null);
		}
		catch (Exception error) {
			error.printStackTrace(System.err);
			return (null);
		}
		
		return (stack.pop ());
	}
	
	/**
	 * A customised SAX handler to process the XML input stream
	 */
	private static class Handler extends DefaultHandler
	{
		public void characters (char[] ch, int start, int length)
			throws SAXException
		{
			int		span = module.getByteSize () / 4;
			chars = new String (ch, start, length);	
			
			if (tags.peek ().equals ("section")) {
				for (int index = 0; index < length; index += span) {
					section.addByte (java.lang.Long.parseLong (
							chars.substring (index, index + span), 16));
				}
			}
			else {
				if (tags.peek().equals ("gbl"))
					stack.push (chars);
			}
		}
			
		public void startElement (String uri, String localName, String qName, Attributes attrs)
			throws SAXException
		{
			localName = qName;
			
			tags.push (localName);
			
			switch (localName.charAt (0)) {
			case 'l':
				if (localName.equals ("library")) {
					stack.push (new Library ());
				}
				break;
				
			case 'm':
				if (localName.equals ("module")) {
					module = new Module (
							attrs.getValue("target"),
							!attrs.getValue("endian").equals ("little"),
							Integer.parseInt (attrs.getValue ("byteSize")));

					if (attrs.getIndex ("name") != -1)
						module.setName (attrs.getValue ("name"));
					
					stack.push (module);
				}
				break;
				
			case 's':
				if (localName.equals ("section")) {
					if (attrs.getIndex ("addr") != -1) {
						long start = java.lang.Long.parseLong (attrs.getValue ("addr"), 16);
				
						section = module.findSection (attrs.getValue("name"), start);;						
					}
					else
						section = module.findSection (attrs.getValue("name"));
				}
				break;
				
			case 'v':
				if (localName.equals ("val")) {
					sect = attrs.getValue ("sect");
				}
				break;
			}
		}
		
		public void endElement (String uri, String localName, String qName)
			throws SAXException
		{
			localName = qName;
			tags.pop ();
			
			switch (localName.charAt (0)) {
			case 'a':
				if (localName.equals ("add")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Add (lhs, rhs));
					return;
				}
				if (localName.equals ("and")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Add (lhs, rhs));
					return;
				}
				break;
				
			case 'b':
				if (localName.equals ("byte")) {
					Expr	exp = (Expr) stack.pop ();
				
					section.addByte (exp);
					return;
				}
				break;

			case 'c':
				if (localName.equals ("cpl")) {
					Expr	exp = (Expr) stack.pop ();
				
					stack.push (new UnaryExpr.Cpl (exp));
					return;
				}
				break;
				
			case 'd':
				if (localName.equals ("div")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Div (lhs, rhs));
					return;
				}
				break;
								
			case 'e':
				if (localName.equals ("eq")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Eq (lhs, rhs));
					return;
				}
				if (localName.equals ("ext")) {
					stack.push (new Extern (chars));
				}
				break;
				
			case 'g':
				if (localName.equals ("ge")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Ge (lhs, rhs));
					return;
				}
				if (localName.equals ("gt")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Gt (lhs, rhs));
					return;
				}
				if (localName.equals ("gbl")) {
					Expr	exp	= (Expr) stack.pop ();
					String  sym = (String) stack.pop ();
					
					module.addGlobal (sym, exp);
				}
				break;
				
			case 'l':
				if (localName.equals ("le")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Le (lhs, rhs));
					return;
				}
				if (localName.equals ("lt")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Lt (lhs, rhs));
					return;
				}
				if (localName.equals ("land")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.LAnd (lhs, rhs));
					return;
				}
				if (localName.equals ("lor")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.LOr (lhs, rhs));
					return;
				}
				if (localName.equals ("long")) {
					Expr	exp = (Expr) stack.pop ();
				
					section.addLong (exp);
					return;
				}
				if (localName.equals ("library")) {
					Stack<Module> modules = new Stack<Module> ();
					
					while (stack.peek() instanceof Module)
						modules.push ((Module) stack.pop ());
		
					Library library = (Library) stack.peek ();
					
					while (!modules.empty ())
						library.addModule (modules.pop ());
				}
				break;
				
			case 'm':
				if (localName.equals ("mod")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Mod (lhs, rhs));
					return;
				}
				break;
								
			case 'n':
				if (localName.equals ("ne")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Ne (lhs, rhs));
					return;
				}
				if (localName.equals ("neg")) {
					Expr	exp = (Expr) stack.pop ();
				
					stack.push (new UnaryExpr.Neg (exp));
					return;
				}
				if (localName.equals ("not")) {
					Expr	exp = (Expr) stack.pop ();
				
					stack.push (new UnaryExpr.Not (exp));
					return;
				}
				break;

			case 'o':
				if (localName.equals ("or")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Or (lhs, rhs));
					return;
				}
				break;
								
			case 's':
				if (localName.equals ("sub")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Sub (lhs, rhs));
					return;
				}
				if (localName.equals ("shl")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Shl (lhs, rhs));
					return;
				}
				if (localName.equals ("shr")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Shr (lhs, rhs));
					return;
				}
				break;
				
			case 'v':
				if (localName.equals ("val")) {
					int		value = Integer.parseInt (chars);
	
					if (sect != null)
						stack.push (new Value (module.findSection (sect), value));
					else
						stack.push (new Value (null, value));
					return;
				}
				break;

			case 'w':
				if (localName.equals ("word")) {
					Expr	exp = (Expr) stack.pop ();
				
					section.addWord (exp);
					return;
				}
				break;


			case 'x':
				if (localName.equals ("xor")) {
					Expr	rhs = (Expr) stack.pop ();
					Expr	lhs = (Expr) stack.pop ();
				
					stack.push (new BinaryExpr.Xor (lhs, rhs));
					return;
				}
				break;
			}
		}
		
		private Stack<String>			tags	= new Stack<String> ();
		
		private Module					module;
		
		private Section					section;
		
		private String					sect;
		
		private String					chars;
	}
	
	/**
	 * <CODE>SAXParserFactory</CODE> instance used to create XML parsers.
	 */
	private static SAXParserFactory	factory	= null;
	
	/**
	 * The <CODE>SAXParser</CODE> used to parser the object module.
	 */
	private static SAXParser		parser	= null;
	
	/**
	 * The <CODE>Handler</CODE> used to process the SAX event stream.
	 */
	private static Handler			handler	= new Handler ();
	
	/**
	 * A <CODE>Stack</CODE> used to hold objects under construction.
	 */
	private static Stack<Object>	stack	= new Stack<Object> ();

	/**
	 * Returns a <CODE>SAXParser</CODE> that will be used to process the XML
	 * file.
	 * 
	 * @return	The <CODE>SAXParser</CODE>.
	 */
	private static SAXParser getParser ()
	{
		if (parser == null)	{
			try {
				if (factory == null) {
					factory = SAXParserFactory.newInstance ();
					
					factory.setNamespaceAware (false);
					factory.setValidating (false);
				}
				parser = factory.newSAXParser ();
			}
			catch (ParserConfigurationException error) {
				System.err.println ("Error: No SAX Parser installed in the JAVA runtime");
				System.exit (1);
			}
			catch (SAXException error) {
				System.err.println ("Error: Failed to create a SAX parser");
				System.exit (1);				
			}
		}
		return (parser);
	}
}