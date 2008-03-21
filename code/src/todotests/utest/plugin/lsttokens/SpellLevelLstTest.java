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
package utest.plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.ClassSpellList;
import pcgen.cdom.inst.DomainSpellList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.SpelllevelLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class SpellLevelLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new SpelllevelLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

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
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
	}

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClassOnly() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClassBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyClass() throws PersistenceLayerException
	{
		assertFalse(parse("|Cleric=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptySpell() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellCommaStarting()
		throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|,Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellCommaEnding() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellDoubleComma() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|Fireball,,Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellDoublePipe1() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSpellLevel() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellLevelNaN() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=CL|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellLevelDecimal() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=4.5|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellLevelNoEquals()
		throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric 4|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellLevelJustLevel()
		throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|4|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidForgotSpell() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=4|PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellLevelDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric==5|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellDoublePipe2() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellDoublePipe3() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|Fireball||Druid=2|Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptySPELLCASTER() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("CLASS|SPELLCASTER.=1|Fireball"));
		}
		catch (IllegalArgumentException e)
		{
			//This is okay too ;)
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS|Cleric=1|Fireball|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS||Cleric=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidFirstArgBad() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|Cleric=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball,Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoLevel() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball|Cleric=2|Lightning Bolt");
	}

	@Test
	public void testValidInputTwoClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		assertTrue(parse("CLASS|Cleric,Druid=1|Fireball,Lightning Bolt"));
	}

	@Test
	public void testRoundRobinTwoDiffClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		runRoundRobin("CLASS|Cleric=1|Fireball|Druid=2|Lightning Bolt");
	}

	@Test
	public void testInvalidDomainOnly() throws PersistenceLayerException
	{
		assertFalse(parse("CLASS"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyDomain() throws PersistenceLayerException
	{
		assertFalse(parse("|Cleric=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainEmptySpell() throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellCommaStarting()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|,Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellCommaEnding()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellDoubleComma()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|Fireball,,Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellDoublePipe1()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainNoSpellLevel()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellLevelNaN()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=CL|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellLevelDecimal()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=4.5|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellLevelDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric==5|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellDoublePipe2()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainSpellDoublePipe3()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|Fireball||Druid=2|Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainEmptySPELLCASTER()
		throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("DOMAIN|SPELLCASTER.=1|Fireball"));
		}
		catch (IllegalArgumentException e)
		{
			//This is okay too ;)
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainTrailingPipe()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=1|Fireball|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN||Cleric=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainCasterCommaTrailing()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric,=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainCasterCommaLeading()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|,Cleric=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainCasterCommaDouble()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric,,Druid=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDomainCasterDoubleAssignAttempt()
		throws PersistenceLayerException
	{
		assertFalse(parse("DOMAIN|Cleric=4,Druid=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testDomainRoundRobinJustSpell()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball");
	}

	@Test
	public void testDomainRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball,Lightning Bolt");
	}

	@Test
	public void testDomainRoundRobinTwoLevel() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|Cleric=2|Lightning Bolt");
	}

	@Test
	public void testDomainRoundRobinTwoDomain()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Druid");
		secondaryContext.ref
			.constructCDOMObject(DomainSpellList.class, "Druid");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|Druid=2|Lightning Bolt");
	}

	@Test
	public void testDomainRoundRobinPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|PRERACE:1,Human");
	}

	@Test
	public void testDomainRoundRobinDupePre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|PRERACE:1,Dwarf",
			"DOMAIN|Cleric=1|Fireball|PRERACE:1,Human");
	}

	@Test
	public void testDomainRoundRobinTwoPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball,Lightning Bolt|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
	}

	@Test
	public void testDomainRoundRobinSplitPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball",
			"DOMAIN|Cleric=1|Lightning Bolt|PRERACE:1,Human");
	}

	@Test
	public void testMixedRoundRobinTwoDomain() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(CDOMSpell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("CLASS|Cleric=1|Lightning Bolt", "DOMAIN|Fire=1|Fireball");
	}

}