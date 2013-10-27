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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class VisionLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new VisionLst();
	static CDOMTokenLoader<CDOMTemplate> loader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);

	@Override
	public CDOMLoader<CDOMTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<CDOMTemplate> getCDOMClass()
	{
		return CDOMTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNoOpenParen() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision 25')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoCloseParen() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision (25'"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoParen() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision 25'"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidExtraStuff() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision (25')Normal"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidExtraStuffAfterFoot()
		throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision (25'm)"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDecimalFoot() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision (25.5')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDistanceNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Darkvision (zzzb32')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoOpenParen() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision 25')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoCloseParen() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision (25'"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidNoParen() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision 25'"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidExtraStuff() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision (25')Normal"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidExtraStuffAfterFoot()
		throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision (25'm)"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidDecimalFoot() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision (25.5')"));
		assertNoSideEffects();
	}

	@Test
	public void test2InvalidDistanceNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Normal|Darkvision (zzzb32')"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoComma() throws PersistenceLayerException
	{
		assertFalse(parse("Normal,Darkvision"));
		assertNoSideEffects();
	}

	@Test
	public void testValidDistanceFormula() throws PersistenceLayerException
	{
		assertTrue(parse("Darkvision (zzzb32)"));
	}

	@Test
	public void testValidDistanceNoSpaceNumber()
		throws PersistenceLayerException
	{
		assertTrue(parse("Darkvision(20')"));
	}

	@Test
	public void testValidDistanceNoSpaceShortNumber()
		throws PersistenceLayerException
	{
		assertTrue(parse("Darkvision(5')"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision");
	}

	@Test
	public void testRoundRobinNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (30')");
	}

	@Test
	public void testRoundRobinShortNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (5')");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (Formula*5)");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision|Normal");
	}

	@Test
	public void testRoundRobinMultipleNumber() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (10')|Normal");
	}

	@Test
	public void testRoundRobinMultipleNumberToo()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (10')|Normal (20')");
	}

	@Test
	public void testRoundRobinMultipleNumberSame()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (20')|Normal (20')");
	}

	@Test
	public void testRoundRobinMultipleFormula()
		throws PersistenceLayerException
	{
		runRoundRobin("Darkvision (CL*10)|Normal (Form)");
	}

}
