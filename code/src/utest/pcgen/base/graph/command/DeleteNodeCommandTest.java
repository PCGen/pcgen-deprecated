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

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.command.DeleteNodeCommand;
import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
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
public class DeleteNodeCommandTest extends TestCase {

	private SimpleListMapGraph graph;

	private Integer node, node2, node3, innocentNode;

	private DefaultGraphEdge edge;

	private Command factory, factory2;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph();
		node = new Integer(1);
		node2 = new Integer(2);
		node3 = new Integer(3);
		innocentNode = new Integer(4);
		graph.addNode(node);
		graph.addNode(node2);
		graph.addNode(node3);
		edge = new DefaultGraphEdge(node2, node3);
		graph.addEdge(edge);
		graph.addNode(innocentNode);
		factory = new DeleteNodeCommand("edit", graph, node);
		factory2 = new DeleteNodeCommand("edit", graph, node2);
	}

	public void testDeleteGraphNode() {
		// NEEDTEST Need to test primitive construction
	}

	public void testExecute() {
		assertTrue(graph.containsNode(node));
		assertTrue(graph.containsNode(innocentNode));
		factory.execute();
		assertFalse(graph.containsNode(node));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertTrue(graph.containsEdge(edge));
		// Now test where the Node will also implicitly delete an edge
		factory2.execute();
		assertFalse(graph.containsNode(node));
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(graph.containsEdge(edge));
	}

	public void testBadExecute() {
		try {
			new DeleteNodeCommand("edit", graph, new Integer(-1)).execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testSimpleUndoRedo() {
		assertTrue(graph.containsNode(node));
		assertTrue(graph.containsNode(innocentNode));
		UndoableEdit edit = factory.execute();
		assertFalse(graph.containsNode(node));
		assertTrue(graph.containsNode(innocentNode));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertFalse(graph.containsNode(node));
		assertTrue(graph.containsNode(innocentNode));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testSideEffectUndoRedo() {
		graph.addGraphChangeListener(new GraphChangeListener() {
			public void nodeAdded(NodeChangeEvent gce) {
			}

			public void nodeRemoved(NodeChangeEvent gce) {
				graph.removeNode(node);
			}

			public void edgeAdded(EdgeChangeEvent gce) {
			}

			public void edgeRemoved(EdgeChangeEvent gce) {
			}
		});
		assertTrue(graph.containsNode(node));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertTrue(graph.containsEdge(edge));
		UndoableEdit edit2 = factory2.execute();
		assertFalse(graph.containsNode(node));
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(graph.containsEdge(edge));
		assertTrue(edit2.canUndo());
		assertFalse(edit2.canRedo());
		edit2.undo();
		assertTrue(graph.containsNode(node));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		// this is the significant part!
		assertTrue(graph.containsEdge(edge));
		assertFalse(edit2.canUndo());
		assertTrue(edit2.canRedo());
		edit2.redo();
		assertFalse(graph.containsNode(node));
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(graph.containsEdge(edge));
		assertTrue(edit2.canUndo());
		assertFalse(edit2.canRedo());
	}

	public void testComplexUndoRedo() {
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertTrue(graph.containsEdge(edge));
		UndoableEdit edit2 = factory2.execute();
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(graph.containsEdge(edge));
		assertTrue(edit2.canUndo());
		assertFalse(edit2.canRedo());
		edit2.undo();
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		// this is the significant part!
		assertTrue(graph.containsEdge(edge));
		assertFalse(edit2.canUndo());
		assertTrue(edit2.canRedo());
		edit2.redo();
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(innocentNode));
		assertFalse(graph.containsEdge(edge));
		assertTrue(edit2.canUndo());
		assertFalse(edit2.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", factory.getPresentationName());
		assertEquals("edit", factory.execute().getPresentationName());
		assertEquals("Delete Graph Node", new DeleteNodeCommand(null, graph,
				node).getPresentationName());
	}

	public void testGetDeleteGraphNodeEditor() {
		try {
			new DeleteNodeCommand("edit", null, node);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new DeleteNodeCommand("edit", graph, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new DeleteNodeCommand(null, graph, node);
	}

	public void testSerialization() {
		// NEEDTEST serialization
	}

}