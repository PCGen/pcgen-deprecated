package plugin.lsttokens.statsandchecks.alignment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.core.PCAlignment;
import pcgen.persistence.lst.PCAlignmentLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with VALIDFORFOLLOWER Token
 */
public class ValidforfollowerToken implements PCAlignmentLstToken,
		CDOMPrimaryToken<CDOMAlignment>
{

	public String getTokenName()
	{
		return "VALIDFORFOLLOWER";
	}

	public boolean parse(PCAlignment align, String value)
	{
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging
						.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
					&& !value.equalsIgnoreCase("NO"))
			{
				Logging.deprecationPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName());
				Logging
						.deprecationPrint("Abbreviations will fail after PCGen 5.14");
			}
			set = false;
		}
		align.setValidForFollower(set);
		return true;
	}

	public boolean parse(LoadContext context, CDOMAlignment adj, String value)
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

	public String[] unparse(LoadContext context, CDOMAlignment adj)
	{
		Boolean mult = context.getObjectContext().getObject(adj,
				ObjectKey.IS_DEFAULT);
		if (mult == null)
		{
			return null;
		}
		return new String[] { mult.booleanValue() ? "YES" : "NO" };
	}

	public Class<CDOMAlignment> getTokenClass()
	{
		return CDOMAlignment.class;
	}

}
