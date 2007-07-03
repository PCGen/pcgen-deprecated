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
import java.util.List;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.visitor.DirectedDepthFirstTraverseAlgorithm;
import pcgen.base.lang.Command;
import pcgen.base.util.InsignificantUndoableEdit;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A PruneUnconnectedToNodeCommand is a Command which, when executed, will
 * delete all Nodes and all Edges not connected (as children or otherwise as
 * descendents) to the given Node from a DirectionalGraph. The search for what
 * is kept is a *directional search* through the given DirectionalGraph.
 * 
 * *WARNING* Executing PruneUnconnectedToNodeCommand on an object other than the
 * root object of a DirectionalGraph may delete large portions of the Graph. Be
 * aware that this only maintains the directionally connected subgraph of the
 * given Node.
 * 
 * Note that deletion of the Node may have side effects upon the Edges to which
 * the Node was connected (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in
 * PruneUnconnectedToNodeCommand will track any side effects to the graph itself
 * (Node or Edge addition or removal). Thus, if the deletion of one Node causes
 * an Edge to also be deleted, the returned UndoableEdit will represent both the
 * Node removal and the Edge removal (and the undo() and redo() methods of the
 * UndoableEdit would undo and redo both deletions).
 */
public class PruneUnconnectedToNodeCommand<N, ET extends DirectionalEdge<N>>
		implements Command
{

	/**
	 * The DirectionalGraph on which this PruneUnconnectedToNodeCommand will
	 * operate.
	 */
	private final DirectionalGraph<N, ET> graph;

	/**
	 * The Node to be the root of the Graph after this
	 * PruneUnconnectedToNodeCommand is executed.
	 */
	private final N node;

	/**
	 * The name of this PruneUnconnectedToNodeCommand. Intended to be accessible
	 * to the end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new PruneUnconnectedToNodeCommand with the given Name, Graph in
	 * which the Prune will take place, the current Node in the Graph to be used
	 * as the 'root' after the prune. The given Graph and Node must not be null.
	 * Upon execution, all Nodes and all Edges not connected (as children) to
	 * the given Node from a DirectionalGraph will be deleted from the Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Prune will take place
	 * @param graphRoot
	 *            The 'root' Node of the Graph after the prune (the root node of
	 *            the objects to NOT be deleted from the Graph)
	 */
	public PruneUnconnectedToNodeCommand(String editName,
		DirectionalGraph<N, ET> g, N graphRoot)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (graphRoot == null)
		{
			throw new IllegalArgumentException("Node cannot be null");
		}
		graph = g;
		node = graphRoot;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Prune unconnected to Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the PruneUnconnectedToNodeCommand on the Graph provided during
	 * Command construction. Returns an UndoableEdit indicating the changes that
	 * took place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		/*
		 * Note that this method of execution is NOT thread safe.
		 */
		if (!graph.containsNode(node))
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain Node to act as root node");
		}
		DirectedDepthFirstTraverseAlgorithm<N, ET> dfta =
				new DirectedDepthFirstTraverseAlgorithm<N, ET>(graph);
		dfta.traverseFromNode(node);
		CompoundEdit edit = new CompoundEdit();
		/*
		 * Must copy the arrays, because we are going to mutate them, and they
		 * are not guaranteed to be value semantic by the Graph Interface
		 */
		List<ET> edgeList = new ArrayList<ET>(graph.getEdgeList());
		List<N> nodeList = new ArrayList<N>(graph.getNodeList());
		edgeList.removeAll(dfta.getVisitedEdges());
		nodeList.removeAll(dfta.getVisitedNodes());
		/*
		 * Remove edges first, to avoid implicit edge removal which would cause
		 * DeleteEdgesCommand to fail
		 */
		if (edgeList.size() > 0)
		{
			edit.addEdit(new DeleteEdgesCommand<N, ET>(name, graph, edgeList)
				.execute());
		}
		if (nodeList.size() > 0)
		{
			edit.addEdit(new DeleteNodesCommand<N, ET>(name, graph, nodeList)
				.execute());
		}
		if (!edit.isSignificant())
		{
			return new InsignificantUndoableEdit();
		}
		edit.end();
		return edit;
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