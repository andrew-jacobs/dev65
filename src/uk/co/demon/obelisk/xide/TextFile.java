package uk.co.demon.obelisk.xide;

public class TextFile extends DataFile
{
	public TextFile ()
	{
		super (null, false);
	}
	
	@Override
	protected String getDefaultPath ()
	{
		return ("untitled.asm");
	}
	
}
