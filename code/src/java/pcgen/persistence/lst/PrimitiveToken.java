package pcgen.persistence.lst;

import pcgen.cdom.helper.PrimitiveChoiceFilter;
import pcgen.persistence.LoadContext;

public interface PrimitiveToken<T> extends LstToken, PrimitiveChoiceFilter<T>
{
	public void initialize(LoadContext context, String value);
}
