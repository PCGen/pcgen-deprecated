package pcgen.persistence.lst;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;

public interface DeferredToken<T extends PObject>
{
	public boolean process(LoadContext context, T obj);
	
	public Class<T> getObjectClass();
}
