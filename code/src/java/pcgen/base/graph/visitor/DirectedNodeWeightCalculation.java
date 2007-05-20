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
 * 
 * Created on Oct 12, 2004
 */
package pcgen.base.graph.visitor;

import java.util.HashMap;
import java.util.List;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalListMapGraph;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This calculates the minimum weight to a given node in a Graph. This is a
 * derivative of a Breadth First Traverse
 * 
 * Note: This derivative is optimized to a Graph which has low fan-in (meaning
 * each node has very vew incoming [source] edges). Therefore, is likely that
 * NodeWeightCalculation is only useful in certain Directed Graphs. If you are
 * calculating weight for a Graph with high fan-in (but low fan-out), you should
 * use a derivative of DirectedBreadthFirstTraverseAlgorithm.
 * 
 * It is assumed that the Edges have a defined weight. The default weight for
 * each edge is 1. This weight can be modified by a sub-Class by overriding the
 * calculateEdgeWeight method. Note that weights are MULTIPLIED as edges are
 * traversed.
 */
public class DirectedNodeWeightCalculation<N, ET extends DirectionalEdge<N>>
{

	private final ReverseDirectedBreadthFirstTraverseAlgorithm<N, ET> identification;

	/**
	 * Storage for the edges that were traversed and are relevant to weight
	 * calculation
	 */
	private final DirectionalGraph<N, ET> relevantGraph =
			new DirectionalListMapGraph<N, ET>();

	private final DirectionalGraph<N, ET> workingGraph =
			new DirectionalListMapGraph<N, ET>();

	private final HashMap<N, Integer> nodeWeightMap = new HashMap<N, Integer>();

	private final HashMap<ET, Integer> edgeWeightMap =
			new HashMap<ET, Integer>();

	/**
	 * Creates a new DirectedNodeWeightCalculation to traverse the given Graph.
	 * 
	 * @param g
	 *            The Graph this DirectedNodeWeightCalculation will traverse.
	 */
	public DirectedNodeWeightCalculation(DirectionalGraph<N, ET> g)
	{
		identification =
				new ReverseDirectedBreadthFirstTraverseAlgorithm<N, ET>(g);
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
	public int calculateNodeWeight(N gn)
	{
		if (!relevantGraph.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (gn == null)
		{
			throw new IllegalArgumentException(
				"Node to traverse from cannot be null");
		}
		/*
		 * FIXME This needs to be MULT:NO aware :(
		 * 
		 * One possibility is to make canTraverseEdge into a strategy, rather
		 * than an extendable method in the Traverse Algorithms. The "problem"
		 * with that is that it makes the Directed* Algorithms uglier, since
		 * there may a necessity to perform a CAST rather than using Generics.
		 * (Though it seems there is a way around this??) ... on the plus side,
		 * it WOULD eliminate the Directed*First* algorithms as separate
		 * classes...
		 */
		// FUTURE Shortcut if not present in Graph??
		identification.traverseFromNode(gn);
		runVisiting();
		Integer weight = nodeWeightMap.get(gn);
		return weight == null ? -1 : weight.intValue();
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
	public int calculateEdgeWeight(ET he)
	{
		if (!relevantGraph.isEmpty())
		{
			throw new UnsupportedOperationException();
		}
		if (he == null)
		{
			throw new IllegalArgumentException(
				"Edge to traverse from cannot be null");
		}
		identification.traverseFromEdge(he);
		runVisiting();
		Integer weight = edgeWeightMap.get(he);
		return weight == null ? -1 : weight.intValue();
	}

	private void runVisiting()
	{
		for (N node : identification.getVisitedNodes())
		{
			relevantGraph.addNode(node);
			workingGraph.addNode(node);
		}
		for (ET edge : identification.getVisitedEdges())
		{
			relevantGraph.addEdge(edge);
			workingGraph.addEdge(edge);
		}
		int loopDetect = -1;
		while (!workingGraph.isEmpty())
		{
			int currentNodeCount = workingGraph.getNodeCount();
			if (loopDetect == currentNodeCount)
			{
				// TODO Indicate failure...
				break;
			}
			else
			{
				loopDetect = currentNodeCount;
			}
			for (N node : workingGraph.getNodeList())
			{
				if (!workingGraph.getInwardEdgeList(node).isEmpty())
				{
					continue;
				}
				List<ET> inEdges = relevantGraph.getInwardEdgeList(node);
				int weight;
				if (inEdges.isEmpty())
				{
					weight = 1;
				}
				else
				{
					weight = 0;
					for (ET edge : inEdges)
					{
						weight += edgeWeightMap.get(edge).intValue();
					}
				}
				nodeWeightMap.put(node, Integer.valueOf(weight));
				for (ET edge : workingGraph.getOutwardEdgeList(node))
				{
					int edgeWeight = getEdgeWeight(weight, edge);
					edgeWeightMap.put(edge, Integer.valueOf(edgeWeight));
					workingGraph.removeEdge(edge);
				}
				workingGraph.removeNode(node);
			}
		}
	}

	protected int getEdgeWeight(int weight, ET edge)
	{
		return weight;
	}

	/**
	 * Clears the distance results so that this BreadthFirstTraverseAlgorithm
	 * may have a traverseFrom method called.
	 */
	public void clear()
	{
		identification.clear();
		relevantGraph.clear();
		workingGraph.clear();
	}
}