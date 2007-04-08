package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with MODTOSKILLS Token
 */
public class ModtoskillsToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "MODTOSKILLS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setModToSkills(!"No".equalsIgnoreCase(value));
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
		pcc.put(ObjectKey.MOD_TO_SKILLS, set);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Boolean mts = pcc.get(ObjectKey.MOD_TO_SKILLS);
		if (mts == null)
		{
			return null;
		}
		return new String[]{mts.booleanValue() ? "YES" : "NO"};
	}
}
