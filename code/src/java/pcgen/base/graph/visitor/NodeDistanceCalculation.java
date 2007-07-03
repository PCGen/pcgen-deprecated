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
 * Created on Aug 27, 2004
 */
package pcgen.base.graph.visitor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.util.IdentityHashSet;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This calculates the minimum distance between two Nodes in a Graph. This is a
 * derivative of Dijkstra's Algorithm, see
 * http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * 
 * Note: This derivative is optimized to a Graph which has low fan-in (meaning
 * each node has very vew incoming [source] edges). Therefore, is likely that
 * NodeDistanceCalculation is only useful in certain Directed Graphs. If you are
 * calculating distance for a Graph with high fan-in (but low fan-out), you
 * should use DijkstraNodeAlgorithm or DijkstraEdgeAlgorithm.
 * 
 * It is assumed that the Edges have a defined length. The default length for
 * each edge is 1. This length can be modified by a sub-Class by overriding the
 * calculateEdgeLength method.
 */
public class NodeDistanceCalculation<N, ET extends Edge<N>>
{

	/**
	 * The Graph on which this search algorithm is operating.
	 */
	private final Graph<N, ET> graph;

	/**
	 * The upper limit of distance to which this algorithm should search. This
	 * is used as a shortcut to terminate the search early if only objects
	 * within a certain distance of the source of the search are desired.
	 */
	private final double upperLimit;

	/**
	 * The heap, used to keep track of parts of the Graph to be visited (in
	 * order). A PriorityQueue used because it can handle items that have a
	 * Comparator that is not consistent with equals (something a TreeSet, for
	 * example, doesn't handle)
	 */
	private final PriorityQueue<GraphHeapComponent<N, ET>> heap;

	/*
	 * Technically, I think the use of this set for shortcutting processing
	 * assumes that each edge has only one or two connections (a sink only or a
	 * source and a sink). If that assumption is violated, then this method may
	 * not calculate all possible combinations, which could cause it to fail
	 * (albeit under some weird circumstances!)
	 */

	/**
	 * A Set indicating the edges that have already been visited
	 */
	private final IdentityHashSet<ET> visitedEdgeSet;

	/**
	 * A Map indicating the distance from the source of the search for each Node
	 */
	private final Map<N, Double> nodeDistanceMap;

	/**
	 * Creates a new NodeDistanceCalculation to traverse the given Graph. There
	 * is no limitation on the distance that can be traversed in the Graph.
	 * 
	 * @param g
	 *            The Graph this NodeDistanceCalculation will traverse.
	 */
	public NodeDistanceCalculation(Graph<N, ET> g)
	{
		this(g, Double.POSITIVE_INFINITY);
	}

	/**
	 * Creates a new NodeDistanceCalculation to traverse the given Graph. The
	 * Graph will only be traversed until the distance traversed over any
	 * individual path is equal to the given limit. (Note this is distance of
	 * any individual destination Node or Edge from the source Node or Edge, not
	 * a cumulative distance traversed over all paths)
	 * 
	 * @param g
	 *            The Graph this NodeDistanceCalculation will traverse.
	 * @param limit
	 *            The distance limit for any traversal.
	 */
	public NodeDistanceCalculation(Graph<N, ET> g, double limit)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException(
				"Graph for DijkstraNodeAlgorithm cannot be null");
		}
		// This is !>=0 in order to also catch Double.NaN
		if (!(limit >= 0))
		{
			throw new IllegalArgumentException(
				"Limit must be greater than or equal to zero");
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
		upperLimit = limit;
		heap =
				new PriorityQueue<GraphHeapComponent<N, ET>>(20,
					GraphHeapComponent.DISTANCE_COMPARATOR);
		nodeDistanceMap = new IdentityHashMap<N, Double>();
		visitedEdgeSet = new IdentityHashSet<ET>();
	}

	/**
	 * Calculates the distance between the given source Node and the given
	 * destination Node.
	 * 
	 * @param sourceNode
	 *            The source Node for the distance calculation
	 * @param destinationNode
	 *            The destination Node for the distance calculation
	 * @return The distance between the source Node and the destination Node, as
	 *         a double
	 */
	public double calculateDistance(N sourceNode, N destinationNode)
	{
		if (sourceNode == null)
		{
			throw new IllegalArgumentException(
				"Node to calculate distance from cannot be null");
		}
		if (destinationNode == null)
		{
			throw new IllegalArgumentException(
				"Node to calculate distance to cannot be null");
		}
		clear();
		if (!graph.containsNode(sourceNode)
			|| !graph.containsNode(destinationNode))
		{
			return Double.NaN;
		}
		nodeDistanceMap.put(destinationNode, new Double(0.0));
		addAdjacentEdgesToHeap(destinationNode, 0.0);
		runCalculation();
		Double distance = nodeDistanceMap.get(sourceNode);
		return distance == null ? Double.POSITIVE_INFINITY : distance
			.doubleValue();
	}

	/**
	 * Adds the edges adjacent to the given Node to the Heap, for later
	 * processing
	 */
	private void addAdjacentEdgesToHeap(N gn, double d)
	{
		for (ET edge : graph.getAdjacentEdges(gn))
		{
			// even if legal, don't traverse the edge twice
			if (canTraverseEdge(edge, gn, DirectionalEdge.SINK)
				&& !visitedEdgeSet.contains(edge))
			{
				iterateOverAdjacentNodes(gn, edge, d);
			}
		}
	}

	/**
	 * Actually runs the distance calculation
	 */
	private void runCalculation()
	{
		while (!heap.isEmpty())
		{
			GraphHeapComponent<N, ET> ghc = heap.poll();
			if (!nodeDistanceMap.containsKey(ghc.node))
			{
				nodeDistanceMap.put(ghc.node, Double.valueOf(ghc.distance));
				addAdjacentEdgesToHeap(ghc.node, ghc.distance);
			}
		}
	}

	/**
	 * Returns true if the given Edge can be traversed by this
	 * NodeDistanceCalculation. This check is performed in context to the given
	 * Node and interface type for the given Node.
	 * 
	 * @param edge
	 *            The Edge to potentially be traversed
	 * @param gn
	 *            The contextual Node connected to the edge
	 * @param type
	 *            The connection type of the given Node
	 * @return true if the given Edge can be traversed; false otherwise
	 */
	protected boolean canTraverseEdge(Edge<N> edge, N gn, int type)
	{
		return true;
	}

	private void iterateOverAdjacentNodes(N node, ET edge, double distance)
	{
		List<N> graphNodes = edge.getAdjacentNodes();
		if (graphNodes.size() == 1)
		{
			visitedEdgeSet.add(edge);
		}
		else
		{
			for (N thisNode : graphNodes)
			{
				if (!node.equals(thisNode)
					&& canTraverseEdge(edge, thisNode, DirectionalEdge.SOURCE))
				{
					double depth =
							distance
								+ calculateEdgeLength(node, edge, thisNode);
					if (depth < upperLimit)
					{
						visitedEdgeSet.add(edge);
						heap.add(new GraphHeapComponent<N, ET>(depth, edge,
							thisNode));
					}
				}
			}
		}
	}

	/**
	 * Returns the edge length between the given Nodes.
	 * 
	 * @param source
	 *            The source node for length calculation
	 * @param other
	 *            The edge being traversed
	 * @param thisEdge
	 *            The sink node for separation calcualtion
	 * @return The distance traversed along the edge between the given source
	 *         Node and sink Node
	 */
	protected double calculateEdgeLength(N node1, ET edge, N node2)
	{
		return 1.0;
	}

	/**
	 * Clears the distance results so that this NodeDistanceCalculation may have
	 * the calculateDistance method called.
	 */
	private void clear()
	{
		heap.clear();
		nodeDistanceMap.clear();
		visitedEdgeSet.clear();
	}
}