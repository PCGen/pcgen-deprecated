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

import pcgen.cdom.inst.CDOMRace;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class HitDiceAdvancementTokenTest extends AbstractTokenTestCase<CDOMRace>
{

	static HitdiceadvancementToken token = new HitdiceadvancementToken();
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
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertNoSideEffects();
	}

	// @Test
	// public void testInvalidTooManyValues() throws PersistenceLayerException
	// {
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "1,2,3,4,5,6,7,8,9,0"));
	// }

	@Test
	public void testInvalidEmptyValue1() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ",2,3,4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyValue2() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1,,3,4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyValueLast() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1,2,3,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "-1,2,3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDecreasingValue() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,3,8"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmbeddedSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,*,8"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNaN() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,N"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTooMuchSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,8*"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTooMuchAfterSplat() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "5,*8"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSingle() throws PersistenceLayerException
	{
		this.runRoundRobin("1");
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin("1,2,3");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		this.runRoundRobin("5,7,9,*");
	}
}
