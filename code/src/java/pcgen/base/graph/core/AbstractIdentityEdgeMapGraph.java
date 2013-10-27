/*
 * Copyright (c) Thomas Parker, 2007.
 *   This is a derivative of AbstractListMapGraph (c) 2004-2007
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
 * Created on June 30, 2007
 */
package pcgen.base.graph.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.IdentityHashSet;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * This Graph uses redundant storage to improve query speed for certain methods.
 * In addition to simple lists of the nodes and edges present in a Graph, a Map
 * from each node to the adjacent edges is maintained.
 * 
 * This Graph uses identiy equality (==) to determine equality for purposes of
 * checking whether nodes and edges are already part of the Graph.
 * 
 * This class provides a more balanced query speed for querying adjacent graph
 * elements. Specifically, an edge knows to which nodes it is connected. To
 * determine which edges a node is connected to requires a query to the Graph.
 * The Map maintained by this class prevents an iteration over the entire List
 * of edges whenever getAdjacentEdgeList(GraphNode n) is called.
 * 
 * WARNING: This AbstractIdentityEdgeMapGraph contains a CACHE which uses the
 * Nodes as a KEY. Due to the functioning of a Map (it uses the .hashCode()
 * method), if a Node is modified IN PLACE in the Graph (without being removed
 * and readded), it WILL cause the caching to FAIL, because the cache will have
 * indexed the Node by the old hash code. It is therefore HIGHLY advised that
 * this Graph implementation ONLY be used where the Nodes are either Immutable
 * or do not override Object.equals().
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
public abstract class AbstractIdentityEdgeMapGraph<N, ET extends Edge<N>>
		implements Graph<N, ET>
{

	/**
	 * The Set of nodes contained in this Graph.
	 */
	private final Map<N, N> nodeMap;

	/**
	 * The Set of edges contained in this Graph. An edge must be connected to a
	 * node which is already in the nodeList (this makes no statement about
	 * whether this addition is done implicitly by addEdge [it is in
	 * AbstractIdentityEdgeMapGraph] or whether it is explicit).
	 */
	private final Set<ET> edgeSet;

	/**
	 * A Map indicating which nodes are connected to which edges. This is
	 * redundant information to what is actually contained in the edges
	 * themselves, but is present in AbstractIdentityEdgeMapGraph in order to
	 * speed calls to getAdjacentEdges
	 */
	private final transient Map<N, Set<ET>> nodeEdgeMap;

	/**
	 * The GraphChangeSupport object which provides management of
	 * GraphChangeListeners and fires events to the listeners.
	 */
	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * Creates a new, empty AbstractIdentityEdgeMapGraph
	 */
	public AbstractIdentityEdgeMapGraph()
	{
		super();
		edgeSet = new IdentityHashSet<ET>();
		nodeMap = new HashMap<N, N>();
		gcs = new GraphChangeSupport<N, ET>(this);
		nodeEdgeMap = new HashMap<N, Set<ET>>();
	}

	/**
	 * Adds the given Node to the Graph. Returns true if the given Node was
	 * successfully added. Because the Nodes in this Graph are a Set, this
	 * method will return false if a Node is already present in the Graph.
	 * 
	 * @see pcgen.base.graph.core.Graph#addNode(java.lang.Object)
	 */
	public boolean addNode(N v)
	{
		if (v == null)
		{
			return false;
		}
		if (nodeMap.containsKey(v))
		{
			// Node already in this Graph
			return false;
		}
		nodeMap.put(v, v);
		nodeEdgeMap.put(v, new IdentityHashSet<ET>());
		gcs.fireGraphNodeChangeEvent(v, NodeChangeEvent.NODE_ADDED);
		return true;
	}

	public N getInternalizedNode(N v)
	{
		if (v == null)
		{
			return null;
		}
		// TODO FIXME Consider whether to return null or v... if not in the
		// Graph?
		return nodeMap.get(v);
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
		boolean added = edgeSet.add(e);
		if (!added)
		{
			return false;
		}
		List<N> graphNodes = e.getAdjacentNodes();
		for (N node : graphNodes)
		{
			addNode(node);
			nodeEdgeMap.get(node).add(e);
		}
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
	 * Returns true if this Graph contains the given Edge.
	 * 
	 * @see pcgen.base.graph.core.Graph#containsEdge(pcgen.base.graph.core.Edge)
	 */
	public boolean containsEdge(Edge<?> e)
	{
		return edgeSet.contains(e);
	}

	/**
	 * Returns a List of Nodes in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * AbstractIdentityEdgeMapGraph.
	 * 
	 * However, the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the AbstractIdentityEdgeMapGraph.
	 * 
	 * *WARNING*: Modification of the Nodes in place may result in failure of
	 * the AbstractIdentityEdgeMapGraph to return appropriate values from
	 * various methods of AbstractIdentityEdgeMapGraph. If a Node is modified in
	 * place, the modifications must not alter the hash code (as returned by the
	 * Node's .hashCode() method) for AbstractIdentityEdgeMapGraph to maintain
	 * proper operation.
	 * 
	 * @see pcgen.base.graph.core.Graph#getNodeList()
	 */
	public List<N> getNodeList()
	{
		return new ArrayList<N>(nodeMap.keySet());
	}

	/**
	 * Returns a List of Edges in this Graph.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by
	 * AbstractIdentityEdgeMapGraph. However, the Edges contained in the List
	 * are returned BY REFERENCE, and modification of the returned Edges will
	 * modify the Edges contained within the AbstractIdentityEdgeMapGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getEdgeList()
	 */
	public List<ET> getEdgeList()
	{
		return new ArrayList<ET>(edgeSet);
	}

	/**
	 * Removes the given Node from the AbstractIdentityEdgeMapGraph. As a
	 * byproduct of this removal, all Edges connected to the Node will also be
	 * removed from the Graph.
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
		nodeMap.remove(gn);
		gcs.fireGraphNodeChangeEvent(gn, NodeChangeEvent.NODE_REMOVED);
		return true;
	}

	/**
	 * Removes the given Edge from the AbstractIdentityEdgeMapGraph. This
	 * removes the edge based on object equality (.equals) not instance equality
	 * (==). If you require instance equality, please use removeEdgeInstance()
	 * 
	 * @see pcgen.base.graph.core.Graph#removeEdge(java.lang.Object)
	 */
	public boolean removeEdge(ET ge)
	{
		if (ge == null)
		{
			return false;
		}
		boolean removed = edgeSet.remove(ge);
		if (!removed)
		{
			return false;
		}
		/*
		 * Must be present in the Graph if we made it to this point
		 */
		List<N> graphNodes = ge.getAdjacentNodes();
		for (N node : graphNodes)
		{
			Set<ET> thing = nodeEdgeMap.get(node);
			// Could be null due to side effects
			if (thing != null)
			{
				thing.remove(ge);
			}
		}
		gcs.fireGraphEdgeChangeEvent(ge, EdgeChangeEvent.EDGE_REMOVED);
		return true;
	}

	/**
	 * Returns a Set of the Edges which are Adjacent (connected) to the given
	 * Node. Returns null if the given Node is not in the Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by
	 * AbstractIdentityEdgeMapGraph. However, the Edges contained in the Set are
	 * returned BY REFERENCE, and modification of the returned Edges will modify
	 * the Edges contained within the AbstractIdentityEdgeMapGraph.
	 * 
	 * @see pcgen.base.graph.core.Graph#getAdjacentEdges(java.lang.Object)
	 */
	public List<ET> getAdjacentEdges(N gn)
	{
		// implicitly returns null if gn is not in the nodeEdgeMap
		Set<ET> s = nodeEdgeMap.get(gn);
		return s == null ? null : new ArrayList<ET>(s);
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
	 * reference to the Array is maintained by AbstractIdentityEdgeMapGraph.
	 * However, the GraphChangeListeners contained in the Array are (obviously!)
	 * returned BY REFERENCE, and care should be taken with modifying those
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
		int thisNodeSize = nodeMap.size();
		if (thisNodeSize != otherNodeList.size())
		{
			System.err.println("Not equal node count");
			System.err.println(nodeMap.keySet());
			System.err.println(otherNodeList);
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherNodeList = new ArrayList<N>(otherNodeList);
		if (otherNodeList.retainAll(nodeMap.keySet()))
		{
			// Some nodes are not identical
			System.err.println("Not equal node list");
			System.err.println(nodeMap.keySet());
			System.err.println(otherNodeList);
			ArrayList<N> al = new ArrayList<N>(nodeMap.keySet());
			al.removeAll(otherNodeList);
			for (Object o : al)
			{
				System.err.println("1- " + o.hashCode() + " " + o);
			}
			System.err.println("?!?");
			ArrayList<N> al2 = new ArrayList<N>(otherGraph.getNodeList());
			al2.removeAll(otherNodeList);
			for (Object o : al2)
			{
				System.err.println("2- " + o.hashCode() + " " + o);
			}
			System.err.println(al.equals(al2));
			System.err.println(al2.equals(al));
			return false;
		}
		// Here, the node lists are identical...
		List<ET> otherEdgeList = otherGraph.getEdgeList();
		int thisEdgeSize = edgeSet.size();
		if (thisEdgeSize != otherEdgeList.size())
		{
			System.err.println("Not equal edge count");
			System.err.println(edgeSet);
			System.err.println(otherEdgeList);
			return false;
		}
		// (potentially wasteful, but defensive copy)
		otherEdgeList = new ArrayList<ET>(otherEdgeList);
		if (otherEdgeList.retainAll(edgeSet))
		{
			// Other Graph contains extra edges
			System.err.println("not equal edge retain");
			System.err.println(edgeSet);
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
		return nodeMap.size() + edgeSet.size() * 23;
	}

	/**
	 * Returns true if this Graph is empty (has no Nodes and no Edges); false
	 * otherwise.
	 * 
	 * @return true if this Graph is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return nodeMap.isEmpty() && edgeSet.isEmpty();
	}

	/**
	 * Returns the number of nodes in this Graph.
	 * 
	 * @return The number of nodes in the Graph, as an integer
	 */
	public int getNodeCount()
	{
		return nodeMap.size();
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
		nodeMap.clear();
		edgeSet.clear();
	}
}