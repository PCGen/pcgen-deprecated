package plugin.pretokens.test;

import org.junit.Test;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreReachTesterTest extends AbstractCDOMPreTestTestCase<Race>
{

	PreReachTester tester = new PreReachTester();

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
		return "REACH";
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
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTwoPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setOperand("2");
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
	public void testRace() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		PObject human = grantCDOMObject("Human");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		human.put(IntegerKey.REACH, Integer.valueOf(1));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		human.put(IntegerKey.REACH, Integer.valueOf(2));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		human.put(IntegerKey.REACH, Integer.valueOf(0));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testRaceTemplate() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		PObject human = grantCDOMObject("Human");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		human.put(IntegerKey.REACH, Integer.valueOf(3));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		PObject templ = getObject(PCTemplate.class, "Templ");
		grantObject(templ);
		templ.put(IntegerKey.REACH, Integer.valueOf(1));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		human.put(IntegerKey.REACH, Integer.valueOf(0));
		templ.put(IntegerKey.REACH, Integer.valueOf(4));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GTEQ);
		human.put(IntegerKey.REACH, Integer.valueOf(4));
		templ.put(IntegerKey.REACH, Integer.valueOf(0));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.NEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LTEQ);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.LT);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.GT);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperator(PrerequisiteOperator.EQ);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getOnePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		PObject falseObj = grantFalseObject("Wild Mage");
		falseObj.put(IntegerKey.REACH, Integer.valueOf(1));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?
	
	// TODO Need to test BONUS impacting REACH
}
