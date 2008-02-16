package plugin.primitive.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.character.CharacterDataStore;
import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class SpellcasterToken implements PrimitiveToken<PCClass>
{

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		if (value != null)
		{
			throw new IllegalArgumentException("Value for primitive "
				+ getTokenName() + "must be null");
		}
		return true;
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

	public boolean allow(CharacterDataStore pc, PCClass obj)
	{
		return obj.get(ObjectKey.USE_SPELL_SPELL_STAT) != null;
	}
}
