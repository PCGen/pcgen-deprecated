package plugin.primitive.domain;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.DomainList;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PrimitiveToken;

public class DeityToken implements PrimitiveToken<Domain>
{

	private CDOMSimpleSingleRef<Deity> ref;
	private CDOMSimpleSingleRef<DomainList> list;

	public void initialize(LoadContext context, String value)
	{
		ref = context.ref.getCDOMReference(Deity.class, value);
		list = context.ref.getCDOMReference(DomainList.class, "*Starting");
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

	public boolean allow(PlayerCharacter pc, Domain obj)
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
