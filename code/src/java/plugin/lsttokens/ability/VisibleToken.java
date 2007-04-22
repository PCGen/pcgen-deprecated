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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbilityLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag in the
 * definition of an Ability.
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements AbilityLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/**
	 * @see pcgen.persistence.lst.AbilityLstToken#parse(pcgen.core.Ability,
	 *      java.lang.String)
	 */
	public boolean parse(Ability ability, String value)
	{
		final String visType = value.toUpperCase();
		if (visType.startsWith("EXPORT"))
		{
			if (!"EXPORT".equalsIgnoreCase(visType))
			{
				Logging.errorPrint(visType + " is not a valid value for "
					+ getTokenName());
				Logging.errorPrint(" using EXPORT");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			ability.setVisibility(Visibility.EXPORT);
		}
		else if (visType.startsWith("NO"))
		{
			if (!"NO".equalsIgnoreCase(visType))
			{
				Logging.errorPrint(visType + " is not a valid value for "
					+ getTokenName());
				Logging.errorPrint(" using NO");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			ability.setVisibility(Visibility.NO);
		}
		else if (visType.startsWith("DISPLAY"))
		{
			if (!"DISPLAY".equalsIgnoreCase(visType))
			{
				Logging.errorPrint(visType + " is not a valid value for "
					+ getTokenName());
				Logging.errorPrint(" using DISPLAY");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			ability.setVisibility(Visibility.DISPLAY);
		}
		else
		{
			ability.setVisibility(Visibility.YES);
		}
		return true;
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (value.equals("YES"))
		{
			ability.put(ObjectKey.VISIBILITY, Visibility.YES);
		}
		else if (value.equals("DISPLAY"))
		{
			ability.put(ObjectKey.VISIBILITY, Visibility.DISPLAY);
		}
		else if (value.equals("EXPORT"))
		{
			ability.put(ObjectKey.VISIBILITY, Visibility.EXPORT);
		}
		else if (value.equals("NO"))
		{
			ability.put(ObjectKey.VISIBILITY, Visibility.NO);
		}
		else
		{
			Logging.errorPrint("Unable to understand " + getTokenName()
				+ " tag: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Visibility vis = ability.get(ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.YES))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.EXPORT))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.NO))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for an Ability");
			return null;
		}
		return new String[]{visString};
	}

}
