package pcgen.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.persistence.lst.utils.TokenUtilities;

public class MasterListChanges<T extends CDOMObject>
{

	private final DoubleKeyMapToList<CDOMReference, LSTWriteable, AssociatedPrereqObject> map;

	private final CDOMReference<? extends CDOMList<T>> ref;

	private final String token;

	private final CDOMObject owner;

	public MasterListChanges(
		String tokenName,
		CDOMObject cdo,
		DoubleKeyMapToList<CDOMReference, LSTWriteable, AssociatedPrereqObject> dkm,
		CDOMReference<? extends CDOMList<T>> list)
	{
		map = dkm;
		ref = list;
		owner = cdo;
		token = tokenName;
	}

	public boolean hasAddedItems()
	{
		return true;
	}

	public boolean hasRemovedItems()
	{
		return false;
	}

	public boolean includesGlobalClear()
	{
		return false;
	}

	public Collection<LSTWriteable> getAdded()
	{
		Set<LSTWriteable> set = map.getSecondaryKeySet(ref);
		if (set == null || set.isEmpty())
		{
			return null;
		}
		TreeSet<LSTWriteable> returnset =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		returnset.addAll(set);
		return returnset;
	}

	public Collection<LSTWriteable> getRemoved()
	{
		return null;
	}

	public AssociatedPrereqObject getAddedAssociation(LSTWriteable added)
	{
		List<AssociatedPrereqObject> assocList = map.getListFor(ref, added);
		if (assocList == null)
		{
			return null;
		}
		for (AssociatedPrereqObject assoc : assocList)
		{
			if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
				&& token.equals(assoc.getAssociation(AssociationKey.TOKEN)))
			{
				return assoc;
			}
		}
		return null;
	}

	public AssociatedPrereqObject getRemovedAssociation(LSTWriteable added)
	{
		return null;
	}

}
