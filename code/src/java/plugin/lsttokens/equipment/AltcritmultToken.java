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

import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with ALTCRITMULT token
 */
public class AltcritmultToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "ALTCRITMULT";
	}

	public boolean parse(Equipment eq, String value)
	{
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				eq.setAltCritMult(Integer.parseInt(value.substring(1)));
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
			return true;
		}
		else if (value.equals("-"))
		{
			eq.setAltCritMult(-1);
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		Integer cm = null;
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				cm = Integer.valueOf(value.substring(1));
				if (cm.intValue() <= 0)
				{
					Logging.errorPrint(getTokenName() + " cannot be <= 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint(getTokenName()
					+ " was expecting an Integer: " + value);
				return false;
			}
		}
		else if ("-".equals(value))
		{
			cm = Integer.valueOf(-1);
		}
		if (cm == null)
		{
			Logging.errorPrint(getTokenName()
				+ " was expecting x followed by an integer "
				+ "or the special value '-' (representing no value)");
			return false;
		}
		EquipmentHead primHead = getEquipmentHead(context, eq, 2);
		context.obj.put(primHead, IntegerKey.CRIT_MULT, cm);
		return true;
	}

	protected EquipmentHead getEquipmentHead(LoadContext context, Equipment eq,
		int index)
	{
		EquipmentHead head = getEquipmentHeadReference(context, eq, index);
		if (head == null)
		{
			// Isn't there already, so create new
			head = new EquipmentHead(this, index);
			context.graph.grant(Constants.VT_EQ_HEAD, eq, head);
		}
		return head;
	}

	private EquipmentHead getEquipmentHeadReference(LoadContext context,
		Equipment eq, int index)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(Constants.VT_EQ_HEAD, eq,
					EquipmentHead.class);
		for (PCGraphEdge edge : edges)
		{
			EquipmentHead head =
					(EquipmentHead) edge.getSinkNodes().iterator().next();
			if (head.getHeadIndex() == index)
			{
				return head;
			}
		}
		return null;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = getEquipmentHeadReference(context, eq, 2);
		if (head == null)
		{
			return null;
		}
		Integer mult = context.obj.getInteger(head, IntegerKey.CRIT_MULT);
		if (mult == null)
		{
			return null;
		}
		int multInt = mult.intValue();
		String retString;
		if (multInt == -1)
		{
			retString = "-";
		}
		else
		{
			retString = "x" + multInt;
		}
		return new String[]{retString};
	}
}
