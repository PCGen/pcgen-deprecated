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
import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;

public class AbilityLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new AbilityLst();
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
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotANature() throws PersistenceLayerException
	{
		assertFalse(parse("Mutation|NotANature|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotaCategory() throws PersistenceLayerException
	{
		assertFalse(parse("NotaCategory|NORMAL|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCategoryOnly() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCategoryBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyCategory() throws PersistenceLayerException
	{
		assertFalse(parse("|NORMAL|Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyNature() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT||Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleBarAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|Abil1||Abil2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleBarStartAbility()
		throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL||Abil1|Abil2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBarEndAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|Abil1|"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		runRoundRobin("FEAT|NORMAL|Abil1");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		runRoundRobin("FEAT|NORMAL|Abil1|Abil2");
	}

	@Test
	public void testRoundRobinTwoNature() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil3");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil3");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil4");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil4");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		runRoundRobin("FEAT|NORMAL|Abil1|Abil2", "FEAT|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinTwoCategory() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil2");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		CDOMAbilityCategory ac = CDOMAbilityCategory.getConstant("NEWCAT");
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil3");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil3");
		secondaryContext.ref.reassociateCategory(ac, ab);
		ab = primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil4");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil4");
		secondaryContext.ref.reassociateCategory(ac, ab);
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil2", "NEWCAT|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1");
	}

	@Test
	public void testRoundRobinDupeDiffNature() throws PersistenceLayerException
	{
		CDOMAbility ab =
				primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		primaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
		secondaryContext.ref.reassociateCategory(CDOMAbilityCategory.FEAT, ab);
		runRoundRobin("FEAT|NORMAL|Abil1", "FEAT|VIRTUAL|Abil1");
	}

	// @Test
	// public void testRoundRobinDupeOnePrereq() throws
	// PersistenceLayerException
	// {
	// Ability ab =
	// primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// primaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// secondaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Human");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }
	//	
	// @Test
	// public void testRoundRobinDupeDiffPrereqs()
	// throws PersistenceLayerException
	// {
	// Ability ab =
	// primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// primaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// secondaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// runRoundRobin("FEAT|VIRTUAL|Abil1",
	// "FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testRoundRobinDupeTwoDiffPrereqs()
	// throws PersistenceLayerException
	// {
	// Ability ab =
	// primaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// primaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// ab = secondaryContext.ref.constructCDOMObject(CDOMAbility.class, "Abil1");
	// secondaryContext.ref.reassociateReference(AbilityCategory.FEAT, ab);
	// runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Elf",
	// "FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
	// assertTrue(primaryContext.ref.validate());
	// assertTrue(secondaryContext.ref.validate());
	// }
}