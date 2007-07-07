package plugin.primitive.deity;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class PantheonToken implements PrimitiveToken<Deity>
{

	private Pantheon pantheon;

	public void initialize(LoadContext context, String value)
	{
		pantheon = Pantheon.getConstant(value);
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

	public boolean allow(PlayerCharacter pc, Deity deity)
	{
		return deity.containsInList(ListKey.PANTHEON, pantheon);
	}

}
