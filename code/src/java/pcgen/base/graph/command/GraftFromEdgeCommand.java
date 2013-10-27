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

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.visitor.DirectedDepthFirstTraverseAlgorithm;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An GraftFromEdgeCommand is a Command which, when executed, will copy an Edge
 * and the entire subgraph defined by all child Nodes and Edges of the given
 * Edge from a source Graph to a destination Graph. Both Graphs must be
 * DirectionalGraphs.
 * 
 * Note that this command may FAIL if any source Nodes of the given Edge (the
 * Edge acting as a source for the subgraph to be copied) are not already
 * present in the destination Graph. Alternately, the source Nodes of the given
 * Edge may be automatically inserted into the destination Graph (this will
 * depend on the Graph implementation).
 * 
 * Note that insertion of the Nodes and Edges may have side effects upon other
 * objects in the destination Graph (this will depend on the Graph
 * implementation).
 * 
 * The UndoableEdit returned by the execute() method in GraftFromEdgeCommand
 * will track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the addition of one Edge causes a Node to also be added,
 * the returned UndoableEdit will represent both the Edge addition and the Node
 * addition (and the undo() and redo() methods of the UndoableEdit would undo
 * and redo both additions).
 */
public class GraftFromEdgeCommand<N, ET extends DirectionalEdge<N>> implements
		Command
{

	/**
	 * The source Graph from which this GraftFromEdgeCommand will operate.
	 */
	private final DirectionalGraph<N, ET> sourceGraph;

	/**
	 * The destination Graph to which this GraftFromEdgeCommand will copy
	 * objects.
	 */
	private final DirectionalGraph<N, ET> destinationGraph;

	/**
	 * The source Edge to be used in the source Graph for determining the
	 * subgraph to be copied when this GraftFromEdgeCommand is executed.
	 */
	private final ET edge;

	/**
	 * The name of this GraftFromEdgeCommand. Intended to be accessible to the
	 * end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new GraftFromEdgeCommand with the given Name, source Graph,
	 * source Edge, and destination Graph in which the Insertion will take
	 * place. The given Graphs and Edge must not be null. Upon execution, the
	 * subgraph which is a child of the given Edge will be inserted into the
	 * Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param sourceG
	 *            The source Graph for the graft
	 * @param ge
	 *            The Edge to be used as a source for the subgraph to be copied
	 * @param destinationG
	 *            The destination Graph for the graft
	 */
	public GraftFromEdgeCommand(String editName,
		DirectionalGraph<N, ET> sourceG, ET ge,
		DirectionalGraph<N, ET> destinationG)
	{
		if (sourceG == null)
		{
			throw new IllegalArgumentException("Source Graph Cannot be null");
		}
		if (ge == null)
		{
			throw new IllegalArgumentException("GraphEdge Cannot be null");
		}
		if (destinationG == null)
		{
			throw new IllegalArgumentException(
				"Destination Graph Cannot be null");
		}
		sourceGraph = sourceG;
		edge = ge;
		destinationGraph = destinationG;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Graft From Edge";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the GraftFromEdgeCommand on the Graph provided during Command
	 * construction. Returns an UndoableEdit indicating the changes that took
	 * place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		if (!sourceGraph.containsEdge(edge))
		{
			throw new UnsupportedGraphOperationException(
				"Source Graph did not contain starting Edge");
		}
		DirectedDepthFirstTraverseAlgorithm<N, ET> dfta =
				new DirectedDepthFirstTraverseAlgorithm<N, ET>(sourceGraph);
		dfta.traverseFromEdge(edge);
		CompoundEdit edit = new CompoundEdit();
		edit.addEdit(new InsertNodesCommand<N, ET>(name, destinationGraph, dfta
			.getVisitedNodes()).execute());
		edit.addEdit(new InsertEdgesCommand<N, ET>(name, destinationGraph, dfta
			.getVisitedEdges()).execute());
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