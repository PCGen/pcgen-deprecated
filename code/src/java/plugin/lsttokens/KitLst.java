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
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choice.ReferenceChooser;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Kit;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class KitLst extends AbstractToken implements GlobalLstToken
{

	private static final Class<Kit> KIT_CLASS = Kit.class;

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
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		List<CDOMReference<Kit>> list = new ArrayList<CDOMReference<Kit>>();
		int count;
		try
		{
			count = Integer.parseInt(tok.nextToken());
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

		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_DOT_CLEAR.equals(tokText))
			{
				if (!list.isEmpty())
				{
					Logging.errorPrint("Invalid " + getTokenName()
						+ " .CLEAR was not the first item: " + value);
					return false;
				}
				context.obj.put(obj, ObjectKey.KIT_CHOICE, null);
			}
			else
			{
				list.add(context.ref.getCDOMReference(KIT_CLASS, tokText));
			}
		}
		ReferenceChooser<Kit> chooser = new ReferenceChooser<Kit>(list);
		context.obj.put(obj, ObjectKey.KIT_CHOICE, chooser);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		ChoiceSet<?> choice = context.obj.getObject(obj, ObjectKey.KIT_CHOICE);
		if (choice == null)
		{
			return null;
		}
		// TODO Not sure how to unparse a CHOOSE ;)
		return null;
	}
}
