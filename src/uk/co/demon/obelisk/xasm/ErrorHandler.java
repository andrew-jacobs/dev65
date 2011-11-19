package uk.co.demon.obelisk.xasm;

public interface ErrorHandler
{
	public abstract void error (final String message);
	
	public abstract void warning (final String message);
}
