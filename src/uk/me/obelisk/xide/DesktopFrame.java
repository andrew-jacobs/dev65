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

 package uk.me.obelisk.xide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import uk.me.obelisk.xide.swing.DockingFrame;

public class DesktopFrame extends DockingFrame
{
	public DesktopFrame ()
	{
		super ("uk.me.obelisk.xide.FrameWindow");
	
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

		fileNewDocumentItem.setEnabled (true);

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
				
		newButton.setEnabled (false);
		saveButton.setEnabled (false);
		saveAsButton.setEnabled (false);
		
		undoButton.setEnabled (false);
		redoButton.setEnabled (false);
		cutButton.setEnabled (false);
		copyButton.setEnabled (false);
		pasteButton.setEnabled (false);
		deleteButton.setEnabled (false);
		
		frame.getContentPane ().add (BorderLayout.NORTH, toolBar);
			
		fileNewDocumentItem.addActionListener (new ActionListener ()
		{
			public void actionPerformed (ActionEvent event)
			{
				new SourceView (DesktopFrame.this);
			}
		});
		
		stackView = new StackView (this);	
		memoryView = new MemoryView (this);
		registerView = new RegisterView (this);
		projectView = new ProjectView (this);
		
		errorView = new ErrorView (this);
		outputView = new OutputView (this);
		
		helpView = new HelpView (this);
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

	protected JMenu				buildMenu
		= createMenu ("build");

	protected JMenu				debugMenu
		= createMenu ("debug");

	protected JMenu				windowMenu
		= createMenu ("window");

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
	
	protected ProjectView		projectView;
		
	protected RegisterView		registerView;
	
	protected StackView			stackView;
	
	protected MemoryView		memoryView;
	
	protected OutputView		outputView;
	
	protected ErrorView			errorView;
	
	protected HelpView			helpView;
}