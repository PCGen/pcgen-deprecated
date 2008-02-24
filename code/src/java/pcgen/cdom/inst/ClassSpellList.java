package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMListObject;

public class ClassSpellList extends CDOMListObject<CDOMSpell>
{

	public Class<CDOMSpell> getListClass()
	{
		return CDOMSpell.class;
	}

	// No additional Functionality :)

}
