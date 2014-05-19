package uk.co.demon.obelisk.w65xx;

import uk.co.demon.obelisk.xide.IntegratedDevelopmentEnvironment;

public final class Dev65 extends IntegratedDevelopmentEnvironment
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