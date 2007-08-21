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
package plugin.lsttokens.editcontext.pcclass;

import org.junit.Test;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLevelLstToken;
import plugin.lsttokens.pcclass.HitdieLst;

public class HitDieTokenTest extends AbstractPCClassLevelTokenTestCase
{

	static HitdieLst token = new HitdieLst();

	@Override
	public PCClassLevelLstToken getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputTooManyLimits()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS=Fighter|CLASS.TYPE=Base", 2));
	}

	@Test
	public void testInvalidInputNotALimit() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|PRECLASS:1,Fighter", 2));
	}

	@Test
	public void testInvalidInputEmptyLimit() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "15|CLASS=",
			2));
	}

	@Test
	public void testInvalidInputEmptyTypeLimit()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"15|CLASS.TYPE=", 2));
	}

	@Test
	public void testValidInputDivideNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%/-2", 2));
	}

	@Test
	public void testValidInputDivideZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%/0", 2));
	}

	@Test
	public void testValidInputDivide() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%/4", 2));
	}

	@Test
	public void testInvalidInputAddNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%+-3", 2));
	}

	@Test
	public void testInvalidInputAddZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%+0", 2));
	}

	@Test
	public void testValidInputAdd() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%+4", 2));
	}

	@Test
	public void testInvalidInputMultiplyNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%*-3", 2));
	}

	@Test
	public void testInvalidInputMultiplyZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%*0", 2));
	}

	@Test
	public void testValidInputMultiply() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%*4", 2));
	}

	@Test
	public void testInvalidInputSubtractNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%--3", 2));
	}

	@Test
	public void testInvalidInputSubtractZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%-0", 2));
	}

	@Test
	public void testValidInputSubtract() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%-4", 2));
	}

	@Test
	public void testInvalidInputUpNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up-3", 2));
	}

	@Test
	public void testInvalidInputUpZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%up0", 2));
	}

	@Test
	public void testValidInputUp() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%up4", 2));
	}

	@Test
	public void testInvalidInputHUpNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hup-3", 2));
	}

	@Test
	public void testInvalidInputHUpZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hup0", 2));
	}

	@Test
	public void testValidInputHUp() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%Hup4", 2));
	}

	@Test
	public void testInvalidInputDownNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down-3", 2));
	}

	@Test
	public void testInvalidInputDownZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%down0", 2));
	}

	@Test
	public void testValidInputDown() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%down4", 2));
	}

	@Test
	public void testInvalidInputHdownNegative()
		throws PersistenceLayerException
	{
		assertFalse(getToken()
			.parse(primaryContext, primaryProf, "%Hdown-3", 2));
	}

	@Test
	public void testInvalidInputHdownZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%Hdown0", 2));
	}

	@Test
	public void testValidInputHdown() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "%Hdown4", 2));
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "-3", 2));
	}

	@Test
	public void testInvalidInputZero() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "0", 2));
	}

	@Test
	public void testInvalidInputDecimal() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3.5", 2));
	}

	@Test
	public void testInvalidInputMisspell() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "%upn5", 2));
	}

	@Test
	public void testRoundRobinInteger() throws PersistenceLayerException
	{
		runRoundRobin("2");
	}

	@Test
	public void testRoundRobinIntegerClass() throws PersistenceLayerException
	{
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
		runRoundRobin("%Hdown2|CLASS=Fighter");
	}

	@Test
	public void testRoundRobinHdownType() throws PersistenceLayerException
	{
		runRoundRobin("%Hdown2|CLASS.TYPE=Base");
	}
}
