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

public class DrLstTest extends AbstractGlobalTokenTestCase
{
	static GlobalLstToken token = new DrLst();
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
	public void testInvalidNoSlash() throws PersistenceLayerException
	{
		assertFalse(parse("+1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoReduction() throws PersistenceLayerException
	{
		assertFalse(parse("10/"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoKey() throws PersistenceLayerException
	{
		assertFalse(parse("/+3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTwoSlash() throws PersistenceLayerException
	{
		assertFalse(parse("10/3/+3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoOrSpace() throws PersistenceLayerException
	{
		assertFalse(parse("10/+3 or"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoOrSuffix() throws PersistenceLayerException
	{
		assertFalse(parse("10/+3 or "));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoAndSpace() throws PersistenceLayerException
	{
		assertFalse(parse("10/+3 and"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoAndSuffix() throws PersistenceLayerException
	{
		assertFalse(parse("10/+3 and "));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSpaceOr() throws PersistenceLayerException
	{
		assertFalse(parse("10/or +3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoOrPrefix() throws PersistenceLayerException
	{
		assertFalse(parse("10/ or +3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSpaceAnd() throws PersistenceLayerException
	{
		assertFalse(parse("10/and +3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoAndPrefix() throws PersistenceLayerException
	{
		assertFalse(parse("10/ and +3"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("10/+1");
	}

	@Test
	public void testRoundRobinComplexOr() throws PersistenceLayerException
	{
		runRoundRobin("10/+1 or +2");
	}

	@Test
	public void testRoundRobinComplexAnd() throws PersistenceLayerException
	{
		runRoundRobin("10/Holy and Silver");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		runRoundRobin("10/+1", "5/+2");
	}
}
