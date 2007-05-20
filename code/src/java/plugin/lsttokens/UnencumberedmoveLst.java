/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ArmorType;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Load;

/**
 * @author djones4
 */
public class UnencumberedmoveLst extends AbstractToken implements
		GlobalLstToken
{

	@Override
	public String getTokenName()
	{
		return "UNENCUMBEREDMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setEncumberedLoadMove(Load.LIGHT, anInt);
		obj.setEncumberedArmorMove(Load.LIGHT, anInt);

		final StringTokenizer st = new StringTokenizer(value, "|");

		while (st.hasMoreTokens())
		{
			final String loadString = st.nextToken();

			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				obj.setEncumberedLoadMove(Load.MEDIUM, anInt);
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				obj.setEncumberedLoadMove(Load.HEAVY, anInt);
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				obj.setEncumberedLoadMove(Load.OVERLOAD, anInt);
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				obj.setEncumberedArmorMove(Load.MEDIUM, anInt);
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				obj.setEncumberedArmorMove(Load.OVERLOAD, anInt);
			}
			else if (loadString.equalsIgnoreCase("LightLoad")
				|| loadString.equalsIgnoreCase("LightArmor"))
			{
				// do nothing, but accept values as valid
			}
			else
			{
				ShowMessageDelegate.showMessageDialog("Invalid value of \""
					+ loadString + "\" for UNENCUMBEREDMOVE in \""
					+ obj.getDisplayName() + "\".", "PCGen", MessageType.ERROR);
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		boolean setLoad = false;
		boolean setArmor = false;

		while (tok.hasMoreTokens())
		{
			String tokString = tok.nextToken();
			try
			{
				pcgen.cdom.enumeration.Load load =
						pcgen.cdom.enumeration.Load.valueOf(tokString);
				if (setLoad)
				{
					Logging.errorPrint("Encountered Second Load value in "
						+ getTokenName() + " this is not valid.");
					return false;
				}
				context.obj.put(obj, ObjectKey.UNENCUMBERED_LOAD, load);
				setLoad = true;
			}
			catch (IllegalArgumentException e)
			{
				// TODO FIXME This is an Armor Type... :/
				try
				{
					ArmorType at = ArmorType.valueOf(tokString);
					if (setArmor)
					{
						Logging.errorPrint("Encountered Second Armor Type in "
							+ getTokenName() + " this is not valid.");
						return false;
					}
					context.obj.put(obj, ObjectKey.UNENCUMBERED_ARMOR, at);
					setArmor = true;
				}
				catch (IllegalArgumentException e2)
				{
					Logging.errorPrint("Misunderstood text in "
						+ getTokenName() + ": " + tokString
						+ " is not a Load or ArmorType");
					return false;
				}
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		pcgen.cdom.enumeration.Load load =
				context.obj.getObject(obj, ObjectKey.UNENCUMBERED_LOAD);
		ArmorType at = context.obj.getObject(obj, ObjectKey.UNENCUMBERED_ARMOR);
		if (load == null && at == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if (load != null)
		{
			sb.append(load);
		}
		if (at != null)
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(at);
		}
		return new String[]{sb.toString()};
	}
}
