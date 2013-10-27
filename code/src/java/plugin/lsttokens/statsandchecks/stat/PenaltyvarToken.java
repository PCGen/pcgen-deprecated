package plugin.lsttokens.statsandchecks.stat;

import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PENALTYVAR Token
 */
public class PenaltyvarToken implements PCStatLstToken,
		CDOMPrimaryToken<CDOMStat>
{

	public String getTokenName()
	{
		return "PENALTYVAR";
	}

	public boolean parse(PCStat stat, String value)
	{
		stat.setPenaltyVar(value);
		return true;
	}

	public Class<CDOMStat> getTokenClass()
	{
		return CDOMStat.class;
	}

	public boolean parse(LoadContext context, CDOMStat stat, String value)
	{
		context.getObjectContext().put(stat, StringKey.PENALTYVAR, value);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMStat stat)
	{
		String f = context.getObjectContext().getString(stat,
				StringKey.PENALTYVAR);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}
}
