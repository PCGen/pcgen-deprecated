package plugin.pretokens.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CDOMSimpleSingleRef;
import pcgen.cdom.inst.CDOMPCLevel;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.rules.RulesDataStore;

public class SimpleRulesDataStore implements RulesDataStore
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

	DoubleKeyMap<Class<? extends CDOMObject>, String, CDOMObject> objects = new DoubleKeyMap<Class<? extends CDOMObject>, String, CDOMObject>();

	public <T extends CDOMObject> T create(Class<T> cl, String key)
	{
		try
		{
			T inst = cl.newInstance();
			objects.put(cl, key, inst);
			inst.setName(key);
			return inst;
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public <T extends CDOMObject> Set<T> getAll(Class<T> cl)
	{
		return (Set<T>) objects.values(cl);
	}

	public <T extends CDOMObject> T getObject(Class<T> cl, String key)
	{
		return (T) objects.get(cl, key);
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

	public <TT extends CDOMObject> CDOMReference<TT> getReference(Class<TT> cl,
			String name)
	{
		return new CDOMSimpleSingleRef<TT>(cl, name);
	}

}
