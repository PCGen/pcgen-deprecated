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
package plugin.lsttokens.skill;

import pcgen.base.util.Logging;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(Skill skill, String value)
	{
		final String visType = value.toUpperCase();

		if (visType.startsWith("YES"))
		{
			if (!value.equals("YES"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'YES' (in all CAPS)");
			}
			skill.setVisibility(Visibility.YES);
		}
		else if (visType.startsWith("ALWAYS"))
		{
			if (!value.equals("ALWAYS"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'YES' or 'ALWAYS' (in all CAPS)");
				skill.setVisibility(Visibility.YES);
			}
		}
		else if (value.equals("DISPLAY"))
		{
			skill.setVisibility(Visibility.DISPLAY);
		}
		else if (visType.startsWith("GUI"))
		{
			if (!value.equals("GUI"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'GUI' (in all CAPS)");
			}
			skill.setVisibility(Visibility.DISPLAY);
		}
		else if (visType.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'EXPORT' (in all CAPS)");
			}
			skill.setVisibility(Visibility.EXPORT);
		}
		else if (visType.startsWith("CSHEET"))
		{
			if (!value.equals("CSHEET"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'CSHEET' or 'EXPORT' (in all CAPS)");
			}
			skill.setVisibility(Visibility.EXPORT);
		}
		else
		{
			Logging.errorPrint("Invalid Visibility: " + value);
			return false;
		}

		String[] elements = value.split("\\|");

		if (elements.length > 1)
		{
			if (elements[1].equalsIgnoreCase("READONLY")
				&& !visType.startsWith("EXPORT"))
			{
				if (!elements[1].equals("READONLY"))
				{
					Logging.errorPrint("In " + getTokenName()
						+ " Use of lower case is deprecated in "
						+ getTokenName()
						+ ".  Please use 'READONLY' (in all CAPS): " + value);
				}
				skill.setReadOnly(true);
			}
			else
			{
				Logging
					.errorPrint("Invalid Combination in "
						+ getTokenName()
						+ ".  | must separate READONLY and cannot be used with EXPORT: "
						+ value);
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, Skill skill, String value)
		throws PersistenceLayerException
	{
		String visString = value;
		int pipeLoc = value.indexOf(Constants.PIPE);
		boolean readOnly = false;
		if (pipeLoc != -1)
		{
			if (value.substring(pipeLoc + 1).equals("READONLY"))
			{
				visString = value.substring(0, pipeLoc);
				readOnly = true;
			}
			else
			{
				Logging.errorPrint("Misunderstood text after pipe on Tag: "
					+ value);
				return false;
			}
		}
		Visibility vis;
		try
		{
			vis = Visibility.valueOf(visString);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Unable to understand " + getTokenName()
				+ " tag: " + value);
			return false;
		}
		if (readOnly)
		{
			if (vis.equals(Visibility.EXPORT))
			{
				Logging.errorPrint("|READONLY suffix not valid with "
					+ getTokenName() + " EXPORT or CSHEET");
				return false;
			}
			context.obj.put(skill, ObjectKey.READ_ONLY, Boolean.TRUE);
		}
		context.obj.put(skill, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Visibility vis = context.obj.getObject(skill, ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		if (!vis.equals(Visibility.YES) && !vis.equals(Visibility.DISPLAY)
			&& !vis.equals(Visibility.EXPORT))
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a Skill");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(vis.getLSTFormat());
		Boolean readOnly = context.obj.getObject(skill, ObjectKey.READ_ONLY);
		if (readOnly != null)
		{
			if (vis.equals(Visibility.EXPORT))
			{
				context.addWriteMessage("ReadOnly is not allowed on a "
					+ "Skill with Visibility " + vis);
				return null;
			}
			sb.append('|').append("READONLY");
		}
		return new String[]{sb.toString()};
	}

}
