/**
 * RaceChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;

/**
 * This is the chooser that deals with choosing a race.
 */
public class RaceChoiceManager extends AbstractBasicPObjectChoiceManager<Race>
{
	/**
	 * Make a new Race chooser.
	 *
	 * @param  aPObject
	 * @param  choiceString
	 * @param  aPC
	 */
	public RaceChoiceManager(
		PObject         aPObject,
		String          choiceString,
		PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		this.setTitle("Choose Race");
	}


	/**
	 * Construct a list of available selections for race.
	 *
	 * @param  aPc
	 * @param  availableList
	 * @param  selectedList
	 */
	@Override
	public void getChoices(
		final PlayerCharacter aPc,
		final List<Race>            availableList,
		final List<Race>            selectedList)
	{
		// CHOOSE:RACE|RACETYPE=x,RACESUBTYPE=y,<racename>,TYPE=z
		// or CHOOSE:RACE|[RACETYPE=x,RACESUBTYPE=y]
		Collection<Race> races = Globals.getAllRaces();

		for (String choice : getChoiceList())
		{
			// All top-level comma-separated items are added to the list.
			if (choice.indexOf("[") != -1)
			{
				processTokenWithBrackets(availableList, races, choice);
			}

			if (choice.startsWith("RACETYPE=") || choice.startsWith("RACETYPE."))
			{
				// Add all races matching this racetype
				for ( Race race : races )
				{
					if (race.getRaceType().equals(choice.substring(9)))
					{
						availableList.add(race);
					}
				}
			}
			else if (
				choice.startsWith("RACESUBTYPE=") ||
				choice.startsWith("RACESUBTYPE."))
			{
				// Add all races matching this racetype
				for ( Race race : races )
				{
					if (race.getRacialSubTypes().contains(choice.substring(9)))
					{
						availableList.add(race);
					}
				}
			}
			else if (choice.startsWith("TYPE=") || choice.startsWith("TYPE."))
			{
				// Add all races matching this racetype
				for ( Race race : races )
				{
					if (race.getType().equals(choice.substring(5)))
					{
						availableList.add(race);
					}
				}
			}
			else
			{
				Race race = Globals.getRaceKeyed(choice);

				if (race != null)
				{
					availableList.add(race);
				}
			}
		}
		
		List<String> raceKeys = new ArrayList<String>();
		pobject.addAssociatedTo(raceKeys);
		for (String key : raceKeys)
		{
			Race race = Globals.getRaceKeyed(key);
			if (race != null)
			{
				selectedList.add(race);
			}
		}
		setPreChooserChoices(selectedList.size());
	}


	/**
	 * process a choice token of the form [RACETYPE=x,RACESUBTYPE=y].  A race
	 * will only be added to the available list if all of the given specifiers
	 * (RACETYPE, RACESUBTYPE, etc.) match
	 *
	 * @param  availableList
	 * @param  races
	 * @param  choice
	 */
	private void processTokenWithBrackets(
		final List<Race> availableList,
		Collection<Race> races,
		String     choice)
	{
		ArrayList<String> raceTypes    = new ArrayList<String>();
		ArrayList<String> raceSubTypes = new ArrayList<String>();
		ArrayList<String> types        = new ArrayList<String>();

		choice = choice.substring(1, choice.length() - 1);

		StringTokenizer options = new StringTokenizer(choice, ",");

		while (options.hasMoreTokens())
		{
			String option = options.nextToken();

			if (option.startsWith("RACETYPE=") || option.startsWith("RACETYPE."))
			{
				raceTypes.add(option.substring(9));
			}
			else if (
				option.startsWith("RACESUBTYPE=") ||
				option.startsWith("RACESUBTYPE."))
			{
				raceSubTypes.add(option.substring(12));
			}
			else if (option.startsWith("TYPE=") || option.startsWith("TYPE."))
			{
				types.add(option.substring(5));
			}
		}

		for ( Race race : races )
		{
			if (checkRace(race, raceTypes, raceSubTypes, types))
			{
				availableList.add(race);
			}
		}
	}

	/**
	 * Does race match all of the given raceTypes, raceSubtypes and types
	 *
	 * @param   race
	 * @param   raceTypes
	 * @param   raceSubTypes
	 * @param   types
	 *
	 * @return  true if race matches
	 */
	private static boolean checkRace(
		Race race,
		List<String> raceTypes,
		List<String> raceSubTypes,
		List<String> types)
	{
		for ( String raceType : raceTypes )
		{
			if (!race.getRaceType().equals(raceType))
			{
				return false;
			}
		}

		for ( String raceSubType : raceSubTypes )
		{
			if (!race.getRacialSubTypes().contains(raceSubType))
			{
				return false;
			}
		}

		for ( String rType : types )
		{
			if (!race.getType().equals(rType))
			{
				return false;
			}
		}

		return true;
	}

}
