package plugin.primitive.deity;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class AlignToken implements PrimitiveToken<CDOMDeity>
{

	private CDOMAlignment ref;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getAbbreviatedObject(CDOMAlignment.class, value);
		return true;
	}

	public String getTokenName()
	{
		return "ALIGN";
	}

	public Class<CDOMDeity> getReferenceClass()
	{
		return CDOMDeity.class;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(CharacterDataStore pc, CDOMDeity deity)
	{
		return ref.equals(deity.get(ObjectKey.ALIGNMENT));
	}

	public Set<CDOMDeity> getSet(CharacterDataStore pc)
	{
		HashSet<CDOMDeity> deitySet = new HashSet<CDOMDeity>();
		for (CDOMDeity deity : pc.getRulesData().getAll(CDOMDeity.class))
		{
			if (ref.equals(deity.get(ObjectKey.ALIGNMENT)))
			{
				deitySet.add(deity);
			}
		}
		return deitySet;
	}

}
