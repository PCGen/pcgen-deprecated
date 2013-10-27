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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This Graph uses redundant storage to improve query speed for certain methods.
 * In addition to simple lists of the nodes and edges present in a Graph, a Map
 * from each node to the adjacent edges is maintained.
 * 
 * This Graph uses normal equality (.equals()) to determine equality for
 * purposes of checking whether nodes and edges are already part of the Graph.
 * 
 * This Graph implementation provides a more balanced query speed for querying
 * adjacent graph elements. Specifically, an edge knows to which nodes it is
 * connected. To determine which edges a node is connected to requires a query
 * to the Graph. The Map maintained by this class prevents an iteration over the
 * entire List of edges whenever getAdjacentEdgeList(GraphNode n) is called.
 * 
 * If frequent testing of containsNode() or containsEdge() is required in a
 * Graph (and the Graph has a large number of nodes), this is not a good Graph
 * implementation. Because the contains test must iterate through a List, it is
 * significantly slower than other possible implementations. For a Graph
 * implementation that is much faster for such tests, see AbstractSetMapGraph.
 * 
 * WARNING: This AbstractListMapGraph contains a CACHE which uses the Nodes as a
 * KEY. Due to the functioning of a Map (it uses the .hashCode() method), if a
 * Node is modified IN PLACE in the Graph (without being removed and readded),
 * it WILL cause the caching to FAIL, because the cache will have indexed the
 * Node by the old hash code. It is therefore HIGHLY advised that this Graph
 * implementation ONLY be used where the Nodes are either Immutable or do not
 * override Object.equals().
 * 
 * Note: It is NOT possible for an edge to connect to a node which is not in the
 * graph. There are (at least) two side effects to this limit: (1) If an edge is
 * added when the nodes to which it is not connected are not in the Graph, those
 * nodes will be implicitly added to the graph. (2) If a node is removed from
 * the Graph, all of the edges connected to that node will also be removed from
 * the graph.
 * 
 * WARNING: This Graph has SIDE EFFECTS. When any GraphNode is deleted from the
 * graph, ANY and ALL Edges connected to that GraphNode are implicitly deleted
 * from the graph. You CANNOT rely on the GraphNodeRemoved event, as it will
 * occur AFTER all of the attached edges have been removed. You must check for
 * and clean up adjacent edges BEFORE removing any GraphNode if you wish for
 * those edges (in a modified form) to remain in the graph.
 */
public abstract class AbstractListMapGraph<N, ET extends Edge<N>> implements
		Graph<N, ET>
{

	/**
	 * The List of nodes contained in this Graph.
	 */
	private final List<N> nodeList;

	/**
	 * The List of edges contained in this Graph. An edge must be connected to a
	 * node which is already in the nodeList (this makes no statement about
	 * whether this addition is done implicitly by addEdge [it is in
	 * AbstractListMapGraph] or whether it is explicit).
	 */
	private final List<ET> edgeList;

	/**
	 * A Map indicating which nodes are connected to which edges. This is
	 * redundant information to what is actually contained in the edges
	 * themselves, but is present in AbstractListMapGraph in order to speed
	 * calls to getAdjacentEdges
	 */
	private final transient Map<N, Set<ET>> nodeEdgeMap;

	/**
	 * The GraphChangeSupport object which provides management of
	 * GraphChangeListeners and fires events to the listeners.
	 */
	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * Creates a new, empty AbstractListMapGraph
	 */
	public AbstractListMapGraph()
	{
		super();
		edgeList = new ArrayList<ET>();
		nodeList = new ArrayList<N>();
		gcs = new GraphChangeSupport<N, ET>(this);
		nodeEdgeMap = new HashMap<N, Set<ET>>();
	}

	/**
	 * Adds the given Node to the Graph. Returns true if the given Node was
	 * successfully added.
	 * 
	 * @see pcgen.base.graph.core.Graph#addNode(java.lang.Object)
	 */
	public boolean addNode(N v)
	{
		if (v == null)
		{
			return false;
		}
		if (nodeEdgeMap.containsKey(v))
		{
			// Node already in this Graph
			return false;
		}
		nodeList.add(v);
		nodeEdgeMap.put(v, new HashSet<ET>());
		gcs.fireGraphNodeChangeEvent(v, NodeChangeEvent.NODE_ADDED);
		return true;
	}

	/**
	 * Returns the node actually stored in the graph that is equal to the given
	 * node. This is used to avoid memory leaks in the case of matching Nodes
	 * (to avoid storing a Node that is .equal but not == in an edge that will
	 * be placed into the Graph).
	 * 
	 * @param v
	 *            The Node to be internalized.
	 * @return The internalized version of the Node, relative to this Graph.
	 */
	public N getInternalizedNode(N v)
	{
		if (v == null)
		{
			return null;
		}
		if (nodeEdgeMap.containsKey(v))
		{
			// Node already in this Graph
			/*
			 * This is using a slow method (array search) when it would be nice
			 * to be able to get the reference from the Map. However, that would
			 * require the use of Jakarta Common Collections and extending their
			 * HashMap in order to do that... because it's normally impossible
			 * to get a Key back out of a Map without iterating over the Entries :/
			 */
			return nodeList.get(nodeList.indexOf(v));
		}
		// TODO Consider whether to return null or v... ?
		return null;
	}

	/**
	 * Adds the given Edge to the Graph. Returns true if the given Edge was
	 * successfully added. Implicitly adds any Nodes connected to the given Edge
	 * to the Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#addEdge(java.lang.Object)
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
			addNode(node);
			nodeEdgeMap.get(node).add(e);
		}
		edgeList.add(e);
		gcs.fireGraphEdgeChangeEvent(e, EdgeChangeEvent.EDGE_ADDED);
		return true;
	}

	/**
	 * Returns true if this Graph contains the given Node.
	 * 
	 * @see pcgen.base.graph.core.Graph#containsNode(java.lang.Object)
	 */
	public boolean containsNode(Object v)
	{
		// This is presumably faster than searching through nodeList
		return nodeEdgeMap.containsKey(v);
	}

	/**
	 * Returns true if this Graph contains the given Edge
	 * 
	 * @see pcgen.base.graph.core.Graph#containsEdge(pcgen.base.graph.core.Edge)
	 */
	public boolean containsEdge(Edge<?> e)
	{
		return edgeList.contains(e);
	}

	/**
	 * Returns a List of Nodes in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by AbstractListMapGraph.
	 * 
	 * However, the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the AbstractListMapGraph.
	 * 
	 * *WARNING*: Modification of the Nodes in place may result in failure of
	 * the AbstractListMapGraph to return appropriate values from various
	 * methods of AbstractListMapGraph. If a Node is modified in place, the
	 * modifications must not alter the hash code (as returned by the Node's
	 * .hashCode() method) for AbstractListMapGraph to maintain proper
	 * operation.
	 * 
	 * @see pcgen.base.graph.core.Graph#getNodeList()
	 */
	public List<N> getNodeList()
	{
		return new ArrayList<N>(nodeList);
	}

	/**
	 * Returns a List of Edges in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by AbstractListMapGraph.
	 * However, the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the AbstractListMapGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getEdgeList()
	 */
	public List<ET> getEdgeList()
	{
		return new ArrayList<ET>(edgeList);
	}

	/**
	 * Removes the given Node from the AbstractListMapGraph. As a byproduct of
	 * this removal, all Edges connected to the Node will also be removed from
	 * the Graph.
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
		/*
		 * Note: This method is command sequence sensitive.
		 * 
		 * First, the remove (from nodeEdgeMap) below is "guaranteed" to work,
		 * since the graph must contain the node (test above) and it is assumed
		 * that the addNode method initialized nodeEdgeMap.
		 * 
		 * Second, the use of remove is significant, in that it removes the set
		 * of connected edges from the Map. This is important, since removeEdge
		 * is called from within the loop, and removeEdge will alter sets within
		 * nodeEdgeMap. Therefore, the use of get in place of remove for
		 * creation of this Iterator would result in a
		 * ConcurrentModificationException (since the set for GraphNode gn would
		 * be modified by removeEdge while inside this Iterator).
		 */
		for (ET edge : nodeEdgeMap.remove(gn))
		{
			// FUTURE Consider Check of return values here to ensure success??
			removeEdge(edge);
		}
		/*
		 * containsNode test means we don't need to check return value of remove
		 * we 'know' it is present (barring an internal error!). This remove
		 * must happen after removeEdge above, as removeEdge may trigger side
		 * effects that will expect this Node to still be present in the Graph.
		 */
		nodeList.remove(gn);
		gcs.fireGraphNodeChangeEvent(gn, NodeChangeEvent.NODE_REMOVED);
		return true;
	}

	/**
	 * Removes the given Edge from the AbstractListMapGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(ET ge)
	{
		if (ge == null)
		{
			return false;
		}
		List<N> graphNodes = ge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			Set<ET> set = nodeEdgeMap.get(node);
			/*
			 * null Protection required in case edge wasn't actually in the
			 * graph
			 */
			if (set != null)
			{
				set.remove(ge);
			}
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
	 * reference to the Set Object is maintained by AbstractListMapGraph.
	 * However, the Edges contained in the Set are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the AbstractListMapGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getAdjacentEdges(java.lang.Object)
	 */
	public Set<ET> getAdjacentEdges(N gn)
	{
		// implicitly returns null if gn is not in the nodeEdgeMap
		Set<ET> s = nodeEdgeMap.get(gn);
		return s == null ? null : new HashSet<ET>(s);
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
	 * reference to the Array is maintained by AbstractListMapGraph. However,
	 * the GraphChangeListeners contained in the Array are (obviously!) returned
	 * BY REFERENCE, and care should be taken with modifying those
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
			System.err.println("Not equal node count");
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherNodeList = new ArrayList<N>(otherNodeList);
		if (otherNodeList.retainAll(nodeList))
		{
			// Some nodes are not identical
			System.err.println("Not equal node list");
			System.err.println(nodeList);
			System.err.println(otherNodeList);
			return false;
		}
		// Here, the node lists are identical...
		List<ET> otherEdgeList = otherGraph.getEdgeList();
		int thisEdgeSize = edgeList.size();
		if (thisEdgeSize != otherEdgeList.size())
		{
			System.err.println("Not equal edge count");
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherEdgeList = new ArrayList<ET>(otherEdgeList);
		if (otherEdgeList.retainAll(edgeList))
		{
			// Other Graph contains extra edges
			System.err.println("not equal edge retain");
			System.err.println(edgeList);
			System.err.println(otherEdgeList);
			return false;
		}
		return true;
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
		nodeEdgeMap.clear();
		nodeList.clear();
		edgeList.clear();
	}
}