package plugin.primitive.domain;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.character.CharacterDataStore;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class DeityToken implements PrimitiveToken<Domain>
{

	private CDOMSimpleSingleRef<Deity> ref;
	private CDOMSimpleSingleRef<DomainList> list;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getCDOMReference(Deity.class, value);
		list = context.ref.getCDOMReference(DomainList.class, "*Starting");
		return true;
	}

	public String getTokenName()
	{
		return "DEITY";
	}

	public Class<Domain> getReferenceClass()
	{
		return Domain.class;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(CharacterDataStore pc, Domain obj)
	{
		for (CDOMReference<Domain> domainRef : ref.resolvesTo().getListMods(
			list))
		{
			if (domainRef.contains(obj))
			{
				return true;
			}
		}
		return false;
	}

}
