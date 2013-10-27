package pcgen.base.lang;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import pcgen.util.TestSupport;
import junit.framework.TestCase;

public class StringUtilTest extends TestCase {

	@Test
	public void testPrivateConstructor()
	{
		Object instance = TestSupport.invokePrivateConstructor(StringUtil.class);
		assertTrue(instance instanceof StringUtil);
	}

	@Test
	public void testJoin() {
		String expectedResult = "Foo|Bar";
		String string1 = "Foo";
		String string2 = "Bar";
		Collection<String> strings = new ArrayList<String>();
		strings.add(string1);
		strings.add(string2);
		String seperator = "|";
		String result = StringUtil.join(strings, seperator);
		assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testNullJoinToStringBuffer() {
		String expectedResult = "";
		Collection<String> strings = null;
		String seperator = "|";
		StringBuilder result = StringUtil
				.joinToStringBuffer(strings, seperator);
		String finalResult = result.toString();
		assertTrue(finalResult.equals(expectedResult));
	}

	@Test
	public void testJoinToStringBuffer() {
		final String expectedResult = "Foo|Bar";
		String string1 = "Foo";
		String string2 = "Bar";
		Collection<String> strings = new ArrayList<String>();
		strings.add(string1);
		strings.add(string2);
		String seperator = "|";
		StringBuilder result = StringUtil
				.joinToStringBuffer(strings, seperator);
		String finalResult = result.toString();
		assertTrue(finalResult.equals(expectedResult));
	}

	@Test
	public void testJoinOneToStringBuffer() {
		final String expectedResult = "Foo";
		String string1 = "Foo";
		Collection<String> strings = new ArrayList<String>();
		strings.add(string1);
		String seperator = "|";
		StringBuilder result = StringUtil
				.joinToStringBuffer(strings, seperator);
		String finalResult = result.toString();
		assertTrue(finalResult.equals(expectedResult));
	}

	@Test
	public void testReplaceAll() {
		String in = "FooBar";
		String find = "oB";
		String newStr = "CC";
		String expectedResult = "FoCCar";
		String result = StringUtil.replaceAll(in, find, newStr);
		assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testJoinArrayOfStrings() {
		final String expectedResult = "Foo|Bar";
		String string1 = "Foo";
		String string2 = "Bar";
		String[] strings = new String[2];
		strings[0] = string1;
		strings[1] = string2;
		String seperator = "|";
		String result = StringUtil.join(strings, seperator);
		assertTrue(result.equals(expectedResult));
	}

	@Test
	public void testNullJoinArrayOfStrings() {
		final String expectedResult = "";
		String[] strings = null;
		String seperator = "|";
		String result = StringUtil.join(strings, seperator);
		assertTrue(result.equals(expectedResult));
	}

}
