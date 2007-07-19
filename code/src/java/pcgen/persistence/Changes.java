package pcgen.persistence;

import java.util.Collection;

public interface Changes<T>
{
	public Collection<T> getAdded();

	public boolean isEmpty();

	public boolean includesGlobalClear();
}
