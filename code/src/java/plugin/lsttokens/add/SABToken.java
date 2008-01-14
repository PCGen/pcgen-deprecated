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
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.ReferenceChoiceSet;
import pcgen.core.Constants;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.SpecialAbility;
import pcgen.persistence.AssociatedChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AddLstToken;
import pcgen.util.Logging;

public class SABToken extends AbstractToken implements AddLstToken
{

	private static final Class<SpecialAbility> SPECABILITY_CLASS =
		SpecialAbility.class;

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

	public String getTokenName()
	{
		return "SAB";
	}

	public boolean parse(LoadContext context, PObject obj, String value)
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
			items = rest.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}

		List<CDOMReference<SpecialAbility>> refs =
				new ArrayList<CDOMReference<SpecialAbility>>();
		StringTokenizer tok = new StringTokenizer(items, Constants.COMMA);
		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			context.ref.constructIfNecessary(SPECABILITY_CLASS, token);
			refs.add(context.ref.getCDOMReference(SPECABILITY_CLASS, token));
		}

		ChooseActionContainer container = new ChooseActionContainer("ADD");
		container.addActor(new GrantActor<PCTemplate>());
		context.getGraphContext().grant(getTokenName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
			.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE));
		ReferenceChoiceSet<SpecialAbility> rcs =
				new ReferenceChoiceSet<SpecialAbility>(refs);
		ChoiceSet<SpecialAbility> cs = new ChoiceSet<SpecialAbility>(name, rcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
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
			if (SPECABILITY_CLASS.equals(cs.getChoiceClass()))
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
}
