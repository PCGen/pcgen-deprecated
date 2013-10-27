/*
 * SettingFilter.java
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on March 25, 2002, 13:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.util.PropertyFactory;

/**
 * <code>SettingFilter</code>
 *
 * @author Bryan McRoberts
 * @version $Revision$
 */
final class SettingFilter extends AbstractPObjectFilter
{
	private String setting;

	SettingFilter(String arg)
	{
		super();
		setting = arg;
		setCategory(PropertyFactory.getString("in_settingLabel"));
		setName(arg);
		setDescription(PropertyFactory.getFormattedString("in_filterAccObj",getName()));
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		Campaign c;

		if (pObject instanceof Campaign)
		{
			c = (Campaign) pObject;
		}
		else
		{
			c = pObject.getSourceCampaign();
		}

		return ((c != null) && c.getSetting().equals(setting));
	}
}
