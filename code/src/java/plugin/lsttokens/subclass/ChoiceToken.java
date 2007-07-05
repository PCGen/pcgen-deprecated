package plugin.lsttokens.subclass;

import pcgen.core.SubClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SubClassLstToken;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(SubClass subclass, String value)
	{
		subclass.setChoice(value);
		return true;
	}

	public boolean parse(LoadContext context, SubClass sc, String value)
	{
		/*
		 * TODO This is a problem, because it can be a School, SubSchool, or
		 * Descriptor... Need to disambiguate this in 5.13
		 */
		return false;
	}
}
