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

import pcgen.base.graph.command.InsertNodesCommand;
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
public class InsertNodesCommandTest extends TestCase {

	private SimpleListMapGraph graph;

	private Integer node1, node2, node3;

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
		node1 = new Integer(1);
		node2 = new Integer(2);
		node3 = new Integer(3);
		factory = new InsertNodesCommand("edit", graph, Arrays.asList(node1));
		factory2 = new InsertNodesCommand("edit", graph, Arrays.asList(node2,
				node3));
	}

	public void testInsertGraphNode() {
		// NEEDTEST Need to test primitive construction
	}

	public void testExecute() {
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		factory.execute();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
	}

	public void testBadExecute() {
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.addNode(node1));
		assertTrue(graph.containsNode(node1));
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testUndoRedo() {
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testUndoRedo2() {
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		UndoableEdit edit = factory2.execute();
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testSideEffectUndoRedo() {
		final Integer node3 = new Integer(3);
		graph.addGraphChangeListener(new GraphChangeListener() {
			public void nodeAdded(NodeChangeEvent gce) {
				graph.addNode(node3);
			}

			public void nodeRemoved(NodeChangeEvent gce) {
			}

			public void edgeAdded(EdgeChangeEvent gce) {
			}

			public void edgeRemoved(EdgeChangeEvent gce) {
			}
		});
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertFalse(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", factory.getPresentationName());
		assertEquals("edit", factory.execute().getPresentationName());
		assertEquals("Insert Graph Nodes", new InsertNodesCommand(null, graph,
				Arrays.asList(node1)).getPresentationName());
	}

	public void testGetInsertGraphEdgeEditor() {
		try {
			new InsertNodesCommand("edit", null, Arrays.asList(node1));
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new InsertNodesCommand("edit", graph, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new InsertNodesCommand("edit", graph, new ArrayList());
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new InsertNodesCommand(null, graph, Arrays.asList(node1));
	}

	public void testSerialization() {
		// NEEDTEST serialization
	}

}