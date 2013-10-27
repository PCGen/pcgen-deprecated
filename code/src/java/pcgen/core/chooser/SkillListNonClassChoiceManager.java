/**
 * SkillListNonClassChoiceManager.java
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

import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.enumeration.Visibility;

/**
 * This is one of the choosers that deals with choosing a skill.
 */
public class SkillListNonClassChoiceManager extends SkillListChoiceManager {

	/**
	 * Make a new Skill List chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public SkillListNonClassChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		List<String> choices = getChoiceList();
		final String choiceVal = choices.get(0) != null
				? choices.get(0)
				: pobject.getKeyName();

		if ((choiceVal.length() > 0) && !"LIST".equals(choiceVal))
		{
			StringTokenizer choiceTok = new StringTokenizer(choiceVal, ",");

			while (choiceTok.hasMoreTokens())
			{
				availableList.add(choiceTok.nextToken());
			}
		}

		else // if it was LIST
		{
			for ( Skill skill : Globals.getPartialSkillList(Visibility.DISPLAY) )
			{
				if ((skill.costForPCClassList(aPc.getClassList(), aPc) == Globals.getGameModeSkillCost_Class()) || skill.isExclusive())
				{
					continue; // builds a list of Cross class skills
				}

				final String rootName = skill.getRootName();
				final int rootNameLength = rootName.length();

				 //all skills have ROOTs now, so go ahead and add it if the name and root are identical
				if ((rootNameLength == 0) || rootName.equals(skill.getKeyName()))
				{
					availableList.add(skill.getKeyName());
				}

				final boolean rootArrayContainsRootName = rootArrayList.contains(rootName);

				if ((rootNameLength > 0) && !rootArrayContainsRootName)
				{
					rootArrayList.add(rootName);
				}

				if ((rootNameLength > 0) && rootArrayContainsRootName)
				{
					availableList.add(skill.getKeyName());
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
		setPreChooserChoices(selectedList.size());
	}

}
