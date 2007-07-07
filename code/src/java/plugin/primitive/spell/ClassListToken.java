package plugin.primitive.spell;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.core.ClassSpellList;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class ClassListToken implements PrimitiveToken<Spell>
{

	private CDOMSimpleSingleRef<ClassSpellList> ref;

	public void initialize(LoadContext context, String value)
	{
		ref = context.ref.getCDOMReference(ClassSpellList.class, value);
	}

	public String getTokenName()
	{
		return "CLASSLIST";
	}

	public Class<Spell> getReferenceClass()
	{
		return Spell.class;
	}

	public Set<Spell> getSet(PlayerCharacter pc)
	{
		return new HashSet<Spell>(pc.getActiveLists().getListContents(
			ref.resolvesTo()));
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(PlayerCharacter pc, Spell obj)
	{
		return pc.getActiveLists().listContains(ref.resolvesTo(), obj);
	}
}
