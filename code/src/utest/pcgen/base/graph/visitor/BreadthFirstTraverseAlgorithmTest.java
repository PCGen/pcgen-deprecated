/*
 * Copyright (c) Thomas Parker, 2005.
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
package pcgen.base.graph.visitor;

import java.util.Set;

import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.testsupport.TestDirectionalEdge;
import pcgen.base.graph.visitor.BreadthFirstTraverseAlgorithm;
import pcgen.base.graph.visitor.DirectedBreadthFirstTraverseAlgorithm;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BreadthFirstTraverseAlgorithmTest extends TestCase {

	DirectionalGraph g2 = new DirectionalListMapGraph();

	Integer node1, node2, node3, node4, node5, node6, node7, node8, node9,
			nodea, nodeb, nodec, noded, nodee, nodef;

	TestDirectionalEdge dedge1, dedge2, dedge3, dedge4, dedge5, dedge6, dedge7,
			dedge8, dedge9, dedgea, dedgeb, dedgec;

	DirectedBreadthFirstTraverseAlgorithm d2;

	BreadthFirstTraverseAlgorithm d;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		node1 = new Integer(1);
		node2 = new Integer(2);
		node3 = new Integer(3);
		node4 = new Integer(4);
		node5 = new Integer(5);
		node6 = new Integer(6);
		node7 = new Integer(7);
		node8 = new Integer(8);
		node9 = new Integer(9);
		nodea = new Integer(10);
		nodeb = new Integer(11);
		nodec = new Integer(12);
		noded = new Integer(13);
		nodee = new Integer(14);
		nodef = new Integer(15);
		dedge1 = new TestDirectionalEdge(node1, node2);
		dedge2 = new TestDirectionalEdge(node4, node2);
		dedge3 = new TestDirectionalEdge(node1, node3);
		dedge4 = new TestDirectionalEdge(node1, node3);
		dedge5 = new TestDirectionalEdge(node4, node4);
		dedge6 = new TestDirectionalEdge(new Integer[] { node8, node9 },
				new Integer[] { node1, node7 });
		dedge7 = new TestDirectionalEdge(new Integer[] { node1, nodea },
				new Integer[] { nodeb, nodec });
		dedge8 = new TestDirectionalEdge(node6, node5);
		dedge9 = new TestDirectionalEdge(node6, node7);
		dedgea = new TestDirectionalEdge(node5, node7);
		dedgeb = new TestDirectionalEdge(noded, nodee);
		dedgec = new TestDirectionalEdge(new Integer[] { nodef },
				new Integer[] {});
		g2.addNode(node1);
		g2.addNode(node2);
		g2.addNode(node3);
		g2.addNode(node4);
		g2.addNode(node5);
		g2.addNode(node6);
		g2.addNode(node7);
		g2.addNode(node8);
		g2.addNode(node9);
		g2.addNode(nodea);
		g2.addNode(nodeb);
		g2.addNode(nodec);
		g2.addNode(noded);
		g2.addNode(nodee);
		g2.addNode(nodef);
		g2.addEdge(dedge1);
		g2.addEdge(dedge2);
		g2.addEdge(dedge3);
		g2.addEdge(dedge4);
		g2.addEdge(dedge5);
		g2.addEdge(dedge6);
		g2.addEdge(dedge7);
		g2.addEdge(dedge8);
		g2.addEdge(dedge9);
		g2.addEdge(dedgea);
		g2.addEdge(dedgeb);
		g2.addEdge(dedgec);
		d = new BreadthFirstTraverseAlgorithm(g2);
		d2 = new DirectedBreadthFirstTraverseAlgorithm(g2);
	}

	public void testBreadthFirstTraverseAlgorithm() {
		try {
			new BreadthFirstTraverseAlgorithm(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testTraverseFromNode() {
		try {
			d2.traverseFromNode(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		d2.traverseFromNode(nodee);
		assertFalse(d2.getVisitedNodes().isEmpty());
		// Can't traverse again
		try {
			d2.traverseFromNode(noded);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		try {
			d2.traverseFromEdge(dedgec);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
	}

	public void testTraverseFromEdge() {
		try {
			d2.traverseFromEdge(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		d2.traverseFromEdge(dedgec);
		assertFalse(d2.getVisitedEdges().isEmpty());
		// Can't traverse again
		try {
			d2.traverseFromEdge(dedgec);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		try {
			d2.traverseFromNode(noded);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
	}

	public void testTraverse() {
		d2.traverseFromNode(node1);
		Set<Edge> edge = d2.getVisitedEdges();
		assertEquals(4, edge.size());
		assertTrue(edge.contains(dedge1));
		assertTrue(edge.contains(dedge3));
		assertTrue(edge.contains(dedge4));
		assertTrue(edge.contains(dedge7));
		Set<Integer> node = d2.getVisitedNodes();
		assertEquals(5, node.size());
		assertTrue(node.contains(node1));
		assertTrue(node.contains(node2));
		assertTrue(node.contains(node3));
		assertTrue(node.contains(nodeb));
		assertTrue(node.contains(nodec));
		d2.clear();
		edge = d2.getVisitedEdges();
		assertEquals(0, edge.size());
		node = d2.getVisitedNodes();
		assertEquals(0, node.size());
		d2.traverseFromEdge(dedge7);
		edge = d2.getVisitedEdges();
		assertEquals(1, edge.size());
		assertTrue(edge.contains(dedge7));
		node = d2.getVisitedNodes();
		assertEquals(2, node.size());
		assertTrue(node.contains(nodeb));
		assertTrue(node.contains(nodec));

		d.traverseFromNode(node1);
		edge = d.getVisitedEdges();
		assertEquals(10, edge.size());
		assertTrue(edge.contains(dedge1));
		assertTrue(edge.contains(dedge2));
		assertTrue(edge.contains(dedge3));
		assertTrue(edge.contains(dedge4));
		assertTrue(edge.contains(dedge5));
		assertTrue(edge.contains(dedge6));
		assertTrue(edge.contains(dedge7));
		assertTrue(edge.contains(dedge8));
		assertTrue(edge.contains(dedge9));
		assertTrue(edge.contains(dedgea));
		node = d.getVisitedNodes();
		assertEquals(12, node.size());
		assertTrue(node.contains(node1));
		assertTrue(node.contains(node2));
		assertTrue(node.contains(node3));
		assertTrue(node.contains(node4));
		assertTrue(node.contains(node5));
		assertTrue(node.contains(node6));
		assertTrue(node.contains(node7));
		assertTrue(node.contains(node8));
		assertTrue(node.contains(node9));
		assertTrue(node.contains(nodea));
		assertTrue(node.contains(nodeb));
		assertTrue(node.contains(nodec));
		d.clear();
		edge = d.getVisitedEdges();
		assertEquals(0, edge.size());
		node = d.getVisitedNodes();
		assertEquals(0, node.size());
	}
}