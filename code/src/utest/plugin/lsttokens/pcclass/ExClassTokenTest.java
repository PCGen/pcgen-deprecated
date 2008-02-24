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

import pcgen.cdom.inst.CDOMPCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ExClassTokenTest extends AbstractTokenTestCase<CDOMPCClass>
{

	static ExclassToken token = new ExclassToken();
	static CDOMTokenLoader<CDOMPCClass> loader = new CDOMTokenLoader<CDOMPCClass>(
			CDOMPCClass.class);

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public CDOMLoader<CDOMPCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMPCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}
	
	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(), "Rheinhessen");
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Niederösterreich");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Niederösterreich");
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		secondaryContext.ref.constructCDOMObject(getCDOMClass(),
			"Languedoc-Roussillon");
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.ref
			.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		runRoundRobin("Yarra Valley");
	}

}
