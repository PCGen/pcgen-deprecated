package plugin.primitive.spell;

import pcgen.cdom.inst.CDOMSpell;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class SpellBookToken implements PrimitiveToken<CDOMSpell>
{

	private String spellBook;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		spellBook = value;
		return true;
	}

	public String getTokenName()
	{
		return "SPELLBOOK";
	}

	public Class<CDOMSpell> getReferenceClass()
	{
		return CDOMSpell.class;
	}

	public String getLSTformat()
	{
		return spellBook;
	}

	public boolean allow(CharacterDataStore pc, CDOMSpell obj)
	{
		//Is the spell in the given spell book for the PC?
		//TODO this is a hack...
		return false;
	}
}
