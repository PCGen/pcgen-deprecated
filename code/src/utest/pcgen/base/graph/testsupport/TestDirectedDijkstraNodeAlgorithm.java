/*
 * Copyright (c) Thomas Parker, 2005.
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

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.graph.core.Edge;
import pcgen.base.graph.core.Graph;
import pcgen.base.graph.visitor.DijkstraNodeAlgorithm;

public class TestDirectedDijkstraNodeAlgorithm extends DijkstraNodeAlgorithm {

	public TestDirectedDijkstraNodeAlgorithm(Graph g) {
		super(g);
	}

	public TestDirectedDijkstraNodeAlgorithm(Graph g, double limit) {
		super(g, limit);
	}

	@Override
	protected boolean canTraverseEdge(Edge edge, Object gn, int type) {
		return (edge instanceof DirectionalEdge)
				&& (((DirectionalEdge) edge).getNodeInterfaceType(gn) & type) != 0;
	}

}
