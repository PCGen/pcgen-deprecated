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
 * Class deals with ITEMCREATE Token
 */
public class ItemcreateToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "ITEMCREATE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setItemCreationMultiplier(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		pcc.put(FormulaKey.ITEM_CREATION_MULTIPLIER, FormulaFactory
			.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Formula f = pcc.get(FormulaKey.ITEM_CREATION_MULTIPLIER);
		if (f == null)
		{
			return null;
		}
		return new String[]{f.toString()};
	}
}
