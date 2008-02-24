package plugin.lsttokens.subclass;

import pcgen.cdom.inst.CDOMSubClass;
import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

/**
 * Class deals with SUBCLASSLEVEL Token
 */
public class SubclasslevelToken implements SubClassLstToken, CDOMCompatibilityToken<CDOMSubClass>
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

	public boolean parse(LoadContext context, CDOMSubClass sc, String value)
	{
		// NOT USED in CDOM
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public Class<CDOMSubClass> getTokenClass()
	{
		return CDOMSubClass.class;
	}
}
