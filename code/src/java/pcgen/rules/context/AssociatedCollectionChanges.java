package pcgen.rules.context;

import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.util.MapToList;

public class AssociatedCollectionChanges<T> implements AssociatedChanges<T>
{
	private MapToList<LSTWriteable, AssociatedPrereqObject> positive;
	private MapToList<LSTWriteable, AssociatedPrereqObject> negative;
	private boolean clear;

	public AssociatedCollectionChanges(
		MapToList<LSTWriteable, AssociatedPrereqObject> added,
		MapToList<LSTWriteable, AssociatedPrereqObject> removed,
		boolean globallyCleared)
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

	public Collection<LSTWriteable> getAdded()
	{
		return positive.getKeySet();
	}

	public boolean hasAddedItems()
	{
		return positive != null && !positive.isEmpty();
	}

	public Collection<LSTWriteable> getRemoved()
	{
		return negative.getKeySet();
	}

	public boolean hasRemovedItems()
	{
		return negative != null && !negative.isEmpty();
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getAddedAssociations()
	{
		return positive;
	}

	public MapToList<LSTWriteable, AssociatedPrereqObject> getRemovedAssociations()
	{
		return negative;
	}
}
