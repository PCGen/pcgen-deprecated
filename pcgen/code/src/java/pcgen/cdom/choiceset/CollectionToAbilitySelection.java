/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.util.Logging;

public class CollectionToAbilitySelection implements
		PrimitiveChoiceSet<AbilitySelection>
{
	private final PrimitiveCollection<Ability> collection;
	
	private final Category<Ability> category;

	public CollectionToAbilitySelection(Category<Ability> cat, PrimitiveCollection<Ability> coll)
	{
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Category must not be null");
		}
		if (coll == null)
		{
			throw new IllegalArgumentException(
					"PrimitiveCollection must not be null");
		}
		category = cat;
		collection = coll;
	}

	@Override
	public Class<? super AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}

	@Override
	public GroupingState getGroupingState()
	{
		return collection.getGroupingState();
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return collection.getLSTformat(useAny);
	}

	@Override
	public Collection<AbilitySelection> getSet(PlayerCharacter pc)
	{
		return collection.getCollection(pc, new ExpandingConverter(pc));
	}

	public Category<Ability> getCategory()
	{
		return category;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * CollectionToAbilitySelection
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return collection.hashCode();
	}

	/**
	 * Returns true if this CollectionToAbilitySelection is equal to the given
	 * Object. Equality is defined as being another CollectionToAbilitySelection
	 * object with equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CollectionToAbilitySelection)
				&& ((CollectionToAbilitySelection) obj).collection
						.equals(collection);
	}

	public static class ExpandingConverter implements
			Converter<Ability, AbilitySelection>
	{

		private final PlayerCharacter character;

		public ExpandingConverter(PlayerCharacter pc)
		{
			character = pc;
		}

		private static Stack<Ability> stack = new Stack<Ability>();

		@Override
		public Collection<AbilitySelection> convert(CDOMReference<Ability> ref)
		{
			Set<AbilitySelection> returnSet = new HashSet<AbilitySelection>();
			for (Ability a : ref.getContainedObjects())
			{
				if (stack.contains(a))
				{
					Stack<Ability> current = new Stack<Ability>();
					current.addAll(stack);
					Logging.errorPrint("Error: Circular Expansion Found: "
						+ reportCircularExpansion(current));
					continue;
				}
				try
				{
					stack.push(a);
					processAbility(ref, returnSet, a);
				}
				finally
				{
					stack.pop();
				}
			}
			return returnSet;
		}

		private String reportCircularExpansion(Stack<Ability> s)
		{
			StringBuilder sb = new StringBuilder(2000);
			processCircularExpansion(sb, s);
			sb.append("    which is a circular reference");
			return sb.toString();
		}

		private void processCircularExpansion(StringBuilder sb, Stack<Ability> s)
		{
			Ability a = s.pop();
			if (!s.isEmpty())
			{
				processCircularExpansion(sb, s);
				sb.append("     which includes");
			}
			sb.append(a.getCDOMCategory()).append(' ').append(a.getKeyName());
			sb.append(" selects items: ");
			sb.append(a.get(ObjectKey.CHOOSE_INFO).getLSTformat());
			sb.append('\n');
		}

		private void processAbility(CDOMReference<Ability> ref,
				Set<AbilitySelection> returnSet, Ability a)
		{
			if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				returnSet.addAll(addMultiplySelectableAbility(character, a, ref
						.getChoice()));
			}
			else
			{
				returnSet.add(new AbilitySelection(a, null));
			}
		}

		@Override
		public Collection<AbilitySelection> convert(CDOMReference<Ability> ref,
				PrimitiveFilter<Ability> lim)
		{
			Set<AbilitySelection> returnSet = new HashSet<AbilitySelection>();
			for (Ability a : ref.getContainedObjects())
			{
				if (lim.allow(character, a))
				{
					if (stack.contains(a))
					{
						Stack<Ability> current = new Stack<Ability>();
						current.addAll(stack);
						Logging.errorPrint("Error: Circular Expansion Found: "
							+ reportCircularExpansion(current));
						continue;
					}
					try
					{
						stack.push(a);
						processAbility(ref, returnSet, a);
					}
					finally
					{
						stack.pop();
					}
				}
			}
			return returnSet;
		}

		private Collection<AbilitySelection> addMultiplySelectableAbility(
				final PlayerCharacter aPC, Ability ability, String subName)
		{
			// If already have taken the feat, use it so we can remove
			// any choices already selected
			final Ability pcFeat = aPC.getFeatNamed(ability.getKeyName());

			Ability pcability = ability;
			if (pcFeat != null)
			{
				pcability = pcFeat;
			}

			boolean isPattern = false;
			String nameRoot = null;
			if (subName != null)
			{
				final int percIdx = subName.indexOf('%');

				if (percIdx > -1)
				{
					isPattern = true;
					nameRoot = subName.substring(0, percIdx);
				}
				else if (subName.length() != 0)
				{
					nameRoot = subName;
				}
			}

			final List<String> availableList = new ArrayList<String>();
			final List<?> tempAvailList = new ArrayList<Object>();
			final List<?> tempSelList = new ArrayList<Object>();
			ChooserUtilities.modChoices(pcability, tempAvailList, tempSelList,
					false, aPC, true, AbilityCategory.FEAT);
			// Mod choices may have sent us back weaponprofs, abilities or
			// strings,
			// so we have to do a conversion here
			for (Object o : tempAvailList)
			{
				String choice = o.toString();
				if ("NOCHOICE".equals(choice))
				{
					availableList.add("");
				}
				else
				{
					availableList.add(choice);
				}
			}

			// Remove any that don't match

			if (nameRoot != null && nameRoot.length() != 0)
			{
				for (int n = availableList.size() - 1; n >= 0; --n)
				{
					final String aString = availableList.get(n);

					if (!aString.startsWith(nameRoot))
					{
						availableList.remove(n);
					}
				}

				// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving))) If you
				// have no ranks in Craft (Basketweaving), the available list
				// will
				// be empty
				//
				// Make sure that the specified feat is available, even though
				// it
				// does not meet the prerequisite

				if (isPattern && !availableList.isEmpty())
				{
					availableList.add(nameRoot);
				}
			}

			List<AbilitySelection> returnList = new ArrayList<AbilitySelection>(
					availableList.size());
			for (String s : availableList)
			{
				returnList.add(new AbilitySelection(pcability, s));
			}
			return returnList;
		}

	}
}
