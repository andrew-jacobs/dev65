package uk.co.demon.obelisk.xide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import uk.co.demon.obelisk.xide.swing.DesktopFrame;
import uk.co.demon.obelisk.xide.swing.MenuManager;

public class FrameWindow extends DesktopFrame
{

	protected FrameWindow ()
	{
		super ("uk.co.demon.obelisk.xide.FrameWindow");
		// Construct the menu bar
		menuBar.add (fileMenu);
		menuBar.add (editMenu);
		menuBar.add (viewMenu);
		menuBar.add (buildMenu);
		menuBar.add (debugMenu);
		menuBar.add (windowMenu);
		menuBar.add (helpMenu);
		
		// Construct File menu
		fileMenu.add (fileNewMenu);
		fileMenu.add (fileOpenItem);
		fileMenu.add (fileCloseItem);
		fileMenu.addSeparator ();
		fileMenu.add (fileSaveItem);
		fileMenu.add (fileSaveAsItem);
		fileMenu.addSeparator ();
		fileMenu.add (fileExitItem);
		
		fileNewMenu.add (fileNewProjectItem);
		fileNewMenu.add (fileNewDocumentItem);
				
		fileOpenItem.setEnabled (false);
		fileCloseItem.setEnabled (false);
		fileSaveItem.setEnabled (false);
		fileSaveAsItem.setEnabled (false);

		fileNewDocumentItem.setEnabled (false);

		// Construct Edit menu
		editMenu.add (editUndo);
		editMenu.add (editRedo);
		editMenu.addSeparator ();
		editMenu.add (editCut);
		editMenu.add (editCopy);
		editMenu.add (editPaste);
		editMenu.add (editDelete);
		editMenu.addSeparator ();
		editMenu.add (editSelectAll);
		
		editUndo.setEnabled (false);
		editRedo.setEnabled (false);
		editCut.setEnabled (false);
		editCopy.setEnabled (false);
		editPaste.setEnabled (false);
		editDelete.setEnabled (false);
		editSelectAll.setEnabled (false);
		
		// Construct View menu
//		viewMenu.add (viewCalendar);
//		viewMenu.add (viewXmlText);
//		viewMenu.add (viewXmlTree);

		// Construct Build menu
		
		// Construct Debug menu

		// Construct Windows menu
		windowMenu.add (windowCascade);
		windowMenu.add (windowTileHorz);
		windowMenu.add (windowTileVert);
		windowMenu.add (windowArrange);

		windowCascade.setEnabled (false);
		windowTileHorz.setEnabled (false);
		windowTileVert.setEnabled (false);
		windowArrange.setEnabled (false);

		// Construct Help menu
		helpMenu.add (helpContents);
		helpMenu.addSeparator ();
		helpMenu.add (helpAbout);
		
		helpContents.setEnabled (false);
		
		// Contruct Toolbar
		toolBar.add (newButton);
		toolBar.add (openButton);
		toolBar.add (saveButton);
		toolBar.add (saveAsButton);
		toolBar.addSeparator ();
		toolBar.add (cutButton);
		toolBar.add (copyButton);
		toolBar.add (pasteButton);
		toolBar.add (deleteButton);
		toolBar.addSeparator ();
		toolBar.add (undoButton);
		toolBar.add (redoButton);
//		toolBar.addSeparator ();
				
		newButton.setEnabled (false);
		saveButton.setEnabled (false);
		saveAsButton.setEnabled (false);
		
		undoButton.setEnabled (false);
		redoButton.setEnabled (false);
		cutButton.setEnabled (false);
		copyButton.setEnabled (false);
		pasteButton.setEnabled (false);
		deleteButton.setEnabled (false);
		
		//viewXmlText.setEnabled (false);
		//viewXmlTree.setEnabled (false);
		
		//toolsReformat.setEnabled (false);
		//toolsValidate.setEnabled (false);
		
		contentPanel.add (BorderLayout.NORTH, toolBar);
			
		frame.setSize (750, 570);
		center ();

		MenuManager.manage (frame, fileNewDocumentItem, newButton, 
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{
						TextWindow.forFile (FrameWindow.this, new TextFile ()).show ();
					}
				});

		MenuManager.manage (frame, fileOpenItem, openButton, 
				new ActionListener ()
				{
					public void actionPerformed (ActionEvent event)
					{					
						// chooser.addChoosableFileFilter (new XmlFileFilter ());
						
						if (chooser.showOpenDialog (frame) == JFileChooser.APPROVE_OPTION) {
							chooser.getSelectedFile ();
						}
					}
				});
	}

	protected JMenu				fileNewMenu
		= createMenu ("fileNew");

	protected JMenuItem			fileNewProjectItem
		= createItem ("fileNewProject");
	
	protected JMenuItem			fileNewDocumentItem
		= createItem ("fileNewDocument");
	
	protected JMenuItem			fileOpenItem
		= createItem ("fileOpen");
	
	protected JMenuItem			fileCloseItem
		= createItem ("fileClose");
	
	protected JMenuItem			fileSaveItem
		= createItem ("fileSave");
	
	protected JMenuItem			fileSaveAsItem
		= createItem ("fileSaveAs");
	
	protected final	JMenu		editMenu
		= createMenu ("edit");
	
	protected JMenuItem			editUndo
		= createItem ("editUndo");
	
	protected JMenuItem			editRedo
		= createItem ("editRedo");
	
	protected JMenuItem			editCut
		= createItem ("editCut");
	
	protected JMenuItem			editCopy
		= createItem ("editCopy");
	
	protected JMenuItem			editPaste
		= createItem ("editPaste");
	
	protected JMenuItem			editDelete
		= createItem ("editDelete");
	
	protected JMenuItem			editSelectAll
		= createItem ("editSelectAll");
	
	protected JMenu				viewMenu
		= createMenu ("view");
	
//	protected JMenuItem			viewCalendar
//		= createItem ("viewCalendar");
//	
//	protected JMenuItem			viewXmlText
//		= createItem ("viewXmlText");
//	
//	protected JMenuItem			viewXmlTree
//		= createItem ("viewXmlTree");
	
	protected JMenu				buildMenu
		= createMenu ("build");
	
	protected JMenu				debugMenu
		= createMenu ("debug");
	
//	protected JMenuItem			toolsReformat
//		= createItem ("toolsReformat");
//	
//	protected JMenuItem			toolsValidate
//		= createItem ("toolsValidate");
//	
//	protected JMenu 			toolsConvert
//		= createMenu ("toolsConvert");
	
	protected JMenuItem			windowCascade
		= createItem ("windowCascade");
	
	protected JMenuItem			windowTileHorz
		= createItem ("windowTileHorz");
	
	protected JMenuItem			windowTileVert
		= createItem ("windowTileVert");
	
	protected JMenuItem			windowArrange
		= createItem ("windowArrange");
	
	protected JMenuItem			helpContents
		= createItem ("helpContents");
	
	protected JMenuItem			helpAbout
			= createItem ("helpAbout");
	
	protected JToolBar			toolBar
		= new JToolBar ();
	
	protected JButton			newButton
		= createButton ("newButton");
	
	protected JButton			openButton
		= createButton ("openButton");
	
	protected JButton			saveButton
		= createButton ("saveButton");
	
	protected JButton			saveAsButton
		= createButton ("saveAsButton");
	
	protected JButton			cutButton
		= createButton ("cutButton");
	
	protected JButton			copyButton
		= createButton ("copyButton");
	
	protected JButton			pasteButton
		= createButton ("pasteButton");
	
	protected JButton			deleteButton
		= createButton ("deleteButton");
	
	protected JButton			undoButton
		= createButton ("undoButton");
	
	protected JButton			redoButton
		= createButton ("redoButton");
		
	protected JFileChooser		chooser
		= new JFileChooser ();
}
