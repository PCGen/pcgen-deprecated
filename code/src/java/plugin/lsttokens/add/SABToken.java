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
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.actor.GrantActor;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.choiceset.CollectionChoiceSet;
import pcgen.cdom.content.CDOMSpecialAbility;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class SABToken extends AbstractToken implements AddLstToken,
		CDOMSecondaryToken<CDOMObject>
{

	private static final Class<CDOMSpecialAbility> SPECABILITY_CLASS = CDOMSpecialAbility.class;

	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	public boolean parse(PObject target, String value, int level)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("Lack of a SUBTOKEN for ADD:SAB "
					+ "is prohibited.");
			Logging.errorPrint("Please use ADD:SAB|name|[count|]X,X");
			return false;
		}
		String subToken = value.substring(0, pipeLoc);
		String countString;
		String items;
		int lastPipeLoc = value.lastIndexOf(Constants.PIPE);
		if (lastPipeLoc == pipeLoc)
		{
			items = value;
			countString = "1";
		}
		else
		{
			items = value.substring(pipeLoc + 1, lastPipeLoc);
			countString = value.substring(lastPipeLoc + 1);
		}
		target.addAddList(level, subToken + "(" + items + ")" + countString);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "SAB";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.errorPrint("Lack of a SUBTOKEN for ADD:SAB "
					+ "is prohibited.");
			Logging.errorPrint("Please use ADD:SAB|name|[count|]X,X");
			return false;
		}
		String name = value.substring(0, pipeLoc);
		if (name.length() == 0)
		{
			Logging.errorPrint("Empty name for ADD:SAB " + "is prohibited.");
			Logging.errorPrint("Please use ADD:SAB|name|[count|]X,X");
			return false;
		}
		String rest = value.substring(pipeLoc + 1);
		int count;
		String items;
		pipeLoc = rest.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			count = 1;
			items = rest;
		}
		else
		{
			String countString = rest.substring(0, pipeLoc);
			try
			{
				count = Integer.parseInt(countString);
				if (count < 1)
				{
					Logging.errorPrint("Count in " + getFullName()
							+ " must be > 0");
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Invalid Count in " + getFullName() + ": "
						+ countString);
				return false;
			}
			items = rest.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMSpecialAbility> refs = new ArrayList<CDOMSpecialAbility>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMSpecialAbility sab = new CDOMSpecialAbility(token);
			sab.setName(token);
			refs.add(sab);
		}

		ChooseActionContainer container = new ChooseActionContainer("ADD");
		container.addActor(new GrantActor<CDOMSpecialAbility>());
		context.getObjectContext().give(getFullName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE));
		CollectionChoiceSet<CDOMSpecialAbility> rcs = new CollectionChoiceSet<CDOMSpecialAbility>(
				refs);
		ChoiceSet<CDOMSpecialAbility> cs = new ChoiceSet<CDOMSpecialAbility>(
				name, rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ChooseActionContainer> grantChanges = context
				.getObjectContext().getGivenChanges(getFullName(), obj,
						ChooseActionContainer.class);
		Collection<ChooseActionContainer> addedItems = grantChanges.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (LSTWriteable lstw : addedItems)
		{
			ChooseActionContainer container = (ChooseActionContainer) lstw;
			if (!"ADD".equals(container.getName()))
			{
				context.addWriteMessage("Unexpected CHOOSE container found: "
						+ container.getName());
				continue;
			}
			ChoiceSet<?> cs = container.getChoiceSet();
			if (SPECABILITY_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container
						.getAssociation(AssociationKey.CHOICE_COUNT);
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				String fString = f.toString();
				StringBuilder sb = new StringBuilder();
				sb.append(cs.getName()).append(Constants.PIPE);
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
