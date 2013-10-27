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
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.RemoveLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class RemoveLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "REMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		String key;
		if (value.startsWith("FEAT"))
		{
			key = "FEAT";
		}
		else
		{
			Logging
					.errorPrint(getTokenName() + " only supports FEAT: "
							+ value);
			return false;
		}

		int keyLength = key.length();
		if (value.charAt(keyLength) == '(')
		{
			Logging
					.deprecationPrint("REMOVE: syntax with parenthesis is deprecated.");
			Logging.deprecationPrint("Please use REMOVE:" + key + "|...");
			if (anInt > -9)
			{
				obj.setRemoveString(anInt + "|" + value);
			}
			else
			{
				obj.setRemoveString("0|" + value);
			}
			return true;
		}
		// Guaranteed new format here
		RemoveLoader.parseLine(obj, key, value.substring(keyLength + 1), anInt);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " requires a SubToken");
			return false;
		}
		return context.processSubToken(obj, getTokenName(), value.substring(0,
				pipeLoc), value.substring(pipeLoc + 1));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		return context.unparse(obj, getTokenName());
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
