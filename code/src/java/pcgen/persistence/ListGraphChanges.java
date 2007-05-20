package pcgen.persistence;

import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;
import pcgen.persistence.lst.utils.TokenUtilities;

public class ListGraphChanges<T extends CDOMObject> implements GraphChanges<T>
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

	public Collection<LSTWriteable> getAdded()
	{
		TreeSet<LSTWriteable> set =
				new TreeSet<LSTWriteable>(TokenUtilities.WRITEABLE_SORTER);
		set.addAll(owner.getListMods(ref));
		return set;
	}

	public Collection<LSTWriteable> getRemoved()
	{
		return null;
	}

	public AssociatedPrereqObject getAddedAssociation(LSTWriteable added)
	{
		return owner.getListAssociation(ref, added);
	}

	public AssociatedPrereqObject getRemovedAssociation(LSTWriteable added)
	{
		return null;
	}
}
