package pcgen.cdom.filter;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceFilter;
import pcgen.character.CharacterDataStore;

public class PatternMatchFilter<T extends CDOMObject> implements
		PrimitiveChoiceFilter<T>
{

	private final Class<T> baseClass;

	private final String pattern;

	public PatternMatchFilter(Class<T> cl, String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("Pattern cannot be null");
		}
		if (!key.endsWith("%"))
		{
			throw new IllegalArgumentException("Pattern must end with %");
		}
		baseClass = cl;
		pattern = key.substring(0, key.length() - 1);
	}

	public boolean allow(CharacterDataStore pc, T obj)
	{
		return obj.getKeyName().startsWith(pattern);
	}

	public Class<T> getReferenceClass()
	{
		return baseClass;
	}

	public String getLSTformat()
	{
		return pattern;
	}

}
