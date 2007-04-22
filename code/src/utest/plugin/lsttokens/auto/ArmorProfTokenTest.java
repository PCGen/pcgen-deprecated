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
package plugin.lsttokens.auto;

import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.core.ArmorProf;
import pcgen.persistence.lst.LstObjectFileLoader;
import pcgen.persistence.lst.PCTemplateLoader;

public class ArmorProfTokenTest extends AbstractAutoTokenTestCase
{

	@Override
	protected ArmorProfToken getSubToken()
	{
		return new ArmorProfToken();
	}

	@Override
	protected Class<ArmorProf> getSubTokenType()
	{
		return ArmorProf.class;
	}

	static PCTemplateLoader loader = new PCTemplateLoader();

	@Override
	public LstObjectFileLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}
}
