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

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class AbilityTokenTest extends AbstractGlobalTokenTestCase
{

	private AddLstToken aToken = new AbilityToken();

	private String getSubTokenString()
	{
		return "ABILITY";
	}

	protected AddLstToken getSubToken()
	{
		return aToken;
	}

	protected Class<Ability> getSubTokenType()
	{
		return Ability.class;
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

	protected void construct(LoadContext loadContext, String one)
	{
		Ability ab =
				loadContext.ref.constructCDOMObject(getSubTokenType(), one);
		loadContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	}

	@Test
	public void testInvalidInputOneArg() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoArg() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputThreeArg() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|2|FEAT|NORMAL"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotAnAbility() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|Abil1"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TestWP1|TestWP2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TYPE="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TYPE=One."));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TYPE=One..Two"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TYPE=.One"));
		assertTrue(primaryGraph.isEmpty());
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
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TestWP1,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|,TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|0|FEAT|NORMAL|TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|-4|FEAT|NORMAL|TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TestWP2,,TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|1|FEAT|NORMAL|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the ,
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|1|FEAT|NORMAL|TestWP1,TYPE=TestType,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the ,
		construct(primaryContext, "TestWP1");
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|1|FEAT|NORMAL|TestWP1,TYPE.TestType.OtherTestType,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL|TYPE.TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|NORMAL|TestWP1,TestWP2,TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|FEAT|NORMAL|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|2|FEAT|NORMAL|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|2|FEAT|NORMAL|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|6|FEAT|NORMAL|TYPE.TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|8|FEAT|NORMAL|TestWP1,TestWP2,TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|3|FEAT|NORMAL|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|FEAT|NORMAL|TestWP1");
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
		runRoundRobin(getSubTokenString()
			+ "|FEAT|NORMAL|TestWP1,TestWP2,TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (true)
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
				+ "|FEAT|NORMAL|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (true)
		{
			runRoundRobin(getSubTokenString() + "|FEAT|NORMAL|TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (true)
		{
			runRoundRobin(getSubTokenString()
				+ "|FEAT|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
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
		runRoundRobin(getSubTokenString()
			+ "|2|FEAT|NORMAL|TestWP1,TestWP2,TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinCountThreeWithEqualType()
		throws PersistenceLayerException
	{
		if (true)
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenString()
				+ "|3|FEAT|NORMAL|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinCountTwoTestEquals()
		throws PersistenceLayerException
	{
		if (true)
		{
			runRoundRobin(getSubTokenString() + "|2|FEAT|NORMAL|TYPE=TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinCountFourTestEqualThree()
		throws PersistenceLayerException
	{
		if (true)
		{
			runRoundRobin(getSubTokenString()
				+ "|4|FEAT|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
			assertTrue(primaryContext.ref.validate());
			assertTrue(secondaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|ALL|TestWP1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|TestWP1|ALL"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|ALL|TYPE=TestType"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|FEAT|TYPE=TestType|ALL"));
		assertTrue(primaryGraph.isEmpty());
	}

	static AddLst token = new AddLst();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}
}
