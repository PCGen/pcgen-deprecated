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
package plugin.lsttokens.editcontext.equipment;

import pcgen.cdom.inst.CDOMEquipment;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractBigDecimalIntegrationTestCase;
import plugin.lsttokens.equipment.WtToken;

public class WtIntegrationTest extends
		AbstractBigDecimalIntegrationTestCase<CDOMEquipment>
{
	static WtToken token = new WtToken();
	static CDOMTokenLoader<CDOMEquipment> loader = new CDOMTokenLoader<CDOMEquipment>(
			CDOMEquipment.class);

	@Override
	public Class<CDOMEquipment> getCDOMClass()
	{
		return CDOMEquipment.class;
	}

	@Override
	public CDOMLoader<CDOMEquipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMEquipment> getToken()
	{
		return token;
	}

	@Override
	public boolean isNegativeAllowed()
	{
		return false;
	}

	@Override
	public boolean isPositiveAllowed()
	{
		return true;
	}

	@Override
	public boolean isZeroAllowed()
	{
		return true;
	}

}
