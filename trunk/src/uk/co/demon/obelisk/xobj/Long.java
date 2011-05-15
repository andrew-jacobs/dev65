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
 * The <CODE>Long</CODE> class hold an expression which will be converted into
 * a long value during linking.
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public final class Long extends Part implements Evaluatable
{
	/**
	 * Constructs a <CODE>Long</CODE> instance for the given expression.
	 * 
	 * @param 	expr		The expression to be converted.
	 */
	public Long (Expr expr)
	{
		this.expr = expr;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expr getExpr ()
	{
		return (expr);
	}
	
	/**
	 * Converts the module into an XML string.
	 * 
	 * @return	The XML representation of this module.
	 */
	public String toString ()
	{
		return ("<long>" + expr + "</long>");
	}
	
	/**
	 * The underlying expression.
	 */
	private final Expr		expr;
}