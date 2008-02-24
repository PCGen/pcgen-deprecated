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
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements BonusSpellLstToken, CDOMPrimaryToken<CDOMBonusSpellLevel>
{

	public String getTokenName()
	{
		return "STATRANGE";
	}

	public boolean parse(Map<String, String> bonus, String value)
	{
		bonus.put(BonusSpellLoader.STAT_RANGE, value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMBonusSpellLevel bsl, String value)
	{
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(bsl, IntegerKey.STATRANGE, in);
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
		Integer range =
				context.getObjectContext().getInteger(bsl, IntegerKey.STATRANGE);
		if (range == null)
		{
			return null;
		}
		if (range.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{range.toString()};
	}

	public Class<CDOMBonusSpellLevel> getTokenClass()
	{
		return CDOMBonusSpellLevel.class;
	}
}
