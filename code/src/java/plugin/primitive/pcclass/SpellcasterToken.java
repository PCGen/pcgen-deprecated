package plugin.primitive.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class SpellcasterToken implements PrimitiveToken<PCClass>
{

	public void initialize(LoadContext context, String value)
	{
		if (value != null)
		{
			throw new IllegalArgumentException("Value for primitive "
				+ getTokenName() + "must be null");
		}
	}

	public String getTokenName()
	{
		return "SPELLCASTER";
	}

	public Class<PCClass> getReferenceClass()
	{
		return PCClass.class;
	}

	public String getLSTformat()
	{
		return getTokenName();
	}

	public boolean allow(PlayerCharacter pc, PCClass obj)
	{
		return obj.get(ObjectKey.USE_SPELL_SPELL_STAT) != null;
	}
}
