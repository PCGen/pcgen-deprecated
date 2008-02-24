package plugin.primitive.domain;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.DomainList;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class DeityToken implements PrimitiveToken<CDOMDomain>
{

	private CDOMSingleRef<CDOMDeity> ref;
	private CDOMSingleRef<DomainList> list;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getCDOMReference(CDOMDeity.class, value);
		list = context.ref.getCDOMReference(DomainList.class, "*Starting");
		return true;
	}

	public String getTokenName()
	{
		return "DEITY";
	}

	public Class<CDOMDomain> getReferenceClass()
	{
		return CDOMDomain.class;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(CharacterDataStore pc, CDOMDomain obj)
	{
		for (CDOMReference<CDOMDomain> domainRef : ref.resolvesTo()
				.getListMods(list))
		{
			if (domainRef.contains(obj))
			{
				return true;
			}
		}
		return false;
	}

}
