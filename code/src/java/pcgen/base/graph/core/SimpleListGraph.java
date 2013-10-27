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
 * Created on Aug 26, 2004
 */
package pcgen.base.graph.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This class is a simple Graph which stores a List of the nodes and edges in
 * the Graph. While this may not be the most efficient storage mechanism from a
 * speed perspective, it is relatively efficient in terms of memory use.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * Note: It is NOT possible for an edge to connect to a node which is not in the
 * graph. There are (at least) two side effects to this limit: (1) If an edge is
 * added when the nodes to which it is not connected are not in the Graph, those
 * nodes will be implicitly added to the graph. (2) If a node is removed from
 * the Graph, all of the edges connected to that node will also be removed from
 * the graph.
 * 
 * WARNING: This GraphStorageStrategy has SIDE EFFECTS. When any GraphNode is
 * deleted from the graph, ANY and ALL HyperEdges connected to that GraphNode
 * are implicitly deleted from the graph. You CANNOT rely on the
 * GraphNodeRemoved event, as it will occur AFTER all of the attached edges have
 * been removed. You must check for and clean up adjacent edges BEFORE removing
 * any GraphNode if you wish for those edges to remain (in a modified form) in
 * the graph.
 */
public class SimpleListGraph<N, ET extends Edge<N>> implements Graph<N, ET>
{

	/**
	 * The List of nodes contained in this Graph.
	 */
	private final List<N> nodeList;

	/**
	 * The List of edges contained in this Graph. An edge must be connected to a
	 * node which is already in the nodeList (this makes no statement about
	 * whether this addition is done implicitly by addEdge [it is in
	 * SimpleListGraph] or whether it is explicit).
	 */
	private final List<ET> edgeList;

	/**
	 * The GraphChangeSupport object which provides management of
	 * GraphChangeListeners and fires events to the listeners.
	 */
	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * Create a new, empty SimpleListMapGraph
	 */
	public SimpleListGraph()
	{
		super();
		edgeList = new ArrayList<ET>();
		nodeList = new ArrayList<N>();
		gcs = new GraphChangeSupport<N, ET>(this);
	}

	/**
	 * Add the given Node to the Graph
	 * 
	 * @see pcgen.base.graph.core.Graph#addNode(java.lang.Object)
	 */
	public boolean addNode(N v)
	{
		if (v == null)
		{
			return false;
		}
		if (nodeList.contains(v))
		{
			return false;
		}
		nodeList.add(v);
		gcs.fireGraphNodeChangeEvent(v, NodeChangeEvent.NODE_ADDED);
		return true;
	}

	/**
	 * Adds the given Edge to the Graph.
	 * 
	 * @see rpgmapgen.map.graph.GraphStorageStrategy#addEdge(rpgmapgen.map.graph.Edge)
	 */
	public boolean addEdge(ET e)
	{
		if (e == null)
		{
			return false;
		}
		if (edgeList.contains(e))
		{
			return false;
		}
		List<N> graphNodes = e.getAdjacentNodes();
		for (N node : graphNodes)
		{
			if (!nodeList.contains(node))
			{
				addNode(node);
			}
		}
		edgeList.add(e);
		gcs.fireGraphEdgeChangeEvent(e, EdgeChangeEvent.EDGE_ADDED);
		return true;
	}

	/**
	 * Returns true if the Graph contains the given Object as a Node.
	 * 
	 * @see pcgen.base.graph.core.Graph#containsNode(java.lang.Object)
	 */
	public boolean containsNode(Object v)
	{
		return nodeList.contains(v);
	}

	/**
	 * Returns true if the Graph contains the given Object as an Edge.
	 * 
	 * @see pcgen.base.graph.core.Graph#containsEdge(pcgen.base.graph.core.Edge)
	 */
	public boolean containsEdge(Edge<?> e)
	{
		return edgeList.contains(e);
	}

	/**
	 * Returns a List of the Nodes contained within this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by SimpleListGraph. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the SimpleListGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getNodeList()
	 */
	public List<N> getNodeList()
	{
		return new ArrayList<N>(nodeList);
	}

	/**
	 * Returns a List of the Edges contained in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by SimpleListGraph. However,
	 * the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the SimpleListGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getEdgeList()
	 */
	public List<ET> getEdgeList()
	{
		return new ArrayList<ET>(edgeList);
	}

	/**
	 * Removes the given Node from the Graph.
	 * 
	 * As a side effect, any Edges contained within the Graph which are
	 * connected to the given Node are also removed from the Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#removeNode(java.lang.Object)
	 */
	public boolean removeNode(N gn)
	{
		if (gn == null)
		{
			return false;
		}
		if (!containsNode(gn))
		{
			return false;
		}
		boolean successful = true;
		for (ET edge : getAdjacentEdges(gn))
		{
			successful &= removeEdge(edge);
		}
		if (successful)
		{
			successful &= nodeList.remove(gn);
		}
		if (successful)
		{
			gcs.fireGraphNodeChangeEvent(gn, NodeChangeEvent.NODE_REMOVED);
		}
		return successful;
	}

	/**
	 * Removes the given Edge from the Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#removeEdge(pcgen.base.graph.core.Edge)
	 */
	public boolean removeEdge(ET ge)
	{
		if (ge == null)
		{
			return false;
		}
		if (edgeList.remove(ge))
		{
			gcs.fireGraphEdgeChangeEvent(ge, EdgeChangeEvent.EDGE_REMOVED);
			return true;
		}
		return false;
	}

	/**
	 * Returns a Set of the Edges which are Adjacent (connected) to the given
	 * Node. Returns null if the given Node is not in the Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by SimpleListGraph. However,
	 * the Edges contained in the Set are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the SimpleListGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getAdjacentEdges(java.lang.Object)
	 */
	public Set<ET> getAdjacentEdges(N gn)
	{
		if (!containsNode(gn))
		{
			return null;
		}
		Set<ET> adjacentEdgeList = new HashSet<ET>();
		EDGEITER: for (ET ge : edgeList)
		{
			List<N> graphNodes = ge.getAdjacentNodes();
			for (N node : graphNodes)
			{
				if (node.equals(gn))
				{
					adjacentEdgeList.add(ge);
					continue EDGEITER;
				}
			}
		}
		return adjacentEdgeList;
	}

	/**
	 * Adds the given GraphChangeListener as a GraphChangeListener of this
	 * Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#addGraphChangeListener(pcgen.base.graph.core.GraphChangeListener)
	 */
	public void addGraphChangeListener(GraphChangeListener<N, ET> arg0)
	{
		gcs.addGraphChangeListener(arg0);
	}

	/**
	 * Returns an array of the GraphChangeListeners to this Graph.
	 * 
	 * Ownership of the returned Array is transferred to the calling Object. No
	 * reference to the Array is maintained by SimpleListGraph. However, the
	 * GraphChangeListeners contained in the Array are (obviously!) returned BY
	 * REFERENCE, and care should be taken with modifying those
	 * GraphChangeListeners.
	 * 
	 * @see pcgen.base.graph.core.Graph#getGraphChangeListeners()
	 */
	public GraphChangeListener<N, ET>[] getGraphChangeListeners()
	{
		return gcs.getGraphChangeListeners();
	}

	/**
	 * Removes the given GraphChangeListener as a GraphChangeListener of this
	 * Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#removeGraphChangeListener(pcgen.base.graph.core.GraphChangeListener)
	 */
	public void removeGraphChangeListener(GraphChangeListener<N, ET> arg0)
	{
		gcs.removeGraphChangeListener(arg0);
	}

	/**
	 * Tests to see if this Graph is equal to the provided Object. This will
	 * return true if the given Object is also a Graph, and that Graph contains
	 * equal Nodes and Edges.
	 * 
	 * @param o
	 *            The Object to be tested for equality with this Graph
	 * @return true if the given Object is a Graph that contains equal Nodes and
	 *         Edges to this Graph; false otherwise
	 */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Graph))
		{
			return false;
		}
		Graph<N, ET> otherGraph = (Graph<N, ET>) other;
		List<N> otherNodeList = otherGraph.getNodeList();
		int thisNodeSize = nodeList.size();
		if (thisNodeSize != otherNodeList.size())
		{
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherNodeList = new ArrayList<N>(otherNodeList);
		if (otherNodeList.retainAll(nodeList))
		{
			// Other Graph contains extra nodes
			return false;
		}
		// Here, the node lists are identical...
		List<ET> otherEdgeList = otherGraph.getEdgeList();
		int thisEdgeSize = edgeList.size();
		if (thisEdgeSize != otherEdgeList.size())
		{
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherEdgeList = new ArrayList<ET>(otherEdgeList);
		// possible that the Other Graph contains extra edges
		return !otherEdgeList.retainAll(edgeList);
	}

	/**
	 * Returns the hashCode for this Graph.
	 * 
	 * @return the hashCode for this Graph.
	 */
	@Override
	public int hashCode()
	{
		// This is really simple, but it works... and prevents a deep hash
		return nodeList.size() + edgeList.size() * 23;
	}

	/**
	 * Returns true if this Graph is empty (has no Nodes and no Edges); false
	 * otherwise.
	 * 
	 * @return true if this Graph is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return nodeList.isEmpty() && edgeList.isEmpty();
	}

	/**
	 * Returns the number of nodes in this Graph.
	 * 
	 * @return The number of nodes in the Graph, as an integer
	 */
	public int getNodeCount()
	{
		return nodeList.size();
	}

	/**
	 * Clears this Graph, removing all Nodes and Edges from the Graph.
	 */
	public void clear()
	{
		/*
		 * TODO This doesn't actually notify GraphChangeListeners, is that a
		 * problem? - probably is ... thpr, 6/27/07
		 */
		nodeList.clear();
		edgeList.clear();
	}
}