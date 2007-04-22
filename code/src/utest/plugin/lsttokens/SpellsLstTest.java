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

public class SpellsLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new SpellsLst();
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
	}

	@Test
	public void testInvalidSpellbookOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SpellBook"));
	}

	@Test
	public void testInvalidSpellbookBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SpellBook|"));
	}

	@Test
	public void testInvalidEmptySpellbook() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Fireball"));
	}

	@Test
	public void testInvalidSpellbookAndSpellBarOnly()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|Fireball|"));
	}

	@Test
	public void testInvalidSpellCommaStarting()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|,Fireball"));
	}

	@Test
	public void testInvalidSpellCommaEnding() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|Fireball,"));
	}

	@Test
	public void testInvalidSpellDoubleComma() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|Fireball,,DCFormula"));
	}

	@Test
	public void testInvalidSpellDoublePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|Fireball||Lightning Bolt"));
	}

	@Test
	public void testInvalidSpellEmbeddedPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|Fireball|PRERACE:1,Human|Lightning Bolt"));
	}

	@Test
	public void testInvalidBadTimes() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|TIMES=|Fireball"));
	}

	@Test
	public void testInvalidOnlyTimes() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|TIMES=3"));
	}

	@Test
	public void testInvalidOnlyTimesBar() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|TIMES=3|"));
	}

	@Test
	public void testInvalidOnlyLevel() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|CASTERLEVEL=3"));
	}

	@Test
	public void testInvalidOnlyLevelBar() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|CASTERLEVEL=3|"));
	}

	@Test
	public void testInvalidEmptyTimes() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook||Fireball"));
	}

	@Test
	public void testInvalidBadCasterLevel() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SpellBook|CASTERLEVEL=|Fireball"));
	}

	@Test
	public void testInvalidOutOfOrder() throws PersistenceLayerException
	{
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
				"SpellBook|CASTERLEVEL=4|TIMES=2|Fireball"));
		}
		catch (IllegalArgumentException iae)
		{
			// This is ok too
		}
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
				"SpellBook|TIMES=2|PRERACE:1,Human"));
		}
		catch (IllegalArgumentException iae)
		{
			// This is ok too
		}
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|Fireball");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|Fireball|Lightning Bolt");
	}

	@Test
	public void testRoundRobinTimes() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|TIMES=3|Fireball");
	}

	@Test
	public void testRoundRobinDC() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|Fireball,CL+5");
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|Fireball|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinCasterLevel() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|CASTERLEVEL=15|Fireball");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|TIMES=2|CASTERLEVEL=15|Fireball,CL+5|Lightning Bolt,25|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinTwoBooksJustSpell()
		throws PersistenceLayerException
	{
		runRoundRobin("OtherBook|Lightning Bolt", "SpellBook|Fireball");
	}

	@Test
	public void testRoundRobinTwoTimesJustSpell()
		throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|TIMES=2|Fireball",
			"SpellBook|TIMES=3|Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoLevelJustSpell()
		throws PersistenceLayerException
	{
		runRoundRobin("SpellBook|CASTERLEVEL=12|Fireball",
			"SpellBook|CASTERLEVEL=15|Lightning Bolt");
	}
}