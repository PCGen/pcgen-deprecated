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
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.PCClassLoaderFacade;

public class ExchangeLevelTokenTest extends AbstractTokenTestCase<PCClass>
{

	static ExchangelevelToken token = new ExchangelevelToken();
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
	public CDOMToken<PCClass> getToken()
	{
		return token;
	}

	public Class<PCClass> getTargetClass()
	{
		return PCClass.class;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNoLevels() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf, "Paladin"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyMin() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin||5|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyMax() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|5||3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyRem() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|6|5|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotEnoughPipes()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "Paladin|6|5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTooManyPipes() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|6|5|4|3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyClass() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|4|3|2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOpenStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"|Paladin|6|5|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOpenEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|6|5|4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeOne()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin||6|5|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeTwo()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|6||5|4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipeTypeThree()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|6|5||4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputRemTooLow() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|5|2|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegativeMax() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|-5|2|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputZeroMax() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|0|2|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegativeDonate()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|5|-2|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputZeroDonate() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|5|0|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegativeRemaining()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|4|5|-1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputRemainingNaN()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|11|10|x"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDonateNaN()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|11|x|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputMaxNaN()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Paladin|x|10|1"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|11|10|1");
	}

	@Test
	public void testRoundRobinZeroRem() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|10|10|0");
	}

	@Test
	public void testRoundRobinHighMax() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|5|10|1");
	}
}
