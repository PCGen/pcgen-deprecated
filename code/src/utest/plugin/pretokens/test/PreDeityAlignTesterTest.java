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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Alignment;
import pcgen.core.Deity;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreDeityAlignTesterTest extends AbstractCDOMPreTestTestCase<Deity>
{

	PreDeityAlignTester tester = new PreDeityAlignTester();
	Alignment lg;
	Alignment le;
	Alignment tn;

	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		lg = rules.create(Alignment.class, "LG");
		le = rules.create(Alignment.class, "LN");
		rules.create(Alignment.class, "LE");
		rules.create(Alignment.class, "NG");
		tn = rules.create(Alignment.class, "TN");
		rules.create(Alignment.class, "NE");
		rules.create(Alignment.class, "CG");
		rules.create(Alignment.class, "CN");
		rules.create(Alignment.class, "CE");
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Language.class;
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
		PObject deity = grantCDOMObject("Wild Mage");
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
//		PObject deity = grantCDOMObject("Wild Mage");
//		deity.put(ObjectKey.ALIGNMENT, le);
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//		PObject fo = grantFalseObject("Winged Mage");
//		fo.put(ObjectKey.ALIGNMENT, lg);
//		assertEquals(0, getTest().passesCDOM(prereq, pc));
//	}
}
