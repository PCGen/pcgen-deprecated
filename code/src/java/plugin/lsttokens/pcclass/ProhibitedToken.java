package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with PROHIBITED Token
 */
public class ProhibitedToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "PROHIBITED";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		final StringTokenizer aTok = new StringTokenizer(value, ",");
		while (aTok.hasMoreTokens())
		{
			String prohibitedSchool = aTok.nextToken();
			if (!prohibitedSchool.equals(Constants.s_NONE))
			{
				pcclass.addProhibitedSchool(prohibitedSchool);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value) throws PersistenceLayerException
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
