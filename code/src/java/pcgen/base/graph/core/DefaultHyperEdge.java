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
 * Created on Aug 26, 2004
 */
package pcgen.base.graph.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DefaultHyperEdge is a default implementation of a non-directional
 * HyperEdge. As a HyperEdge, a DefaultHyperEdge can be connected to any
 * non-zero number of GraphNodes.
 */
public class DefaultHyperEdge<N> implements NonDirectionalEdge<N>
{

	/**
	 * The array of GraphNodes to which this DefaultHyperEdge is connected.
	 * These GraphNodes are not identified with any direction, as the
	 * DefaultHyperEdge is not a DirectionalEdge.
	 * 
	 * In normal operation, this array must not be null or empty (should be
	 * enforced in object construction).
	 */
	private final List<N> nodes;

	/**
	 * Creates a new DefaultHyperEdge connected to the Nodes in the given
	 * Collection. The Collection must not be empty or null.
	 * 
	 * @param nodeArray
	 *            The Collection of Nodes to which this DefaultHyperEdge is
	 *            connected
	 */
	public DefaultHyperEdge(Collection<N> nodeArray)
	{
		super();
		if (nodeArray == null)
		{
			throw new IllegalArgumentException(
				"GraphNode List of DefaultHyperEdge cannot be null");
		}
		/*
		 * Copy before length check for thread safety
		 */
		nodes = new ArrayList<N>(nodeArray.size());
		nodes.addAll(nodeArray);
		if (nodes.size() == 0)
		{
			throw new IllegalArgumentException(
				"GraphNode List of DefaultHyperEdge cannot be empty");
		}
		for (N node : nodes)
		{
			if (node == null)
			{
				throw new IllegalArgumentException("Node List contains null");
			}
		}
	}

	/**
	 * Returns the node at the given index.
	 * 
	 * @see pcgen.base.graph.core.Edge#getNodeAt(int)
	 */
	public N getNodeAt(int i)
	{
		return nodes.get(i);
	}

	/**
	 * Returns the List of Adjacent (connected) Nodes to this DefaultHyperEdge.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DefaultHyperEdge. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the DefaultHyperEdge.
	 * 
	 * @see pcgen.base.graph.core.Edge#getAdjacentNodes()
	 */
	public List<N> getAdjacentNodes()
	{
		return new ArrayList<N>(nodes);
	}

	/**
	 * Returns true if the given Node is adjacent (connected) to this
	 * DefaultHyperEdge; false otherwise.
	 * 
	 * @see pcgen.base.graph.core.Edge#isAdjacentNode(java.lang.Object)
	 */
	public boolean isAdjacentNode(N gn)
	{
		return nodes.contains(gn);
	}

	/**
	 * Returns the number of Nodes to which this DefaultHyperEdge is connected.
	 * 
	 * @see pcgen.base.graph.core.Edge#getAdjacentNodeCount()
	 */
	public int getAdjacentNodeCount()
	{
		/*
		 * CONSIDER This isn't ENTIRELY true, if this edge is connected to the
		 * same node more than once... what precisely should that corner case
		 * do? - thpr 11/20/06
		 */
		return nodes.size();
	}

	/**
	 * Creates a replacement DefaultHyperEdge for this DefaultHyperEdge, with
	 * the replacement connected to the Nodes in the given Collection. The
	 * Collection must not be empty or null.
	 * 
	 * @see pcgen.base.graph.core.NonDirectionalEdge#createReplacementEdge(java.util.Collection)
	 */
	public DefaultHyperEdge<N> createReplacementEdge(Collection<N> gn1)
	{
		return new DefaultHyperEdge<N>(gn1);
	}
}