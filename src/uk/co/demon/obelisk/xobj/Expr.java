/*
 * Copyright (C),2005-2015 Andrew John Jacobs.
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
 * An instance of the abstract <CODE>Expr</CODE> class represents part of an
 * expression tree.
 * <P>
 * The <CODE>Expr</CODE> class implements a set of functions corresponding to
 * mathematical operations that build expression trees, optimising to constant
 * values where possible.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public abstract class Expr
{
	/**
	 * Calculate the logical AND of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr land (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ()) {
			if ((lhs.resolve (null, null) != 0) && (rhs.resolve (null, null) != 0))
				return (TRUE);
			else
				return (FALSE);
		}
		return (new BinaryExpr.LAnd (lhs, rhs));
	}

	/**
	 * Calculate the logical OR of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr lor (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ()) {
			if ((lhs.resolve (null, null) != 0) || (rhs.resolve (null, null) != 0))
				return (TRUE);
			else
				return (FALSE);
		}
		return (new BinaryExpr.LOr (lhs, rhs));
	}

	/**
	 * Calculate the logical NOT an expression.
	 * 
	 * @param 	exp				The input expression.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr lnot (final Expr exp)
	{
		if (exp.isAbsolute ()) {
			if (exp.resolve (null, null) != 0)
				return (FALSE);
			else
				return (TRUE);
		}
		return (new UnaryExpr.Not (exp));
	}

	/**
	 * Calculate the binary AND of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr and (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) & rhs.resolve (null, null)));
		
		return (new BinaryExpr.And (lhs, rhs));
	}

	/**
	 * Calculate the binary OR of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr or (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) | rhs.resolve (null, null)));
		
		return (new BinaryExpr.Or (lhs, rhs));
	}

	/**
	 * Calculate the binary XOR of two expressons.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr xor (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) ^ rhs.resolve (null, null)));
		
		return (new BinaryExpr.Xor (lhs, rhs));
	}

	/**
	 * Calculate the binary complement of an expressions.
	 * 
	 * @param 	exp				The sub-expression.

	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr cpl (final Expr exp)
	{
		if (exp.isAbsolute ())
			return (new Value (null, ~exp.resolve (null, null)));
		
		return (new UnaryExpr.Cpl (exp));
	}

	/**
	 * Calculate the addition of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr add (final Expr lhs, final Expr rhs)
	{ 
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) + rhs.resolve (null, null)));

		// Relative address optimisations
		if (lhs.isAbsolute () && rhs instanceof Value) {
			Value val = (Value) rhs;
			return (new Value (val.getSection (), lhs.resolve (null, null) + val.getValue ()));
		}
		if (lhs instanceof Value && rhs.isAbsolute ()) {
			Value val = (Value) lhs;
			return (new Value (val.getSection (), val.getValue () + rhs.resolve (null, null)));
		}
		
		return (new BinaryExpr.Add (lhs, rhs));
	}

	/**
	 * Calculate the subtraction of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr sub (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) - rhs.resolve (null, null)));
		
		// Relative address optimisations
		if (lhs.isAbsolute () && rhs instanceof Value) {
			Value val = (Value) rhs;
			return (new Value (val.getSection (), lhs.resolve (null, null) - val.getValue ()));
		}
		if (lhs instanceof Value && rhs.isAbsolute ()) {
			Value val = (Value) lhs;
			return (new Value (val.getSection (), val.getValue () - rhs.resolve (null, null)));
		}
		
		// A useful relative branch optimisation
		if (lhs instanceof Value && rhs instanceof Value) {
			Value	lh 	= (Value) lhs;
			Value	rh 	= (Value) rhs;
			
			if (lh.getSection () == rh.getSection ())
				return (new Value (null, lh.getValue () - rh.getValue ()));
		}
		
		return (new BinaryExpr.Sub (lhs, rhs));
	}

	/**
	 * Calculate the multiplication of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr mul (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) * rhs.resolve (null, null)));
		
		return (new BinaryExpr.Mul (lhs, rhs));
	}

	/**
	 * Calculate the division of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr div (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) / rhs.resolve (null, null)));
		
		return (new BinaryExpr.Div (lhs, rhs));
	}

	/**
	 * Calculate the modulus of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr mod (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) % rhs.resolve (null, null)));
		
		return (new BinaryExpr.Mod (lhs, rhs));
	}

	/**
	 * Calculate the negation of an expressions.
	 * 
	 * @param 	exp				The sub-expression.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr neg (final Expr exp)
	{
		if (exp.isAbsolute ())
			return (new Value (null, -exp.resolve (null, null)));
		
		return (new UnaryExpr.Neg (exp));
	}

	/**
	 * Calculate the right shift of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr shr (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) >> rhs.resolve (null, null)));
		
		return (new BinaryExpr.Shr (lhs, rhs));
	}
	
	/**
	 * Calculate the left shift of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr shl (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return (new Value (null, lhs.resolve (null, null) << rhs.resolve (null, null)));
		
		return (new BinaryExpr.Shl (lhs, rhs));
	}
		
	/**
	 * Calculate the equality of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr eq (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) == rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Eq (lhs, rhs));
	}
		
	/**
	 * Calculate the inequality of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr ne (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) != rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Ne (lhs, rhs));
	}

	/**
	 * Calculate the less that of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr lt (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) < rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Lt (lhs, rhs));
	}
	
	/**
	 * Calculate the less or equal of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr le (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) <= rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Le (lhs, rhs));
	}
		
	/**
	 * Calculate the greater than of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr gt (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) > rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Gt (lhs, rhs));
	}
		
	/**
	 * Calculate the greater or equal of two expressions.
	 * 
	 * @param 	lhs				The left hand side.
	 * @param 	rhs				The right hand side.
	 * @return	The resulting value as an <CODE>Expr</CODE>.
	 */
	public static Expr ge (final Expr lhs, final Expr rhs)
	{
		if (lhs.isAbsolute () && rhs.isAbsolute ())
			return ((lhs.resolve (null, null) >= rhs.resolve (null, null)) ? TRUE : FALSE);
		
		return (new BinaryExpr.Ge (lhs, rhs));
	}

	/**
	 * Determines if this <CODE>Expr</CODE> represents an absolute value.
	 * 
	 * @return	<CODE>true</CODE> if the value is absolute.
	 */
	public abstract boolean isAbsolute ();
	
	
	/**
	 * Determines if this <CODE>Expr</CODE> represents a relative value.
	 * 
	 * @return	<CODE>true</CODE> if the value is relative.
	 */
	public final boolean isRelative ()
	{
		return (!isAbsolute ());
	}
	
	/**
	 * Determines if this <CODE>Expr</CODE> represents an external value.
	 * 
	 * @return	<CODE>true</CODE> if teh value is external.
	 */
	public abstract boolean isExternal (Section section);

	/**
	 * Calculates the real value of an expression given the details of
	 * the section mapping and symbol values.
	 * 
	 * @param 	sections		A structure showing where sections have been placed.
	 * @param 	symbols			A structure showing where symbols are located.
	 * @return	The target value of the expression.
	 */
	public abstract long resolve (SectionMap sections, SymbolMap symbols);
	
	/**
	 * Calculates the real value of an expression assuming it is an absolute value.
	 * 
	 * @return 	The target value of the expression.
	 */
	public long resolve ()
	{
		return (resolve (null, null));
	}
	
	/**
	 * A constant <CODE>Value</CODE> representing a true state.
	 */
	private static Value		TRUE	= new Value (null, 1);
	
	/**
	 * A constant <CODE>Value</CODE> representing a false state.
	 */
	private static Value		FALSE	= new Value (null, 0);
}