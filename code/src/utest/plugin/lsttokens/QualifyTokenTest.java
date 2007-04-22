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
	}

	@Test
	public void testInvalidTypeOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL"));
	}

	@Test
	public void testInvalidTypeBarOnly() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL|"));
	}

	@Test
	public void testInvalidEmptyType() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Fireball"));
	}

	@Test
	public void testInvalidCatTypeNoEqual() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "ABILITY|Abil"));
	}

	@Test
	public void testInvalidNonCatTypeEquals() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SPELL=Arcane|Fireball"));
	}

	@Test
	public void testInvalidSpellbookAndSpellBarOnly()
		throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL|Fireball|"));
	}

	@Test
	public void testInvalidSpellBarStarting() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SPELL||Fireball"));
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL|Fireball");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL|Fireball|Lightning Bolt");
	}

	@Test
	public void testRoundRobinTwoBooksJustSpell()
		throws PersistenceLayerException
	{
		runRoundRobin("ABILITY=FEAT|My Feat", "SPELL|Lightning Bolt");
	}
}