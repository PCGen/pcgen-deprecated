package pcgen.cdom.base;

import pcgen.character.CharacterDataStore;

public interface PrimitiveChoiceFilter<T> extends LSTWriteable
{

	public boolean allow(CharacterDataStore pc, T obj);

	public Class<T> getReferenceClass();

}