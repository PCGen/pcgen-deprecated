package pcgen.rules.context;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.io.FileLocationFactory;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.rules.persistence.TokenUtilities;

public class ConsolidatedGraphCommitStrategy implements GraphCommitStrategy
{
	private URI sourceURI;

	private URI extractURI;

	private final DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject> grants;
	
	private FileLocationFactory locFac = new FileLocationFactory();

	public ConsolidatedGraphCommitStrategy(DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject> thing)
	{
		if (thing == null)
		{
			grants = new DoubleKeyMapToList<CDOMObject, CDOMReference<?>, AssociatedPrereqObject>();
		}
		else
		{
			grants = thing;
		}
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
	/*
	 * TODO need to translate this over to give in ObjectContext...??
	 */
//
//	private PrereqObject getInternalizedNode(PrereqObject pro)
//	{
//		PrereqObject node = graph.getInternalizedNode(pro);
//		if (node == null)
//		{
//			node = pro;
//		}
//		return node;
//	}

	public <T extends CDOMObject> AssociatedPrereqObject grant(String sourceToken, CDOMObject obj,
			CDOMReference<T> pro)
	{
		AssociatedPrereqObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.TOKEN, sourceToken);
		grants.addToListFor(obj, pro, a);
		return a;
	}

	public <T extends CDOMObject> void remove(String tokenName, CDOMObject obj, CDOMReference<T> child)
	{
		List<AssociatedPrereqObject> assoc = grants.getListFor(obj, child);
		if (assoc != null)
		{
			for (AssociatedPrereqObject apo : assoc)
			{
				if (tokenName.equals(apo.getAssociation(AssociationKey.TOKEN)))
				{
					grants.removeFromListFor(obj, child, apo);
				}
			}
		}
	}

	public void removeAll(String tokenName, CDOMObject obj)
	{
		Set<CDOMReference<?>> targets = grants.getSecondaryKeySet(obj);
		if (targets != null)
		{
			for (CDOMReference<?> target : targets)
			{
				for (AssociatedPrereqObject apo : grants.getListFor(obj, target))
				{
					if (tokenName.equals(apo.getAssociation(AssociationKey.TOKEN)))
					{
						grants.removeFromListFor(obj, target, apo);
					}
				}
			}
		}
	}

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesFromToken(
		String tokenName, CDOMObject pct, Class<T> name)
	{
		return new GraphChangesFacade<T>(tokenName, pct, name);
	}

	private class GraphChangesFacade<T extends CDOMObject>
			implements AssociatedChanges<CDOMReference<T>>
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

		public Collection<CDOMReference<T>> getAdded()
		{
			Collection<CDOMReference<T>> coll =
					new WeightedCollection<CDOMReference<T>>(
							TokenUtilities.REFERENCE_SORTER);
			Set<CDOMReference<?>> targets = grants.getSecondaryKeySet(source);
			if (targets == null)
			{
				return coll;
			}
			for (CDOMReference<?> target : targets)
			{
				if (!target.getReferenceClass().equals(childClass))
				{
					continue;
				}
				for (AssociatedPrereqObject apo : grants.getListFor(source, target))
				{
					if (token.equals(apo.getAssociation(AssociationKey.TOKEN)))
					{
						coll.add((CDOMReference<T>) target);
						break;
					}
				}
			}
			return coll;
		}

		public MapToList<CDOMReference<T>, AssociatedPrereqObject> getAddedAssociations()
		{
			TreeMapToList<CDOMReference<T>, AssociatedPrereqObject> coll =
					new TreeMapToList<CDOMReference<T>, AssociatedPrereqObject>(
							TokenUtilities.REFERENCE_SORTER);
			Set<CDOMReference<?>> targets = grants.getSecondaryKeySet(source);
			if (targets == null)
			{
				return coll;
			}
			for (CDOMReference<?> target : targets)
			{
				if (!target.getReferenceClass().equals(childClass))
				{
					continue;
				}
				for (AssociatedPrereqObject apo : grants.getListFor(source, target))
				{
					if (token.equals(apo.getAssociation(AssociationKey.TOKEN)))
					{
						coll.addToListFor((CDOMReference<T>) target, apo);
					}
				}
			}
			return coll;
		}

		public Collection<CDOMReference<T>> getRemoved()
		{
			return null;
		}

		public MapToList<CDOMReference<T>, AssociatedPrereqObject> getRemovedAssociations()
		{
			return null;
		}

		public boolean hasAddedItems()
		{
			Set<CDOMReference<?>> targets = grants.getSecondaryKeySet(source);
			if (targets == null)
			{
				return false;
			}
			for (CDOMReference<?> target : targets)
			{
				if (!target.getReferenceClass().equals(childClass))
				{
					continue;
				}
				for (AssociatedPrereqObject apo : grants.getListFor(source, target))
				{
					if (token.equals(apo.getAssociation(AssociationKey.TOKEN)))
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
