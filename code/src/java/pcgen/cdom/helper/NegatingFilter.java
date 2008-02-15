package pcgen.cdom.helper;

import pcgen.character.CharacterDataStore;

public class NegatingFilter<T> implements PrimitiveChoiceFilter<T>
{

	private final PrimitiveChoiceFilter<T> filter;

	public NegatingFilter(PrimitiveChoiceFilter<T> f)
	{
		if (f == null)
		{
			throw new IllegalArgumentException();
		}
		filter = f;
	}

	public boolean allow(CharacterDataStore pc, T obj)
	{
		return !filter.allow(pc, obj);
	}

	public Class<T> getReferenceClass()
	{
		return filter.getReferenceClass();
	}

	public String getLSTformat()
	{
		return "!" + filter.getLSTformat();
	}

}
