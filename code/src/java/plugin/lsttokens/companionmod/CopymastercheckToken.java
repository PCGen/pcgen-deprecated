package plugin.lsttokens.companionmod;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.inst.CDOMFollower;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.lst.CompanionModLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYMASTERCHECK Token
 */
public class CopymastercheckToken implements CompanionModLstToken,
		CDOMPrimaryToken<CDOMFollower>
{

	public String getTokenName()
	{
		return "COPYMASTERCHECK";
	}

	public boolean parse(CompanionMod cmpMod, String value)
	{
		cmpMod.setCopyMasterCheck(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMFollower stat,
			String value)
	{
		context.getObjectContext().put(stat, FormulaKey.MASTER_CHECK,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMFollower stat)
	{
		Formula f = context.getObjectContext().getFormula(stat,
				FormulaKey.MASTER_CHECK);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<CDOMFollower> getTokenClass()
	{
		return CDOMFollower.class;
	}

}
