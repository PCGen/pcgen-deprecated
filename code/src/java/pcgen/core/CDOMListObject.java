package pcgen.core;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;

public class CDOMListObject<T extends CDOMObject> extends PObject implements
		CDOMList<T>
{

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o != null && o.getClass().equals(getClass());
	}
}
