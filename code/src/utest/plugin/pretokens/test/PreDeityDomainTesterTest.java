/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.pretokens.test;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreDeityDomainTesterTest extends
		AbstractCDOMPreTestTestCase<CDOMDeity>
{

	PreDeityDomainTester tester = new PreDeityDomainTester();
	CDOMReference<DomainList> domainList = getReference(DomainList.class,
			"*Starting");
	CDOMReference<CDOMDomain> water = getReference(CDOMDomain.class, "Water");
	CDOMReference<CDOMDomain> fire = getReference(CDOMDomain.class, "Fire");
	CDOMGroupRef<CDOMDomain> allDomains = new CDOMAllRef<CDOMDomain>(CDOMDomain.class);

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		allDomains.addResolution(rules.create(CDOMDomain.class, "Water"));
		allDomains.addResolution(rules.create(CDOMDomain.class, "Earth"));
		allDomains.addResolution(rules.create(CDOMDomain.class, "Fire"));
	}

	@Override
	public Class<CDOMDeity> getCDOMClass()
	{
		return CDOMDeity.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	public String getKind()
	{
		return "DEITYDOMAIN";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getAnyPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("ANY");
		p.setOperand("2");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Fire");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testAny() throws PrerequisiteException
	{
		Prerequisite prereq = getAnyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("ThisDeity");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		addToList("TestCase", deity, domainList, water);
		// not enough
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		addToList("TestCase", deity, domainList, water);
		// single object twice doesn't qualify
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		addToList("TestCase", deity, domainList, fire);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseAny() throws PrerequisiteException
	{
		Prerequisite prereq = getAnyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, getReference(CDOMDomain.class,
				"Water"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject fo = grantFalseObject("Winged Mage");
		addToList("TestCase", fo, domainList,
				getReference(CDOMDomain.class, "Fire"));
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		addToList("TestCase", deity, domainList, water);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		addToList("TestCase", deity, domainList, fire);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, water);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject fo = grantFalseObject("Winged Mage");
		addToList("TestCase", fo, domainList, fire);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testAll() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, allDomains);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testAnyAll() throws PrerequisiteException
	{
		Prerequisite prereq = getAnyPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, allDomains);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testBreakingAllAfter() throws PrerequisiteException
	{
		/*
		 * This test makes sure the ALL reference shortcuts the process -
		 * meaning that no others items are counted.
		 */
		Prerequisite prereq = getAnyPrereq();
		prereq.setOperand(Integer.toString(allDomains.getObjectCount() + 1));
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, water);
		addToList("TestCase", deity, domainList, allDomains);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperand(Integer.toString(allDomains.getObjectCount()));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testBreakingAllBefore() throws PrerequisiteException
	{
		/*
		 * This test makes sure the ALL reference shortcuts the process -
		 * meaning that no others items are counted.
		 */
		Prerequisite prereq = getAnyPrereq();
		prereq.setOperand(Integer.toString(allDomains.getObjectCount() + 1));
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		addToList("TestCase", deity, domainList, allDomains);

		addToList("TestCase", deity, domainList, water);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		prereq.setOperand(Integer.toString(allDomains.getObjectCount()));
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

}
