package pcgen.persistence;

import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.persistence.lst.utils.TokenUtilities;

public class ListChanges<T extends CDOMObject> implements
		Changes<CDOMReference<T>>
{
	private CDOMObject positive;
	private CDOMObject negative;
	private CDOMReference<? extends CDOMList<T>> list;
	private boolean clear;

	public ListChanges(CDOMObject added, CDOMObject removed,
		CDOMReference<? extends CDOMList<T>> listref, boolean globallyCleared)
	{
		positive = added;
		negative = removed;
		list = listref;
		clear = globallyCleared;
	}

	public boolean includesGlobalClear()
	{
		return clear;
	}

	public boolean isEmpty()
	{
		return !clear && !hasAddedItems() && !hasRemovedItems();
	}

	public Collection<CDOMReference<T>> getAdded()
	{
		TreeSet<CDOMReference<T>> set =
				new TreeSet<CDOMReference<T>>(TokenUtilities.REFERENCE_SORTER);
		set.addAll(positive.getListMods(list));
		return set;
	}

	public boolean hasAddedItems()
	{
		return positive != null && positive.getListMods(list) != null
			&& !positive.getListMods(list).isEmpty();
	}

	public Collection<CDOMReference<T>> getRemoved()
	{
		TreeSet<CDOMReference<T>> set =
				new TreeSet<CDOMReference<T>>(TokenUtilities.REFERENCE_SORTER);
		if (negative == null)
		{
			return set;
		}
		set.addAll(negative.getListMods(list));
		return set;
	}

	public boolean hasRemovedItems()
	{
		return negative != null && negative.getListMods(list) != null
			&& !negative.getListMods(list).isEmpty();
	}

	public AssociatedPrereqObject getAddedAssociation(CDOMReference<T> added)
	{
		return positive.getListAssociation(list, added);
	}

	public AssociatedPrereqObject getRemovedAssociation(CDOMReference<T> removed)
	{
		return negative.getListAssociation(list, removed);
	}
}
