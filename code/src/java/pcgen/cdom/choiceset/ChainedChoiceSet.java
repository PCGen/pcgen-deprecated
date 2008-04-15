package pcgen.cdom.choiceset;

import java.util.Set;

import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

public class ChainedChoiceSet<T> implements PrimitiveChoiceSet<T>
{
	
	private final PrimitiveChoiceSet<T> baseSet;
	private final PrimitiveChoiceSet<?> secondSet;
	
	public ChainedChoiceSet(PrimitiveChoiceSet<T> set, PrimitiveChoiceSet<?> otherSet)
	{
		baseSet = set;
		secondSet = otherSet;
	}

	public Class<? super T> getChoiceClass()
	{
		return baseSet.getChoiceClass();
	}

	public Set<T> getSet(CharacterDataStore pc)
	{
		return baseSet.getSet(pc);
	}

	public String getLSTformat()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public PrimitiveChoiceSet<?> getSecondSet()
	{
		return secondSet;
	}

}
