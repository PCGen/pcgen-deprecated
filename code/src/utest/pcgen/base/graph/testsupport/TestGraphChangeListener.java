/*
 * Copyright (c) Thomas Parker, 2004, 2005.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.graph.testsupport;

import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.NodeChangeEvent;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestGraphChangeListener<T, ET extends Edge<T>> implements
		GraphChangeListener<T, ET> {

	public T lastAddNode;

	public ET lastAddEdge;

	public T lastRemoveNode;

	public ET lastRemoveEdge;

	public int nodeCount = 0;

	public int edgeCount = 0;

	/**
	 * 
	 */
	public TestGraphChangeListener() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rpgmapgen.util.graph.event.GraphChangeListener#nodeAdded(rpgmapgen.util.graph.event.GraphNodeChangeEvent)
	 */
	public void nodeAdded(NodeChangeEvent<T> gce) {
		lastAddNode = gce.getGraphNode();
		nodeCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rpgmapgen.util.graph.event.GraphChangeListener#nodeRemoved(rpgmapgen.util.graph.event.GraphNodeChangeEvent)
	 */
	public void nodeRemoved(NodeChangeEvent<T> gce) {
		lastRemoveNode = gce.getGraphNode();
		nodeCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rpgmapgen.util.graph.event.GraphChangeListener#edgeAdded(rpgmapgen.util.graph.event.GraphEdgeChangeEvent)
	 */
	public void edgeAdded(EdgeChangeEvent<T, ET> gce) {
		lastAddEdge = gce.getGraphEdge();
		edgeCount++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see rpgmapgen.util.graph.event.GraphChangeListener#edgeRemoved(rpgmapgen.util.graph.event.GraphEdgeChangeEvent)
	 */
	public void edgeRemoved(EdgeChangeEvent<T, ET> gce) {
		lastRemoveEdge = gce.getGraphEdge();
		edgeCount++;
	}
}