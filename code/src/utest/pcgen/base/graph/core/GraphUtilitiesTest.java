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

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GraphUtilitiesTest extends TestCase {
	private Integer node1, node2, node3, node4, node5;

	private DefaultDirectionalHyperEdge<Integer> dedge1, dedge2, dedge3,
			dedge4, dedge5;

	private DirectionalGraph<Integer, DefaultDirectionalHyperEdge<Integer>> graph2;

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		graph2 = new DirectionalListMapGraph<Integer, DefaultDirectionalHyperEdge<Integer>>();
		node1 = Integer.valueOf(1);
		node2 = Integer.valueOf(3);
		node3 = Integer.valueOf(4);
		node4 = Integer.valueOf(6);
		node5 = Integer.valueOf(18);
		dedge1 = new DefaultDirectionalHyperEdge<Integer>(Collections
				.singletonList(node1), Collections.singletonList(node2));
		dedge2 = new DefaultDirectionalHyperEdge<Integer>(Collections
				.singletonList(node2), Collections.singletonList(node1));
		dedge3 = new DefaultDirectionalHyperEdge<Integer>(Collections
				.singletonList(node1), Collections.singletonList(node3));
		dedge4 = new DefaultDirectionalHyperEdge<Integer>(Collections
				.singletonList(node1), Collections.singletonList(node3));
		dedge5 = new DefaultDirectionalHyperEdge<Integer>(Collections
				.singletonList(node4), Collections.singletonList(node4));
		assertTrue(graph2.addNode(node1));
		assertTrue(graph2.addNode(node2));
		assertTrue(graph2.addNode(node3));
		assertTrue(graph2.addNode(node4));
		assertTrue(graph2.addEdge(dedge1));
		assertTrue(graph2.addEdge(dedge2));
		assertTrue(graph2.addEdge(dedge3));
		assertTrue(graph2.addEdge(dedge4));
		assertTrue(graph2.addEdge(dedge5));
	}

	public void testGetDescendentNodes() {
		Collection<Integer> s = GraphUtilities.getDescendentNodes(graph2, node1);
		assertTrue(s.remove(node1));
		assertTrue(s.remove(node2));
		assertTrue(s.remove(node3));
		assertTrue(s.isEmpty());
		s = GraphUtilities.getDescendentNodes(graph2, node2);
		assertTrue(s.remove(node1));
		assertTrue(s.remove(node2));
		assertTrue(s.remove(node3));
		assertTrue(s.isEmpty());
		s = GraphUtilities.getDescendentNodes(graph2, node3);
		assertTrue(s.isEmpty());
		s = GraphUtilities.getDescendentNodes(graph2, node4);
		assertTrue(s.remove(node4));
		assertTrue(s.isEmpty());
		s = GraphUtilities.getDescendentNodes(graph2, node5);
		assertNull(s);
		try {
			GraphUtilities.getDescendentNodes(null, node5);
			fail();
		} catch (NullPointerException e) {
			// OK
		}
		assertNull(GraphUtilities.getDescendentNodes(graph2, null));
	}
}