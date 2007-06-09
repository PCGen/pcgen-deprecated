package pcgen.cdom.helper;

import pcgen.core.PlayerCharacter;

public interface ChoiceFilter<T>
{
	public boolean remove(PlayerCharacter pc, T obj);
}
