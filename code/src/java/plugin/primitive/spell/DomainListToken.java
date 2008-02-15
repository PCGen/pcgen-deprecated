package plugin.primitive.spell;

import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.character.CharacterDataStore;
import pcgen.core.DomainSpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class DomainListToken implements PrimitiveToken<Spell>
{

	private CDOMSimpleSingleRef<DomainSpellList> ref;

	public void initialize(LoadContext context, String value)
	{
		ref = context.ref.getCDOMReference(DomainSpellList.class, value);
	}

	public String getTokenName()
	{
		return "DOMAINLIST";
	}

	public Class<Spell> getReferenceClass()
	{
		return Spell.class;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(CharacterDataStore pc, Spell obj)
	{
		return pc.getActiveLists().listContains(ref.resolvesTo(), obj);
	}

}
