package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMListObject;

public class SpellList extends CDOMListObject<CDOMSpell>
{

	public Class<CDOMSpell> getListClass()
	{
		return CDOMSpell.class;
	}

	// No additional Functionality :)

}
