/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.base.graph.command;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DefaultDirectionalGraphEdge;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.graph.testsupport.ComplexCommandTestCase;
import pcgen.base.lang.Command;

public class PruneUnconnectedToNodeCommandTest extends ComplexCommandTestCase {

	private Command factory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = new PruneUnconnectedToNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
				"PruneCmd", graph, node1);
	}


	public void testGetPresentationName() {
		assertEquals("PruneCmd", factory.getPresentationName());
		assertEquals("PruneCmd", factory.execute().getPresentationName());
		assertEquals(
				"Prune unconnected to Node",
				new PruneUnconnectedToNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
						null, graph, node1).getPresentationName());
	}
	
	public void testConstructor() {
		try {
			new PruneUnconnectedToNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
					"edit", null, node1);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new PruneUnconnectedToNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
					"edit", graph, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new PruneUnconnectedToNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
				null, graph, node1);
	}

	public void testEdgeNotPresent() {
		graph.removeNode(node1);
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testRemoveNothing() {
		graph.removeNode(node9);
		graph.removeEdge(sideEffectEdge);
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertTrue(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertEquals(8, graph.getNodeList().size());
		assertTrue(graph.containsEdge(edge1));
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));
		assertEquals(7, graph.getEdgeList().size());
		factory.execute();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertTrue(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertEquals(8, graph.getNodeList().size());
		assertTrue(graph.containsEdge(edge1));
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));
		assertEquals(7, graph.getEdgeList().size());
	}
	
	public void testRemoveNormal() {
		graph.removeEdge(edge2);
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertTrue(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertTrue(graph.containsNode(node9));
		assertTrue(graph.containsEdge(edge1));
		assertFalse(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertFalse(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertFalse(graph.containsNode(node9));
		assertTrue(graph.containsEdge(edge1));
		assertFalse(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertFalse(graph.containsEdge(edge6));
		assertFalse(graph.containsEdge(edge7));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertTrue(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertTrue(graph.containsNode(node9));
		assertTrue(graph.containsEdge(edge1));
		assertFalse(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertFalse(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsNode(node6));
		assertFalse(graph.containsNode(node7));
		assertTrue(graph.containsNode(node8));
		assertFalse(graph.containsNode(node9));
		assertTrue(graph.containsEdge(edge1));
		assertFalse(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertFalse(graph.containsEdge(edge6));
		assertFalse(graph.containsEdge(edge7));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}
}
