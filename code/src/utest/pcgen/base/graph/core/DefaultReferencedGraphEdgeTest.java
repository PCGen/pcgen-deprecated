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
package pcgen.base.graph.core;

import pcgen.base.graph.core.DefaultReferencedGraphEdge;
import pcgen.base.graph.core.Edge;
import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultReferencedGraphEdgeTest extends TestCase {

	Integer node1, node2, node3, node4;

	DefaultReferencedGraphEdge<Integer, Double> edge1, edge2, edge3, edge4,
			edge5;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		node1 = Integer.valueOf(1);
		node2 = Integer.valueOf(3);
		node3 = Integer.valueOf(5);
		node4 = Integer.valueOf(16);
		edge1 = new DefaultReferencedGraphEdge<Integer, Double>(node1, node2,
				new Double(-1));
		edge2 = new DefaultReferencedGraphEdge<Integer, Double>(node2, node1,
				new Double(-2));
		edge3 = new DefaultReferencedGraphEdge<Integer, Double>(node1, node3,
				new Double(-3));
		edge4 = new DefaultReferencedGraphEdge<Integer, Double>(node1, node3,
				new Double(-4));
		edge5 = new DefaultReferencedGraphEdge<Integer, Double>(node4, node4,
				new Double(-5));
	}

	public void testCreateReplacementEdge() {
		Edge<Integer> ge = edge1.createReplacementEdge(node3, node4);
		assertTrue(ge instanceof DefaultReferencedGraphEdge);
		// check nodes
		assertEquals(node3, ge.getNodeAt(0));
		assertEquals(node4, ge.getNodeAt(1));
		// check contents too!
		assertEquals(new Double(-1), ((DefaultReferencedGraphEdge) ge)
				.getReferenceObject());
	}

	public void testDefaultReferencedGraphEdge() {
		try {
			new DefaultReferencedGraphEdge<Integer, Double>(node1, null,
					new Double(5));
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		try {
			new DefaultReferencedGraphEdge<Integer, Double>(null, node4,
					new Double(5));
			fail();
		} catch (IllegalArgumentException e) {
			// OK
		}
		// This is legal (parallel behavior of DefaultGraphNode)
		DefaultReferencedGraphEdge<Integer, Double> ge = new DefaultReferencedGraphEdge<Integer, Double>(
				node1, node2, null);
		assertNull(ge.getReferenceObject());
	}

	public void testGetReferenceObject() {
		assertEquals(new Double(-1), edge1.getReferenceObject());
		assertEquals(new Double(-2), edge2.getReferenceObject());
		assertEquals(new Double(-3), edge3.getReferenceObject());
		assertEquals(new Double(-4), edge4.getReferenceObject());
		assertEquals(new Double(-5), edge5.getReferenceObject());
	}

	public void getGraphEdgeFactoryTest() {
		// NEEDTEST
	}
}