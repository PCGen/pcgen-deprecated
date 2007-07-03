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
 * Created on Aug 21, 2004
 */
package pcgen.base.graph.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalHyperEdge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A TransferEdgesCommand is a Command which will disconnect all of the edges
 * from the given Node and reconnect replacement edges to the given new Node.
 * Note that the old Node is NOT removed from the Graph by TransferEdgesCommand.
 * Note also that the new Node may have other non-related edges already
 * connected to it.
 * 
 * Note that the transfer of the GraphEdges may have side effects upon other
 * GraphElements in the Graph (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in TransferEdgesCommand
 * will track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the transfer of Edges causes an unrelated Node to also be
 * deleted, the returned UndoableEdit will represent both the GraphEdge
 * transfers and the removal of the unrelated Node (and the undo() and redo()
 * methods of the UndoableEdit would undo and redo both the transfers and the
 * deletion).
 */
public final class TransferEdgesCommand<N, ET extends DirectionalHyperEdge<N>>
		implements Command
{

	/**
	 * The Graph on which this TransferEdgesCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Node which will act as a source of edges when this
	 * TransferEdgesCommand is executed.
	 */
	private final N oldNode;

	/**
	 * The Node which is the destination of the transferred edges when this
	 * TransferEdgesCommand is executed.
	 */
	private final N newNode;

	/**
	 * The name of this TransferEdgesCommand. Intended to be accessible to the
	 * end user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new TransferEdgesCommand with the given Name, Graph in which
	 * the Transfer will take place, and nodes the edges will transferred from
	 * and to (the old and new node, respectively). The given Graph must not be
	 * null. The given Nodes must also not be null.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Transfer will take place
	 * @param oldN
	 *            The Node that Edges will be transferred from
	 * @param newN
	 *            The Node that Edges will be transferred to
	 */
	public TransferEdgesCommand(String editName, Graph<N, ET> g, N oldN, N newN)
	{
		if (g == null)
		{
			throw new IllegalArgumentException("Graph Cannot be null");
		}
		if (oldN == null)
		{
			throw new IllegalArgumentException("old GraphNode Cannot be null");
		}
		if (newN == null)
		{
			throw new IllegalArgumentException("new GraphNode Cannot be null");
		}
		if (oldN.equals(newN))
		{
			throw new IllegalArgumentException(
				"old GraphNode and new GraphNode identical (cannot be the same)");
		}
		graph = g;
		oldNode = oldN;
		newNode = newN;
		if (editName == null || editName.trim().length() == 0)
		{
			name = "Transfer Adjacent Edges to new Graph Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the TransferEdgesCommand on the Graph provided during Command
	 * construction. Returns an UndoableEdit indicating the changes that took
	 * place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		if (!graph.containsNode(oldNode))
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain source GraphNode");
		}
		if (!graph.containsNode(newNode))
		{
			throw new UnsupportedGraphOperationException(
				"Graph did not contain destination GraphNode");
		}
		CompoundEdit edit = new CompoundEdit();
		List<N> newSources = new ArrayList<N>();
		List<N> newSinks = new ArrayList<N>();
		for (ET edge : graph.getAdjacentEdges(oldNode))
		{
			Collection<N> sources = edge.getSourceNodes();
			Collection<N> sinks = edge.getSinkNodes();
			/*
			 * Can't do a test here of if sources else assume sinks because if
			 * the given adjacent edge is a LOOP then BOTH need to be changed to
			 * properly perform the replaceNode!
			 */
			if (sources != null)
			{
				newSources.clear();
				for (N source : sources)
				{
					newSources.add(source.equals(oldNode) ? newNode : source);
				}
				sources = newSources;
			}
			if (sinks != null)
			{
				newSinks.clear();
				for (N sink : sinks)
				{
					newSinks.add(sink.equals(oldNode) ? newNode : sink);
				}
				sinks = newSinks;
			}

			ET newEdge = (ET) edge.createReplacementEdge(sources, sinks);

			edit.addEdit(new DeleteEdgeCommand<N, ET>(name, graph, edge)
				.execute());
			edit.addEdit(new InsertEdgeCommand<N, ET>(name, graph, newEdge)
				.execute());
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