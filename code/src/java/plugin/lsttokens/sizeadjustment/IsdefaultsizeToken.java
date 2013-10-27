package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.lst.SizeAdjustmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ISDEFAULTSIZE Token
 */
public class IsdefaultsizeToken implements SizeAdjustmentLstToken,
		CDOMPrimaryToken<CDOMSizeAdjustment>
{

	public String getTokenName()
	{
		return "ISDEFAULTSIZE";
	}

	public boolean parse(SizeAdjustment sa, String value)
	{
		sa.setIsDefaultSize(value.toUpperCase().startsWith("Y"));
		return true;
	}

	public Class<CDOMSizeAdjustment> getTokenClass()
	{
		return CDOMSizeAdjustment.class;
	}

	public boolean parse(LoadContext context, CDOMSizeAdjustment adj,
			String value)
	{
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
				{
					Logging.errorPrint("You should use 'YES' or 'NO' as the "
							+ getTokenName() + ": " + value);
					return false;
				}
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(adj, ObjectKey.IS_DEFAULT, set);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSizeAdjustment adj)
	{
		Boolean mult = context.getObjectContext().getObject(adj,
				ObjectKey.IS_DEFAULT);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.booleanValue() ? "YES" : "NO" };
	}

}
