package pcgen.cdom.inst;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;

public class CDOMRace extends CDOMObject
{

	/*
	 * BEGIN CDOM CODE
	 */
	public int sizesAdvancedCDOM(int currentHD)
	{
		List<Integer> list = getListFor(ListKey.HITDICE_ADVANCEMENT);
		if (list != null)
		{
			for (int x = 0; x < list.size(); x++)
			{
				int listDie = list.get(x).intValue();
				if ((currentHD <= listDie) || (listDie == -1))
				{
					return x;
				}
			}
		}
		return 0;
	}

	private CDOMKit defaultMonsterKit;
	
	public CDOMKit getCompatMonsterKit()
	{
		if (defaultMonsterKit == null)
		{
			defaultMonsterKit = new CDOMKit();
		}
		return defaultMonsterKit;
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
		if (o instanceof CDOMRace)
		{
			CDOMRace other = (CDOMRace) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}
}
