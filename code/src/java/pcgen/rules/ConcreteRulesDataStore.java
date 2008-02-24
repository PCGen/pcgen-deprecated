package pcgen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.inst.DomainList;
import pcgen.cdom.inst.CDOMPCLevel;

public class ConcreteRulesDataStore
{
	private final List<CDOMPCLevel> list = new ArrayList<CDOMPCLevel>();

	public CDOMPCLevel getLevel(int level)
	{
		if (list.size() <= level)
		{
			for (int i = list.size(); i <= level; i++)
			{
				list.add(new CDOMPCLevel());
			}
		}
		return list.get(level);
	}

	public <T extends CDOMObject> Set<T> getAll(Class<T> cl)
	{
		return null;
	}

	public <T extends CDOMObject> T getObject(Class<T> cl, String key)
	{
		return null;
	}

	public CDOMSizeAdjustment getNextSize(CDOMSizeAdjustment size)
	{
		// TODO What if null (if this is last?)
		return null;
	}

	public CDOMSizeAdjustment getPreviousSize(CDOMSizeAdjustment size)
	{
		// TODO What if null (if this is last?)
		return null;
	}

	public CDOMSizeAdjustment getDefaultSizeAdjustment()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public CDOMReference<DomainList> getReference(Class<DomainList> class1,
			String string)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
