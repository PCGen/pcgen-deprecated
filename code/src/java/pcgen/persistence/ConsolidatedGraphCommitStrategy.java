package pcgen.persistence;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.graph.core.DirectionalEdge;
import pcgen.base.io.FileLocationFactory;
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
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.persistence.lst.utils.TokenUtilities;

public class ConsolidatedGraphCommitStrategy implements GraphCommitStrategy
{
	private URI sourceURI;

	private URI extractURI;

	private final PCGenGraph graph;

	private FileLocationFactory locFac = new FileLocationFactory();

	public ConsolidatedGraphCommitStrategy(PCGenGraph pgg)
	{
		graph = pgg;
	}

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI uri)
	{
		extractURI = uri;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI uri)
	{
		sourceURI = uri;
		locFac.newFile();
	}

	public void setLine(int line)
	{
		locFac.setLine(line);
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

	public PCGraphGrantsEdge grant(String sourceToken, CDOMObject obj,
		PrereqObject pro)
	{
		PrereqObject node = getInternalizedNode(pro);
		graph.addNode(node);
		PCGraphGrantsEdge edge = new PCGraphGrantsEdge(obj, node, sourceToken);
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

	public void removeAll(String tokenName, CDOMObject obj)
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

	public <T extends PrereqObject & LSTWriteable> AssociatedChanges<T> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name)
	{
		return new GraphChangesFacade<T>(tokenName, pct, name);
	}

	public Set<PCGraphEdge> getChildLinksFromToken(String tokenName,
		CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private class GraphChangesFacade<T extends PrereqObject & LSTWriteable>
			implements AssociatedChanges<T>
	{

		private final String token;

		private final CDOMObject source;

		private final Class<T> childClass;

		public GraphChangesFacade(String tokenName, CDOMObject cdo,
			Class<T> name)
		{
			token = tokenName;
			childClass = name;
			source = cdo;
		}

		public Collection<LSTWriteable> getAdded()
		{
			Collection<LSTWriteable> coll =
					new WeightedCollection<LSTWriteable>(
						TokenUtilities.WRITEABLE_SORTER);
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
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
			List<PCGraphEdge> outwardEdgeList =
					graph.getOutwardEdgeList(source);
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
}
