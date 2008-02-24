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
package plugin.lsttokens.editcontext.skill;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.ClassSkillList;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.skill.ClassesToken;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

public class ClassesIntegrationTest extends
		AbstractIntegrationTestCase<CDOMSkill>
{

	static ClassesToken token = new ClassesToken();
	static CDOMTokenLoader<CDOMSkill> loader = new CDOMTokenLoader<CDOMSkill>(
			CDOMSkill.class);

	private static boolean classSetUpFired = false;

	@BeforeClass
	public static final void ltClassSetUp() throws PersistenceLayerException
	{
		TokenRegistration.register(new PreClassParser());
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
	public Class<CDOMSkill> getCDOMClass()
	{
		return CDOMSkill.class;
	}

	@Override
	public CDOMLoader<CDOMSkill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMSkill> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
				"Sorcerer");
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Cleric");
		secondaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Cleric");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard");
		commit(modCampaign, tc, "Cleric|Sorcerer");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinAddNot() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Wizard");
		commit(modCampaign, tc, "!Sorcerer");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinOverridePre() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "!Sorcerer|!Wizard");
		commit(modCampaign, tc, "Sorcerer");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc, "Sorcerer");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		verifyCleanStart();
		primaryContext.ref.constructCDOMObject(ClassSkillList.class, "Wizard");
		secondaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Wizard");
		primaryContext.ref
				.constructCDOMObject(ClassSkillList.class, "Sorcerer");
		secondaryContext.ref.constructCDOMObject(ClassSkillList.class,
				"Sorcerer");
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "Sorcerer|Wizard");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}
}
