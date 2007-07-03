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

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DirectionalHyperEdge;
import pcgen.base.graph.core.Graph;
import pcgen.base.lang.Command;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A ReplaceNodeCommand is a Command which will replace a given Node with
 * another given Node when it is executed on the given Graph.
 * 
 * All of the edges which connect to the Node to be replaced will be replaced
 * with edges connected to the given replacement Node.
 * 
 * Note that replacement of the Node may have side effects upon other objects in
 * the Graph (this will depend on the Graph implementation).
 * 
 * The UndoableEdit returned by the execute() method in ReplaceNodeCommand will
 * track any side effects to the graph itself (Node or Edge addition or
 * removal). Thus, if the replacement of one Node causes another Node to be
 * deleted, the returned UndoableEdit will represent both the Node replacement
 * and the Node deletion (and the undo() and redo() methods of the UndoableEdit
 * would undo and redo both changes).
 */
public final class ReplaceNodeCommand<N, ET extends DirectionalHyperEdge<N>>
		implements Command
{

	/**
	 * The Graph on which this ReplaceNodeCommand will operate.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The Node that will be replaced when this ReplaceNodeCommand is executed.
	 */
	private final N oldNode;

	/**
	 * The Node that will be inserted (used as a replacement for the old Node)
	 * when this ReplaceNodeCommand is executed.
	 */
	private final N newNode;

	/**
	 * The name of this ReplaceNodeCommand. Intended to be accessible to the end
	 * user as a potential name for this Command.
	 */
	private final String name;

	/**
	 * Creates a new ReplaceNodeCommand with the given Name, Graph in which the
	 * Replacement will take place, the current Node in the Graph and the
	 * replacement Node. The given Graph must not be null. The given Nodes must
	 * also not be null. Upon execution, the new Node will be inserted into the
	 * Graph, all of the edges on the old Node will be transferred to the new
	 * Node and the old Node will be deleted from the Graph.
	 * 
	 * @param editName
	 *            The edit name for this Command. This Command will use its
	 *            default edit name is this parameter is null or a String
	 *            composed entirely of whitespace.
	 * @param g
	 *            The Graph in which the Replacement will take place
	 * @param oldN
	 *            The Node in the Graph from which Edges will be removed
	 * @param newN
	 *            The Node in the Graph to which Edges will be added
	 */
	public ReplaceNodeCommand(String editName, Graph<N, ET> g, N oldN, N newN)
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
			name = "Replace Graph Node";
		}
		else
		{
			name = editName;
		}
	}

	/**
	 * Execute the ReplaceNodeCommand on the Graph provided during Command
	 * construction. Returns an UndoableEdit indicating the changes that took
	 * place in the Graph.
	 * 
	 * @see pcbase.lang.Command#execute()
	 */
	public UndoableEdit execute()
	{
		CompoundEdit edit = new CompoundEdit();
		edit.addEdit(new InsertNodeCommand<N, ET>(name, graph, newNode)
			.execute());
		edit.addEdit(new TransferEdgesCommand<N, ET>(name, graph, oldNode,
			newNode).execute());
		edit.addEdit(new DeleteNodeCommand<N, ET>(name, graph, oldNode)
			.execute());
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