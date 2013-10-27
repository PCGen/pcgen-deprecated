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
package pcgen.base.graph.monitor;

import java.util.Set;

import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.core.SimpleListGraph;
import pcgen.base.graph.monitor.GraphAdditionMonitor;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphAdditionMonitorTest extends TestCase {

	GraphAdditionMonitor gam;

	Integer gn1, gn2, gn3, gn4;

	DefaultGraphEdge ge1, ge2, ge3, ge4;

	Graph graph = new SimpleListGraph();

	@Override
	protected void setUp() throws Exception {
		gam = new GraphAdditionMonitor();
		gn1 = new Integer(1);
		gn2 = new Integer(2);
		gn3 = new Integer(3);
		gn4 = new Integer(4);
		ge1 = new DefaultGraphEdge(gn1, gn2);
		ge2 = new DefaultGraphEdge(gn2, gn3);
		ge3 = new DefaultGraphEdge(gn3, gn4);
		ge4 = new DefaultGraphEdge(gn4, gn1);
	}

	public void testGraphAdditionMonitor() {
	}

	public void testNodeAdded() {
		assertEquals(0, gam.getEdgeList().size());
		assertEquals(0, gam.getNodeList().size());
		gam.nodeAdded(new NodeChangeEvent(graph, gn1,
				NodeChangeEvent.NODE_ADDED));
		assertEquals(1, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		gam.nodeAdded(new NodeChangeEvent(graph, gn2,
				NodeChangeEvent.NODE_ADDED));
		assertEquals(2, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		assertTrue(gam.getNodeList().contains(gn2));
		// harmless to add a second time
		gam.nodeAdded(new NodeChangeEvent(graph, gn1,
				NodeChangeEvent.NODE_ADDED));
		assertEquals(2, gam.getNodeList().size());
		gam.nodeRemoved(new NodeChangeEvent(graph, gn2,
				NodeChangeEvent.NODE_REMOVED));
		assertEquals(1, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		// harmless to remove when not present
		gam.nodeRemoved(new NodeChangeEvent(graph, gn2,
				NodeChangeEvent.NODE_REMOVED));
		assertEquals(1, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
	}

	public void testEdgeAddedRemoved() {
		assertEquals(0, gam.getEdgeList().size());
		assertEquals(0, gam.getNodeList().size());
		gam.edgeAdded(new EdgeChangeEvent(graph, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(1, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		gam.edgeAdded(new EdgeChangeEvent(graph, ge2,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(2, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		assertTrue(gam.getEdgeList().contains(ge2));
		// harmless to add a second time
		gam.edgeAdded(new EdgeChangeEvent(graph, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(2, gam.getEdgeList().size());
		gam.edgeRemoved(new EdgeChangeEvent(graph, ge2,
				EdgeChangeEvent.EDGE_REMOVED));
		assertEquals(1, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		// harmless to remove when not present
		gam.edgeRemoved(new EdgeChangeEvent(graph, ge2,
				EdgeChangeEvent.EDGE_REMOVED));
		assertEquals(1, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
	}

	public void testGetEdgeList() {
		assertEquals(0, gam.getEdgeList().size());
		assertEquals(0, gam.getNodeList().size());
		gam.edgeAdded(new EdgeChangeEvent(graph, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(1, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		gam.edgeAdded(new EdgeChangeEvent(graph, ge2,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(2, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		assertTrue(gam.getEdgeList().contains(ge2));
		// Ensure ownership of Set is transferred
		Set<Edge> s = gam.getEdgeList();
		s.remove(ge1);
		assertEquals(2, gam.getEdgeList().size());
		assertTrue(gam.getEdgeList().contains(ge1));
		assertTrue(gam.getEdgeList().contains(ge2));
		assertFalse(s.contains(ge1));
	}

	public void testGetNodeList() {
		assertEquals(0, gam.getEdgeList().size());
		assertEquals(0, gam.getNodeList().size());
		gam.nodeAdded(new NodeChangeEvent(graph, gn1,
				NodeChangeEvent.NODE_ADDED));
		assertEquals(1, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		gam.nodeAdded(new NodeChangeEvent(graph, gn2,
				NodeChangeEvent.NODE_ADDED));
		assertEquals(2, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		assertTrue(gam.getNodeList().contains(gn2));
		// Ensure ownership of Set is transferred
		Set<Integer> s = gam.getNodeList();
		s.remove(gn1);
		assertEquals(2, gam.getNodeList().size());
		assertTrue(gam.getNodeList().contains(gn1));
		assertTrue(gam.getNodeList().contains(gn2));
		assertFalse(s.contains(gn1));
	}

	public void testClear() {
		assertEquals(0, gam.getEdgeList().size());
		assertEquals(0, gam.getNodeList().size());
		gam.nodeAdded(new NodeChangeEvent(graph, gn1,
				NodeChangeEvent.NODE_ADDED));
		gam.nodeAdded(new NodeChangeEvent(graph, gn2,
				NodeChangeEvent.NODE_ADDED));
		gam.edgeAdded(new EdgeChangeEvent(graph, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		gam.edgeAdded(new EdgeChangeEvent(graph, ge2,
				EdgeChangeEvent.EDGE_ADDED));
		assertEquals(2, gam.getNodeList().size());
		assertEquals(2, gam.getEdgeList().size());
		gam.clear();
		assertEquals(0, gam.getNodeList().size());
		assertEquals(0, gam.getEdgeList().size());
	}

}