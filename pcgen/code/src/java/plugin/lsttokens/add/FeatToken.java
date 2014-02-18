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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.utils.ParsingSeparator;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

public class FeatToken extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<CategorizedAbilitySelection>
{

	private static final Class<CategorizedAbilitySelection> CAT_ABILITY_SELECTION_CLASS =
			CategorizedAbilitySelection.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getParentToken()
	{
		return "ADD";
	}

	private String getFullName()
	{
		return getParentToken() + Constants.COLON + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "FEAT";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Nature.NORMAL;

		ParsingSeparator sep = new ParsingSeparator(value, '|');
		String activeValue = sep.next();
		Formula count;
		if (!sep.hasNext())
		{
			count = FormulaFactory.ONE;
		}
		else
		{
			count = FormulaFactory.getFormulaFor(activeValue);
			if (!count.isValid())
			{
				return new ParseResult.Fail("Count in " + getTokenName()
						+ " was not valid: " + count.toString(), context);
			}
			if (count.isStatic() && count.resolveStatic().doubleValue() <= 0)
			{
				return new ParseResult.Fail("Count in " + getFullName()
								+ " must be > 0", context);
			}
			activeValue = sep.next();
		}
		if (sep.hasNext())
		{
			return new ParseResult.Fail(getFullName()
					+ " had too many pipe separated items: " + value, context);
		}
		ParseResult pr = checkSeparatorsAndNonEmpty(',', activeValue);
		if (!pr.passed())
		{
			return pr;
		}

		List<CDOMReference<Ability>> refs = new ArrayList<CDOMReference<Ability>>();
		ParsingSeparator tok = new ParsingSeparator(activeValue, ',');
		boolean allowStack = false;
		int dupChoices = 0;

		ReferenceManufacturer<Ability> rm = context.ref.getManufacturer(
				ABILITY_CLASS, AbilityCategory.FEAT);

		while (tok.hasNext())
		{
			CDOMReference<Ability> ab;
			String token = tok.next();
			if ("STACKS".equals(token))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(getFullName()
							+ " found second stacking specification in value: "
							+ value, context);
				}
				allowStack = true;
				continue;
			}
			else if (token.startsWith("STACKS="))
			{
				if (allowStack)
				{
					return new ParseResult.Fail(getFullName()
							+ " found second stacking specification in value: "
							+ value, context);
				}
				allowStack = true;
				try
				{
					dupChoices = Integer.parseInt(token.substring(7));
				}
				catch (NumberFormatException nfe)
				{
					return new ParseResult.Fail("Invalid Stack number in "
							+ getFullName() + ": " + value, context);
				}
				if (dupChoices <= 0)
				{
					return new ParseResult.Fail("Invalid (less than 1) Stack number in "
							+ getFullName() + ": " + value, context);
				}
				continue;
			}
			else if (Constants.LST_ALL.equals(token))
			{
				ab = rm.getAllReference();
			}
			else
			{
				ab = TokenUtilities.getTypeOrPrimitive(rm, token);
			}
			if (ab == null)
			{
				return new ParseResult.Fail("  Error was encountered while parsing "
						+ getTokenName() + ": " + value
						+ " had an invalid reference: " + token, context);
			}
			refs.add(ab);
		}

		if (refs.isEmpty())
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains no ability reference: " + value, context);
		}

		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(category, refs, nature);
		if (!rcs.getGroupingState().isValid())
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value, context);
		}
		ChoiceSet<CategorizedAbilitySelection> cs = new ChoiceSet<CategorizedAbilitySelection>(
				getTokenName(), rcs);
		cs.setTitle("Feat Choice");
		PersistentTransitionChoice<CategorizedAbilitySelection> tc = new ConcretePersistentTransitionChoice<CategorizedAbilitySelection>(
				cs, count);
		context.getObjectContext().addToList(obj, ListKey.ADD, tc);
		tc.allowStack(allowStack);
		if (dupChoices != 0)
		{
			tc.setStackLimit(dupChoices);
		}
		tc.setChoiceActor(this);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<PersistentTransitionChoice<?>> grantChanges = context
				.getObjectContext().getListChanges(obj, ListKey.ADD);
		Collection<PersistentTransitionChoice<?>> addedItems = grantChanges
				.getAdded();
		if (addedItems == null || addedItems.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		List<String> addStrings = new ArrayList<String>();
		for (TransitionChoice<?> container : addedItems)
		{
			SelectableSet<?> cs = container.getChoices();
			if (getTokenName().equals(cs.getName())
					&& CAT_ABILITY_SELECTION_CLASS.equals(cs.getChoiceClass()))
			{
				Formula f = container.getCount();
				if (f == null)
				{
					context.addWriteMessage("Unable to find " + getFullName()
							+ " Count");
					return null;
				}
				if (f.isStatic() && f.resolveStatic().doubleValue() <= 0)
				{
					context.addWriteMessage("Count in " + getFullName()
							+ " must be > 0");
					return null;
				}
				if (!cs.getGroupingState().isValid())
				{
					context.addWriteMessage("Non-sensical " + getFullName()
							+ ": Contains ANY and a specific reference: "
							+ cs.getLSTformat());
					return null;
				}
				StringBuilder sb = new StringBuilder();
				if (!FormulaFactory.ONE.equals(f))
				{
					sb.append(f).append(Constants.PIPE);
				}
				if (container.allowsStacking())
				{
					sb.append("STACKS");
					Integer stackLimit = container.getStackLimit();
					if (stackLimit != null)
					{
						if (stackLimit.intValue() <= 0)
						{
							context.addWriteMessage("Stack Limit in "
								+ getFullName() + " must be > 0");
							return null;
						}
						sb.append(Constants.EQUALS);
						sb.append(stackLimit.intValue());
					}
					sb.append(Constants.COMMA);
				}
				sb.append(cs.getLSTformat());
				addStrings.add(sb.toString());
			}
		}
		return addStrings.toArray(new String[addStrings.size()]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(CDOMObject owner, CategorizedAbilitySelection choice,
			PlayerCharacter pc)
	{
		Ability ability = choice.getAbility();
		double cost = ability.getSafe(ObjectKey.SELECTION_COST).doubleValue();
		if (cost > 0.0001)
		{
			pc.adjustFeats(cost);
		}
		AbilityUtilities.modAbility(pc, ability, choice.getSelection(),
			AbilityCategory.FEAT);
	}

	@Override
	public boolean allow(CategorizedAbilitySelection choice, PlayerCharacter pc,
			boolean allowStack)
	{
		Ability ability = choice.getAbility();
		if (!ability.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT))
		{
			return false;
		}
		if (!pc.canSelectAbility(ability, false))
		{
			return false;
		}
		String selection = choice.getSelection();
		// Avoid any already selected
		return !AbilityUtilities.alreadySelected(pc, ability, selection, allowStack);
	}

	@Override
	public CategorizedAbilitySelection decodeChoice(LoadContext context, String s)
	{
		return CategorizedAbilitySelection.getAbilitySelectionFromPersistentFormat(s);
	}

	@Override
	public String encodeChoice(CategorizedAbilitySelection choice)
	{
		return choice.getPersistentFormat();
	}

	@Override
	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		CategorizedAbilitySelection choice)
	{
		// String featName = choice.getAbilityKey();
		// Ability aFeat = pc.getAbilityKeyed(AbilityCategory.FEAT,
		// Ability.Nature.NORMAL, featName);
		// pc.addAssoc(owner, AssociationListKey.ADDED_ABILITY, aFeat);
	}

	@Override
	public void removeChoice(PlayerCharacter pc, CDOMObject owner,
		CategorizedAbilitySelection choice)
	{
		if (!pc.isImporting())
		{
			pc.getSpellList();
		}
		
		// See if our choice is not auto or virtual
		Ability anAbility = pc.getMatchingAbility(AbilityCategory.FEAT, choice
				.getAbility(), Nature.NORMAL);
		
		if (anAbility != null)
		{
			if (anAbility.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				ChoiceManagerList cm = ChooserUtilities.getChoiceManager(anAbility, pc);
				remove(cm, pc, anAbility, choice.getSelection());
			}
			pc.removeRealAbility(AbilityCategory.FEAT, anAbility);
			CDOMObjectUtilities.removeAdds(anAbility, pc);
			CDOMObjectUtilities.restoreRemovals(anAbility, pc);
			pc.adjustMoveRates();
		}
	}

	private static <T> void remove(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		CDOMObject obj, String choice)
	{
		T sel = aMan.decodeChoice(choice);
		aMan.removeChoice(pc, obj, sel);
	}
}
