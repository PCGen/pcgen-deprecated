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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.graph.core.DirectionalEdge;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Slot;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.util.Logging;

public class GraphContext
{

	private final PCGenGraph graph;

	private URI sourceURI;

	private URI extractURI;

	public GraphContext(PCGenGraph pgg)
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

	public <S extends PrereqObject> Slot<S> addSlot(String tokenName,
		CDOMObject pro, Class<S> slotClass, Formula count)
	{
		Slot<S> slot = new Slot<S>(slotClass, count);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(pro, slot, tokenName);
		edge.setAssociation(AssociationKey.SOURCE_URI, sourceURI);
		if (!graph.addEdge(edge))
		{
			Logging.errorPrint("Failed Add for Slot " + tokenName + " " + pro
				+ " " + slotClass + " " + count);
		}
		return slot;
	}

	/*
	 * TODO This is basically only used in NaturalAttacks - probably remove??
	 */
	public Set<PCGraphEdge> getChildLinks(CDOMObject obj,
		Class<? extends PrereqObject> cl)
	{
		/*
		 * CONSIDER At one point, I made this a TreeSet - what was I attempting
		 * to do by having order? That requires PCGraphEdge implement
		 * Comparable... ??
		 */
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
				for (PrereqObject node : edge.getAdjacentNodes())
				{
					if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK
					/*
					 * FIXME If the child is a reference, this isn't true
					 */
					&& node.getClass().equals(cl))
					{
						set.add(edge);
						break;
					}
				}
			}
		}
		return set;
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

	public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
		CDOMObject obj, Class<? extends PrereqObject> cl)
	{
		Set<PCGraphEdge> set = new HashSet<PCGraphEdge>();
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList == null)
		{
			return set;
		}
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
			if (!edge.getSourceToken().equals(tokenName))
			{
				continue;
			}
			for (PrereqObject node : edge.getAdjacentNodes())
			{
				if (edge.getNodeInterfaceType(node) != DirectionalEdge.SINK)
				{
					continue;
				}
				if (cl.isAssignableFrom(node.getClass())
					|| (node instanceof CDOMReference && ((CDOMReference) node)
						.getReferenceClass().equals(cl)))
				{
					set.add(edge);
					break;
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

	public void remove(String tokenName, CDOMObject obj, PrereqObject child)
	{
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList != null)
		{
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (edge.getSourceToken().equals(tokenName))
				{
					for (PrereqObject node : edge.getAdjacentNodes())
					{
						if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK
							&& node.equals(child))
						{
							// CONSIDER Clean up parent/child if no remaining
							// links?
							graph.removeEdge(edge);
							break;
						}
					}
				}
			}
		}
	}

	public void removeAll(String tokenName, CDOMObject obj,
		Class<? extends PrereqObject> cl)
	{
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList != null)
		{
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (edge.getSourceToken().equals(tokenName))
				{
					for (PrereqObject node : edge.getAdjacentNodes())
					{
						if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK
							&& node.getClass().equals(cl))
						{
							graph.removeEdge(edge);
							// CONSIDER Clean up parent/child if no remaining
							// links?
							break;
						}
					}
				}
			}
		}
	}

	public void removeAll(String tokenName, PrereqObject obj)
	{
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList != null)
		{
			for (PCGraphEdge edge : outwardEdgeList)
			{
				if (edge.getSourceToken().equals(tokenName))
				{
					graph.removeEdge(edge);
				}
			}
		}
	}

	public <T extends PrereqObject & LSTWriteable> GraphChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name)
	{
		return new GraphChangesFacade<T>(graph, tokenName, pct, name);
	}
}
