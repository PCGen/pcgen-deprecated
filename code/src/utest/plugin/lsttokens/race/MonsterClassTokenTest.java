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
package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMRace;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class MonsterClassTokenTest extends AbstractTokenTestCase<CDOMRace>
{

	static MonsterclassToken token = new MonsterclassToken();
	static CDOMTokenLoader<CDOMRace> loader = new CDOMTokenLoader<CDOMRace>(
			CDOMRace.class);

	@Override
	public Class<CDOMRace> getCDOMClass()
	{
		return CDOMRace.class;
	}

	@Override
	public CDOMLoader<CDOMRace> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMRace> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoColon() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTwoColon() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:4:1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLevelNegative() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:-4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLevelZero() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLevelNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:Level"));
		assertNoSideEffects();
	}

	@Test
	public void testBadClass() throws PersistenceLayerException
	{
		assertTrue(parse("Fighter:4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		runRoundRobin("Fighter:4");
	}

	@Test
	public void testMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(CDOMPCClass.class, "Wizard");
		runRoundRobin("Fighter:4", "Wizard:5");
	}
}
