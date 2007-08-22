package pcgen.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.persistence.lst.utils.TokenUtilities;

public class RuntimeListContext implements ListContext
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

	private final DoubleKeyMapToList<CDOMReference, LSTWriteable, AssociatedPrereqObject> masterList =
			new DoubleKeyMapToList<CDOMReference, LSTWriteable, AssociatedPrereqObject>();

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, LSTWriteable allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.OWNER, owner);
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		masterList.addToListFor(list, allowed, a);
		return a;
	}

	public Collection<CDOMReference> getMasterLists(
		Class<? extends CDOMList<?>> cl)
	{
		ArrayList<CDOMReference> list = new ArrayList<CDOMReference>();
		for (CDOMReference ref : masterList.getKeySet())
		{
			if (cl.equals(ref.getReferenceClass()))
			{
				list.add(ref);
			}
		}
		return list;
	}

	public <T extends CDOMObject> Changes<LSTWriteable> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		Set<LSTWriteable> added = masterList.getSecondaryKeySet(swl);
		Map<LSTWriteable, AssociatedPrereqObject> owned =
				new TreeMap<LSTWriteable, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
		for (LSTWriteable lw : added)
		{
			List<AssociatedPrereqObject> list = masterList.getListFor(swl, lw);
			for (AssociatedPrereqObject assoc : list)
			{
				if (owner.equals(assoc.getAssociation(AssociationKey.OWNER)))
				{
					owned.put(lw, assoc);
					break;
				}
			}
		}
		if (owned.isEmpty())
		{
			return null;
		}
		return new AssociatedCollectionChanges<LSTWriteable>(owned, null, false);
	}

	public <T extends CDOMObject> void clearMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> list)
	{
		for (LSTWriteable lw : masterList.getSecondaryKeySet(list))
		{
			List<AssociatedPrereqObject> assocList =
					masterList.getListFor(list, lw);
			for (AssociatedPrereqObject assoc : assocList)
			{
				if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
					&& tokenName.equals(assoc
						.getAssociation(AssociationKey.TOKEN)))
				{
					masterList.removeFromListFor(list, lw, assoc);
				}
			}
		}
	}

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, CDOMReference<T> allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		owner.putToList(list, allowed, a);
		return a;
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
		// TODO Auto-generated method stub

	}

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl,
		CDOMReference<T> ref)
	{
		// TODO Auto-generated method stub

	}

	public <T extends CDOMObject> Changes<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		if (owner.hasListMods(swl))
		{
			// TODO Deal with matching the token... :/
			return new ListChanges<T>(owner, null, swl, false);
		}
		return null;
	}
}
