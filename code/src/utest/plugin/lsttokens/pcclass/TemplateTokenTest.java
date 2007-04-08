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
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;

public class TemplateTokenTest extends
		AbstractListTokenTestCase<PObject, PCTemplate>
{

	static TemplateToken token = new TemplateToken();
	static PCClassLoaderFacade loader = new PCClassLoaderFacade();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public LstLoader getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<PObject> getToken()
	{
		return token;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<PCTemplate> getTargetClass()
	{
		return PCTemplate.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Test
	public void testInvalidEmptyChoose()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:"));
	}

	@Test
	public void testInvalidChooseListStart()
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:|TestWP1"));
	}

	@Test
	public void testInvalidChooseDoubleJoin()
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		assertFalse(token.parse(primaryContext, primaryProf,
			"CHOOSE:TestWP2||TestWP1"));
	}

	@Test
	public void testInvalidChooseListEnd()
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf, "CHOOSE:TestWP1|"));
	}

	@Test
	public void testInvalidEmptyAddChoice()
	{
		assertFalse(token.parse(primaryContext, primaryProf, "ADDCHOICE:"));
	}

	@Test
	public void testValidChooseInputs()
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		assertTrue(token.parse(primaryContext, primaryProf, "CHOOSE:TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(token.parse(primaryContext, primaryProf,
			"CHOOSE:TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinChooseOne() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		runRoundRobin("CHOOSE:TestWP1");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinChooseThree() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP3");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP3");
		runRoundRobin("CHOOSE:TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP3");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP4");
		primaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP5");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP1");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP2");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP3");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP4");
		secondaryContext.ref.constructCDOMObject(getTargetClass(), "TestWP5");
		runRoundRobin("TestWP4|TestWP5", "CHOOSE:TestWP1|TestWP2|TestWP3");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}
}
