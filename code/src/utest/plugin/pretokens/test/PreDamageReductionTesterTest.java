package plugin.pretokens.test;

import org.junit.Test;

import pcgen.cdom.content.DamageReduction;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreDamageReductionTesterTest extends
		AbstractCDOMPreTestTestCase<Race>
{

	PreDamageReductionTester tester = new PreDamageReductionTester();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Language.class;
	}

	private String getKind()
	{
		return "DR";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getOnePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("+1");
		p.setOperand("10");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTwoPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("+2");
		p.setOperand("5");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testInvalidCount()
	{
		Prerequisite prereq = getOnePrereq();
		prereq.setOperand("x");
		try
		{
			getTest().passesCDOM(prereq, pc);
			fail();
		}
		catch (PrerequisiteException pe)
		{
			// OK (operand should be a number)
		}
		catch (NumberFormatException pe)
		{
			// OK (operand should be a number)
		}
	}

	@Test
	public void testInsufficient() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		DamageReduction fiveOne = DamageReduction.getDamageReduction("5/+1");
		grantObject(fiveOne);
		// Insufficient
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		DamageReduction fiveTwo = DamageReduction.getDamageReduction("5/+2");
		grantObject(fiveTwo);
		// Insufficient
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testExact() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		DamageReduction tenOne = DamageReduction.getDamageReduction("10/+1");
		grantObject(tenOne);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testExactOr() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		DamageReduction tenOne =
				DamageReduction.getDamageReduction("10/+1 or +2");
		grantObject(tenOne);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testSurplus() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		DamageReduction tenOne = DamageReduction.getDamageReduction("15/+1");
		grantObject(tenOne);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?

	// TODO Need to test DRs with or and and

	// TODO Need to test DRs that use variables...
}
