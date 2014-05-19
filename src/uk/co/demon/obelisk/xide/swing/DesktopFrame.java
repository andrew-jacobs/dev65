package uk.co.demon.obelisk.xide.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DesktopManager;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class DesktopFrame extends Frame
{
	/**
	 * Provides access to the underlying <CODE>JDesktopPane</CODE>.
	 * 
	 * @return	The underlying <CODE>JDesktopPane</CODE>.
	 * @since	TFP 1.0 
	 */
	public final JDesktopPane getDesktopPane ()
	{
		return (desktopPane);
	}
	
	/**
	 * Provides access to the underlying <CODE>DesktopManager</CODE>.
	 * 
	 * @return	The underlying <CODE>DesktopManager</CODE>.
	 * @since	TFP 1.0 
	 */
	public final DesktopManager getDesktopManager ()
	{
		return (desktopPane.getDesktopManager ());
	}
	
	/**
	 * Causes the visible and non-iconised windows to be cascade across
	 * the desktop.
	 * @since	TFP 1.0
	 */
	public void cascade ()
	{
		int				deskw	= desktopPane.getSize ().width;
		int				deskh	= desktopPane.getSize ().height;
		int				width	= (int)(deskw * SCALE);
		int				height	= (int)(deskh * SCALE);
		int				xpos	= 0;
		int				ypos	= 0;
		
		JInternalFrame [] frames = affectedFrames ();
		
		for (int index = 0; index < frames.length; ++index) {
			frames [index].setBounds (xpos, ypos, width, height);
			xpos += SPACE;
			ypos += SPACE;
			
			if (((xpos + width) > deskw) || ((ypos + height) > deskh))
				xpos = ypos = 0;
		}
	}
	
	/**
	 * Causes the visible and non-iconised windows to be tiles vertically
	 * across the desktop.
	 * @since	TFP 1.0
	 */
	public void tileVertically ()
	{
		JInternalFrame [] frames = affectedFrames ();
		
		if (frames.length > 0) {
			int				width	= desktopPane.getSize ().width / frames.length;
			int				height	= desktopPane.getSize ().height;
			
			for (int index = 0; index < frames.length; ++index) {
				frames [index].setBounds (index * width, 0, width, height);
			}
		}
	}
	
	/**
	 * Causes the visible and non-iconised windows to be tiles horizontally
	 * across the desktop.
	 * @since	TFP 1.0
	 */
	public void tileHorizontally ()
	{
		JInternalFrame [] frames = affectedFrames ();
		
		if (frames.length > 0) {
			int				width	= desktopPane.getSize ().width;
			int				height	= desktopPane.getSize ().height / frames.length;
			
			for (int index = 0; index < frames.length; ++index) {
				frames [index].setBounds (0, index * height, width, height);
			}
		}
	}
	
	/**
	 * TODO: Arrange Icons
	 *
	 */
	public void arrangeIcons ()
	{
	}

	/**
	 * The desktop pane instance.
	 * @since	TFP 1.0
	 */
	protected JDesktopPane		desktopPane
		= new JDesktopPane ();

	/**
	 * The 'Window' menu instance.
	 * @since	TFP 1.0
	 */
	protected JMenu			windowMenu
		= createMenu ("window");
	
	/**
	 * Seperator between command and frame list.
	 * @since	TFP 1.0
	 */
	protected JSeparator	separator
		= new JSeparator ();

	/**
	 * The 'Window>Cascade' menu item instance.
	 * @since	TFP 1.0
	 */
	protected JMenuItem		windowCascadeItem
		= createItem ("windowCascade");

	/**
	 * The 'Window>Tile Horizontally' menu item instance.
	 * @since	TFP 1.0
	 */
	protected JMenuItem		windowTileHorzItem
		= createItem ("windowTileHorz");

	/**
	 * The 'Window>Tile Vertically' menu item instance.
	 * @since	TFP 1.0
	 */
	protected JMenuItem		windowTileVertItem
		= createItem ("windowTileVert");

	/**
	 * Constructs a <CODE>DesktopFrame</CODE> instance.
	 * 
	 * @param	filename		The resource file base name.
	 * @since	TFP 1.0
	 */
	protected DesktopFrame (
	final String		filename)
	{
		super (filename);

		contentPanel.setLayout (new BorderLayout ());
		contentPanel.add (BorderLayout.CENTER, desktopPane);
		
		windowMenu.addMenuListener (
				new MenuListener ()
				{
					public void menuCanceled (MenuEvent event)
					{ }

					public void menuDeselected (MenuEvent event)
					{
						for (int index = 0; index < items.size (); ++index)
							windowMenu.remove ((Component) items.elementAt (index));
						
						items.clear ();
					}
	
					public void menuSelected (MenuEvent event)
					{
						boolean		swapped;
						
						JInternalFrame []	frames = desktopPane.getAllFrames ();
						
						do {
							swapped = false;

							for (int index = 1; index < frames.length; ++index) {
								JInternalFrame a	= frames [index - 1];
								JInternalFrame b	= frames [index];
								
								if (a.getTitle ().compareTo (b.getTitle ()) > 0) {
									frames [index - 1] = b;
									frames [index]     = a;
									
									swapped = true;
								}
							}
						} while (swapped);
						
						if (frames.length > 0) {
							addItem (separator);
							for (int index = 0; index < frames.length; ++index) {
								JCheckBoxMenuItem	item = new JCheckBoxMenuItem (frames [index].getTitle ());
								Icon icon = frames [index].getFrameIcon ();
								
								item.setState (frames [index].isSelected ());
								item.setIcon ((icon != null) ? icon : getIcon ("frame/blankIcon"));
						
								item.addActionListener (new WindowActivator (frames [index]));								
								addItem (item);
							}
						}
					}
					
					private Vector<Component> items	= new Vector<Component> ();
					
					private void addItem (Component item)
					{
						windowMenu.add (item);
						items.add (item);
					}
				});
	}
	
	/**
	 * <CODE>WindowActivator</CODE> instance are attached to the dynamically
	 * constructed 'Window' menu to cause a selected frame to become active.
	 * @since	TFP 1.0
	 */
	private class WindowActivator implements ActionListener
	{
		/**
		 * Constructs a <CODE>WindowActivator</CODE> instance.
		 * @param frame
		 */
		public WindowActivator (JInternalFrame frame)
		{
			this.frame = frame;
		}
		
		/**
		 * {@inheritDoc}
		 * @since	TFP 1.0
		 */
		public void actionPerformed (ActionEvent event)
		{
			try {
				frame.setIcon (false);
				frame.setSelected (true);
			}
			catch (Exception error) {
				/* Ignore veto */ ;
			}
			frame.toFront ();
		}
		
		/**
		 * The <CODE>JInternalFrame</CODE> under management.
		 * @since	TFP 1.0
		 */
		private JInternalFrame	frame;
	}
	
	/**
	 * Constant value used to determine cascaded window size.
	 * @since	TFP 1.0
	 */
	private static final double	SCALE	= 0.6;
	
	/**
	 * Constant value used to determine cascaded window spacing.
	 * @since	TFP 1.0
	 */
	private static final int	SPACE	= 30;
	
	/**
	 * Determines the subset of <CODE>JInternalFrame</CODE> instances that
	 * will be affected by a cascade or tile operation.
	 * 
	 * @return	An array of affected <CODE>JInternalFrame</CODE> instances.
	 * @since	TFP 1.0
	 */
	private JInternalFrame [] affectedFrames ()
	{
		Vector<JInternalFrame> vector = new Vector<JInternalFrame> ();
		JInternalFrame [] 	frames = desktopPane.getAllFrames ();
		
		for (int index = 0; index < frames.length; ++index) {
			if ((frames [index].isVisible () && !frames [index].isIcon ()))
				vector.add (frames [index]);
		}
	
		JInternalFrame []	result = new JInternalFrame [vector.size ()];
		vector.copyInto (result);
		
		return (result);	
	}
}
