package pcgen.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Support class for running Junit tests
 */
public class TestSupport
{

	/**
	 * Utility method for Unit tests to invoke private constructors
	 * 
	 * @param clazz The class we're gonig to invoke the constructor on
	 * @return An instance of the class
	 */
	public static Object invokePrivateConstructor(Class<?> clazz)
	{
		Constructor<?> constructor = null; 
		try
		{
			constructor = clazz.getDeclaredConstructor();
		}
		catch (NoSuchMethodException e)
		{
			System.out.println("Constructor for [" + clazz.getName() + "] does not exist");
		}
		
		constructor.setAccessible(true);
		Object instance = null;
		
		try
		{
			instance = constructor.newInstance();
		}
		catch (InvocationTargetException ite)
		{
			System.out.println("Instance creation failed with [" + ite.getCause() + "]");
		}
		catch (IllegalAccessException iae)
		{
			System.out.println("Instance creation failed due to access violation.");
		}
		catch (InstantiationException ie)
		{
			System.out.println("Instance creation failed with [" + ie.getCause() + "]");
		}
		
		return instance;
	}
	
}
