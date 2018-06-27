package uk.co.demon.obelisk.w65xx;

import uk.me.obelisk.xide.Environment;

public final class Dev65 extends Environment
{
	public static void main (String [] arguments)
	{
		new Dev65 ().run ();
	}
	
	public Dev65 ()
	{
		super ("65XX");
	}
}