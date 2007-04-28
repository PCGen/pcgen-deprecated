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
package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class GenderLockTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static GenderlockToken token = new GenderlockToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString()
	{
		internalTestInvalidInputString(null);
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputStringSet()
	{
		assertTrue(token.parse(primaryContext, primaryProf, "Male"));
		assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
		internalTestInvalidInputString(Gender.Male);
		assertTrue(primaryGraph.isEmpty());
	}

	public void internalTestInvalidInputString(Object val)
	{
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(token.parse(primaryContext, primaryProf, "Always"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		//Note case sensitivity
		assertFalse(token.parse(primaryContext, primaryProf, "MALE"));
	}

	@Test
	public void testValidInputs()
	{
		assertTrue(token.parse(primaryContext, primaryProf, "Male"));
		assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertTrue(token.parse(primaryContext, primaryProf, "Female"));
		assertEquals(Gender.Female, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertTrue(token.parse(primaryContext, primaryProf, "Neuter"));
		assertEquals(Gender.Neuter, primaryProf.get(ObjectKey.GENDER_LOCK));
	}

	@Test
	public void testRoundRobinMale() throws PersistenceLayerException
	{
		runRoundRobin("Male");
	}

	@Test
	public void testRoundRobinFemale() throws PersistenceLayerException
	{
		runRoundRobin("Female");
	}

	@Test
	public void testRoundRobinNeuter() throws PersistenceLayerException
	{
		runRoundRobin("Neuter");
	}

}
