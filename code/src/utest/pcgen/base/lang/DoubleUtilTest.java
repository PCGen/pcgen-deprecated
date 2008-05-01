package pcgen.base.lang;

import org.junit.Test;

import junit.framework.TestCase;

public class DoubleUtilTest extends TestCase {

	@Test
	public void testDoublesEqual()
	{
		boolean expectedResult = true;
		boolean result = DoubleUtil.doublesEqual(1.0000, 1.0000);
		assertTrue(result == expectedResult);
	}

	@Test
	public void testBreakingDefaultEpsilonDoublesEqual()
	{
		boolean expectedResult = true;
		boolean result = DoubleUtil.doublesEqual(1.00000, 1.00001);
		assertTrue(result == expectedResult);
	}
	
	@Test
	public void testLessThanEpsilonCompareDouble()
	{
		boolean expectedResult = true;
		boolean result = DoubleUtil.compareDouble(1.0000, 1.0001, 0.01);
		assertTrue(result == expectedResult);
	}

	@Test
	public void testGreaterThanEpsilonCompareDouble()
	{
		boolean expectedResult = true;
		boolean result = DoubleUtil.compareDouble(1.0, 1.1, 0.01);
		assertFalse(result == expectedResult);
	}
	
}
