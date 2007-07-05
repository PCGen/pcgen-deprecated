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

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.cdom.base.CDOMEdgeReference;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;

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
		/*
		 * TODO In order to allow certain behavior, such as FEAT:Foo|Foo
		 * (awarding a feat twice from the same object), one MUST store into the
		 * edge (1) The source [already done], (2) The source line (protects
		 * against MODs and multiple tokens doing the grant) (3) The source
		 * column (protects against a single line having multiple tokens doing
		 * the grant) and (4) The request count [the count of objects granted by
		 * the token located in the given source on the given line at the given
		 * column].
		 * 
		 * By doing this method (of making the instance local only to the token
		 * instance at a given line and column, one can keep thread safe loading
		 * (order independent)) while allowing multiple semi-identical edge
		 * instances.
		 * 
		 * Actually the question should be: What is the value of line and column
		 * in this case - can't the source request count simple be based on the
		 * URL and still allow thread-safe loading of different files by
		 * different loaders in parallel?
		 */
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
}
