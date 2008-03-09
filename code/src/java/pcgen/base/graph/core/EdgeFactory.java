/*
 * EdgeFactory.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Mar 6, 2008, 7:20:10 PM
 */
package pcgen.base.graph.core;

import java.util.Collection;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
 */
public final class EdgeFactory
{

    public static final <T> GraphEdge<T> createGraphEdge(T node1,
                                                            T node2)
    {
        return new DefaultGraphEdge<T>(node1, node2);
    }

    public static final <T> DirectionalGraphEdge<T> createDirectionalGraphEdge(T node1,
                                                                                  T node2)
    {
        return new DefaultDirectionalGraphEdge<T>(node1, node2);
    }

    public static final <T> HyperEdge<T> createHyperEdge(Collection<T> nodeArray)
    {
        return new DefaultHyperEdge<T>(nodeArray);
    }

    public static final <T> DirectionalHyperEdge<T> createDirectionalHyperEdge(Collection<T> na1,
                                                                                  Collection<T> na2)
    {
        return new DefaultDirectionalHyperEdge(na1, na2);
    }

}
