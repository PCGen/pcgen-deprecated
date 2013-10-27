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

import java.util.Collections;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DefaultDirectionalHyperEdge;
import pcgen.base.graph.core.SimpleListMapGraph;
import pcgen.base.graph.edit.InsertGraphEdge;

import junit.framework.TestCase;

public class InsertGraphEdgeTest extends TestCase {

	private SimpleListMapGraph graph;

	private DefaultDirectionalHyperEdge edge, innocentEdge;

	private UndoableEdit edit;

	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph();
		Integer node1 = new Integer(1);
		Integer node2 = new Integer(2);
		edge = new DefaultDirectionalHyperEdge(
				Collections.singletonList(node1), Collections
						.singletonList(node2));
		innocentEdge = new DefaultDirectionalHyperEdge(Collections
				.singletonList(node1), Collections.singletonList(node2));
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addEdge(edge);
		edit = new InsertGraphEdge(graph, edge, "edit");
	}

	public void testRedo() {
	}

	public void testUndoRedo() {
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

	public void testGetPresentationName() {
		assertEquals("edit", edit.getPresentationName());
		assertEquals("Insert Graph Edge",
				new InsertGraphEdge(graph, edge, null).getPresentationName());
	}

	public void testGetInsertGraphEdgeEditor() {
		try {
			new InsertGraphEdge(null, edge, "edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new InsertGraphEdge(graph, null, "edit");
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		new InsertGraphEdge(graph, edge, null);
	}
}
