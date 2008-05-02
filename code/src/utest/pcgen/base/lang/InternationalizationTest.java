package pcgen.base.lang;

import org.junit.Test;

import pcgen.util.TestSupport;

import junit.framework.TestCase;

public class InternationalizationTest extends TestCase {

	@Test
	public void testPrivateConstructor()
	{
		Object instance = TestSupport.invokePrivateConstructor(Internationalization.class);
		assertTrue(instance instanceof Internationalization);
	}
	
	@Test
	public void testSetGetCountry()
	{
		String expectedResult = "NL";
		Internationalization.setCountry("NL");
		String result = Internationalization.getCountry();
		assertTrue(result.equals(expectedResult));
	}
	
	@Test
	public void testSetGetLanguage()
	{
		String expectedResult = "nl";
		Internationalization.setLanguage("nl");
		String result = Internationalization.getLanguage();
		assertTrue(result.equals(expectedResult));
	}

}
