package plugin.lsttokens.statsandchecks.stat;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.inst.CDOMStat;
import pcgen.core.PCStat;
import pcgen.persistence.lst.PCStatLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken implements PCStatLstToken, CDOMPrimaryToken<CDOMStat>
{

	public String getTokenName()
	{
		return "STATMOD";
	}

	public boolean parse(PCStat stat, String value)
	{
		stat.setStatMod(value);
		return true;
	}

	public Class<CDOMStat> getTokenClass()
	{
		return CDOMStat.class;
	}

	public boolean parse(LoadContext context, CDOMStat stat, String value)
	{
		context.getObjectContext().put(stat, FormulaKey.STAT_MOD,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMStat stat)
	{
		Formula f = context.getObjectContext().getFormula(stat,
				FormulaKey.STAT_MOD);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}
}
