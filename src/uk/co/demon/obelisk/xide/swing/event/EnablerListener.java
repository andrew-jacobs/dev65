package uk.co.demon.obelisk.xide.swing.event;

/**
 * The <CODE>MenuEnablerListener</CODE> interface provides a way to find out
 * the desired state of a component.
 * 
 * @author	Andrew Jacobs
 * @version	$Id$
 */
public interface EnablerListener
{
	/**
	 * Determines if the associated component should be enabled.
	 * 
	 * @return	<CODE>true</CODE> if the component should be enabled,
	 * 			<CODE>false</CODE> otherwise.
	 */
	public boolean isEnabled ();
}