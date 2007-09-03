package plugin.lstcompatibility.global;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AddLoader;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;

public class Add512Lst_Language extends AbstractToken implements
		GlobalLstCompatibilityToken
{

	@Override
	public String getTokenName()
	{
		return "ADD";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 6;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
		throws PersistenceLayerException
	{
		if (!value.startsWith("Language("))
		{
			// Not valid compatibility
			return false;
		}
		int endParenLoc = value.indexOf(")");
		if (endParenLoc == -1)
		{
			// Don't deal with bad constructs
			return false;
		}
		String feats = value.substring(9, endParenLoc);
		String count;
		if (value.length() - 1 == endParenLoc)
		{
			count = "1";
		}
		else
		{
			count = value.substring(endParenLoc + 1);
		}
		return AddLoader.parseLine(context, (PObject) cdo, "LANGUAGE|" + count
			+ "|" + feats);
	}
}
