package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with SUBCLASSLEVEL Token
 */
public class SubclasslevelToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "SUBCLASSLEVEL";
	}

	public boolean parse(SubClass subclass, String value)
	{
		subclass.addToLevelArray(value);
		return true;
	}

	public boolean parse(LoadContext context, SubClass sc, String value)
	{
		// NOT USED in CDOM
		return false;
	}
}
