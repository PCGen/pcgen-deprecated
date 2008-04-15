package pcgen.cdom.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.ReferenceUtilities;
import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.character.CharacterDataStore;

public class CompoundOrFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final Class<T> refClass;
	private final Set<PrimitiveChoiceFilter<T>> set =
			new HashSet<PrimitiveChoiceFilter<T>>();

	public CompoundOrFilter(Collection<PrimitiveChoiceFilter<T>> coll)
	{
		if (coll == null)
		{
			throw new IllegalArgumentException();
		}
		if (coll.isEmpty())
		{
			throw new IllegalArgumentException();
		}
		refClass = set.iterator().next().getReferenceClass();
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
			if (cs.allow(pc, obj))
			{
				return true;
			}
		}
		return false;
	}
}
