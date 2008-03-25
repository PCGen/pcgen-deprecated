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

import pcgen.base.graph.command.GraftFromNodeCommand;
import pcgen.base.graph.core.DefaultDirectionalGraphEdge;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.EdgeChangeEvent;
import pcgen.base.graph.core.GraphChangeListener;
import pcgen.base.graph.core.NodeChangeEvent;
import pcgen.base.graph.core.UnsupportedGraphOperationException;
import pcgen.base.lang.Command;
import pcgen.base.graph.testsupport.ComplexCommandTestCase;

public class GraftFromNodeCommandTest extends ComplexCommandTestCase {
	private Command factory;

	private DirectionalListMapGraph<Integer, DefaultDirectionalGraphEdge<Integer>> destination;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		destination = new DirectionalListMapGraph<Integer, DefaultDirectionalGraphEdge<Integer>>();
		destination.addNode(node1);

		factory = new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
				"GraftCmd", graph, node2, destination);
	}

	public void testGetPresentationName() {
		assertEquals("GraftCmd", factory.getPresentationName());
		assertEquals("GraftCmd", factory.execute().getPresentationName());
		assertEquals(
				"Graft From Node",
				new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
						null, graph, node2, destination).getPresentationName());
	}

	public void testConstructor() {
		try {
			new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
					"edit", null, node2, destination);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
					"edit", graph, node2, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
					"edit", graph, null, destination);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new GraftFromNodeCommand<Integer, DefaultDirectionalGraphEdge<Integer>>(
				null, graph, node2, destination);
	}

	public void testGraft() {
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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));

		factory.execute();

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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
	}

	public void testUndoRedo() {
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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));

		UndoableEdit edit = factory.execute();

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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));

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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));

		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();

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
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertTrue(graph.containsEdge(edge6));
		assertTrue(graph.containsEdge(edge7));

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
	}

	public void testGraftingLoop() {
		DefaultDirectionalGraphEdge<Integer> loopEdge = new DefaultDirectionalGraphEdge<Integer>(
				node6, node4);
		graph.addEdge(loopEdge);

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertFalse(destination.containsEdge(loopEdge));

		UndoableEdit edit = factory.execute();

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertTrue(destination.containsEdge(loopEdge));

		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertFalse(destination.containsEdge(loopEdge));

		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertTrue(destination.containsEdge(loopEdge));

		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testSideEffectUndoRedo() {
		destination.addGraphChangeListener(new GraphChangeListener() {
			public void nodeAdded(NodeChangeEvent gce) {
				destination.addNode(sideEffectNode);
			}

			public void nodeRemoved(NodeChangeEvent gce) {
			}

			public void edgeAdded(EdgeChangeEvent gce) {
			}

			public void edgeRemoved(EdgeChangeEvent gce) {
			}
		});
		DefaultDirectionalGraphEdge<Integer> loopEdge = new DefaultDirectionalGraphEdge<Integer>(
				node6, node4);
		graph.addEdge(loopEdge);

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsNode(sideEffectNode));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertFalse(destination.containsEdge(loopEdge));

		UndoableEdit edit = factory.execute();

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertTrue(destination.containsNode(sideEffectNode));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertTrue(destination.containsEdge(loopEdge));

		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();

		assertTrue(destination.containsNode(node1));
		assertFalse(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertFalse(destination.containsNode(node4));
		assertFalse(destination.containsNode(node5));
		assertFalse(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertFalse(destination.containsNode(sideEffectNode));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertFalse(destination.containsEdge(edge3));
		assertFalse(destination.containsEdge(edge4));
		assertFalse(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertFalse(destination.containsEdge(loopEdge));

		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();

		assertTrue(destination.containsNode(node1));
		assertTrue(destination.containsNode(node2));
		assertFalse(destination.containsNode(node3));
		assertTrue(destination.containsNode(node4));
		assertTrue(destination.containsNode(node5));
		assertTrue(destination.containsNode(node6));
		assertFalse(destination.containsNode(node7));
		assertFalse(destination.containsNode(node8));
		assertFalse(destination.containsNode(node9));
		assertTrue(destination.containsNode(sideEffectNode));
		assertFalse(destination.containsEdge(edge1));
		assertFalse(destination.containsEdge(edge2));
		assertTrue(destination.containsEdge(edge3));
		assertTrue(destination.containsEdge(edge4));
		assertTrue(destination.containsEdge(edge5));
		assertFalse(destination.containsEdge(edge6));
		assertFalse(destination.containsEdge(edge7));
		assertTrue(destination.containsEdge(loopEdge));

		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testNodeNotPresent() {
		graph.removeNode(node2);
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}
}
