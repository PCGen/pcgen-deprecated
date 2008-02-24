/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class LanguageToken extends AbstractToken implements AddLstToken, CDOMSecondaryToken<CDOMObject>
{
	private static final Class<CDOMLanguage> LANGUAGE_CLASS = CDOMLanguage.class;

	public String getParentToken()
	{
		return "ADD";
	}

	public boolean parse(PObject target, String value, int level)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		String countString;
		String items;
		if (pipeLoc == -1)
		{
			countString = "1";
			items = value;
		}
		else
		{
			if (pipeLoc != value.lastIndexOf(Constants.PIPE))
			{
				Logging.errorPrint("Syntax of ADD:" + getTokenName()
					+ " only allows one | : " + value);
				return false;
			}
			countString = value.substring(0, pipeLoc);
			items = value.substring(pipeLoc + 1);
		}
		target.addAddList(level, getTokenName() + "(" + items + ")"
			+ countString);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "LANGUAGE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		int count;
		String items;
		if (pipeLoc == -1)
		{
			count = 1;
			items = value;
		}
		else
		{
			String countString = value.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in ADD:" + getTokenName()
						+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in ADD:" + getTokenName()
					+ ": " + countString);
				return false;
			}
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);

		boolean foundAny = false;
		boolean foundOther = false;

		List<CDOMReference<CDOMLanguage>> refs =
				new ArrayList<CDOMReference<CDOMLanguage>>();
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<CDOMLanguage> ref;
			if (Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
			}
			else
			{
				foundOther = true;
				ref =
						TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, token);
				if (ref == null)
				{
					Logging
						.errorPrint("  Error was encountered while parsing ADD:"
							+ getTokenName()
							+ ": "
							+ token
							+ " is not a valid reference: " + value);
					return false;
				}
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ChooseActionContainer container = new ChooseActionContainer("ADD");
		container.addActor(new GrantActor<CDOMLanguage>());
		context.getGraphContext().grant(getTokenName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		ReferenceChoiceSet<CDOMLanguage> rcs =
				new ReferenceChoiceSet<CDOMLanguage>(refs);
		ChoiceSet<CDOMLanguage> cs = new ChoiceSet<CDOMLanguage>("ADD", rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<ChooseActionContainer> grantChanges =
				context.getGraphContext().getChangesFromToken(getTokenName(),
					obj, ChooseActionContainer.class);
		if (grantChanges == null)
		{
			return null;
		}
		if (!grantChanges.hasAddedItems())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : grantChanges.getAdded())
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (!"ADD".equals(container.getName()))
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
					+ container.getName());
				continue;
			}
			ChoiceSet<?> cs = container.getChoiceSet();
			if (LANGUAGE_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f =
						container.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getTokenName()
						+ " Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
