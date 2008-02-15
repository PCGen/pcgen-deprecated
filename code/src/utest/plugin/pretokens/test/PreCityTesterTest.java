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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.character.CharacterDataStore;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreCityTesterTest extends TestCase
{

	PreCityTester tester = new PreCityTester();

	CharacterDataStore pc;

	@Override
	@Before
	public void setUp()
	{
		pc = new CharacterDataStore(new SimpleRulesDataStore());
	}

	public String getKind()
	{
		return "CITY";
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

	public Prerequisite getAustraliaPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Australia");
		p.setOperator(PrerequisiteOperator.EQ);
		return p;
	}

	@Test
	public void testCharacter() throws PrerequisiteException
	{
		Prerequisite nypre = getNewYorkPrereq();
		Prerequisite flpre = getAustraliaPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(flpre, pc));
		assertEquals(0, getTest().passesCDOM(nypre, pc));
		pc.put(StringKey.RESIDENCE, "Australia");
		assertEquals(1, getTest().passesCDOM(flpre, pc));
		assertEquals(0, getTest().passesCDOM(nypre, pc));
	}
}
