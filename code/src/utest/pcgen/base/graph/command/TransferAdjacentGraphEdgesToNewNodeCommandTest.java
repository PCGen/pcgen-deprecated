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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.UndoableEdit;

import pcgen.base.graph.core.DefaultDirectionalHyperEdge;
import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.DirectionalHyperEdge;
import pcgen.base.graph.core.GraphEdge;
import pcgen.base.graph.core.ReferencedEdge;
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
public class TransferAdjacentGraphEdgesToNewNodeCommandTest extends TestCase {

	private SimpleListMapGraph<Integer, DirectionalHyperEdge<Integer>> graph;

	private Integer node1, node2, node3, node4, node5;

	private DirectionalHyperEdge<Integer> edge1, edge2, edge3, edge4, edge5;

	private Command factory, factory2;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph = new SimpleListMapGraph<Integer, DirectionalHyperEdge<Integer>>();
		node1 = new Integer(1);
		node2 = new Integer(2);
		node3 = new Integer(3);
		node4 = new Integer(4);
		node5 = new Integer(5);
		edge1 = new DefaultDirectionalHyperEdge<Integer>(Arrays.asList(node1),
				Arrays.asList(node4));
		edge2 = new DefaultDirectionalHyperEdge<Integer>(Arrays.asList(node4),
				Arrays.asList(node5));
		edge3 = new DefaultDirectionalHyperEdge<Integer>(Arrays.asList(node5),
				Arrays.asList(node3));
		edge4 = new DefaultDirectionalHyperEdge<Integer>(Arrays.asList(node5),
				Arrays.asList(node4));
		edge5 = new DefaultDirectionalHyperEdge<Integer>(Arrays.asList(node4),
				Arrays.asList(node4));
		assertTrue(graph.addNode(node1));
		assertTrue(graph.addNode(node2));
		assertTrue(graph.addNode(node3));
		assertTrue(graph.addNode(node4));
		assertTrue(graph.addNode(node5));
		assertTrue(graph.addEdge(edge1));
		assertTrue(graph.addEdge(edge2));
		assertTrue(graph.addEdge(edge3));
		assertTrue(graph.addEdge(edge4));
		assertTrue(graph.addEdge(edge5));
		factory = new TransferEdgesCommand<Integer, DirectionalHyperEdge<Integer>>(
				"edit", graph, node1, node2);
		factory2 = new TransferEdgesCommand<Integer, DirectionalHyperEdge<Integer>>(
				"edit", graph, node4, node2);
	}

	public void testRelpaceGraphNode() {
		// ASSUME this is implicitly tested in testGetReplaceGraphNodeEditor
	}

	public void testMissingSource() {
		graph.removeNode(node1);
		// what happens if old node not in graph
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testMissingDestination() {
		graph.removeNode(node2);
		// what happens if new node not in graph
		try {
			factory.execute();
			fail();
		} catch (UnsupportedGraphOperationException e) {
			// OK
		}
	}

	public void testExecute() {
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsEdge(edge1));
		factory.execute();
		assertTrue(graph.containsNode(node1));
		assertFalse(graph.containsEdge(edge1));
		assertEquals(1, graph.getAdjacentEdges(node2).size());
		DirectionalHyperEdge e = (DirectionalHyperEdge) graph.getAdjacentEdges(
				node2).toArray()[0];
		assertEquals(node2, e.getNodeAt(0));
		assertEquals(node4, e.getNodeAt(1));
	}

	public void testSimpleUndoRedo() {
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsEdge(edge1));
		UndoableEdit edit = factory.execute();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertFalse(graph.containsEdge(edge1));
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(1, graph.getAdjacentEdges(node2).size());
		DirectionalHyperEdge e = (DirectionalHyperEdge) graph.getAdjacentEdges(
				node2).toArray()[0];
		assertEquals(node2, e.getNodeAt(0));
		assertEquals(node4, e.getNodeAt(1));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertEquals(1, graph.getAdjacentEdges(node1).size());
		assertEquals(0, graph.getAdjacentEdges(node2).size());
		e = (DirectionalHyperEdge) graph.getAdjacentEdges(node1).toArray()[0];
		assertEquals(node1, e.getNodeAt(0));
		assertEquals(node4, e.getNodeAt(1));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node2));
		assertFalse(graph.containsEdge(edge1));
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(1, graph.getAdjacentEdges(node2).size());
		e = (DirectionalHyperEdge) graph.getAdjacentEdges(node2).toArray()[0];
		assertEquals(node2, e.getNodeAt(0));
		assertEquals(node4, e.getNodeAt(1));
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testOverlapUndoRedo() {
		// ensure that if only some of the nodes on newNode are transferred in,
		// only those nodes are transferred back out on undo
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsEdge(edge1));
		Command c = new TransferEdgesCommand<Integer, DirectionalHyperEdge<Integer>>(
				"edit", graph, node1, node5);
		UndoableEdit edit = c.execute();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(edge1));
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(4, graph.getAdjacentEdges(node5).size());
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node5));
		assertEquals(1, graph.getAdjacentEdges(node1).size());
		assertEquals(3, graph.getAdjacentEdges(node5).size());
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node1));
		assertTrue(graph.containsNode(node5));
		assertFalse(graph.containsEdge(edge1));
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(4, graph.getAdjacentEdges(node5).size());
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testComplexUndoRedo() {
		List<GraphEdge> postExecution = new ArrayList<GraphEdge>(4);
		postExecution.add(new DefaultGraphEdge(node1, node2));
		postExecution.add(new DefaultGraphEdge(node2, node5));
		postExecution.add(new DefaultGraphEdge(node5, node3));
		postExecution.add(new DefaultGraphEdge(node5, node2));
		postExecution.add(new DefaultGraphEdge(node2, node2));

		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsEdge(edge1));
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertEquals(5, graph.getEdgeList().size());
		UndoableEdit edit = factory2.execute();
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertEquals(5, graph.getEdgeList().size());
		graphITER: for (Iterator<DirectionalHyperEdge<Integer>> it = graph
				.getEdgeList().iterator(); it.hasNext();) {
			DirectionalHyperEdge<Integer> ge = it.next();
			for (Iterator<GraphEdge> postIt = postExecution.iterator(); postIt
					.hasNext();) {
				GraphEdge testEdge = postIt.next();
				if (testEdge.getNodeAt(0).equals(ge.getNodeAt(0))
						&& testEdge.getNodeAt(1).equals(ge.getNodeAt(1))) {
					continue graphITER;
				}
			}
			fail();
		}
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertTrue(graph.containsEdge(edge1));
		assertTrue(graph.containsEdge(edge2));
		assertTrue(graph.containsEdge(edge3));
		assertTrue(graph.containsEdge(edge4));
		assertTrue(graph.containsEdge(edge5));
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertTrue(graph.containsNode(node2));
		assertTrue(graph.containsNode(node3));
		assertTrue(graph.containsNode(node4));
		assertTrue(graph.containsNode(node5));
		assertEquals(5, graph.getEdgeList().size());
		graphITER: for (Iterator<DirectionalHyperEdge<Integer>> it = graph
				.getEdgeList().iterator(); it.hasNext();) {
			DirectionalHyperEdge<Integer> ge = it.next();
			for (Iterator<GraphEdge> postIt = postExecution.iterator(); postIt
					.hasNext();) {
				GraphEdge testEdge = postIt.next();
				if (testEdge.getNodeAt(0).equals(ge.getNodeAt(0))
						&& testEdge.getNodeAt(1).equals(ge.getNodeAt(1))) {
					continue graphITER;
				}
			}
			fail();
		}
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public class TestDirectionalReferencedGraphEdge extends
			DefaultDirectionalHyperEdge<Integer> implements
			ReferencedEdge<Integer, Double> {

		private final Double ref;

		public TestDirectionalReferencedGraphEdge(Integer n1, Integer n2,
				Double o) {
			this(Arrays.asList(n1), Arrays.asList(n2), o);
		}

		public TestDirectionalReferencedGraphEdge(Collection<Integer> gn1,
				Collection<Integer> gn2, Double o) {
			super(gn1, gn2);
			ref = o;
		}

		public Double getReferenceObject() {
			return ref;
		}

		@Override
		public TestDirectionalReferencedGraphEdge createReplacementEdge(
				Collection<Integer> gn1, Collection<Integer> gn2) {
			TestDirectionalReferencedGraphEdge testDirectionalReferencedGraphEdge = new TestDirectionalReferencedGraphEdge(
					gn1, gn2, ref);
			return testDirectionalReferencedGraphEdge;
		}
	}

	public void testReferencedUndoRedo() {
		// Ensure proper interaction with Edge rebuilding!
		Double o = new Double(9.4);
		TestDirectionalReferencedGraphEdge edge = new TestDirectionalReferencedGraphEdge(
				node1, node3, o);
		assertEquals(5, graph.getEdgeList().size());
		assertTrue(graph.addEdge(edge));
		Command c = new TransferEdgesCommand("edit", graph, node1, node2);
		assertEquals(6, graph.getEdgeList().size());
		assertEquals(2, graph.getAdjacentEdges(node1).size());
		assertEquals(0, graph.getAdjacentEdges(node2).size());
		UndoableEdit edit = c.execute();
		assertEquals(6, graph.getEdgeList().size());
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(2, graph.getAdjacentEdges(node2).size());
		boolean checked = false;
		for (Iterator<DirectionalHyperEdge<Integer>> it = graph.getEdgeList()
				.iterator(); it.hasNext();) {
			DirectionalHyperEdge<Integer> ge = it.next();
			if (node2.equals(ge.getNodeAt(0)) && node3.equals(ge.getNodeAt(1))) {
				assertEquals(o, ((ReferencedEdge) ge).getReferenceObject());
				checked = true;
			}
		}
		assertTrue(checked);
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
		edit.undo();
		assertEquals(2, graph.getAdjacentEdges(node1).size());
		assertEquals(0, graph.getAdjacentEdges(node2).size());
		checked = false;
		for (Iterator<DirectionalHyperEdge<Integer>> it = graph.getEdgeList()
				.iterator(); it.hasNext();) {
			DirectionalHyperEdge<Integer> ge = it.next();
			if (node1.equals(ge.getNodeAt(0)) && node3.equals(ge.getNodeAt(1))) {
				assertEquals(o, ((ReferencedEdge) ge).getReferenceObject());
				checked = true;
			}
		}
		assertTrue(checked);
		assertFalse(edit.canUndo());
		assertTrue(edit.canRedo());
		edit.redo();
		assertEquals(0, graph.getAdjacentEdges(node1).size());
		assertEquals(2, graph.getAdjacentEdges(node2).size());
		checked = false;
		for (Iterator<DirectionalHyperEdge<Integer>> it = graph.getEdgeList()
				.iterator(); it.hasNext();) {
			DirectionalHyperEdge<Integer> ge = it.next();
			if (node2.equals(ge.getNodeAt(0)) && node3.equals(ge.getNodeAt(1))) {
				assertEquals(o, ((ReferencedEdge) ge).getReferenceObject());
				checked = true;
			}
		}
		assertTrue(checked);
		assertTrue(edit.canUndo());
		assertFalse(edit.canRedo());
	}

	public void testGetPresentationName() {
		assertEquals("edit", factory.getPresentationName());
		assertEquals("edit", factory.execute().getPresentationName());
		assertEquals("Transfer Adjacent Edges to new Graph Node",
				new TransferEdgesCommand(null, graph, node1, node2)
						.getPresentationName());
	}

	public void testGetInsertGraphEdgeEditor() {
		try {
			new TransferEdgesCommand("edit", null, node1, node2);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new TransferEdgesCommand("edit", graph, null, node2);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		try {
			new TransferEdgesCommand("edit", graph, node1, null);
			fail();
		} catch (IllegalArgumentException iae) {
			// OK
		}
		new TransferEdgesCommand(null, graph, node1, node2);
		// Same is illegal
		try {
			new TransferEdgesCommand("edit", graph, node1, node1);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testSerialization() {
		// NEEDTEST
	}

	public void testHyperEdge() {
		// NEEDTEST
	}
}