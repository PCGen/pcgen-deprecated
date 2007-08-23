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

	private static AddLstToken aToken = new AbilityToken();

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
		loadContext.ref.reassociateReference(AbilityCategory.Mutation, ab);
	}

	@Test
	public void testInvalidOnlyCount() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOneArg() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Mutation"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoArg() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Mutation|NORMAL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputThreeArg() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|2|Mutation|NORMAL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAnAbility() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenString() + "|1|Mutation|NORMAL|Abil1"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|1|Mutation|NORMAL|TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|1|Mutation|NORMAL|TYPE=One."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TYPE=One..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeFalseStart()
		throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|1|Mutation|NORMAL|TYPE=.One"));
		assertNoSideEffects();
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse(
	// getSubTokenString() + "|ALL"));
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
		assertFalse(parse(getSubTokenString() + "|1|Mutation|NORMAL|TestWP1,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|1|Mutation|NORMAL|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|0|Mutation|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|-4|Mutation|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP2,,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP1,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the ,
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP1,TYPE=TestType,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
		throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the ,
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenString()
			+ "|1|Mutation|NORMAL|TestWP1,TYPE.TestType.OtherTestType,TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenString() + "|Mutation|NORMAL|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|Mutation|NORMAL|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString() + "|Mutation|NORMAL|TYPE.TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2,TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|2|Mutation|NORMAL|TestWP1,TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|2|Mutation|NORMAL|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|6|Mutation|NORMAL|TYPE.TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|8|Mutation|NORMAL|TestWP1,TestWP2,TYPE=TestType"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse(getSubTokenString()
			+ "|3|Mutation|NORMAL|TestWP1,TestWP2,TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString() + "|Mutation|NORMAL|TestWP1");
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
			+ "|Mutation|NORMAL|TestWP1,TestWP2,TestWP3");
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|Mutation|NORMAL|TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString()
			+ "|Mutation|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
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
			+ "|2|Mutation|NORMAL|TestWP1,TestWP2,TestWP3");
	}

	@Test
	public void testRoundRobinCountThreeWithEqualType()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenString()
			+ "|3|Mutation|NORMAL|TestWP1,TestWP2,TYPE=OtherTestType,TYPE=TestType");
	}

	@Test
	public void testRoundRobinCountTwoTestEquals()
		throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString() + "|2|Mutation|NORMAL|TYPE=TestType");
	}

	@Test
	public void testRoundRobinCountFourTestEqualThree()
		throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenString()
			+ "|4|Mutation|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|Mutation|ANY,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenString() + "|Mutation|TestWP1,ANY"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Mutation|ANY,TYPE=TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenString() + "|Mutation|TYPE=TestType,ANY"));
		assertNoSideEffects();
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

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInputInvalidAddsTypeNoSideEffect()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertTrue(parseSecondary(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertEquals("Test setup failed", primaryGraph, secondaryGraph);
		assertFalse(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP3,TYPE="));
		assertEquals("Bad Add had Side Effects", primaryGraph, secondaryGraph);
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		construct(primaryContext, "TestWP4");
		construct(secondaryContext, "TestWP4");
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertTrue(parseSecondary(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertEquals("Test setup failed", primaryGraph, secondaryGraph);
		assertFalse(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP3,,TestWP4"));
		assertEquals("Bad Add had Side Effects", primaryGraph, secondaryGraph);
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		assertTrue(parse(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertTrue(parseSecondary(getSubTokenString()
			+ "|Mutation|NORMAL|TestWP1,TestWP2"));
		assertEquals("Test setup failed", primaryGraph, secondaryGraph);
		assertFalse(parse(getSubTokenString() + "|Mutation|NORMAL|TestWP3,ANY"));
		assertEquals("Bad Add had Side Effects", primaryGraph, secondaryGraph);
	}
}
