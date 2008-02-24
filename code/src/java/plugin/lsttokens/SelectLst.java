package plugin.lsttokens;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SELECT Token
 */
public class SelectLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "SELECT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setSelect(value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
	{
		context.getObjectContext().put(cdo, FormulaKey.SELECT,
				FormulaFactory.getFormulaFor(value));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		Formula f = context.getObjectContext().getFormula(cdo,
				FormulaKey.SELECT);
		if (f == null)
		{
			return null;
		}
		return new String[] { f.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
