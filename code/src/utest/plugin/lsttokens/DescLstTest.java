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
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class DescLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new DescLst();
	static PCTemplateLoader loader = new PCTemplateLoader();

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
		TokenRegistration.register(prerace);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(preracewriter);
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SA Number %||VarF"));
	}

	@Test
	public void testInvalidEndingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "SA Number|"));
	}

	@Test
	public void testInvalidStartingPipe() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf, "|Var"));
	}

	@Test
	public void testInvalidVarAfterPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"SA % plus %|Var|PRECLASS:1,Fighter|Var2"));
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		assertFalse(token.parse(primaryContext, primaryProf,
			"PRECLASS:1,Fighter"));
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One");
	}

	@Test
	public void testRoundRobinVariable() throws PersistenceLayerException
	{
		runRoundRobin("SA Number %|Variab");
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testRoundRobinDoublePre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number One|PRECLASS:1,Fighter=1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinVarDoublePre() throws PersistenceLayerException
	{
		runRoundRobin("SA Number % before %|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinCompound() throws PersistenceLayerException
	{
		runRoundRobin(
			"SA Number % before %|Var|TwoVar|PRECLASS:1,Fighter=1|PRERACE:1,Human",
			"SA Number One|PRECLASS:1,Fighter=1");
	}

}
