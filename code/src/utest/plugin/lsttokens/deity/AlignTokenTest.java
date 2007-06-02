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
package plugin.lsttokens.deity;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Alignment;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.DeityLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AlignTokenTest extends AbstractTokenTestCase<Deity>
{
	static AlignToken token = new AlignToken();
	static DeityLoader loader = new DeityLoader();

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		primaryContext.ref.constructCDOMObject(Alignment.class, "LG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "LN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "LE");
		primaryContext.ref.constructCDOMObject(Alignment.class, "NG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "TN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "NE");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CG");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CN");
		primaryContext.ref.constructCDOMObject(Alignment.class, "CE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "LE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "NG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "TN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "NE");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CG");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CN");
		secondaryContext.ref.constructCDOMObject(Alignment.class, "CE");
	}

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public LstObjectFileLoader<Deity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Deity> getToken()
	{
		return token;
	}

	public ObjectKey<?> getObjectKey()
	{
		return ObjectKey.ALIGNMENT;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidFormula() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInteger() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testRoundRobinLG() throws PersistenceLayerException
	{
		runRoundRobin("LG");
	}

	@Test
	public void testRoundRobinNG() throws PersistenceLayerException
	{
		runRoundRobin("NG");
	}

	@Test
	public void testRoundRobinCG() throws PersistenceLayerException
	{
		runRoundRobin("CG");
	}

	@Test
	public void testRoundRobinLN() throws PersistenceLayerException
	{
		runRoundRobin("LN");
	}

	@Test
	public void testRoundRobinTN() throws PersistenceLayerException
	{
		runRoundRobin("TN");
	}

	@Test
	public void testRoundRobinCN() throws PersistenceLayerException
	{
		runRoundRobin("CN");
	}

	@Test
	public void testRoundRobinLE() throws PersistenceLayerException
	{
		runRoundRobin("LE");
	}

	@Test
	public void testRoundRobinNE() throws PersistenceLayerException
	{
		runRoundRobin("NE");
	}

	@Test
	public void testRoundRobinCE() throws PersistenceLayerException
	{
		runRoundRobin("CE");
	}

}
