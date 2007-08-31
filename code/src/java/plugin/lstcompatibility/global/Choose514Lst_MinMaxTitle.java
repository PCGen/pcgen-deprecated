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
 * Current Ver: $Revision: 3182 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2007-06-17 12:53:46 -0400 (Sun, 17 Jun 2007) $
 */
package plugin.lstcompatibility.global;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseCDOMLstToken;
import pcgen.persistence.lst.GlobalLstCompatibilityToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;

public class Choose514Lst_MinMaxTitle implements GlobalLstCompatibilityToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			// Has to be at least two items for MinMaxTitle
			Logging.addParseMessage(Logging.LST_ERROR,
				"CHOOSE min/max/title must have a PIPE");
			return false;
		}
		ChooseCDOMLstToken cTok =
				TokenStore.inst().getToken(ChooseCDOMLstToken.class, "NUMBER");
		if (cTok == null)
		{
			// Depends on NUMBER token being valid...
			Logging.addParseMessage(Logging.LST_ERROR,
				"CHOOSE min/max/title must have NUMBER token to delegate to");
			return false;
		}
		String token = value.substring(0, pipeLoc);
		String rest = value.substring(pipeLoc + 1);
		String count = null;
		String maxCount = null;
		while (pipeLoc != -1)
		{
			if (token.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot use COUNT more than once in CHOOSE: " + value);
					return false;
				}
				count = token.substring(6);
				if (count == null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"COUNT in CHOOSE must be a formula: " + value);
					return false;
				}
			}
			else if (token.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot use NUMCHOICES more than once in CHOOSE: "
							+ value);
					return false;
				}
				maxCount = token.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"NUMCHOICES in CHOOSE must be a formula: " + value);
					return false;
				}
			}
			else
			{
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE);
			if (pipeLoc == -1)
			{
				Logging.addParseMessage(Logging.LST_ERROR,
					"CHOOSE min/max/title must contain both MIN= and MAX=: "
						+ value);
				return false;
			}
			else
			{
				token = rest.substring(0, pipeLoc);
				rest = rest.substring(pipeLoc + 1);
			}
		}
		String min = null;
		String max = null;
		String title = null;
		while (true)
		{
			if (token.startsWith("MIN="))
			{
				if (min != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot use MIN more than once in CHOOSE: " + value);
					return false;
				}
				min = token.substring(4);
				if (min == null || min.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"MIN in CHOOSE must be a number: " + value);
					return false;
				}
			}
			else if (token.startsWith("MAX="))
			{
				if (max != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot use MAX more than once in CHOOSE: " + value);
					return false;
				}
				max = token.substring(4);
				if (max == null || max.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"MAX in CHOOSE must be a number: " + value);
					return false;
				}
			}
			else if (token.startsWith("TITLE="))
			{
				if (title != null)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"Cannot use TITLE more than once in CHOOSE: " + value);
					return false;
				}
				title = token.substring(6);
				if (title == null || title.length() == 0)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
						"TITLE in CHOOSE must be a number: " + value);
					return false;
				}
			}
			else
			{
				break;
			}
			if (pipeLoc == -1)
			{
				// Done
				break;
			}
			pipeLoc = rest.indexOf(Constants.PIPE);
			if (pipeLoc == -1)
			{
				token = rest;
				rest = "";
			}
			else
			{
				token = rest.substring(0, pipeLoc);
				rest = rest.substring(pipeLoc + 1);
			}
		}
		if (rest != null && rest.length() != 0)
		{
			// Parse failed (unexpected stuff)
			Logging.addParseMessage(Logging.LST_ERROR,
				"CHOOSE min/max/title found unexpected stuff: " + rest);
			return false;
		}
		if (min == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"CHOOSE min/max/title had no MIN=");
			return false;
		}
		if (max == null)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
				"CHOOSE min/max/title had no MAX=");
			return false;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MIN=").append(min).append("|MAX=").append(max);
		if (title != null)
		{
			sb.append("|TITLE=").append(title);
		}
		PrimitiveChoiceSet<?> chooser = cTok.parse(context, obj, sb.toString());
		if (chooser == null)
		{
			return false;
		}

		Formula maxFormula =
				maxCount == null ? FormulaFactory
					.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
					.getFormulaFor(maxCount);
		Formula countFormula =
				count == null ? FormulaFactory.getFormulaFor(1)
					: FormulaFactory.getFormulaFor(count);
		ChoiceSet<?> cs = new ChoiceSet("Choose", chooser);
		ChooseActionContainer container = obj.getChooseContainer();
		container.setChoiceSet(cs);
		container.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		return true;
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilitySubLevel()
	{
		return 14;
	}

	public int compatibilityPriority()
	{
		return 0;
	}
}
