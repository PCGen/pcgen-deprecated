/*
 * Copyright (c) Thomas Parker, 2007
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
 */
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * Treats all edges in the Graph as directed.
 * 
 * Note that use of this class over a simple BreadthFirstTraverseAlgorithm is
 * not required, as it is possible in a DirectedGraph for a user to desire only
 * absolute distance or to know directional distance, depending on the
 * circumstances of the graph search.
 */
public class ReverseDirectedBreadthFirstTraverseAlgorithm<N, ET extends DirectionalEdge<N>>
		extends BreadthFirstTraverseAlgorithm<N, ET>
{

	/**
	 * Creates a new ReverseDirectedBreadthFirstTraverseAlgorithm to traverse
	 * the given Graph.
	 * 
	 * @param g
	 *            The Graph this ReverseDirectedBreadthFirstTraverseAlgorithm
	 *            will traverse.
	 */
	public ReverseDirectedBreadthFirstTraverseAlgorithm(
		DirectionalGraph<N, ET> g)
	{
		super(g);
	}

	/**
	 * Indicates if this ReverseDirectedBreadthFirstTraverseAlgorithm should
	 * traverse the given Edge. This is done with respect to the given node and
	 * node interface type. Returns true if the edge should be traversed.
	 * 
	 * This method enforces the directional nature of the
	 * ReverseDirectedBreadthFirstTraverseAlgorithm.
	 * 
	 * @return true if the given edge should be traversed; false otherwise
	 */
	@Override
	protected boolean canTraverseEdge(ET edge, N gn, int type)
	{
		// TODO This isn't entirely correct, as an edge that is both SOURCE and
		// SINK will fail here
		return (edge.getNodeInterfaceType(gn) & type) == 0;
	}

}
