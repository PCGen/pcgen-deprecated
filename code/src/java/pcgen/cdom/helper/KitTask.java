package pcgen.cdom.helper;

public class KitTask<T>
{

	private final Class<T> cl;
	private final T object;
	
	public KitTask(Class<T> c, T obj)
	{
		cl = c;
		object = obj;
	}

	public Class<T> getUnderlyingClass()
	{
		return cl;
	}

	public T getObject()
	{
		return object;
	}
	
}
