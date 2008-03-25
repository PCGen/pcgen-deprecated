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

import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.GraphChangeSupport;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.SimpleListGraph;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TransparentTestStorageStrategy<N, ET extends Edge<N>>
		implements Graph<N, ET> {

	/**
	 * 
	 */
	public TransparentTestStorageStrategy() {
		super();
		gcs = new GraphChangeSupport<N, ET>(new SimpleListGraph<N, ET>());
	}

	public N addNode;

	public boolean addNodeReturn;

	public boolean addNode(N v) {
		addNode = v;
		return addNodeReturn;
	}

	public ET addEdge;

	public boolean addEdgeReturn;

	public boolean addEdge(ET e) {
		addEdge = e;
		return addEdgeReturn;
	}

	public boolean containsNodeReturn;

	public Map<N, Boolean> containsNodeMap = null;

	public boolean containsNode(Object v) {
		if (containsNodeMap == null) {
			return containsNodeReturn;
		}
		return containsNodeMap.get(v).booleanValue();
	}

	public boolean containsEdgeReturn;

	public boolean containsEdge(Edge<?> e) {
		return containsEdgeReturn;
	}

	public List<N> getNodeListReturn;

	public List<N> getNodeList() {
		return getNodeListReturn;
	}

	public List<ET> getEdgeListReturn;

	public List<ET> getEdgeList() {
		return getEdgeListReturn;
	}

	public boolean removeNode(N gn) {
		return false;
	}

	public boolean removeEdgeReturn;

	public boolean removeEdge(ET ge) {
		return removeEdgeReturn;
	}

	public Set<ET> getAdjacentEdgeListReturn;

	public N getAdjacentEdgeList;

	public Set<ET> getAdjacentEdges(N v) {
		getAdjacentEdgeList = v;
		return getAdjacentEdgeListReturn;
	}

	private final GraphChangeSupport<N, ET> gcs;

	/**
	 * @param arg0
	 */
	public void addGraphChangeListener(GraphChangeListener<N, ET> arg0) {
		gcs.addGraphChangeListener(arg0);
	}

	/**
	 */
	public GraphChangeListener<N, ET>[] getGraphChangeListeners() {
		return gcs.getGraphChangeListeners();
	}

	/**
	 * @param arg0
	 */
	public void removeGraphChangeListener(GraphChangeListener<N, ET> arg0) {
		gcs.removeGraphChangeListener(arg0);
	}

	public boolean isEmpty()
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
	}

	public int getNodeCount()
	{
		return 0;
	}

}