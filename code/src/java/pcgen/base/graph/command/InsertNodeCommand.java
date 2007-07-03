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
 * An InsertNodeCommand is a Command which, when executed, will insert a Node
 * into a Graph.
 * 
 * Note that insertion of the Node may have side effects upon other objects in
 * the Graph (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in InsertNodeCommand will
 * track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the addition of one Node causes another Node to be
 * deleted, the returned UndoableEdit will represent both the Node addition and
 * the Node deletion (and the undo() and redo() methods of the UndoableEdit
 * would undo and redo both changes).
 */
public class InsertNodeCommand<N, ET extends Edge<N>> implements Command
{

	/**
	 * The Graph on which this InsertNodeCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Node to be inserted into the Graph when this InsertNodeCommand is
	 * executed.
	 */
	private final N node;

	/**
	 * The name of this InsertNodeCommand. Intended to be accessible to the end
	 * user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new InsertNodeCommand with the given Name, Graph in which the
	 * Insertion will take place, and Node to be added to the Graph. The given
	 * Graph and Node must not be null. Upon execution, the given Node will be
	 * inserted into the Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Insertion will take place
	 * @param newNode
	 *            The Node to be added to the Graph
	 */
	public InsertNodeCommand(String editName, Graph<N, ET> g, N newNode)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph Cannot be null");
		}
		if (newNode == null)
		{
			throw new IllegalArgumentException("GraphNode Cannot be null");
		}
		graph = g;
		node = newNode;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Insert Graph Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the InsertNodeCommand on the Graph provided during Command
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
		boolean added = graph.addNode(node);
		if (!added)
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not allow addition of GraphNode to be inserted");
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