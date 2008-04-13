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

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.AbstractCDOMClassAwareObject;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.AddLstToken;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class ClassSkillsToken extends AbstractToken implements AddLstToken,
		CDOMSecondaryToken<AbstractCDOMClassAwareObject>
{

	private static final Class<CDOMSkill> SKILL_CLASS = CDOMSkill.class;

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
		if (!target.getClass().equals(PCClass.class))
		{
			Logging
					.errorPrint("ADD:CLASSSKILLS is only valid in Class LST files");
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
		return "CLASSSKILLS";
	}

	public boolean parse(LoadContext context, AbstractCDOMClassAwareObject obj,
			String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getFullName() + " may not have empty argument");
			return false;
		}
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
			items = value.substring(pipeLoc + 1);
		}

		if (isEmpty(items) || hasIllegalSeparator(',', items))
		{
			return false;
		}
		String possibilities = StringUtil.replaceAll(items, Constants.COMMA, Constants.PIPE);
		PrimitiveChoiceSet<CDOMSkill> pcs = context.getChoiceSet(SKILL_CLASS, possibilities);
		if (pcs == null)
		{
			return false;
		}
		// ClassSkillList csl = ((ClassContext) obj).getCDOMClassSkillList();
		ChooseActionContainer container = new ChooseActionContainer("ADD");
		// container.addActor(new AllowActor<CDOMSkill>(csl));
		/*
		 * TODO Need to actually add an Actor here (See above)
		 */
		context.getGraphContext().grant(getFullName(), obj, container);
		container.setAssociation(AssociationKey.CHOICE_COUNT, FormulaFactory
				.getFormulaFor(count));
		container.setAssociation(AssociationKey.CHOICE_MAXCOUNT, FormulaFactory
				.getFormulaFor(Integer.MAX_VALUE));
		container.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
		ChoiceSet<CDOMSkill> cs = new ChoiceSet<CDOMSkill>("ADD", pcs);
		container.setChoiceSet(cs);
		return true;
	}

	public String[] unparse(LoadContext context,
			AbstractCDOMClassAwareObject obj)
	{
		AssociatedChanges<ChooseActionContainer> grantChanges = context
				.getGraphContext().getChangesFromToken(getFullName(), obj,
						ChooseActionContainer.class);
		Collection<LSTWriteable> addedItems = grantChanges.getAdded();
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
			if (SKILL_CLASS.equals(cs.getChoiceClass()))
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
				if (!"1".equals(fString))
				{
					sb.append(fString).append(Constants.PIPE);
				}
				sb.append(StringUtil.replaceAll(cs.getLSTformat(),
						Constants.PIPE, Constants.COMMA));
				addStrings.add(sb.toString());

				// assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	public Class<AbstractCDOMClassAwareObject> getTokenClass()
	{
		return AbstractCDOMClassAwareObject.class;
	}
}
