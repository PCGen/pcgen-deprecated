package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;

public interface ChoiceSetCompatibilityToken<T extends CDOMObject> extends
		CompatibilityToken, ChoiceSetToken<T>
{

}
