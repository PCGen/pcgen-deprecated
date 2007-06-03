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

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;

public class PreAbilityTesterTest extends AbstractCDOMObjectTestCase<Ability>
{

	PreFeatTester tester = new PreFeatTester();

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return Language.class;
	}

	@Override
	public String getKind()
	{
		return "FEAT";
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

	@Override
	public boolean isAnyLegal()
	{
		return false;
	}

	@Override
	public boolean isTestStarting()
	{
		return false;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public boolean isWildcardLegal()
	{
		return false;
	}
	
	@Override
	public Ability getObject(String s)
	{
		Ability a = super.getObject(s);
		a.setCDOMCategory(AbilityCategory.FEAT);
		return a;
	}


	@Test
	public void testNotAFeat() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		Ability wrong = super.getObject("Winged Mage");
		wrong.setCDOMCategory(AbilityCategory.Mutation);
		grantObject(wrong);
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		grantCDOMObject("Winged Mage");
		//Pass, as it's a FEAT
		assertEquals(1, getTest().passesCDOM(prereq, pc));
	}

	@Override
	public boolean isSubKeyAware()
	{
		return true;
	}
	
	//TODO Test subkey TYPE=
	//TODO Test subkey TYPE.
	//TODO Test Count Multiples on item w/ SubKey
	//TODO Test subkey wildcard (%) also make sure to use count, just to get empty wildcard
	
}
