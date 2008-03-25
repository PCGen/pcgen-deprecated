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
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.testsupport.TestDirectionalEdge;
import pcgen.base.graph.testsupport.TestDirectionalHyperEdge;
import pcgen.base.graph.visitor.NodeDistanceCalculation;
import junit.framework.TestCase;

public class NodeDistanceCalculationTest extends TestCase {

	private class MathUtilities {

		public static final double CALCULATION_ERROR = 1E-15;

	}

	DirectionalGraph g = new DirectionalListMapGraph();

	TestDirectionalHyperEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7;

	Integer node1, node2, node3, node4, node5, node6, node7, node8, node9,
			nodea, nodeb, nodec;

	TestDirectionalEdge dedge1, dedge2, dedge3, dedge4, dedge5, dedge6, dedge7;

	NodeDistanceCalculation d, d2;

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
		edge1 = new TestDirectionalHyperEdge(node2, new Integer[] { node3,
				node6 });
		edge2 = new TestDirectionalHyperEdge(node6, new Integer[] { node5 });
		edge3 = new TestDirectionalHyperEdge(node6, new Integer[] { node4,
				node9 });
		edge4 = new TestDirectionalHyperEdge(node6, new Integer[] { nodea });
		edge5 = new TestDirectionalHyperEdge(node6, new Integer[] {});
		edge6 = new TestDirectionalHyperEdge(node4, new Integer[] { node6 });
		edge7 = new TestDirectionalHyperEdge(node6, new Integer[] { node6 });
		dedge1 = new TestDirectionalEdge(node1, node2);
		dedge2 = new TestDirectionalEdge(node4, node2);
		dedge3 = new TestDirectionalEdge(node1, node3);
		dedge4 = new TestDirectionalEdge(node1, node3);
		dedge5 = new TestDirectionalEdge(node4, node4);
		dedge6 = new TestDirectionalEdge(node3, node7);
		dedge7 = new TestDirectionalEdge(new Integer[] { node1, nodea },
				new Integer[] { nodeb, nodec });
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
		d = new NodeDistanceCalculation(g);
		d2 = new NodeDistanceCalculation(g) {
			@Override
			protected boolean canTraverseEdge(Edge edge, Object gn, int type) {
				if (edge == edge3) {
					return false;
				}
				if (edge instanceof DirectionalEdge) {
					DirectionalEdge de = (DirectionalEdge) edge;
					if ((de.getNodeInterfaceType(gn) & type) == 0) {
						// == is appropriate here, as it's an 'only' check
						return false;
					}
				}
				return super.canTraverseEdge(edge, gn, type);
			}

			@Override
			protected double calculateEdgeLength(Object node1, Edge edge,
					Object node2) {
				if (edge == dedge3) {
					return 5;
				} else if (edge == dedge4) {
					return 6;
				}
				return super.calculateEdgeLength(node1, edge, node2);
			}

		};
	}

	public void testDijkstraNodeAlgorithm() {
		try {
			new NodeDistanceCalculation(null);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
	}

	public void testDijkstraNodeAlgorithmGraphdoubledouble() {
		try {
			new NodeDistanceCalculation(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new NodeDistanceCalculation(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			new NodeDistanceCalculation(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new NodeDistanceCalculation(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testNullNodes() {
		try {
			d.calculateDistance(null, null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			d.calculateDistance(null, node1);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			d.calculateDistance(node1, null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testMissingNodes() {
		assertTrue(Double.isNaN(d.calculateDistance(node8, node1)));
		assertTrue(Double.isNaN(d.calculateDistance(node1, node8)));
	}

	public void testSimpleDistance() {
		double dbl1 = d.calculateDistance(node1, node7);
		assertEquals(2.0, dbl1, MathUtilities.CALCULATION_ERROR);
		double dbl2 = d2.calculateDistance(node1, node7);
		assertEquals(3.0, dbl2, MathUtilities.CALCULATION_ERROR);
	}

	public void testAroundDistance() {
		double dbl1 = d.calculateDistance(node1, nodea);
		assertEquals(1.0, dbl1, MathUtilities.CALCULATION_ERROR);
		double dbl2 = d2.calculateDistance(node1, nodea);
		assertEquals(3.0, dbl2, MathUtilities.CALCULATION_ERROR);
	}

	public void testSelfDistance() {
		double dbl1 = d.calculateDistance(node1, node1);
		assertEquals(0.0, dbl1, MathUtilities.CALCULATION_ERROR);
		double dbl2 = d2.calculateDistance(node1, node1);
		assertEquals(0.0, dbl2, MathUtilities.CALCULATION_ERROR);
	}
	
	public void testStranded() {
		double dbl1 = d.calculateDistance(node1, node9);
		assertEquals(3.0, dbl1, MathUtilities.CALCULATION_ERROR);
		double dbl2 = d2.calculateDistance(node1, node9);
		assertEquals(Double.POSITIVE_INFINITY, dbl2, MathUtilities.CALCULATION_ERROR);
	}

	public void testReverse() {
		double dbl1 = d.calculateDistance(nodea, node1);
		assertEquals(1.0, dbl1, MathUtilities.CALCULATION_ERROR);
		double dbl2 = d2.calculateDistance(nodea, node1);
		assertEquals(Double.POSITIVE_INFINITY, dbl2, MathUtilities.CALCULATION_ERROR);
	}
}
