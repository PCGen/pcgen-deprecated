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
package plugin.lsttokens.editcontext.equipmentmodifier;

import org.junit.Test;

import pcgen.cdom.inst.CDOMEqMod;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.equipmentmodifier.ReplacesToken;

public class ReplacesIntegrationTest extends
		AbstractTypeSafeListIntegrationTestCase<CDOMEqMod>
{

	static ReplacesToken token = new ReplacesToken();
	static CDOMTokenLoader<CDOMEqMod> loader = new CDOMTokenLoader<CDOMEqMod>(
			CDOMEqMod.class);

	@Override
	public Class<CDOMEqMod> getCDOMClass()
	{
		return CDOMEqMod.class;
	}

	@Override
	public CDOMLoader<CDOMEqMod> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMEqMod> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		primaryContext.ref.constructIfNecessary(CDOMEqMod.class, string);
		secondaryContext.ref.constructIfNecessary(CDOMEqMod.class, string);
		return null;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return true;
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public char getJoinCharacter()
	{
		return ',';
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

}
