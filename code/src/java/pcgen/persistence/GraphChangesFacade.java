package pcgen.persistence;

import java.util.Collection;
import java.util.List;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.base.util.WeightedCollection;
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
		Collection<LSTWriteable> coll =
				new WeightedCollection<LSTWriteable>(
					TokenUtilities.WRITEABLE_SORTER);
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(source);
		if (outwardEdgeList == null)
		{
			return coll;
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
					// TODO Can the edge actually return an LSTWriteable?
					coll.add((LSTWriteable) node);
					break;
				}
				else if (node instanceof CDOMReference)
				{
					CDOMReference<?> cdr = (CDOMReference) node;
					if (cdr.getReferenceClass().equals(childClass))
					{
						coll.add((LSTWriteable) node);
						break;
					}
				}
			}
		}
		return coll;
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations()
	{
		TreeMapToList<LSTWriteable, AssociatedPrereqObject> coll =
				new TreeMapToList<LSTWriteable, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
		List<PCGraphEdge> outwardEdgeList = graph.getOutwardEdgeList(source);
		if (outwardEdgeList == null)
		{
			return coll;
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
					// TODO Can the edge actually return an LSTWriteable?
					coll.addToListFor((LSTWriteable) node, edge);
					break;
				}
				else if (node instanceof CDOMReference)
				{
					CDOMReference<?> cdr = (CDOMReference) node;
					if (cdr.getReferenceClass().equals(childClass))
					{
						coll.addToListFor((LSTWriteable) node, edge);
						break;
					}
				}
			}
		}
		return coll;
	}

	public Collection<LSTWriteable> getRemoved()
	{
		return null;
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations()
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
