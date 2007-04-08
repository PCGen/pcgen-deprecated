package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HD Token
 */
public class HdToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "HD";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		try
		{
			pcclass.setHitDie(Integer.parseInt(value));
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
		try
		{
			Integer in = Integer.valueOf(value);
			if (in.intValue() <= 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer > 0");
				return false;
			}
			pcc.put(IntegerKey.HIT_DIE, in);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Integer lpf = pcc.get(IntegerKey.HIT_DIE);
		if (lpf == null)
		{
			return null;
		}
		if (lpf.intValue() <= 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{lpf.toString()};
	}
}
