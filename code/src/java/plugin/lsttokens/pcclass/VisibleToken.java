package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassClassLstToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCClassLstToken, PCClassClassLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (!value.toUpperCase().startsWith("Y"))
		{
			pcclass.setVisibility(Visibility.NO);
		} // Assume DEFAULT is the DEFAULT :)
		return true;
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
		throws PersistenceLayerException
	{
		if (value.equals("NO"))
		{
			pcc.put(ObjectKey.VISIBILITY, Visibility.NO);
		}
		else if (value.equals("YES"))
		{
			pcc.put(ObjectKey.VISIBILITY, Visibility.YES);
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Visibility vis = pcc.get(ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a PCClass");
			return null;
		}
		return new String[]{visString};
	}
}
