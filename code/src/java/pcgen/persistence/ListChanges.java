package pcgen.persistence;

import java.util.Collection;
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.persistence.lst.utils.TokenUtilities;

public class ListChanges<T extends CDOMObject> implements
		AssociatedChanges<CDOMReference<T>>
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

	public Collection<LSTWriteable> getAdded()
	{
		TreeSet<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		Collection<CDOMReference<T>> listMods = positive.getListMods(list);
		if (listMods != null)
		{
			set.addAll(listMods);
		}
		return set;
	}

	public boolean hasAddedItems()
	{
		return positive != null && positive.getListMods(list) != null
			&& !positive.getListMods(list).isEmpty();
	}

	public Collection<LSTWriteable> getRemoved()
	{
		TreeSet<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		if (negative == null)
		{
			return set;
		}
		Collection<CDOMReference<T>> listMods = negative.getListMods(list);
		if (listMods != null)
		{
			set.addAll(listMods);
		}
		return set;
	}

	public boolean hasRemovedItems()
	{
		return negative != null && negative.getListMods(list) != null
			&& !negative.getListMods(list).isEmpty();
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations()
	{
		MapToList<LSTWriteable, AssociatedPrereqObject> owned =
			new TreeMapToList<LSTWriteable, AssociatedPrereqObject>(
				TokenUtilities.WRITEABLE_SORTER);
		Collection<CDOMReference<T>> mods = positive.getListMods(list);
		if (mods == null)
		{
			return null;
		}
		for (CDOMReference<T> lw : mods)
		{
			Collection<AssociatedPrereqObject> assocs =
					positive.getListAssociations(list, lw);
			for (AssociatedPrereqObject assoc : assocs)
			{
				owned.addToListFor(lw, assoc);
			}
		}
		if (owned.isEmpty())
		{
			return null;
		}
		return owned;
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations()
	{
		Collection<CDOMReference<T>> mods = negative.getListMods(list);
		MapToList<LSTWriteable, AssociatedPrereqObject> owned =
				new TreeMapToList<LSTWriteable, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
		for (CDOMReference<T> lw : mods)
		{
			Collection<AssociatedPrereqObject> assocs =
					negative.getListAssociations(list, lw);
			for (AssociatedPrereqObject assoc : assocs)
			{
				owned.addToListFor(lw, assoc);
			}
		}
		if (owned.isEmpty())
		{
			return null;
		}
		return owned;
	}
}
