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
 * Current Ver: $Revision: 3059 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-27 14:26:01 -0400 (Sun, 27 May 2007) $
 */
package plugin.lstcompatibility.global;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Vision;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.VisionType;

/**
 * <code>VisionLst</code> handles the processing of the VISION tag in LST
 * code.
 * 
 * @author Devon Jones
 * @version $Revision: 3059 $
 */
public class Vision514Lst implements CDOMCompatibilityToken<CDOMObject>
{

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		List<Vision> list = new ArrayList<Vision>();
		while (aTok.hasMoreTokens())
		{
			String visionString = aTok.nextToken();

			if (visionString.indexOf(',') >= 0)
			{
				final StringTokenizer visionTok =
						new StringTokenizer(visionString, ",");
				String numberTok = visionTok.nextToken();
				if (numberTok == "2")
				{
					visionString = ".CLEAR." + visionTok.nextToken();
				}
				else if (numberTok == "0")
				{
					visionString = ".SET." + visionTok.nextToken();
				}
				else
				{
					visionString = visionTok.nextToken();
				}
			}

			if (".CLEAR".equals(visionString))
			{
				context.getGraphContext().removeAll(getTokenName(), obj);
				continue;
			}

			if (visionString.startsWith(".CLEAR."))
			{
				try
				{
					Vision vis = getVision(visionString.substring(7));
					context.getObjectContext().revoke(getTokenName(), obj, vis);
				}
				catch (IllegalArgumentException e)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Bad Syntax for Cleared Vision in " + getTokenName());
					Logging.addParseMessage(Logging.LST_ERROR, e.getMessage());
					return false;
				}
			}
			else
			{
				if (visionString.startsWith(".SET."))
				{
					context.getGraphContext().removeAll(getTokenName(), obj);
					visionString = visionString.substring(5);
				}
				try
				{
					list.add(getVision(visionString));
				}
				catch (IllegalArgumentException e)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Bad Syntax for Vision in " + getTokenName());
					Logging.addParseMessage(Logging.LST_ERROR, e.getMessage());
					return false;
				}
			}
		}
		for (Vision vis : list)
		{
			context.getObjectContext().give(getTokenName(), obj, vis);
		}
		return true;
	}

	private Vision getVision(String visionType)
	{
		// expecting value in form of Darkvision (60') or Darkvision
		StringTokenizer cTok = new StringTokenizer(visionType, "(')");
		String aKey = cTok.nextToken().trim(); // e.g. Darkvision
		String aVal = "0";
		if (cTok.hasMoreTokens())
		{
			aVal = cTok.nextToken(); // e.g. 60
		}
		return new Vision(VisionType.getVisionType(aKey), aVal);
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public String getTokenName()
	{
		return "VISION";
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
