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
package plugin.lsttokens.add;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SATokenTest extends AbstractGlobalTokenTestCase
{

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(aToken);
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	static AddLst token = new AddLst();

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	private AddLstToken aToken = new SAToken();

	public String getSubTokenString()
	{
		return aToken.getTokenName();
	}

	@Test
	public void testInvalidInputOnePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNullSecond() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNullStartItem()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name|,Item"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyName() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "||Item"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNullEndItem() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name|Item,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNullMiddleItem()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name|Item,,Item2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputooManyPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|Slot Name|Item|Item2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString() + "|Slot Name|Item");
	}

	@Test
	public void testRoundRobinTwoItem() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString() + "|Slot Name|Item,Item Two");
	}

	@Test
	public void testRoundRobinTwoAdds() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString() + "|Slot Name|Item,Item Two",
			getSubTokenString() + "|Slot Too|Item Too,Item Two");
	}

	@Test
	public void testRoundRobinCountItems() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString()
			+ "|Slot Name|2|Item,Item Also,Item Last");
	}

	@Test
	public void testRoundRobinCountTwoAdds() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString()
			+ "|Slot Name|2|Item,Item Also,Item Last", getSubTokenString()
			+ "|Slot Name|3|Item,Item Also,Item Mid,Item X-Ray");
	}
}
