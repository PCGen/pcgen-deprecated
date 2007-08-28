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
package plugin.lsttokens.template;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.DrLst;
import plugin.lsttokens.SaLst;
import plugin.lsttokens.SrLst;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreLevelWriter;

public class RepeatLevelTokenTest extends AbstractTokenTestCase<PCTemplate>
{
	static RepeatlevelToken token = new RepeatlevelToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new CrToken());
		TokenRegistration.register(new DrLst());
		TokenRegistration.register(new SrLst());
		TokenRegistration.register(new SaLst());
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCTemplate> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoSubcommand() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:5:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNumberOnly() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOneColon() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyStartLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20::SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadStartLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:StartLevel:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadIncrementLevel() throws PersistenceLayerException
	{
		assertFalse(parse("IncrLevel|2|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadSkipLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|SkipLevel|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadMaxLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|MaxLevel:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeStartLevel()
		throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:-4:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeIncrementLevel()
		throws PersistenceLayerException
	{
		assertFalse(parse("-1|2|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeSkipLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|-2|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyMaxLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyCommand() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|30:5::Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeMaxLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|-5:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTooManyColons() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:4:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTooManyBars() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20|40:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoMaxLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1|2:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoFirstToken() throws PersistenceLayerException
	{
		assertFalse(parse(":5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSkipLevel() throws PersistenceLayerException
	{
		assertFalse(parse("1||20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoIncrementLevel() throws PersistenceLayerException
	{
		assertFalse(parse("|3|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSubcommandArgs() throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:5:SA:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidStartGreaterThanEnd()
		throws PersistenceLayerException
	{
		assertFalse(parse("1|2|20:50:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoIncrement() throws PersistenceLayerException
	{
		assertFalse(parse("10|2|20:15:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSkipUse() throws PersistenceLayerException
	{
		assertFalse(parse("5|4|20:5:SA:Stuff"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadTemplateToken() throws PersistenceLayerException
	{
		assertFalse(parse("5|0|10:5:CR:-3"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinZeroConsecutive()
		throws PersistenceLayerException
	{
		runRoundRobin("5|0|10:5:SA:Sample Spec Abil");
	}

	@Test
	public void testRoundRobinNoIncrementBorderCase()
		throws PersistenceLayerException
	{
		runRoundRobin("5|1|10:5:SA:Sample Spec Abil");
	}

	@Test
	public void testRoundRobinNoSkipBorderCase()
		throws PersistenceLayerException
	{
		runRoundRobin("5|3|20:5:SA:Sample Spec Abil");
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("1|2|20:5:SA:Sample Spec Abil");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		runRoundRobin("1|2|20:5:SA:Sample Spec Abil",
			"2|4|20:5:SA:Sample Spec Abil");
	}

	@Test
	public void testRoundRobinMultipleSame() throws PersistenceLayerException
	{
		runRoundRobin("1|2|20:5:CR:Formula",
			"1|2|20:5:SA:Special Ability, Man!");
	}

}
