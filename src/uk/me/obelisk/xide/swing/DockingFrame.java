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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.javadocking.DockingManager;
import com.javadocking.dock.FloatDock;
import com.javadocking.dock.Position;
import com.javadocking.dock.SplitDock;
import com.javadocking.dock.TabDock;
import com.javadocking.dock.factory.LeafDockFactory;
import com.javadocking.model.FloatDockModel;

public abstract class DockingFrame extends Window
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

	public final TabDock getCenterTabbedDock ()
	{
		return (centerTabbedDock);
	}
	
	public final TabDock getBottomTabbedDock ()
	{
		return (bottomTabbedDock);
	}
	
	public final TabDock getLeftUpperTabbedDock ()
	{
		return (leftUpperTabbedDock);
	}
	
	public final TabDock getLeftLowerTabbedDock ()
	{
		return (leftLowerTabbedDock);
	}
	
	public final TabDock getRightTabbedDock ()
	{
		return (rightTabbedDock);
	}
	
	protected FloatDockModel 	dockModel
		= new FloatDockModel ("workspace");
	
	protected TabDock			centerTabbedDock
		= new TabDock ();

	protected TabDock			bottomTabbedDock
		= new TabDock ();

	protected TabDock			leftUpperTabbedDock
		= new TabDock ();

	protected TabDock			leftLowerTabbedDock
		= new TabDock ();

	protected TabDock			rightTabbedDock
		= new TabDock ();
	
	protected SplitDock			centerSplitDock
		= new SplitDock ();

	protected SplitDock			bottomSplitDock
		= new SplitDock ();

	protected SplitDock			leftSplitDock
		= new SplitDock ();

	protected SplitDock			rightSplitDock
		= new SplitDock ();

	protected SplitDock			totalSplitDock
		= new SplitDock ();

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
	 * Constructs a <CODE>DockingFrame</CODE> instance.
	 * 
	 * @param filename			The base path of the resources.
	 */
	protected DockingFrame (final String filename)
	{
		super (filename);
		
		dockModel.addOwner ("frame", frame);
	
		DockingManager.setDockModel (dockModel);
		
		centerSplitDock.addChildDock (centerTabbedDock, new Position (Position.CENTER));
		centerSplitDock.addChildDock (rightTabbedDock, new Position (Position.RIGHT));
		centerSplitDock.setDividerLocation(800);
		bottomSplitDock.addChildDock (bottomTabbedDock, new Position (Position.CENTER));
		rightSplitDock.addChildDock (centerSplitDock, new Position (Position.CENTER));
		rightSplitDock.addChildDock (bottomSplitDock, new Position (Position.BOTTOM));
		rightSplitDock.setDividerLocation(650);
		leftSplitDock.addChildDock (leftUpperTabbedDock, new Position (Position.TOP));
		leftSplitDock.addChildDock (leftLowerTabbedDock, new Position (Position.BOTTOM));
		leftSplitDock.setDividerLocation (300);
		totalSplitDock.addChildDock (leftSplitDock, new Position (Position.LEFT));
		totalSplitDock.addChildDock (rightSplitDock, new Position (Position.RIGHT));
		totalSplitDock.setDividerLocation(300);
		
		dockModel.addRootDock ("totalDock", totalSplitDock, frame);

		FloatDock floatDock = dockModel.getFloatDock(frame);
		floatDock.setChildDockFactory(new LeafDockFactory(false));

		frame.getContentPane().add (BorderLayout.CENTER, totalSplitDock);
		frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		frame.setSize (1440, 900);
		frame.setJMenuBar (menuBar);
		
		frame.addWindowListener (new WindowAdapter ()
			{
				public void windowClosed (WindowEvent event)
				{
					MenuManager.unmanage (frame);
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
}