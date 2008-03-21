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
package plugin.lsttokens;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class DescispiLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new DescispiLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
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
		assertTrue(parse("YES"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		internalTestInvalidInputString(Boolean.TRUE);
		assertTrue(primaryGraph.isEmpty());
	}

	public void internalTestInvalidInputString(Boolean val)
			throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.DESC_PI));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(ObjectKey.DESC_PI));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.DESC_PI));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.DESC_PI));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.DESC_PI));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("YES"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("NO"));
		assertFalse(primaryProf.get(ObjectKey.DESC_PI));
		// We're nice enough to be case insensitive here...
		assertTrue(parse("YeS"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("Yes"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("No"));
		assertFalse(primaryProf.get(ObjectKey.DESC_PI));
		// And we also allow single characters
		assertTrue(parse("Y"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("N"));
		assertFalse(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("y"));
		assertTrue(primaryProf.get(ObjectKey.DESC_PI));
		assertTrue(parse("n"));
		assertFalse(primaryProf.get(ObjectKey.DESC_PI));
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
