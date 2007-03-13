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
package plugin.lsttokens.equipment;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.Capacity;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with CONTAINS token
 */
public class ContainsToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "CONTAINS";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.setContainer(value);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		if (!pipeTok.hasMoreTokens())
		{
			// Differs from 5.12
			return false;
		}

		String weightCapacity = pipeTok.nextToken();

		if (weightCapacity.charAt(0) == Constants.CHAR_ASTERISK)
		{
			eq.put(ObjectKey.CONTAINER_CONSTANT_WEIGHT, Boolean.TRUE);
			weightCapacity = weightCapacity.substring(1);
		}

		int percentLoc = weightCapacity.indexOf(Constants.PERCENT);
		if (percentLoc != weightCapacity.lastIndexOf(Constants.PERCENT))
		{
			return false;
		}
		if (percentLoc != -1)
		{
			String redString = weightCapacity.substring(0, percentLoc);
			weightCapacity = weightCapacity.substring(percentLoc + 1);

			try
			{
				eq.put(IntegerKey.CONTAINER_REDUCE_WEIGHT, Integer
					.valueOf(redString));
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
		}

		try
		{
			eq.put(ObjectKey.CONTAINER_WEIGHT_CAPACITY, Double
				.valueOf(weightCapacity));
		}
		catch (NumberFormatException ex)
		{
			return false;
		}

		boolean limited = true;
		if (!pipeTok.hasMoreTokens())
		{
			limited = false;
			eq.addToListFor(ListKey.CAPACITY, Capacity.ANY);
		}

		double limitedCapacity = 0;

		while (pipeTok.hasMoreTokens())
		{
			String typeString = pipeTok.nextToken();
			int equalLoc = typeString.indexOf(Constants.EQUALS);
			if (equalLoc != typeString.lastIndexOf(Constants.EQUALS))
			{
				return false;
			}
			if (equalLoc == -1)
			{
				limited = false;
				Type t = Type.getConstant(typeString);
				eq.addToListFor(ListKey.CAPACITY, new Capacity(t, -1));
			}
			else
			{
				String itemType = typeString.substring(0, equalLoc);
				double itemNumber =
						Double.parseDouble(typeString.substring(equalLoc + 1));
				if (limited)
				{
					limitedCapacity += itemNumber;
				}
				Type t = Type.getConstant(itemType);
				eq.addToListFor(ListKey.CAPACITY, new Capacity(t, itemNumber));
			}
		}

		/*
		 * TODO FIXME Can this be optimized, or can CAPACITY be placed more than
		 * once in a piece of Equipment?
		 * 
		 * I think it CAN'T because then the limitedCapacity could be messed up
		 * anyway... :/
		 */
		List<Capacity> list = eq.getListFor(ListKey.CAPACITY);
		for (Capacity cap : list)
		{
			if ("Total".equals(cap.getType()))
			{
				return true;
			}
		}

		double totalCapacity = limited ? limitedCapacity : -1;
		eq.addToListFor(ListKey.CAPACITY, Capacity
			.getTotalCapacity(totalCapacity));

		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		List<Capacity> capacityList = eq.getListFor(ListKey.CAPACITY);
		if (capacityList == null || capacityList.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();

		Boolean b = eq.get(ObjectKey.CONTAINER_CONSTANT_WEIGHT);
		if (b != null && b.booleanValue())
		{
			sb.append(Constants.CHAR_ASTERISK);
		}

		Integer reducePercent = eq.get(IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (reducePercent != null)
		{
			sb.append(reducePercent).append(Constants.PERCENT);
		}

		Double cap = eq.get(ObjectKey.CONTAINER_WEIGHT_CAPACITY);
		if (cap == null)
		{
			// CONSIDER ERROR??
			return null;
		}
		sb.append(cap);

		if (capacityList.size() == 2)
		{
			for (Capacity c : capacityList)
			{
				if ("Any".equals(c.getType()) && c.getCapacity() == -1)
				{
					// Special Case: Nothing additional
					return new String[]{sb.toString()};
				}
			}
		}
		double limitedCapacity = 0;
		boolean limited = true;
		boolean needsPipe = false;
		Capacity total = null;
		for (Capacity c : capacityList)
		{
			Type capType = c.getType();
			if ("Total".equals(capType))
			{
				total = c;
			}
			else
			{
				if (needsPipe)
				{
					sb.append(Constants.PIPE);
				}
				double thisCap = c.getCapacity();
				sb.append(capType);
				if (thisCap == -1)
				{
					limited = false;
				}
				else
				{
					if (limited)
					{
						limitedCapacity += thisCap;
					}
					sb.append(Constants.EQUALS).append(thisCap);
				}
			}
		}
		if (total == null)
		{
			// Error
			return null;
		}
		if (limitedCapacity != total.getCapacity())
		{
			// Need to write out total
			sb.append("Total").append(Constants.EQUALS).append(
				total.getCapacity());
		}
		return new String[]{sb.toString()};
	}
}
