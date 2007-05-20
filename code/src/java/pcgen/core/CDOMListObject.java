package pcgen.core;

import java.util.List;

import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;

public class CDOMListObject<T extends CDOMObject> extends PObject implements
		CDOMList<T>
{

	public AssociatedObject getAssociation(T sk)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> getList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsKey(String requiredKey)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public T get(String requiredKey)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
