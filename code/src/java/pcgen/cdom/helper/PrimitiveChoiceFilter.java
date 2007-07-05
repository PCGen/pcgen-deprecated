package pcgen.cdom.helper;

import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PlayerCharacter;

public interface PrimitiveChoiceFilter<T> extends LSTWriteable
{

	public boolean allow(PlayerCharacter pc, T obj);

	public Class<T> getReferenceClass();

}