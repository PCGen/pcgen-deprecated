package pcgen.cdom.transition;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.CustomData;
import pcgen.core.Description;
import pcgen.persistence.lst.CampaignSourceEntry;

public class CustomCampaign implements CampaignInterface
{

	public int getRank()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public URI getSourceURI()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void ohyea()
	{
		CampaignSourceEntry tempSource = null;

//		// The dummy campaign for custom data.
//		Campaign customCampaign = new Campaign();
//		customCampaign.setName("Custom");
//		customCampaign.addDescription(new Description("Custom data"));
//
//		//
//		// Add the custom bioset file to the start of the list if it exists
//		//
//		File bioSetFile = new File(CustomData.customBioSetFilePath(true));
//		if (bioSetFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, bioSetFile
//					.toURI());
//			bioSetFileList.remove(tempSource);
//			bioSetFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom class file to the start of the list if it exists
//		//
//		File classFile = new File(CustomData.customClassFilePath(true));
//		if (classFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, classFile
//					.toURI());
//			classFileList.remove(tempSource);
//			classFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom deity file to the start of the list if it exists
//		//
//		File deityFile = new File(CustomData.customDeityFilePath(true));
//		if (deityFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, deityFile
//					.toURI());
//			deityFileList.remove(tempSource);
//			deityFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom domain file to the start of the list if it exists
//		//
//		File domainFile = new File(CustomData.customDomainFilePath(true));
//		if (domainFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, domainFile
//					.toURI());
//			domainFileList.remove(tempSource);
//			domainFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom ability file to the start of the list if it exists
//		//
//		File abilityFile = new File(CustomData.customAbilityFilePath(true));
//		if (abilityFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, abilityFile
//					.toURI());
//			abilityFileList.remove(tempSource);
//			abilityFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom feat file to the start of the list if it exists
//		//
//		File featFile = new File(CustomData.customFeatFilePath(true));
//		if (featFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, featFile
//					.toURI());
//			featFileList.remove(tempSource);
//			featFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom language file to the start of the list if it exists
//		//
//		File languageFile = new File(CustomData.customLanguageFilePath(true));
//		if (languageFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, languageFile
//					.toURI());
//			languageFileList.remove(tempSource);
//			languageFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom race file to the start of the list if it exists
//		//
//		File raceFile = new File(CustomData.customRaceFilePath(true));
//		if (raceFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, raceFile
//					.toURI());
//			raceFileList.remove(tempSource);
//			raceFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom skill file to the start of the list if it exists
//		//
//		File skillFile = new File(CustomData.customSkillFilePath(true));
//		if (skillFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, skillFile
//					.toURI());
//			skillFileList.remove(tempSource);
//			skillFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom spell file to the start of the list if it exists
//		//
//		File spellFile = new File(CustomData.customSpellFilePath(true));
//		if (spellFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, spellFile
//					.toURI());
//			spellFileList.remove(tempSource);
//			spellFileList.add(0, tempSource);
//		}
//
//		//
//		// Add the custom template file to the start of the list if it exists
//		//
//		File templateFile = new File(CustomData.customTemplateFilePath(true));
//		if (templateFile.exists())
//		{
//			tempSource = new CampaignSourceEntry(customCampaign, templateFile
//					.toURI());
//			templateFileList.remove(tempSource);
//			templateFileList.add(0, tempSource);
//		}

	}

	public int countTotalFiles()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public List<CampaignSourceEntry> getArmorProfFiles()
	{
		return null;
	}

	public List<CampaignSourceEntry> getShieldProfFiles()
	{
		return null;
	}

	public List<CampaignSourceEntry> getWeaponProfFiles()
	{
		return null;
	}

	public Collection<CampaignSourceEntry> getSkillFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getLanguageFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getDeityFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getDomainFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getEquipFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getEquipModFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getRaceFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getSpellFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getTemplateFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getAbilityFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getFeatFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getAbilityCategoryFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getCompanionModFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getKitFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<CampaignSourceEntry> getClassFiles()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
