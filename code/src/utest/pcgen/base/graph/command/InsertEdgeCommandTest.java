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
package pcgen.base.graph.command;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.command.InsertEdgeCommand;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.core.SimpleListMapGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.lang.Command;
import pcgen.base.graph.core.TestDirectionalEdge;
import pcgen.base.graph.core.TestDirectionalHyperEdge;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InsertEdgeCommandTest extends TestCase {

	private SimpleListMapGraph graph;

	private TestDirectionalEdge<Integer> edge, edge2, edge3, edge4,
			innocentEdge;

	private Integer node3, node4, node5;

	private Command factory, factory2, factory3, factory4;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph();
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		node3 = new Integer(3);
		node4 = new Integer(4);
		node5 = new Integer(5);
		edge = new TestDirectionalEdge(node1, node2);
		innocentEdge = new TestDirectionalEdge(node1, node2);
		graph.addNode(node1);
		graph.addNode(node2);
		edge2 = new TestDirectionalEdge(node1, node3);
		edge3 = new TestDirectionalEdge(node3, node2);
		edge4 = new TestDirectionalEdge(node5, node4);
		factory = new InsertEdgeCommand("edit", graph, edge);
		factory2 = new InsertEdgeCommand("edit", graph, edge2);
		factory3 = new InsertEdgeCommand("edit", graph, edge3);
		factory4 = new InsertEdgeCommand("edit", graph, edge4);
	}

	public void testExecute() {
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		factory.execute();
		assertTrue(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		Edge hedge = new TestDirectionalHyperEdge(edge.getNodeAt(0),
				new Integer[] {});
		assertFalse(graph.containsEdge(hedge));
		assertFalse(graph.containsEdge(innocentEdge));
		Command fac = new InsertEdgeCommand("edit", graph, hedge);
		fac.execute();
		assertTrue(graph.containsEdge(hedge));
		assertFalse(graph.containsEdge(innocentEdge));
	}

	public void testBadExecute() {
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(graph.addEdge(edge));
		assertTrue(graph.containsEdge(edge));
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testSideEffectUndoRedo() {
		graph.addNode(node5);
		graph.addGraphChangeListener(new GraphChangeListener() {
			public void nodeAdded(NodeChangeEvent gce) {
			}

			public void nodeRemoved(NodeChangeEvent gce) {
			}

			public void edgeAdded(EdgeChangeEvent gce) {
				graph.removeNode(node5);
			}

			public void edgeRemoved(EdgeChangeEvent gce) {
			}
		});
		assertFalse(graph.containsEdge(edge));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsEdge(edge));
		assertFalse(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsEdge(edge));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsEdge(edge));
		assertFalse(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testSimpleUndoRedo() {
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsEdge(edge));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testComplexUndoRedo2() {
		assertFalse(graph.containsEdge(edge2));
		assertFalse(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory2.execute();
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.getNodeList().contains(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsEdge(edge2));
		assertFalse(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testComplexUndoRedo3() {
		assertFalse(graph.containsEdge(edge3));
		assertFalse(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory3.execute();
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsEdge(edge3));
		assertFalse(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsNode(node3));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testComplexUndoRedo4() {
		assertFalse(graph.containsEdge(edge4));
		assertFalse(graph.containsNode(node4));
		assertFalse(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory4.execute();
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsEdge(edge4));
		assertFalse(graph.containsNode(node4));
		assertFalse(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", factory.getPresentationName());
		assertEquals("edit", factory.execute().getPresentationName());
		assertEquals("Insert Graph Edge", new InsertEdgeCommand(null, graph,
				edge).getPresentationName());
	}

	public void testGetInsertGraphEdgeEditor() {
		try {
			new InsertEdgeCommand("edit", null, edge);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new InsertEdgeCommand("edit", graph, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new InsertEdgeCommand(null, graph, edge);
	}

	public void testSerialization() {
		// NEEDTEST serialization
	}

}