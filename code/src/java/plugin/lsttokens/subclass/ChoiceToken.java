package plugin.lsttokens.subclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.core.Constants;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SubClassLstToken;
import pcgen.util.Logging;

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
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint(getTokenName() + " invalid: " + value
				+ "\n  Must be type|value, e.g. SCHOOL|Abjuration");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.errorPrint(getTokenName() + " has invalid arguments: "
				+ value + "\n  cannot have two | characters");
			return false;
		}
		String type = value.substring(0, pipeLoc);
		if ("SCHOOL".equals(type) || "SUBSCHOOL".equals(type)
			|| "DESCRIPTOR".equals(type))
		{
			SpellProhibitor<?> sp =
					getSpellProhib(ProhibitedSpellType.getReference(type),
						value.substring(pipeLoc + 1));
			context.getObjectContext().put(sc, ObjectKey.SELETED_SPELLS, sp);
		}
		else
		{
			Logging.errorPrint(getTokenName() + " did not understand type: "
				+ type);
			return false;
		}
		return true;
	}

	private <T> SpellProhibitor<T> getSpellProhib(ProhibitedSpellType<T> pst,
		String arg)
	{
		SpellProhibitor<T> spSchool = new SpellProhibitor<T>();
		spSchool.setType(pst);
		spSchool.addValue(pst.getTypeValue(arg));
		return spSchool;
	}
}
