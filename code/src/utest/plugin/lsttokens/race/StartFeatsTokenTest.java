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

import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreHDParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreHDWriter;
import plugin.pretokens.writer.PreLevelWriter;

public class StartFeatsTokenTest extends AbstractTokenTestCase<Race>
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
	public void testInvalidZero()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNegative()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "-5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEquation()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "1+2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidString()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "String"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDecimal()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "4.0"));
		assertTrue(primaryGraph.isEmpty());
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
