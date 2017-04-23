package uk.co.demon.obelisk.xide;

import java.util.Hashtable;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

final class TextWindow extends ChildWindow
{
	public static TextWindow forFile (FrameWindow parent, TextFile textFile)
	{
		if (windows.contains (textFile))
			return (windows.get (textFile));
		
		return (new TextWindow (parent, textFile));		
	}
	
	protected TextWindow (FrameWindow parent, TextFile textFile)
	{
		super (parent, "uk.co.demon.obelisk.xide.TextWindow");
		
		frame.getContentPane ().add (scrollPane);
		scrollPane.add (textPane);

		windows.put (this.textFile = textFile, this);

		frame.setResizable (true);
		frame.setClosable (true);
		frame.setMaximizable (true);
		frame.setIconifiable (true);
		frame.setSize (400, 300);
	}

	
	private static Hashtable<TextFile,TextWindow> windows
		= new Hashtable<TextFile,TextWindow> ();
	
	private TextFile textFile;
	
	private JScrollPane	scrollPane
		= new JScrollPane ();
	
	private JTextPane textPane
		= new JTextPane ();
}