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

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class NameispiLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new NameispiLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(Boolean.FALSE);
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(parse("YES"));
		assertTrue(primaryProf.isNamePI());
		internalTestInvalidInputString(Boolean.TRUE);
		assertTrue(primaryGraph.isEmpty());
	}

	public void internalTestInvalidInputString(Boolean val)
		throws PersistenceLayerException
	{
		assertEquals(val, Boolean.valueOf(primaryProf.isNamePI()));
		assertFalse(parse("String"));
		assertEquals(val, Boolean.valueOf(primaryProf.isNamePI()));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, Boolean.valueOf(primaryProf.isNamePI()));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, Boolean.valueOf(primaryProf.isNamePI()));
		assertFalse(parse("ALL"));
		assertEquals(val, Boolean.valueOf(primaryProf.isNamePI()));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("YES"));
		assertTrue(primaryProf.isNamePI());
		assertTrue(parse("NO"));
		assertFalse(primaryProf.isNamePI());
		// We're nice enough to be case insensitive here...
		assertTrue(parse("YeS"));
		assertTrue(primaryProf.isNamePI());
		assertTrue(parse("Yes"));
		assertTrue(primaryProf.isNamePI());
		assertTrue(parse("No"));
		assertFalse(primaryProf.isNamePI());
		// And we also allow single characters
		assertTrue(parse("Y"));
		assertTrue(primaryProf.isNamePI());
		assertTrue(parse("N"));
		assertFalse(primaryProf.isNamePI());
		assertTrue(parse("y"));
		assertTrue(primaryProf.isNamePI());
		assertTrue(parse("n"));
		assertFalse(primaryProf.isNamePI());
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
