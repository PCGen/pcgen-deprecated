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
		validate(value);
		obj.addAddList(anInt, value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException {
		//FIXME This is a hack
		return true;
		//return AddLoader.parseLine(context, obj, value);
	}

	private void validate(String value) {
		if ("FEAT".equals(value)) {
			Logging.errorPrint("ADD:FEAT should not be used with no parameters");
			Logging.errorPrint("  This usage is deprecated");
			Logging.errorPrint("  Please use BONUS:FEAT|POOL|1 instead");
		} else if (value.startsWith("INIT(")) {
			Logging.errorPrint("ADD:INIT is deprecated");
			Logging.errorPrint("  Note that the code does not function - "
					+ "you are not getting what you expect!");
		} else if (value.startsWith("SPECIAL(")) {
			Logging.errorPrint("ADD:SPECIAL is deprecated");
			Logging.errorPrint("  Note that the code does not function - "
					+ "you are not getting what you expect!");
		}
		return;
		// once * ADD (Global Add) is invalid, we can do another test:
//		else if (value.startsWith(".CLEAR") || value.startsWith("CLASSSKILLS(")
//				|| value.startsWith("EQUIP(") || value.startsWith("FEAT(")
//				|| value.startsWith("LANGUAGE(") || value.startsWith("SKILL(")
//				|| value.startsWith("SPELLCASTER(")
//				|| value.startsWith("SPELLLEVEL(")
//				|| value.startsWith("VFEAT(")) {
//			// OK
//			return;
//		}
		//Logging.errorPrint(value + " is not a valid ADD");
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
