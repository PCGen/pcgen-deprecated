package plugin.lsttokens.add;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;

public class SpellLevelToken implements AddLstToken
{

	public boolean parse(PObject target, String value, int level)
	{
		target.addAddList(level, getTokenName() + ":" + value);
		return true;
	}

	public String getTokenName()
	{
		return "SPELLLEVEL";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		// FIXME This is a hack
		return true;
	}
}
