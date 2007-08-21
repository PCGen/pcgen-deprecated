/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag in the
 * definition of an Equipment Modifier.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2006-03-14 17:16:52 -0500
 * (Tue, 14 Mar 2006) $
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements EquipmentModifierLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/**
	 * @see pcgen.persistence.lst.EquipmentModifierLstToken#parse(pcgen.core.EquipmentModifier,
	 *      java.lang.String)
	 */
	public boolean parse(EquipmentModifier mod, String value)
	{
		mod.setVisible(value.toUpperCase());
		return true;
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
		String value)
	{
		try
		{
			context.getObjectContext().put(mod, ObjectKey.VISIBILITY,
				Visibility.valueOf(value));
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Invalid Visibility in Token " + getTokenName()
				+ ": " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		Visibility vis =
				context.getObjectContext().getObject(mod, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.QUALIFY))
		{
			visString = "QUALIFY";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a Equipment Modifier");
			return null;
		}
		return new String[]{visString};
	}
}
