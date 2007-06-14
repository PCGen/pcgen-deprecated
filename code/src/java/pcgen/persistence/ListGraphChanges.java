package pcgen.persistence;

import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.persistence.lst.utils.TokenUtilities;

public class ListGraphChanges<T extends CDOMObject>
{

	private final CDOMObject owner;
	private final CDOMReference<? extends CDOMList<T>> ref;

	public ListGraphChanges(CDOMObject cdo,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		owner = cdo;
		ref = swl;
	}

	public boolean hasAddedItems()
	{
		return owner.hasListMods(ref);
	}

	public boolean hasRemovedItems()
	{
		return false;
	}

	public boolean includesGlobalClear()
	{
		return false;
	}

	public Collection<CDOMReference<T>> getAdded()
	{
		TreeSet<CDOMReference<T>> set =
				new TreeSet<CDOMReference<T>>(TokenUtilities.WRITEABLE_SORTER);
		set.addAll(owner.getListMods(ref));
		return set;
	}

	public Collection<CDOMReference<T>> getRemoved()
	{
		return null;
	}

	public AssociatedPrereqObject getAddedAssociation(CDOMReference<T> added)
	{
		return owner.getListAssociation(ref, added);
	}

	public AssociatedPrereqObject getRemovedAssociation(CDOMReference<T> added)
	{
		return null;
	}
}
