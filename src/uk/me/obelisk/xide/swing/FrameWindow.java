/*
 * Copyright (C),2014-2018 Andrew John Jacobs.
 *
 * This program is provided free of charge for educational purposes
 *
 * Redistribution and use in binary form without modification, is permitted
 * provided that the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS 'AS IS' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.me.obelisk.xide.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class FrameWindow extends Window
{
	/**
	 * Provides access to the underlying <CODE>JFrame</CODE>.
	 * 
	 * @return	The underlying <CODE>JFrame</CODE>.
	 */
	public final JFrame getFrame ()
	{
		return (frame);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void show ()
	{
		frame.setVisible (true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void hide ()
	{
		frame.setVisible (false);
	}

	/**
	 * The Frame window itself.
	 */
	protected JFrame			frame
		= createFrame ("frame");

	/**
	 * The frame's menu bar instance.
	 */
	protected JMenuBar			menuBar
		= new JMenuBar ();

	/**
	 * The 'File' menu instance.
	 */
	protected JMenu				fileMenu
		= createMenu ("file");

	/**
	 * The 'File>Exit' menu item instance.
	 */
	protected JMenuItem			fileExitItem
		= createItem ("fileExit");

	/**
	 * The 'Help' menu instance.
	 */
	protected JMenu				helpMenu
		= createMenu ("help");

	/**
	 * The 'Help>About' menu instance.
	 */
	protected JMenuItem			helpAboutItem
		= createItem ("helpAbout");

	/**
	 * The <CODE>JPanel</CODE> containing the main GUI components.
	 */
	protected JPanel			contentPanel
		= new JPanel ();

	/**
	 */
	protected FrameWindow (final String filename)
	{
		super (filename);

		frame.setJMenuBar (menuBar);
		//frame.getContentPane().setLayout(new BorderLayout ());
		//frame.getContentPane().add (BorderLayout.CENTER, contentPanel);
		frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		
		frame.addWindowListener (new WindowAdapter ()
			{
				public void windowClosed (WindowEvent event)
				{
					MenuManager.unmanage (frame);
					FrameWindow.this.destroy ();
				}
			});

		fileExitItem.addActionListener (new ActionListener ()
			{
				public void actionPerformed (ActionEvent event)
				{
					frame.dispose ();
				}
			});
	}

	/**
	 * Removes this <CODE>Frame</CODE> from the set managed by the applicaton.
	 */
	protected void destroy ()
	{ }
	
	/**
	 * Centres the <CODE>Frame</CODE> within the screen.
	 */
	protected void center ()
	{
		Dimension	screen	= frame.getToolkit ().getScreenSize ();
		
		frame.setLocation (
			(screen.width  - frame.getWidth ())  / 2,
			(screen.height - frame.getHeight ()) / 2);
	}
}