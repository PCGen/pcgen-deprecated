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

import java.util.List;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A PruneFromEdgeCommand is a Command which, when executed, will delete an Edge -
 * and all Edges and Nodes only connected to the given Edge - from a
 * DirectionalGraph.
 * 
 * *WARNING* PruneFromEdgeCommand does NOT perform a mark and sweep of the
 * Graph, it ASSUMES that the given Graph is Acyclic.
 * 
 * Note that deletion of the child Nodes may have side effects upon the Edges to
 * which the Node was connected (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in PruneFromEdgeCommand
 * will track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the deletion of one Node causes an Edge to also be
 * deleted, the returned UndoableEdit will represent both the Node removal and
 * the Edge removal (and the undo() and redo() methods of the UndoableEdit would
 * undo and redo both deletions).
 */
public class PruneFromEdgeCommand<N, ET extends DirectionalEdge<N>> implements
		Command
{

	/**
	 * The DirectionalGraph on which this PruneFromEdgeCommand will operate.
	 */
	private final DirectionalGraph<N, ET> graph;

	/**
	 * The Edge to be removed from the Graph when this PruneFromEdgeCommand is
	 * executed.
	 */
	private final ET edge;

	/**
	 * The name of this PruneFromEdgeCommand. Intended to be accessible to the
	 * end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new PruneFromEdgeCommand with the given Name, Graph in which
	 * the Prune will take place, the current Edge in the Graph to be used as
	 * the 'root' for the prune. The given Graph and Edge must not be null. Upon
	 * execution, the given Edge and any Nodes that are solely descendents of
	 * the given prune source Edge will be will be deleted from the Graph. No
	 * edges other than the source Edge are explicitly removed by
	 * PruneFromEdgeCommand, you must check the implementation of the Graph
	 * being used to determine how edges connected to the removed Nodes will be
	 * handled.
	 * 
	 * *WARNING* PruneFromEdgeCommand does NOT perform a mark and sweep of the
	 * Graph, it ASSUMES that the given Graph is Acyclic.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Prune will take place
	 * @param pruneSourceEdge
	 *            The source Edge at which to begin the prune
	 */
	public PruneFromEdgeCommand(String editName, DirectionalGraph<N, ET> g,
		ET pruneSourceEdge)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		if (pruneSourceEdge == null)
		{
			throw new IllegalArgumentException("Edge cannot be null");
		}
		graph = g;
		edge = pruneSourceEdge;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Prune from Edge";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the PruneFromEdgeCommand on the Graph provided during Command
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
		CompoundEdit edit = new CompoundEdit();
		edit.addEdit(new DeleteEdgeCommand<N, ET>(name, graph, edge).execute());
		List<N> nodeList = edge.getAdjacentNodes();
		for (N node : nodeList)
		{
			if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK
				&& graph.containsNode(node) && !graph.hasInwardEdge(node))
			{
				edit.addEdit(new PruneFromNodeCommand<N, ET>(name, graph, node)
					.execute());
			}
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