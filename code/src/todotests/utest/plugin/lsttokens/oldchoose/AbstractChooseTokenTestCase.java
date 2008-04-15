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
package utest.plugin.lsttokens.oldchoose;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractChooseTokenTestCase extends
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

	@Test
	public void testArchitecture()
	{
		/*
		 * This case is not handled well by this generic tester, and thus should
		 * be prohibited in this level of automation... - Tom Parker 6/15/2007
		 */
		assertFalse(isTypeLegal() && getJoinCharacter() == '.');
	}

	protected abstract char getJoinCharacter();

	protected abstract ChooseLstToken getSubToken();

	protected abstract <T extends CDOMObject> Class<T> getSubTokenType();

	protected abstract boolean isPrimitiveLegal();

	protected abstract boolean requiresConstruction();

	protected abstract boolean isTypeLegal();

	protected abstract boolean isAnyLegal();

	public String getSubTokenString()
	{
		return getSubToken().getTokenName();
	}

	private String prefix = "";

	protected void setPrefix(String s)
	{
		prefix = s;
	}

	protected String getPrefix()
	{
		return prefix;
	}

	@Test
	public void testInvalidNoPrefix() throws PersistenceLayerException
	{
		if (prefix.length() != 0)
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + "|String"));
		}
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		boolean parse =
				getToken().parse(primaryContext, primaryProf,
					getSubTokenString() + prefix + "|String");
		if (isPrimitiveLegal() == parse)
		{
			assertEquals(!requiresConstruction(), primaryContext.ref.validate());
		}
		else
		{
			assertEquals(!requiresConstruction(), parse);
		}
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		boolean parse =
				getToken().parse(primaryContext, primaryProf,
					getSubTokenString() + prefix + "|TestType");
		if (isPrimitiveLegal() == parse)
		{
			assertEquals(!requiresConstruction(), primaryContext.ref.validate());
		}
		else
		{
			assertEquals(!requiresConstruction(), parse);
		}
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && getJoinCharacter() != ',')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			boolean parse =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + prefix + "|TestWP1,TestWP2");
			if (parse)
			{
				assertFalse(primaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && getJoinCharacter() != '.')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			boolean parse =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + prefix + "|TestWP1.TestWP2");
			if (parse)
			{
				assertFalse(primaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && getJoinCharacter() != '|')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			boolean parse =
					getToken().parse(primaryContext, primaryProf,
						getSubTokenString() + prefix + "|TestWP1|TestWP2");
			if (parse)
			{
				assertFalse(primaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE=.One"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|ANY" + getJoinCharacter()
					+ "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TestWP1" + getJoinCharacter()
					+ "ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isAnyLegal() && isTypeLegal())
		{
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|ANY" + getJoinCharacter()
					+ "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isAnyLegal() && isTypeLegal())
		{
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TYPE=TestType"
					+ getJoinCharacter() + "ANY"));
			assertNoSideEffects();
		}
	}

	// FIXME Need to implement!
	// @Test
	// public void testInvalidInputCheckType() throws PersistenceLayerException
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, getSubTokenString()
	// + "|TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }

	@Test
	public void testInvalidEmptyBracket() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TestWP1[]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TestWP1" + getJoinCharacter()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|" + getJoinCharacter()
					+ "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TestWP2" + getJoinCharacter()
					+ getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListStartDoubleJoin()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + getJoinCharacter() + prefix + "|TestWP2"
					+ getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			// Explicitly do NOT build TestWP2
			construct(primaryContext, "TestWP1");
			boolean parse =
					getToken().parse(
						primaryContext,
						primaryProf,
						getSubTokenString() + prefix + "|TestWP1"
							+ getJoinCharacter() + "TestWP2");
			if (parse)
			{
				assertEquals(!requiresConstruction(), primaryContext.ref
					.validate());
			}
			else
			{
				assertTrue(requiresConstruction());
			}
		}
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TestWP1" + getJoinCharacter()
					+ "TYPE=TestType" + getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		if (isPrimitiveLegal() && isTypeLegal())
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the |
			construct(primaryContext, "TestWP1");
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TestWP1" + getJoinCharacter()
					+ "TYPE.TestType.OtherTestType" + getJoinCharacter()
					+ "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TestWP1"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(
				primaryContext,
				primaryProf,
				getSubTokenString() + prefix + "|TestWP1" + getJoinCharacter()
					+ "TestWP2"));
			assertTrue(primaryContext.ref.validate());
		}
		if (isTypeLegal())
		{
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(getToken().parse(primaryContext, primaryProf,
				getSubTokenString() + prefix + "|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			if (isPrimitiveLegal())
			{
				assertTrue(getToken().parse(
					primaryContext,
					primaryProf,
					getSubTokenString() + prefix + "|TestWP1"
						+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
						+ "TYPE=TestType"));
				assertTrue(primaryContext.ref.validate());
				assertTrue(getToken().parse(
					primaryContext,
					primaryProf,
					getSubTokenString() + prefix + "|TestWP1"
						+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
						+ "TYPE=TestType.OtherTestType"));
				assertTrue(primaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + prefix + "|TestWP1");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinAny() throws PersistenceLayerException
	{
		if (isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString() + prefix + "|ANY");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			construct(secondaryContext, "TestWP3");
			runRoundRobin(getSubTokenString() + prefix + "|TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isPrimitiveLegal())
		{
			if (isTypeLegal())
			{
				construct(primaryContext, "TestWP1");
				construct(primaryContext, "TestWP2");
				construct(secondaryContext, "TestWP1");
				construct(secondaryContext, "TestWP2");
				runRoundRobin(getSubTokenString() + prefix + "|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=OtherTestType" + getJoinCharacter()
					+ "TYPE=TestType");
				assertTrue(primaryContext.ref.validate());
				assertTrue(secondaryContext.ref.validate());
			}
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + prefix + "|TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + prefix
				+ "|TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
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
