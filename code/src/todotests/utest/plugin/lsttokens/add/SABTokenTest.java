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
package utest.plugin.lsttokens.add;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.add.SABToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SABTokenTest extends AbstractGlobalTokenTestCase
{

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(aToken);
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	static AddLst token = new AddLst();

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	private static AddLstToken aToken = new SABToken();

	public String getSubTokenString()
	{
		return aToken.getTokenName();
	}

	@Test
	public void testInvalidInputOnePipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNullSecond() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNullStartItem()
		throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|,Item"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyName() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "||Item"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNullEndItem() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|Item,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNullMiddleItem()
		throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|Item,,Item2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputooManyPipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|Item|Item2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|0|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Slot Name|-4|TestWP1"));
		assertNoSideEffects();
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
		//Order seems backwards, but it sorts by list contents, not name
		this.runRoundRobin(getSubTokenString() + "|Slot Too|Item Too,Item Two",
			getSubTokenString() + "|Slot Name|Item,Item Two");
	}

	@Test
	public void testRoundRobinCountItems() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString()
			+ "|Slot Name|2|Item,Item Also,Item Last");
	}

	@Test
	public void testRoundRobinCountTwoAddsSameCount()
		throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString()
			+ "|Slot Name|2|Item,Item Also,Item Last", getSubTokenString()
			+ "|Slot Too|2|Item,Item Also,Item Mid,Item X-Ray");
	}

	@Test
	public void testRoundRobinCountTwoAdds() throws PersistenceLayerException
	{
		this.runRoundRobin(getSubTokenString()
			+ "|Slot Name|2|Item,Item Also,Item Last", getSubTokenString()
			+ "|Slot Too|3|Item,Item Also,Item Mid,Item X-Ray");
	}
}
