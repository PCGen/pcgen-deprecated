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

public class PreAlignTesterTest extends AbstractCDOMPreTestTestCase<Alignment>
{

	PreAlignTester tester = new PreAlignTester();

	Alignment lg;
	Alignment le;
	Alignment tn;

	@Before
	@Override
	public void setUp()
	{
		super.setUp();
		lg = context.ref.constructCDOMObject(Alignment.class, "LG");
		le = context.ref.constructCDOMObject(Alignment.class, "LN");
		context.ref.constructCDOMObject(Alignment.class, "LE");
		context.ref.constructCDOMObject(Alignment.class, "NG");
		tn = context.ref.constructCDOMObject(Alignment.class, "TN");
		context.ref.constructCDOMObject(Alignment.class, "NE");
		context.ref.constructCDOMObject(Alignment.class, "CG");
		context.ref.constructCDOMObject(Alignment.class, "CN");
		context.ref.constructCDOMObject(Alignment.class, "CE");
	}

	@Override
	public Class<Alignment> getCDOMClass()
	{
		return Alignment.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Language.class;
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
		Deity deity = getObject(Deity.class, "MyDeity");
		grantObject(deity);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, le);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		deity.put(ObjectKey.ALIGNMENT, lg);
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}
}
