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
package plugin.lsttokens.skill;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ACheckTokenTest extends AbstractTokenTestCase<Skill>
{

	static AcheckToken token = new AcheckToken();
	static SkillLoader loader = new SkillLoader();

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public LstObjectFileLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Skill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(SkillArmorCheck.YES, primaryProf
			.get(ObjectKey.ARMOR_CHECK));
		internalTestInvalidInputString(SkillArmorCheck.YES);
		assertTrue(primaryGraph.isEmpty());
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		//Note case sensitivity
		assertFalse(token.parse(primaryContext, primaryProf, "Yes"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "No"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertFalse(token.parse(primaryContext, primaryProf, "Double"));
		assertEquals(val, primaryProf.get(ObjectKey.ARMOR_CHECK));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertEquals(SkillArmorCheck.YES, primaryProf
			.get(ObjectKey.ARMOR_CHECK));
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertEquals(SkillArmorCheck.NO, primaryProf.get(ObjectKey.ARMOR_CHECK));
		assertTrue(token.parse(primaryContext, primaryProf, "PROFICIENT"));
		assertEquals(SkillArmorCheck.PROFICIENT, primaryProf
			.get(ObjectKey.ARMOR_CHECK));
		assertTrue(token.parse(primaryContext, primaryProf, "DOUBLE"));
		assertEquals(SkillArmorCheck.DOUBLE, primaryProf
			.get(ObjectKey.ARMOR_CHECK));
		assertTrue(token.parse(primaryContext, primaryProf, "WEIGHT"));
		assertEquals(SkillArmorCheck.WEIGHT, primaryProf
			.get(ObjectKey.ARMOR_CHECK));
	}

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinNo() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}

	@Test
	public void testRoundRobinProficient() throws PersistenceLayerException
	{
		runRoundRobin("PROFICIENT");
	}

	@Test
	public void testRoundRobinDouble() throws PersistenceLayerException
	{
		runRoundRobin("DOUBLE");
	}

	@Test
	public void testRoundRobinWeight() throws PersistenceLayerException
	{
		runRoundRobin("WEIGHT");
	}
}
