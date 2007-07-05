package pcgen.persistence.lst;

import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;

public interface ChooseLstQualifierToken<T extends PObject> extends LstToken,
		PrimitiveChoiceSet<T>
{
	public boolean initialize(LoadContext context, Class<T> cl, String value);
}
