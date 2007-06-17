package pcgen.cdom.helper;

import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PlayerCharacter;

public interface ChoiceFilter<T> extends LSTWriteable
{
	public boolean remove(PlayerCharacter pc, T obj);
}
