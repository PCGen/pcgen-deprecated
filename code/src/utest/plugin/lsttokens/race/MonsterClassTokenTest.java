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

import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class MonsterClassTokenTest extends AbstractTokenTestCase<Race>
{

	static MonsterclassToken token = new MonsterclassToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoColon() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTwoColon() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Fighter:4:1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidLevelNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:-4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidLevelZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Fighter:0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidLevelNaN() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Fighter:Level"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testBadClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Fighter:4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("Fighter:4");
	}

	@Test
	public void testMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Wizard");
		runRoundRobin("Fighter:4", "Wizard:5");
	}
}
