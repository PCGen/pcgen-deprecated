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

import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ProhibitedTokenTest extends AbstractTokenTestCase<CDOMPCClass>
{

	static ProhibitedToken token = new ProhibitedToken();
	static CDOMTokenLoader<CDOMPCClass> loader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public CDOMLoader<CDOMPCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleSeparator()
		throws PersistenceLayerException
	{
		assertFalse(parse("Lawful,,Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingComma() throws PersistenceLayerException
	{
		assertFalse(parse(",Chaos"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(parse("Evocation,"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinGood() throws PersistenceLayerException
	{
		runRoundRobin("Good");
	}

	@Test
	public void testRoundRobinSchoolMultiple() throws PersistenceLayerException
	{
		runRoundRobin("Divination,Evocation");
	}
}
