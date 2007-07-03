/*
 * Copyright (c) Thomas Parker, 2005-2007.
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
 * Created on Apr 29, 2005
 */
package pcgen.base.graph.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.monitor.GraphEditMonitor;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DeleteEdgeCommand is a Command which, when executed, will delete a Edge
 * from a Graph.
 * 
 * Note that deletion of the Edge may have side effects upon the Nodes to which
 * the Edge was connected (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in DeleteEdgeCommand will
 * track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the deletion of one Edge causes a Node to also be deleted,
 * the returned UndoableEdit will represent both the Edge removal and the Node
 * removal (and the undo() and redo() methods of the UndoableEdit would undo and
 * redo both deletions)
 */
public class DeleteEdgeCommand<N, ET extends Edge<N>> implements Command
{

	/**
	 * The Graph on which this DeleteEdgeCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Edge to be deleted from the Graph when this DeleteEdgeCommand is
	 * executed.
	 */
	private final ET edge;

	/**
	 * The name of this DeleteEdgeCommand. Intended to be accessible to the end
	 * user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new DeleteEdgeCommand with the given Name, Graph in which the
	 * Deletion will take place, and Edge to be removed from the Graph. The
	 * given Graph and Edge must not be null. Upon execution, the given Edge
	 * will be removed from the Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Deletion will take place
	 * @param ge
	 *            The Edge to be removed from the Graph
	 */
	public DeleteEdgeCommand(String editName, Graph<N, ET> g, ET ge)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (ge == null)
		{
			throw new IllegalArgumentException("Edge cannot be null");
		}
		graph = g;
		edge = ge;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Delete Graph Edge";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the DeleteEdgeCommand on the Graph provided during Command
	 * construction. Returns an UndoableEdit indicating the changes that took
	 * place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		/*
		 * Note that this method of execution (passively collecting concurrent
		 * changes) is NOT thread safe.
		 * 
		 * FUTURE So consider, should this Command (and other similar ones) be
		 * factories, just asking this class (or a subclass) for a
		 * GraphEditMonitor? That would allow this to be thread safe by creating
		 * an optional GraphEditMonitor (could be inserted by subclasses) that
		 * ensures that the Graph edits come from the same thread... (saving the
		 * thread they were created in and checking calls to *Added and *Removed
		 * to ensure that they are the same Thread.)
		 */
		GraphEditMonitor<N, ET> edit =
				GraphEditMonitor.getGraphEditMonitor(graph);
		boolean removed = graph.removeEdge(edge);
		if (!removed)
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain Edge to be deleted");
		}
		graph.removeGraphChangeListener(edit);
		return edit.getEdit(name);
	}

	/**
	 * Returns the user-presentable name for this Command
	 * 
	 * @see pcbase.lang.Command#getPresentationName()
	 */
	public String getPresentationName()
	{
		return name;
	}
}