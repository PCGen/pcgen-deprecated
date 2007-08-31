package plugin.primitive.spell;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class SpellBookToken implements PrimitiveToken<Spell>
{

	private boolean requiresSpellBook;

	public void initialize(LoadContext context, String value)
	{
		if (value.equals("YES"))
		{
			requiresSpellBook = true;
		}
		else if (value.equals("NO"))
		{
			requiresSpellBook = false;
		}
		else
		{
			throw new IllegalArgumentException(
				"Did not understand SpellBook requrirement String: " + value);
		}
	}

	public String getTokenName()
	{
		return "SPELLBOOK";
	}

	public Class<Spell> getReferenceClass()
	{
		return Spell.class;
	}

	public String getLSTformat()
	{
		return requiresSpellBook ? "YES" : "NO";
	}

	public boolean allow(PlayerCharacter pc, Spell obj)
	{
		List<PCClass> classList =
				pc.getActiveGraph().getGrantedNodeList(PCClass.class);
		for (PCClass cl : classList)
		{
			Boolean spellbook = cl.get(ObjectKey.SPELLBOOK);
			if (spellbook != null
				&& (spellbook.booleanValue() == requiresSpellBook))
			{
				// TODO use this class
			}
		}
		//TODO this is a hack...
		return false;
	}
}
