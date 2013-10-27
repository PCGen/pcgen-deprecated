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

import pcgen.base.graph.command.DeleteEdgesCommand;
import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.DefaultHyperEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.GraphEdge;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.core.SimpleListMapGraph;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.lang.Command;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeleteEdgesCommandTest extends TestCase {

	private SimpleListMapGraph graph;

	private GraphEdge<Integer> edge, innocentEdge, sideEffectEdge;

	private Integer node1, node2;

	private Command factory;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph();
		node1 = new Integer(1);
		node2 = new Integer(2);
		Integer node3 = new Integer(3);
		edge = new DefaultGraphEdge(node1, node2);
		innocentEdge = new DefaultGraphEdge(node3, node2);
		sideEffectEdge = new DefaultGraphEdge(node1, node2);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addEdge(edge);
		graph.addEdge(innocentEdge);
		graph.addEdge(sideEffectEdge);
		factory = new DeleteEdgesCommand("edit", graph, Arrays.asList(edge));
	}

	public void testExecute() {
		assertTrue(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		factory.execute();
		assertFalse(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		Edge hedge = new DefaultHyperEdge(Arrays.asList(edge.getNodeAt(0)));
		graph.addEdge(hedge);
		assertTrue(graph.containsEdge(hedge));
		assertTrue(graph.containsEdge(innocentEdge));
		Command factory2 = new DeleteEdgesCommand("edit", graph, Arrays
				.asList(hedge));
		factory2.execute();
		assertFalse(graph.containsEdge(hedge));
		assertTrue(graph.containsEdge(innocentEdge));
	}

	public void testBadExecute() {
		try {
			Edge hedge = new DefaultHyperEdge(Arrays.asList(edge.getNodeAt(0)));
			new DeleteEdgesCommand("edit", graph, Arrays.asList(hedge))
					.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testUndoRedo() {
		assertTrue(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		UndoableEdit edit = factory.execute();
		assertFalse(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertFalse(graph.containsEdge(edge));
		assertTrue(graph.containsEdge(innocentEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testSideEffectUndoRedo() {
		graph.addGraphChangeListener(new GraphChangeListener() {
			public void nodeAdded(NodeChangeEvent gce) {
			}

			public void nodeRemoved(NodeChangeEvent gce) {
			}

			public void edgeAdded(EdgeChangeEvent gce) {
			}

			public void edgeRemoved(EdgeChangeEvent gce) {
				graph.removeNode(node1);
			}
		});
		assertTrue(graph.containsEdge(edge));
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsEdge(innocentEdge));
		assertTrue(graph.containsEdge(sideEffectEdge));
		UndoableEdit edit = factory.execute();
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsEdge(innocentEdge));
		assertFalse(graph.containsEdge(sideEffectEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsEdge(edge));
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsEdge(innocentEdge));
		assertTrue(graph.containsEdge(sideEffectEdge));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertFalse(graph.containsEdge(edge));
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsEdge(innocentEdge));
		assertFalse(graph.containsEdge(sideEffectEdge));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", factory.getPresentationName());
		assertEquals("edit", factory.execute().getPresentationName());
		assertEquals("Delete Graph Edges", new DeleteEdgesCommand(null, graph,
				Arrays.asList(edge)).getPresentationName());
	}

	public void testGetDeleteGraphEdgeEditor() {
		try {
			new DeleteEdgesCommand("edit", null, Arrays.asList(edge));
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new DeleteEdgesCommand("edit", graph, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new DeleteEdgesCommand("edit", graph, new ArrayList());
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new DeleteEdgesCommand(null, graph, Arrays.asList(edge));
	}

}