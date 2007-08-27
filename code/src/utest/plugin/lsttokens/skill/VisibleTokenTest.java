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
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SkillLoader;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class VisibleTokenTest extends AbstractTokenTestCase<Skill>
{

	static VisibleToken token = new VisibleToken();
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
	public void testInvalidOutput()
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryProf.put(ObjectKey.VISIBILITY, Visibility.QUALIFY);
		assertNull(token.unparse(primaryContext, primaryProf));
		assertFalse(primaryContext.getWriteMessageCount() == 0);
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
		assertTrue(parse("EXPORT"));
		assertEquals(Visibility.EXPORT, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.EXPORT);
	}

	@Test
	public void testInvalidInputStringSetDisplay()
		throws PersistenceLayerException
	{
		assertTrue(parse("DISPLAY"));
		assertEquals(Visibility.DISPLAY, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.DISPLAY);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("Always"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		// Must be EXPORT|READONLY
		assertFalse(parse("DISPLAY|"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("DISPLAY|FLUFF"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		// Note case sensitivity
		assertFalse(parse("Display"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("DISPLAY|ReadOnly"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		assertFalse(parse("EXPORT|READONLY"));
		assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("DISPLAY"));
		assertEquals(Visibility.DISPLAY, primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(parse("EXPORT"));
		assertEquals(Visibility.EXPORT, primaryProf.get(ObjectKey.VISIBILITY));
		assertTrue(parse("YES"));
		assertEquals(Visibility.YES, primaryProf.get(ObjectKey.VISIBILITY));
	}

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("DISPLAY");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("EXPORT");
	}

	@Test
	public void testRoundRobinDisplayReadOnly()
		throws PersistenceLayerException
	{
		runRoundRobin("DISPLAY|READONLY");
	}
}
