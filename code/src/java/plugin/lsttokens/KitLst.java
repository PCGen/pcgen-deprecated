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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class KitLst extends AbstractToken implements GlobalLstToken
{

	@Override
	public String getTokenName()
	{
		return "KIT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setKitString(anInt + "|" + value);
		}
		else
		{
			obj.setKitString("0|" + value);
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		int pipeLoc = value.indexOf(Constants.PIPE);

		int count;
		try
		{
			count = Integer.parseInt(value.substring(0, pipeLoc));
			if (count <= 0)
			{
				Logging.errorPrint("Count in " + getTokenName()
					+ " must be > 0");
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " parse error: first value must be a number");
			return false;
		}

		PrimitiveChoiceSet<?> chooser =
				ChooseLoader.parseToken(context, obj, "KIT", value
					.substring(pipeLoc + 1));
		if (chooser == null)
		{
			Logging.errorPrint("Internal Error: " + getTokenName()
				+ " failed to build Chooser");
			return false;
		}
		ChoiceSet<?> choiceSet = new ChoiceSet("KIT", chooser);
		PCGraphGrantsEdge edge =
				context.getGraphContext().grant(getTokenName(), obj, choiceSet);
		edge.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		edge.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		ChoiceSet<?> choice =
				context.getObjectContext().getObject(obj, ObjectKey.KIT_CHOICE);
		if (choice == null)
		{
			return null;
		}
		// Substring takes off the KIT| prefix
		return new String[]{choice.getLSTformat().substring(4)};
	}
}
