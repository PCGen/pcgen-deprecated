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

import junit.framework.TestCase;
import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.DirectionalGraph;
import pcgen.base.graph.core.DirectionalListMapGraph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.TestDirectionalEdge;
import pcgen.base.graph.core.TestDirectionalHyperEdge;
import pcgen.base.util.DefaultMap;

public class DirectedNodeWeightCalculationTest extends TestCase
{

	private class MathUtilities
	{

		public static final double CALCULATION_ERROR = 1E-15;

	}

	DirectionalGraph g = new DirectionalListMapGraph();

	TestDirectionalHyperEdge edge1, edge2, edge3, edge4, edge5, edge6, edge7,
			edge8;

	Integer node1, node2, node3, node4, node5, node6, node7, node8, node9,
			nodea, nodeb, nodec;

	TestDirectionalEdge dedge1, dedge2, dedge3, dedge4, dedge5, dedge6, dedge7;

	DirectedNodeWeightCalculation d;

	DefaultMap<Edge, Integer> map = new DefaultMap<Edge, Integer>();

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		map.clear();
		map.setDefaultValue(Integer.valueOf(1));
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
		edge1 =
				new TestDirectionalHyperEdge(node2, new Integer[]{node3, node6});
		edge2 = new TestDirectionalHyperEdge(node6, new Integer[]{node5});
		edge3 =
				new TestDirectionalHyperEdge(node6, new Integer[]{node4, node9});
		edge4 = new TestDirectionalHyperEdge(node6, new Integer[]{nodea});
		edge5 = new TestDirectionalHyperEdge(node6, new Integer[]{});
		edge6 = new TestDirectionalHyperEdge(node4, new Integer[]{node6});
		edge7 = new TestDirectionalHyperEdge(node6, new Integer[]{node6});
		edge8 = new TestDirectionalHyperEdge(node8, new Integer[]{node6});
		dedge1 = new TestDirectionalEdge(node1, node2);
		dedge2 = new TestDirectionalEdge(node4, node5);
		dedge3 = new TestDirectionalEdge(node1, node3);
		dedge4 = new TestDirectionalEdge(node1, node3);
		dedge5 = new TestDirectionalEdge(node4, node4);
		dedge6 = new TestDirectionalEdge(node3, node7);
		dedge7 =
				new TestDirectionalEdge(new Integer[]{node1, nodea},
					new Integer[]{nodeb, nodec});
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
		d = new DirectedNodeWeightCalculation(g)
		{
			@Override
			protected int getEdgeWeight(int weight, DirectionalEdge edge)
			{
				System.err.println(weight + " " + edge + " "
					+ map.get(edge).intValue());
				return weight * map.get(edge).intValue();
			}

		};
	}

	public void testDijkstraNodeAlgorithm()
	{
		try
		{
			new NodeDistanceCalculation(null);
			fail();
		}
		catch (IllegalArgumentException npe)
		{
			// OK
		}
	}

	public void testDijkstraNodeAlgorithmGraphdoubledouble()
	{
		try
		{
			new NodeDistanceCalculation(null, 1.0);
			fail();
		}
		catch (IllegalArgumentException npe)
		{
			// OK
		}
		try
		{
			new NodeDistanceCalculation(g, -1.0);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new NodeDistanceCalculation(null, 1.0);
			fail();
		}
		catch (IllegalArgumentException npe)
		{
			// OK
		}
		try
		{
			new NodeDistanceCalculation(g, -1.0);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testNullNode()
	{
		try
		{
			d.calculateNodeWeight(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testNullEdge()
	{
		try
		{
			d.calculateEdgeWeight(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testOneNodeCall()
	{
		d.calculateNodeWeight(node3);
		try
		{
			d.calculateNodeWeight(node1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//OK
		}
		try
		{
			d.calculateEdgeWeight(dedge3);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//OK
		}
	}

	public void testClear()
	{
		d.calculateNodeWeight(node3);
		try
		{
			d.calculateNodeWeight(node1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//OK
		}
		d.clear();
		d.calculateEdgeWeight(dedge3);
	}

	public void testOneEdgeCall()
	{
		d.calculateEdgeWeight(dedge6);
		try
		{
			d.calculateNodeWeight(node1);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//OK
		}
		try
		{
			d.calculateEdgeWeight(dedge3);
			fail();
		}
		catch (UnsupportedOperationException e)
		{
			//OK
		}
	}

	public void testMissingNode()
	{
		assertEquals(-1, d.calculateNodeWeight(node8));
	}

	public void testMissingEdge()
	{
		assertEquals(-1, d.calculateEdgeWeight(edge8));
	}

	public void testSimpleEdgeDistance()
	{
		assertEquals(1, d.calculateEdgeWeight(dedge3));
	}

	public void testComplexEdgeDistance()
	{
		assertEquals(3, d.calculateEdgeWeight(dedge6));
	}

	public void testSimpleNodeDistance()
	{
		assertEquals(1, d.calculateNodeWeight(node1));
	}

	public void testComplexNodeDistance()
	{
		assertEquals(3, d.calculateNodeWeight(node3));
	}

	public void testCycleError()
	{
		assertEquals(-1, d.calculateNodeWeight(node4));
	}

	public void testWeighted()
	{
		map.put(dedge6, 2);
		assertEquals(6, d.calculateNodeWeight(node7));
	}

	public void testWeightedComplex()
	{
		map.put(edge1, 2);
		map.put(dedge1, 3);
		map.put(dedge3, 4);
		map.put(dedge6, 5);
		// Should be (1 + 4 + (2 * 3)) * 5
		assertEquals(55, d.calculateNodeWeight(node7));
	}
}
