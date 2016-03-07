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
	
	@SuppressWarnings("unused")
	private Vector<String> 	sourceFiles;
	@SuppressWarnings("unused")
	private Vector<String> 	libraryFiles;
	@SuppressWarnings("unused")
	private Vector<String> 	assemblerOptions;
	@SuppressWarnings("unused")
	private Vector<String>	linkerOptions;
}
