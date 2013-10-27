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
package plugin.lsttokens.pcclass;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class ProhibitspellTokenTest extends AbstractTokenTestCase<CDOMPCClass>
{

	static ProhibitspellToken token = new ProhibitspellToken();
	static CDOMTokenLoader<CDOMPCClass> loader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);

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
	}

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public CDOMLoader<CDOMPCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyType() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoType() throws PersistenceLayerException
	{
		assertFalse(parse(".Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|ALIGNMENT.Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Good|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleDot() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT..Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingDot() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Lawful."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingDotContinued()
		throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Lawful.|PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleDotSeparator()
		throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Lawful..Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDotComma() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL.,Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(parse("SPELL.Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingCommaContinued()
		throws PersistenceLayerException
	{
		assertFalse(parse("SPELL.Fireball,|PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleCommaSeparator()
		throws PersistenceLayerException
	{
		assertFalse(parse("SPELL.Fireball,,Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Good||PRECLASS:1,Fighte"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNeutral() throws PersistenceLayerException
	{
		assertFalse(parse("ALIGNMENT.Neutral"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAType() throws PersistenceLayerException
	{
		assertFalse(parse("NOTATYPE.Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoLimits() throws PersistenceLayerException
	{
		assertFalse(parse("DESCRIPTOR.Fear|DESCRIPTOR.Fire"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"PRECLASS:1,Fighter=1"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinAlignment() throws PersistenceLayerException
	{
		runRoundRobin("ALIGNMENT.Good");
	}

	@Test
	public void testRoundRobinDescriptorSimple()
		throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR.Fire");
	}

	@Test
	public void testRoundRobinDescriptorAnd() throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR.Fear.Fire");
	}

	@Test
	public void testRoundRobinSchoolSimple() throws PersistenceLayerException
	{
		runRoundRobin("SCHOOL.Evocation");
	}

	@Test
	public void testRoundRobinSubSchoolSimple()
		throws PersistenceLayerException
	{
		runRoundRobin("SUBSCHOOL.Subsch");
	}

	@Test
	public void testRoundRobinSpellSimple() throws PersistenceLayerException
	{
		runRoundRobin("SPELL.Fireball");
	}

	@Test
	public void testRoundRobinSpellComplex() throws PersistenceLayerException
	{
		runRoundRobin("SPELL.Fireball,Lightning Bolt");
	}

	@Test
	public void testInvalidInputEmbeddedPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SPELL.Fireball,Lightning Bolt|PRECLASS:1,Fighter=1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipePre()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SPELL.Fireball||PRECLASS:1,Fighter=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputPostPrePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		runRoundRobin("SUBSCHOOL.Subsch|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinTwoPre() throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR.Fear.Fire|!PRERACE:1,Human|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinNotPre() throws PersistenceLayerException
	{
		runRoundRobin("DESCRIPTOR.Fear.Fire|!PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinWWoPre() throws PersistenceLayerException
	{
		runRoundRobin("SPELL.Fireball,Lightning Bolt|PRECLASS:1,Fighter=1",
			"SUBSCHOOL.Subsch");
	}

}
