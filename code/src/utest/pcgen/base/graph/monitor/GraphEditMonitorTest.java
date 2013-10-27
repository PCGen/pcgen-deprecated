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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.undo.CompoundEdit;

import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.core.SimpleListGraph;
import pcgen.base.graph.edit.DeleteGraphEdge;
import pcgen.base.graph.edit.DeleteGraphNode;
import pcgen.base.graph.edit.InsertGraphEdge;
import pcgen.base.graph.edit.InsertGraphNode;
import pcgen.base.graph.monitor.GraphEditMonitor;
import pcgen.base.graph.monitor.GraphMismatchException;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphEditMonitorTest extends TestCase {

	DirectionalListMapGraph graph1;

	GraphEditMonitor gem1;

	Integer gn1, gn2, gn3, gn4;

	DefaultGraphEdge ge1, ge2, ge3, ge4;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph1 = new DirectionalListMapGraph();
		gem1 = GraphEditMonitor.getGraphEditMonitor(graph1);
		gn1 = new Integer(1);
		gn2 = new Integer(2);
		gn3 = new Integer(3);
		gn4 = new Integer(4);
		ge1 = new DefaultGraphEdge(gn1, gn2);
		ge2 = new DefaultGraphEdge(gn2, gn3);
		ge3 = new DefaultGraphEdge(gn3, gn4);
		ge4 = new DefaultGraphEdge(gn4, gn1);
	}

	public void testGraphEditMonitor() {
		try {
			GraphEditMonitor<Object, Edge<Object>> gem = GraphEditMonitor
					.getGraphEditMonitor(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK;
		}
	}

	public void testNodeAdded() {
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
		gem1.nodeAdded(new NodeChangeEvent(graph1, gn1,
				NodeChangeEvent.NODE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphNode);
		// harmless to add a second time
		gem1.nodeAdded(new NodeChangeEvent(graph1, gn1,
				NodeChangeEvent.NODE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphNode);
		// another node means compound edit
		gem1.nodeAdded(new NodeChangeEvent(graph1, gn2,
				NodeChangeEvent.NODE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof CompoundEdit);
		// and reverses
		gem1.nodeRemoved(new NodeChangeEvent(graph1, gn2,
				NodeChangeEvent.NODE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphNode);
		try {
			gem1.nodeRemoved(new NodeChangeEvent(new SimpleListGraph(), gn2,
					NodeChangeEvent.NODE_REMOVED));
			fail();
		} catch (GraphMismatchException e) {
			// OK
		}
	}

	public void testNodeRemoved() {
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
		gem1.nodeRemoved(new NodeChangeEvent(graph1, gn1,
				NodeChangeEvent.NODE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphNode);
		// harmless to add a second time
		gem1.nodeRemoved(new NodeChangeEvent(graph1, gn1,
				NodeChangeEvent.NODE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphNode);
		// another node means compound edit
		gem1.nodeRemoved(new NodeChangeEvent(graph1, gn2,
				NodeChangeEvent.NODE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof CompoundEdit);
		// and reverses
		gem1.nodeAdded(new NodeChangeEvent(graph1, gn2,
				NodeChangeEvent.NODE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphNode);
		try {
			gem1.nodeAdded(new NodeChangeEvent(new SimpleListGraph(), gn2,
					NodeChangeEvent.NODE_ADDED));
			fail();
		} catch (GraphMismatchException e) {
			// OK
		}
	}

	public void testEdgeAdded() {
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
		gem1.edgeAdded(new EdgeChangeEvent(graph1, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphEdge);
		// harmless to add a second time
		gem1.edgeAdded(new EdgeChangeEvent(graph1, ge1,
				EdgeChangeEvent.EDGE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphEdge);
		// another node means compound edit
		gem1.edgeAdded(new EdgeChangeEvent(graph1, ge2,
				EdgeChangeEvent.EDGE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof CompoundEdit);
		// and reverses
		gem1.edgeRemoved(new EdgeChangeEvent(graph1, ge2,
				EdgeChangeEvent.EDGE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof InsertGraphEdge);
		try {
			gem1.edgeRemoved(new EdgeChangeEvent(new SimpleListGraph(), ge2,
					EdgeChangeEvent.EDGE_REMOVED));
			fail();
		} catch (GraphMismatchException e) {
			// OK
		}
	}

	public void testEdgeRemoved() {
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
		gem1.edgeRemoved(new EdgeChangeEvent(graph1, ge1,
				EdgeChangeEvent.EDGE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphEdge);
		// harmless to add a second time
		gem1.edgeRemoved(new EdgeChangeEvent(graph1, ge1,
				EdgeChangeEvent.EDGE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphEdge);
		// another node means compound edit
		gem1.edgeRemoved(new EdgeChangeEvent(graph1, ge2,
				EdgeChangeEvent.EDGE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof CompoundEdit);
		// and reverses
		gem1.edgeAdded(new EdgeChangeEvent(graph1, ge2,
				EdgeChangeEvent.EDGE_ADDED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphEdge);
		try {
			gem1.edgeAdded(new EdgeChangeEvent(new SimpleListGraph(), ge2,
					EdgeChangeEvent.EDGE_ADDED));
			fail();
		} catch (GraphMismatchException e) {
			// OK
		}
	}

	public void testClear() {
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
		gem1.edgeRemoved(new EdgeChangeEvent(graph1, ge1,
				EdgeChangeEvent.EDGE_REMOVED));
		assertTrue(gem1.getEdit("gem1") instanceof DeleteGraphEdge);
		gem1.clear();
		assertTrue(gem1.getEdit("gem1").getClass().toString().equals(
				"class javax.swing.undo.AbstractUndoableEdit"));
	}

	public void testGetEdit() {
		assertNotNull(gem1.getEdit(null).getPresentationName());
	}

	public void testGetEditFor() {
		try {
			Method method = GraphEditMonitor.class.getDeclaredMethod(
					"getEditFor", new Class[] { Object.class, String.class });
			method.setAccessible(true);
			method.invoke(gem1, new Object[] { new Object(), "Edit Name" });
			fail();
		} catch (NoSuchMethodException e) {
			fail(e.toString());
		} catch (SecurityException e) {
			fail(e.toString());
		} catch (IllegalAccessException e) {
			fail(e.toString());
		} catch (IllegalArgumentException e) {
			fail(e.toString());
		} catch (InvocationTargetException e) {
			assert (e.getCause() instanceof NullPointerException);
		}
	}
}