package pcgen.rules.persistence.token;

import pcgen.rules.context.LoadContext;

public interface QualifierToken<T>
{
	public boolean initialize(LoadContext context, Class<T> cl,
			String condition, String value);

	public String getTokenName();
}
