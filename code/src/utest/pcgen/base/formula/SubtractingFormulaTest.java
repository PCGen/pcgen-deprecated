package pcgen.base.formula;

import junit.framework.TestCase;


public class SubtractingFormulaTest extends TestCase
{

	public void testIdentity()
	{
		SubtractingFormula f = new SubtractingFormula(1);
		assertEquals(-1, f.resolve(Integer.valueOf(0)).intValue());
		assertEquals(1, f.resolve(Integer.valueOf(2)).intValue());
		assertEquals(1, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testPositive()
	{
		SubtractingFormula f = new SubtractingFormula(3);
		assertEquals(2, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(5.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		SubtractingFormula f = new SubtractingFormula(0);
		assertEquals(5, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.3)).intValue());
		testBrokenCalls(f);
	}

	public void testNegative()
	{
		SubtractingFormula f = new SubtractingFormula(-2);
		assertEquals(7, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(-4, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(SubtractingFormula f)
	{
		try
		{
			f.resolve((Number[]) null);
			fail("null should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(new Number[]{});
			fail("empty array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(new Number[]{Integer.valueOf(4), Double.valueOf(2.5)});
			fail("two arguments in array should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		try
		{
			f.resolve(Integer.valueOf(4), Double.valueOf(2.5));
			fail("two arguments should be illegal");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

}
