package plugin.primitive.deity;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class PantheonToken implements PrimitiveToken<CDOMDeity>
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

	public Class<CDOMDeity> getReferenceClass()
	{
		return CDOMDeity.class;
	}

	public String getLSTformat()
	{
		return pantheon.toString();
	}

	public boolean allow(CharacterDataStore pc, CDOMDeity deity)
	{
		return deity.containsInList(ListKey.PANTHEON, pantheon);
	}

	public Set<CDOMDeity> getSet(CharacterDataStore pc)
	{
		HashSet<CDOMDeity> deitySet = new HashSet<CDOMDeity>();
		for (CDOMDeity deity : pc.getRulesData().getAll(CDOMDeity.class))
		{
			if (deity.containsInList(ListKey.PANTHEON, pantheon))
			{
				deitySet.add(deity);
			}
		}
		return deitySet;
	}
}
