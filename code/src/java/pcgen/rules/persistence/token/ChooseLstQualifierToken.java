package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.persistence.lst.LstToken;

public interface ChooseLstQualifierToken<T extends CDOMObject> extends LstToken,
		PrimitiveChoiceSet<T>, QualifierToken<T>
{
	//No additional items (tagging interface)
}
