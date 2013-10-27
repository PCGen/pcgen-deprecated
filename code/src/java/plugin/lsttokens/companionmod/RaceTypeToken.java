package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.inst.CDOMFollower;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACETYPE: Token
 */
public class RaceTypeToken extends AbstractToken implements
		CompanionModLstToken, CDOMPrimaryToken<CDOMFollower>
{

	@Override
	public String getTokenName()
	{
		return "RACETYPE";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setRaceType(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMFollower race,
			String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(race, ObjectKey.RACETYPE,
				RaceType.getConstant(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMFollower race)
	{
		RaceType raceType = context.getObjectContext().getObject(race,
				ObjectKey.RACETYPE);
		if (raceType == null)
		{
			return null;
		}
		return new String[] { raceType.toString() };
	}

	public Class<CDOMFollower> getTokenClass()
	{
		return CDOMFollower.class;
	}

}
