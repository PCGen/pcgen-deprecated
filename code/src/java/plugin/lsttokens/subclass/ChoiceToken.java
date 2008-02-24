package plugin.lsttokens.subclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CDOMSpellProhibitor;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.ProhibitedSpellType;
import pcgen.cdom.inst.CDOMSubClass;
import pcgen.core.SubClass;
import pcgen.persistence.lst.SubClassLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken implements SubClassLstToken, CDOMPrimaryToken<CDOMSubClass>
{

	public String getTokenName()
	{
		return "CHOICE";
	}

	public boolean parse(SubClass subclass, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " uses a deprecated format: " + value + "\n  "
					+ "New format must be type|value, e.g. SCHOOL|Abjuration");
			subclass.setChoice(value);
			return true;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " has invalid arguments: " + value
					+ "\n  cannot have two | characters");
			return false;
		}
		String type = value.substring(0, pipeLoc);
		if ("SCHOOL".equals(type) || "SUBSCHOOL".equals(type)
				|| "DESCRIPTOR".equals(type))
		{
			// Unfortunately, in 5.14, we have no way of validating that the
			// input
			// is correct
			subclass.setChoice(value.substring(pipeLoc + 1));
			return true;
		}
		Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
				+ " did not understand type: " + type);
		return false;
	}

	public boolean parse(LoadContext context, CDOMSubClass sc, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " invalid: " + value
					+ "\n  Must be type|value, e.g. SCHOOL|Abjuration");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " has invalid arguments: " + value
					+ "\n  cannot have two | characters");
			return false;
		}
		String type = value.substring(0, pipeLoc);
		if ("SCHOOL".equals(type) || "SUBSCHOOL".equals(type)
				|| "DESCRIPTOR".equals(type))
		{
			CDOMSpellProhibitor<?> sp = getSpellProhib(ProhibitedSpellType
					.getReference(type), value.substring(pipeLoc + 1));
			context.getObjectContext().put(sc, ObjectKey.SELETED_SPELLS, sp);
			return true;
		}
		else
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " did not understand type: " + type);
			return false;
		}
	}

	private <T> CDOMSpellProhibitor<T> getSpellProhib(ProhibitedSpellType<T> pst,
			String arg)
	{
		CDOMSpellProhibitor<T> spSchool = new CDOMSpellProhibitor<T>();
		spSchool.setType(pst);
		spSchool.addValue(pst.getTypeValue(arg));
		return spSchool;
	}

	public String[] unparse(LoadContext context, CDOMSubClass obj)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Class<CDOMSubClass> getTokenClass()
	{
		return CDOMSubClass.class;
	}
}
