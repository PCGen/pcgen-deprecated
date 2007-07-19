package pcgen.persistence;

import java.util.Collection;

public class CollectionChanges<T> implements Changes<T>
{
	private Collection<T> positive;
	private Collection<T> negative;
	private boolean clear;

	public CollectionChanges(Collection<T> added, Collection<T> removed,
		boolean globallyCleared)
	{
		positive = added;
		negative = removed;
		clear = globallyCleared;
	}

	public Collection<T> getAdded()
	{
		return positive;
	}

	public boolean includesGlobalClear()
	{
		return clear;
	}

	public boolean isEmpty()
	{
		return (positive == null || positive.isEmpty())
			&& (negative == null || negative.isEmpty());
	}
}
