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
 * A DeleteNodeCommand is a Command which, when executed, will delete a Node
 * from a Graph.
 * 
 * Note that deletion of the Node may have side effects upon the Edges to which
 * the Node was connected (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in DeleteNodeCommand will
 * track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the deletion of one Node causes an Edge to also be
 * deleted, the returned UndoableEdit will represent both the Node removal and
 * the Edge removal (and the undo() and redo() methods of the UndoableEdit would
 * undo and redo both deletions).
 */
public class DeleteNodeCommand<N, ET extends Edge<N>> implements Command
{

	/**
	 * The Graph on which this DeleteNodeCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Node to be removed from the Graph when this DeleteNodeCommand is
	 * executed.
	 */
	private final N node;

	/**
	 * The name of this DeleteNodeCommand. Intended to be accessible to the end
	 * user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new DeleteNodeCommand with the given Name, Graph in which the
	 * Deletion will take place, and Node to be removed from the Graph. The
	 * given Graph and Node must not be null. Upon execution, the given Node
	 * will be removed from the Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Deletion will take place
	 * @param n
	 *            The Node to be removed from the Graph
	 */
	public DeleteNodeCommand(String editName, Graph<N, ET> g, N n)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (n == null)
		{
			throw new IllegalArgumentException("Node cannot be null");
		}
		graph = g;
		node = n;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Delete Graph Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the DeleteNodeCommand on the Graph provided during Command
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
		 */
		GraphEditMonitor<N, ET> edit =
				GraphEditMonitor.getGraphEditMonitor(graph);
		boolean removed = graph.removeNode(node);
		if (!removed)
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain GraphNode to be deleted");
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