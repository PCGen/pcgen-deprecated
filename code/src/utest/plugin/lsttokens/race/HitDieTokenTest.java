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
package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.RaceLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class HitDieTokenTest extends AbstractTokenTestCase<Race>
{

	static HitdieToken token = new HitdieToken();
	static RaceLoader loader = new RaceLoader();

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public LstObjectFileLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Race> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputTooManyLimits()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS=Fighter|CLASS.TYPE=Base"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotALimit() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|PRECLASS:1,Fighter"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyLimit() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "15|CLASS="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyTypeLimit()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS.TYPE="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputStartDotTypeLimit()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS.TYPE=.Strange"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEndDotTypeLimit()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS.TYPE=Strange."));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleDotTypeLimit()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS.TYPE=Prestige..Strange"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputDivideNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%/-2"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputDivideZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%/0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputDivide() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%/4"));
	}

	@Test
	public void testInvalidInputAddNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%+-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputAddZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%+0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputAdd() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%+4"));
	}

	@Test
	public void testInvalidInputMultiplyNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%*-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputMultiplyZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%*0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputMultiply() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%*4"));
	}

	@Test
	public void testInvalidInputSubtractNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%--3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputSubtractZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%-0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputSubtract() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%-4"));
	}

	@Test
	public void testInvalidInputUpNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputUpZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputUp() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%up4"));
	}

	@Test
	public void testInvalidInputUpTooBig() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputUpReallyTooBig()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up15"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputHUpNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hup-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputHUpZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hup0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputHUp() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%Hup4"));
	}

	@Test
	public void testInvalidInputDownNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDownZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputDown() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%down4"));
	}

	@Test
	public void testInvalidInputHdownNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hdown-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputHdownZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hdown0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testValidInputHdown() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%Hdown4"));
	}

	@Test
	public void testInvalidInputDownTooBig() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDownReallyTooBig()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down15"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "0"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDecimal() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3.5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputMisspell() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%upn5"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinInteger() throws PersistenceLayerException
	{
		runRoundRobin("2");
	}

	@Test
	public void testRoundRobinIntegerClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinIntegerType() throws PersistenceLayerException
	{
		runRoundRobin("2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		runRoundRobin("%+2");
	}

	@Test
	public void testRoundRobinAddClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%+2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinAddType() throws PersistenceLayerException
	{
		runRoundRobin("%+2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinSubtract() throws PersistenceLayerException
	{
		runRoundRobin("%-2");
	}

	@Test
	public void testRoundRobinSubtractClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%-2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinSubtractType() throws PersistenceLayerException
	{
		runRoundRobin("%-2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("%*2");
	}

	@Test
	public void testRoundRobinMultiplyClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%*2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinMultiplyType() throws PersistenceLayerException
	{
		runRoundRobin("%*2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinDivide() throws PersistenceLayerException
	{
		runRoundRobin("%/2");
	}

	@Test
	public void testRoundRobinDivideClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%/2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinDivideType() throws PersistenceLayerException
	{
		runRoundRobin("%/2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinUp() throws PersistenceLayerException
	{
		runRoundRobin("%up2");
	}

	@Test
	public void testRoundRobinUpClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%up2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinUpType() throws PersistenceLayerException
	{
		runRoundRobin("%up2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinHup() throws PersistenceLayerException
	{
		runRoundRobin("%Hup2");
	}

	@Test
	public void testRoundRobinHupClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%Hup2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinHupType() throws PersistenceLayerException
	{
		runRoundRobin("%Hup2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinDown() throws PersistenceLayerException
	{
		runRoundRobin("%down2");
	}

	@Test
	public void testRoundRobinDownClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%down2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinDownType() throws PersistenceLayerException
	{
		runRoundRobin("%down2|CLASS.TYPE=Base");
	}

	@Test
	public void testRoundRobinHdown() throws PersistenceLayerException
	{
		runRoundRobin("%Hdown2");
	}

	@Test
	public void testRoundRobinHdownClass() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%Hdown2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinHdownType() throws PersistenceLayerException
	{
		runRoundRobin("%Hdown2|CLASS.TYPE=Base");
	}
}
