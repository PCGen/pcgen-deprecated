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

import java.util.Collection;
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
 * This is a derivative of Dijkstra's Algorithm.
 * 
 * For more information, see http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 * 
 * This is "Dijkstra's Edge Algorithm" because the Algorithm is traversing the
 * Edges (not the Nodes as Dijkstra's algorithm is technically defined to be).
 * It is assumed that the Nodes have a defined area. Therefore, there is a
 * length between the Edges of the Graph (and the Edges have length zero). This
 * is useful when a Graph is used to represent a map (with the Nodes as regions
 * and the Edges as borders between those regions). [Note this is a map in the
 * navigational sense not a Map in the java.util.Map sense] The default length
 * for traversing each node is 1. This length can be modified by a sub-Class by
 * overriding the calculateEdgeSeparation method.
 * 
 * Note that it is possible for the Node to have a different separation between
 * various edges to which it is connected.
 */
public class DijkstraEdgeAlgorithm<N, ET extends Edge<N>>
{

	/**
	 * The Graph on which this search algorithm is operating.
	 */
	private final Graph<N, ET> graph;

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
	 * The upper limit of distance to which this algorithm should search. This
	 * is used as a shortcut to terminate the search early if only objects
	 * within a certain distance of the source of the search are desired.
	 */
	private final double upperLimit;

	/**
	 * Creates a new DijkstraEdgeAlgorithm to traverse the given Graph.
	 * 
	 * @param g
	 *            The Graph this DijkstraEdgeAlgorithm will traverse.
	 */
	public DijkstraEdgeAlgorithm(Graph<N, ET> g)
	{
		this(g, Double.POSITIVE_INFINITY);
	}

	/**
	 * Creates a new DijkstraEdgeAlgorithm to traverse the given Graph. The
	 * Graph will only be traversed until the distance traversed over any
	 * individual path is equal to the given limit. (Note this is distance of
	 * any individual destination Node or Edge from the source Node or Edge, not
	 * a cumulative distance traversed over all paths)
	 * 
	 * @param g
	 *            The Graph this DijkstraEdgeAlgorithm will traverse.
	 * @param limit
	 *            The distance limit for any traversal.
	 */
	public DijkstraEdgeAlgorithm(Graph<N, ET> g, double limit)
	{
		super();
		if (g == null)
		{
			throw new IllegalArgumentException(
				"Graph for DijkstraEdgeAlgorithm cannot be null");
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
		edgeDistanceMap = new IdentityHashMap<ET, Double>();
		nodeDistanceMap = new IdentityHashMap<N, Double>();
	}

	/**
	 * Returns true if the given Edge can be traversed by this
	 * DijkstraEdgeAlgorithm. This check is performed in context to the given
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
	protected boolean canTraverseEdge(ET edge, N gn, int type)
	{
		return true;
	}

	/**
	 * Calculates the distance (within the limit specified during construction
	 * of the DijkstraEdgeAlgorithm) to all Nodes and Edges in the Graph from
	 * the given source Edge.
	 * 
	 * Results of this calculation are available from the getDistanceTo methods.
	 * 
	 * Calling a calculateFrom*() method a second time without calling clear()
	 * will result in an UnsupportedOperationException being thrown.
	 * 
	 * @param gn
	 *            The source Edge to be used for distance calculation.
	 */
	public void calculateFromEdge(ET ge)
	{
		if (!edgeDistanceMap.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (ge == null)
		{
			throw new IllegalArgumentException(
				"Edge to calculate from cannot be null");
		}
		if (!graph.containsEdge(ge))
		{
			throw new IllegalArgumentException("Edge is not a part of Graph");
		}
		List<N> graphNodes = ge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			if (canTraverseEdge(ge, node, DirectionalEdge.SOURCE))
			{
				heap.add(new GraphHeapComponent<N, ET>(0.0, ge, node));
			}
		}
		runCalculation();
	}

	/**
	 * Calculates the distance (within the limit specified during construction
	 * of the DijkstraEdgeAlgorithm) to all Nodes and Edges in the Graph from
	 * the given source Node.
	 * 
	 * Results of this calculation are available from the getDistanceTo methods.
	 * 
	 * Calling a calculateFrom*() method a second time without calling clear()
	 * will result in an UnsupportedOperationException being thrown.
	 * 
	 * @param gn
	 *            The source Node to be used for distance calculation.
	 */
	public void calculateFromNode(N gn)
	{
		if (!edgeDistanceMap.isEmpty())
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
		for (ET thisEdge : graph.getAdjacentEdges(gn))
		{
			if (canTraverseEdge(thisEdge, gn, DirectionalEdge.SOURCE))
			{
				heap.add(new GraphHeapComponent<N, ET>(0.0, thisEdge, gn));
			}
		}
		runCalculation();
	}

	/**
	 * Actually runs Dijkstra's Algorithm
	 */
	private void runCalculation()
	{
		while (!heap.isEmpty())
		{
			GraphHeapComponent<N, ET> ghc = heap.poll();
			if (!edgeDistanceMap.containsKey(ghc.edge))
			{
				edgeDistanceMap.put(ghc.edge, Double.valueOf(ghc.distance));
				processAdjacentNodes(ghc);
			}
		}
	}

	private void processAdjacentNodes(GraphHeapComponent<N, ET> ghc)
	{
		List<N> graphNodes = ghc.edge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			N destinationNode = node;
			// Make sure we can traverse the edge in this direction
			if (canTraverseEdge(ghc.edge, destinationNode, DirectionalEdge.SINK))
			{
				/*
				 * Do NOT throw out ghc.node (similar to
				 * iterateOverAdjacentNodes in DijkstraNodeAlgorithm) because if
				 * the graph was calculateFrom(HyperEdge) then the node in the
				 * heap has NOT been visited. Thus we need to visit everything,
				 * and allow the edgeDistanceMap.containsKey() check in
				 * runCalculation() to throw out when we have hit an edge a
				 * second time.
				 */
				if (!nodeDistanceMap.containsKey(destinationNode))
				{
					nodeDistanceMap.put(destinationNode, Double
						.valueOf(ghc.distance));
				}
				addGoodAdjacentEdgesToHeap(ghc, destinationNode);
			}
		}
	}

	private void addGoodAdjacentEdgesToHeap(GraphHeapComponent<N, ET> ghc,
		N destinationNode)
	{
		Collection<ET> l = graph.getAdjacentEdges(destinationNode);
		l.remove(ghc.edge);
		for (ET thisEdge : l)
		{
			if (canTraverseEdge(thisEdge, destinationNode,
				DirectionalEdge.SOURCE))
			{
				double distanceToEdge =
						ghc.distance
							+ calculateEdgeSeparation(ghc.edge,
								destinationNode, thisEdge);
				if (distanceToEdge < upperLimit)
				{
					heap.add(new GraphHeapComponent<N, ET>(distanceToEdge,
						thisEdge, destinationNode));
				}
			}
		}
	}

	/**
	 * Returns the separation between the two given edges through the given
	 * Node.
	 * 
	 * @param source
	 *            The source edge for separation calculation
	 * @param other
	 *            The node being traversed
	 * @param thisEdge
	 *            The sink edge for separation calcualtion
	 * @return The distance traversed through the node between the given source
	 *         Edge and sink Edge
	 */
	protected double calculateEdgeSeparation(ET source, N other, ET thisEdge)
	{
		return 1.0;
	}

	/**
	 * Returns the distance to the given Edge from the source node or edge
	 * provided in the calculateFrom() method.
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
	 * Returns the distance to the given Node from the source node or edge
	 * provided in the calculateFrom() method.
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
	 * Returns the closest Edge to the source Node or Edge which is also
	 * connected to the given Node.
	 * 
	 * @param node
	 *            The node to find the closest edge attached to it.
	 * @return The closest Edge to the source Node or Edge which is also
	 *         connected to the given Node
	 */
	public ET getClosestEdgeOnNode(N node)
	{
		if (!graph.containsNode(node))
		{
			// implicitly covers null argument
			throw new IllegalArgumentException("Graph must contain GraphNode");
		}
		ET edge = null;
		double dist = Double.POSITIVE_INFINITY;
		for (ET thisEdge : graph.getAdjacentEdges(node))
		{
			double thisDist = getDistanceTo(thisEdge);
			if (thisDist < dist)
			{
				dist = thisDist;
				edge = thisEdge;
			}
		}
		return edge;
	}

	/**
	 * Clears the distance results so that this DijkstraEdgeAlgorithm may have
	 * the calculateFrom method called.
	 */
	public void clear()
	{
		heap.clear();
		edgeDistanceMap.clear();
		nodeDistanceMap.clear();
	}
}