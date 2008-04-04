package pcgen.rules.persistence;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMAlignment;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.cdom.inst.CDOMBonusSpellLevel;
import pcgen.cdom.inst.CDOMCheck;
import pcgen.cdom.inst.CDOMDeity;
import pcgen.cdom.inst.CDOMDomain;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMFollower;
import pcgen.cdom.inst.CDOMLanguage;
import pcgen.cdom.inst.CDOMMasterRace;
import pcgen.cdom.inst.CDOMRace;
import pcgen.cdom.inst.CDOMShieldProf;
import pcgen.cdom.inst.CDOMSizeAdjustment;
import pcgen.cdom.inst.CDOMSkill;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.CDOMStat;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.cdom.inst.CDOMWeaponProf;
import pcgen.cdom.kit.CDOMKitAbility;
import pcgen.cdom.kit.CDOMKitAlignment;
import pcgen.cdom.kit.CDOMKitClass;
import pcgen.cdom.kit.CDOMKitDeity;
import pcgen.cdom.kit.CDOMKitFunds;
import pcgen.cdom.kit.CDOMKitGear;
import pcgen.cdom.kit.CDOMKitGender;
import pcgen.cdom.kit.CDOMKitKit;
import pcgen.cdom.kit.CDOMKitLevelAbility;
import pcgen.cdom.kit.CDOMKitName;
import pcgen.cdom.kit.CDOMKitRace;
import pcgen.cdom.kit.CDOMKitSelect;
import pcgen.cdom.kit.CDOMKitSkill;
import pcgen.cdom.kit.CDOMKitSpells;
import pcgen.cdom.kit.CDOMKitStat;
import pcgen.cdom.kit.CDOMKitTable;
import pcgen.cdom.kit.CDOMKitTemplate;
import pcgen.cdom.transition.CampaignInterface;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class SystemLoader extends Observable
{

	CDOMLoader<CDOMWeaponProf> wProfLoader = new CDOMTokenLoader<CDOMWeaponProf>(
			CDOMWeaponProf.class);
	CDOMLoader<CDOMArmorProf> aProfLoader = new CDOMTokenLoader<CDOMArmorProf>(
			CDOMArmorProf.class);
	CDOMLoader<CDOMShieldProf> sProfLoader = new CDOMTokenLoader<CDOMShieldProf>(
			CDOMShieldProf.class);
	CDOMLoader<CDOMSkill> skillLoader = new CDOMTokenLoader<CDOMSkill>(
			CDOMSkill.class);
	CDOMLoader<CDOMAbility> featLoader = new CDOMTokenLoader<CDOMAbility>(
			CDOMAbility.class);
	CDOMLoader<CDOMAbility> abilityLoader = new CDOMTokenLoader<CDOMAbility>(
			CDOMAbility.class);
	CDOMLoader<CDOMLanguage> languageLoader = new CDOMTokenLoader<CDOMLanguage>(
			CDOMLanguage.class);
	CDOMLoader<CDOMRace> raceLoader = new CDOMTokenLoader<CDOMRace>(
			CDOMRace.class);
	CDOMLoader<CDOMTemplate> templateLoader = new CDOMTokenLoader<CDOMTemplate>(
			CDOMTemplate.class);
	CDOMLoader<CDOMDomain> domainLoader = new CDOMTokenLoader<CDOMDomain>(
			CDOMDomain.class);
	CDOMLoader<CDOMDeity> deityLoader = new CDOMTokenLoader<CDOMDeity>(
			CDOMDeity.class);
	CDOMLoader<CDOMSpell> spellLoader = new CDOMTokenLoader<CDOMSpell>(
			CDOMSpell.class);
	CDOMLoader<CDOMEqMod> eqModLoader = new CDOMTokenLoader<CDOMEqMod>(
			CDOMEqMod.class);
	CDOMLoader<CDOMEquipment> equipmentLoader = new CDOMTokenLoader<CDOMEquipment>(
			CDOMEquipment.class);

	CDOMLoader<CDOMAbilityCategory> abilityCategoryLoader = new CDOMAbilityCategoryLoader();
	CDOMClassLstLoader classLoader = new CDOMClassLstLoader();

	CDOMLineLoader<CDOMSizeAdjustment> sizeLoader = new CDOMLineLoader<CDOMSizeAdjustment>(
			"SIZENAME", CDOMSizeAdjustment.class);
	CDOMKitLoader kitLoader = new CDOMKitLoader();

	private final CDOMCompositeLineLoader statsChecksLoader;
	private final CDOMCompositeLineLoader companionModLoader;

	public SystemLoader()
	{
		statsChecksLoader = new CDOMCompositeLineLoader();
		statsChecksLoader.addLineLoader(new CDOMLineLoader<CDOMStat>(
				"STATNAME", CDOMStat.class));
		statsChecksLoader.addLineLoader(new CDOMLineLoader<CDOMCheck>(
				"CHECKNAME", CDOMCheck.class));
		statsChecksLoader
				.addLineLoader(new CDOMLineLoader<CDOMBonusSpellLevel>(
						"BONUSSPELLLEVEL", CDOMBonusSpellLevel.class));
		statsChecksLoader.addLineLoader(new CDOMLineLoader<CDOMAlignment>(
				"ALIGNMENTNAME", CDOMAlignment.class));

		companionModLoader = new CDOMCompositeLineLoader();
		companionModLoader.addLineLoader(new CDOMLineLoader<CDOMFollower>(
				"FOLLOWER", CDOMFollower.class));
		companionModLoader.addLineLoader(new CDOMLineLoader<CDOMMasterRace>(
				"MASTERBONUSRACE", CDOMMasterRace.class));

		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitRace>("*KITTOKEN",
				"RACE", CDOMKitRace.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSkill>(
				"*KITTOKEN", "SKILL", CDOMKitSkill.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitGear>("*KITTOKEN",
				"GEAR", CDOMKitGear.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSpells>(
				"*KITTOKEN", "SPELLS", CDOMKitSpells.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitStat>("*KITTOKEN",
				"STAT", CDOMKitStat.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitAbility>(
				"*KITTOKEN", "FEAT", CDOMKitAbility.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitName>("*KITTOKEN",
				"NAME", CDOMKitName.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitAlignment>(
				"*KITTOKEN", "ALIGN", CDOMKitAlignment.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitLevelAbility>(
				"*KITTOKEN", "LEVELABILITY", CDOMKitLevelAbility.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitClass>(
				"*KITTOKEN", "CLASS", CDOMKitClass.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitTemplate>(
				"*KITTOKEN", "TEMPLATE", CDOMKitTemplate.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitDeity>(
				"*KITTOKEN", "DEITY", CDOMKitDeity.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitKit>("*KITTOKEN",
				"KIT", CDOMKitKit.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitTable>(
				"*KITTOKEN", "TABLE", CDOMKitTable.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitSelect>(
				"*KITTOKEN", "SELECT", CDOMKitSelect.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitGender>(
				"*KITTOKEN", "GENDER", CDOMKitGender.class));
		kitLoader.addLineLoader(new CDOMSubLineLoader<CDOMKitFunds>(
				"*KITTOKEN", "FUNDS", CDOMKitFunds.class));
	}

	public void loadCampaign(LoadContext context, CampaignInterface campaign)
	{
		// The first thing we need to do is load the
		// correct statsandchecks.lst file for this gameMode
		File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(),
				"gameModes");
		List<String> modes = campaign.getGameModes();
		if (modes.isEmpty())
		{
			Logging.errorPrint("Campaigns provided are not compatible");
			return;
		}
		//TODO This is a temporary hack for ReportUnconstructed ... need a better method
		GameMode mode = SystemCollections.getGameModeNamed(modes.get(0));
		File specificGameModeDir = new File(gameModeDir, mode.getFolderName());
		File statsAndChecks = new File(specificGameModeDir,
				"statsandchecks.lst");
		statsChecksLoader.loadLstFile(context, statsAndChecks.toURI());
		File sizeAdjustment = new File(specificGameModeDir,
				"sizeadjustment.lst");
		sizeLoader.loadLstFile(context, sizeAdjustment.toURI());

		// Notify our observers of how many files we intend
		// to load in total so that they can set up any
		// progress meters that they want to.
		setChanged();
		notifyObservers(Integer.valueOf(campaign.countTotalFiles()));

		// load ability categories first as they used to only be at the game
		// mode
		abilityCategoryLoader.loadLstFiles(context, campaign
				.getAbilityCategoryFiles());
		for (CDOMAbilityCategory cat : CDOMAbilityCategory.getAllConstants())
		{
			context.ref.importObject(cat);
		}

		// load weapon profs first
		wProfLoader.loadLstFiles(context, campaign.getWeaponProfFiles());
		aProfLoader.loadLstFiles(context, campaign.getArmorProfFiles());
		sProfLoader.loadLstFiles(context, campaign.getShieldProfFiles());

		// load skills before classes to handle class skills
		skillLoader.loadLstFiles(context, campaign.getSkillFiles());

		// load before races to handle auto known languages
		languageLoader.loadLstFiles(context, campaign.getLanguageFiles());

		// load before race or class to handle abilities
		abilityLoader.loadLstFiles(context, campaign.getAbilityFiles());

		// load before race or class to handle feats
		featLoader.loadLstFiles(context, campaign.getFeatFiles());

		raceLoader.loadLstFiles(context, campaign.getRaceFiles());

		// Domain must load before CLASS - thpr 10/29/06
		domainLoader.loadLstFiles(context, campaign.getDomainFiles());

		spellLoader.loadLstFiles(context, campaign.getSpellFiles());
		deityLoader.loadLstFiles(context, campaign.getDeityFiles());

		classLoader.loadLstFiles(context, campaign.getClassFiles());

		templateLoader.loadLstFiles(context, campaign.getTemplateFiles());

		// loaded before equipment (required)
		eqModLoader.loadLstFiles(context, campaign.getEquipModFiles());

		equipmentLoader.loadLstFiles(context, campaign.getEquipFiles());

		companionModLoader.loadLstFiles(context, campaign
				.getCompanionModFiles());
		kitLoader.loadLstFiles(context, campaign.getKitFiles());

		//
		// // Load the bio settings files
		// bioLoader.loadLstFiles(bioSetFileList);
		//
		// // Check for the required skills
		// if (reqSkillFileList != null)
		// {
		// addToGlobals(LstConstants.REQSKILL_TYPE, reqSkillFileList);
		// }
		// checkRequiredSkills();
		//
		// // Check for the default deities
		// checkRequiredDeities();
		//
		// // Add default EQ mods
		// eqModLoader.addDefaultEquipmentMods();
		//
		// // Load custom items
		// loadCustomItems();
		//
		// // Check for valid race types
		// // checkRaceTypes();
		//
		// // Verify weapons are melee or ranged
		// verifyWeaponsMeleeOrRanged();
		// verifyFavClassSyntax();
		//
		// // Auto-gen additional equipment
		// if (!SettingsHandler.wantToLoadMasterworkAndMagic())
		// {
		// EquipmentList.autoGenerateEquipment();
		// }
		context.resolveDeferredTokens();
		context.ref.buildDerivedObjects();
		context.ref.validate();
		context.resolveReferences();
	}

	public void unloadCampaign(LoadContext context, CampaignInterface campaign,
			String outputDirectory)
	{
		URI out = new File(outputDirectory).toURI();

		// File gameModeDir = new File(SettingsHandler.getPcgenSystemDir(),
		// "gameModes");
		// File specificGameModeDir = new File(gameModeDir, SettingsHandler
		// .getGame().getFolderName());
		// File statsAndChecks = new File(specificGameModeDir,
		// "statsandchecks.lst");
		// statsChecksLoader.loadLstFile(context, statsAndChecks.toURI());
		// File sizeAdjustment = new File(specificGameModeDir,
		// "sizeadjustment.lst");
		// sizeLoader.loadLstFile(context, sizeAdjustment.toURI());

		// abilityCategoryLoader.loadLstFiles(context, campaign
		// .getAbilityCategoryFiles());

		wProfLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getWeaponProfFiles()));
		aProfLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getShieldProfFiles()));
		sProfLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getShieldProfFiles()));
		skillLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getSkillFiles()));
		languageLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getLanguageFiles()));
		abilityLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getAbilityFiles()));
		featLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getFeatFiles()));
		raceLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getRaceFiles()));
		domainLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getDomainFiles()));
		spellLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getSpellFiles()));
		deityLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getDeityFiles()));
		classLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getClassFiles()));
		templateLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getTemplateFiles()));
		eqModLoader.unloadLstFiles(context, prepare(campaign.getSourceURI(),
				out, campaign.getEquipModFiles()));
		equipmentLoader.unloadLstFiles(context, prepare(
				campaign.getSourceURI(), out, campaign.getEquipFiles()));

		// companionModLoader.unloadLstFiles(context, campaign
		// .getCompanionModFiles());
		// kitLoader.unloadLstFiles(context, campaign.getKitFiles());

		// TODO Auto-generated method stub

	}

	private List<CampaignSourceEntry> prepare(URI sourceURI, URI out,
			Collection<CampaignSourceEntry> incoming)
	{
		String source = sourceURI.toString();
		int slashLoc = source.lastIndexOf('/');
		String uriBase = source.substring(0, slashLoc);
		List<CampaignSourceEntry> list = new ArrayList<CampaignSourceEntry>();
		for (CampaignSourceEntry cse : incoming)
		{
			String uriLocal = cse.getURI().toString();
			if (uriLocal.startsWith(uriBase))
			{
				String relative = uriLocal.substring(uriBase.length());
				try
				{
					URI uri = new URI(out + relative);
					cse.setWriteURI(uri);
					list.add(cse);
				}
				catch (URISyntaxException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				Logging.errorPrint("Skipping: " + uriLocal);
			}
		}
		return list;
	}
}
