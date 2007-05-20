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
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.CDOMToken;
import pcgen.persistence.lst.EquipmentModifierLoader;
import pcgen.persistence.lst.LstObjectFileLoader;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;

public class ReplacesTokenTest extends
		AbstractTypeSafeListTestCase<EquipmentModifier>
{
	static ReplacesToken token = new ReplacesToken();
	static EquipmentModifierLoader loader = new EquipmentModifierLoader();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public LstObjectFileLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return primaryContext.ref.getCDOMReference(EquipmentModifier.class,
			string);
	}

	@Override
	public char getJoinCharacter()
	{
		return ',';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.REPLACED_KEYS;
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
