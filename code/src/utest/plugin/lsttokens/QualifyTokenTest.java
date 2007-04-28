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

import pcgen.cdom.enumeration.AbilityCategory;
import pcgen.core.Ability;
import pcgen.core.PCTemplate;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class QualifyTokenTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new QualifyToken();
	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
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
	public void testInvalidTypeOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidTypeBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidEmptyType() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidCatTypeNoEqual() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "ABILITY|Abil"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidNonCatTypeEquals() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SPELL=Arcane|Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellbookAndSpellBarOnly()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL|Fireball|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidSpellBarStarting() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL||Fireball"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		runRoundRobin("SPELL|Fireball");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		runRoundRobin("SPELL|Fireball|Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoBooksJustSpell()
		throws PersistenceLayerException
	{
		Ability a = primaryContext.ref.constructCDOMObject(Ability.class, "My Feat");
		primaryContext.ref.reassociateReference(AbilityCategory.FEAT, a);
		a = secondaryContext.ref.constructCDOMObject(Ability.class, "My Feat");
		secondaryContext.ref.reassociateReference(AbilityCategory.FEAT, a);
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Lightning Bolt");
		runRoundRobin("ABILITY=FEAT|My Feat", "SPELL|Lightning Bolt");
	}
}