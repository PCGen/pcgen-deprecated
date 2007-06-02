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
package plugin.pretokens.test;

import pcgen.core.Deity;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.prereq.PrerequisiteTest;

public class PreDeityTesterTest extends AbstractCDOMObjectTestCase<Deity>
{

	PreDeityTester tester = new PreDeityTester();

	@Override
	public Class<Deity> getCDOMClass()
	{
		return Deity.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return PCTemplate.class;
	}

	@Override
	public String getKind()
	{
		return "DEITY";
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

	@Override
	public boolean isWildcardLegal()
	{
		return false;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public boolean isAnyLegal()
	{
		return false;
	}

	@Override
	public boolean isTestStarting()
	{
		return false;
	}

	@Override
	public boolean isSubKeyAware()
	{
		return false;
	}

}
