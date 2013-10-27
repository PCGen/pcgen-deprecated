package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.InstallLstToken;

/**
 * Class deals with COVER Token
 */
public class CoverToken implements CampaignLstToken, InstallLstToken
{

	public String getTokenName()
	{
		return "COVER";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("COVER:" + value);
		campaign.addCoverFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}
