/*
 * Copyright (c) Thomas Parker, 2004, 2005.
 * 
 * This file is part of RPG-MapGen
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
 * 
 * Created on Aug 27, 2004
 */
package pcgen.base.graph.visitor;

import pcgen.base.graph.core.Edge;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A Comparable component for use in Heap based functions on Graphs. Use of this
 * as the Object in a TreeMap will convert the TreeMap into a Heap sorted based
 * on the compareTo method of this class.
 */
public class GraphHeapComponent<N, ET extends Edge<N>> implements
		Comparable<GraphHeapComponent<N, ET>>
{

	/**
	 * The distance of this component from the reference point
	 */
	public final double distance;

	/**
	 * The edge to be traversed during the search
	 */
	public final ET edge;

	/**
	 * The node to be accessed during the search
	 */
	public final N node;

	/**
	 * This variable is required in order to guarantee (within even an
	 * unreasonable JVM) that two GraphHeapComponent objects conform to the
	 * "consistent with equals" contract of Comparable (and compareTo(Object o))
	 */
	private final int index;

	/**
	 * The master index used to create a unique identifier for each
	 * GraphHeapComponent object.
	 */
	private static int ghcIndex = Integer.MIN_VALUE;

	/**
	 * Creates a new GraphHeapComponent with the given depth, edge, and node.
	 * 
	 * @param d The depth for this GraphHeapComponent
	 * @param ge The edge for this GraphHeapComponent
	 * @param gn The node for this GraphHeapComponent
	 */
	public GraphHeapComponent(double d, ET ge, N gn)
	{
		super();
		if (ge == null)
		{
			throw new IllegalArgumentException("HyperEdge cannot be null");
		}
		if (gn == null)
		{
			throw new IllegalArgumentException("GraphNode cannot be null");
		}
		node = gn;
		edge = ge;
		distance = d;
		index = getIndex();
	}

	/**
	 * Returns a master index for this GraphHeapComponent.  This is used to
	 * create a unique identifier for each GraphHeapComponent object.
	 */
	private static synchronized int getIndex()
	{
		return ghcIndex++;
	}

	/**
	 * Compares the given GraphHeapComponents to this GraphHeapComponent.
	 * Returns -1 if the given GraphHeapComponent is less than this 
	 * GraphHeapComponent, 0 if the given GraphHeapComponent is equal to
	 * this GraphHeapComponent, and 1 if the given GraphHeapComponent is
	 * greater than this GraphHeapComponent.
	 * 
	 * @return an integer indicating if the given GraphHeapComponent is 
	 * less than, equal to, or greater than this GraphHeapComponent.
	 */
	public int compareTo(GraphHeapComponent<N, ET> secondGHC)
	{
		if (this.distance > secondGHC.distance)
		{
			return 1;
		}
		else if (this.distance < secondGHC.distance)
		{
			return -1;
		}
		// diff == 0.0
		return this.index - secondGHC.index;
	}
}