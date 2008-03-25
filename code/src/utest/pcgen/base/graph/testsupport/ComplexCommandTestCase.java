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
package pcgen.base.graph.testsupport;

import pcgen.base.graph.core.DefaultDirectionalGraphEdge;
import pcgen.base.graph.core.DirectionalListMapGraph;
import junit.framework.TestCase;

public class ComplexCommandTestCase extends TestCase {

	protected DirectionalListMapGraph<Integer, DefaultDirectionalGraphEdge<Integer>> graph;

	protected DefaultDirectionalGraphEdge<Integer> edge1, edge2, edge3, edge4,
			edge5, edge6, edge7, sideEffectEdge;

	protected Integer node1, node2, node3, node4, node5, node6, node7, node8,
			node9, sideEffectNode;

	@Override
	protected void setUp() throws Exception {
		graph = new DirectionalListMapGraph<Integer, DefaultDirectionalGraphEdge<Integer>>();
		node1 = Integer.valueOf(1);
		node2 = Integer.valueOf(2);
		node3 = Integer.valueOf(3);
		node4 = Integer.valueOf(4);
		node5 = Integer.valueOf(5);
		node6 = Integer.valueOf(6);
		node7 = Integer.valueOf(7);
		node8 = Integer.valueOf(8);
		node9 = Integer.valueOf(9);
		sideEffectNode = Integer.valueOf(-1);
		edge1 = new DefaultDirectionalGraphEdge<Integer>(node1, node2);
		edge2 = new DefaultDirectionalGraphEdge<Integer>(node1, node3);
		edge3 = new DefaultDirectionalGraphEdge<Integer>(node2, node4);
		edge4 = new DefaultDirectionalGraphEdge<Integer>(node2, node5);
		edge5 = new DefaultDirectionalGraphEdge<Integer>(node4, node6);
		edge6 = new DefaultDirectionalGraphEdge<Integer>(node3, node7);
		edge7 = new DefaultDirectionalGraphEdge<Integer>(node7, node8);
		sideEffectEdge = new DefaultDirectionalGraphEdge<Integer>(node1, node8);
		graph.addNode(node1);
		graph.addNode(node2);
		graph.addNode(node3);
		graph.addNode(node4);
		graph.addNode(node5);
		graph.addNode(node6);
		graph.addNode(node7);
		graph.addNode(node8);
		graph.addNode(node9);
		graph.addEdge(edge1);
		graph.addEdge(edge2);
		graph.addEdge(edge3);
		graph.addEdge(edge4);
		graph.addEdge(edge5);
		graph.addEdge(edge6);
		graph.addEdge(edge7);
		graph.addEdge(sideEffectEdge);
	}

}
