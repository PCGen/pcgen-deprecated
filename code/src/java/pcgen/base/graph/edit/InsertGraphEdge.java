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
 * Created on Aug 22, 2004
 */
package pcgen.base.graph.edit;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * InsertGraphEdge is an UndoableEdit which represents the atomic insertion of
 * an Edge into a Graph.
 * 
 * 'Atomic' indicates that this UndoableEdit does not capture or reflect any
 * side-effects that took place during the insertion of the Edge into the Graph.
 * (This class does not actually perform the insertion of the Edge). In order to
 * capture the side-effects and have an UndoableEdit which reflects all of the
 * changes that took place to a Graph as a result of the insertion of an Edge,
 * see InsertEdgeCommand.
 * 
 * @see pcgen.base.graph.command.InsertEdgeCommand
 */
public class InsertGraphEdge<N, ET extends Edge<N>> extends
		AbstractUndoableEdit implements UndoableEdit
{

	/**
	 * The Edge added to the Graph
	 */
	private final ET edge;

	/**
	 * The Graph on which this InsertGraphEdge occurred
	 */
	private final Graph<N, ET> graph;

	/**
	 * The name of this InsertGraphEdge edit. Intended to be accessible to the
	 * end user as a name for this edit.
	 */
	private final String presentationName;

	/**
	 * Creates a new InsertGraphEdge Edit with the given Graph in which the
	 * Insertion took place, the Edge added to the Graph, and presentation name.
	 * The given Graph and Edge must not be null.
	 * 
	 * @param g
	 *            The Graph in which the Insertion took place
	 * @param ge
	 *            The Edge added to the Graph
	 * @param name
	 *            The edit name for this undoableEdit. This Edit will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 */
	public InsertGraphEdge(Graph<N, ET> g, ET he, String name)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (he == null)
		{
			throw new IllegalArgumentException("HyperEdge cannot be null");
		}
		graph = g;
		edge = he;
		if (name == null || name.trim().length() == 0)
		{
			presentationName = "Insert Graph Edge";
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
		graph.addEdge(edge);
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
		graph.removeEdge(edge);
	}

}