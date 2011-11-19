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

/**
 * The <CODE>BinaryExpr</CODE> class is the common base of all mathematical and
 * logical operators that take two arguments.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class BinaryExpr extends Expr
{
	/**
	 * Get the left hand side sub-expression.
	 * 
	 * @return	The left hand side sub-expression.
	 */
	public final Expr getLhs ()
	{
		return (lhs);
	}
	
	/**
	 * Get the right hand side sub-expression.
	 * 
	 * @return	The right hand side sub-expression.
	 */
	public final Expr getRhs ()
	{
		return (rhs);
	}
	
	/**
	 * The <CODE>LAnd</CODE> class implements a LOGICAL AND expression tree
	 * node.
	 */
	public static final class LAnd extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>LAnd</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public LAnd (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if ((lhs.resolve (sections, symbols) != 0) && (rhs.resolve (sections, symbols) != 0))
				return (1);
			else
				return (0);
		}
	
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<land>" + lhs + rhs + "</land>");
		}
	}
	
	/**
	 * The <CODE>LOr</CODE> class implements a LOGICAL OR expression tree
	 * node.
	 */
	public static final class LOr extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>LOr</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public LOr (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if ((lhs.resolve (sections, symbols) != 0) || (rhs.resolve (sections, symbols) != 0))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<lor>" + lhs + rhs + "</lor>");
		}
	}
	
	/**
	 * The <CODE>And</CODE> class implements a BINARY OR expression tree
	 * node.
	 */
	public static final class And extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>And</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public And (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) & rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<and>" + lhs + rhs + "</and>");
		}
	}
	
	/**
	 * The <CODE>Or</CODE> class implements a BINARY OR expression tree
	 * node.
	 */
	public static final class Or extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Or</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Or (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) | rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<or>" + lhs + rhs + "</or>");
		}
	}
	
	/**
	 * The <CODE>Xor</CODE> class implements a BINARY OR expression tree
	 * node.
	 */
	public static final class Xor extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Xor</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Xor (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) ^ rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<xor>" + lhs + rhs + "</xor>");
		}
	}
	
	/**
	 * The <CODE>Add</CODE> class implements an addition expression tree
	 * node.
	 */
	public static final class Add extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Add</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Add (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) + rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<add>" + lhs + rhs + "</add>");
		}
	}
	
	/**
	 * The <CODE>Sub</CODE> class implements a subtract expression tree
	 * node.
	 */
	public static final class Sub extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Sub</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Sub (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) - rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<sub>" + lhs + rhs + "</sub>");
		}
	}
	
	/**
	 * The <CODE>Mul</CODE> class implements a multiply expression tree
	 * node.
	 */
	public static final class Mul extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Mul</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Mul (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) * rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<mul>" + lhs + rhs + "</mul>");
		}
	}
	
	/**
	 * The <CODE>Div</CODE> class implements a division expression tree
	 * node.
	 */
	public static final class Div extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Div</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Div (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) / rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<div>" + lhs + rhs + "</div>");
		}
	}
	
	/**
	 * The <CODE>Mod</CODE> class implements a modulus expression tree
	 * node.
	 */
	public static class Mod extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Mod</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Mod (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) % rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<mod>" + lhs + rhs + "</mod>");
		}
	}
	
	/**
	 * The <CODE>Shr</CODE> class implements a right shift expression tree
	 * node.
	 */
	public static class Shr extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Shr</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Shr (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) >> rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<shr>" + lhs + rhs + "</shr>");
		}
	}
	
	/**
	 * The <CODE>Shl</CODE> class implements a left shift expression tree
	 * node.
	 */
	public static class Shl extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Shl</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Shl (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			return (lhs.resolve (sections, symbols) << rhs.resolve (sections, symbols));
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<shl>" + lhs + rhs + "</shl>");
		}
	}

	/**
	 * The <CODE>Eq</CODE> class implements an equals expression tree
	 * node.
	 */
	public static final class Eq extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Eq</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Eq (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) == rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<eq>" + lhs + rhs + "</eq>");
		}
	}
	
	/**
	 * The <CODE>Ne</CODE> class implements a not equals expression tree
	 * node.
	 */
	public static final class Ne extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Ne</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Ne (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) != rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<ne>" + lhs + rhs + "</ne>");
		}
	}
	
	/**
	 * The <CODE>Add</CODE> class implements a less than expression tree
	 * node.
	 */
	public static final class Lt extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Lt</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Lt (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) < rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<lt>" + lhs + rhs + "</lt>");
		}
	}
	
	/**
	 * The <CODE>Add</CODE> class implements a less that or equal expression tree
	 * node.
	 */
	public static final class Le extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Le</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Le (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) <= rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<le>" + lhs + rhs + "</le>");
		}
	}
	
	/**
	 * The <CODE>Add</CODE> class implements a greater than expression tree
	 * node.
	 */
	public static final class Gt extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Gt</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Gt (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) > rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<gt>" + lhs + rhs + "</gt>");
		}
	}
	
	/**
	 * The <CODE>Add</CODE> class implements a greater than or equal expression
	 * tree node.
	 */
	public static final class Ge extends BinaryExpr
	{
		/**
		 * Constructs a <CODE>Ge</CODE> instance from its sub-expressions.
		 * 
		 * @param	lhs				The left hand sub-expression.
		 * @param 	rhs				The right hand sub-expression.
		 */
		public Ge (final Expr lhs, final Expr rhs)
		{
			super (lhs, rhs);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public long resolve (SectionMap sections, SymbolMap symbols)
		{
			if (lhs.resolve (sections, symbols) >= rhs.resolve (sections, symbols))
				return (1);
			else
				return (0);
		}
		
		/**
		 * {@inheritDoc}
		 */
		public String toString ()
		{
			return ("<ge>" + lhs + rhs + "</ge>");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean isAbsolute ()
	{
		return (lhs.isAbsolute () && rhs.isAbsolute ());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean isExternal (Section section)
	{
		return (lhs.isExternal (section) || rhs.isExternal (section));
	}

	/**
	 * {@inheritDoc}
	 */
	public abstract long resolve (SectionMap sections, SymbolMap symbols);

	/**
	 * The left hand sub-expression.
	 */
	protected final Expr		lhs;
	
	/**
	 * The right hand sub-expression.
	 */
	protected final Expr		rhs;

	/**
	 * Constructs a <CODE>BinaryExpr</CODE> from its left and right
	 * sub-expressions.
	 * 
	 * @param 	lhs				The left hand sub-expression.
	 * @param 	rhs				The right hand sub-expression.
	 */
	protected BinaryExpr (final Expr lhs, final Expr rhs)
	{
		this.lhs = lhs;
		this.rhs = rhs;
	}
}