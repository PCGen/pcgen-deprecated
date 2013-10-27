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

import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.GraphEdge;
import pcgen.base.graph.core.SimpleListMapGraph;
import pcgen.base.graph.edit.DeleteGraphEdge;

import junit.framework.TestCase;

public class DeleteGraphEdgeTest extends TestCase {

	private SimpleListMapGraph<Integer, GraphEdge<Integer>> graph;

	private GraphEdge<Integer> edge, innocentEdge, sideEffectEdge;

	private Integer node1, node2;

	private UndoableEdit edit;

	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph<Integer, GraphEdge<Integer>>();
		node1 = new Integer(1);
		node2 = new Integer(2);
		Integer node3 = new Integer(3);
		edge = new DefaultGraphEdge<Integer>(node1, node2);
		innocentEdge = new DefaultGraphEdge<Integer>(node3, node2);
		sideEffectEdge = new DefaultGraphEdge<Integer>(node1, node3);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addEdge(innocentEdge);
		graph.addEdge(sideEffectEdge);
		edit = new DeleteGraphEdge<Integer, GraphEdge<Integer>>(graph, edge,
				"edit");
	}

	public void testUndoRedo() {
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

	public void testGetPresentationName() {
		assertEquals("edit", edit.getPresentationName());
		assertEquals("Delete Graph Edge",
				new DeleteGraphEdge<Integer, GraphEdge<Integer>>(graph, edge,
						null).getPresentationName());
	}

	public void testGetDeleteGraphEdgeEditor() {
		try {
			new DeleteGraphEdge<Integer, GraphEdge<Integer>>(null, edge, "edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new DeleteGraphEdge<Integer, GraphEdge<Integer>>(graph, null,
					"edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		new DeleteGraphEdge<Integer, GraphEdge<Integer>>(graph, edge, null);
	}

}
