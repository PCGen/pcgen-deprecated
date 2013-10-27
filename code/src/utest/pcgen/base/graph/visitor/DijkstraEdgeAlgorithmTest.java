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
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.testsupport.TestDirectedDijkstraEdgeAlgorithm;
import pcgen.base.graph.testsupport.TestDirectionalEdge;
import pcgen.base.graph.testsupport.TestDirectionalHyperEdge;
import pcgen.base.graph.visitor.DijkstraEdgeAlgorithm;
import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DijkstraEdgeAlgorithmTest extends TestCase {

	private class MathUtilities {

		public static final double CALCULATION_ERROR = 1E-15;

	}

	Graph g = new DirectionalListMapGraph();

	Graph g2 = new DirectionalListMapGraph();

	TestDirectionalHyperEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7;

	Integer node1, node2, node3, node4, node5, node6, node7, node8, node9,
			nodea, nodeb, nodec;

	TestDirectionalEdge dedge1, dedge2, dedge3, dedge4, dedge5, dedge6, dedge7;

	DijkstraEdgeAlgorithm d, d2;

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
		edge1 = new TestDirectionalHyperEdge(node4, new Integer[] { node3,
				node6 });
		edge2 = new TestDirectionalHyperEdge(node6, new Integer[] { node5 });
		edge3 = new TestDirectionalHyperEdge(node6, new Integer[] { node4,
				node3 });
		edge4 = new TestDirectionalHyperEdge(node6, new Integer[] { node2 });
		edge5 = new TestDirectionalHyperEdge(node6, new Integer[] {});
		edge6 = new TestDirectionalHyperEdge(node4, new Integer[] { node6 });
		edge7 = new TestDirectionalHyperEdge(node6, new Integer[] { node6 });
		dedge1 = new TestDirectionalEdge(node1, node2);
		dedge2 = new TestDirectionalEdge(node4, node2);
		dedge3 = new TestDirectionalEdge(node1, node3);
		dedge4 = new TestDirectionalEdge(node1, node3);
		dedge5 = new TestDirectionalEdge(node4, node4);
		dedge6 = new TestDirectionalEdge(new Integer[] { node8, node9 },
				new Integer[] { node1, node7 });
		dedge7 = new TestDirectionalEdge(new Integer[] { node1, nodea },
				new Integer[] { nodeb, nodec });
		assertTrue(g.addNode(node1));
		assertTrue(g.addNode(node2));
		assertTrue(g.addNode(node3));
		assertTrue(g.addNode(node4));
		assertTrue(g.addNode(node5));
		assertTrue(g.addNode(node6));
		assertTrue(g.addEdge(edge1));
		assertTrue(g.addEdge(edge2));
		assertTrue(g.addEdge(edge3));
		assertTrue(g.addEdge(edge4));
		assertTrue(g.addEdge(edge5));
		assertTrue(g.addEdge(edge6));
		assertTrue(g.addEdge(edge7));
		assertTrue(g.addEdge(dedge1));
		assertTrue(g.addEdge(dedge2));
		assertTrue(g.addEdge(dedge3));
		assertTrue(g.addEdge(dedge4));
		assertTrue(g.addEdge(dedge5));
		assertTrue(g.addEdge(dedge6));
		assertTrue(g.addEdge(dedge7));
		assertTrue(g2.addNode(node1));
		assertTrue(g2.addNode(node2));
		assertTrue(g2.addNode(node3));
		assertTrue(g2.addNode(node4));
		assertTrue(g2.addNode(node5));
		assertTrue(g2.addEdge(dedge1));
		assertTrue(g2.addEdge(dedge2));
		assertTrue(g2.addEdge(dedge3));
		assertTrue(g2.addEdge(dedge4));
		assertTrue(g2.addEdge(dedge5));
		assertTrue(g2.addEdge(dedge6));
		assertTrue(g2.addEdge(dedge7));
		d = new DijkstraEdgeAlgorithm(g);
		d2 = new TestDirectedDijkstraEdgeAlgorithm(g2);
	}

	public void testDijkstraEdgeAlgorithmGraph() {
		try {
			new DijkstraEdgeAlgorithm(null);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
	}

	public void testDijkstraEdgeAlgorithmGraphdouble() {
		try {
			new DijkstraEdgeAlgorithm(null);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
	}

	public void testDijkstraEdgeAlgorithmGraphdoubledouble() {
		try {
			new DijkstraEdgeAlgorithm(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new DijkstraEdgeAlgorithm(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			new TestDirectedDijkstraEdgeAlgorithm(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new TestDirectedDijkstraEdgeAlgorithm(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testCalculateFrom() {
		d.calculateFromNode(node1);
		assertEquals(0.0, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(2.0, d.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(node6),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(node8),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(node9),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(dedge1, d.getClosestEdgeOnNode(node2));
		assertTrue(d.getClosestEdgeOnNode(node3).equals(dedge3)
				|| d.getClosestEdgeOnNode(node3).equals(dedge4));
		assertTrue(d.getClosestEdgeOnNode(node4).equals(edge1)
				|| d.getClosestEdgeOnNode(node4).equals(edge3)
				|| d.getClosestEdgeOnNode(node4).equals(dedge2)
				|| d.getClosestEdgeOnNode(node4).equals(dedge5));
		assertEquals(edge2, d.getClosestEdgeOnNode(node5));
		assertTrue(d.getClosestEdgeOnNode(node6).equals(edge1)
				|| d.getClosestEdgeOnNode(node6).equals(edge3)
				|| d.getClosestEdgeOnNode(node6).equals(edge4));
		try {
			d.getClosestEdgeOnNode(new Integer(-1));
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			d.getClosestEdgeOnNode(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			d.calculateFromNode(node1);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		// legal after clear
		d.clear();
		assertEquals(Double.POSITIVE_INFINITY, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		d.calculateFromNode(node1);
		assertEquals(0.0, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		try {
			d2.calculateFromNode(node6);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		d2.calculateFromNode(node1);
		assertEquals(0.0, d2.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d2.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d2.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d2.getDistanceTo(node6)));
		assertEquals(0.0, d2.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d2.getDistanceTo(edge2)));
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node8),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node9),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d2.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d2.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		try {
			d2.calculateFromEdge(dedge1);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		d2.clear();
		try {
			Edge he = null;
			d2.calculateFromEdge(he);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			Object he = null;
			d2.calculateFromNode(he);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		d2.clear();
		d2.calculateFromEdge(dedge1);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d2.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d2.getDistanceTo(node6)));
		assertEquals(0.0, d2.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d2.getDistanceTo(edge2)));
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		try {
			d2.calculateFromEdge(dedge1);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		try {
			d2.calculateFromNode(node1);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		// legal after clear
		d2.clear();
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		d2.calculateFromEdge(dedge1);
		assertEquals(0.0, d2.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		d2.clear();
		try {
			d2.calculateFromEdge(edge1);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}

	}

	public void testGetClosestEdgeOnNode() {
		// NEEDTEST
	}
}