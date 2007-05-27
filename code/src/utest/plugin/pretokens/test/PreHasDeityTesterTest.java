package plugin.pretokens.test;

import org.junit.Test;

import pcgen.core.Deity;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreHasDeityTesterTest extends AbstractCDOMPreTestTestCase<Deity>
{

	PreHasDeityTester tester = new PreHasDeityTester();

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return PCTemplate.class;
	}

	private String getKind()
	{
		return "HASDEITY";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getYesPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("YES");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getNoPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("NO");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	// TODO Is this even possible to achieve, or dont' worry about it?
	// @Test
	// public void testInvalidCount()
	// {
	// Prerequisite prereq = getYesPrereq();
	// prereq.setOperand("x");
	// try
	// {
	// getTest().passesCDOM(prereq, pc);
	// fail();
	// }
	// catch (PrerequisiteException pe)
	// {
	// // OK (operand should be a number)
	// }
	// catch (NumberFormatException pe)
	// {
	// // OK (operand should be a number)
	// }
	// }

	@Test
	public void testYes() throws PrerequisiteException
	{
		Prerequisite prereq = getYesPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage");
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testNo() throws PrerequisiteException
	{
		Prerequisite prereq = getNoPrereq();
		// PC Should start without
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getYesPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantFalseObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantFalseObject("Winged Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?
}
