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
package pcgen.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.lst.utils.TokenUtilities;

public class EditorGraphContext implements GraphContext
{

	private DoubleKeyMapToList<URI, PrereqObject, String> globalRemoveSet =
			new DoubleKeyMapToList<URI, PrereqObject, String>();

	private final PCGenGraph graph;

	private URI sourceURI;

	private URI extractURI;

	public EditorGraphContext(PCGenGraph pgg)
	{
		graph = pgg;
	}

	public URI setSourceURI(URI source)
	{
		URI oldURI = sourceURI;
		sourceURI = source;
		return oldURI;
	}

	public URI setExtractURI(URI uri)
	{
		URI oldURI = extractURI;
		extractURI = uri;
		return oldURI;
	}

	private PrereqObject getInternalizedNode(PrereqObject pro)
	{
		PrereqObject node = graph.getInternalizedNode(pro);
		if (node == null)
		{
			node = pro;
		}
		return node;
	}

	/*
	 * TODO This is basically only used for Aggregator cleanup - change Agg
	 * cleanup method?
	 */
	public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
		CDOMObject obj)
	{
		Set<PCGraphEdge> set = new HashSet<PCGraphEdge>();
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList != null)
		{
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (extractURI != null)
				{
					if (!extractURI.equals(edge
						.getAssociation(AssociationKey.SOURCE_URI)))
					{
						continue;
					}
				}
				if (edge.getSourceToken().equals(tokenName))
				{
					for (PrereqObject node : edge.getAdjacentNodes())
					{
						if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK)
						{
							set.add(edge);
							break;
						}
					}
				}
			}
		}
		return set;
	}

	public PCGraphGrantsEdge grant(String sourceToken, PrereqObject obj,
		PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(obj, node, sourceToken);
		edge.setAssociation(AssociationKey.SOURCE_URI, sourceURI);
		graph.addEdge(edge);
		return edge;
	}

	public void remove(String sourceToken, CDOMObject obj, PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(obj, node, sourceToken);
		edge.setAssociation(AssociationKey.SOURCE_URI, sourceURI);
		edge.setAssociation(AssociationKey.RETIRED_BY, sourceURI);
		graph.addEdge(edge);
	}

	public void removeAll(String tokenName, PrereqObject obj)
	{
		globalRemoveSet.addToListFor(sourceURI, obj, tokenName);
	}

	public <T extends PrereqObject & LSTWriteable> GraphChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name)
	{
		return new EditorGraphChanges<T>(tokenName, pct, name);
	}

	public <T extends PrereqObject, A> CDOMEdgeReference getEdgeReference(
		PObject parent, Class<T> childClass, String childName,
		Class<A> assocClass)
	{
		if (parent == null)
		{
			throw new IllegalArgumentException("Choice Parent cannot be null");
		}
		if (childClass == null)
		{
			throw new IllegalArgumentException("Child Class cannot be null");
		}
		if (childName == null)
		{
			throw new IllegalArgumentException("Child Name cannot be null");
		}
		if (assocClass == null)
		{
			throw new IllegalArgumentException(
				"Association Class cannot be null");
		}
		if (childClass.equals(ChoiceSet.class))
		{
			/*
			 * TODO This choice set needs to be stored and validated as existing &
			 * having the appropriate assocClass after LST load is complete
			 */
			return new CDOMEdgeReference(parent, assocClass, childName);
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	public EquipmentHead getEquipmentHead(Equipment eq, int index)
	{
		EquipmentHead head = getEquipmentHeadReference(eq, index);
		if (head == null)
		{
			// Isn't there already, so create new
			head = new EquipmentHead(eq, index);
			grant(Constants.VT_EQ_HEAD, eq, head);
		}
		return head;
	}

	public EquipmentHead getEquipmentHeadReference(Equipment eq, int index)
	{
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(eq);
		if (outwardEdgeList == null)
		{
			return null;
		}
		for (PCGraphEdge edge : outwardEdgeList)
		{
			if (!edge.getSourceToken().equals(Constants.VT_EQ_HEAD))
			{
				continue;
			}
			for (PrereqObject node : edge.getAdjacentNodes())
			{
				if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
				{
					continue;
				}
				if (EquipmentHead.class.isAssignableFrom(node.getClass()))
				{
					EquipmentHead head = (EquipmentHead) node;
					if (head.getHeadIndex() == index)
					{
						return head;
					}
				}
			}
		}
		return null;
	}

	public class EditorGraphChanges<T> implements GraphChanges<T>
	{

		private final String token;

		private final CDOMObject source;

		private final Class<T> childClass;

		public EditorGraphChanges(String tokenName, CDOMObject cdo,
			Class<T> name)
		{
			token = tokenName;
			childClass = name;
			source = cdo;
		}

		public Collection<LSTWriteable> getAdded()
		{
			Set<LSTWriteable> set =
					new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
			if (outwardEdgeList == null)
			{
				return set;
			}
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (!edge.getSourceToken().equals(token))
				{
					continue;
				}
				for (PrereqObject node : edge.getAdjacentNodes())
				{
					if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
					{
						continue;
					}
					if (edge.getAssociation(AssociationKey.RETIRED_BY) != null)
					{
						continue;
					}
					if (childClass.isAssignableFrom(node.getClass()))
					{
						// TODO Can the edge actually return an LSTWriteable?
						set.add((LSTWriteable) node);
						break;
					}
					else if (node instanceof CDOMReference)
					{
						CDOMReference<?> cdr = (CDOMReference) node;
						if (cdr.getReferenceClass().equals(childClass))
						{
							set.add((LSTWriteable) node);
							break;
						}
					}
				}
			}
			return set;
		}

		public AssociatedPrereqObject getAddedAssociation(LSTWriteable added)
		{
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (added.equals(edge.getNodeAt(1)))
				{
					return edge;
				}
			}
			return null;
		}

		public Collection<LSTWriteable> getRemoved()
		{
			Set<LSTWriteable> set =
					new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
			if (outwardEdgeList == null)
			{
				return set;
			}
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (!edge.getSourceToken().equals(token))
				{
					continue;
				}
				for (PrereqObject node : edge.getAdjacentNodes())
				{
					if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
					{
						continue;
					}
					if (edge.getAssociation(AssociationKey.RETIRED_BY) == null)
					{
						continue;
					}
					if (childClass.isAssignableFrom(node.getClass()))
					{
						// TODO Can the edge actually return an LSTWriteable?
						set.add((LSTWriteable) node);
						break;
					}
					else if (node instanceof CDOMReference)
					{
						CDOMReference<?> cdr = (CDOMReference) node;
						if (cdr.getReferenceClass().equals(childClass))
						{
							set.add((LSTWriteable) node);
							break;
						}
					}
				}
			}
			return set;
		}

		public AssociatedPrereqObject getRemovedAssociation(LSTWriteable added)
		{
			return null;
		}

		public boolean hasAddedItems()
		{
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
			if (outwardEdgeList == null)
			{
				return false;
			}
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (!edge.getSourceToken().equals(token))
				{
					continue;
				}
				if (edge.getAssociation(AssociationKey.RETIRED_BY) != null)
				{
					continue;
				}
				for (PrereqObject node : edge.getAdjacentNodes())
				{
					if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
					{
						continue;
					}
					if (childClass.isAssignableFrom(node.getClass())
						|| (node instanceof CDOMReference && ((CDOMReference) node)
							.getReferenceClass().equals(childClass)))
					{
						return true;
					}
				}
			}
			return false;
		}

		public boolean hasRemovedItems()
		{
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
			if (outwardEdgeList == null)
			{
				return false;
			}
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (!edge.getSourceToken().equals(token))
				{
					continue;
				}
				if (edge.getAssociation(AssociationKey.RETIRED_BY) == null)
				{
					continue;
				}
				for (PrereqObject node : edge.getAdjacentNodes())
				{
					if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
					{
						continue;
					}
					if (childClass.isAssignableFrom(node.getClass())
						|| (node instanceof CDOMReference && ((CDOMReference) node)
							.getReferenceClass().equals(childClass)))
					{
						return true;
					}
				}
			}
			return false;
		}

		public boolean includesGlobalClear()
		{
			return globalRemoveSet.containsInList(extractURI, source, token);
		}
	}
}
