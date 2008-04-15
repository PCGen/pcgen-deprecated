package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.inst.CDOMSpell;

public class DomainSpellList extends CDOMListObject<CDOMSpell>
{

	public Class<CDOMSpell> getListClass()
	{
		return CDOMSpell.class;
	}

	// No additional Functionality :)

}
