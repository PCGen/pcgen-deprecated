/*
 * CampaignOutput.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.persistence.lst;

import pcgen.core.Campaign;
import pcgen.core.SettingsHandler;
import pcgen.core.Source;
import pcgen.io.FileAccess;
import pcgen.util.Logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * <code>CampaignOutput</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class CampaignOutput
{
	/**
	 * Private constructor added to inhibit instance creation for this utility class.
	 */
	private CampaignOutput()
	{
		// Empty Constructor
	}

	/**
	 * @param campaign
	 */
	public static void output(Campaign campaign)
	{
		final File outFile =
				new File(SettingsHandler.getPccFilesLocation()
					.getAbsolutePath()
					+ File.separator + campaign.getDestination());
		BufferedWriter out = null;

		try
		{
			out =
					new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF-8"));
			FileAccess.write(out, "CAMPAIGN:" + campaign.getKeyName());
			FileAccess.newLine(out);
			FileAccess.write(out, "RANK:" + campaign.getRank());
			FileAccess.newLine(out);
			FileAccess.write(out, "GAMEMODE:");

			for (Iterator<String> gm = campaign.getGameModes().iterator(); gm
				.hasNext();)
			{
				String gmName = gm.next();
				FileAccess.write(out, gmName);

				if (gm.hasNext())
				{
					FileAccess.write(out, "|");
				}
			}

			FileAccess.newLine(out);
			FileAccess.write(out, "INFOTEXT:" + campaign.getInfoText());
			FileAccess.newLine(out);
			final Source source = campaign.getSourceEntry().getSourceBook();
			FileAccess.write(out, "SOURCELONG:" + source.getLongName());
			FileAccess.newLine(out);
			FileAccess.write(out, "SOURCESHORT:" + source.getShortName());
			FileAccess.newLine(out);
			FileAccess.write(out, "SOURCEWEB:" + source.getWebsite());
			FileAccess.newLine(out);
			FileAccess.write(out, "ISD20:" + (campaign.isD20() ? "YES" : "NO"));
			FileAccess.newLine(out);
			FileAccess.write(out, "ISOGL:" + (campaign.isOGL() ? "YES" : "NO"));
			FileAccess.newLine(out);
			FileAccess.write(out, "SHOWINMENU:"
				+ (campaign.canShowInMenu() ? "YES" : "NO"));
			FileAccess.newLine(out);
			FileAccess.write(out, "ISLICENSED:"
				+ (campaign.isLicensed() ? "YES" : "NO"));
			FileAccess.newLine(out);
			FileAccess.write(out, "BOOKTYPE:" + campaign.getBookType());
			FileAccess.newLine(out);
			FileAccess.write(out, "SETTING:" + campaign.getSetting());
			FileAccess.newLine(out);
			FileAccess.write(out, "GENRE:" + campaign.getGenre());
			FileAccess.newLine(out);

			for (Iterator i = campaign.getOptionsList().iterator(); i.hasNext();)
			{
				FileAccess.write(out, "OPTION:"
					+ campaign.getOptions().getProperty((String) i.next()));
				FileAccess.newLine(out);
			}

			for (Iterator<String> i = campaign.getSection15s().iterator(); i
				.hasNext();)
			{
				FileAccess.write(out, "COPYRIGHT:" + i.next());
				FileAccess.newLine(out);
			}

			for (Iterator<String> i = campaign.getLicenses().iterator(); i
				.hasNext();)
			{
				FileAccess.write(out, "LICENSE:" + i.next());
				FileAccess.newLine(out);
			}

			for (Iterator<String> i = campaign.getLines().iterator(); i
				.hasNext();)
			{
				FileAccess.write(out, i.next());
				FileAccess.newLine(out);
			}
		}
		catch (FileNotFoundException exc)
		{
			Logging.errorPrint("Error while writing to " + outFile.toString(),
				exc);

			//TODO: Is this ok? Shouldn't something be done if writing a campaign fails?
		}
		catch (UnsupportedEncodingException exc)
		{
			Logging.errorPrint("Error while writing to " + outFile.toString(),
				exc);

			//TODO: Is this ok? Shouldn't something be done if writing a campaign fails?
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.flush();
					out.close();
				}
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Can't close " + outFile.toString(), ex); //Not much more to do really...
			}
		}
	}
}
