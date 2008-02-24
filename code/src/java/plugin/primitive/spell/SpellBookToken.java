package plugin.primitive.spell;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class SpellBookToken implements PrimitiveToken<CDOMSpell>
{

	private boolean requiresSpellBook;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
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
		return requiresSpellBook ? "YES" : "NO";
	}

	public boolean allow(CharacterDataStore pc, CDOMSpell obj)
	{
		List<CDOMPCClass> classList =
				pc.getActiveGraph().getGrantedNodeList(CDOMPCClass.class);
		for (CDOMPCClass cl : classList)
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
