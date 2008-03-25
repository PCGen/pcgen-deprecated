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
package plugin.lsttokens.editcontext.spell;

import java.net.URISyntaxException;

import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.CDOMStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractItemIntegrationTestCase;
import plugin.lsttokens.spell.StatToken;

public class StatIntegrationTest extends
		AbstractItemIntegrationTestCase<CDOMSpell, CDOMStat>
{

	static StatToken token = new StatToken();
	static CDOMTokenLoader<CDOMSpell> loader = new CDOMTokenLoader<CDOMSpell>(
			CDOMSpell.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		CDOMStat ps = primaryContext.ref.constructCDOMObject(CDOMStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "TestWP1");
		CDOMStat ss = secondaryContext.ref.constructCDOMObject(CDOMStat.class, "Strength");
		secondaryContext.ref.registerAbbreviation(ss, "TestWP1");
		CDOMStat pi = primaryContext.ref.constructCDOMObject(CDOMStat.class, "Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "TestWP2");
		CDOMStat si = secondaryContext.ref.constructCDOMObject(CDOMStat.class, "Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "TestWP2");
	}

	@Override
	public Class<CDOMSpell> getCDOMClass()
	{
		return CDOMSpell.class;
	}

	@Override
	public CDOMLoader<CDOMSpell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMSpell> getToken()
	{
		return token;
	}

	@Override
	public Class<CDOMStat> getTargetClass()
	{
		return CDOMStat.class;
	}
}
