package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMObject;

public class GrantedList extends CDOMListObject<CDOMObject>
{

	public Class<CDOMObject> getListClass()
	{
		return CDOMObject.class;
	}

	// No additional Functionality :)

}
