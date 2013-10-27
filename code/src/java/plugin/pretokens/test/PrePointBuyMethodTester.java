/*
 * PrePointBuyMethod.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 16, 2005
 *
 * $Id$
 */
package plugin.pretokens.test;

import pcgen.character.CharacterDataStore;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * <code>PrePointBuyMethod</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class PrePointBuyMethodTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;
		final GameMode gm = SettingsHandler.getGame();
		if (gm != null)
		{
			final String purchaseMode = gm.getPurchaseModeMethodName();
			if ((purchaseMode != null)
				&& purchaseMode.equalsIgnoreCase(prereq.getKey()))
			{
				++runningTotal;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "POINTBUYMETHOD"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final String foo =
				PropertyFactory.getFormattedString("PrePointBuyMethod.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getKey()});
		return foo;
	}

	public int passesCDOM(Prerequisite prereq, CharacterDataStore character) throws PrerequisiteException
	{
		int runningTotal = 0;
		final GameMode gm = SettingsHandler.getGame();
		if (gm != null)
		{
			final String purchaseMode = gm.getPurchaseModeMethodName();
			if ((purchaseMode != null)
				&& purchaseMode.equalsIgnoreCase(prereq.getKey()))
			{
				++runningTotal;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

}
