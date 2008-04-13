package pcgen.cdom.inst;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;

public class CDOMPCClass extends AbstractCDOMClassAwareObject
{

	Map<Integer, CDOMPCClassLevel> levelMap = new HashMap<Integer, CDOMPCClassLevel>();
	List<CDOMPCClassLevel> repeatLevelObjects = new ArrayList<CDOMPCClassLevel>();

	public CDOMPCClassLevel getClassLevel(int lvl)
	{
		if (!levelMap.containsKey(lvl))
		{
			CDOMPCClassLevel classLevel = new CDOMPCClassLevel();
			classLevel.put(IntegerKey.LEVEL, Integer.valueOf(lvl));
			classLevel.setName(getDisplayName() + "(" + lvl + ")");
			classLevel.put(ObjectKey.PARENT, this);
			levelMap.put(lvl, classLevel);
		}
		return levelMap.get(lvl);
	}

	public int getClassLevelCount()
	{
		return levelMap.size();
	}

	public Collection<CDOMPCClassLevel> getClassLevelCollection()
	{
		return Collections.unmodifiableCollection(levelMap.values());
	}

	private CDOMSpellProgressionInfo spi = null;

	public CDOMSpellProgressionInfo getCDOMSpellProgressionInfo()
	{
		if (spi == null)
		{
			spi = new CDOMSpellProgressionInfo();
		}
		return spi;
	}

	public boolean hasCDOMSpellProgression()
	{
		return spi != null;
	}

	public int getCDOMLevel(CDOMPCClassLevel pcl)
	{
		if (this.equals(pcl.get(ObjectKey.PARENT)))
		{
			for (Map.Entry<Integer, CDOMPCClassLevel> me : levelMap.entrySet())
			{
				if (me.getValue().equals(pcl))
				{
					return me.getKey().intValue();
				}
			}
		}
		return -1;
	}

	private ClassSkillList cdomClassSkillList = new ClassSkillList();

	public ClassSkillList getCDOMClassSkillList()
	{
		return cdomClassSkillList;
	}

	public List<CDOMSubClass> getCDOMSubClassList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMPCClass)
		{
			CDOMPCClass other = (CDOMPCClass) o;
			// TODO spi, class skill list
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}

	public CDOMPCClassLevel getRepeatLevel(int level, String objectName)
	{
		CDOMPCClassLevel pcl = new CDOMPCClassLevel();
		repeatLevelObjects.add(pcl);
		pcl.put(ObjectKey.PARENT, this);
		pcl.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.TRUE);
		String originalLevels = level + ":" + objectName;
		pcl.put(StringKey.REPEAT, originalLevels);
		pcl.setName(getDisplayName() + "(" + originalLevels + ")");
		return pcl;
	}
	
	public Collection<CDOMPCClassLevel> getRepeatLevels()
	{
		return Collections.unmodifiableList(repeatLevelObjects);
	}
}
