package pcgen.base.formula;

import junit.framework.TestCase;

public class MultiplyingFormulaTest extends TestCase
{

	public void testIdentity()
	{
		MultiplyingFormula f = new MultiplyingFormula(1);
		assertEquals(2, f.resolve(Integer.valueOf(2)).intValue());
		assertEquals(2, f.resolve(Double.valueOf(2.5)).intValue());
		testBrokenCalls(f);
	}

	public void testPositive()
	{
		MultiplyingFormula f = new MultiplyingFormula(3);
		assertEquals(15, f.resolve(Integer.valueOf(5)).intValue());
		//TODO Need to specify the order of operations - is this rounded first or second?
		//assertEquals(17, f.resolve(Double.valueOf(5.5)).intValue());
		testBrokenCalls(f);
	}

	public void testZero()
	{
		MultiplyingFormula f = new MultiplyingFormula(0);
		assertEquals(0, f.resolve(Integer.valueOf(5)).intValue());
		assertEquals(0, f.resolve(Double.valueOf(2.3)).intValue());
		testBrokenCalls(f);
	}

	public void testNegative()
	{
		MultiplyingFormula f = new MultiplyingFormula(-2);
		assertEquals(-10, f.resolve(Integer.valueOf(5)).intValue());
		//TODO Need to specify the order of operations - is this rounded first or second?
		//assertEquals(13, f.resolve(Double.valueOf(-6.7)).intValue());
		testBrokenCalls(f);
	}

	private void testBrokenCalls(MultiplyingFormula f)
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
