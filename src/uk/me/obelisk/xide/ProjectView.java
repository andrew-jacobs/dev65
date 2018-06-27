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

import javax.swing.tree.DefaultMutableTreeNode;

import com.javadocking.dock.Position;

import uk.me.obelisk.xide.swing.DockableTree;
import uk.me.obelisk.xide.swing.DockingFrame;

public class ProjectView extends DockableTree
{
	public ProjectView (DockingFrame dockingFrame)
	{
		super ("project", "uk.me.obelisk.xide.ProjectView");
		
		root.add (includes);
		root.add (sources);
		root.add (libraries);
		
		dockingFrame.getLeftUpperTabbedDock ().addDockable (dockable, new Position (0));;
	}
	
	protected DefaultMutableTreeNode	includes
		= new DefaultMutableTreeNode ("Includes");
	
	protected DefaultMutableTreeNode	sources
		= new DefaultMutableTreeNode ("Sources");
	
	protected DefaultMutableTreeNode	libraries
		= new DefaultMutableTreeNode ("Libraries");
}