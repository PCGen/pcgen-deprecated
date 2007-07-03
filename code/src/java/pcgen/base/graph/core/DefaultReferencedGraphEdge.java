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
 * Created on Aug 27, 2004
 */
package pcgen.base.graph.core;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A DefaultReferencedGraphEdge is a default implementation of an edge that
 * implements the ReferencedEdge and GraphEdge interfaces.
 */
public class DefaultReferencedGraphEdge<N, T> extends DefaultGraphEdge<N>
		implements ReferencedEdge<N, T>
{

	/**
	 * The reference object of this DefaultReferencedGraphEdge.
	 */
	private final T ref;

	/**
	 * Creates a new DefaultReferencedGraphEdge which is connected to the given
	 * Nodes and contains the given Reference Object.
	 * 
	 * @param node1
	 *            The first Node to which this DefaultReferencedGraphEdge is
	 *            connected
	 * @param node2
	 *            The second Node to which this DefaultReferencedGraphEdge is
	 *            connected
	 * @param o
	 *            The Reference Object for this DefaultReferencedGraphEdge
	 */
	public DefaultReferencedGraphEdge(N node1, N node2, T o)
	{
		super(node1, node2);
		ref = o;
	}

	/**
	 * Returns the Reference Object for this DefaultReferencedGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.ReferencedEdge#getReferenceObject()
	 */
	public T getReferenceObject()
	{
		return ref;
	}

	/**
	 * Creates a replacement edge for this DefaultReferencedGraphEdge. The
	 * replacement Edge will be connected to the given Nodes. The Reference
	 * Object for the replacement Edge will be the same as the Reference Object
	 * of this DefaultReferencedGraphEdge.
	 * 
	 * @see pcgen.base.graph.core.DefaultGraphEdge#createReplacementEdge(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public DefaultReferencedGraphEdge<N, T> createReplacementEdge(N gn1, N gn2)
	{
		return new DefaultReferencedGraphEdge<N, T>(gn1, gn2, ref);
	}
}