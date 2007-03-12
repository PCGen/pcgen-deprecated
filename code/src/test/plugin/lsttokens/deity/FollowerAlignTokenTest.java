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
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.enumeration.AlignmentType;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.DeityLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.AbstractTokenTestCase;

public class FollowerAlignTokenTest extends AbstractTokenTestCase<Deity>
{
	static FolloweralignToken token = new FolloweralignToken();
	static DeityLoader loader = new DeityLoader();

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		AlignmentType.getConstant("LG");
		AlignmentType.getConstant("LN");
		AlignmentType.getConstant("LE");
		AlignmentType.getConstant("NG");
		AlignmentType.getConstant("TN");
		AlignmentType.getConstant("NE");
		AlignmentType.getConstant("CG");
		AlignmentType.getConstant("CN");
		AlignmentType.getConstant("CE");
		classSetUpFired = true;
	}

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
		URISyntaxException
	{
		super.setUp();
		if (!classSetUpFired)
		{
			ltClassSetUp();
		}
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

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
	}

	@Test
	public void testInvalidFormula() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "1+3"));
	}

	@Test
	public void testInvalidInteger() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "4"));
	}

	@Test
	public void testInvalidString() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "String"));
	}

	@Test
	public void testInvalidDoubleComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "LG,,LN"));
	}

	@Test
	public void testInvalidStartingComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ",LG"));
	}

	@Test
	public void testInvalidEndingComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "LG,"));
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

	@Test
	public void testRoundRobinComplex() throws PersistenceLayerException
	{
		runRoundRobin("LG,LN,LE");
	}

}
