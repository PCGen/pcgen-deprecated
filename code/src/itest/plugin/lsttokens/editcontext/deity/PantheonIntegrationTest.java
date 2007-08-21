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
package plugin.lsttokens.editcontext.deity;

import org.junit.Test;

import pcgen.cdom.enumeration.Type;
import pcgen.core.Deity;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.DeityLoader;
import pcgen.persistence.lst.LstLoader;
import plugin.lsttokens.deity.PantheonToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;

public class PantheonIntegrationTest extends
		AbstractTypeSafeListIntegrationTestCase<Deity>
{

	static PantheonToken token = new PantheonToken();
	static DeityLoader loader = new DeityLoader();

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public LstLoader<Deity> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<Deity> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return Type.getConstant(string);
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return false;
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
