/*
 * Ability.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.Tab;

/**
 * Definition and games rules for an Ability.
 *
 * @author   ???
 * @version  $Revision$
 */
public final class Ability extends PObject implements HasCost, Categorisable
{
	/** An enum for the various types of ability options. */
	public enum Nature {
		/** Ability is Normal */
		NORMAL,
		/** Ability is Automatic */
		AUTOMATIC,
		/** Ability is Virtual */
		VIRTUAL,
		/** Ability of any type */
		ANY
	}

	private boolean multiples = false;
	private boolean needsSaving = false;
	private boolean stacks = false;

	private Nature theNature = Nature.NORMAL;
	
	// /////////////////////////////////////
	// Fields - Associations

	private List<Description> theBenefits = null;

	// /////////////////////////////////////
	// Constructor
	/* default constructor only */

	// /////////////////////////////////////
	// Methods - Accessors
	/**
	 * Set the increase in spell level that this metamagic feat costs to apply
	 *
	 * @param  addSpellLevel  the increase to apply
	 */
	public void setAddSpellLevel(final int addSpellLevel)
	{
		integerChar.put(IntegerKey.ADD_SPELL_LEVEL, addSpellLevel);
	}

	/**
	 * for metamagic feats increase in spelllevel
	 *
	 * @return  The number of levels that this Ability increases the level of a
	 *          spell it is applied to
	 */
	public int getAddSpellLevel()
	{
		final Integer characteristic = integerChar.get(IntegerKey.ADD_SPELL_LEVEL);
		return characteristic == null ? 0 : characteristic;
	}

	/**
	 * Set the attribute that controls what this ability adds, e.g. WEAPONPROF,
	 * TEMPLATE, etc.
	 *
	 * @param  add   what this ability adds, e.g. WEAPONPROF, TEMPLATE, etc.
	 */
	public void setAddString(final String add)
	{
		stringChar.put(StringKey.ADD, add);
	}

	/**
	 * Return the unparsed string that controls what this ability adds, e.g.
	 * WEAPONPROF, TEMPLATE, etc.
	 *
	 * @return  return the unparsed string that controls what this ability adds,
	 *          e.g. WEAPONPROF, TEMPLATE, etc.
	 */
	public String getAddString()
	{
		final String characteristic = stringChar.get(StringKey.ADD);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Adds a description for this object.  Multiple descriptions are allowed 
	 * and will be concatonated on output.
	 * 
	 * <p>The format of the description tag 
	 * @param aDesc a description of what this object provides
	 */
	public void addBenefit( final Description aDesc )
	{
		if ( theBenefits == null )
		{
			theBenefits = new ArrayList<Description>();
		}
		aDesc.setOwner( this );
		theBenefits.add( aDesc );
	}
	
	/**
	 * Clears all current benefits for the object.
	 */
	public void removeAllBenefits()
	{
		theBenefits.clear();
		theBenefits = null;
	}
	
	/**
	 * Removes benefit <tt>Description</tt>s who's PCC Text matches the pattern
	 * specified.
	 *  
	 * @param aDescPattern The regular expression to search for.
	 */
	public void removeBenefit( final String aDescPattern )
	{
		if ( theBenefits == null )
		{
			return;
		}
		final Pattern pattern = Pattern.compile(aDescPattern);

		for ( final Iterator<Description> i = theBenefits.iterator(); i.hasNext(); )
		{
			final String descText = i.next().getPCCText();
			final Matcher matcher = pattern.matcher(descText);
			if ( matcher.find() )
			{
				i.remove();
			}
		}
	}
	
	/**
	 * Get the benefits of this object
	 * 
	 * @param aPC The PlayerCharacter this object is associated to.
	 * @return the benefits of this ability
	 */
	public String getBenefits(final PlayerCharacter aPC)
	{
		if ( theBenefits == null )
		{
			return Constants.EMPTY_STRING;
		}
		final StringBuffer buf = new StringBuffer();
		boolean wrote = false;
		for ( final Description desc : theBenefits )
		{
			final String str = desc.getDescription(aPC);
			if ( str.length() > 0 )
			{
				if ( wrote )
				{
					buf.append(Constants.COMMA);
				}
				buf.append(str);
				wrote = true;
			}
		}
		return buf.toString();
	}

	public List<Description> getBenefitList()
	{
		if ( theBenefits == null )
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(theBenefits);
	}

	// TODO - Remove this once a decision is made about descriptions.
	public String getBenefitDescription()
	{
		if (SettingsHandler.useFeatBenefits() && getBenefits(null).length() > 1)
		{
			return getBenefits(null);
		}

		return getDescription();
	}
	
	/**
	 * Get a description of what this ability does
	 *
	 * @return  the benefit if it is set and they are turned on, otherwise
	 *          return the description
	 */
	@Override
	public String getDescription(final PlayerCharacter aPC)
	{
		if (SettingsHandler.useFeatBenefits() && getBenefits(aPC).length() > 1)
		{
			return getBenefits(aPC);
		}

		return super.getDescription(aPC);
	}

	/**
	 * Set the category of this Ability
	 *
	 * @param  category  the category of the ability
	 */
	public void setCategory(final String category)
	{
		stringChar.put(StringKey.CATEGORY, category);
	}

	/**
	 * Get the category of this ability
	 *
	 * @return  The category of this Ability
	 */
	public String getCategory()
	{
		final String characteristic = stringChar.get(StringKey.CATEGORY);
		return characteristic == null ? Constants.FEAT_CATEGORY : characteristic;
	}

	/**
	 * Identify if this ability is actually a feat.
	 * @return true if this is a feat, false otherwise.
	 */
	public boolean isFeat()
	{
		return Constants.FEAT_CATEGORY.equals(getCategory());
	}

	/**
	 * Set how many "points" this ability costs
	 *
	 * @param  cost  the cost of the ability
	 */
	public void setCost(final String cost)
	{
		stringChar.put(StringKey.COST, cost);
	}

	/**
	 * Get the cost of this ability
	 *
	 * @return  a double representing the cost of the ability
	 */
	public double getCost()
	{
		return Double.parseDouble(getCostString());
	}

	/**
	 * Get the cost of this ability
	 *
	 * @return  a String representing the cost of the ability
	 */
	public String getCostString()
	{
		final String characteristic = stringChar.get(StringKey.COST);
		return characteristic == null ? "1" : characteristic;
	}

	/**
	 * This version of getCost treats the thing stored in cost as the name of a
	 * variable which it looks up in the PlayerCharacter object passed in.
	 *
	 * @param   pc  a PlayerCharacter object to look up the cost in
	 *
	 * @return  the cost of the ability
	 */
	public double getCost(final PlayerCharacter pc)
	{
		return pc.getVariableValue(getCostString(), "").doubleValue();
	}

	/**
	 * Set the AbilityType property of this Ability
	 *
	 * @param  type  The type of this ability (normal, automatic, virtual (see
	 *               named constants))
	 */
	public void setFeatType(final Nature type)
	{
		if ( type == Nature.ANY )
		{
			return;
		}

		theNature = type;
	}

	/**
	 * Really badly named method.
	 *
	 * @return  The nature of this feat.
	 */
	public Nature getFeatType()
	{
		return theNature;
	}

	/**
	 * Returns true if the feat matches the given type (the type is contained in
	 * the type string of the feat).
	 *
	 * @param   type  the type to test against
	 *
	 * @return  true if the Ability is of type abilityType
	 */
	boolean matchesType(final String type)
	{
		return isType(type);
	}

	/**
	 * Set whether or not a character may have multiple instances of this
	 * ability
	 *
	 * @param  aString  If this begins with Y the property will be set true
	 */
	public void setMultiples(final String aString)
	{
		if (aString.length() == 0)
		{
			multiples = false;
		}
		else
		{
			final char firstChar = aString.charAt(0);
			multiples = firstChar == 'y' || firstChar == 'Y';
		}
	}

	/**
	 * Whether or not we can have multiples of this ability
	 *
	 * @return  whether there can be multiples.
	 */
	public boolean isMultiples()
	{
		return multiples;
	}

	/**
	 * If this is a "virtual Ability", this property controls whether it will be
	 * saved with the character
	 *
	 * @param  save  whether to save the feat
	 */
	public void setNeedsSaving(final boolean save)
	{
		needsSaving = save;
	}

	/**
	 * If this is a "virtual Ability", this property controls whether it will be
	 * saved with the character
	 *
	 * @return  whether to save the feat
	 */
	public boolean needsSaving()
	{
		return needsSaving;
	}

	/**
	 * Whether this ability may be taken multiple times for enhanced effect
	 *
	 * @param  aString  To allow stacking, pass a string beginning with Y
	 */
	public void setStacks(final String aString)
	{
		stacks = (aString.length() > 0) && (aString.charAt(0) == 'Y');
	}

	/**
	 * Does this ability stack for enhanced effect?
	 *
	 * @return  Whether this ability stacks for enhanced effect.
	 */
	public boolean isStacks()
	{
		return stacks;
	}

	/**
	 * Whether we can add newAssociation to the associated list of this
	 * Ability
	 *
	 * @param newAssociation The thing to be associated with this Ability
	 * @return true if we can add the association
	 */
	public boolean canAddAssociation(final String newAssociation)
	{
		return 	this.isStacks() || (this.isMultiples() && !this.containsAssociated(newAssociation));
	}

	/**
	 * Bog standard clone method
	 *
	 * @return  a copy of this Ability
	 */
	@Override
	public Ability clone()
	{
		try
		{
			final Ability ret = (Ability)super.clone();

			if ( theBenefits != null )
			{
				ret.theBenefits = new ArrayList<Description>();
				for ( final Description desc : theBenefits )
				{
					desc.setOwner(ret);
					ret.theBenefits.add(desc);
				}
			}
			
			return ret;
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
			return null;
		}
	}

	/**
	 * Make a string that can be saved that will represent this Ability object
	 *
	 * @return  a string representation that can be parsed to rebuild the
	 *          Ability
	 */
	@Override
	public String getPCCText()
	{
		final StringBuffer txt = new StringBuffer(200);
		txt.append(getDisplayName());
		txt.append("\tCATEGORY:").append(getCategory());
		txt.append("\tCOST:").append(String.valueOf(getCost()));

		if (isMultiples())
		{
			txt.append("\tMULT:Y");
		}

		if (isStacks())
		{
			txt.append("\tSTACK:Y");
		}

		if (getAddSpellLevel() != 0)
		{
			txt.append("\tADDSPELLLEVEL:").append(getAddSpellLevel());
		}

		if (getAddString().length() != 0)
		{
			txt.append("\tADD:").append(getAddString());
		}

		txt.append("\tVISIBLE:");

		switch (getVisibility())
		{
			case NO:
				txt.append("NO");
				break;

			case EXPORT:
				txt.append("EXPORT");
				break;

			case DISPLAY:
				txt.append("DISPLAY");
				break;

			case YES:
			default:
				txt.append("YES");
				break;
		}

		if (getChoiceToModify().length() != 0)
		{
            txt.append("\tMODIFYABILITYCHOICE:").append(getChoiceToModify());
		}

		txt.append(super.getPCCText(false));

		return txt.toString();
	}

	// /////////////////////////////////////////////
	// move to CharacterFeat
	/**
	 * This method generates a name for this Ability which includes any choices
	 * made and a count of how many times it has been applied.
	 *
	 * @return  The name of the full Ability, plus any sub-choices made for this
	 *          character. Starts with the name of the ability, and then (for
	 *          types other than weapon proficiencies), either appends a count
	 *          of the times the ability is applied e.g. " (3x)", or a list of
	 *          the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the ability
		// don't do for Weapon Profs
		final StringBuffer aStrBuf = new StringBuffer(getOutputName());

		if ("[BASE]".equalsIgnoreCase(getOutputName()))
		{
			return getDisplayName();
		}
		if ((getAssociatedCount() > 0)
				&& !getKeyName().startsWith("Armor Proficiency")
				)
		{
			if ((getChoiceString().length() == 0) || (multiples && stacks))
			{
				if (getAssociatedCount() > 1)
				{
					// number of items only (ie stacking), e.g. " (1x)"
					aStrBuf.append(" (");
					aStrBuf.append((int) (getAssociatedCount() * getCost()));
					aStrBuf.append("x)");
				}
			}
			else
			{
				// has a sub-detail
				aStrBuf.append(" (");

                int i = 0;

				// list of items in associatedList, e.g. " (Sub1, Sub2, ...)"
				for (int e = 0; e < getAssociatedCount(true); ++e)
				{
					if (i > 0)
					{
						aStrBuf.append(", ");
					}

					aStrBuf.append(getAssociated(e, true));
					++i;
				}

				aStrBuf.append(')');
			}
		}

		return aStrBuf.toString();
	}

	boolean canBeSelectedBy(final PlayerCharacter pc)
	{
		return PrereqHandler.passesAll(getPreReqList(), pc, this);
	}

	/**
	 * Deal with CHOOSE tags.   The actual items the choice will be made from are
	 * based on this.choiceString, as applied to current character. Choices already
	 * made (getAssociatedList) are indicated in the selectedList.  This method
	 * always processes the choices.
	 *
	 * @param   aPC    The Player Character that we're opening the chooser for.
	 * @param   addIt  Whether to add or remove a choice from this Ability.
     * @param   category The ability category whose pool is to be charged for the ability.
	 *
	 * @return  true if the Ability was modified, false otherwise
	 */
	public boolean modChoices(final PlayerCharacter aPC, final boolean addIt, final AbilityCategory category)
	{
		final List availableList = new ArrayList(); // available list of choices
		final List selectedList  = new ArrayList(); // selected list of choices

		return modChoices(
				availableList,
				selectedList,
				true,
				aPC,
				addIt,
				category);
	}

	/**
	 * Deal with CHOOSE tags. The actual items the choice will be made from are
	 * based on this.choiceString, as applied to current character. Choices already
	 * made (getAssociatedList) are indicated in the selectedList.  This method
	 * may also be used to build a list of choices available and choices
	 * already made by passing false in the process parameter
	 *
	 * @param availableList the list of things not already chosen
	 * @param selectedList the list of things already chosen
	 * @param process if false do not process the choice, just poplate the lists
	 * @param aPC the PC that owns the Ability
	 * @param addIt Whether to add or remove a choice from this Ability
     * @param category The ability category whose pool is to be charged for the ability.
	 *
	 * @return true if we processed the list of choices, false if we used the routine to
	 * build the list of choices without processing them.
	 */
	public boolean modChoices(
		final List            availableList,
		final List            selectedList,
		final boolean         process,
		final PlayerCharacter aPC,
		final boolean         addIt,
		final AbilityCategory category)
	{
		return ChooserUtilities.modChoices(
				this,
				availableList,
				selectedList,
				process,
				aPC,
				addIt,
				category);
	}

	/**
	 * Enhanced containsAssociated, which parses the input parameter for "=",
	 * "+num" and "-num" to extract the value to look for.
	 *
	 * @param   type  The type we're looking for
	 *
	 * @return  enhanced containsAssociated, which parses the input parameter
	 *          for "=", "+num" and "-num" to extract the value to look for.
	 */
	@Override int numberInList(final String type)
	{
        String aType = type;

        if (aType.lastIndexOf('=') > -1)
		{
			aType = aType.substring(aType.lastIndexOf('=') + 1);
		}

		// truncate at + sign if following character is a number
        final String numString = "0123456789";
        if (aType.lastIndexOf('+') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('+') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('+'));
			}
		}

		// truncate at - sign if following character is a number
		if (aType.lastIndexOf('-') > -1)
		{
			final String aString = aType.substring(aType.lastIndexOf('-') + 1);

			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
			{
				aType = aType.substring(0, aType.lastIndexOf('-'));
			}
		}

        int iCount = 0;
        for (int i = 0; i < getAssociatedCount(); ++i)
		{
			if (getAssociated(i).equalsIgnoreCase(aType))
			{
				iCount += 1;
			}
		}

		return iCount;
	}

	/**
	 * Adds some info that controls what this ability adds, e.g. WEAPONPROF,
	 * TEMPLATE, etc.
	 *
	 * @param   aLevel   an int (think it represents the character level that
	 *                   this ability is granted at)
	 * @param   aString  the information about things (templates, weapon profs,
	 *                   etc.) this ability adds.
	 *
	 * @return  The new LevelAbility object if one was created.
	 */
	@Override
	public LevelAbility addAddList(final int aLevel, final String aString)
	{
		if (aString.startsWith("TEMPLATE|"))
		{
			setAddString(aString);
			return null;
		}
		return super.addAddList(aLevel, aString);
	}

	@Override boolean isTypeHidden(final String type)
	{
		return Globals.isAbilityTypeHidden(type);
	}

	/**
	 * Simple setter method for a String representing a choice that must be
	 * made when applying this ability
	 *
	 * @param  choiceToModify sets the choice
	 */
	public void setChoiceToModify(final String choiceToModify)
	{
		stringChar.put(StringKey.CHOICE_TO_MODIFY, choiceToModify);
	}

	/**
	 * simple getter method for a string that represents a choice that must
	 * be made when applying this Ability.
	 *
	 * @return  The choice to be made.
	 */
	public String getChoiceToModify()
	{
		final String characteristic = stringChar.get(StringKey.CHOICE_TO_MODIFY);
		return characteristic == null ? "" : characteristic;
	}

	/**
	 * Modify the Ability as per the info from this.getChoiceToModify() and the
	 * choices made by the user in the GUI.
	 *
	 * @param   aPC  The Player Character object this Ability belongs to.
	 *
	 * @return  whether we modified the Ability
	 */
	public boolean modifyChoice(final PlayerCharacter aPC)
	{
		String abilityName = getChoiceToModify();

		if (abilityName.length() == 0)
		{
			return false;
		}

		final List<String> abilityList = new ArrayList<String>();
		final List<String> selectedList = new ArrayList<String>();

        if (abilityName.startsWith("TYPE=") || abilityName.startsWith("TYPE."))
		{
			final String anAbilityType = abilityName.substring(5);

			//
			// Get a list of all ability possessed by the character that
			// are the specified type
			//
			for ( final PObject ability : aPC.aggregateFeatList() )
			{
				if (ability.isType(anAbilityType))
				{
					abilityList.add(ability.getKeyName());
				}
			}

			//
			// Get the user to select one if there is more than 1.
			//
			switch (abilityList.size())
			{
				case 0:
					Logging.debugPrint("PC does not have an ability of type: "
							+ anAbilityType);
					return false; // no ability to modify

				case 1:
					abilityName = abilityList.get(0);
					break;

				default:

					final ChooserInterface chooser = ChooserFactory.getChooserInstance();
					chooser.setPoolFlag(false); // user is not required to make any

					// changes
					chooser.setTotalChoicesAvail(1);

					chooser.setTitle("Select a "
							+ SettingsHandler.getGame().getSingularTabName(Tab.ABILITIES)
							+ " to modify");

					Globals.sortChooserLists(abilityList, selectedList);
					chooser.setAvailableList(abilityList);
					chooser.setSelectedList(selectedList);
					chooser.setVisible(true);

					final int selectedSize = chooser.getSelectedList().size();

					if (selectedSize == 0)
					{
						return false; // no ability chosen, so nothing was modified
					}

					abilityName = (String) chooser.getSelectedList().get(0);

					break;
			}
		}

        final Ability anAbility = aPC.getFeatNamed(abilityName);

        if (anAbility == null)
		{
			Logging.debugPrint("PC does not have ability: " + abilityName);

			return false;
		}

		//
		// Ability doesn't allow choices, so we cannot modify
		//
		if (!anAbility.isMultiples())
		{
			Logging.debugPrint("MULT:NO for: " + abilityName);

			return false;
		}

		// build a list of available choices and choices already made.
		anAbility.modChoices(abilityList, selectedList, false, aPC, true,
			SettingsHandler.getGame().getAbilityCategory(this.getCategory()));

		final int currentSelections = selectedList.size();

		//
		// If nothing to choose, or nothing selected, then leave
		//
		if ((abilityList.size() == 0) || (currentSelections == 0))
		{
			return false;
		}

		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(true); // user is required to use all available
								   // pool points
		chooser.setTotalChoicesAvail(selectedList.size()); // need to remove 1 to add another

		chooser.setTitle("Modify selections for " + abilityName);
		Globals.sortChooserLists(abilityList, selectedList);
		chooser.setAvailableList(abilityList);
		chooser.setSelectedList(selectedList);
		chooser.setVisible(true);

		final int selectedSize = chooser.getSelectedList().size();

		if (selectedSize != currentSelections)
		{
			return false; // need to have the same number of selections when finished
		}

		// replace old selection(s) with new and update bonuses
		//
		anAbility.clearAssociated();

		for (int i = 0; i < selectedSize; ++i)
		{
			anAbility.addAssociated((String) chooser.getSelectedList().get(i));
		}

		// aPC.calcActiveBonuses();
		return true;
	}

    /**
     * Compare an ability (category) to another one
     * Returns the compare value from String.compareToIgnoreCase
     * 
     * @param obj the object that we're comparing against
     * @return compare value
     */
	@Override
	public int compareTo(final Object obj)
	{
		if (obj != null)
		{
			try
			{
				final Categorisable ab = (Categorisable) obj;
				if (this.getCategory().compareToIgnoreCase(ab.getCategory()) != 0)
				{
					return this.getCategory().compareToIgnoreCase(ab.getCategory());
				}
			}
			catch (ClassCastException e)
			{
				// Do nothing.  If the cast to Ability doesn't work, we assume that
				// the category of the Object passed in matches the category of this
				// Ability and compare KeyNames
			}

			// this should throw a ClassCastException for non-PObjects, like the
			// Comparable interface calls for
			return this.keyName.compareToIgnoreCase(((PObject) obj).keyName);
		}
		return 1;
	}

	/**
     * Equals function, uses compareTo to do the work
     * 
	 * @param other Ability to compare to
	 * @return true if they are equal
	 */
    @Override
	public boolean equals(final Object other)
	{
		return other instanceof Ability && this.compareTo(other) == 0;
	}
    
    /**
     * Must be consistent with equals
     */
    @Override
	public int hashCode() {
    	//Can't be more complicated because the weird nature of compareTo
    	return keyName.hashCode();
    }

	/**
	 * Test whether other is the same base ability as this (ignoring any changes
	 *  made to apply either to a PC)
	 *
	 * @param that the other ability
	 * @return true is the abilities are copies of the same base ability
	 */
	public boolean isSameBaseAbility(Ability that) {
		return AbilityUtilities.areSameAbility(this, that);
	}
}
