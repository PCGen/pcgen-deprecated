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

import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreRegionTesterTest extends AbstractCDOMPreTestTestCase<PCTemplate>
{

	PreRegionTester tester = new PreRegionTester();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Race.class;
	}

	public String getKind()
	{
		return "REGION";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public Prerequisite getNewYorkPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("New York");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	public Prerequisite getFingerLakesPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("New York (Finger Lakes)");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	@Test
	public void testCharacter() throws PrerequisiteException
	{
		Prerequisite nypre = getNewYorkPrereq();
		Prerequisite flpre = getFingerLakesPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.REGION, "New York");
		assertEquals(1, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.SUB_REGION, "Finger Lakes");
		assertEquals(1, getTest().passesCDOM(nypre, pc));
		assertEquals(1, getTest().passesCDOM(flpre, pc));
	}

	@Test
	public void testCharacterRegionTemplate() throws PrerequisiteException
	{
		Prerequisite nypre = getNewYorkPrereq();
		Prerequisite flpre = getFingerLakesPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		PObject template = grantCDOMObject("Template 1");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		template.put(StringKey.REGION, "New York");
		assertEquals(1, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		template.put(StringKey.SUB_REGION, "New York City");
		assertEquals(1, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.SUB_REGION, "Finger Lakes");
		assertEquals(1, getTest().passesCDOM(nypre, pc));
		assertEquals(1, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.REGION, "Australia");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
	}
	
	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite nypre = getNewYorkPrereq();
		Prerequisite flpre = getFingerLakesPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.REGION, "Australia");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		pc.setStringFor(StringKey.SUB_REGION, "Finger Lakes");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		PObject falseObj = grantFalseObject("Template 1");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		falseObj.put(StringKey.REGION, "New York");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		falseObj.put(StringKey.SUB_REGION, "New York City");
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		assertEquals(0, getTest().passesCDOM(flpre, pc));
	}

	// TODO Need to consider inverted? !PRE?

}
