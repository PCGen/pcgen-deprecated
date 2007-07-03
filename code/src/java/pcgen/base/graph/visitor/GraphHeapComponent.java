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
package pcgen.base.graph.visitor;

import java.util.Comparator;

import pcgen.base.graph.core.Edge;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A Comparable component for use in Heap based functions on Graphs. Use of this
 * as the Object in a TreeMap will convert the TreeMap into a Heap sorted based
 * on the compareTo method of this class.
 */
public class GraphHeapComponent<N, ET extends Edge<N>>
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
	 * Creates a new GraphHeapComponent with the given depth, edge, and node.
	 * 
	 * @param d
	 *            The depth for this GraphHeapComponent
	 * @param ge
	 *            The edge for this GraphHeapComponent
	 * @param gn
	 *            The node for this GraphHeapComponent
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
	}

	/**
	 * Returns a String representation of this GraphHeapComponent
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "GraphHeapComponent: [" + node + "," + edge + "," + distance
			+ "]";
	}

	/*
	 * DISTANCE_COMPARATOR is created as a separate Comparator rather than
	 * making GraphHeapComponent implement the Comparable interface, in order to
	 * have a Comparator that is not consistent with equals. (If one made
	 * GraphHeapComponent Comparable and did not have a consistent-with-equals
	 * compareTo method, then storing a GraphHeapComponent into a TreeMap or
	 * certain other collections would be impossible or frought with confusion.)
	 * This is separate to avoid that confusion. It should not be integrated
	 * into GraphHeapComponent as a compareTo method.
	 */

	/**
	 * This Distance Comparator is used to compare the depth of two
	 * GraphHeapComponents. Note that this Comparator is NOT CONSISTENT WITH
	 * EQUALS, and therefore should NOT be used with a TreeSet or other Class
	 * which expects a Comparator to be consistent with equals. This Comparator
	 * can safely be used with a PriorityQueue, however, which does not assume
	 * the Comparator used is consistent with equals.
	 */
	public static Comparator<GraphHeapComponent<?, ?>> DISTANCE_COMPARATOR =
			new Comparator<GraphHeapComponent<?, ?>>()
			{
				/**
				 * Compares the given GraphHeapComponents. Returns -1 if the
				 * distance of the first GraphHeapComponent is less than the
				 * distance of the second GraphHeapComponent, 0 if the given
				 * GraphHeapComponents have equal distance, and 1 if the
				 * distance of the first GraphHeapComponent is greater than the
				 * distance of the second GraphHeapComponent. The calculation is
				 * based *solely* on distance; thus this compare method is not
				 * consistent with equals.
				 * 
				 * @return an integer indicating if the first GraphHeapComponent
				 *         is less than, equal to, or greater than the second
				 *         GraphHeapComponent.
				 */
				public int compare(GraphHeapComponent<?, ?> firstGHC,
					GraphHeapComponent<?, ?> secondGHC)
				{
					if (firstGHC.distance > secondGHC.distance)
					{
						return 1;
					}
					else if (firstGHC.distance < secondGHC.distance)
					{
						return -1;
					}
					return 0;
				}
			};
}