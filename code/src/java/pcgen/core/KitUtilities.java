/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Refactored out of PObject July 22, 2005
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This is a utility class related to Kit objects.
 */
public final class KitUtilities
{
	private KitUtilities()
	{
		//Don't allow instantiation of utility class
	}

	/**
	 * Make the Kit seclections
	 * @param arg
	 * @param kitString
	 * @param iKit
	 * @param aPC
	 */
	public static final void makeKitSelections(final int arg, final String kitString, final int iKit, final PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(kitString, "|", false);

		// first element is prelevel - should be 0 for everything but PCClass entries
		String tok = aTok.nextToken();
		int aLevel;

		try
		{
			aLevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed kitString: " + tok);
			aLevel = 0;
		}

		if (aLevel > arg)
		{
			return;
		}

		tok = aTok.nextToken();

		int num;

		try
		{
			num = Integer.parseInt(tok); // number of kit selections
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed number of kit selections attribute: " + tok);
			num = 0;
		}

		List<Kit> aList = new ArrayList<Kit>();

		while (aTok.hasMoreTokens())
		{
			final String kitKey = aTok.nextToken();
			final Kit aKit = Globals.getKitKeyed(kitKey);
			if (aKit == null)
			{
				Logging.errorPrint("Nonexistant kit: " + kitKey);
				return;
			}
			if (PrereqHandler.passesAll(aKit.getPreReqList(), aPC, aKit))
			{
				aList.add(aKit);
			}
		}

		if (num != aList.size())
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setTitle("Kit Selection");
			c.setTotalChoicesAvail(num);
			c.setPoolFlag(false);
			c.setAvailableList(aList);
			c.setVisible(true);
			aList = c.getSelectedList();
		}

		if (aList.size() > 0)
		{
			for ( Kit kit : aList )
			{
				Kit.applyKit( kit, aPC );
			}
		}
	}

}
