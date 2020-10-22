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

package uk.me.obelisk.sxb;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import com.fazecast.jSerialComm.SerialPort;

/**
 * A simple terminal window.
 * 
 * @author Andrew Jacobs
 */
public class Terminal
{
	/**
	 * Constructs a <CODE>Terminal</CODE> that uses the specified serial port.
	 * 
	 * @param port			The serial port.
	 */
	public Terminal (SerialPort port)
	{
		this.serial = port;
		
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().add (scroll);
    	scroll.setViewportView (text);
    	text.setFont (new Font ("Monospaced", Font.PLAIN, 15));
    	text.setTabSize (8);
    	text.setBackground (Color.BLACK);
    	text.setForeground (Color.ORANGE);
    	text.setEditable (false);
    	text.getCaret ().setVisible (true);
    	text.setCaretColor (Color.WHITE);
    	
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
    	
    	frame.setBounds (0, 0, 800, 600);
    	frame.setVisible (true);
    	
		Thread worker = new Thread ("Serial RX")
				{
					@Override
					public void run ()
					{
						byte [] data = new byte [1];
						
						for (;;) {
							if (serial.readBytes(data, data.length) == data.length) {
								try {
									int		offset = text.getCaretPosition ();
									int 	line = text.getLineOfOffset (offset);
									int		start = text.getLineStartOffset (line);
									int		col = offset - start;
									
									switch (data [0]) {
									
									case 8:
										{										
											if (col > 0) text.setCaretPosition (offset - 1);
											break;
										}
										
									case 9:
										{
											data [0] = ' ';
											do {
												output (data, offset++);
											} while ((++col % 8) != 0);
											break;
										}
										
									case 10:
										{
											text.append ("\n");
											text.setCaretPosition(text.getText ().length ());
										}
										
									case 13:
										{
											if (col != 0) text.setCaretPosition (start);
											break;
										}
										
									default:
										output (data, offset);
									}
								}
								catch (BadLocationException err) {
									;
								}
							}
						}
					}
					
					private void output (byte [] data, int offset)
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
							
							text.setCaretPosition (offset + 1);
						}
					}
				};
		
    	worker.setDaemon (true);
    	worker.start ();
	}
	
	/**
	 * The frame window.
	 */
    private JFrame 			frame 	= new JFrame ("Terminal");
    
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
}