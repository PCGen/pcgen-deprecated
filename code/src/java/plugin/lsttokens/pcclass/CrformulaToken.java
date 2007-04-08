package plugin.lsttokens.pcclass;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with CRFORMULA Token
 */
public class CrformulaToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "CRFORMULA";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setCRFormula(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		pcc.put(FormulaKey.CR, FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Formula f = pcc.get(FormulaKey.CR);
		if (f == null)
		{
			return null;
		}
		return new String[]{f.toString()};
	}
}
