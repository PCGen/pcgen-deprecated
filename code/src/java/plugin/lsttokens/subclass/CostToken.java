package plugin.lsttokens.subclass;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SubClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.SubClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with COST Token
 */
public class CostToken implements SubClassLstToken
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(SubClass subclass, String value)
	{
		try
		{
			subclass.setCost(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
	}

	public boolean parse(LoadContext context, SubClass sc, String value)
	{
		try
		{
			BigDecimal cost = new BigDecimal(value);
			if (cost.compareTo(BigDecimal.ZERO) < 0)
			{
				Logging.errorPrint(getTokenName()
					+ " must be a positive number: " + value);
				return false;
			}
			context.obj.put(sc, ObjectKey.COST, cost);
			return true;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(getTokenName() + " expected a number: " + value);
			return false;
		}
	}
}
