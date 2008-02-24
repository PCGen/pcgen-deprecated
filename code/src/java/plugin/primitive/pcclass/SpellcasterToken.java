package plugin.primitive.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class SpellcasterToken implements PrimitiveToken<CDOMPCClass>
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

	public Class<CDOMPCClass> getReferenceClass()
	{
		return CDOMPCClass.class;
	}

	public String getLSTformat()
	{
		return getTokenName();
	}

	public boolean allow(CharacterDataStore pc, CDOMPCClass obj)
	{
		return obj.get(ObjectKey.USE_SPELL_SPELL_STAT) != null;
	}
}
