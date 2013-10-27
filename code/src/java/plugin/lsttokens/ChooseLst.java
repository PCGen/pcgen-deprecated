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
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseActionContainer;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.EquipmentModifier;
import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseLst implements GlobalLstToken, CDOMPrimaryToken<CDOMObject>
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof EquipmentModifier)
		{
			return false;
		}
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
				if (val.startsWith("FEAT="))
				{
					key = "FEAT";
					val = val.substring(5);
				}
				else if (pipeLoc == -1)
				{
					key = val;
					val = null;
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
						Logging.addParseMessage(Logging.LST_ERROR,
								"Cannot use COUNT more than once in CHOOSE: "
										+ value);
						return false;
					}
					prefixList.add(key);
					count = key.substring(6);
					if (count == null)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"COUNT in CHOOSE must be a formula: " + value);
						return false;
					}
					Logging.deprecationPrint("Support for COUNT= in CHOOSE"
							+ "is tenuous, at best, use the SELECT: token "
							+ value);
				}
				else if (key.startsWith("NUMCHOICES="))
				{
					if (maxCount != null)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"Cannot use NUMCHOICES more than once in CHOOSE: "
										+ value);
						return false;
					}
					prefixList.add(key);
					maxCount = key.substring(11);
					if (maxCount == null || maxCount.length() == 0)
					{
						Logging.addParseMessage(Logging.LST_ERROR,
								"NUMCHOICES in CHOOSE must be a formula: "
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
			boolean parse = ChooseLoader.parseToken(obj, prefixString, key,
					val, anInt);
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
//		if (obj instanceof CDOMEqMod)
//		{
//			return false;
//		}
		String token = null;
		String rest = value;
		String count = null;
		String maxCount = null;
		int pipeLoc = value.indexOf(Constants.PIPE);
		while (pipeLoc != -1)
		{
			token = rest.substring(0, pipeLoc);
			rest = rest.substring(pipeLoc + 1);
			if (token.startsWith("NUMCHOICES="))
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
		if (val != null)
		{
			int titleLoc = val.indexOf("|TITLE=");
			String title = null;
			if (titleLoc != -1)
			{
				if (val.substring(titleLoc + 1).indexOf(Constants.PIPE) != -1)
				{
					Logging
							.addParseMessage(
									Logging.LST_ERROR,
									"CHOOSE: If TITLE= is used, must END with TITLE= . "
											+ "No additional arguments allowed after the title.  "
											+ "Offending value: " + value);
					return false;
				}
				title = val.substring(titleLoc + 7);
				val = val.substring(0, titleLoc);
			}
		}

		/*
		 * TODO Need to process the title!!!
		 */
		PrimitiveChoiceSet<?> chooser = context.getChoiceSet(obj, key, val);
		if (chooser == null)
		{
			// Yes, direct access, not through the context!!
			obj.put(StringKey.CHOOSE_BACKUP, value);
			return false;
		}
		ChooseActionContainer cac = obj.getChooseContainer();
		Formula maxFormula = maxCount == null ? FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE) : FormulaFactory
				.getFormulaFor(maxCount);
		Formula countFormula = count == null ? FormulaFactory.getFormulaFor(1)
				: FormulaFactory.getFormulaFor(count);
		ChoiceSet<?> choiceSet = new ChoiceSet(Constants.CHOOSE, chooser);
		cac.setChoiceSet(choiceSet);
		cac.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		cac.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		context.getObjectContext().give(getTokenName(), obj, cac);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ChoiceSet> changes = context.getObjectContext()
				.getGivenChanges(getTokenName(), obj, ChoiceSet.class);
		Collection<ChoiceSet> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		return new String[] { ReferenceUtilities.joinLstFormat(added,
				Constants.PIPE) };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
