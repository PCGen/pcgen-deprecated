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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.bonustokens.Weapon;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class NaturalAttacksLstTest extends AbstractGlobalTokenTestCase
{

	static GlobalLstToken token = new NaturalattacksLst();
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
		addBonus("WEAPON", Weapon.class);
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
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
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNameOnly() throws PersistenceLayerException
	{
		assertFalse(parse("Claw"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNameCommaOnly() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyClass() throws PersistenceLayerException
	{
		assertFalse(parse(",Weapon.Natural.Melee.Piercing.Slashing"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyCount() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyDamage() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,2,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMissingCount() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMissingType() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,,2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTypeFalseStart() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,.Weapon.Natural.Melee.Piercing.Slashing,2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTypeFalseEnd() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing.,2,1d4"));
		assertNoSideEffects();
	}

	// Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4

	@Test
	public void testInvalidTypeDoubleDot() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing..Slashing,2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidLeadingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("|Claw,Weapon.Natural.Melee.Piercing.Slashing,2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingPipe() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,2,1d4|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCountNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,NaN,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCountStarNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,*NaN,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Claw,Weapon.Natural.Melee.Piercing.Slashing,2,1d4||"
			+ "Bite,Weapon.Natural.Melee.Piercing.Slashing,1,1d6"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidReservedName() throws PersistenceLayerException
	{
		assertFalse(parse("None,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSameName() throws PersistenceLayerException
	{
		assertFalse(parse("Bite,Weapon.Natural.Melee.Piercing.Slashing,*1,1d10|"
			+ "Bite,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,2,1d4");
	}

	@Test
	public void testRoundRobinHands() throws PersistenceLayerException
	{
		runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,2,1d4, 1");
	}

	@Test
	public void testRoundRobinStarSimple() throws PersistenceLayerException
	{
		runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4");
	}

	@Test
	public void testRoundRobinStarDouble() throws PersistenceLayerException
	{
		runRoundRobin("Bite,Weapon.Natural.Melee.Piercing.Slashing,*1,1d10|Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4");
	}
}