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
package plugin.lsttokens.ability;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbilityLoader;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class MultTokenTest extends AbstractTokenTestCase<Ability>
{

	static MultToken token = new MultToken();
	static AbilityLoader loader = new AbilityLoader();

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public LstObjectFileLoader<Ability> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Ability> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		internalTestInvalidInputString(Boolean.TRUE);
		assertTrue(primaryGraph.isEmpty());
	}

	public void internalTestInvalidInputString(Boolean val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.MULTIPLE_ALLOWED));
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertEquals(val, primaryProf.get(ObjectKey.MULTIPLE_ALLOWED));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.MULTIPLE_ALLOWED));
		assertFalse(token.parse(primaryContext, primaryProf, "TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.MULTIPLE_ALLOWED));
		assertFalse(token.parse(primaryContext, primaryProf, "ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.MULTIPLE_ALLOWED));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(token.parse(primaryContext, primaryProf, "YES"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "NO"));
		assertFalse(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		// We're nice enough to be case insensitive here...
		assertTrue(token.parse(primaryContext, primaryProf, "YeS"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "Yes"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "No"));
		assertFalse(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		// And we also allow single characters
		assertTrue(token.parse(primaryContext, primaryProf, "Y"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "N"));
		assertFalse(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "y"));
		assertTrue(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
		assertTrue(token.parse(primaryContext, primaryProf, "n"));
		assertFalse(primaryProf.get(ObjectKey.MULTIPLE_ALLOWED).booleanValue());
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
}
