package plugin.lsttokens.statsandchecks.bonusspell;

import java.util.Map;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMBonusSpellLevel;
import pcgen.persistence.lst.BonusSpellLoader;
import pcgen.persistence.lst.BonusSpellLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with BASESTATSCORE Token
 */
public class BasestatscoreToken implements BonusSpellLstToken,
		CDOMPrimaryToken<CDOMBonusSpellLevel>
{

	public String getTokenName()
	{
		return "BASESTATSCORE";
	}

	public boolean parse(Map<String, String> bonus, String value)
	{
		bonus.put(BonusSpellLoader.BASE_STAT_SCORE, value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMBonusSpellLevel bsl,
			String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			context.getObjectContext().put(bsl, IntegerKey.BONUSSTATSCORE, in);
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

	public String[] unparse(LoadContext context, CDOMBonusSpellLevel bsl)
	{
		Integer score = context.getObjectContext().getInteger(bsl,
				IntegerKey.BONUSSTATSCORE);
		if (score == null)
		{
			return null;
		}
		return new String[] { score.toString() };
	}

	public Class<CDOMBonusSpellLevel> getTokenClass()
	{
		return CDOMBonusSpellLevel.class;
	}
}
