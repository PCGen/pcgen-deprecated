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

import java.util.Comparator;

import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.visitor.GraphHeapComponent;
import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphHeapComponentTest extends TestCase
{

	GraphHeapComponent ghc1, ghc2, ghc3, ghc4, ghc5, ghc6, ghc7, ghc8, ghc9,
			ghc0;

	TestGraphNode gn6, gn7, gn8, gn90;

	DefaultGraphEdge ge67, ge8, ge9, ge0;

	public static class TestGraphNode
	{
		private final int integer;

		public TestGraphNode(int i)
		{
			integer = i;
		}

		@Override
		public int hashCode()
		{
			return integer;
		}
	}

	public static class TestGraphEdge extends DefaultGraphEdge
	{
		private final int integer;

		public TestGraphEdge(Object n1, Object n2, int i)
		{
			super(n1, n2);
			integer = i;
		}

		@Override
		public int hashCode()
		{
			return integer;
		}
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception
	{
		gn6 = new TestGraphNode(6);
		gn7 = new TestGraphNode(7);
		gn8 = new TestGraphNode(-8);
		gn90 = new TestGraphNode(90);
		ge67 = new TestGraphEdge(gn6, gn7, 5);
		ge8 = new TestGraphEdge(gn7, gn8, 8);
		ge9 = new TestGraphEdge(gn8, gn90, -14);
		ge0 = new TestGraphEdge(gn90, gn6, -25);
		ghc0 = new GraphHeapComponent(0, ge0, gn90);
		ghc1 = new GraphHeapComponent(0, ge67, gn6);
		ghc2 = new GraphHeapComponent(1, ge8, gn7);
		ghc3 = new GraphHeapComponent(-1, ge9, gn8);
		ghc4 = new GraphHeapComponent(0.01, ge0, gn90);
		ghc5 = new GraphHeapComponent(-0.01, ge0, gn90);
		ghc6 = new GraphHeapComponent(0, ge67, gn6);
		ghc7 = new GraphHeapComponent(0, ge67, gn7);
		ghc8 = new GraphHeapComponent(0, ge8, gn8);
		ghc9 = new GraphHeapComponent(0, ge9, gn90);
	}

	public void testGraphHeapComponent()
	{
		try
		{
			new GraphHeapComponent(0, ge9, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new GraphHeapComponent(0, null, gn90);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			new GraphHeapComponent(0, null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testCompareTo()
	{
		Comparator<GraphHeapComponent<?, ?>> comp =
				GraphHeapComponent.DISTANCE_COMPARATOR;
		assertTrue(comp.compare(ghc1, ghc2) < 0);
		assertTrue(comp.compare(ghc1, ghc3) > 0);
		assertTrue(comp.compare(ghc1, ghc4) < 0);
		assertTrue(comp.compare(ghc1, ghc5) > 0);
		assertTrue(comp.compare(ghc6, ghc6) == 0);
		assertTrue(comp.compare(ghc6, ghc7) == 0);
		assertTrue(comp.compare(ghc6, ghc8) == 0);
		assertTrue(comp.compare(ghc6, ghc9) == 0);
		assertTrue(comp.compare(ghc6, ghc0) == 0);
		assertTrue(comp.compare(ghc7, ghc7) == 0);
		assertTrue(comp.compare(ghc7, ghc8) == 0);
		assertTrue(comp.compare(ghc7, ghc9) == 0);
		assertTrue(comp.compare(ghc7, ghc0) == 0);
		assertTrue(comp.compare(ghc8, ghc8) == 0);
		assertTrue(comp.compare(ghc8, ghc9) == 0);
		assertTrue(comp.compare(ghc8, ghc0) == 0);
		assertTrue(comp.compare(ghc9, ghc9) == 0);
		assertTrue(comp.compare(ghc9, ghc0) == 0);
		assertTrue(comp.compare(ghc0, ghc0) == 0);
		// Check in case all 3 are identical! (should still be consistent with
		// equals)
		TestGraphNode gna = new TestGraphNode(6);
		TestGraphEdge gea = new TestGraphEdge(gn6, gn7, 5);
		GraphHeapComponent ghca = new GraphHeapComponent(0, gea, gna);
		assertEquals(ghc6.distance, ghca.distance, 0.0);
		assertEquals(ghc6.node.hashCode(), ghca.node.hashCode());
		assertEquals(ghc6.edge.hashCode(), ghca.edge.hashCode());
		assertTrue(comp.compare(ghc6, ghca) == 0);
		// overflow!
		GraphHeapComponent ghcb =
				new GraphHeapComponent(-Double.MAX_VALUE, gea, gna);
		GraphHeapComponent ghcc =
				new GraphHeapComponent(Double.MAX_VALUE, gea, gna);
		assertTrue(comp.compare(ghcb, ghcc) < 0);
		assertTrue(comp.compare(ghcc, ghcb) > 0);
		// note do not care if the edge or node order changes - not really
		// important
	}
}