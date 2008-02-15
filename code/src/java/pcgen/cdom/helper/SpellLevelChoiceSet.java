package pcgen.cdom.helper;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.character.CharacterDataStore;
import pcgen.core.SpellProgressionInfo;

public class SpellLevelChoiceSet implements PrimitiveChoiceSet<String>
{

	private final CDOMReference<SpellProgressionInfo> spellLists;
	private final Formula minimum;
	private final Formula maximum;

	public SpellLevelChoiceSet(CDOMReference<SpellProgressionInfo> slref,
		Formula min, Formula max)
	{
		spellLists = slref;
		minimum = min;
		maximum = max;
	}

	public Class<String> getChoiceClass()
	{
		return String.class;
	}

	public Set<String> getSet(CharacterDataStore pc)
	{
		Set<String> set = new TreeSet<String>();

		List<SpellProgressionInfo> classes =
				pc.getActiveGraph().getGrantedNodeList(
					SpellProgressionInfo.class);
		for (SpellProgressionInfo cl : classes)
		{
			if (spellLists.contains(cl))
			{
				// TODO Need to set MAXLEVEL in maximum...
				// int maxLevelVal =
				// cl.getMaxSpellLevelForClassLevel(cl.getLevel());
				int maxLevel = maximum.resolve(pc, "");
				int minLevel = minimum.resolve(pc, "");

				String prefix = cl.getKeyName() + " ";

				for (int j = minLevel; j <= maxLevel; ++j)
				{
					set.add(prefix + j);
				}
			}
		}
		return set;
	}

	public String getLSTformat()
	{
		return spellLists.getLSTformat();
	}

}
