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
package plugin.lsttokens.remove;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.RemoveLstToken;
import plugin.lsttokens.RemoveLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public abstract class AbstractRemoveTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	static RemoveLst token = new RemoveLst();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	protected abstract RemoveLstToken getSubToken();

	protected abstract <T extends PObject> Class<T> getSubTokenType();

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
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenString() + "|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString() + "|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	public abstract boolean isTypeLegal();

	public abstract boolean isAnyLegal();

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		try
		{
			boolean result = parse(getSubTokenString() + "|TYPE=");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertNoSideEffects();
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		try
		{
			boolean result = parse(getSubTokenString() + "|TYPE=One.");
			if (isTypeLegal())
			{
				assertFalse(result);
				assertNoSideEffects();
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		try
		{
			boolean result = parse(getSubTokenString() + "|TYPE=One..Two");
			if (isTypeLegal())
			{
				assertNoSideEffects();
				assertFalse(result);
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		try
		{
			boolean result = parse(getSubTokenString() + "|TYPE=.One");
			if (isTypeLegal())
			{
				assertNoSideEffects();
				assertFalse(result);
			}
			else
			{
				assertTrue(result);
				assertFalse(primaryContext.ref.validate());
			}
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				// Should have returned false;
				throw e;
			}
			assertNoSideEffects();
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse(
	// getSubTokenString() + "|ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse(
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
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|TestWP1,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|0|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|-4|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenString() + "|TestWP2,,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenString() + "|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		try
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the ,
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenString()
				+ "|TestWP1,TYPE=TestType,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				throw e;
			}
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		try
		{
			// Explicitly do NOT build TestWP2 (this checks that the TYPE=
			// doesn't
			// consume the ,
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenString()
				+ "|TestWP1,TYPE.TestType.OtherTestType,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
		catch (IllegalArgumentException e)
		{
			if (isTypeLegal())
			{
				throw e;
			}
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
		assertTrue(parse(getSubTokenString() + "|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(parse(getSubTokenString() + "|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString() + "|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString()
				+ "|TestWP1,TestWP2,TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString()
				+ "|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		}
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|2|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		if (isTypeLegal())
		{
			assertTrue(parse(getSubTokenString() + "|2|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString() + "|6|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString()
				+ "|8|TestWP1,TestWP2,TYPE=TestType"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString()
				+ "|3|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate());
		}
		if (isAnyLegal())
		{
			assertTrue(parse(getSubTokenString() + "|ANY"));
			assertTrue(primaryContext.ref.validate());
			assertTrue(parse(getSubTokenString() + "|2|ANY"));
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
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenString() + "|TestWP1,TestWP2,TestWP3");
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
			runRoundRobin(getSubTokenString()
				+ "|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString()
				+ "|TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	public void testRoundRobinTwoCountThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenString() + "|2|TestWP1,TestWP2,TestWP3");
	}

	@Test
	public void testRoundRobinCountThreeWithEqualType()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
				+ "|3|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinCountTwoTestEquals()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString() + "|2|TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinCountFourTestEqualThree()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenString()
				+ "|4|TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("ANY|TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAnyLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("TestWP1|ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAnyLegal())
		{
			assertFalse(parse("ANY|TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAnyLegal())
		{
			assertFalse(parse("TYPE=TestType|ANY"));
			assertNoSideEffects();
		}
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
