package uk.co.demon.obelisk.xide.swing;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

/**
 * @author	BitWise
 * @version	$Id$
 */
public abstract class InternalFrame extends Window
{
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
		frame.setVisible (true);
	}

	/**
	 * Returns the associated <CODE>JInternalFrame</CODE> instance.
	 * 
	 * @return	The associated <CODE>JInternalFrame</CODE> instance.
	 */
	public JInternalFrame getFrame ()
	{
		return (frame);
	}

	/**
	 * The associated SWING <CODE>JInternalFrame</CODE> component.
	 */
	protected JInternalFrame		frame
		= createInternalFrame ("frame");

	/**
	 * Constructs an <CODE>InternalFrame</CODE> instance. The frame will
	 * automatically unmanage any common menu items it uses when it closes.
	 * 
	 * @param	filename			Base name of the properties file.
	 */
	protected InternalFrame (
	final String		filename)
	{
		super (filename);
		
		frame.addInternalFrameListener (
			new InternalFrameAdapter () {
				public void internalFrameClosed (InternalFrameEvent event)
				{
					MenuManager.unmanage (frame);
				}
			});
	}
}
