package plugin.lsttokens.add;

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

	public boolean parse(LoadContext context, PObject obj, String value)
		throws PersistenceLayerException
	{
		// FIXME This is a hack
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
