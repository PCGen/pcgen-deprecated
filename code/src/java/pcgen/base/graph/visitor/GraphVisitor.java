/*
 * Copyright (c) Thomas Parker, 2004-2007.
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
 * 
 * Created on Oct 11, 2004
 */
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.Edge;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * Provides an interface for a GraphVisitor which expects a value to be returned
 * by the visited Graph Element.
 * 
 * One use of this interface is in a fashion in a Visitor pattern (using double
 * dispatch), such as: <code>
 * public T getValue(GraphVisitor da) {
 *   return visitNode(this);
 * }
 * </code>
 * The methods in this interface are intentionally given different names so that
 * conflicts are avoided. Since Node and Edge are interfaces, the use of
 * different method names is required in order to avoid any ambiguity in the use
 * of this interface (though one could argue if someone made a single object
 * both a Node and a Edge, then they get what they deserve... but like many
 * classes in this library, this is defensive in its design (and documentation).
 */
public interface GraphVisitor<N, T>
{
	public T visitNode(N node);

	public T visitEdge(Edge<N> edge);
}