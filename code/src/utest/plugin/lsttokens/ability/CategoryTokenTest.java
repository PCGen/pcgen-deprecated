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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeTokenTestCase;

public class CategoryTokenTest extends AbstractTypeSafeTokenTestCase<CDOMAbility>
{

	static CategoryToken token = new CategoryToken();
	static CDOMTokenLoader<CDOMAbility> loader = new CDOMTokenLoader<CDOMAbility>(
			CDOMAbility.class);

	@Override
	public Class<CDOMAbility> getCDOMClass()
	{
		return CDOMAbility.class;
	}

	@Override
	public CDOMLoader<CDOMAbility> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMAbility> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return CDOMAbilityCategory.getConstant(string);
	}

	@Override
	public ObjectKey<?> getObjectKey()
	{
		return ObjectKey.CATEGORY;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return true;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

}
