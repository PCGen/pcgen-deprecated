package pcgen.cdom.base;

import java.util.Set;

import pcgen.character.CharacterDataStore;

public interface PrimitiveChoiceSet<T> extends LSTWriteable
{
	public Set<T> getSet(CharacterDataStore pc);

	public Class<? super T> getChoiceClass();
}