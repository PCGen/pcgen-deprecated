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

import pcgen.core.PCTemplate;
import pcgen.core.SpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

public class SpellLevelTokenTest extends AbstractGlobalTokenTestCase
{

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(aToken);
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

	static AddLst token = new AddLst();

	@Override
	public GlobalLstToken getToken()
	{
		return token;
	}

	private AddLstToken aToken = new SpellLevelToken();

	// ADD:SPELLLEVEL|CLASS|SPELLCASTER.Arcane=1|Change Self

	public String getSubTokenString()
	{
		return aToken.getTokenName();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|String"));
	}

	@Test
	public void testInvalidInputOnePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS"));
	}

	@Test
	public void testInvalidInputNullSecond() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|"));
	}

	@Test
	public void testInvalidInputNoList() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=1"));
	}

	@Test
	public void testInvalidInputNoSpell() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER."));
	}

	@Test
	public void testInvalidInputNullSpell() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=1|"));
	}

	@Test
	public void testInvalidInputNoLevel() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane|Change Self"));
	}

	@Test
	public void testInvalidInputNotASpell() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=1|Change Self"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNoClassString()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "||SPELLCASTER.Arcane=1|Change Self"));
	}

	@Test
	public void testInvalidInputNoSpellcasterString()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|Arcane=1|Change Self"));
	}

	@Test
	public void testInvalidInputTooManyEquals()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=1=2|Change Self"));
	}

	@Test
	public void testInvalidInputLevelNaN() throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(
				primaryContext,
				primaryProf,
				getSubTokenString()
					+ "|CLASS|SPELLCASTER.Arcane=Three|Change Self"));
	}

	@Test
	public void testInvalidInputNegativeLevel()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=-2|Change Self"));
	}

	@Test
	public void testInvalidInputTooManyPipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(
			primaryContext,
			primaryProf,
			getSubTokenString()
				+ "|CLASS|SPELLCASTER.Arcane=3|Change Self|Alarm"));
	}

	@Test
	public void testInvalidInputNotAClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			getSubTokenString() + "|CLASS|SPELLCASTER.Arcane=1|Change Self"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Arcane");
		this.runRoundRobin(getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=1|Change Self");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Alarm");
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Arcane");
		this.runRoundRobin(getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=1|Alarm", getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=4|Change Self");
	}

	@Test
	public void testRoundRobinTwoGrant() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Alarm");
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Arcane");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Divine");
		this.runRoundRobin(getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=2|Change Self", getSubTokenString()
			+ "|CLASS|SPELLCASTER.Divine=3|Alarm");
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Alarm");
		primaryContext.ref.constructCDOMObject(Spell.class, "Change Self");
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class, "Sleep");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Arcane");
		primaryContext.ref.constructCDOMObject(SpellList.class, "Divine");
		this.runRoundRobin(getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=1|Alarm", getSubTokenString()
			+ "|CLASS|SPELLCASTER.Arcane=2|Fireball", getSubTokenString()
			+ "|CLASS|SPELLCASTER.Divine=3|Change Self", getSubTokenString()
			+ "|CLASS|SPELLCASTER.Divine=4|Sleep");
	}
}
