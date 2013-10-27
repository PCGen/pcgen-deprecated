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
package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.enumeration.EqModFormatCat;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class FormatcatTokenTest extends
		AbstractTokenTestCase<CDOMEqMod>
{

	static FormatcatToken token = new FormatcatToken();
	static CDOMTokenLoader<CDOMEqMod> loader = new CDOMTokenLoader<CDOMEqMod>(
			CDOMEqMod.class);

	@Override
	public Class<CDOMEqMod> getCDOMClass()
	{
		return CDOMEqMod.class;
	}

	@Override
	public CDOMLoader<CDOMEqMod> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMEqMod> getToken()
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
		assertTrue(parse("FRONT"));
		assertTrue(parseSecondary("FRONT"));
		assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		internalTestInvalidInputString(EqModFormatCat.FRONT);
		assertNoSideEffects();
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(parse("Always"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		// Note case sensitivity
		assertFalse(parse("Middle"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("FRONT"));
		assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		assertTrue(parse("MIDDLE"));
		assertEquals(EqModFormatCat.MIDDLE, primaryProf.get(ObjectKey.FORMAT));
		assertTrue(parse("PARENS"));
		assertEquals(EqModFormatCat.PARENS, primaryProf.get(ObjectKey.FORMAT));
	}

	@Test
	public void testRoundRobinFront() throws PersistenceLayerException
	{
		runRoundRobin("FRONT");
	}

	@Test
	public void testRoundRobinMiddle() throws PersistenceLayerException
	{
		runRoundRobin("MIDDLE");
	}

	@Test
	public void testRoundRobinParens() throws PersistenceLayerException
	{
		runRoundRobin("PARENS");
	}
}
