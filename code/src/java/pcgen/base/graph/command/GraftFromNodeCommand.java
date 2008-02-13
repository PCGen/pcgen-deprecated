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

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.visitor.DirectedDepthFirstTraverseAlgorithm;
import pcgen.base.graph.visitor.EdgeTourist;
import pcgen.base.graph.visitor.NodeTourist;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An GraftFromNodeCommand is a Command which, when executed, will copy a Node
 * and the entire subgraph defined by all child Nodes and Edges of the given
 * Node from a source Graph to a destination Graph. Both Graphs must be
 * DirectionalGraphs.
 * 
 * Note that insertion of the Nodes and Edges may have side effects upon other
 * objects in the destination Graph (this will depend on the Graph
 * implementation).
 * 
 * The UndoableEdit returned by the execute() method in GraftFromNodeCommand
 * will track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the addition of one Edge causes a Node to also be added,
 * the returned UndoableEdit will represent both the Edge addition and the Node
 * addition (and the undo() and redo() methods of the UndoableEdit would undo
 * and redo both additions).
 */
public class GraftFromNodeCommand<N, ET extends DirectionalEdge<N>> implements
		Command
{

	/**
	 * The source Graph from which this GraftFromNodeCommand will operate.
	 */
	private final DirectionalGraph<N, ET> sourceGraph;

	/**
	 * The destination Graph to which this GraftFromNodeCommand will copy
	 * objects.
	 */
	private final DirectionalGraph<N, ET> destinationGraph;

	/**
	 * The source Node to be used in the source Graph for determining the
	 * subgraph to be copied when this GraftFromNodeCommand is executed.
	 */
	private final N node;

	/**
	 * The name of this GraftFromNodeCommand. Intended to be accessible to the
	 * end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new GraftFromNodeCommand with the given Name, source Graph,
	 * source Node, and destination Graph in which the Insertion will take
	 * place. The given Graphs and Node must not be null. Upon execution, the
	 * subgraph which is a child of the given Node will be inserted into the
	 * Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param sourceG
	 *            The source Graph for the graft
	 * @param gn
	 *            The Node to be used as a source for the subgraph to be copied
	 * @param destinationG
	 *            The destination Graph for the graft
	 */
	public GraftFromNodeCommand(String editName,
		DirectionalGraph<N, ET> sourceG, N gn,
		DirectionalGraph<N, ET> destinationG)
	{
		if (sourceG == null)
		{
			throw new IllegalArgumentException("Source Graph Cannot be null");
		}
		if (gn == null)
		{
			throw new IllegalArgumentException("Source Node Cannot be null");
		}
		if (destinationG == null)
		{
			throw new IllegalArgumentException(
				"Destination Graph Cannot be null");
		}
		sourceGraph = sourceG;
		node = gn;
		destinationGraph = destinationG;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Graft From Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the GraftFromNodeCommand on the Graph provided during Command
	 * construction. Returns an UndoableEdit indicating the changes that took
	 * place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		if (!sourceGraph.containsNode(node))
		{
			throw new UnsupportedGraphOperationException(
				"Source Graph did not contain starting Node");
		}
		DirectedDepthFirstTraverseAlgorithm<N, ET> dfta =
				new DirectedDepthFirstTraverseAlgorithm<N, ET>(sourceGraph);
		dfta.traverseFromNode(node);
		CompoundEdit edit = new CompoundEdit();
		edit.addEdit(new InsertNodesCommand<N, ET>(name, destinationGraph,
				getNodes(dfta)).execute());
		edit.addEdit(new InsertEdgesCommand<N, ET>(name, destinationGraph,
				getEdges(dfta)).execute());
		edit.end();
		return edit;
	}

	public Collection<N> getNodes(NodeTourist<N> dfta)
	{
		return dfta.getVisitedNodes();
	}

	public Collection<ET> getEdges(EdgeTourist<ET> dfta)
	{
		return dfta.getVisitedEdges();
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