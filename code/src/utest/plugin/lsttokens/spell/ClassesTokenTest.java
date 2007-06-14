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
package plugin.lsttokens.spell;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.ClassSpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.SpellLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class ClassesTokenTest extends AbstractTokenTestCase<Spell>
{

	static ClassesToken token = new ClassesToken();
	static SpellLoader loader = new SpellLoader();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public LstObjectFileLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ""));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputClassOnly() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputLevelOnly() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputChainClassOnly()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard=3|Sorcerer"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleEquals() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard==4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputBadLevel() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard=Sorcerer"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNegativeLevel()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard=-4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "|Wizard=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard=4|"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard=3||Sorcerer=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard,,Sorcerer=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputLeadingComma() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, ",Wizard=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingEquals()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard=4="));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputDoubleSet() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard=4=3"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard,=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "TYPE.=4"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputEmptyPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf, "Wizard=4[]"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputOpenEndedPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(getToken().parse(primaryContext, primaryProf,
			"Wizard=4[PRERACE:1,Human"));
		assertTrue(primaryGraph.isEmpty());
	}

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(getToken().parse(primaryContext, primaryProf, "Wizard=4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		assertTrue(getToken().parse(primaryContext, primaryProf,
			"Wizard,Sorcerer=4"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("Wizard=4");
	}

	@Test
	public void testRoundRobinPrereq() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("Wizard=4[PRERACE:1,Human]");
	}

	@Test
	public void testRoundRobinComma() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
			"Sorcerer");
		runRoundRobin("Sorcerer,Wizard=4");
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
			"Sorcerer");
		runRoundRobin("Wizard=3|Sorcerer=4");
	}

	@Test
	public void testRoundRobinCommaPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		primaryContext.ref.constructCDOMObject(ClassSpellList.class, "Bard");
		secondaryContext.ref
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class,
			"Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSpellList.class, "Bard");
		runRoundRobin("Sorcerer,Wizard=3|Bard=4");
	}
}
