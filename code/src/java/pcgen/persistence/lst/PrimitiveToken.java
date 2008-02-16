package pcgen.persistence.lst;

import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.persistence.LoadContext;

public interface PrimitiveToken<T> extends PrimitiveChoiceFilter<T>
{
	public boolean initialize(LoadContext context, String value, String args);

	public String getTokenName();
}
