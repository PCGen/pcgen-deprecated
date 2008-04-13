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

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
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

	protected abstract <T extends CDOMObject> Class<T> getSubTokenType();

	protected abstract boolean isAllLegal();

	protected abstract boolean isTypeLegal();

	protected abstract boolean isTypeDotLegal();

	protected String getTypePrefix()
	{
		return "";
	}

	protected abstract boolean isPrereqLegal();

	protected abstract boolean isListLegal();

	private char getJoinCharacter()
	{
		return '|';
	}

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenString() + "|String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenString() + "|TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString() + "|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString() + "|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			if (parse(getSubTokenString() + "|" + getTypePrefix() + "TYPE="))
			{
				assertFalse(primaryContext.ref.validate());
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			if (parse(getSubTokenString() + "|" + getTypePrefix() + "TYPE=One."))
			{
				assertFalse(primaryContext.ref.validate());
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			if (parse(getSubTokenString() + "|" + getTypePrefix()
					+ "TYPE=One..Two"))
			{
				assertFalse(primaryContext.ref.validate());
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidInputTypeFalseStart()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			if (parse(getSubTokenString() + "|" + getTypePrefix() + "TYPE=.One"))
			{
				assertFalse(primaryContext.ref.validate());
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	// @Test
	// public void testInvalidInputAnyItem() throws PersistenceLayerException
	// {
	// if (isAnyLegal())
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(parse(
	// getSubTokenString() + "|ANY|TestWP1"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }
	//
	// @Test
	// public void testInvalidInputItemAny() throws PersistenceLayerException
	// {
	// if (isAnyLegal())
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(parse(
	// getSubTokenString() + "|TestWP1|ANY"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }
	//
	// @Test
	// public void testInvalidInputAnyType() throws PersistenceLayerException
	// {
	// if (isTypeLegal() && isAnyLegal())
	// {
	// assertFalse(parse(
	// getSubTokenString() + "|ANY|" + getTypePrefix() + "TYPE=TestType"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }
	//
	// @Test
	// public void testInvalidInputTypeAny() throws PersistenceLayerException
	// {
	// if (isTypeLegal() && isAnyLegal())
	// {
	// assertFalse(parse(
	// getSubTokenString() + "|" + getTypePrefix() + "TYPE=TestType|ANY"));
	// assertTrue(primaryGraph.isEmpty());
	// }
	// }

	// FIXME Need to implement!
	// @Test
	// public void testInvalidInputCheckType() throws PersistenceLayerException
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, getSubTokenString()
	// + "|" + getTypePrefix() + "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }

	@Test
	public void testInvalidEmptyPrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenString() + "|TestWP1[]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidUnterminatedPrereq()
			throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenString() + "|TestWP1[PRERACE:1,Human"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenString() + "|TestWP2||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenString() + "|TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenString() + "|TestWP1|"
					+ getTypePrefix() + "TYPE=TestType|TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenString() + "|TestWP1|"
					+ getTypePrefix() + "TYPE.TestType.OtherTestType|TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidEmbeddedPrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			assertFalse(parse(getSubTokenString()
					+ "|TestWP1[PRERACE:1,Human]|TestWP2"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString() + "|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(parse(getSubTokenString() + "|" + getTypePrefix()
					+ "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			if (isTypeDotLegal())
			{
				assertTrue(parse(getSubTokenString() + "|" + getTypePrefix()
						+ "TYPE.TestType"));
				assertTrue(primaryContext.ref.validate());
			}
			assertTrue(parse(getSubTokenString() + "|TestWP1|TestWP2|"
					+ getTypePrefix() + "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString() + "|TestWP1|TestWP2|"
					+ getTypePrefix() + "TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate());
		}
		if (isAllLegal())
		{
			assertTrue(parse(getSubTokenString() + "|ALL"));
			assertTrue(primaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|TestWP1");
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		if (isListLegal())
		{
			runRoundRobin(getSubTokenString() + "|%LIST");
		}
	}

	@Test
	public void testRoundRobinBecauseUsersAreCreative()
			throws PersistenceLayerException
	{
		if (isListLegal() && isPrereqLegal())
		{
			runRoundRobin(getSubTokenString() + "|%LIST[PRERACE:1,Human]");
		}
	}

	@Test
	public void testRoundRobinOnePrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + "|TestWP1[PRERACE:1,Human]");
		}
	}

	@Test
	public void testRoundRobinTwoPrereq() throws PersistenceLayerException
	{
		if (isPrereqLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
					+ "|TestWP1|TestWP2[PRERACE:1,Human]");
		}
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
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + "|TestWP1|TestWP2|"
					+ getTypePrefix() + "TYPE=OtherTestType|" + getTypePrefix()
					+ "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|" + getTypePrefix()
					+ "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|" + getTypePrefix()
					+ "TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	public void testInvalidInputAll() throws PersistenceLayerException
	{
		if (!isAllLegal())
		{
			try
			{
				boolean parse = parse(getSubTokenString() + "|ALL");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate());
				}
				else
				{
					assertNoSideEffects();
				}
			}
			catch (IllegalArgumentException e)
			{
				// This is okay too
				assertNoSideEffects();
			}
		}
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenString() + "|ALL" + getJoinCharacter()
					+ "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenString() + "|TestWP1"
					+ getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenString() + "|ALL" + getJoinCharacter()
					+ "" + getTypePrefix() + "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenString() + "|" + getTypePrefix()
					+ "TYPE=TestType" + getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(parse(getSubTokenString() + "|TestWP1"
					+ getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary(getSubTokenString() + "|TestWP1"
					+ getJoinCharacter() + "TestWP2"));
			assertEquals("Test setup failed", primaryGraph, secondaryGraph);
			assertFalse(parse(getSubTokenString() + "|TestWP3"
					+ getJoinCharacter() + "ALL"));
			assertEquals("Bad Add had Side Effects", primaryGraph,
					secondaryGraph);
		}
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getSubTokenType(), one);
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

}
