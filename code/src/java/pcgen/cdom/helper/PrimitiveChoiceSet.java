package pcgen.cdom.helper;

import java.util.Set;

import pcgen.cdom.base.LSTWriteable;
import pcgen.character.CharacterDataStore;

public interface PrimitiveChoiceSet<T> extends LSTWriteable
{
	public Set<T> getSet(CharacterDataStore pc);

	public Class<T> getChoiceClass();
}