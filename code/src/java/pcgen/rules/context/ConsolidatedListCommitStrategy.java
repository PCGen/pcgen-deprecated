package pcgen.rules.context;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.SimpleAssociatedObject;
import pcgen.rules.persistence.TokenUtilities;

public class ConsolidatedListCommitStrategy implements ListCommitStrategy
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

	public AssociatedPrereqObject addToMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<?>> list,
		LSTWriteable allowed)
	{
		SimpleAssociatedObject a = new SimpleAssociatedObject();
		a.setAssociation(AssociationKey.OWNER, owner);
		a.setAssociation(AssociationKey.TOKEN, tokenName);
		masterList.addToListFor(list, allowed, a);
		return a;
	}

	public Changes<CDOMReference> getMasterListChanges(String tokenName,
		CDOMObject owner, Class<? extends CDOMList<?>> cl)
	{
		ArrayList<CDOMReference> list = new ArrayList<CDOMReference>();
		LIST: for (CDOMReference<? extends CDOMList<?>> ref : masterList
			.getKeySet())
		{
			if (!cl.equals(ref.getReferenceClass()))
			{
				continue;
			}
			for (LSTWriteable allowed : masterList.getSecondaryKeySet(ref))
			{
				for (AssociatedPrereqObject assoc : masterList.getListFor(ref,
					allowed))
				{
					if (owner
						.equals(assoc.getAssociation(AssociationKey.OWNER))
						&& tokenName.equals(assoc
							.getAssociation(AssociationKey.TOKEN)))
					{
						list.add(ref);
						continue LIST;
					}
				}
			}
		}
		return new CollectionChanges<CDOMReference>(list, null, false);
	}

	public void clearAllMasterLists(String tokenName, CDOMObject owner)
	{
		for (CDOMReference<? extends CDOMList<?>> ref : masterList.getKeySet())
		{
			for (LSTWriteable allowed : masterList.getSecondaryKeySet(ref))
			{
				for (AssociatedPrereqObject assoc : masterList.getListFor(ref,
					allowed))
				{
					if (owner
						.equals(assoc.getAssociation(AssociationKey.OWNER))
						&& tokenName.equals(assoc
							.getAssociation(AssociationKey.TOKEN)))
					{
						masterList.removeFromListFor(ref, allowed, assoc);
					}
				}
			}
		}
	}

	public AssociatedChanges<LSTWriteable> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<?>> swl)
	{
		Set<LSTWriteable> added = masterList.getSecondaryKeySet(swl);
		MapToList<LSTWriteable, AssociatedPrereqObject> owned =
				new TreeMapToList<LSTWriteable, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
		for (LSTWriteable lw : added)
		{
			List<AssociatedPrereqObject> list = masterList.getListFor(swl, lw);
			for (AssociatedPrereqObject assoc : list)
			{
				if (owner.equals(assoc.getAssociation(AssociationKey.OWNER)))
				{
					owned.addToListFor(lw, assoc);
					break;
				}
			}
		}
		return new AssociatedCollectionChanges<LSTWriteable>(owned, null, false);
	}

	public boolean hasMasterLists()
	{
		return !masterList.isEmpty();
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

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl,
		CDOMReference<T> ref)
	{
		// TODO Auto-generated method stub

	}

	public void removeAllFromList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<?>> swl)
	{
		// TODO Auto-generated method stub

	}

	public Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl)
	{
		ArrayList<CDOMReference<? extends CDOMList<? extends CDOMObject>>> list =
				new ArrayList<CDOMReference<? extends CDOMList<? extends CDOMObject>>>();
		for (CDOMReference<? extends CDOMList<? extends CDOMObject>> ref : owner
			.getModifiedLists())
		{
			if (cl.equals(ref.getReferenceClass()))
			{
				list.add(ref);
			}
		}
		return list;
	}

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		// TODO Deal with matching the token... :/
		return new ListChanges<T>(tokenName, owner, null, swl, false);
	}
}
