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
package plugin.lsttokens.auto;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public abstract class AbstractAutoTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	static AutoLst token = new AutoLst();

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
		TokenRegistration.register(getSubToken());
	}

	protected abstract AutoLstToken getSubToken();

	protected abstract <T extends PObject> Class<T> getSubTokenType();

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE="));
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE=One."));
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE=One..Two"));
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE=.One"));
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(getToken().parse(primaryContext, primaryProf,
	// getSubTokenString() + "|ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(getToken().parse(primaryContext, primaryProf,
	// getSubTokenString() + "|ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf,
	// getSubTokenString() + "|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testInvalidEmptyPrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1[]"));
	}

	@Test
	public void testInvalidUnterminatedPrereq()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1[PRERACE:1,Human"));
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|"));
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "||TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP2||TestWP1"));
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|TYPE=TestType|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|TestWP1|TYPE.TestType.OtherTestType|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidEmbeddedPrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1[PRERACE:1,Human]|TestWP2"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TYPE.TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|TestWP1|TestWP2|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|TestWP1|TestWP2|TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|TestWP1[PRERACE:1,Human]");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenString() + "|TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString()
			+ "|TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|TYPE=TestType");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString()
			+ "|TYPE=TestAltType.TestThirdType.TestType");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getSubTokenType(), one);
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

}
