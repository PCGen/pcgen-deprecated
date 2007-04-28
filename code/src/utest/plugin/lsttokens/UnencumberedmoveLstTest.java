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

import pcgen.cdom.enumeration.ArmorType;
import pcgen.cdom.enumeration.Load;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class UnencumberedmoveLstTest extends AbstractGlobalTokenTestCase
{

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		Load.constructConstant("LightLoad", 0);
		Load.constructConstant("MediumLoad", 1);
		Load.constructConstant("HeavyLoad", 2);
		ArmorType.getConstant("LightArmor", 0);
		ArmorType.getConstant("MediumArmor", 1);
		ArmorType.getConstant("HeavyArmor", 2);
	}

	static GlobalLstToken token = new UnencumberedmoveLst();
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

	@Test
	public void testChooseInvalidInputPipeOnly()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputRandomString()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputEndPipe()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "HeavyLoad|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputStartPipe()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|HeavyLoad"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputDoublePipe()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"HeavyLoad||HeavyArmor"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputDoubleLoad()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"HeavyLoad|MediumLoad"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testChooseInvalidInputDoubleArmor()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"MediumArmor|HeavyArmor"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinArmor() throws PersistenceLayerException
	{
		runRoundRobin("HeavyArmor");
	}

	@Test
	public void testRoundRobinLoad() throws PersistenceLayerException
	{
		runRoundRobin("HeavyLoad");
	}

	@Test
	public void testRoundRobinLoadArmor() throws PersistenceLayerException
	{
		runRoundRobin("HeavyLoad|MediumArmor");
	}

}
