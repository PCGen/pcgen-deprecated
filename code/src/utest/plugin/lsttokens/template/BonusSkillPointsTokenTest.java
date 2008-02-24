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

import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class BonusSkillPointsTokenTest extends
		AbstractTokenTestCase<CDOMTemplate>
{

	static BonusskillpointsToken token = new BonusskillpointsToken();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInput() throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertFalse(parse("TestWP"));
		assertNoSideEffects();
		assertFalse(parse("String"));
		assertNoSideEffects();
		assertFalse(parse("TYPE=TestType"));
		assertNoSideEffects();
		assertFalse(parse("TYPE.TestType"));
		assertNoSideEffects();
		assertFalse(parse("ALL"));
		assertNoSideEffects();
		assertFalse(parse("ANY"));
		assertNoSideEffects();
		assertFalse(parse("FIVE"));
		assertNoSideEffects();
		assertFalse(parse("4.5"));
		assertNoSideEffects();
		assertFalse(parse("1/2"));
		assertNoSideEffects();
		assertFalse(parse("1+3"));
		assertNoSideEffects();
		// Require Integer greater than zero
		assertFalse(parse("-1"));
		assertNoSideEffects();
		assertFalse(parse("0"));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("5"));
		assertTrue(parse("1"));
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

}
