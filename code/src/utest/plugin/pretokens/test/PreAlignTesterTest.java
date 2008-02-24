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

public class PreAlignTesterTest extends AbstractCDOMPreTestTestCase<CDOMAlignment>
{

	PreAlignTester tester = new PreAlignTester();

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
	public Class<CDOMAlignment> getCDOMClass()
	{
		return CDOMAlignment.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	public String getKind()
	{
		return "ALIGN";
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
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	public Prerequisite getDeityPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Deity");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	@Test
	public void testFalse() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantObject(le);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantObject(lg);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantObject(tn);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantFalseObject("LG");
		assertEquals(0, getTest().passesCDOM(prereq, pc));
	}

	@Test
	public void testDeity() throws PrerequisiteException
	{
		Prerequisite prereq = getDeityPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantObject(lg);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMDeity deity = getObject(CDOMDeity.class, "MyDeity");
		grantObject(deity);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, le);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, lg);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}
}
