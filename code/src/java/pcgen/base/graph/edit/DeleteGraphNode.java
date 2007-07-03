/*
 * Copyright (c) Thomas Parker, 2004-2007.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Aug 21, 2004
 */
package pcgen.base.graph.edit;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.Graph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * DeleteGraphNode is an UndoableEdit which represents the atomic deletion of a
 * Node from a Graph.
 * 
 * 'Atomic' indicates that this UndoableEdit does not capture or reflect any
 * side-effects that took place during the removal of the Node from the Graph.
 * (This class does not actually perform the removal of the Node). In order to
 * capture the side-effects and have an UndoableEdit which reflects all of the
 * changes that took place to a Graph as a result of the removal of a Node, see
 * DeleteNodeCommand.
 * 
 * @see rpgmapgen.util.graph.command.DeleteNodeCommand
 */
public class DeleteGraphNode<N> extends AbstractUndoableEdit implements
		UndoableEdit
{

	/**
	 * The Node deleted from the Graph
	 */
	private final N node;

	/**
	 * The Graph on which this DeleteGraphNode occurred
	 */
	private final Graph<N, ?> graph;

	/**
	 * The name of this DeleteGraphNode edit. Intended to be accessible to the
	 * end user as a name for this edit.
	 */
	private final String presentationName;

	/**
	 * Creates a new DeleteGraphNode Edit with the given Graph in which the
	 * Deletion took place, the Node removed from the Graph, and presentation
	 * name. The given Graph and Node must not be null.
	 * 
	 * @param g
	 *            The Graph in which the Deletion took place
	 * @param gn
	 *            The Node removed from the Graph
	 * @param name
	 *            The edit name for this undoableEdit. This Edit will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 */
	public DeleteGraphNode(Graph<N, ?> g, N gn, String name)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (gn == null)
		{
			throw new IllegalArgumentException("GrapnNode cannot be null");
		}
		node = gn;
		graph = g;
		if (name == null || "".equals(name.trim()))
		{
			presentationName = "Delete Graph Node";
		}
		else
		{
			presentationName = name;
		}
	}

	/**
	 * Returns the user-presentable name for this UndoableEdit
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#getPresentationName()
	 */
	@Override
	public String getPresentationName()
	{
		return presentationName;
	}

	/**
	 * Performs the 'redo' action for this UndoableEdit.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#redo()
	 */
	@Override
	public void redo()
	{
		super.redo();
		graph.removeNode(node);
	}

	/**
	 * Performs the 'undo' action for this UndoableEdit.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#undo()
	 */
	@Override
	public void undo()
	{
		super.undo();
		graph.addNode(node);
	}

}