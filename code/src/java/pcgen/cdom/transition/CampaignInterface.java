package pcgen.cdom.transition;

import java.net.URI;
import java.util.Collection;

import pcgen.persistence.lst.CampaignSourceEntry;

public interface CampaignInterface
{

	public int getRank();

	public URI getSourceURI();

	public int countTotalFiles();

	public Collection<CampaignSourceEntry> getWeaponProfFiles();

	public Collection<CampaignSourceEntry> getShieldProfFiles();

	public Collection<CampaignSourceEntry> getArmorProfFiles();

	public Collection<CampaignSourceEntry> getSkillFiles();

	public Collection<CampaignSourceEntry> getLanguageFiles();

	public Collection<CampaignSourceEntry> getRaceFiles();

	public Collection<CampaignSourceEntry> getDomainFiles();

	public Collection<CampaignSourceEntry> getSpellFiles();

	public Collection<CampaignSourceEntry> getDeityFiles();

	public Collection<CampaignSourceEntry> getEquipFiles();

	public Collection<CampaignSourceEntry> getEquipModFiles();

	public Collection<CampaignSourceEntry> getTemplateFiles();

	public Collection<CampaignSourceEntry> getAbilityFiles();

	public Collection<CampaignSourceEntry> getFeatFiles();

	public Collection<CampaignSourceEntry> getAbilityCategoryFiles();

	public Collection<CampaignSourceEntry> getCompanionModFiles();

	public Collection<CampaignSourceEntry> getKitFiles();

	public String getDisplayName();

	public Collection<CampaignSourceEntry> getClassFiles();

}
