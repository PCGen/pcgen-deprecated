package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.util.Logging;

import java.net.URI;
import java.util.Properties;

/**
 * Class deals with OPTION Token
 */
public class OptionToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "OPTION";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		// We store a set of options with the campaign, so add this one in now.
		// That way when the campaign is selected the options can be set too.
		Properties options = campaign.getOptions();

		if (options == null)
		{
			options = new Properties();
			campaign.setOptions(options);
		}

		final int equalsPos = value.indexOf("=");

		if (equalsPos >= 0)
		{
			String optName = value.substring(0, equalsPos);

			if (optName.regionMatches(true, 0, "pcgen.options.", 0, 14))
			{
				optName = optName.substring(14);
			}

			final String optValue = value.substring(equalsPos + 1);
			options.setProperty(optName, optValue);
		}
		else
		{
			Logging.errorPrint("Invalid option line in source file "
				+ sourceUri.toString() + " : " + value);
			return false;
		}
		return true;
	}
}
