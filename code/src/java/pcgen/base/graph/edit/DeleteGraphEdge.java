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

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * DeleteGraphEdge is an UndoableEdit which represents the atomic deletion of a
 * Edge from a Graph.
 * 
 * 'Atomic' indicates that this UndoableEdit does not capture or reflect any
 * side-effects that took place during the removal of the Edge from the Graph.
 * (This class does not actually perform the removal of the Edge). In order to
 * capture the side-effects and have an UndoableEdit which reflects all of the
 * changes that took place to a Graph as a result of the removal of a Edge, see
 * DeleteGraphEdgeCommand.
 * 
 * @see rpgmapgen.util.graph.command.DeleteEdgeCommand
 */
public class DeleteGraphEdge<N, ET extends Edge<N>> extends
		AbstractUndoableEdit implements UndoableEdit
{

	/**
	 * The Edge deleted from the Graph
	 */
	private final ET edge;

	/**
	 * The Graph on which this DeleteGraphEdge occurred
	 */
	private final Graph<N, ET> graph;

	/**
	 * The name of this DeleteGraphEdge edit. Intended to be accessible to the
	 * end user as a name for this edit.
	 */
	private final String editName;

	/**
	 * Creates a new DeleteGraphEdge Edit with the given Graph in which the
	 * Deletion took place, the Edge removed from the Graph, and presentation
	 * name. The given Graph and Edge must not be null.
	 * 
	 * @param g
	 *            The Graph in which the Deletion took place
	 * @param he
	 *            The Edge removed from the Graph
	 * @param name
	 *            The edit name for this undoableEdit. This Edit will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 */
	public DeleteGraphEdge(Graph<N, ET> g, ET he, String name)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph Cannot be null");
		}
		if (he == null)
		{
			throw new IllegalArgumentException("HyperEdge Cannot be null");
		}
		edge = he;
		graph = g;
		if (name == null || name.trim().length() == 0)
		{
			editName = "Delete Graph Edge";
		}
		else
		{
			editName = name;
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
		return editName;
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
		graph.removeEdge(edge);
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
		graph.addEdge(edge);
	}

}