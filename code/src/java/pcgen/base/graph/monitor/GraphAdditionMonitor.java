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
 * Created on Sep 10, 2004
 */
package pcgen.base.graph.monitor;

import java.util.HashSet;
import java.util.Set;

import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.NodeChangeEvent;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A GraphAdditionMonitor is a GraphChangeListener that keeps track of Nodes and
 * Edges added to a Graph. If an object is added to a graph and subsequently
 * removed from the Graph, then it is also deleted from the sets maintained by
 * the GraphAdditionMonitor. A GraphAdditionMonitor makes no effort to track the
 * removal of objects which were added to the graph before the
 * GraphAdditionMonitor was added as a GraphChangeListener to the Graph.
 */
public class GraphAdditionMonitor<N, ET extends Edge<N>> implements
		GraphChangeListener<N, ET>
{

	/**
	 * The List of Nodes added to the Graph(s) to which this
	 * GraphAdditionMonitor is listening.
	 */
	private final Set<N> nodeList = new HashSet<N>();

	/**
	 * The List of Edges added to the Graph(s) to which this
	 * GraphAdditionMonitor is listening.
	 */
	private final Set<ET> edgeList = new HashSet<ET>();

	/**
	 * Creates a new GraphAdditionMonitor
	 */
	public GraphAdditionMonitor()
	{
		super();
	}

	/**
	 * Called when a Node is added to a Graph to which this GraphAdditionMonitor
	 * is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void nodeAdded(NodeChangeEvent<N> gce)
	{
		nodeList.add(gce.getGraphNode());
	}

	/**
	 * Called when a Node is removed from a Graph to which this
	 * GraphAdditionMonitor is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeRemoved(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void nodeRemoved(NodeChangeEvent<N> gce)
	{
		nodeList.remove(gce.getGraphNode());
	}

	/**
	 * Called when an Edge is added to a Graph to which this
	 * GraphAdditionMonitor is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void edgeAdded(EdgeChangeEvent<N, ET> gce)
	{
		edgeList.add(gce.getGraphEdge());
	}

	/**
	 * Called when an Edge is removed from a Graph to which this
	 * GraphAdditionMonitor is listening.
	 * 
	 * @see pcgen.base.graph.core.GraphChangeListener#nodeAdded(pcgen.base.graph.core.NodeChangeEvent)
	 */
	public void edgeRemoved(EdgeChangeEvent<N, ET> gce)
	{
		edgeList.remove(gce.getGraphEdge());
	}

	/**
	 * Returns a Set of the Edges added while this GraphAdditionMonitor was
	 * listening to a Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by GraphAdditionMonitor.
	 * However, the Edges contained in the List are returned BY REFERENCE, and
	 * modification of the returned Edges will modify the Edges contained within
	 * the underlying Graph.
	 */
	public Set<ET> getEdgeList()
	{
		return new HashSet<ET>(edgeList);
	}

	/**
	 * Returns a Set of the Nodes added while this GraphAdditionMonitor was
	 * listening to a Graph.
	 * 
	 * Ownership of the returned Set is transferred to the calling Object. No
	 * reference to the Set Object is maintained by GraphAdditionMonitor.
	 * However, the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the Nodes contained within
	 * the underlying Graph.
	 */
	public Set<N> getNodeList()
	{
		return new HashSet<N>(nodeList);
	}

	/**
	 * Clears the lists of added Nodes and Edges maintained by the
	 * GraphAdditionMonitor
	 */
	public void clear()
	{
		nodeList.clear();
		edgeList.clear();
	}
}