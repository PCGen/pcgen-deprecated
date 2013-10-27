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

import java.util.Arrays;

import pcgen.base.graph.core.DefaultGraphEdge;
import pcgen.base.graph.core.DefaultHyperEdge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.SimpleListMapGraph;

/**
 * @author Me
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SimpleListMapGraphTest extends AbstractGraphTestCase<Edge<Integer>> {

	private SimpleListMapGraph<Integer, Edge<Integer>> strategy;

	@Override
	protected DefaultHyperEdge<Integer> getLegalHyperEdge(Integer[] gna2) {
		return new DefaultHyperEdge<Integer>(Arrays.asList(gna2));
	}

	@Override
	protected Edge<Integer> getLegalEdge(Integer node1, Integer node2) {
		return new DefaultGraphEdge<Integer>(node1, node2);
	}

	/**
	 * Sets up the fixture, for example, open a network connection. This method
	 * is called before a test is executed.
	 * 
	 * @throws Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		strategy = new SimpleListMapGraph<Integer, Edge<Integer>>();
	}

	/**
	 * @return Returns the strategy.
	 */
	@Override
	Graph<Integer, Edge<Integer>> getStrategy() {
		return strategy;
	}

	public void testGetInternalizedNode() {
		Integer node = new Integer(1);
		Integer node2 = new Integer(2);
		Integer falseNode1 = new Integer(1); //MUST NOT BE Integer.valueOf(1)!!!!!
		assertFalse(strategy.containsNode(node));
		assertFalse(strategy.containsNode(node2));
		assertFalse(strategy.containsNode(falseNode1));
		assertEquals(0, strategy.getNodeList().size());
		// No nodes are in the graph, so response is null
		assertNull(strategy.getInternalizedNode(null));
		assertNull(strategy.getInternalizedNode(node));
		assertNull(strategy.getInternalizedNode(node2));
		assertNull(strategy.getInternalizedNode(falseNode1));
		
		assertTrue(strategy.addNode(node));
		assertTrue(strategy.containsNode(node));
		assertFalse(strategy.containsNode(node2));
		//Note that this returns true due to .equals()
		assertTrue(strategy.containsNode(falseNode1));
		//But that an instance test will fail
		for (Integer i : strategy.getNodeList()) {
			assertTrue(i == node || i == node2);
			assertTrue(i != falseNode1);
		}
		assertTrue(node == strategy.getInternalizedNode(node));
		assertNull(strategy.getInternalizedNode(node2));
		//And getInternalizedNode will actually return the instance 
		//that is .equal() to the given node, not the given node.
		assertTrue(node == strategy.getInternalizedNode(falseNode1));
	}
}