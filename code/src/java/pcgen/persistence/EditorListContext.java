package pcgen.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.lang.UnreachableError;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.persistence.lst.utils.TokenUtilities;

public class EditorListContext implements ListContext
{
	private URI sourceURI;

	private URI extractURI;

	public URI getExtractURI()
	{
		return extractURI;
	}

	public void setExtractURI(URI extractURI)
	{
		this.extractURI = extractURI;
	}

	public URI getSourceURI()
	{
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI)
	{
		this.sourceURI = sourceURI;
	}

	private static class ListOwner
	{
		public final CDOMReference<? extends CDOMList<?>> ref;
		public final CDOMObject owner;

		public ListOwner(CDOMReference<? extends CDOMList<?>> reference,
			CDOMObject cdo)
		{
			ref = reference;
			owner = cdo;
		}

		@Override
		public int hashCode()
		{
			return owner.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof ListOwner)
			{
				ListOwner other = (ListOwner) o;
				return owner.equals(other.owner) && ref.equals(other.ref);
			}
			return false;
		}
	}

	private TripleKeyMap<URI, ListOwner, LSTWriteable, AssociatedPrereqObject> positiveMasterMap =
			new TripleKeyMap<URI, ListOwner, LSTWriteable, AssociatedPrereqObject>();

	private DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>> masterClearSet =
			new DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>>();

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, LSTWriteable allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.OWNER, owner);
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		positiveMasterMap
			.put(sourceURI, new ListOwner(list, owner), allowed, a);
		return a;
	}

	public Collection<CDOMReference> getMasterLists(
		Class<? extends CDOMList<?>> cl)
	{
		ArrayList<CDOMReference> list = new ArrayList<CDOMReference>();
		Set<ListOwner> set = positiveMasterMap.getSecondaryKeySet(extractURI);
		if (set != null)
		{
			for (ListOwner lo : set)
			{
				if (cl.equals(lo.ref.getReferenceClass()))
				{
					list.add(lo.ref);
				}
			}
		}
		return list;
	}

	public <T extends CDOMObject> Changes<LSTWriteable> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		Map<LSTWriteable, AssociatedPrereqObject> map =
				new TreeMap<LSTWriteable, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
		ListOwner lo = new ListOwner(swl, owner);
		Set<LSTWriteable> added =
				positiveMasterMap.getTertiaryKeySet(extractURI, lo);
		for (LSTWriteable lw : added)
		{
			map.put(lw, positiveMasterMap.get(extractURI, lo, lw));
		}
		return new AssociatedCollectionChanges<LSTWriteable>(map, null,
			masterClearSet.containsInList(extractURI, owner, swl));
	}

	public <T extends CDOMObject> void clearMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> list)
	{
		masterClearSet.addToListFor(sourceURI, owner, list);
	}

	private DoubleKeyMap<CDOMObject, URI, CDOMObject> positiveMap =
			new DoubleKeyMap<CDOMObject, URI, CDOMObject>();

	private DoubleKeyMap<CDOMObject, URI, CDOMObject> negativeMap =
			new DoubleKeyMap<CDOMObject, URI, CDOMObject>();

	private DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>> globalClearSet =
			new DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>>();

	private CDOMObject getPositive(URI source, CDOMObject cdo)
	{
		CDOMObject positive = positiveMap.get(cdo, source);
		if (positive == null)
		{
			try
			{
				positive = cdo.getClass().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a zero argument constructor", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a public zero argument constructor",
					e);
			}
			positiveMap.put(cdo, source, positive);
		}
		return positive;
	}

	private CDOMObject getNegative(URI source, CDOMObject cdo)
	{
		CDOMObject negative = negativeMap.get(cdo, source);
		if (negative == null)
		{
			try
			{
				negative = cdo.getClass().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a zero argument constructor", e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnreachableError(
					"CDOM Objects must have a public zero argument constructor",
					e);
			}
			negativeMap.put(cdo, source, negative);
		}
		return negative;
	}

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, CDOMReference<T> allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		CDOMObject pos = getPositive(sourceURI, owner);
		pos.putToList(list, allowed, a);
		return a;
	}

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> list,
		CDOMReference<T> ref)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		CDOMObject pos = getNegative(sourceURI, owner);
		pos.putToList(list, ref, a);
	}

	public Collection<CDOMReference<CDOMList<? extends CDOMObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl)
	{
		ArrayList<CDOMReference<CDOMList<? extends CDOMObject>>> list =
				new ArrayList<CDOMReference<CDOMList<? extends CDOMObject>>>();
		for (CDOMReference<CDOMList<? extends CDOMObject>> ref : owner
			.getModifiedLists())
		{
			if (cl.equals(ref.getReferenceClass()))
			{
				list.add(ref);
			}
		}
		return list;
	}

	public <T extends CDOMObject> void removeAllFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl)
	{
		globalClearSet.addToListFor(sourceURI, owner, swl);
	}

	public <T extends CDOMObject> Changes<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		return new ListChanges<T>(getPositive(extractURI, owner), getNegative(
			extractURI, owner), swl, globalClearSet.containsInList(extractURI,
			owner, swl));
	}
}
