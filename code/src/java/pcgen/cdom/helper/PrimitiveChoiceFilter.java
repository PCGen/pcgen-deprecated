package pcgen.cdom.helper;

import pcgen.cdom.base.LSTWriteable;
import pcgen.character.CharacterDataStore;

public interface PrimitiveChoiceFilter<T> extends LSTWriteable
{

	public boolean allow(CharacterDataStore pc, T obj);

	public Class<T> getReferenceClass();

}