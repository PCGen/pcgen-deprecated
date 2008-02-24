package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.persistence.lst.LstToken;

public interface ChooseLstGlobalQualifierToken<T extends CDOMObject> extends
		LstToken, PrimitiveChoiceSet<T>, QualifierToken<T>
{
	//No additional items (tagging interface)
}
