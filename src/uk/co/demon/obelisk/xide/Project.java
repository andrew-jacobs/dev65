package uk.co.demon.obelisk.xide;

import java.util.Vector;

public class Project extends DataFile
{
	public Project ()
	{
		super (null, false);
	}
	
	@Override
	protected String getDefaultPath ()
	{
		return ("untitled.prj");
	}
	
	private Vector<String> 	sourceFiles;
	private Vector<String> 	libraryFiles;
	private Vector<String> 	assemblerOptions;
	private Vector<String>	linkerOptions;
}
