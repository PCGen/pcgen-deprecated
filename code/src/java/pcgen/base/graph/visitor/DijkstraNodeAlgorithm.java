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

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This is an implementation of Dijkstra's Algorithm.
 * 
 * For more information, see http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * 
 * This is "Dijkstra's Node Algorithm" because the Algorithm is traversing the
 * Nodes. It is assumed that the Edges have a defined length. The default length
 * for each edge is 1. This length can be modified by a sub-Class by overriding
 * the calculateEdgeLength method.
 * 
 * Note that nothing prohibits a HyperEdge from having a different length
 * between each of the nodes that it connects.
 */
public class DijkstraNodeAlgorithm<N, ET extends Edge<N>>
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

	/**
	 * A Map indicating the distance from the source of the search for each
	 * HyperEdge
	 */
	private final Map<ET, Double> edgeDistanceMap;

	/**
	 * A Map indicating the distance from the source of the search for each
	 * GraphNode
	 */
	private final Map<N, Double> nodeDistanceMap;

	/**
	 * Creates a new DijkstraNodeAlgorithm to traverse the given Graph. There is
	 * no limitation on the distance that can be traversed in the Graph.
	 * 
	 * @param g
	 *            The Graph this DijkstraNodeAlgorithm will traverse.
	 */
	public DijkstraNodeAlgorithm(Graph<N, ET> g)
	{
		this(g, Double.POSITIVE_INFINITY);
	}

	/**
	 * Creates a new DijkstraNodeAlgorithm to traverse the given Graph. The
	 * Graph will only be traversed until the distance traversed over any
	 * individual path is equal to the given limit. (Note this is distance of
	 * any individual destination Node or Edge from the source Node or Edge, not
	 * a cumulative distance traversed over all paths)
	 * 
	 * @param g
	 *            The Graph this DijkstraNodeAlgorithm will traverse.
	 * @param limit
	 *            The distance limit for any traversal.
	 */
	public DijkstraNodeAlgorithm(Graph<N, ET> g, double limit)
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
		edgeDistanceMap = new IdentityHashMap<ET, Double>();
	}

	/**
	 * Calculates the distance (within the limit specified during construction
	 * of the DijkstraNodeAlgorithm) to all Nodes and Edges in the Graph from
	 * the given source Node.
	 * 
	 * Results of this calculation are available from the getDistanceTo methods.
	 * 
	 * Calling calculateFrom() a second time without calling clear() will result
	 * in an UnsupportedOperationException being thrown.
	 * 
	 * @param gn
	 *            The source Node to be used for distance calculation.
	 */
	public void calculateFrom(N gn)
	{
		if (!nodeDistanceMap.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (gn == null)
		{
			throw new IllegalArgumentException(
				"Node to calculate from cannot be null");
		}
		if (!graph.containsNode(gn))
		{
			throw new IllegalArgumentException("Node is not a part of Graph");
		}
		nodeDistanceMap.put(gn, new Double(0.0));
		addAdjacentEdgesToHeap(gn, 0.0);
		runCalculation();
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
			if (canTraverseEdge(edge, gn, DirectionalEdge.SOURCE)
				&& !edgeDistanceMap.containsKey(edge))
			{
				iterateOverAdjacentNodes(gn, edge, d);
			}
		}
	}

	/**
	 * Actually runs Dijkstra's Algorithm
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
	 * DijkstraNodeAlgorithm. This check is performed in context to the given
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
			edgeDistanceMap.put(edge, new Double(distance));
		}
		else
		{
			for (N thisNode : graphNodes)
			{
				if (!node.equals(thisNode)
					&& canTraverseEdge(edge, thisNode, DirectionalEdge.SINK))
				{
					double depth =
							distance
								+ calculateEdgeLength(node, edge, thisNode);
					if (depth < upperLimit)
					{
						edgeDistanceMap.put(edge, Double.valueOf(distance));
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
	 * Returns the distance to the given Node from the source node provided in
	 * the calculateFrom() method.
	 * 
	 * The distance is defined as Double.NaN if the given node is not in the
	 * Graph that was traversed. The distance is defined as
	 * Double.POSITIVE_INFINITY if the given node cannot be reached from the
	 * source node.
	 * 
	 * @param node
	 *            The node for which the distance is desired
	 * @return the distance from the source node to the given node
	 */
	public double getDistanceTo(N node)
	{
		Double dist = nodeDistanceMap.get(node);
		if (dist == null)
		{
			return graph.containsNode(node) ? Double.POSITIVE_INFINITY
				: Double.NaN;
		}
		return dist.doubleValue();
	}

	/**
	 * Returns the distance to the given Edge from the source node provided in
	 * the calculateFrom() method.
	 * 
	 * The distance is defined as Double.NaN if the given Edge is not in the
	 * Graph that was traversed. The distance is defined as
	 * Double.POSITIVE_INFINITY if the given Edge cannot be reached from the
	 * source node.
	 * 
	 * @param edge
	 *            The Edge for which the distance is desired
	 * @return the distance from the source node to the given Edge
	 */
	public double getDistanceTo(ET edge)
	{
		Double dist = edgeDistanceMap.get(edge);
		if (dist == null)
		{
			return graph.containsEdge(edge) ? Double.POSITIVE_INFINITY
				: Double.NaN;
		}
		return dist.doubleValue();
	}

	/**
	 * Clears the distance results so that this DijkstraNodeAlgorithm may have
	 * the calculateFrom method called.
	 */
	public void clear()
	{
		heap.clear();
		nodeDistanceMap.clear();
		edgeDistanceMap.clear();
	}
}