package pcgen.persistence.lst;

import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;

public interface ChooseLstGlobalQualifierToken<T extends PObject> extends
		LstToken, PrimitiveChoiceSet<T>, QualifierToken<T>
{
	//No additional items (tagging interface)
}
