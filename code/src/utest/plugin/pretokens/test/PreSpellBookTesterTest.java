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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.core.prereq.PrerequisiteTest;

public class PreSpellBookTesterTest extends
		AbstractCDOMBooleanTestCase<CDOMPCClass>
{

	PreSpellBookTester tester = new PreSpellBookTester();

	@Override
	public Class<CDOMPCClass> getCDOMClass()
	{
		return CDOMPCClass.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMLanguage.class;
	}

	@Override
	public String getKind()
	{
		return "SPELLBOOK";
	}

	@Override
	public PrerequisiteTest getTest()
	{
		return tester;
	}

	@Override
	public ObjectKey<Boolean> getObjectKey()
	{
		return ObjectKey.SPELLBOOK;
	}
}
