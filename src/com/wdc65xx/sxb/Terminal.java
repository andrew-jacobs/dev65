//==============================================================================
//                                                     
//     SSSSSSSSSSSSSSS XXXXXXX       XXXXXXXBBBBBBBBBBBBBBBBB   
//   SS:::::::::::::::SX:::::X       X:::::XB::::::::::::::::B  
//  S:::::SSSSSS::::::SX:::::X       X:::::XB::::::BBBBBB:::::B 
//  S:::::S     SSSSSSSX::::::X     X::::::XBB:::::B     B:::::B
//  S:::::S            XXX:::::X   X:::::XXX  B::::B     B:::::B
//  S:::::S               X:::::X X:::::X     B::::B     B:::::B
//   S::::SSSS             X:::::X:::::X      B::::BBBBBB:::::B 
//    SS::::::SSSSS         X:::::::::X       B:::::::::::::BB  
//      SSS::::::::SS       X:::::::::X       B::::BBBBBB:::::B 
//         SSSSSS::::S     X:::::X:::::X      B::::B     B:::::B
//              S:::::S   X:::::X X:::::X     B::::B     B:::::B
//              S:::::SXXX:::::X   X:::::XXX  B::::B     B:::::B
//  SSSSSSS     S:::::SX::::::X     X::::::XBB:::::BBBBBB::::::B
//  S::::::SSSSSS:::::SX:::::X       X:::::XB:::::::::::::::::B 
//  S:::::::::::::::SS X:::::X       X:::::XB::::::::::::::::B  
//   SSSSSSSSSSSSSSS   XXXXXXX       XXXXXXXBBBBBBBBBBBBBBBBB   
//
// A Command Line Utility for WDC SXB and MMC Development Boards
//------------------------------------------------------------------------------
// Copyright (C)2019-2020 Andrew Jacobs.
//
// This work is made available under the terms of the Creative Commons
// Attribution-ShareAlike 4.0 International license.Open the following URL to
// see the details.
//
// https://creativecommons.org/licenses/by-sa/4.0/
//
//==============================================================================
// Notes:
//
//------------------------------------------------------------------------------

package com.wdc65xx.sxb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import com.fazecast.jSerialComm.SerialPort;

/**
 * A simple terminal window.
 * 
 * @author Andrew Jacobs
 */
public class Terminal implements ClipboardOwner
{
	/**
	 * Constructs a <CODE>Terminal</CODE> that uses the specified serial port.
	 * 
	 * @param port			The serial port.
	 */
	public Terminal (SerialPort port)
	{
		this.serial = port;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception error)
		{ }
		
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add (scroll);
    	
    	frame.setJMenuBar(menuBar);
    	menuBar.add (fileMenu);
    	menuBar.add (editMenu);
    	
    	fileMenu.add (sendItem);
    	fileMenu.addSeparator ();
    	fileMenu.add (exitItem);
    	
    	editMenu.add (copyItem);
    	editMenu.add (pasteItem);
    	
    	exitItem.setMnemonic('x');
    	//exitItem.setAccelerator(KeyStroke.getKeyStroke ("alt F4"));
    	
    	copyItem.setMnemonic('C');
    	//copyItem.setAccelerator(KeyStroke.getKeyStroke ("control C"));
    	copyItem.setEnabled (false);
    	pasteItem.setMnemonic('P');
    	//pasteItem.setAccelerator(KeyStroke.getKeyStroke ("control V"));
    	pasteItem.setEnabled(canPaste ());
    	
    	scroll.setViewportView (text);
    	text.setFont (new Font ("Monospaced", Font.PLAIN, 15));
    	text.setTabSize (8);
    	text.setBackground (Color.BLACK);
    	text.setForeground (Color.ORANGE);
    	text.setEditable (false);
    	text.getCaret ().setVisible (true);
    	text.setCaretColor (Color.WHITE);
    	
    	sendItem.addActionListener (new ActionListener ()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					if (fileChooser.showOpenDialog (frame) == JFileChooser.APPROVE_OPTION) {
						try {
							BufferedReader	reader = new BufferedReader (new FileReader (fileChooser.getSelectedFile()));
							StringBuilder 	buffer = new StringBuilder ();
							String			text;
							
							while ((text = reader.readLine ()) != null) {
								buffer.append (text);
								buffer.append('\n');
							}
							reader.close ();
							new TxWorker (buffer.toString ()).start ();
						}
						catch (Exception err) {
							System.err.println ("Unexpected exception in sendfile");
						}
					}
				}				
			});
    	
    	exitItem.addActionListener(new ActionListener ()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					frame.dispose ();
				}
			});
    	
    	copyItem.addActionListener(new ActionListener ()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					clipboard.setContents (new StringSelection (text.getSelectedText()), Terminal.this);
				}
			});
    	
    	pasteItem.addActionListener(new ActionListener ()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					try {
						String text = (String) clipboard.getData (DataFlavor.stringFlavor);
						new TxWorker (text).start ();
					}
					catch (Exception err) {
						System.err.println ("Unexpected exception while pasting");
					}
				}
			});
    	
    	text.addKeyListener (new KeyAdapter ()
			{
				@Override
				public void keyPressed (KeyEvent event)
				{
					if (event.getKeyCode() == KeyEvent.VK_ENTER) {
						buffer [0] = '\r';
						serial.writeBytes(buffer, buffer.length);
					}
					else {
						char key = event.getKeyChar ();
						
						if (key != KeyEvent.CHAR_UNDEFINED) {
							buffer [0] = (byte) event.getKeyChar();
							serial.writeBytes(buffer, buffer.length);
						}
					}
					event.consume ();
				}
					
				private byte [] 	buffer = new byte [1];
			}); 
    	
    	text.addCaretListener(new CaretListener ()
			{
				@Override
				public void caretUpdate(CaretEvent event)
				{
					copyItem.setEnabled (event.getDot() != event.getMark());	
				}
			});
    	
    	text.addFocusListener (new FocusListener ()
			{
				@Override
				public void focusGained(FocusEvent event)
				{
					text.getCaret ().setVisible (true);
				}

				@Override
				public void focusLost(FocusEvent arg0)
				{
					text.getCaret ().setVisible (false);
				}
		
			});
    	
    	clipboard.addFlavorListener(new FlavorListener ()
    		{
				@Override
				public void flavorsChanged(FlavorEvent event)
				{
					pasteItem.setEnabled(canPaste ());
				}
			});
    	
    	
    	frame.setBounds (0, 0, 800, 600);
    	frame.setVisible (true);
    	
		new RxWorker ().start ();
	}
	
	private class TxWorker extends Thread
	{
		public TxWorker (String text)
		{
			super ("TxWorker");
			setDaemon (true);
			
			this.text = text;
		}
		
		@Override
		public void run ()
		{
			byte [] data = new byte [1];
			
			for (int index = 0; index < text.length(); ++index) {
				char ch = text.charAt (index);
				if ((ch == '\n') || ((' ' <= ch) && (ch < 0x7f))) {
					data [0] = (byte)((ch == '\n') ? '\r' : ch);
					serial.writeBytes(data, data.length);
				}
			}
		}
		
		private String text;
	}
	
	private class RxWorker extends Thread
	{
		public RxWorker ()
		{
			super ("RxWorker");
			setDaemon (true);
		}
		
		@Override
		public void run ()
		{
			byte [] data = new byte [1];
			offset = text.getCaretPosition ();
			
			for (;;) {
				if (serial.readBytes(data, data.length) == data.length) {
					try {
						int 	line = text.getLineOfOffset (offset);
						int		start = text.getLineStartOffset (line);
						int		col = offset - start;
						
						switch (data [0]) {
						case 8:									
								if (col > 0) text.setCaretPosition (--offset);
								break;
							
						case 9:
								data [0] = ' ';
								do {
									output (data);
								} while ((++col % 8) != 0);
								break;
							
						case 10:
								text.append ("\n");
								text.setCaretPosition (offset = text.getText ().length ());
								break;
							
						case 13:
								if (col != 0) text.setCaretPosition (start);
								break;
							
						default:
								output (data);
						}
					}
					catch (BadLocationException err) {
						;
					}
				}
			}
		};
		
		private void output (byte [] data)
		{
			if ((' ' <= data [0]) && (data [0] < 0x7f)) {
				String raw = text.getText();
				
				if (offset != raw.length ()) {
					char [] chars = raw.toCharArray();
					chars [offset] = (char) data [0];
					text.setText (new String (chars));
				}
				else
					text.append(new String (data));
				
				text.setCaretPosition (++offset);
				text.getCaret().setVisible (true);
			}
		}
		
		
		private int offset;	
	}
	
	private Clipboard		clipboard
		= Toolkit.getDefaultToolkit ().getSystemClipboard ();
	
	/**
	 * The frame window.
	 */
    private JFrame 			frame 	= new JFrame ("Terminal");
    
    
    private JMenuBar		menuBar	= new JMenuBar ();
    
    private JMenu			fileMenu = new JMenu ("File");
    
    private JMenuItem		sendItem = new JMenuItem ("Send file..");
    
    private JMenuItem		exitItem = new JMenuItem ("Exit");
    
    private JMenu			editMenu = new JMenu ("Edit");
    
    private JMenuItem		copyItem = new JMenuItem ("Copy");
    
    private JMenuItem 		pasteItem = new JMenuItem ("Paste");
    
    private JFileChooser	fileChooser = new JFileChooser ();
    
    /**
     * The text area.
     */
    private JTextArea		text 	= new JTextArea ();
    
    /**
     * The scroll pane containing the text area.
     */
    private JScrollPane		scroll	= new JScrollPane ();
    
    /**
     * The serial port used for communication with the SXB/MMC board.
     */
    private SerialPort		serial;
    
    private boolean canPaste ()
    {
    	return (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor));
    }

	@Override
	public void lostOwnership (Clipboard clipboard, Transferable content)
	{ }
}