/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;

public abstract class AbstractPCGraphEdge extends ConcretePrereqObject
{

	/**
	 * One GraphNode to which this DefaultGraphEdge is connected. This GraphNode
	 * is not referred to as either the source or sink, as a DefaultGraphEdge is
	 * not a DirectionalHyperEdge.
	 */
	private final PrereqObject firstNode;

	/**
	 * The second GraphNode to which this DefaultGraphEdge is connected. This
	 * GraphNode is not referred to as either the source or sink, as a
	 * DefaultGraphEdge is not a DirectionalHyperEdge.
	 */
	private final PrereqObject secondNode;

	public AbstractPCGraphEdge(PrereqObject node1, PrereqObject node2)
	{
		if (node1 == null)
		{
			throw new IllegalArgumentException(
				"(First) GraphNode cannot be null");
		}
		if (node2 == null)
		{
			throw new IllegalArgumentException(
				"(Second) GraphNode cannot be null");
		}
		firstNode = node1;
		secondNode = node2;
	}

	/**
	 * Returns the node at the given index.
	 * 
	 * @see pcbase.graph.core.Edge#getNodeAt(int)
	 */
	public PrereqObject getNodeAt(int i)
	{
		if (i == 0)
		{
			return firstNode;
		}
		else if (i == 1)
		{
			return secondNode;
		}
		else
		{
			throw new IndexOutOfBoundsException(
				"GraphEdge does not contain a Node at " + i);
		}
	}

	/**
	 * Returns the Node attached to this DefaultGraphEdge opposite of the given
	 * Node. Returns null if the given Node is not adjacent (connected) to this
	 * DefaultGraphEdge.
	 * 
	 * @see pcbase.graph.core.GraphEdge#getOppositeNode(java.lang.Object)
	 */
	public PrereqObject getOppositeNode(PrereqObject gn)
	{
		if (firstNode.equals(gn))
		{
			return secondNode;
		}
		else if (secondNode.equals(gn))
		{
			return firstNode;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns the List of Adjacent (connected) Nodes to this DefaultGraphEdge.
	 * 
	 * Ownership of the returned List is transferred to the calling Object. No
	 * reference to the List Object is maintained by DefaultGraphEdge. However,
	 * the Nodes contained in the List are returned BY REFERENCE, and
	 * modification of the returned Nodes will modify the nodes contained within
	 * the DefaultGraphEdge.
	 * 
	 * @see pcbase.graph.core.Edge#getAdjacentNodes()
	 */
	public List<PrereqObject> getAdjacentNodes()
	{
		List<PrereqObject> l = new LinkedList<PrereqObject>();
		l.add(firstNode);
		l.add(secondNode);
		return l;
	}

	/**
	 * Returns true if the given Node is adjacent (connected) to this
	 * DefaultGraphEdge; false otherwise.
	 * 
	 * @see pcbase.graph.core.Edge#isAdjacentNode(java.lang.Object)
	 */
	public boolean isAdjacentNode(PrereqObject gn)
	{
		return firstNode.equals(gn) || secondNode.equals(gn);
	}

	/**
	 * Returns 2: the number of Nodes to which this DefaultGraphEdge is
	 * connected.
	 * 
	 * @see pcbase.graph.core.Edge#getAdjacentNodeCount()
	 */
	public int getAdjacentNodeCount()
	{
		return 2;
	}

	public int getNodeInterfaceType(PrereqObject node)
	{
		if (firstNode.equals(node))
		{
			return DirectionalEdge.SOURCE;
		}
		else if (secondNode.equals(node))
		{
			return DirectionalEdge.SINK;
		}
		return 0;
	}

	public List<PrereqObject> getSinkNodes()
	{
		return Collections.singletonList(secondNode);
	}

	public List<PrereqObject> getSourceNodes()
	{
		return Collections.singletonList(firstNode);
	}

	/*
	 * CONSIDER Use AssociationSupport? - Tom Parker 3/1/07
	 */
	private Map<AssociationKey<?>, Object> associationMap;

	protected void copyAssociationMapTo(AbstractPCGraphEdge other)
	{
		if (associationMap != null)
		{
			if (other.associationMap == null)
			{
				other.associationMap = new HashMap<AssociationKey<?>, Object>();
			}
			other.associationMap.putAll(associationMap);
		}
	}

	public <T> void setAssociation(AssociationKey<T> name, T value)
	{
		if (associationMap == null)
		{
			associationMap = new HashMap<AssociationKey<?>, Object>();
		}
		associationMap.put(name, value);
	}

	public <T> T getAssociation(AssociationKey<T> name)
	{
		return associationMap == null ? null : (T) associationMap.get(name);
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return associationMap == null ? null : new HashSet<AssociationKey<?>>(
			associationMap.keySet());
	}

	public boolean hasAssociations()
	{
		return associationMap != null && !associationMap.isEmpty();
	}

	public boolean equalsAbstractPCGraphEdge(AbstractPCGraphEdge other)
	{
		return this == other
			|| firstNode.equals(other.firstNode)
			&& secondNode.equals(other.secondNode)
			&& equalsPrereqObject(other)
			&& ((associationMap == null && other.associationMap == null) || (associationMap != null && associationMap
				.equals(other.associationMap)));
	}

	@Override
	public String toString()
	{
		return firstNode.toString() + "->" + secondNode.toString() + " "
			+ associationMap + " " + getPreReqList();
	}

	private HashMapToList<AssociationListKey<?>, Object> assocListMap = null;

	public <T> void addToAssociationList(AssociationListKey<T> key, T value)
	{
		if (assocListMap == null)
		{
			assocListMap = new HashMapToList<AssociationListKey<?>, Object>();
		}
		assocListMap.addToListFor(key, value);
	}

	public <T> List<T> getAssociationListFor(AssociationListKey<T> listKey)
	{
		return assocListMap == null ? null : (List<T>) assocListMap
			.getListFor(listKey);
	}

	public Collection<AssociationListKey<?>> getAssociationListKeys()
	{
		return assocListMap == null ? null : assocListMap.getKeySet();
	}
}
