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
 * Created on Oct 12, 2004
 */
package pcgen.base.graph.visitor;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.util.IdentityHashSet;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This is an implementation of a Breadth First Search of a Graph.
 * 
 * See http://en.wikipedia.org/wiki/Breadth-first_search for a full definition.
 * 
 * Note: This class uses the simple "remember which nodes (and edges) I've
 * already seen" method in order to avoid confusion from non-termination. This
 * may limit the size of the graph that can be traversed using this class.
 * 
 * If this traverse is constructed as a directional traverse, then the only
 * edges which can be traversed are DirectionalEdges (unless canTraverseEdge is
 * overridden by a subclass).
 */
public class BreadthFirstTraverseAlgorithm<N, ET extends Edge<N>>
{

	/**
	 * Indicates the Graph on which this search algorithm is operating.
	 */
	private final Graph<N, ET> graph;

	/**
	 * A Set of the GraphNodes which have already been visited by this search
	 * algorithm. Used for performance improvement (avoid visiting multiple
	 * times), as well as to avoid an infinite loop in the case of a cycle in a
	 * Graph.
	 */
	private final Set<N> visitedNodes = new IdentityHashSet<N>();

	/**
	 * A Set of the HyperEdges which have already been visited by this search
	 * algorithm. Used for performance improvement (avoid visiting multiple
	 * times), as well as to avoid an infinite loop in the case of a cycle in a
	 * Graph.
	 */
	private final Set<ET> visitedEdges = new IdentityHashSet<ET>();

	/**
	 * Maintains the list of unvisited GraphNodes.
	 */
	private final Set<N> unvisitedNodes = new IdentityHashSet<N>();

	/**
	 * Maintains the list of unvisited HyperEdges.
	 */
	private final Set<ET> unvisitedEdges = new IdentityHashSet<ET>();

	/**
	 * Creates a new BreadthFirstTraverseAlgorithm to traverse the given Graph.
	 * 
	 * @param g
	 *            The Graph this BreadthFirstTraverseAlgorithm will traverse.
	 */
	public BreadthFirstTraverseAlgorithm(Graph<N, ET> g)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException("Graph cannot be null");
		}
		graph = g;
		/*
		 * FUTURE Should this detect a concurrent modification on a graph? Only
		 * trigger/detect after this is running... so after a traverseFrom
		 * method is called. The question is when to 'call it off' - does this
		 * still trigger any time any method is called once the Graph is
		 * modified, or does this only worry about when it is actively
		 * traversing?
		 */
	}

	/**
	 * Traverses the graph to connected Nodes and Edges in the Graph from the
	 * given source Node.
	 * 
	 * Results of this traversal are available from the getVisited* methods.
	 * 
	 * Calling traverseFrom* methods a second time without calling clear() will
	 * result in an UnsupportedOperationException being thrown.
	 * 
	 * @param gn
	 *            The source Node to be used for Graph traversal
	 */
	public void traverseFromNode(N gn)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (gn == null)
		{
			throw new IllegalArgumentException(
				"Node to traverse from cannot be null");
		}
		if (graph.containsNode(gn))
		{
			unvisitedNodes.add(gn);
			runVisiting();
		}
	}

	/**
	 * Traverses the graph to connected Nodes and Edges in the Graph from the
	 * given source Edge.
	 * 
	 * Results of this traversal are available from the getVisited* methods.
	 * 
	 * Calling traverseFrom* methods a second time without calling clear() will
	 * result in an UnsupportedOperationException being thrown.
	 * 
	 * @param he
	 *            The source Edge to be used for Graph traversal
	 */
	public void traverseFromEdge(ET he)
	{
		if (!visitedNodes.isEmpty() || !visitedEdges.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (he == null)
		{
			throw new IllegalArgumentException(
				"Edge to traverse from cannot be null");
		}
		if (graph.containsEdge(he))
		{
			unvisitedEdges.add(he);
			runVisiting();
		}
	}

	/**
	 * Actually performs the traverse of the Graph.
	 * 
	 * The lists of visitedNodes and visitedEdges are required because this is a
	 * Graph: It is possible to have a node or edge that is reachable in more
	 * than one way, and it is also possible to have a cycle in the graph (a
	 * cycle would cause an infinite loop if a tag and sweep system was not used
	 * to identify nodes and edges that have already been visited.
	 */
	private void runVisiting()
	{
		while (!unvisitedNodes.isEmpty() || !unvisitedEdges.isEmpty())
		{
			for (Iterator<N> it = unvisitedNodes.iterator(); it.hasNext();)
			{
				N thisNode = it.next();
				it.remove();
				visitedNodes.add(thisNode);
				conditionallyVisitEdgesOnNode(thisNode);
			}
			for (Iterator<ET> it = unvisitedEdges.iterator(); it.hasNext();)
			{
				ET thisEdge = it.next();
				it.remove();
				visitedEdges.add(thisEdge);
				conditionallyVisitNodesOnEdge(thisEdge);
			}
		}
	}

	/**
	 * Queues up Edges to be visited if (1) The edge has not yet been visited
	 * and (2) the edge can be traversed [is allowed to be visited]
	 */
	private void conditionallyVisitEdgesOnNode(N thisNode)
	{
		for (ET nextEdge : graph.getAdjacentEdges(thisNode))
		{
			if (!visitedEdges.contains(nextEdge)
				&& canTraverseEdge(nextEdge, thisNode, DirectionalEdge.SOURCE))
			{
				unvisitedEdges.add(nextEdge);
			}
		}
	}

	/**
	 * Queues up Nodes to be visited if (1) The node has not yet been visited
	 * and (2) the edge can be traversed to reach the given node [the node is
	 * allowed to be visited]
	 */
	private void conditionallyVisitNodesOnEdge(ET thisEdge)
	{
		List<N> graphNodes = thisEdge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			if (!visitedNodes.contains(node)
				&& canTraverseEdge(thisEdge, node, DirectionalEdge.SINK))
			{
				unvisitedNodes.add(node);
			}
		}
	}

	/**
	 * Indicates if this BreadthFirstTraverseAlgorithm should traverse the given
	 * Edge. This is done with respect to the given node and node interface
	 * type. Returns true if the edge should be traversed.
	 * 
	 * @return true if the given edge should be traversed; false otherwise
	 */
	protected boolean canTraverseEdge(ET edge, N gn, int type)
	{
		return true;
	}

	/**
	 * Returns the Set of Nodes visited by the last traversal by this
	 * BreadthFirstTraverseAlgorithm
	 * 
	 * @return Set of Nodes visited by this BreadthFirstTraverseAlgorithm
	 */
	public Set<N> getVisitedNodes()
	{
		return new IdentityHashSet<N>(visitedNodes);
	}

	/**
	 * Returns the Set of Edges visited by the last traversal by this
	 * BreadthFirstTraverseAlgorithm
	 * 
	 * @return Set of Edges visited by this BreadthFirstTraverseAlgorithm
	 */
	public Set<ET> getVisitedEdges()
	{
		return new IdentityHashSet<ET>(visitedEdges);
	}

	/**
	 * Clears the distance results so that this BreadthFirstTraverseAlgorithm
	 * may have a traverseFrom method called.
	 */
	public void clear()
	{
		visitedNodes.clear();
		visitedEdges.clear();
		unvisitedNodes.clear();
		unvisitedEdges.clear();
	}
}