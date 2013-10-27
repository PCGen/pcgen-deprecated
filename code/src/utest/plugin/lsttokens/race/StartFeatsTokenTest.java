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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMRace;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreHDParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreHDWriter;
import plugin.pretokens.writer.PreLevelWriter;

public class StartFeatsTokenTest extends AbstractTokenTestCase<CDOMRace>
{

	PreHDParser prehd = new PreHDParser();
	PreHDWriter prehdwriter = new PreHDWriter();
	PreLevelParser prelevel = new PreLevelParser();
	PreLevelWriter prelevelwriter = new PreLevelWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prehd);
		TokenRegistration.register(prehdwriter);
		TokenRegistration.register(prelevel);
		TokenRegistration.register(prelevelwriter);
	}

	static StartfeatsToken token = new StartfeatsToken();
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
	public void testInvalidZero()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegative()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "-5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEquation()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1+2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidString()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDecimal()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "4.0"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}
}
