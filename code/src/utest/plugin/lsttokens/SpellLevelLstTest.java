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
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.ClassSpellList;
import pcgen.core.DomainSpellList;
import pcgen.core.PCTemplate;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class SpellLevelLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new SpelllevelLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

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
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
	}

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidClassOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CLASS"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidClassBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CLASS|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyClass() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Cleric=1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptySpell() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CLASS|Cleric=1|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellCommaStarting()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1|,Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellCommaEnding() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1|Fireball,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellDoubleComma() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1|Fireball,,Lightning Bolt"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellDoublePipe1() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CLASS||Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNoSpellLevel() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellLevelNaN() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=CL|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellLevelDecimal() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=4.5|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellLevelNoEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric 4|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellLevelJustLevel()
		throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "CLASS|4|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidForgotSpell() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=4|PRERACE:1,Human"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellLevelDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric==5|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellDoublePipe2() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1||Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellDoublePipe3() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1|Fireball||Druid=2|Lightning Bolt"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptySPELLCASTER() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|SPELLCASTER.=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS|Cleric=1|Fireball|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"CLASS||Cleric=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball,Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoLevel() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=1|Fireball|Cleric=2|Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class, "Druid");
		runRoundRobin("CLASS|Cleric,Druid=1|Fireball,Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoDiffClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
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
		assertFalse(token.parse(primaryContext, primaryProf, "CLASS"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "DOMAIN|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyDomain() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Cleric=1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainEmptySpell() throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "DOMAIN|Cleric=1|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellCommaStarting()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1|,Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellCommaEnding()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1|Fireball,"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellDoubleComma()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1|Fireball,,Lightning Bolt"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellDoublePipe1()
		throws PersistenceLayerException
	{
		assertFalse(token
			.parse(primaryContext, primaryProf, "DOMAIN||Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainNoSpellLevel()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellLevelNaN()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=CL|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellLevelDecimal()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=4.5|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellLevelDoubleEquals()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric==5|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellDoublePipe2()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1||Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainSpellDoublePipe3()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1|Fireball||Druid=2|Lightning Bolt"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainEmptySPELLCASTER()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|SPELLCASTER.=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainTrailingPipe()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=1|Fireball|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN||Cleric=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainCasterCommaTrailing()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric,=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainCasterCommaLeading()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|,Cleric=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainCasterCommaDouble()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric,,Druid=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidDomainCasterDoubleAssignAttempt()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"DOMAIN|Cleric=4,Druid=1|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testDomainRoundRobinJustSpell()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball");
	}

	@Test
	public void testDomainRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball,Lightning Bolt");
	}

	@Test
	public void testDomainRoundRobinTwoLevel() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|Cleric=2|Lightning Bolt");
	}

	@Test
	public void testDomainRoundRobinTwoDomain()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
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
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball|PRERACE:1,Human");
	}

	@Test
	public void testDomainRoundRobinTwoPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball,Lightning Bolt|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
	}

	@Test
	public void testDomainRoundRobinSplitPre() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Cleric");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class,
			"Cleric");
		runRoundRobin("DOMAIN|Cleric=1|Fireball",
			"DOMAIN|Cleric=1|Lightning Bolt|PRERACE:1,Human");
	}

	@Test
	public void testMixedRoundRobinTwoDomain() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.ref.constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("CLASS|Cleric=1|Lightning Bolt", "DOMAIN|Fire=1|Fireball");
	}

}