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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.Capacity;
import pcgen.core.Equipment;
import pcgen.persistence.Changes;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Deals with CONTAINS token
 */
public class ContainsToken extends AbstractToken implements EquipmentLstToken
{

	@Override
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
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

		if (!pipeTok.hasMoreTokens())
		{
			// Differs from 5.12
			return false;
		}

		/*
		 * TODO I believe this needs to clear the List and if it already
		 * contains something, then should it provide an informational message?
		 */
		String weightCapacity = pipeTok.nextToken();

		boolean hadAsterisk = false;
		if (weightCapacity.charAt(0) == Constants.CHAR_ASTERISK)
		{
			hadAsterisk = true;
			context.obj.put(eq, ObjectKey.CONTAINER_CONSTANT_WEIGHT,
				Boolean.TRUE);
			weightCapacity = weightCapacity.substring(1);
		}

		int percentLoc = weightCapacity.indexOf(Constants.PERCENT);
		if (percentLoc != weightCapacity.lastIndexOf(Constants.PERCENT))
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"Cannot have two weight reduction "
					+ "characters (indicated by %): " + value);
			return false;
		}
		if (percentLoc != -1)
		{
			if (hadAsterisk)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
					"Cannot have Constant Weight (indicated by *) "
						+ "and weight reduction (indicated by %): " + value);
				return false;
			}
			String redString = weightCapacity.substring(0, percentLoc);
			weightCapacity = weightCapacity.substring(percentLoc + 1);

			try
			{
				context.obj.put(eq, IntegerKey.CONTAINER_REDUCE_WEIGHT, Integer
					.valueOf(redString));
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
		}

		BigDecimal weightCap;
		if ("UNLIM".equals(weightCapacity))
		{
			weightCap = Capacity.UNLIMITED;
		}
		else
		{
			try
			{
				weightCap =
						BigDecimalHelper.trimBigDecimal(new BigDecimal(
							weightCapacity));
				if (BigDecimal.ZERO.compareTo(weightCap) >= 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Weight Capacity must be > 0: " + weightCapacity
							+ "\n  Use 'UNLIM' (not -1) for unlimited Count");
					return false;
				}
			}
			catch (NumberFormatException ex)
			{
				Logging
					.errorPrint("Weight Capacity must be 'UNLIM or a number > 0: "
						+ weightCapacity);
				return false;
			}
		}
		context.obj.put(eq, ObjectKey.CONTAINER_WEIGHT_CAPACITY, weightCap);

		List<Capacity> capacityList = new ArrayList<Capacity>();
		boolean limited = true;
		if (!pipeTok.hasMoreTokens())
		{
			limited = false;
			capacityList.add(Capacity.ANY);
		}

		BigDecimal limitedCapacity = BigDecimal.ZERO;

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
				capacityList.add(new Capacity(t, Capacity.UNLIMITED));
			}
			else
			{
				String itemType = typeString.substring(0, equalLoc);
				String itemNumString = typeString.substring(equalLoc + 1);
				BigDecimal itemNumber;
				if ("UNLIM".equals(itemNumString))
				{
					limited = false;
					itemNumber = Capacity.UNLIMITED;
				}
				else
				{
					try
					{
						itemNumber =
								BigDecimalHelper.trimBigDecimal(new BigDecimal(
									itemNumString));
					}
					catch (NumberFormatException ex)
					{
						Logging.errorPrint("Item Number for " + itemType
							+ " must be 'UNLIM' or a number > 0: "
							+ itemNumString);
						return false;
					}
					if (BigDecimal.ZERO.compareTo(itemNumber) >= 0)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
							"Cannot have negative quantity of " + itemType
								+ ": " + value);
						return false;
					}
				}
				if (limited)
				{
					limitedCapacity = limitedCapacity.add(itemNumber);
				}
				Type t = Type.getConstant(itemType);
				capacityList.add(new Capacity(t, itemNumber));
			}
		}

		boolean requiresTotal = true;
		for (Capacity cap : capacityList)
		{
			context.obj.addToList(eq, ListKey.CAPACITY, cap);
			if (cap.getType() == null)
			{
				requiresTotal = false;
			}
		}

		if (requiresTotal)
		{
			BigDecimal totalCapacity =
					limited ? limitedCapacity : Capacity.UNLIMITED;
			context.obj.addToList(eq, ListKey.CAPACITY, Capacity
				.getTotalCapacity(totalCapacity));
		}

		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		Changes<Capacity> changes =
				context.obj.getListChanges(eq, ListKey.CAPACITY);
		if (changes == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();

		Boolean b =
				context.obj.getObject(eq, ObjectKey.CONTAINER_CONSTANT_WEIGHT);
		if (b != null && b.booleanValue())
		{
			sb.append(Constants.CHAR_ASTERISK);
		}

		Integer reducePercent =
				context.obj.getInteger(eq, IntegerKey.CONTAINER_REDUCE_WEIGHT);
		if (reducePercent != null)
		{
			sb.append(reducePercent).append(Constants.PERCENT);
		}

		BigDecimal cap =
				context.obj.getObject(eq, ObjectKey.CONTAINER_WEIGHT_CAPACITY);
		if (cap == null)
		{
			// CONSIDER ERROR??
			return null;
		}
		sb.append(cap);

		Collection<Capacity> capacityList = changes.getAdded();
		if (capacityList.size() == 1)
		{
			for (Capacity c : capacityList)
			{
				if (c.getType() == null
					&& Capacity.UNLIMITED.equals(c.getCapacity()))
				{
					// Special Case: Nothing additional
					return new String[]{sb.toString()};
				}
			}
		}
		BigDecimal limitedCapacity = BigDecimal.ZERO;
		boolean limited = true;
		Capacity total = null;
		for (Capacity c : capacityList)
		{
			Type capType = c.getType();
			if (capType == null)
			{
				total = c;
			}
			else
			{
				sb.append(Constants.PIPE);
				BigDecimal thisCap = c.getCapacity();
				sb.append(capType);
				if (Capacity.UNLIMITED.equals(thisCap))
				{
					limited = false;
				}
				else
				{
					if (limited)
					{
						limitedCapacity = limitedCapacity.add(thisCap);
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
		if (!limitedCapacity.equals(total.getCapacity())
			&& !Capacity.UNLIMITED.equals(total.getCapacity()))
		{
			// Need to write out total
			sb.append("Total").append(Constants.EQUALS).append(
				total.getCapacity());
		}
		return new String[]{sb.toString()};
	}
}
