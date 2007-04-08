package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with MONNONSKILLHD Token
 */
public class MonnonskillhdToken implements PCClassLstToken,
		PCClassClassLstToken
{

	public String getTokenName()
	{
		return "MONNONSKILLHD";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.addBonusList("0|MONNONSKILLHD|NUMBER|" + value);
		return true;
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
			pcc.put(IntegerKey.MONSTER_NON_SKILL_HD, in);
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
		Integer msp = pcc.get(IntegerKey.MONSTER_NON_SKILL_HD);
		if (msp == null)
		{
			return null;
		}
		if (msp.intValue() <= 0)
		{
			context.addWriteMessage(getTokenName() + " must be an integer > 0");
			return null;
		}
		return new String[]{msp.toString()};
	}
}
