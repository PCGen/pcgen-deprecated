/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.race;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class SizeTokenTest extends AbstractTokenTestCase<Race>
{

	static SizeToken token = new SizeToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);
	private SizeAdjustment ps;

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		ps = primaryContext.getReferenceContext().constructCDOMObject(SizeAdjustment.class,
				"Small");
		primaryContext.getReferenceContext().registerAbbreviation(ps, "S");
		SizeAdjustment pm = primaryContext.getReferenceContext().constructCDOMObject(
				SizeAdjustment.class, "Medium");
		primaryContext.getReferenceContext().registerAbbreviation(pm, "M");
		SizeAdjustment ss = secondaryContext.getReferenceContext().constructCDOMObject(
				SizeAdjustment.class, "Small");
		secondaryContext.getReferenceContext().registerAbbreviation(ss, "S");
		SizeAdjustment sm = secondaryContext.getReferenceContext().constructCDOMObject(
				SizeAdjustment.class, "Medium");
		secondaryContext.getReferenceContext().registerAbbreviation(sm, "M");
	}

	@Override
	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testInvalidNotASize()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "W").passed());
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinS() throws PersistenceLayerException
	{
		runRoundRobin("S");
	}

	@Test
	public void testRoundRobinM() throws PersistenceLayerException
	{
		runRoundRobin("M");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "S";
	}

	@Override
	protected String getLegalValue()
	{
		return "M";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(FormulaKey.SIZE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		FixedSizeFormula fsf = new FixedSizeFormula(ps);
		primaryProf.put(FormulaKey.SIZE, fsf);
		expectSingle(getToken().unparse(primaryContext, primaryProf), ps
				.getAbbreviation());
	}

	/*
	 * TODO Need to have this as someone's responsibility to check...
	 */
	// @Test
	// public void testUnparseIllegal() throws PersistenceLayerException
	// {
	// Formula f = FormulaFactory.getFormulaFor(1);
	// primaryProf.put(FormulaKey.SIZE, f);
	// assertBadUnparse();
	// }
}
