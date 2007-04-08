package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with MEMORIZE Token
 */
public class MemorizeToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "MEMORIZE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setMemorizeSpells(value.startsWith("Y"));
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
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
		pcc.put(ObjectKey.MEMORIZE_SPELLS, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean mem = pcc.get(ObjectKey.MEMORIZE_SPELLS);
		if (mem == null)
		{
			return null;
		}
		return new String[]{mem.booleanValue() ? "YES" : "NO"};
	}
}
