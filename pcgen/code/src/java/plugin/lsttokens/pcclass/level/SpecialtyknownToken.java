package plugin.lsttokens.pcclass.level;

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SPECIALTYKNOWN Token
 */
public class SpecialtyknownToken extends AbstractToken implements
		CDOMPrimaryToken<PCClassLevel>
{

	@Override
	public String getTokenName()
	{
		return "SPECIALTYKNOWN";
	}

	public boolean parse(LoadContext context, PCClassLevel pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator(',', value))
		{
			return false;
		}

		StringTokenizer st = new StringTokenizer(value, Constants.COMMA);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			try
			{
				if (Integer.parseInt(tok) < 0)
				{
					Logging.errorPrint("Invalid Spell Count: " + tok
							+ " is less than zero");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				// OK, it must be a formula...
			}
			context.obj.addToList(pcc, ListKey.SPECIALTYKNOWN, FormulaFactory
					.getFormulaFor(tok));
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClassLevel pcc)
	{
		Changes<Formula> changes = context.obj.getListChanges(pcc,
				ListKey.SPECIALTYKNOWN);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(changes.getAdded(),
				Constants.COMMA) };
	}

	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}
}
