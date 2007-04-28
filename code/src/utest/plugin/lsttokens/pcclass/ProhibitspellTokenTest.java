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

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class ProhibitspellTokenTest extends AbstractTokenTestCase<PCClass>
{

	static ProhibitspellToken token = new ProhibitspellToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

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
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOnlyType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALIGNMENT"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "ALIGNMENT."));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ".Good"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"|ALIGNMENT.Good"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Good|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleDot() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT..Good"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingDot() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Lawful."));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingDotContinued()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Lawful.|PRECLASS:1,Fighter"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleDotSeparator()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Lawful..Good"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDotComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"SPELL.,Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"SPELL.Fireball,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingCommaContinued()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"SPELL.Fireball,|PRECLASS:1,Fighter"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleCommaSeparator()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"SPELL.Fireball,,Lightning Bolt"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Good||PRECLASS:1,Fighte"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNeutral() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"ALIGNMENT.Neutral"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotAType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"NOTATYPE.Good"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTwoLimits() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"DESCRIPTOR.Fear|DESCRIPTOR.Fire"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOnlyPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"PRECLASS:1,Fighter=1"));
		assertTrue(primaryGraph.isEmpty());
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
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipePre()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SPELL.Fireball||PRECLASS:1,Fighter=1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputPostPrePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"TestWP1|PRECLASS:1,Fighter=1|"));
		assertTrue(primaryGraph.isEmpty());
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
