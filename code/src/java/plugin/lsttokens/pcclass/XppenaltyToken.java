package plugin.lsttokens.pcclass;

import pcgen.base.util.Logging;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.DefaultTriState;

/**
 * Class deals with XPPENALTY Token
 */
public class XppenaltyToken implements PCClassLstToken, PCClassClassLstToken
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XPPENALTY";
	}

	/**
	 * Parse XPPENALTY token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setXPPenalty(DefaultTriState.valueOf(value));
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		try
		{
			pcc.put(ObjectKey.XP_PENALTY, DefaultTriState.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Illegal Value encountered in " + getTokenName()
				+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		DefaultTriState xpp = pcc.get(ObjectKey.XP_PENALTY);
		if (xpp == null)
		{
			return null;
		}
		return new String[]{xpp.toString()};
	}
}
