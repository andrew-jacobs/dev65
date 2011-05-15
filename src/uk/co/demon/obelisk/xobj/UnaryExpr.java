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

package uk.co.demon.obelisk.xobj;

/**
 * The <CODE>UnaryExpr</CODE> class is the common base of all mathematical and
 * logical operators that take a single argument.
 * 
 * @author Andrew Jacobs
 * @version	$Id$
 */
public abstract class UnaryExpr extends Expr
{
	public final Expr getExp ()
	{
		return (exp);
	}
	
	/**
	 * The <CODE>Not</CODE> class implements the logical NOT operation.
	 * 
	 * @author Andrew Jacobs
	 */
	public static class Not extends UnaryExpr
	{
		/**
		 * Constructs a <CODE>Not</CODE> instance which will invert the
		 * associated logical expression.
		 * 
		 * @param exp		The expression to be inverted.
		 */
		public Not (final Expr exp)
		{
			super (exp);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public int resolve (SectionMap sections, SymbolMap symbols)
		{
			return ((exp.resolve (sections, symbols) != 0) ? 0 : 1);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<not>" + exp + "</not>");
		}
	}
	
	/**
	 * The <CODE>Cpl</CODE> class implements the binary complement operation.
	 * 
	 * @author Andrew Jacobs
	 */
	public static class Cpl extends UnaryExpr
	{
		/**
		 * Constructs a <CODE>Cpl</CODE> instance which will complement the
		 * associated binary expression.
		 * 
		 * @param exp		The expression to be complemented.
		 */
		public Cpl (final Expr exp)
		{
			super (exp);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public int resolve (SectionMap sections, SymbolMap symbols)
		{
			return (~exp.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<cpl>" + exp + "</cpl>");
		}
	}
	
	/**
	 * The <CODE>Neg</CODE> class implements the arithmetic negation
	 * operation.
	 * 
	 * @author Andrew Jacobs
	 */
	public static class Neg extends UnaryExpr
	{
		/**
		 * Constructs a <CODE>Neg</CODE> instance which will complement the
		 * associated expression.
		 * 
		 * @param exp		The expression to be complemented.
		 */
		public Neg (final Expr exp)
		{
			super (exp);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public int resolve (SectionMap sections, SymbolMap symbols)
		{
			return (-exp.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<neg>" + exp + "</neg>");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean isAbsolute ()
	{
		return (exp.isAbsolute ());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean isExternal (Section section)
	{
		return (exp.isExternal (section));
	}
		
	/**
	 * {@inheritDoc}
	 */
	public abstract int resolve (SectionMap sections, SymbolMap symbols);
	
	/**
	 * The underlying expression.
	 */
	protected final Expr		exp;

	/**
	 * Constructs a <CODE>UnaryExpr</CODE> instance with the given underlying
	 * expression.
	 * 
	 * @param exp			The underlying expression.
	 */
	protected UnaryExpr (Expr exp)
	{
		this.exp = exp;
	}
}