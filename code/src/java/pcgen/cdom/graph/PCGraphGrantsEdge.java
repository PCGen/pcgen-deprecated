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
package pcgen.cdom.graph;

import pcgen.cdom.base.PrereqObject;

public class PCGraphGrantsEdge extends AbstractPCGraphEdge implements
		PCGraphEdge
{

	private final String source;

	public PCGraphGrantsEdge(PrereqObject node1, PrereqObject node2,
		String sourceToken)
	{
		super(node1, node2);
		source = sourceToken;
	}

	/**
	 * Creates a replacement DefaultGraphEdge for this DefaultGraphEdge, with
	 * the replacement connected to the two given Nodes.
	 * 
	 * @see pcbase.graph.core.GraphEdge#createReplacementEdge(java.lang.Object,
	 *      java.lang.Object)
	 */
	public PCGraphGrantsEdge createReplacementEdge(PrereqObject gn1,
		PrereqObject gn2)
	{
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(gn1, gn2, source);
		copyAssociationMapTo(edge);
		edge.addAllPrerequisites(getPrerequisiteList());
		return edge;
	}

	public String getSourceToken()
	{
		return source;
	}

	@Override
	public int hashCode()
	{
		return getNodeAt(0).hashCode() ^ getNodeAt(1).hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof PCGraphGrantsEdge))
		{
			return false;
		}
		if (this == o)
		{
			return true;
		}
		PCGraphGrantsEdge other = (PCGraphGrantsEdge) o;
		return source.equals(other.source)
			&& super.equalsAbstractPCGraphEdge(other);
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": " + super.toString();
	}
}
