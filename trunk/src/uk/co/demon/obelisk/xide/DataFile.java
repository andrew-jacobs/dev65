package uk.co.demon.obelisk.xide;

import java.io.File;

public abstract class DataFile
{
	public File getFile ()
	{
		return (file);
	}
	
	public final boolean isChanged ()
	{
		return (changed);
	}
	
	public final String getDisplayName ()
	{
		String 		baseName = (file != null) ? file.getName () : getDefaultPath ();
		
		return (baseName + (changed ? "*" : ""));
	}
	
	protected DataFile (File file, boolean changed)
	{
		setFile (file);
		setChanged (changed);
	}
	
	protected abstract String getDefaultPath ();
	
	protected void setFile (File file)
	{
		this.file = file;
	}
	
	protected void setChanged (boolean changed)
	{
		this.changed = changed;
	}
		
	private File		file;
	
	private boolean		changed;
}
