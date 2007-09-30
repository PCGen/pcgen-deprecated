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
package plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractEmptyChooseTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	static ChooseLst token = new ChooseLst();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	protected abstract ChooseLstToken getSubToken();

	protected abstract <T extends PObject> Class<T> getSubTokenType();

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|String"));
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString());
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

}
