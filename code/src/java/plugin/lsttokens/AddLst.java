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
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class AddLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "ADD";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		String key;
		if (value.startsWith("FEAT"))
		{
			key = "FEAT";
		}
		else if (value.startsWith("VFEAT"))
		{
			key = "VFEAT";
		}
		else if (value.startsWith("ABILITY"))
		{
			key = "ABILITY";
		}
		else if (value.startsWith("VABILITY"))
		{
			key = "VABILITY";
		}
		else if (value.startsWith("CLASSSKILLS"))
		{
			key = "CLASSSKILLS";
		}
		else if (value.startsWith("WEAPONBONUS"))
		{
			key = "WEAPONBONUS";
			Logging.errorPrint("ADD:LIST has been deprecated, please use a "
				+ "combination of CHOOSE:WEAPONPROF and BONUS:WEAPONPROF");
		}
		else if (value.startsWith("EQUIP"))
		{
			key = "EQUIP";
		}
		else if (value.startsWith("LIST"))
		{
			key = "LIST";
			Logging.errorPrint("ADD:LIST has been deprecated");
		}
		else if (value.startsWith("Language"))
		{
			Logging.deprecationPrint("Use of lower-case Language "
				+ "in ADD is deprecated. Use upper-case LANGUAGE");
			key = "LANGUAGE";
		}
		else if (value.startsWith("LANGUAGE"))
		{
			key = "LANGUAGE";
		}
		else if (value.startsWith("SKILL"))
		{
			key = "SKILL";
		}
		else if (value.startsWith("SPELLCASTER"))
		{
			key = "SPELLCASTER";
		}
		else if (value.startsWith("SPELLLEVEL"))
		{
			key = "SPELLLEVEL";
		}
		else if (value.startsWith("SA"))
		{
			key = "SA";
		}
		else
		{
			Logging
				.deprecationPrint("Invalid ADD: Token encountered or lack of a SUBTOKEN for ADD:SA is deprecated.");
			Logging.deprecationPrint("Please use ADD:SA|name|[count|]X,X");
			Logging.deprecationPrint("  Offending Token is: ADD:" + value);
			key = "SA";
		}
		String contents;
		int keyLength = key.length();
		if (key.equals("SA"))
		{
			if (value.indexOf(Constants.PIPE) == -1)
			{
				obj.addAddList(anInt, value);
				return true;
			}
			contents = value;
		}
		else
		{
			if (key.equals("FEAT") && value.equals("FEAT"))
			{
				Logging.deprecationPrint("ADD:FEAT "
					+ "should not be used with no parameters");
				Logging.deprecationPrint("  This usage is deprecated");
				Logging
					.deprecationPrint("  Please use BONUS:FEAT|POOL|1 instead");
				return obj.addBonusList("FEAT|POOL|1");
			}
			contents = value.substring(keyLength + 1);
			if (value.charAt(keyLength) == '(')
			{
				Logging
					.deprecationPrint("ADD: syntax with parenthesis is deprecated.");
				Logging.deprecationPrint("Please use ADD:" + key + "|...");
				obj.addAddList(anInt, value);
				return true;
			}
			else if (key.equals("SPELLLEVEL"))
			{
				if (contents.charAt(keyLength) == ':')
				{
					Logging.deprecationPrint("Invalid ADD:SPELLLEVEL Syntax: "
						+ value);
					Logging.deprecationPrint("Please use ADD:SPELLLEVEL|...");
					obj.addAddList(anInt, value);
					return true;
				}
			}
		}
		if (value.charAt(keyLength) != '|')
		{
			Logging.errorPrint("Invalid ADD: Syntax: " + value);
			Logging.errorPrint("Please use ADD:" + key + "|...");
			return false;
		}
		// Guaranteed to be the new syntax here...
		return AddLoader.parseLine(obj, key, contents, anInt);
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException {
		return AddLoader.parseLine(context, (PObject) obj, value);
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		return AddLoader.unparse(context, obj);
	}
}
