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

import java.util.Collection;
import java.util.List;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.monitor.GraphEditMonitor;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A PruneFromNodeCommand is a Command which, when executed, will delete a Node
 * and all Edges and Nodes only connected to the given Node from a
 * DirectionalGraph.
 * 
 * *WARNING* PruneFromNodeCommand does NOT perform a mark and sweep of the
 * Graph, it ASSUMES that the given Graph is Acyclic.
 * 
 * Note that deletion of the Node may have side effects upon the Edges to which
 * the Node was connected (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in PruneFromNodeCommand
 * will track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the deletion of one Node causes an Edge to also be
 * deleted, the returned UndoableEdit will represent both the Node removal and
 * the Edge removal (and the undo() and redo() methods of the UndoableEdit would
 * undo and redo both deletions).
 */
public class PruneFromNodeCommand<N, ET extends DirectionalEdge<N>> implements
		Command
{

	/**
	 * The DirectionalGraph on which this PruneFromNodeCommand will operate.
	 */
	private final DirectionalGraph<N, ET> graph;

	/**
	 * The Node to be removed from the Graph when this PruneFromNodeCommand is
	 * executed.
	 */
	private final N node;

	/**
	 * The name of this PruneFromNodeCommand. Intended to be accessible to the
	 * end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new PruneFromNodeCommand with the given Name, Graph in which
	 * the Prune will take place, the current Node in the Graph to be used as
	 * the 'root' for the prune. The given Graph and Node must not be null. Upon
	 * execution, the old Node and any Nodes that are solely descendents of the
	 * given prune source Node will be will be deleted from the Graph. No edges
	 * are explicitly removed by PruneFromNodeCommand, you must check the
	 * implementation of the Graph being used to determine how edges connected
	 * to the removed Nodes will be handled.
	 * 
	 * *WARNING* PruneFromNodeCommand does NOT perform a mark and sweep of the
	 * Graph, it ASSUMES that the given Graph is Acyclic.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Prune will take place
	 * @param pruneSourceNode
	 *            The 'root' Node at which to begin the prune
	 */
	public PruneFromNodeCommand(String editName, DirectionalGraph<N, ET> g,
		N pruneSourceNode)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (pruneSourceNode == null)
		{
			throw new IllegalArgumentException("Node cannot be null");
		}
		graph = g;
		node = pruneSourceNode;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Prune from Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the PruneFromNodeCommand on the Graph provided during Command
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
		if (!graph.containsNode(node))
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain GraphNode to be deleted");
		}
		GraphEditMonitor<N, ET> edit =
				GraphEditMonitor.getGraphEditMonitor(graph);
		pruneNode(node);
		graph.removeGraphChangeListener(edit);
		return edit.getEdit(name);
	}

	/**
	 * Recursively prunes Nodes from the Graph. Note that this is NOT a mark and
	 * sweep, and therefore, this recursive check WILL BE FOOLED by a cycle in a
	 * Graph (no Nodes in a cycle will ever be removed by this method)
	 * 
	 * @param pruneNode
	 *            The Node to be removed (along with otherwise unconnected
	 *            descendents) from the Graph.
	 */
	private void pruneNode(N pruneNode)
	{
		Collection<ET> edges = graph.getAdjacentEdges(pruneNode);
		graph.removeNode(pruneNode);
		for (ET edge : edges)
		{
			List<N> nodeList = edge.getAdjacentNodes();
			for (N checkNode : nodeList)
			{
				if (!checkNode.equals(pruneNode)
					&& edge.getNodeInterfaceType(checkNode) == DirectionalEdge.SINK
					&& !graph.hasInwardEdge(checkNode))
				{
					pruneNode(checkNode);
				}
			}
		}
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