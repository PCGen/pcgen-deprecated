package plugin.lstcompatibility.global;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Add512Lst_SpellCaster extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
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
		return 2;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("SPELLCASTER("))
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
		String types = value.substring(12, endParenLoc);
		String count;
		if (value.length() - 1 == endParenLoc)
		{
			count = "1";
		}
		else
		{
			count = value.substring(endParenLoc + 1);
		}
		return context.processSubToken(cdo, getTokenName(), "SPELLCASTER",
				count + "|" + types);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
