package pcgen.cdom.choiceset;

import java.util.Collections;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class NoChoiceSet implements PrimitiveChoiceSet<Integer>
{

	public Class<Integer> getReferenceClass()
	{
		return Integer.class;
	}

	public Set<Integer> getSet(CharacterDataStore pc)
	{
		return Collections.emptySet();
	}

	public String getLSTformat()
	{
		return "NOCHOICE";
	}

	public Class<Integer> getChoiceClass()
	{
		return Integer.class;
	}

}
