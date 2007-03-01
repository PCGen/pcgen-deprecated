package pcgen.base.graph.visitor;

import java.util.List;

import pcgen.base.enumeration.TriState;
import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.TestDirectionalEdge;
import pcgen.base.graph.core.TestDirectionalHyperEdge;
import pcgen.base.graph.visitor.IndeterminateDijkstraAlgorithm;
import junit.framework.TestCase;

public class IndeterminateDijkstraAlgorithmTest extends TestCase {

	private class MathUtilities {

		public static final double CALCULATION_ERROR = 1E-15;

	}

	DirectionalGraph g = new DirectionalListMapGraph();

	DirectionalGraph g2 = new DirectionalListMapGraph();

	DirectionalGraph g3 = new DirectionalListMapGraph();

	TestDirectionalHyperEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7;

	Integer node1, node2, node3, node4, node5, node6, node7, node8, node9,
			nodea, nodeb, nodec;

	TestDirectionalEdge dedge1, dedge2, dedge3, dedge4, dedge5, dedge6, dedge7;

	IndeterminateDijkstraAlgorithm d, d2;

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
		assertTrue(g2.addNode(node5));
		assertTrue(g2.addEdge(dedge1));
		assertTrue(g2.addEdge(dedge2));
		assertTrue(g2.addEdge(dedge3));
		assertTrue(g2.addEdge(dedge4));
		assertTrue(g2.addEdge(dedge5));
		assertTrue(g2.addEdge(dedge6));
		assertTrue(g2.addEdge(dedge7));
		d = new IndeterminateDijkstraAlgorithm(g);
		d2 = new IndeterminateDijkstraAlgorithm(g2);
	}

	public void testDijkstraNodeAlgorithm() {
		try {
			new IndeterminateDijkstraAlgorithm(null);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
	}

	public void testDijkstraNodeAlgorithmGraphdoubledouble() {
		try {
			new IndeterminateDijkstraAlgorithm(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new IndeterminateDijkstraAlgorithm(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			new IndeterminateDijkstraAlgorithm(null, 1.0);
			fail();
		} catch (IllegalArgumentException npe) {
			// OK
		}
		try {
			new IndeterminateDijkstraAlgorithm(g, -1.0);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testCalculateFromG1() {
		d.calculateFrom(node1);
		assertEquals(0.0, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(3.0, d.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(3.0, d.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(2.0, d.getDistanceTo(node6),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(0.0, d.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(3.0, d.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(2.0, d.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d.getDistanceTo(node8)));
		assertEquals(3.0, d.getDistanceTo(node9),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(3.0, d.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		try {
			d.calculateFrom(node1);
			fail();
		} catch (UnsupportedOperationException e) {
			// OK
		}
		// legal after clear
		d.clear();
		assertEquals(Double.POSITIVE_INFINITY, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		d.calculateFrom(node1);
		assertEquals(0.0, d.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
	}

	public void testCalculateFromG2() {
		try {
			d2.calculateFrom(node6);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		d2.calculateFrom(node1);
		assertEquals(0.0, d2.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d2.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d2.getDistanceTo(node3),
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
		assertEquals(2.0, d2.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(d2.getDistanceTo(node8)));
		assertTrue(Double.isNaN(d2.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, d2.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d2.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, d2.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		d2.clear();
		try {
			d2.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testTraverseEdgePass() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canTraverseEdge(Object gn, DirectionalEdge edge) {
				if (edge.equals(dedge1)) {
					if (super.getDistanceTo(node7) == Double.POSITIVE_INFINITY) {
						return TriState.UNDETERMINED;
					}
				}
				return super.canTraverseEdge(gn, edge);
			}

		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(0.0, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		ida.clear();
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testTraverseEdgeFail() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canTraverseEdge(Object gn, DirectionalEdge edge) {
				if (edge.equals(dedge1)) {
					if (super.getDistanceTo(node7) != Double.POSITIVE_INFINITY) {
						return TriState.NO;
					} else {
						return TriState.UNDETERMINED;
					}
				}
				return super.canTraverseEdge(gn, edge);
			}
		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		ida.clear();
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testTraverseEdgeFailUndetermined() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canTraverseEdge(Object gn, DirectionalEdge edge) {
				if (edge.equals(dedge1)) {
					return TriState.UNDETERMINED;
				}
				return super.canTraverseEdge(gn, edge);
			}
		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		ida.clear();
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testIncludeNodePass() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canIncludeNode(DirectionalEdge edge,
					Object thisNode) {
				if (thisNode.equals(node2)) {
					if (super.getDistanceTo(node7) == Double.POSITIVE_INFINITY) {
						return TriState.UNDETERMINED;
					}
				}
				return super.canIncludeNode(edge, thisNode);
			}
		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(0.0, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		ida.clear();
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testIncludeNodeFail() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canIncludeNode(DirectionalEdge edge,
					Object thisNode) {
				if (thisNode.equals(node2)) {
					if (super.getDistanceTo(node7) != Double.POSITIVE_INFINITY) {
						return TriState.NO;
					} else {
						return TriState.UNDETERMINED;
					}
				}
				return super.canIncludeNode(edge, thisNode);
			}
		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(0.0, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		List reachable = ida.getReachableNodes();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		// Ensure returned list is not remembered...
		reachable.add(nodea);
		reachable = ida.getReachableNodes();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		ida.clear();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		reachable = ida.getReachableNodes();
		assertEquals(0, reachable.size());
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}

	public void testIncludeNodeFailUndetermined() {
		IndeterminateDijkstraAlgorithm ida = new IndeterminateDijkstraAlgorithm(
				g2) {
			@Override
			protected TriState canIncludeNode(DirectionalEdge edge,
					Object thisNode) {
				if (thisNode.equals(node2)) {
					return TriState.UNDETERMINED;
				}
				return super.canIncludeNode(edge, thisNode);
			}
		};
		try {
			ida.calculateFrom(node8);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		ida.calculateFrom(node1);
		assertEquals(0.0, ida.getDistanceTo(node1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node2),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(node3),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node4),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(node5),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node6)));
		assertEquals(0.0, ida.getDistanceTo(dedge1),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(dedge2),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(edge2)));
		assertEquals(2.0, ida.getDistanceTo(node7),
				MathUtilities.CALCULATION_ERROR);
		assertTrue(Double.isNaN(ida.getDistanceTo(node8)));
		assertTrue(Double.isNaN(ida.getDistanceTo(node9)));
		assertEquals(Double.POSITIVE_INFINITY, ida.getDistanceTo(nodea),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodeb),
				MathUtilities.CALCULATION_ERROR);
		assertEquals(1.0, ida.getDistanceTo(nodec),
				MathUtilities.CALCULATION_ERROR);
		List reachable = ida.getReachableNodes();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		// Ensure returned list is not remembered...
		reachable.add(nodea);
		reachable = ida.getReachableNodes();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		ida.clear();
		assertEquals(5, reachable.size());
		assertTrue(reachable.contains(node1));
		assertTrue(reachable.contains(node3));
		assertTrue(reachable.contains(node7));
		assertTrue(reachable.contains(nodeb));
		assertTrue(reachable.contains(nodec));
		reachable = ida.getReachableNodes();
		assertEquals(0, reachable.size());
		try {
			ida.calculateFrom(null);
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
	}
}
