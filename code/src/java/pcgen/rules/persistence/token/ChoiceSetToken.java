package pcgen.rules.persistence.token;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

public interface ChoiceSetToken<T> extends LstToken
{
	public PrimitiveChoiceSet<?> parse(LoadContext context, T obj, String value)
			throws PersistenceLayerException;

	public Class<T> getTokenClass();
}
