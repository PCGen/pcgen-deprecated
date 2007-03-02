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

import java.util.StringTokenizer;

import pcgen.base.util.Logging;
import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class RemoveLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "REMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
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
	
	public boolean parse(LoadContext context, CDOMObject obj, String value) {
		if (!value.startsWith("FEAT(")) {
			Logging.errorPrint(getTokenName() + " only supports FEAT");
			return false;
		}
		int i = value.indexOf("(");
		int k = value.lastIndexOf(")");
		StringTokenizer tok = new StringTokenizer(value.substring(i + 1, k),
				Constants.COMMA);

		if (tok.countTokens() == 0) {
			Logging.errorPrint("No contents between parenthesis in "
					+ getTokenName() + ": " + value);
			return false;
		}

		while (tok.hasMoreTokens()) {
			String tokText = tok.nextToken();

			if (Constants.LST_DOT_CLEAR.equals(tokText)) {
				// clearChildNodesOfClass(graph, obj, LANGUAGE_CLASS);
			} else if (tokText.startsWith(Constants.LST_TYPE_OLD)) {
				// linkObjectIntoGraph(graph, obj, CDOMFactory.getTypeRef(
				// LANGUAGE_CLASS, tokText.substring(5)));
			} else if (tokText.startsWith(Constants.LST_CLASS_DOT)) {
				PrereqObject cl = context.ref.getCDOMReference(PCClass.class, tokText
				.substring(6));
				CDOMGroupRef<Ability> group = context.groupChildNodesOfClass(cl,
						Ability.class);
				// link the group somehow??
			} else if (tokText.equals("CHOICE")) {
				/*
				 * FIXME This is a special case that I need to consider how to
				 * handle...
				 */
				// for (Ability aFeat : aPC.getRealFeatList()) {
				// theFeatList.add(aFeat);
				// }
			} else {
				// Ability aFeat = aPC.getFeatNamed(arg);
				// if (aFeat != null && !theFeatList.contains(aFeat))
				// theFeatList.add(aFeat);

				// Language lang = CDOMFactory.getRef(LANGUAGE_CLASS, tokText);
				// linkObjectIntoGraph(graph, obj, lang);
			}
		}
//		int remCount = theFeatList.size();
//		if (value.length() > k + 1) {
//			final String rString = value.substring(k + 1);
//			if (!rString.equalsIgnoreCase("ALL")) {
//				remCount = Integer.parseInt(rString);
//			}
//		}
		return true;
	}

	public String unparse(LoadContext context, CDOMObject obj)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
