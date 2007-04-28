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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Vision;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.VisionType;

/**
 * <code>VisionLst</code> handles the processing of the VISION tag in LST
 * code.
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisionLst implements GlobalLstToken
{

	private static final Class<Vision> VISION_CLASS = Vision.class;

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISION";
	}

	/**
	 * @see pcgen.persistence.lst.GlobalLstToken#parse(pcgen.core.PObject,
	 *      java.lang.String, int)
	 */
	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		while (aTok.hasMoreTokens())
		{
			String visionString = aTok.nextToken();

			if (".CLEAR".equals(visionString))
			{
				obj.clearVisionList();
				continue;
			}

			if (visionString.indexOf(',') >= 0)
			{
				Logging
					.errorPrint("Use of comma in VISION Tag is deprecated.  Use .CLEAR.[Vision] instead.");
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

			Vision vis = null;
			if (visionString.startsWith(".CLEAR."))
			{
				obj.removeVisionType(VisionType.getVisionType(visionString
					.substring(7)));
			}
			else if (visionString.startsWith(".SET."))
			{
				obj.clearVisionList();
				vis = getVision(anInt, visionString.substring(5));
			}
			else
			{
				vis = getVision(anInt, visionString);
			}

			if (vis != null)
			{
				if (anInt > -9)
				{
					((PCClass) obj).addVision(anInt, vis);
				}
				else
				{
					obj.addVision(vis);
				}
			}

		}
		return true;
	}

	private Vision getVision(int anInt, String visionType)
	{
		// expecting value in form of Darkvision (60')
		final StringTokenizer cTok = new StringTokenizer(visionType, "(')");
		final String aKey = cTok.nextToken().trim(); // e.g. Darkvision
		String aVal = "0";
		if (cTok.hasMoreTokens())
		{
			aVal = cTok.nextToken(); // e.g. 60
		}
		return new Vision(VisionType.getVisionType(aKey), aVal);
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		List<Vision> list = new ArrayList<Vision>();
		while (aTok.hasMoreTokens())
		{
			String visionString = aTok.nextToken();

			if (".CLEAR".equals(visionString))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
					VISION_CLASS);
				continue;
			}

			if (visionString.startsWith(".CLEAR."))
			{
				try
				{
					//TODO Need to defer unlink until prove that there are no errors
					Vision vis = Vision.getVision(visionString.substring(7));
					context.graph.unlinkChildNode(getTokenName(), obj, vis);
				}
				catch (IllegalArgumentException e)
				{
					Logging.errorPrint("Bad Syntax for Cleared Vision in "
						+ getTokenName());
					Logging.errorPrint(e.getMessage());
					return false;
				}
			}
			else
			{
				if (visionString.startsWith(".SET."))
				{
					context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
						VISION_CLASS);
					visionString = visionString.substring(5);
				}
				try
				{
					list.add(Vision.getVision(visionString));
				}
				catch (IllegalArgumentException e)
				{
					Logging.errorPrint("Bad Syntax for Vision in "
						+ getTokenName());
					Logging.errorPrint(e.getMessage());
					return false;
				}
			}
		}
		for (Vision vis : list)
		{
			context.graph.linkObjectIntoGraph(getTokenName(), obj, vis);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					VISION_CLASS);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (PCGraphEdge edge : edgeList)
		{
			Vision vis = (Vision) edge.getSinkNodes().get(0);
			set.add(vis.toString());
		}
		return new String[] { StringUtil.join(set, Constants.PIPE)};
	}
}
