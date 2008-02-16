package plugin.primitive.deity;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.character.CharacterDataStore;
import pcgen.core.Deity;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class PantheonToken implements PrimitiveToken<Deity>
{

	private Pantheon pantheon;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		pantheon = Pantheon.getConstant(value);
		return true;
	}

	public String getTokenName()
	{
		return "PANTHEON";
	}

	public Class<Deity> getReferenceClass()
	{
		return Deity.class;
	}

	public String getLSTformat()
	{
		return pantheon.toString();
	}

	public boolean allow(CharacterDataStore pc, Deity deity)
	{
		return deity.containsInList(ListKey.PANTHEON, pantheon);
	}

}
