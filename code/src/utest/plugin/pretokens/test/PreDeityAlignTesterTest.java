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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreDeityAlignTesterTest extends AbstractCDOMPreTestTestCase<CDOMDeity>
{

	PreDeityAlignTester tester = new PreDeityAlignTester();
	CDOMAlignment lg;
	CDOMAlignment le;
	CDOMAlignment tn;

	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		lg = rules.create(CDOMAlignment.class, "LG");
		le = rules.create(CDOMAlignment.class, "LN");
		rules.create(CDOMAlignment.class, "LE");
		rules.create(CDOMAlignment.class, "NG");
		tn = rules.create(CDOMAlignment.class, "TN");
		rules.create(CDOMAlignment.class, "NE");
		rules.create(CDOMAlignment.class, "CG");
		rules.create(CDOMAlignment.class, "CN");
		rules.create(CDOMAlignment.class, "CE");
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
		return "DEITYALIGN";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("LG");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMObject deity = grantCDOMObject("Wild Mage");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, tn);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, lg);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

//	@Test
//	public void testFalseObject() throws PrerequisiteException
//	{
//		Prerequisite prereq = getSimplePrereq();
//		// PC Should start without
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		CDOMObject deity = grantCDOMObject("Wild Mage");
//		deity.put(ObjectKey.ALIGNMENT, le);
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		CDOMObject fo = grantFalseObject("Winged Mage");
//		fo.put(ObjectKey.ALIGNMENT, lg);
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//	}
}
