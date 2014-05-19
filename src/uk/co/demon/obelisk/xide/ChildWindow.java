// Copyright (C),2005-2012 HandCoded Software Ltd.
// All rights reserved.
//
// This software is the confidential and proprietary information of HandCoded
// Software Ltd. ("Confidential Information").  You shall not disclose such
// Confidential Information and shall use it only in accordance with the terms
// of the license agreement you entered into with HandCoded Software.
//
// HANDCODED SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
// SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
// LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
// PARTICULAR PURPOSE, OR NON-INFRINGEMENT. HANDCODED SOFTWARE SHALL NOT BE
// LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
// OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.

package uk.co.demon.obelisk.xide;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import uk.co.demon.obelisk.xide.swing.InternalFrame;
import uk.co.demon.obelisk.xide.swing.MenuManager;
import uk.co.demon.obelisk.xide.swing.event.EnablerListener;

/**
 * 
 * @author	BitWise
 * @version	$Id$
 * @since	TFP 1.2
 */
abstract class ChildWindow extends InternalFrame
{
	/**
	 * Returns the parent MDI frame window.
	 * 
	 * @return	The parent <CODE>FrameWindow</CODE>.
	 * @since	TFP 1.2
	 */
	public FrameWindow getParent ()
	{
		return (parent);
	}

	/**
	 * Constructs a <CODE>ChildWindow</CODE> of the indicated <COE>
	 * FrameWindow</CODE>.
	 * 
	 * @param	parent			The parent <CODE>FrameWindow</CODE> instance.
	 * @param	filename		The name of the file displayed.
	 * @since	TFP 1.2
	 */
	protected ChildWindow (FrameWindow parent, final String filename)
	{
		super (filename);
		
		(this.parent = parent).getDesktopPane ().add (this.frame);
		
		MenuManager.manage (frame, parent.windowCascade,
				childWindowsActive,
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{
						ChildWindow.this.parent.cascade ();
					}
				});
		
		MenuManager.manage (frame, parent.windowTileHorz,
				childWindowsActive,
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{
						ChildWindow.this.parent.tileHorizontally ();
					}
				});
		
		MenuManager.manage (frame, parent.windowTileVert,
				childWindowsActive,
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{
						ChildWindow.this.parent.tileVertically ();
					}
				});
		
		MenuManager.manage (frame, parent.windowArrange,
				childWindowsActive,
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{
						ChildWindow.this.parent.arrangeIcons ();
					}
				});
		
		frame.addInternalFrameListener (
				new InternalFrameAdapter () {
					public void internalFrameClosed (InternalFrameEvent event)
					{
						windows.remove (ChildWindow.this);
					}
				});
		
		windows.add (this);
	}
	
	/**
	 * The set of all open child windows.
	 * @since	TFP 1.2
	 */
	private static Vector<ChildWindow> windows
		= new Vector<ChildWindow> ();
	
	/**
	 * An <CODE>EnablerListner</CODE> that is <CODE>true</CODE> when one
	 * or more <CODE>ChildWindow</CODE> instances exists.
	 * @since	TFP 1.2
	 */
	private static EnablerListener		childWindowsActive
		= new EnablerListener ()
		{
			public boolean isEnabled ()
			{
				return (windows.size () > 0);
			}		
		};

	/**
	 * The parent <CODE>FrameWindow</CODE>.
	 * @since	TFP 1.2
	 */
	private FrameWindow		parent;
}