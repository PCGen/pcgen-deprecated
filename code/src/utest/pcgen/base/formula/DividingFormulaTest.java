package pcgen.base.formula;

import junit.framework.TestCase;

public class DividingFormulaTest extends TestCase
{

	public void testIdentity()
	{
		DividingFormula f = new DividingFormula(1);
		assertEquals(2, f.resolve(Integer.valueOf(2)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testPositive()
	{
		DividingFormula f = new DividingFormula(3);
		assertEquals(1, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(2, f.resolve(Integer.valueOf(6)).intValue());
		assertEquals(2, f.resolve(Integer.valueOf(7)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(6.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		try
		{
			new DividingFormula(0);
			fail("DividingFormula should not allow build with zero (will always fail)");
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	public void testNegative()
	{
		DividingFormula f = new DividingFormula(-2);
		assertEquals(-2, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(3, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(DividingFormula f)
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
