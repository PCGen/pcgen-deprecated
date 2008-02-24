package pcgen.cdom.helper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.character.CharacterDataStore;

public class CompoundAndFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final Class<T> refClass;
	private final Set<PrimitiveChoiceFilter<T>> set =
			new HashSet<PrimitiveChoiceFilter<T>>();

	public CompoundAndFilter(Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		if (coll.isEmpty())
		{
			throw new IllegalArgumentException();
		}
		refClass = coll.iterator().next().getReferenceClass();
		set.addAll(coll);
	}

	public String getLSTformat()
	{
		return ReferenceUtilities.joinLstFormat(set, Constants.PIPE);
	}

	public Class<T> getReferenceClass()
	{
		return refClass;
	}

	public boolean allow(CharacterDataStore pc, T obj)
	{
		for (PrimitiveChoiceFilter<T> cs : set)
		{
			if (!cs.allow(pc, obj))
			{
				return false;
			}
		}
		return true;
	}
}
