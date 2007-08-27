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
package plugin.lsttokens.pcclass;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.PCClassLoaderFacade;

public class IsMonsterTokenTest extends AbstractTokenTestCase<PCClass>
{

	static IsmonsterToken token = new IsmonsterToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Before
	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		/*
		 * FIXME This construction of *Starting should be unnecessary
		 */
		primaryContext.ref
			.constructCDOMObject(ClassSkillList.class, "*Monster");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
			"*Monster");
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCClass> getToken()
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
		assertTrue(parse("YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.IS_MONSTER));
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.IS_MONSTER));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(ObjectKey.IS_MONSTER));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.IS_MONSTER));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.IS_MONSTER));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.IS_MONSTER));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("YES"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.IS_MONSTER));
		assertTrue(parse("NO"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.IS_MONSTER));
		// We're nice enough to be case insensitive here...
		assertTrue(parse("YeS"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.IS_MONSTER));
		assertTrue(parse("Yes"));
		assertEquals(Boolean.TRUE, primaryProf.get(ObjectKey.IS_MONSTER));
		assertTrue(parse("No"));
		assertEquals(Boolean.FALSE, primaryProf.get(ObjectKey.IS_MONSTER));
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}
}
