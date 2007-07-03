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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.monitor.GraphEditMonitor;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An InsertEdgesCommand is a Command which, when executed, will insert a
 * Collection of Edges into a Graph.
 * 
 * Note that insertion of the Edges may have side effects upon other objects in
 * the Graph (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in InsertEdgesCommand will
 * track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the addition of one Edge causes another Edge to be
 * deleted, the returned UndoableEdit will represent both the Edge addition and
 * the Edge deletion (and the undo() and redo() methods of the UndoableEdit
 * would undo and redo both changes).
 */
public class InsertEdgesCommand<N, ET extends Edge<N>> implements Command
{

	/**
	 * The Graph on which this InsertEdgesCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Collection of Edges to be inserted into the Graph when this
	 * InsertEdgesCommand is executed.
	 */
	private final List<ET> edgeList;

	/**
	 * The name of this InsertEdgesCommand. Intended to be accessible to the end
	 * user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new InsertEdgesCommand with the given Name, Graph in which the
	 * Insertion will take place, and Collection of Edges to be added to the
	 * Graph. The given Graph must not be null. The given Collection must not be
	 * null or empty. Upon execution, the given Edges will be inserted into the
	 * Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Insertion will take place
	 * @param edgeCollection
	 *            The Edges to be added to the Graph
	 */
	public InsertEdgesCommand(String editName, Graph<N, ET> g,
		Collection<ET> edgeCollection)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (edgeCollection == null)
		{
			throw new IllegalArgumentException("Edge Collection cannot be null");
		}
		graph = g;
		edgeList = new ArrayList<ET>(edgeCollection);
		if (edgeList.isEmpty())
		{
			throw new IllegalArgumentException(
				"Edge Collection cannot be empty");
		}
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Insert Graph Edges";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the InsertEdgesCommand on the Graph provided during Command
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
		boolean added = false;
		for (ET edge : edgeList)
		{
			added |= graph.addEdge(edge);
		}
		if (!added)
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not add any Edges to be inserted");
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