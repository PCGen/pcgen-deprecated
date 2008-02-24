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
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AlignTokenTest extends AbstractTokenTestCase<CDOMDeity>
{
	static AlignToken token = new AlignToken();
	static CDOMTokenLoader<CDOMDeity> loader = new CDOMTokenLoader<CDOMDeity>(
			CDOMDeity.class);

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
			URISyntaxException
	{
		super.setUp();
		CDOMAlignment lg = primaryContext.ref.constructCDOMObject(
				CDOMAlignment.class, "Lawful Good");
		primaryContext.ref.registerAbbreviation(lg, "LG");
		CDOMAlignment ln = primaryContext.ref.constructCDOMObject(
				CDOMAlignment.class, "Lawful Neutral");
		primaryContext.ref.registerAbbreviation(ln, "LN");
		CDOMAlignment slg = secondaryContext.ref.constructCDOMObject(
				CDOMAlignment.class, "Lawful Good");
		secondaryContext.ref.registerAbbreviation(slg, "LG");
		CDOMAlignment sln = secondaryContext.ref.constructCDOMObject(
				CDOMAlignment.class, "Lawful Neutral");
		secondaryContext.ref.registerAbbreviation(sln, "LN");
	}

	@Override
	public Class<CDOMDeity> getCDOMClass()
	{
		return CDOMDeity.class;
	}

	@Override
	public CDOMLoader<CDOMDeity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMDeity> getToken()
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
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidFormula() throws PersistenceLayerException
	{
		assertFalse(parse("1+3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInteger() throws PersistenceLayerException
	{
		assertFalse(parse("4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidString() throws PersistenceLayerException
	{
		assertFalse(parse("String"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinLG() throws PersistenceLayerException
	{
		runRoundRobin("LG");
	}

	@Test
	public void testRoundRobinLN() throws PersistenceLayerException
	{
		runRoundRobin("LN");
	}

}
