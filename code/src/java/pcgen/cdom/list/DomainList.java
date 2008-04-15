package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.inst.CDOMDomain;

public class DomainList extends CDOMListObject<CDOMDomain>
{

	public Class<CDOMDomain> getListClass()
	{
		return CDOMDomain.class;
	}

	// No additional Functionality :)

}
