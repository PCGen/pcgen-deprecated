/*
 * Copyright (c) Thomas Parker, 2004-2007
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
 * Created on Aug 29, 2004
 */
package pcgen.base.graph.core;

import java.util.Collection;
import java.util.List;

import pcgen.base.util.IdentityHashSet;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * GraphUtilities provides utility methods related to Graph objects.
 */
public final class GraphUtilities
{

	/**
	 * This is a utility class and cannot be instantiated.
	 */
	private GraphUtilities()
	{
		super();
	}

	/**
	 * Returns all of the Descendent Nodes of the given Node in the given
	 * DirectionalGraph. Descendent Nodes are nodes that are connected to an
	 * edge where the given Node or any Descendent of the given Node is the
	 * source of the DirectionalEdge.
	 * 
	 * Note that it is possible for this Set to contain the given Node if and
	 * only if there is a loop in the Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by GraphUtilities. However, the
	 * Nodes contained in the Set are returned BY REFERENCE, and modification of
	 * the returned Nodes will modify the Nodes contained within the given
	 * DirectionalGraph.
	 * 
	 * @param <A>
	 *            The Generic type of the given Node and Nodes in the given
	 *            DirectionalGraph
	 * @param <ET>
	 *            The Generic type of the Edges in the given DirectionalGraph
	 * @param graph
	 *            The Graph to be searched for Descendent Nodes.
	 * @param node
	 *            The Node from which to search for Descendent Nodes.
	 * @return The Set of Descendent Nodes of the given Node within the given
	 *         DirectionalGraph.
	 */
	public static <A, ET extends DirectionalEdge<A>> Collection<A> getDescendentNodes(
		DirectionalGraph<A, ET> graph, A node)
	{
		if (!graph.containsNode(node))
		{
			return null;
		}
		/*
		 * Note that IdentityHashSet is used here because there is no guarantee
		 * that the Nodes or Edges in a Graph are unique (relative to .equals).
		 * However, it is guaranteed that they possess a unique identity (==)...
		 * thus this is faster than, and prefereable to, storing the descendents
		 * as a List.
		 */
		Collection<A> descendents = new IdentityHashSet<A>();
		accumulateDescendentNodes(graph, node, descendents);
		return descendents;
	}

	/**
	 * Performs an accumulation of Descendent Nodes within a given Graph.
	 * Accounts for loops in the DirectionalGraph (ensuring an accumulation does
	 * not enter an infinite loop)
	 * 
	 * @param <A>
	 *            The Generic type of the given Node and Nodes in the given
	 *            DirectionalGraph
	 * @param <ET>
	 *            The Generic type of the Edges in the given DirectionalGraph
	 * @param graph
	 *            The Graph to be searched for Descendent Nodes.
	 * @param node
	 *            The Node from which to search for Descendent Nodes.
	 * @param descendents
	 *            The Set of Descendent Nodes of the given Node within the given
	 *            DirectionalGraph. This Set WILL BE MODIFIED by this method.
	 */
	private static <A, ET extends DirectionalEdge<A>> void accumulateDescendentNodes(
		DirectionalGraph<A, ET> graph, A node, Collection<A> descendents)
	{
		for (ET edge : graph.getOutwardEdgeSet(node))
		{
			List<A> graphNodes = edge.getAdjacentNodes();
			for (A gn : graphNodes)
			{
				// skip the "parent" node(s)
				int nodeInterfaceType =
						((DirectionalEdge<A>) edge).getNodeInterfaceType(gn);
				if ((nodeInterfaceType & DirectionalEdge.SINK) == 0)
				{
					// emma code coverage not 100% here due to Eclipse compiler
					continue;
				}
				/*
				 * Because this will return false if the Set already contains
				 * graphNodes[i], then this safely handles the case where there
				 * is a cycle (loop) in the graph!
				 */
				if (descendents.add(gn))
				{
					accumulateDescendentNodes(graph, gn, descendents);
				}
			}
		}
	}
}