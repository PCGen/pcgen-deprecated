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
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.ChooseLstToken;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.LstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!value.startsWith("CHOOSE:LANGAUTO"))
		{
			String key;
			String val = value;
			int activeLoc = 0;
			String count = null;
			String maxCount = null;
			List<String> prefixList = new ArrayList<String>(2);
			while (true)
			{
				int pipeLoc = val.indexOf(Constants.PIPE, activeLoc);
				if (pipeLoc == -1)
				{
					if (val.startsWith("FEAT="))
					{
						key = "FEAT";
						val = val.substring(5);
					}
					else
					{
						key = val;
						val = null;
					}
				}
				else
				{
					key = val.substring(activeLoc, pipeLoc);
					val = val.substring(pipeLoc + 1);
				}
				if (key.startsWith("COUNT="))
				{
					if (count != null)
					{
						Logging
							.errorPrint("Cannot use COUNT more than once in CHOOSE: "
								+ value);
						return false;
					}
					prefixList.add(key);
					count = key.substring(6);
					if (count == null)
					{
						Logging
							.errorPrint("COUNT in CHOOSE must be a formula: "
								+ value);
						return false;
					}
				}
				else if (key.startsWith("NUMCHOICES="))
				{
					if (maxCount != null)
					{
						Logging
							.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
								+ value);
						return false;
					}
					prefixList.add(key);
					maxCount = key.substring(11);
					if (maxCount == null || maxCount.length() == 0)
					{
						Logging
							.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
								+ value);
						return false;
					}
				}
				else
				{
					break;
				}
			}
			String prefixString = CoreUtility.join(prefixList, "|");
			boolean parse =
					ChooseLoader.parseToken(obj, prefixString, key, val, anInt);
			if (!parse)
			{
				// 514 deprecation changes
				// Logging.errorPrint("CHOOSE: syntax you are using is
				// deprecated: "
				// + value);
				// Logging.errorPrint(" Please use CHOOSE:SUBKEY|choices");
				// Logging.errorPrint(" ... see the PCGen docs");
				obj.setChoiceString(value);
			}
			return true;
		}
		return false;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		String token = null;
		String rest = value;
		int activeLoc = 0;
		String count = null;
		String maxCount = null;
		int pipeLoc = value.indexOf(Constants.PIPE, activeLoc);
		while (pipeLoc != -1)
		{
			token = rest.substring(activeLoc, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging
						.errorPrint("Cannot use COUNT more than once in CHOOSE: "
							+ value);
					return false;
				}
				count = token.substring(6);
				if (count == null)
				{
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
						+ value);
					return false;
				}
			}
			else if (token.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging
						.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
							+ value);
					return false;
				}
				maxCount = token.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging
						.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
							+ value);
					return false;
				}
			}
			else
			{
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE, activeLoc);
		}
		String key;
		String val;
		if (rest.startsWith("FEAT="))
		{
			key = "FEAT";
			val = rest.substring(5);
		}
		else if (pipeLoc == -1)

		{
			key = rest;
			val = null;
		}
		else
		{
			key = token;
			val = rest;
		}
		ChoiceSet<?> chooser = ChooseLoader.parseLine(context, obj, key, val);
		if (chooser == null)
		{
			return false;
		}
		if (maxCount == null)
		{
			if (chooser.getMaxSelections() == null)
			{
				chooser.setMaxSelections(FormulaFactory
					.getFormulaFor(Integer.MAX_VALUE));
			}
		}
		else
		{
			chooser.setMaxSelections(FormulaFactory.getFormulaFor(maxCount));
		}
		if (count == null)
		{
			if (chooser.getCount() == null)
			{
				chooser.setCount(FormulaFactory.getFormulaFor(1));
			}
		}
		else
		{
			chooser.setCount(FormulaFactory.getFormulaFor(count));
		}
		chooser.setChooseType(key);
		context.obj.put(obj, ObjectKey.CHOICE, chooser);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		ChoiceSet<?> choice = context.obj.getObject(obj, ObjectKey.CHOICE);
		if (choice == null)
		{
			// didn't have a chooser
			return null;
		}
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ChooseLstToken.class);
		String chooseType = choice.getChooseType();
		ChooseLstToken token = (ChooseLstToken) tokenMap.get(chooseType);
		if (token != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(choice.getChooseType());
			String unparsed = token.unparse(context, choice);
			if (unparsed != null)
			{
				sb.append('|').append(unparsed);
			}
			return new String[]{sb.toString()};
		}
		else
		{
			context.addWriteMessage("Illegal CHOOSE Type '"
				+ choice.getChooseType() + "'");
			return null;
		}
	}
}
