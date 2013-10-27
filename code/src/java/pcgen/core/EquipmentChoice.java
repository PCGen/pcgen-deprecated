/*
 * EquipmentChoice.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Sep 12, 2004
 *
 * $Id$
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * {<code>EquipmentChoice</code>} holds the details of a choice or
 * choices required for an Equipment. It is a java bean with a
 * couple of helper functions to support the users of the bean.
 * This supports either the user manually choosing which option
 * they want, or the generator creating one object for each
 * combination of choices.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

final class EquipmentChoice
{
	private boolean allowDuplicates = false;
	private boolean noSign = false;
	private boolean bAdd = false;
	private boolean skipZero = false;

	private int minValue = 0;
	private int maxValue = 0;
	private int incValue = 1;
	private int maxSelect = 0;
	private int pool = 0;
	private String title = null;

	private List<String> availableList = new ArrayList<String>();

	/**
	 * Default constructor for the equipment choice class.
	 * @param bAdd
	 * @param pool
	 */
	public EquipmentChoice(final boolean bAdd, final int pool)
	{
		super();
		this.bAdd = bAdd;
		this.pool = pool;
	}

	/**
	 * Create an iterator over the available choices. This will either be
	 * an iterator of strings, should there be only one choice, or an array
	 * of strings where nested choices are required. The iterator will run
	 * through each possible combination in this case.
	 *
	 * @param neverEmpty True if a default record should be
	 *                    added if there are no choices.
	 * @return An iterator of choices
	 */
	EquipChoiceIterator getChoiceIterator(final boolean neverEmpty)
	{
		if (neverEmpty && availableList.isEmpty())
		{
			final List<String> temp = new ArrayList<String>();
			temp.add("");
			return new EquipChoiceIterator(temp);
		}
		final List<String> finalList;

		// Account for secondary values (sent as <primary>|<secondary>)
		if (getMinValue() < getMaxValue())
		{
			finalList = new ArrayList<String>();
			for (int i = 0; i < availableList.size(); i++)
			{
				final String choice = availableList.get(i);
				if (choice.indexOf('|') < 0)
				{
					for (int j = getMinValue(); j <= getMaxValue(); j += getIncValue())
					{
						if (!skipZero || j != 0)
						{
							finalList.add(choice + '|' + Delta.toString(j));
						}
					}
				}
				else
				{
					finalList.add(choice);
				}
			}
		}
		else
		{
			finalList = availableList;
		}
		return new EquipChoiceIterator(finalList);
	}

	/**
	 * @return Returns the pool.
	 */
	final int getPool()
	{
		return pool;
	}
	/**
	 * @param pool The pool to set.
	 */
	final void setPool(final int pool)
	{
		this.pool = pool;
	}
	/**
	 * @return Returns the bAdd.
	 */
	final boolean isBAdd()
	{
		return bAdd;
	}
	/**
	 * @param add The bAdd to set.
	 */
	final void setBAdd(final boolean add)
	{
		bAdd = add;
	}
	/**
	 * @return Returns the availableList.
	 */
	final List<String> getAvailableList()
	{
		return availableList;
	}

	/**
	 * @return Returns the allowDuplicates.
	 */
	final boolean isAllowDuplicates()
	{
		return allowDuplicates;
	}
	/**
	 * @param allowDuplicates The allowDuplicates to set.
	 */
	final void setAllowDuplicates(final boolean allowDuplicates)
	{
		this.allowDuplicates = allowDuplicates;
	}
	/**
	 * @return Returns the incValue.
	 */
	final int getIncValue()
	{
		return incValue;
	}
	/**
	 * @param incValue The incValue to set.
	 */
	final void setIncValue(final int incValue)
	{
		this.incValue = incValue;
	}
	/**
	 * @return Returns the maxSelect.
	 */
	final int getMaxSelect()
	{
		return maxSelect;
	}
	/**
	 * @param maxSelect The maxSelect to set.
	 */
	final void setMaxSelect(final int maxSelect)
	{
		this.maxSelect = maxSelect;
	}
	/**
	 * @return Returns the maxValue.
	 */
	final int getMaxValue()
	{
		return maxValue;
	}
	/**
	 * @param maxValue The maxValue to set.
	 */
	final void setMaxValue(final int maxValue)
	{
		this.maxValue = maxValue;
	}
	/**
	 * @return Returns the minValue.
	 */
	final int getMinValue()
	{
		return minValue;
	}
	/**
	 * @param minValue The minValue to set.
	 */
	final void setMinValue(final int minValue)
	{
		this.minValue = minValue;
	}
	/**
	 * @return Returns the noSign.
	 */
	final boolean isNoSign()
	{
		return noSign;
	}
	/**
	 * @param noSign The noSign to set.
	 */
	final void setNoSign(final boolean noSign)
	{
		this.noSign = noSign;
	}
	/**
	 * @return Returns the title.
	 */
	final String getTitle()
	{
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	final void setTitle(final String title)
	{
		this.title = title;
	}

	/**
	 * Add a list of all skills to the available list of the EquipmentChoice object
	 */
	public void addSkills() {
		for ( Skill skill : Globals.getSkillList() )
		{
			this.getAvailableList().add(skill.getKeyName());
		}
	}

	/**
	 * Set MinValue
	 * @param minString a string with the minimum value
	 */
	public void setMinValueFromString(String minString)
	{
		try
		{
			this.setMinValue(Delta.parseInt(minString.substring(4)));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Bad MIN= value: " + minString);
		}
	}

	/**
	 * @param maxString a string with the maximum value
	 */
	public void setMaxValueFromString(String maxString)
	{
		try
		{
			this.setMaxValue(Delta.parseInt(maxString.substring(4)));
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Bad MAX= value: " + maxString);
		}
	}

	/**
	 *
	 * @param incString a string with the increment value
	 */
	public void setIncrementValueFromString(String incString)
	{
		try
		{
			this.setIncValue(Delta.parseInt(incString.substring(10)));

			if (this.getIncValue() < 1)
			{
				this.setIncValue(1);
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Deal with Exception
		}
	}

	/**
	 * Add abilities of Category aCategory and Type typeString to the
	 * available list of the Equipment Chooser equipChoice
	 * @param typeString  the type of Ability to add to the chooser
	 * @param aCategory   the Category of Ability to add to the chooser
	 */
	public void addSelectableAbilities(
		final String          typeString,
		final String          aCategory)
	{
		for (Iterator<? extends Categorisable> e = Globals.getAbilityKeyIterator(aCategory); e.hasNext();)
		{
			final Ability anAbility = (Ability)e.next();

			boolean matchesType = (
					typeString.equalsIgnoreCase("ALL") ||
					anAbility.isType(typeString)
								  );

			if ((anAbility.getVisibility() == Visibility.YES)
					&& !this.getAvailableList().contains(anAbility.getKeyName()))
			{
				if (matchesType && (anAbility.getChoiceString().length() == 0))
				{
					this.getAvailableList().add(anAbility.getKeyName());
				}
			}
		}
	}

	/**
	 * Add Equipment of Type typeString to to the available list of
	 * the Equipment Chooser equipChoice
	 * @param typeString  the type of Equipment to add to the chooser
	 */
	public void addSelectableEquipment(
		final String          typeString)
	{
		for (Iterator<Map.Entry<String, Equipment>> i = EquipmentList.getEquipmentListIterator(); i.hasNext();)
		{
			final Map.Entry<String, Equipment> entry  = i.next();
			final Equipment aEquip = entry.getValue();

			if (
				aEquip.isType(typeString) &&
				!this.getAvailableList().contains(aEquip.getName()))
			{
				this.getAvailableList().add(aEquip.getName());
			}
		}
	}

	/**
	 * Add a list of skills of Type typeString to the available list of
	 * the EquipmentChoice object equipChoice
	 * @param typeString the type of Skill to add to the chooser
	 */
	public void addSelectableSkills(
		final String          typeString)
	{
		for ( Skill skill : Globals.getSkillList() )
		{
			if (
				(typeString.equalsIgnoreCase("ALL") ||
					skill.isType(typeString)) &&
				!this.getAvailableList().contains(skill.getKeyName()))
			{
				this.getAvailableList().add(skill.getKeyName());
			}
		}
	}

	/**
	 * @param parent The piece of Equipment that this Equipment Modifier will be added to
	 * @param choiceType the type of Skill to add to the chooser
	 */
	public void addParentsExistingEquipmentModifiersToChooser(
		final Equipment       parent,
		String                choiceType)
	{
		for ( EquipmentModifier sibling : parent.getEqModifierList(true) )
		{
			if (
				!(sibling.equals(this)) &&
				sibling.getChoiceString().startsWith(choiceType))
			{
				sibling.addAssociatedTo(this.getAvailableList());
			}
		}
	}

	/**
	 * Populate an EquipmentChoice object with choices based on kindToAdd (Skill,
	 * Feat, etc.) and filtered by filterBy.
	 * @param parent the piece of Equipment that this Equipment Modifier will
	 *        be added to
	 * @param numOfChoices the number of choices to make
	 * @param numChosen    the number of choices made up to this point
	 * @param filterBy     the type used to filter the kind of thing being
	 *                     chosen
	 * @param kindToAdd    what kind of choice are we adding?  skills,
	 *                     equipment, etc.
	 * @param category     if adding abilities, this will contain the category
	 *                     of ability to add
	 */
	public void addChoicesByType(
		final Equipment       parent,
		final int             numOfChoices,
		final int             numChosen,
		String                filterBy,
		String                kindToAdd,
		String                category)
	{
		if ((numOfChoices > 0) && (getMaxSelect() == 0))
		{
			setPool(numOfChoices - numChosen);
		}

		final String type = filterBy.substring(5);

		if (type.startsWith("LASTCHOICE"))
		{
			addParentsExistingEquipmentModifiersToChooser(
				parent,
				kindToAdd);
		}
		else if ("SKILL".equalsIgnoreCase(kindToAdd))
		{
			addSelectableSkills(type);
		}
		else if ("EQUIPMENT".equalsIgnoreCase(kindToAdd))
		{
			addSelectableEquipment(type);
		}
		else if ("ABILITY".equalsIgnoreCase(kindToAdd))
		{
			addSelectableAbilities(type, category);
		}
		else if ("FEAT".equalsIgnoreCase(kindToAdd))
		{
			addSelectableAbilities(type, "FEAT");
		}

		// Used by internal equipment modifier "Add Type" see LstSystemLoader.java
		else if ("EQTYPES".equalsIgnoreCase(type))
		{
			getAvailableList().addAll(Equipment.getEquipmentTypes());
		}
		else
		{
			Logging.errorPrint(
				"Unknown option in CHOOSE '" + filterBy + "'");
		}
	}

	/**
	 * Add the current character stats as defined in the game mode to the chooser
	 */
	public void addStats() {
		for (int x = 0; x < SettingsHandler.getGame().s_ATTRIBSHORT.length; x++)
		{
			this.getAvailableList().add(
				SettingsHandler.getGame().s_ATTRIBSHORT[x]);
		}
	}

	/**
	 * @param available
	 * @param numSelected
	 */
	public void adjustPool(final int available, final int numSelected) {
		if (
			(available > 0) &&
			(this.getMaxSelect() > 0) &&
			(this.getMaxSelect() != Integer.MAX_VALUE))
		{
			this.setPool(this.getMaxSelect() - numSelected);
		}
	}

	/**
	 * Populate this EquipmentChoice object using data held in choiceString
	 * @param choiceString The string containing the info to be parsed and added
	 *                     to the chooser
	 * @param parent       the piece of Equipment that this Equipment Modifier
	 *                     will be added to
	 * @param available    used to adjust the pool
	 * @param numSelected  choices made so far
	 * @param forEqBuilder is this being constructed by the equipment builder,
	 *                      or for interaction with the user.
	 */
	public void constructFromChoiceString(
		String                choiceString,
		final Equipment       parent,
		final int             available,
		final int             numSelected,
		final boolean         forEqBuilder,
		PlayerCharacter       pc)
	{
		final StringTokenizer titleTok       = new StringTokenizer(choiceString, "|", false);
		while (!forEqBuilder && titleTok.hasMoreTokens())
		{
			String workingkind = titleTok.nextToken();
			if (workingkind.startsWith("TITLE="))
			{
				this.setTitle(workingkind.substring(6));
			}
		}

		setMaxSelect(parent.getVariableValue(parent.getSelectCount(), "", pc)
				.intValue());

		String originalkind = null;
		final StringTokenizer aTok       = new StringTokenizer(choiceString, "|", false);
		boolean needStats = false;
		boolean needSkills = false;
		String category = null;

		while (!forEqBuilder && aTok.hasMoreTokens())
		{
			String kind = aTok.nextToken();
			if (category == null)
			{
				if (kind.equals("ABILITY"))
				{
					category = aTok.nextToken();
				}
				else
				{
					category = "FEAT";
				}
			}

			this.adjustPool(available, numSelected);

			if (kind.startsWith("TITLE="))
			{
				//Do nothing, handled above
			}
			else if (kind.startsWith("COUNT="))
			{
				// Do nothing, handled above
			}
			else
			{
				if (originalkind == null)
				{
					originalkind = kind;
					needStats = originalkind.equals("STATBONUS");
					needSkills = originalkind.equals("SKILLBONUS")
							|| originalkind.equals("SKILL");
				}
				else if (kind.startsWith("TYPE=") || kind.startsWith("TYPE."))
				{
					if (originalkind.equals("SKILLBONUS") || originalkind.equals("SKILL"))
					{
						//New Style
						needSkills = false;
						this.addChoicesByType(parent, available, numSelected, kind,
								"SKILL", "");
					}
					else if (originalkind.equals("EQUIPMENT")
							|| originalkind.equals("FEAT")
							|| originalkind.equals("ABILITY"))
					{
						//New Style
						this.addChoicesByType(parent, available, numSelected, kind,
								originalkind, category);
					}
					else
					{
						//Old Style
						this.addChoicesByType(parent, available, numSelected, kind,
								getTitle(), category);
					}
				}
				else if ("STAT".equals(kind))
				{
					this.addStats();
				}
				else if ("SKILL".equals(kind))
				{
					this.addSkills();
				}
				else if ("SKIPZERO".equals(kind))
				{
					skipZero = originalkind.equals("NUMBER");
				}
				else if ("MULTIPLE".equals(kind))
				{
					this.setAllowDuplicates(true);
				}
				else if ("NOSIGN".equals(kind))
				{
					this.setNoSign(true);
				}
				else if (kind.startsWith("MIN="))
				{
					this.setMinValueFromString(kind);
				}
				else if (kind.startsWith("MAX="))
				{
					this.setMaxValueFromString(kind);
				}
				else if (kind.startsWith("INCREMENT="))
				{
					this.setIncrementValueFromString(kind);
				}
				else
				{
					needStats = false;
					needSkills = false;
					if (!this.getAvailableList().contains(kind))
					{
						this.getAvailableList().add(kind);
					}
				}
			}
		}

		if (needStats)
		{
			this.addStats();
		}
		else if (needSkills)
		{
			this.addSkills();
		}

		if (this.getTitle() == null)
		{
			this.setTitle(originalkind);
		}

		if (this.getMaxSelect() == Integer.MAX_VALUE)
		{
			this.setPool(this.getAvailableList().size() - numSelected);
			this.setBAdd(true);
		}

		if (
			(this.getAvailableList().size() == 0) &&
			(this.getMinValue() < this.getMaxValue()))
		{
			for (
				int j = this.getMinValue();
				j <= this.getMaxValue();
				j += this.getIncValue())
			{
				if (!skipZero || j != 0)
				{
					if (this.isNoSign() && (j >= 0))
					{
						this.getAvailableList().add(Integer.toString(j));
					}
					else
					{
						this.getAvailableList().add(Delta.toString(j));
					}
				}
			}

			this.setMinValue(this.getMaxValue());
		}
	}

	private class EquipChoiceIterator implements Iterator<String>
	{
		List<String> choiceList;
		int currPos;

		EquipChoiceIterator(final List<String> list)
		{
			choiceList = list;
			currPos=0;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return currPos<choiceList.size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public String next()
		{
			return choiceList.get(currPos++);
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}
}
