package pcgen.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.persistence.lst.utils.TokenUtilities;

public class GraphChangesFacade<T extends PrereqObject & LSTWriteable>
		implements GraphChanges<T>
{

	private final PCGenGraph graph;

	private final String token;

	private final CDOMObject source;

	private final Class<T> childClass;

	public GraphChangesFacade(PCGenGraph gr, String tokenName, CDOMObject cdo,
		Class<T> name)
	{
		graph = gr;
		token = tokenName;
		childClass = name;
		source = cdo;
	}

	public Collection<LSTWriteable> getAdded()
	{
		Set<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(source);
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
				if (childClass.isAssignableFrom(node.getClass()))
				{
					//TODO Can the edge actually return an LSTWriteable?
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
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(source);
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
		return null;
	}

	public AssociatedPrereqObject getRemovedAssociation(LSTWriteable added)
	{
		return null;
	}

	public boolean hasAddedItems()
	{
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(source);
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
		return false;
	}

	public boolean includesGlobalClear()
	{
		return false;
	}
}
