/*
 * Copyright (c) Thomas Parker, 2005.
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
package pcgen.base.graph.edit;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.GraphEdge;
import pcgen.base.graph.core.SimpleListMapGraph;
import pcgen.base.graph.edit.DeleteGraphNode;

import junit.framework.TestCase;

public class DeleteGraphNodeTest extends TestCase {

	private SimpleListMapGraph<Integer, GraphEdge<Integer>> graph;

	private Integer node1, node2;

	private UndoableEdit edit;

	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph<Integer, GraphEdge<Integer>>();
		node1 = new Integer(1);
		node2 = new Integer(2);
		graph.addNode(node2);
		edit = new DeleteGraphNode<Integer>(graph, node1, "edit");
	}

	public void testUndoRedo() {
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertFalse(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", edit.getPresentationName());
		assertEquals("Delete Graph Node", new DeleteGraphNode<Integer>(graph,
				node1, null).getPresentationName());
	}

	public void testInsertGraphNode() {
		try {
			new DeleteGraphNode<Integer>(null, node1, "edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new DeleteGraphNode<Integer>(graph, null, "edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		// Legal w/o name
		new DeleteGraphNode<Integer>(graph, node1, null);
	}
}
