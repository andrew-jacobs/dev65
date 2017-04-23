package uk.co.demon.obelisk.xide;

import javax.swing.UIManager;

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
}