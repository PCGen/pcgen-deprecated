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

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class MoveLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new MoveLst();
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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOneItem() throws PersistenceLayerException
	{
		assertFalse(parse("Normal"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("Normal,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyValue() throws PersistenceLayerException
	{
		assertFalse(parse(",30"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoComma() throws PersistenceLayerException
	{
		assertFalse(parse("Normal,,30"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputThreeItems() throws PersistenceLayerException
	{
		assertFalse(parse("Normal,30,Darkvision"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeMovement()
		throws PersistenceLayerException
	{
		assertFalse(parse("Normal,-30"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Walk,30");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision,0");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("Darkvision,0,Walk,30");
	}

}
