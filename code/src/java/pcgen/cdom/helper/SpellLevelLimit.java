package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMSpell;

public class SpellLevelLimit
{

	private final CDOMReference<CDOMSpell> spellType;
	private final int minLevel;
	private final int maxLevel;

	public SpellLevelLimit(CDOMReference<CDOMSpell> type, int min, int max)
	{
		spellType = type;
		minLevel = min;
		maxLevel = max;
	}

	public CDOMReference<CDOMSpell> getSpellGrouping()
	{
		return spellType;
	}

	public int getMinLevel()
	{
		return minLevel;
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}
}
