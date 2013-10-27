package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Class deals with STATRANGE Token
 */
public class StatrangeToken implements PCStatLstToken
{

	public String getTokenName()
	{
		return "STATRANGE";
	}

	public boolean parse(PCStat stat, String value)
	{
		stat.setStatRange(value);
		return true;
	}

	public Class<CDOMStat> getTokenClass()
	{
		return CDOMStat.class;
	}

	public boolean parse(LoadContext context, CDOMStat stat, String value)
	{
		int barLoc = value.indexOf(Constants.PIPE);
		if (barLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
					+ getTokenName() + " syntax: must have a PIPE: " + value);
			return false;
		}
		if (barLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
					+ getTokenName() + " syntax: must only have one PIPE: "
					+ value);
			return false;
		}
		try
		{
			Integer minValue = Integer.valueOf(value.substring(0, barLoc));
			context.obj.put(stat, IntegerKey.MINIMUM, minValue);
		}
		catch (NumberFormatException ignore)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
					+ getTokenName()
					+ " syntax: minimum (before pipe) must be an integer: "
					+ value);
			return false;
		}
		try
		{
			Integer maxValue = Integer.valueOf(value.substring(barLoc + 1));
			context.obj.put(stat, IntegerKey.MAXIMUM, maxValue);
		}
		catch (NumberFormatException ignore)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid "
					+ getTokenName()
					+ " syntax: minimum (after pipe) must be an integer: "
					+ value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMStat stat)
	{
		Integer min = context.getObjectContext().getInteger(stat,
				IntegerKey.MINIMUM);
		Integer max = context.getObjectContext().getInteger(stat,
				IntegerKey.MAXIMUM);
		if (min == null)
		{
			if (max != null)
			{
				context.addWriteMessage("Invalid " + getTokenName() + " in "
						+ stat.getKeyName() + " had MAXIMUM without MINIMUM");
			}
			return null;
		}
		else
		{
			if (max == null)
			{
				context.addWriteMessage("Invalid " + getTokenName() + " in "
						+ stat.getKeyName() + " had MINIMUM without MAXIMUM");
				return null;
			}
		}
		return new String[] { min + Constants.PIPE + max };
	}
}
