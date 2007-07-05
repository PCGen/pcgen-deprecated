package pcgen.cdom.helper;

import java.util.Set;

import pcgen.cdom.base.LSTWriteable;
import pcgen.core.PlayerCharacter;

public interface PrimitiveChoiceSet<T> extends LSTWriteable
{
	public Set<T> getSet(PlayerCharacter pc);

	public Class<T> getChoiceClass();
}