package plugin.lsttokens.pcclass;

import pcgen.base.util.Logging;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with MAXLEVEL Token
 */
public class MaxlevelToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "MAXLEVEL";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			pcclass.setMaxLevel(PCClass.NO_LEVEL_LIMIT.intValue());
			return true;
		}
		try
		{
			pcclass.setMaxLevel(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		Integer lim;
		if ("NOLIMIT".equalsIgnoreCase(value))
		{
			lim = PCClass.NO_LEVEL_LIMIT;
		}
		else
		{
			try
			{
				lim = Integer.valueOf(value);
				if (lim.intValue() <= 0)
				{
					Logging.errorPrint("Value less than 1 is not valid for "
						+ getTokenName() + ": " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Value was not a number for "
					+ getTokenName() + ": " + value);
				return false;
			}
		}
		pcc.put(IntegerKey.LEVEL_LIMIT, lim);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lim = pcc.get(IntegerKey.LEVEL_LIMIT);
		if (lim == null)
		{
			return null;
		}
		String returnString = lim.toString();
		if (lim.equals(PCClass.NO_LEVEL_LIMIT))
		{
			returnString = "NOLIMIT";
		}
		else if (lim.intValue() <= 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{returnString};
	}
}
