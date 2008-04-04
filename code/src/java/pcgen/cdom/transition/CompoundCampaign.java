package pcgen.cdom.transition;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.persistence.lst.CampaignSourceEntry;

public class CompoundCampaign implements CampaignInterface
{
	private static Comparator<CampaignInterface> CAMPAIGN_COMPARATOR = new Comparator<CampaignInterface>()
	{
		public int compare(CampaignInterface c1, CampaignInterface c2)
		{
			int rankOne = c1.getRank();
			int rankTwo = c2.getRank();
			if (rankOne < rankTwo)
			{
				return -1;
			}
			else if (rankOne > rankTwo)
			{
				return 1;
			}
			return c1.getSourceURI().compareTo(c2.getSourceURI());
		}
	};

	private final Set<CampaignInterface> campaignSet = new TreeSet<CampaignInterface>(
			CAMPAIGN_COMPARATOR);

	public int getRank()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private URI uri;
	
	public URI getSourceURI()
	{
		return uri;
		// TODO Auto-generated method stub
		// TODO must be unique :P
		// return null;
	}

	public void addCampaign(CampaignInterface c)
	{
		if (c != null)
		{
			campaignSet.add(c);
			if (uri == null)
			{
				uri = c.getSourceURI();
			}
		}
	}

	public int countTotalFiles()
	{
		int count = 0;
		for (CampaignInterface ci : campaignSet)
		{
			count += ci.countTotalFiles();
		}
		return count;
	}

	public Collection<CampaignSourceEntry> getArmorProfFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getArmorProfFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getShieldProfFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getShieldProfFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getWeaponProfFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getWeaponProfFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getSkillFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getSkillFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getLanguageFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getLanguageFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getDeityFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getDeityFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getDomainFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getDomainFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getEquipFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getEquipFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getEquipModFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getEquipModFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getRaceFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getRaceFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getSpellFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getSpellFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getTemplateFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getTemplateFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getAbilityFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getAbilityFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getFeatFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getFeatFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getAbilityCategoryFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getAbilityCategoryFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getKitFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getKitFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public Collection<CampaignSourceEntry> getCompanionModFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci
					.getCompanionModFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public String getDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getClassFiles()
	{
		Set<CampaignSourceEntry> fileList = new LinkedHashSet<CampaignSourceEntry>();
		for (CampaignInterface ci : campaignSet)
		{
			Collection<CampaignSourceEntry> campaignList = ci.getClassFiles();
			if (campaignList != null)
			{
				fileList.addAll(campaignList);
			}
		}
		return fileList;
	}

	public List<String> getGameModes()
	{
		List<String> validModes = null;
		for (CampaignInterface ci : campaignSet)
		{
			List<String> theseModes = ci.getGameModes();
			if (validModes == null)
			{
				validModes = theseModes;
			}
			else
			{
				validModes.retainAll(theseModes);
			}
		}
		return validModes;
	}

}
