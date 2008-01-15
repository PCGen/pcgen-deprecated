package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.spell.Spell;

public class SpellLevelLimit
{

	private final CDOMReference<Spell> spellType;
	private final int minLevel;
	private final int maxLevel;

	public SpellLevelLimit(CDOMReference<Spell> type, int min, int max)
	{
		spellType = type;
		minLevel = min;
		maxLevel = max;
	}

	public CDOMReference<Spell> getSpellGrouping()
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
