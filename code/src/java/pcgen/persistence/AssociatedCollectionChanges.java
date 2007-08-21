package pcgen.persistence;

import java.util.Collection;
import java.util.Map;

import pcgen.cdom.base.AssociatedPrereqObject;

public class AssociatedCollectionChanges<T> implements Changes<T>
{
	private Map<T, AssociatedPrereqObject> positive;
	private Map<T, AssociatedPrereqObject> negative;
	private boolean clear;

	public AssociatedCollectionChanges(Map<T, AssociatedPrereqObject> added,
		Map<T, AssociatedPrereqObject> removed, boolean globallyCleared)
	{
		positive = added;
		negative = removed;
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

	public Collection<T> getAdded()
	{
		return positive.keySet();
	}

	public boolean hasAddedItems()
	{
		return positive != null && !positive.isEmpty();
	}

	public Collection<T> getRemoved()
	{
		return negative.keySet();
	}

	public boolean hasRemovedItems()
	{
		return negative != null && !negative.isEmpty();
	}

	public AssociatedPrereqObject getAddedAssociation(T added)
	{
		return positive.get(added);
	}

	public AssociatedPrereqObject getRemovedAssociation(T removed)
	{
		return negative.get(removed);
	}
}
