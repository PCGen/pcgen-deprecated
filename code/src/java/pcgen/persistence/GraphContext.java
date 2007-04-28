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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.graph.core.DirectionalEdge;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Slot;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphActivationEdge;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphHoldsEdge;
import pcgen.cdom.inst.Aggregator;

public class GraphContext
{

	private final PCGenGraph graph;

	public GraphContext(PCGenGraph pgg)
	{
		graph = pgg;
	}

	public PCGraphGrantsEdge linkObjectIntoGraph(String sourceToken,
		CDOMObject obj, PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(obj, node, sourceToken);
		graph.addEdge(edge);
		return edge;
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
	 * FIXME Currently this allows PrereqObject as the 2nd arg - should be
	 * CDOMObject or CDOMReference - can this be restricted?
	 */
	public PCGraphAllowsEdge linkAllowIntoGraph(String sourceToken,
		PrereqObject obj, PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphAllowsEdge edge = new PCGraphAllowsEdge(obj, node, sourceToken);
		graph.addEdge(edge);
		return edge;
	}

	public PCGraphHoldsEdge linkHoldsIntoGraph(String sourceToken,
		PrereqObject obj, PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphHoldsEdge edge = new PCGraphHoldsEdge(obj, node, sourceToken);
		graph.addEdge(edge);
		return edge;
	}

	public PCGraphActivationEdge linkActivationIntoGraph(String sourceToken,
		PrereqObject obj, PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphActivationEdge edge =
				new PCGraphActivationEdge(obj, node, sourceToken);
		graph.addEdge(edge);
		return edge;
	}

	public <S extends PrereqObject> void addSlotIntoGraph(String tokenName,
		CDOMObject pro, Class<S> slotClass)
	{
		PCGraphGrantsEdge edge =
				new PCGraphGrantsEdge(pro, new Slot<S>(slotClass), tokenName);
		graph.addEdge(edge);
	}

	public <S extends PrereqObject> Slot<S> addSlotIntoGraph(String tokenName,
		CDOMObject pro, Class<S> slotClass, Formula count)
	{
		Slot<S> slot = new Slot<S>(slotClass, count);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(pro, slot, tokenName);
		graph.addEdge(edge);
		return slot;
	}

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

	public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
		CDOMObject obj)
	{
		Set<PCGraphEdge> set = new HashSet<PCGraphEdge>();
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(obj);
		if (outwardEdgeList != null)
		{
			for (PCGraphEdge edge : outwardEdgeList)
			{
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

	public void unlinkChildNodesOfClass(String tokenName, CDOMObject obj,
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
							break;
						}
					}
				}
			}
		}
	}

	public void unlinkChildNode(String tokenName, CDOMObject obj,
		PrereqObject child)
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
							graph.removeEdge(edge);
							break;
						}
					}
				}
			}
		}
	}

	public void unlinkChildNodes(String tokenName, PrereqObject obj)
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
						if (edge.getNodeInterfaceType(node) == DirectionalEdge.SINK)
						{
							graph.removeEdge(edge);
							break;
						}
					}
				}
			}
		}
	}

	public void unlinkParentNodes(String tokenName, PrereqObject pro)
	{
		List<PCGraphEdge> inwardEdgeList = graph.getInwardEdgeList(pro);
		if (inwardEdgeList != null)
		{
			for (PCGraphEdge edge : inwardEdgeList)
			{
				if (edge.getSourceToken().equals(tokenName))
				{
					for (PrereqObject node : edge.getAdjacentNodes())
					{
						if (edge.getNodeInterfaceType(node) == DirectionalEdge.SOURCE)
						{
							graph.removeEdge(edge);
							break;
						}
					}
				}
			}
		}
	}

	public Set<PCGraphEdge> getParentLinksFromToken(String tokenName,
		CDOMObject obj, Class<? extends PrereqObject> cl)
	{
		Set<PCGraphEdge> set = new HashSet<PCGraphEdge>();
		List<PCGraphEdge> inwardEdgeList = graph.getInwardEdgeList(obj);
		if (inwardEdgeList == null)
		{
			return set;
		}
		for (PCGraphEdge edge : inwardEdgeList)
		{
			if (!edge.getSourceToken().equals(tokenName))
			{
				continue;
			}
			for (PrereqObject node : edge.getAdjacentNodes())
			{
				if (edge.getNodeInterfaceType(node) == DirectionalEdge.SOURCE)
				{
					if (cl.isAssignableFrom(node.getClass())
						|| (node instanceof CDOMReference && ((CDOMReference) node)
							.getReferenceClass().equals(cl)))
					{
						set.add(edge);
						break;
					}
				}
			}
		}
		return set;
	}

	public void deleteAggregator(String tokenName, Aggregator agg)
	{
		unlinkChildNodes(tokenName, agg);
		unlinkParentNodes(tokenName, agg);
		graph.removeNode(agg);
	}

}
