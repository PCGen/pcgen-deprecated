package plugin.pretokens.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.formula.FixedSizeResolver;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreSizeTesterTest extends AbstractCDOMPreTestTestCase<CDOMRace>
{

	PreSizeTester tester = new PreSizeTester();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public void classSetUp()
	{
		classSetUpFired = true;
	}

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		if (!classSetUpFired)
		{
			classSetUp();
		}
		rules.create(CDOMSizeAdjustment.class, "T");
		rules.create(CDOMSizeAdjustment.class, "S");
		rules.create(CDOMSizeAdjustment.class, "M");
		rules.create(CDOMSizeAdjustment.class, "L");
		rules.create(CDOMSizeAdjustment.class, "H");
	}

	@Override
	public Class<CDOMRace> getCDOMClass()
	{
		return CDOMRace.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	private String getKind()
	{
		return "SIZE";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	private FixedSizeResolver getSmallSize()
	{
		return new FixedSizeResolver(rules.getObject(CDOMSizeAdjustment.class, "S"));
	}

	private FixedSizeResolver getMediumSize()
	{
		return new FixedSizeResolver(rules.getObject(CDOMSizeAdjustment.class, "M"));
	}

	private FixedSizeResolver getLargeSize()
	{
		return new FixedSizeResolver(rules.getObject(CDOMSizeAdjustment.class, "L"));
	}

	public Prerequisite getMediumPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setOperand("M");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getLargePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setOperand("L");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testRace() throws PrerequisiteException
	{
		Prerequisite prereq = getMediumPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject human = grantCDOMObject("Human");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		human.put(ObjectKey.SIZE, getSmallSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
		human.put(ObjectKey.SIZE, getMediumSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
		human.put(ObjectKey.SIZE, getLargeSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
	}

	@Test
	public void testRaceTemplate() throws PrerequisiteException
	{
		Prerequisite prereq = getMediumPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject human = grantCDOMObject("Human");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		human.put(ObjectKey.SIZE, getLargeSize());
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		CDOMObject templ = getObject(CDOMTemplate.class, "Templ");
		grantObject(templ);
		human.put(ObjectKey.SIZE, getMediumSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
		human.put(ObjectKey.SIZE, getLargeSize());
		templ.put(ObjectKey.SIZE, getSmallSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
		human.put(ObjectKey.SIZE, getSmallSize());
		templ.put(ObjectKey.SIZE, getLargeSize());
		prereq.setOperator(PrerequisiteOperator.GTEQ);
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
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getMediumPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject falseObj = grantFalseObject("Wild Mage");
		falseObj.put(ObjectKey.SIZE, getLargeSize());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?

	// TODO Need to take into account BONUSes to Size

	// TODO Need to test with Formula Resolvers

	// TODO Need to test with Monster Hit Die Advancement

}
