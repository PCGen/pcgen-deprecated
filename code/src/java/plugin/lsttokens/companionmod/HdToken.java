package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMFollower;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken implements CompanionModLstToken,
		CDOMPrimaryToken<CDOMFollower>
{

	public String getTokenName()
	{
		return "HD";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setHitDie(Integer.parseInt(value));
		return true;
	}

	public boolean parse(LoadContext context, CDOMFollower race,
			String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(race, IntegerKey.HIT_DIE, in);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, CDOMFollower race)
	{
		Integer hands = context.getObjectContext().getInteger(race,
				IntegerKey.HIT_DIE);
		if (hands == null)
		{
			return null;
		}
		if (hands.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { hands.toString() };
	}

	public Class<CDOMFollower> getTokenClass()
	{
		return CDOMFollower.class;
	}

}
