package pcgen.cdom.helper;

import java.util.Collections;
import java.util.Set;

import pcgen.core.PlayerCharacter;

public class NoChoiceSet implements PrimitiveChoiceSet<Integer>
{

	public Class<Integer> getReferenceClass()
	{
		return Integer.class;
	}

	public Set<Integer> getSet(PlayerCharacter pc)
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
