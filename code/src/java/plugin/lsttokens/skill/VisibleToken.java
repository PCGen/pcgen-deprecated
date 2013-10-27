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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements SkillLstToken, CDOMPrimaryToken<CDOMSkill>
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
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Skill");
				Logging.deprecationPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			skill.setVisibility(Visibility.YES);
		}
		else if (visType.startsWith("ALWAYS"))
		{
			if (!value.equals("ALWAYS"))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Skill");
				Logging.deprecationPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.deprecationPrint(" assuming you meant ALWAYS, please use ALWAYS (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			skill.setVisibility(Visibility.YES);
		}
		else if (value.equals("DISPLAY"))
		{
			skill.setVisibility(Visibility.DISPLAY);
		}
		else if (visType.startsWith("GUI"))
		{
			if (!value.equals("GUI"))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Skill");
				Logging.deprecationPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.deprecationPrint(" assuming you meant GUI, please use GUI or DISPLAY (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			skill.setVisibility(Visibility.DISPLAY);
		}
		else if (visType.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Skill");
				Logging.deprecationPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.deprecationPrint(" assuming you meant EXPORT, please use EXPORT or CSHEET (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			skill.setVisibility(Visibility.EXPORT);
		}
		else if (visType.startsWith("CSHEET"))
		{
			if (!value.equals("CSHEET"))
			{
				Logging.deprecationPrint("Abbreviation used in "
					+ getTokenName() + " in Skill");
				Logging.deprecationPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.deprecationPrint(" assuming you meant CSHEET, please use EXPORT or CSHEET (exact String, upper case) in the LST file");
				Logging.deprecationPrint(" This will break after PCGen 5.14");
			}
			skill.setVisibility(Visibility.EXPORT);
		}
		else
		{
			Logging.deprecationPrint("Unexpected value used in "
				+ getTokenName() + " in Skill");
			Logging.deprecationPrint(" " + visType
				+ " is not a valid value for " + getTokenName());
			Logging
				.deprecationPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
			Logging
				.deprecationPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
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
					Logging
						.deprecationPrint("In Skill "
							+ getTokenName()
							+ " Use of lower case is deprecated in "
							+ getTokenName()
							+ ".  Please use 'READONLY' (exact String, upper case): "
							+ value);
				}
				skill.setReadOnly(true);
			}
			else
			{
				Logging
					.deprecationPrint("Invalid Combination in Skill LST "
						+ getTokenName()
						+ ".  | must separate READONLY and cannot be used with EXPORT: "
						+ value);
				return false;
			}
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMSkill skill, String value)
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
			context.getObjectContext().put(skill, ObjectKey.VISIBILITY, vis);
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
			context.getObjectContext().put(skill, ObjectKey.READ_ONLY, Boolean.TRUE);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMSkill skill)
	{
		Visibility vis = context.getObjectContext().getObject(skill, ObjectKey.VISIBILITY);
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
		Boolean readOnly = context.getObjectContext().getObject(skill, ObjectKey.READ_ONLY);
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

	public Class<CDOMSkill> getTokenClass()
	{
		return CDOMSkill.class;
	}
}
