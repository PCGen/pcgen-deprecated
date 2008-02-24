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
 * Current Ver: $Revision: 2959 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-05-20 00:02:34 -0400 (Sun, 20 May 2007) $
 */
package plugin.lstcompatibility.equipment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.Capacity;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Deals with CONTAINS token
 */
public class Contains512Token extends AbstractToken implements
CDOMCompatibilityToken<CDOMEquipment>
{

	BigDecimal MINUS_ONE = new BigDecimal(-1);

	@Override
	public String getTokenName()
	{
		return "CONTAINS";
	}

	public boolean parse(LoadContext context, CDOMEquipment eq, String value)
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
			context.getObjectContext().put(eq,
				ObjectKey.CONTAINER_CONSTANT_WEIGHT, Boolean.TRUE);
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
				context.getObjectContext().put(eq,
					IntegerKey.CONTAINER_REDUCE_WEIGHT,
					Integer.valueOf(redString));
			}
			catch (NumberFormatException ex)
			{
				return false;
			}
		}

		try
		{
			context.getObjectContext().put(eq,
				ObjectKey.CONTAINER_WEIGHT_CAPACITY,
				new BigDecimal(weightCapacity));
		}
		catch (NumberFormatException ex)
		{
			return false;
		}

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
				try
				{
					String quantityString = typeString.substring(equalLoc + 1);
					BigDecimal itemNumber =
							BigDecimalHelper.trimBigDecimal(new BigDecimal(
								quantityString));
					if (MINUS_ONE.compareTo(itemNumber) == 0)
					{
						limited = false;
						itemNumber = Capacity.UNLIMITED;
					}
					else if (BigDecimal.ZERO.compareTo(itemNumber) >= 0)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
							"Cannot have negative quantity of " + itemType
								+ ": " + value);
						return false;
					}
					if (limited)
					{
						limitedCapacity = limitedCapacity.add(itemNumber);
					}
					Type t = Type.getConstant(itemType);
					capacityList.add(new Capacity(t, itemNumber));
				}
				catch (NumberFormatException nfe)
				{
					return false;
				}
			}
		}

		boolean requiresTotal = true;
		for (Capacity cap : capacityList)
		{
			context.getObjectContext().addToList(eq, ListKey.CAPACITY, cap);
			if (cap.getType() == null)
			{
				requiresTotal = false;
			}
		}

		if (requiresTotal)
		{
			BigDecimal totalCapacity =
					limited ? limitedCapacity : Capacity.UNLIMITED;
			context.getObjectContext().addToList(eq, ListKey.CAPACITY,
				Capacity.getTotalCapacity(totalCapacity));
		}

		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public int compatibilityPriority()
	{
		return 0;
	}

	public Class<CDOMEquipment> getTokenClass()
	{
		return CDOMEquipment.class;
	}
}
