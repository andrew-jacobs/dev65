package uk.co.demon.obelisk.xide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public abstract class IntegratedDevelopmentEnvironment
{


	
	protected IntegratedDevelopmentEnvironment (String title)
	{

	}
	
	protected void run ()
	{
		startUp ();
		
		new FrameWindow ().show ();
	}
	
	protected void startUp ()
	{
		try {
			UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
		frame = new JFrame (title);
		
		// Build the file menu
		fileMenu = new JMenu ("File");
		fileMenu.setMnemonic ('F');
		
		fileNewItem = new JMenuItem ("New", 'N');
		
		fileNewItem.addActionListener (new ActionListener ()
		{
			@Override
			public void actionPerformed (ActionEvent arg0)
			{
				new SourceEditorFrame ().show ();				
			}
		});
		
		fileOpenItem = new JMenuItem ("Open...", 'O');
		fileSaveItem = new JMenuItem ("Save", 'S');
		fileSaveAsItem = new JMenuItem ("Save As...", 'A');
		
		fileMenu.add (fileNewItem);
		fileMenu.add (fileOpenItem);
		fileMenu.add (fileSaveItem);
		fileMenu.add (fileSaveAsItem);
		
		// Build the edit menu
		editMenu = new JMenu ("Edit");
		editMenu.setMnemonic ('E');
		
		// Build the build menu
		buildMenu = new JMenu ("Build");
		buildMenu.setMnemonic ('B');
		
		// Build the help menu
		helpMenu = new JMenu ("Help");
		helpMenu.setMnemonic ('H');
		
		// Assemble menu bar
		menuBar.add (fileMenu);
		menuBar.add (editMenu);
		menuBar.add (buildMenu);
		menuBar.add (helpMenu);
		
		// Assemble frame
		frame.getContentPane ().add (menuBar, BorderLayout.NORTH);
		frame.getContentPane ().add (desktopPane, BorderLayout.CENTER);
		frame.setJMenuBar (menuBar);
		
		frame.setSize (800, 600);
	}
	
	protected final JDesktopPane getDesktopPane ()
	{
		return (desktopPane);
	}
	
	protected final DesktopManager getDesktopManager ()
	{
		return (desktopPane.getDesktopManager ());
	}
	
	private final String 	title;
	
	private JFrame			frame;
	
	private JMenuBar		menuBar
		= new JMenuBar ();
	
	private JMenu			fileMenu;
	
	private JMenuItem		fileNewItem;
	
	private JMenuItem		fileOpenItem;
	
	private JMenuItem		fileSaveItem;
	
	private JMenuItem		fileSaveAsItem;

	private JMenu			editMenu;
	
	private JMenu			buildMenu;
	
	private JMenu			helpMenu;
	
	private JDesktopPane 	desktopPane
		= new JDesktopPane ();
*/
}
