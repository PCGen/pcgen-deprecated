package pcgen.persistence.lst;

import pcgen.persistence.LoadContext;

public interface QualifierToken<T>
{
	public boolean initialize(LoadContext context, Class<T> cl,
			String condition, String value);

	public String getTokenName();
}
