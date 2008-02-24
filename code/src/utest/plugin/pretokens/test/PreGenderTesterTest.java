package plugin.pretokens.test;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreGenderTesterTest extends
		AbstractCDOMPreTestTestCase<CDOMTemplate>
{

	PreGenderTester tester = new PreGenderTester();

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	private String getKind()
	{
		return "GENDER";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getNotMalePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Male");
		p.setOperator(PrerequisiteOperator.NEQ);
		return p;
	}

	public Prerequisite getFemalePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("FEMALE");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	@Test
	public void testMaleLocked() throws PrerequisiteException
	{
		Prerequisite prereq = getNotMalePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		pc.setCDOMGender(Gender.Male);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		pc.setCDOMGender(Gender.Female);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		CDOMObject templ = grantCDOMObject("Templ");
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		templ.put(ObjectKey.GENDER_LOCK, Gender.Neuter);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		templ.put(ObjectKey.GENDER_LOCK, Gender.Female);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		templ.put(ObjectKey.GENDER_LOCK, Gender.Male);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFemaleLocked() throws PrerequisiteException
	{
		Prerequisite prereq = getFemalePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		pc.setCDOMGender(Gender.Male);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		pc.setCDOMGender(Gender.Female);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		pc.setCDOMGender(Gender.Neuter);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject templ = grantCDOMObject("Templ");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		templ.put(ObjectKey.GENDER_LOCK, Gender.Male);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		templ.put(ObjectKey.GENDER_LOCK, Gender.Female);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getNotMalePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject falseObj = grantFalseObject("Wild Mage");
		falseObj.put(ObjectKey.GENDER_LOCK, Gender.Neuter);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	// TODO Need to consider inverted? !PRE?
}
