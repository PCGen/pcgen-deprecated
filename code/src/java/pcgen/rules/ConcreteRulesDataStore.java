package pcgen.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.DomainList;
import pcgen.core.PCCharacterLevel;
import pcgen.core.SizeAdjustment;

public class ConcreteRulesDataStore
{
	private final List<PCCharacterLevel> list = new ArrayList<PCCharacterLevel>();

	public PCCharacterLevel getLevel(int level)
	{
		if (list.size() <= level)
		{
			for (int i = list.size(); i <= level; i++)
			{
				list.add(new PCCharacterLevel());
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

	public SizeAdjustment getNextSize(SizeAdjustment size)
	{
		// TODO What if null (if this is last?)
		return null;
	}

	public SizeAdjustment getPreviousSize(SizeAdjustment size)
	{
		// TODO What if null (if this is last?)
		return null;
	}

	public SizeAdjustment getDefaultSizeAdjustment()
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
