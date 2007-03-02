/*
 * SpellLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision: 1600 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2006-11-05 19:02:15 -0500 (Sun, 05 Nov 2006) $
 *
 */
package pcgen.persistence.lst;

import java.util.Map;

import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

public final class ChooseLoader {

	private ChooseLoader() {
		super();
	}

	public static boolean parseLine(LoadContext context, CDOMObject obj,
			String value) throws PersistenceLayerException {

		int pipeLoc = value.indexOf(Constants.PIPE);
		int equalLoc = value.indexOf(Constants.EQUALS);
		if (pipeLoc != -1 && (equalLoc == -1 || pipeLoc < equalLoc)) {
			String key = value.substring(0, pipeLoc);
			String newValue = value.substring(pipeLoc + 1);
			return processChoose(context, obj, value, key, newValue);
		}

		if (equalLoc != -1) {
			String key = value.substring(0, equalLoc);
			String newValue = value.substring(equalLoc + 1);
			return processChoose(context, obj, value, key, newValue);
		}

		int openParenLoc = value.indexOf(Constants.OPEN_PAREN);
		if (openParenLoc != -1) {
			String key = value.substring(0, openParenLoc);
			int closeParenLoc = value.lastIndexOf(Constants.CLOSE_PAREN);
			if (closeParenLoc == -1) {
				Logging.errorPrint("Close Paren Error: " + value);
				return false;
			} else if (closeParenLoc != value.length() - 1) {
				Logging.errorPrint("Close Paren not at end: " + value);
				return false;
			}
			String newValue = value.substring(openParenLoc + 1, closeParenLoc);
			return processChoose(context, obj, value, key, newValue);
		}

		return processChoose(context, obj, value, value, null);
	}

	private static boolean processChoose(LoadContext context, CDOMObject obj,
			String value, String key, String newValue)
			throws PersistenceLayerException {
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				ChooseLstToken.class);

		ChooseLstToken token = (ChooseLstToken) tokenMap.get(key);

		if (token != null) {
			LstUtils.deprecationCheck(token, obj, value);
			if (!token.parse(context, obj, newValue)) {
				Logging.errorPrint("Error parsing CHOOSE in "
						+ obj.getDisplayName() + ": \"" + value + "\"");
			}
		} else {
			//FIXME Consume for now - too frequent!
			//Logging.errorPrint("Illegal CHOOSE info '" + value + "'");
		}
		return true;
	}
}
