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

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.bonustokens.MonSkillPts;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.PCClassLoaderFacade;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class MonSkillTokenTest extends AbstractTokenTestCase<PCClass>
{

	static MonskillToken token = new MonskillToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
		addBonus("MONSKILLPTS", MonSkillPts.class);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
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
		try
		{
			assertFalse(parse(""));
		}
		catch (IllegalArgumentException e)
		{
			// This is Okay too :)
		}
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinOnlyPre() throws PersistenceLayerException
	{
		assertFalse(parse("PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("VARIABLE1");
	}

	@Test
	public void testRoundRobinNumber() throws PersistenceLayerException
	{
		runRoundRobin("3");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("3+CL(\"FIGHTER\")");
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		runRoundRobin("VARIABLE1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDupePre() throws PersistenceLayerException
	{
		runRoundRobin("VARIABLE1", "VARIABLE1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDiffPre() throws PersistenceLayerException
	{
		runRoundRobin("VARIABLE1|PRERACE:1,Dwarf", "VARIABLE1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDiffSamePre() throws PersistenceLayerException
	{
		runRoundRobin("VARIABLE1|PRERACE:1,Human", "VARIABLE2|PRERACE:1,Human");
	}

}
