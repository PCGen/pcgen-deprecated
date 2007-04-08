package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for PCC files
 */
public class AbbToken implements PCClassLstToken, PCClassClassLstToken
{

	/**
	 * Return token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	/**
	 * Parse the ABB token
	 * 
	 * @param pcclass
	 * @param value
	 * @param level
	 * @return true
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setAbbrev(value);
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		pcc.put(StringKey.ABB, value);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		String abb = pcc.get(StringKey.ABB);
		if (abb == null)
		{
			return null;
		}
		return new String[]{abb};
	}
}
