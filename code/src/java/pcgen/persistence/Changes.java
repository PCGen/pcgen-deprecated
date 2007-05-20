package pcgen.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Changes<T>
{

	List<T> added = new ArrayList<T>();
	boolean globallyCleared = false;

	public void addAdded(T obj)
	{
		added.add(obj);
	}

	public Collection<T> getAdded()
	{
		return added;
	}

	public boolean isEmpty()
	{
		return added.isEmpty() && !globallyCleared;
	}

	public void setGloballyCleared()
	{
		globallyCleared = true;
	}

	public boolean includesGlobalClear()
	{
		return globallyCleared;
	}
}
