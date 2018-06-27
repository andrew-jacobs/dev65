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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;

import uk.co.demon.obelisk.xide.swing.event.EnablerListener;

/**
 * The <CODE>MenuManager</CODE> class provides thread which executes in the
 * background to keep menu items and tool bar buttons in synchronisation with
 * the application state.
 *  
 * @author	BitWise
 */
public final class MenuManager
{
	/**
	 * A <CODE>EnablerListener</CODE> that always indicated enabled.
	 */
	public final static EnablerListener ALWAYS_ENABLED
		= new EnablerListener ()
		{
			public boolean isEnabled ()
			{
				return (true);
			}
		};
		
	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> should be managed for
	 * the <CODE>JFrame</CODE>. The <CODE>EnablerListener</CODE> and
	 * <CODE>ActionListener</CODE> are used to handle status gathering and
	 * event processing.
	 *  
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param 	action			The event listener.
	 */
	public static void manage (JFrame frame, JMenuItem item, ActionListener action)
	{
		manage (frame, item, null, ALWAYS_ENABLED, action);
	}
	
	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> should be managed for
	 * the <CODE>JFrame</CODE>. The <CODE>EnablerListener</CODE> and
	 * <CODE>ActionListener</CODE> are used to handle status gathering and
	 * event processing.
	 *  
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param	enabler			The menu item enabler.
	 * @param 	action			The event listener.
	 */
	public static void manage (JFrame frame, JMenuItem item, EnablerListener enabler, ActionListener action)
	{
		manage (frame, item, null, enabler, action);
	}
	
	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> and <CODE>JButton</CODE>
	 * should be managed for the <CODE>JFrame</CODE>. The <CODE>EnablerListener</CODE>
	 * and <CODE>ActionListener</CODE> are used to handle status gathering and
	 * event processing.
	 *  
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param	button			The <CODE>JButton</CODE> to manage
	 * @param 	action			The event listener.
	 */
	public static void manage (JFrame frame, JMenuItem item, JButton button, ActionListener action)
	{
		manage (frame, item, button, ALWAYS_ENABLED, action);
	}
	
	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> and <CODE>JButton</CODE>
	 * should be managed for the <CODE>JFrame</CODE>. The <CODE>EnablerListener</CODE>
	 * and <CODE>ActionListener</CODE> are used to handle status gathering and
	 * event processing.
	 *  
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param	button			The <CODE>JButton</CODE> to manage
	 * @param	enabler			The menu item enabler.
	 * @param 	action			The event listener.
	 */
	public static void manage (JFrame frame, JMenuItem item, JButton button, EnablerListener enabler, ActionListener action)
	{
		ControlData		control;
		
		synchronized (lock) {
			if ((control = (ControlData) data.get (item)) == null)
				data.put (item, control = new ControlData (item, button));

			control.addEnabler (frame, enabler);
			control.addAction (frame, action);
		}
	}
	
	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> should be managed for
	 * the <CODE>JInternalFrame</CODE>. The <CODE>ActionListener</CODE>
	 * is used to handle status gathering and event processing.
	 *  
	 * @param 	frame			The owning <CODE>JInternalFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param	enabler			The menu item enabler.
	 * @param 	action			The event listener.
	 */
	public static void manage (JInternalFrame frame, JMenuItem item, EnablerListener enabler, ActionListener action)
	{
		manage (frame, item, null, enabler, action);
	}

	/**
	 * Indicates that the given <CODE>JMenuItem</CODE> and <CODE>JButton</CODE>
	 * should be managed for the <CODE>JInternalFrame</CODE>. The <CODE>ActionListener</CODE>
	 * is used to handle status gathering and event processing.
	 *  
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 * @param 	item			The <CODE>JMenuItem</CODE> to manage.
	 * @param	button			The <CODE>JButton</CODE> to manage
	 * @param	enabler			The menu item enabler.
	 * @param 	action			The event listener.
	 */
	public static void manage (JInternalFrame frame, JMenuItem item, JButton button, EnablerListener enabler, ActionListener action)
	{
		ControlData		control;
		
		synchronized (lock) {
			if ((control = (ControlData) data.get (item)) == null)
				data.put (item, control = new ControlData (item, button));
				
			control.addEnabler (frame, enabler);
			control.addAction (frame, action);
		}
	}

	/**
	 * Causes any management information for the indicated <CODE>JFrame</CODE>
	 * to be removed.
	 * 
	 * @param 	frame			The owning <CODE>JFrame</CODE>.
	 */
	public static void unmanage (JFrame frame)
	{
		synchronized (lock) {
			for (Enumeration<JMenuItem> cursor = data.keys (); cursor.hasMoreElements();) {
				JMenuItem 		item = cursor.nextElement ();
				ControlData		control = data.get (item);

				control.removeEnabler (frame);
				control.removeAction (frame);
			}
		}
	}
	
	/**
	 * Causes any management information for the indicated <CODE>JInternalFrame</CODE>
	 * to be removed.
	 * 
	 * @param 	frame			The owning <CODE>JInternalFrame</CODE>.
	 * @since	TFP 1.0
	 */
	public static void unmanage (JInternalFrame frame)
	{
		synchronized (lock) {
			for (Enumeration<JMenuItem> cursor = data.keys (); cursor.hasMoreElements();) {
				JMenuItem 		item = cursor.nextElement ();
				ControlData		control = data.get (item);

				control.removeEnabler (frame);
				control.removeAction (frame);
			}
		}
	}
	
	/**
	 * Causes an immediate update of all the menu items and buttons that are
	 * under management. Controls that have no listeners are removed from the
	 * management set.
	 * @since	TFP 1.0
	 */
	public static void update ()
	{
		synchronized (lock) {
			for (Enumeration<JMenuItem> cursor = data.keys (); cursor.hasMoreElements();) {
				JMenuItem item	= cursor.nextElement ();
				ControlData control = data.get (item);

				control.updateState ();
				if (!control.hasActions ()) {
					data.remove (item);
					break;
				}
			}
		}		
	}
	
	/**
	 * An instance of the <CODE>Worker</CODE> class executes on a background
	 * thread periodically checking the state of each registered menu item.
	 * @since	TFP 1.0
	 */
	private static class Worker extends Thread
	{
		/**
		 * Constructs a <CODE>Worker</CODE> and sets its properties.
		 * @since	TFP 1.0
		 */
		public Worker ()
		{
			setName ("MenuManger");
			setDaemon (true);
			setPriority (Thread.MIN_PRIORITY);
		}
		
		/**
		 * Performs a period update of registered menu items.
		 * @since	TFP 1.0 
		 */
		public void run ()
		{
			for (;;) {
				try {
					update ();
					sleep (10);
				}
				catch (InterruptedException error) {
					/* Ignore interrupt */ ;
				}
			}
		}
	}
	
	/**
	 * The <CODE>ControlData</CODE> class keeps a record of the
	 * <CODE>ManagedActionListeners</CODE> registered for a control.
	 * If a listener belongs to a <CODE>JInternalFrame</CODE> then
	 * it will only recieve events when it is active.
	 * 
	 * @since	TFP 1.0
	 */
	private static final class ControlData implements ActionListener
	{
		/**
		 * Constructs a <CODE>ControlData</CODE> instance for the given
		 * <CODE>JMenuItem</CODE> and option <CODE>JButton</CODE>.
		 * 
		 * @param 	item			The <CODE>JMenuItem</CODE> instance.
		 * @param 	button			A <CODE>JButton</CODE> or <CODE>null</CODE>.
		 * @since	TFP 1.0
		 */
		public ControlData (JMenuItem item, JButton button)
		{
			this.item = item;
			this.item.addActionListener (this);
			
			if ((this.button = button) != null)
				this.button.addActionListener (this);
		}
		
		/**
		 * Adds the given <CODE>EnablerListener</CODE> to the set
		 * for this control indexed by its <CODE>JFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JFrame</CODE>.	
		 * @param 	enabler			The <CODE>EnablerListener</CODE>.
		 */
		public void addEnabler (JFrame frame, EnablerListener enabler)
		{
			enablers.put (frame, enabler);
		}
		
		/**
		 * Adds the given <CODE>EnablerListener</CODE> to the set
		 * for this control indexed by its <CODE>JInternalFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JInternalFrame</CODE>.	
		 * @param 	enabler			The <CODE>EnablerListener</CODE>.
		 */
		public void addEnabler (JInternalFrame frame, EnablerListener enabler)
		{
			enablers.put (frame, enabler);
		}
		
		/**
		 * Remove the <CODE>EnablerListener</CODE> associated with
		 * the indicated <CODE>JFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JFrame</CODE>
		 * @since	TFP 1.0
		 */
		public void removeEnabler (JFrame frame)
		{
			enablers.remove (frame);
		}
		
		/**
		 * Remove the <CODE>EnablerListener</CODE> associated with
		 * the indicated <CODE>JInternalFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JInternalFrame</CODE>
		 * @since	TFP 1.0
		 */
		public void removeEnabler (JInternalFrame frame)
		{
			enablers.remove (frame);
		}
		
		/**
		 * Adds the given <CODE>ActionListener</CODE> to the set
		 * for this control indexed by its <CODE>JFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JFrame</CODE>.	
		 * @param 	action			The <CODE>ActionListener</CODE>.
		 */
		public void addAction (JFrame frame, ActionListener action)
		{
			actions.put (frame, action);
		}
		
		/**
		 * Adds the given <CODE>ActionListener</CODE> to the set
		 * for this control indexed by its <CODE>JInternalFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JInternalFrame</CODE>.	
		 * @param 	action			The <CODE>ActionListener</CODE>.
		 */
		public void addAction (JInternalFrame frame, ActionListener action)
		{
			actions.put (frame, action);
		}
		
		/**
		 * Remove the <CODE>ActionListener</CODE> associated with
		 * the indicated <CODE>JFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JFrame</CODE>
		 * @since	TFP 1.0
		 */
		public void removeAction (JFrame frame)
		{
			actions.remove (frame);
		}
		
		/**
		 * Remove the <CODE>ActionListener</CODE> associated with
		 * the indicated <CODE>JInternalFrame</CODE>.
		 * 
		 * @param 	frame			The owning <CODE>JInternalFrame</CODE>
		 * @since	TFP 1.0
		 */
		public void removeAction (JInternalFrame frame)
		{
			actions.remove (frame);
		}
		
		/**
		 * Determines if any <CODE>ActionListener</CODE> instances
		 * have been registered for this control.
		 * 
		 * @return	<CODE>true</CODE> if one or more listeners have been
		 * 			registered.
		 * @since	TFP 1.0
		 */
		public boolean hasActions ()
		{
			return (actions.size () > 0);
		}

		/**
		 * Determines that state of a menu item and its associated button by
		 * calling the appropriate <CODE>EnablerListener</CODE>.
		 * @since	TFP 1.0
		 */
		public void updateState ()
		{
			boolean		state = false;
			
			synchronized (lock) {
				for (Enumeration<Object> cursor = actions.keys (); cursor.hasMoreElements ();) {
					Object frame = cursor.nextElement ();
					
					if ((frame instanceof JInternalFrame) && !((JInternalFrame) frame).isSelected ())
						continue;

					if (((EnablerListener) enablers.get (frame)).isEnabled()) {
						state = true;
						break;
					}
				}
			}
			item.setEnabled (state);
			if (button != null)
				button.setEnabled (state);
		}
		
		/**
		 * Passes the event on to the active listener.
		 * 
		 * @param	event			The <CODE>ActionEvent</CODE> data.
		 * @since	TFP 1.0
		 */
		public void actionPerformed (ActionEvent event)
		{
			synchronized (lock) {
				for (Enumeration<Object> cursor = actions.keys(); cursor.hasMoreElements();) {
					Object		frame = cursor.nextElement ();
					
					if ((frame instanceof JInternalFrame) && !((JInternalFrame) frame).isSelected ())
						continue;

					((ActionListener) actions.get (frame)).actionPerformed (event);
				}
			}
			update ();
		}
		
		/**
		 * The <CODE>JMenuItem</CODE> being managed.
		 */
		private JMenuItem 			item;
		
		/**
		 * The <CODE>JButton</CODE> being managed or <CODE>null</CODE>.
		 */
		private JButton				button;
		
		/**
		 * The <CODE>EnablerListener</CODE> instances indexed by frame.
		 */
		private Hashtable<Object, EnablerListener> enablers
			= new Hashtable<Object, EnablerListener> ();
		
		/**
		 * The <CODE>ActionListener</CODE> instances indexed by frame.
		 */
		private Hashtable<Object, ActionListener> actions
			= new Hashtable<Object, ActionListener> ();
	}
	
	/**
	 * Lock object used internally.
	 * @since	TFP 1.0
	 */
	private static Object		lock	= new Object ();
	
	/**
	 * A daemon <CODE>Thread</CODE> used to update menus dynamically.
	 * @since	TFP 1.0
	 */
	private static Thread		worker	= new Worker ();

	/**
	 * A <CODE>Hashtable</CODE> used to map from a <CODE>JMenuItem</CODE>
	 * instance to its <CODE>ControlData</CODE> instance.
	 * @since	TFP 1.0
	 */
	private static Hashtable<JMenuItem, ControlData> data
		= new Hashtable<JMenuItem, ControlData> ();
	
	/**
	 * Ensures that no instances can be constructed.
	 * @since	TFP 1.0
	 */
	private MenuManager ()
	{ }

	/**
	 * Kicks off the worker thread.
	 * @since	TFP 1.0
	 */
	static {
		worker.start ();
	}
}