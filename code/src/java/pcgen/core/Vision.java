/*
 * PCClass.java
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on October 25, 2006
 *
 * $Id: PCClass.java 1526 2006-10-25 03:56:08Z thpr $
 */
package pcgen.core;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.LSTWriteable;
import pcgen.util.enumeration.VisionType;

public class Vision extends ConcretePrereqObject implements Comparable<Vision>,
		LSTWriteable
{

	private final VisionType visionType;

	private final String distance;

	public Vision(VisionType type, String dist)
	{
		if (type == null)
		{
			throw new IllegalArgumentException("Vision Type cannot be null");
		}
		visionType = type;
		distance = dist;
	}

	public String getDistance()
	{
		return distance;
	}

	public VisionType getType()
	{
		return visionType;
	}

	@Override
	public String toString()
	{
		try
		{
			return toString(Integer.parseInt(distance));
		}
		catch (NumberFormatException e)
		{
			return visionType + " (" + distance + ")";
		}
	}

	private String toString(int d)
	{
		if (d <= 0)
		{
			return visionType.toString();
		}
		else
		{
			return visionType + " (" + d + "')";
		}
	}

	@Override
	public int hashCode()
	{
		return visionType.hashCode()
			^ (distance == null ? 0 : distance.hashCode());
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Vision)
		{
			Vision v2 = (Vision) o;
			if (v2.visionType.equals(visionType))
			{
				return v2.distance == null && distance == null
					|| distance != null && distance.equals(v2.distance);
			}
		}
		return false;
	}

	public int compareTo(Vision v)
	{
		// CONSIDER This is potentially a slow method, but definitely works -
		// thpr 10/26/06
		return toString().compareTo(v.toString());
	}

	public static Vision getVision(String visionType)
	{
		// expecting value in form of Darkvision (60') or Darkvision
		int commaLoc = visionType.indexOf(',');
		if (commaLoc != -1)
		{
			throw new IllegalArgumentException("Invalid Vision: " + visionType
				+ ". May not contain a comma");
		}
		int quoteLoc = visionType.indexOf('\'');
		int openParenLoc = visionType.indexOf('(');
		String distance;
		String type;
		if (openParenLoc == -1)
		{
			if (visionType.indexOf(')') != -1)
			{
				throw new IllegalArgumentException("Invalid Vision: "
					+ visionType + ". Had close paren without open paren");
			}
			if (quoteLoc != -1)
			{
				throw new IllegalArgumentException("Invalid Vision: "
					+ visionType + ". Had quote parens");
			}
			type = visionType;
			distance = "0";
		}
		else
		{
			int length = visionType.length();
			if (visionType.indexOf(')') != length - 1)
			{
				throw new IllegalArgumentException("Invalid Vision: "
					+ visionType + ". Close paren not at end of string");
			}
			int endDistance = length - 1;
			if (quoteLoc != -1)
			{
				if (quoteLoc == length - 2)
				{
					endDistance--;
				}
				else
				{
					throw new IllegalArgumentException(
						"Invalid Vision: "
							+ visionType
							+ ". Foot character ' not immediately before close paren");
				}
			}
			type = visionType.substring(0, openParenLoc).trim();
			distance = visionType.substring(openParenLoc + 1, endDistance);
			if (distance.length() == 0)
			{
				throw new IllegalArgumentException("Invalid Vision: "
					+ visionType + ". No Distance provided");
			}
			if (quoteLoc != -1)
			{
				try
				{
					Integer.parseInt(distance);
				}
				catch (NumberFormatException nfe)
				{
					throw new IllegalArgumentException(
						"Invalid Vision: "
							+ visionType
							+ ". Vision Distance with Foot character ' was not an integer");
				}
			}
		}
		if (type.length() == 0)
		{
			throw new IllegalArgumentException("Invalid Vision: " + visionType
				+ ". No Vision Type provided");
		}
		return new Vision(VisionType.getVisionType(type), distance);
	}

	/*
	 * REFACTOR NEED TO GET RID OF THIS - REFERENCES PlayerCharacter :(
	 */
	public String toString(PlayerCharacter aPC)
	{
		return toString(aPC.getVariableValue(distance, "").intValue());
	}

	public String getLSTformat()
	{
		return toString();
	}
}
