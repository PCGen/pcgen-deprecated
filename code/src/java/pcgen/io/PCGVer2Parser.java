/*
 * PCGVer2Parser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on March 22, 2002, 12:15 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.io;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Campaign;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.FeatMultipleChoice;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.NoteItem;
import pcgen.core.PCClass;
import pcgen.core.PCSpell;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SpecialAbility;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.levelability.LevelAbility;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;
import pcgen.gui.GuiConstants;
import pcgen.io.parsers.CharacterDomainParser;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.TokenStore;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>PCGVer2Parser</code>
 * Parses a line oriented format.
 * Each line should adhere to the following grammar:<br>
 *
 * <i>line</i> := EMPTY | <i>comment</i> | <i>taglist</i>
 * <i>comment</i> := '#' STRING
 * <i>taglist</i> := tag ('|' tag)*
 * <i>tag</i> := simpletag | nestedtag
 * <i>nestedtag</i> := TAGNAME ':' '[' taglist ']'
 * <i>simpletag</i> := TAGNAME ':' TAGVALUE
 *
 *
 * @author Thomas Behr 22-03-02
 * @version $Revision$
 */
final class PCGVer2Parser implements PCGParser, IOConstants
{
	/**
	 * DO NOT CHANGE line separator.
	 * Need to keep the Unix line separator to ensure cross-platform portability.
	 *
	 * author: Thomas Behr 2002-11-13
	 */
	private final List<String> warnings = new ArrayList<String>();
	private Cache cache;
	private final List<String> weaponprofs = new ArrayList<String>();
	private PlayerCharacter thePC;
	private final Set<String> seenStats = new HashSet<String>();

	//
	// MAJOR.MINOR.REVISION
	//
	private int[] pcgenVersion = {0, 0, 0};
	private String pcgenVersionSuffix;
	private boolean calcFeatPoolAfterLoad = false;
	private double baseFeatPool = 0.0;
	private boolean featsPresent = false;

	/**
	 * Constructor
	 * @param aPC
	 */
	PCGVer2Parser(PlayerCharacter aPC)
	{
		thePC = aPC;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 22-03-02
	 *
	 * @return a list of warning messages
	 */
	public List<String> getWarnings()
	{
		return warnings;
	}

	/**
	 * parse a String in PCG format
	 *
	 * <br>author: Thomas Behr 20-07-02
	 *
	 * @param lines   the String to parse
	 * @throws PCGParseException
	 */
	public void parsePCG(String[] lines) throws PCGParseException
	{
		initCache(lines.length);

		for (int i = 0; i < lines.length; ++i)
		{
			if ((lines[i].trim().length() > 0) && !isComment(lines[i]))
			{
				cacheLine(lines[i].trim());
			}
		}

		parseCachedLines();
	}

	/*
	 * ###############################################################
	 * Miscellaneous methods
	 * ###############################################################
	 */

	/**
	 * Convenience Method
	 *
	 * <br>author: Thomas Behr 28-04-02
	 *
	 * @param line
	 * @return true if it is a comment
	 */
	private static boolean isComment(String line)
	{
		return line.trim().startsWith(TAG_COMMENT);
	}

	/*
	 * Given a Source string and Target string,
	 * return a List of BonusObj's
	 */
	private List<BonusObj> getBonusFromName(String sName, String tName)
	{
		//sName = SPELL=Haste
		//tName = PC
		String sourceStr = sName.substring(TAG_TEMPBONUS.length() + 1);
		String targetStr = tName.substring(TAG_TEMPBONUSTARGET.length() + 1);
		Object oSource = null;
		Object oTarget = null;

		if (sourceStr.startsWith(TAG_FEAT + '='))
		{
			sourceStr = sourceStr.substring(5);
			oSource = thePC.getFeatKeyed(sourceStr);
		}
		else if (sourceStr.startsWith(TAG_SPELL + '='))
		{
			sourceStr = sourceStr.substring(6);

			//oSource = aPC.getSpellNamed(sourceStr);
			oSource = Globals.getSpellKeyed(sourceStr);
		}
		else if (sourceStr.startsWith(TAG_EQUIPMENT + '='))
		{
			sourceStr = sourceStr.substring(10);
			oSource = thePC.getEquipmentNamed(sourceStr);
		}
		else if (sourceStr.startsWith(TAG_CLASS + '='))
		{
			sourceStr = sourceStr.substring(6);
			oSource = thePC.getClassKeyed(sourceStr);
		}
		else if (sourceStr.startsWith(TAG_TEMPLATE + '='))
		{
			sourceStr = sourceStr.substring(9);
			oSource = thePC.getTemplateKeyed(sourceStr);
		}
		else if (sourceStr.startsWith(TAG_SKILL + '='))
		{
			sourceStr = sourceStr.substring(6);
			oSource = thePC.getSkillKeyed(sourceStr);
		}
		else
		{
			// TODO Error message
			// Hmm, not a supported type
		}

		if (oSource != null)
		{
			sourceStr = ((PObject) oSource).getKeyName();
		}

		if (targetStr.equals(TAG_PC))
		{
			targetStr = thePC.getName();
		}
		else
		{
			oTarget = thePC.getEquipmentNamed(targetStr);
			targetStr = ((PObject) oTarget).getDisplayName();
		}

		List<BonusObj> aList = thePC.getTempBonusList(sourceStr, targetStr);

		return aList;
	}

	private PCTemplate addKeyedTemplate(final String templateKey)
	{
		PCTemplate aPCTemplate =
				Globals.getTemplateKeyed(EntityEncoder.decode(templateKey));

		if (aPCTemplate != null)
		{
			final int preXP = thePC.getXP();
			aPCTemplate = thePC.addTemplate(aPCTemplate);

			//
			// XP written to file contains leveladjustment XP. If template modifies XP, then
			// it will have already been added into total. Need to make sure it is not doubled.
			//
			if (thePC.getXP() != preXP)
			{
				thePC.setXP(preXP);
			}
		}

		return aPCTemplate;
	}

	private void cacheLine(String s)
	{
		cache.put(s.substring(0, s.indexOf(':')), s);
	}

	private void checkSkillPools()
	{
		int skillPoints = 0;

		for (final PCClass pcClass : thePC.getClassList())
		{
			skillPoints += pcClass.getSkillPool(thePC);
		}

		thePC.setSkillPoints(skillPoints);
	}

	private void checkStats() throws PCGParseException
	{
		if (seenStats.size() != SettingsHandler.getGame().s_ATTRIBSHORT.length)
		{
			final String message =
					PropertyFactory.getFormattedString(
						"Exceptions.PCGenParser.WrongNumAttributes", //$NON-NLS-1$
						seenStats.size(),
						SettingsHandler.getGame().s_ATTRIBSHORT.length);
			throw new PCGParseException("parseStatLines", "N/A", message); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	/*
	 * ###############################################################
	 * private helper methods
	 * ###############################################################
	 */
	private void initCache(int capacity)
	{
		cache = new Cache((capacity * 4) / 3);
	}

	private void parseAgeLine(String line)
	{
		try
		{
			thePC
				.setAge(Integer.parseInt(line.substring(TAG_AGE.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalAgeLine", //$NON-NLS-1$
						line);
			warnings.add(message);
		}
	}

	private void parseAgeSet(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, TAG_END, false);
		int i = 0;
		aTok.nextToken(); // skip tag

		while (aTok.hasMoreTokens() && (i < 10))
		{
			thePC.setHasMadeKitSelectionForAgeSet(i++, aTok.nextToken().equals(
				"1")); //$NON-NLS-1$
		}
	}

	private void parseAlignmentLine(String line)
	{
		final String alignment = line.substring(TAG_ALIGNMENT.length() + 1);
		final int i = SettingsHandler.getGame().getIndexOfAlignment(alignment);

		if (i >= 0)
		{
			thePC.setAlignment(i, true);

			return;
		}

		final String message =
				PropertyFactory.getFormattedString(
					"Warnings.PCGenParser.IllegalAlignment", //$NON-NLS-1$
					line);
		warnings.add(message);
	}

	private void parseArmorProfLine(String line)
	{
		final StringTokenizer stok =
				new StringTokenizer(line.substring(TAG_ARMORPROF.length() + 1),
					TAG_END, false);

		// should be in the form ARMORPROF:objectype=name:prof:prof:prof:prof:etc.
		final String objecttype = stok.nextToken();
		final String objectname =
				objecttype.substring(objecttype.indexOf('=') + 1);
		final List<String> aList = new ArrayList<String>();

		while (stok.hasMoreTokens())
		{
			aList.add(stok.nextToken());
		}

		if (objecttype.startsWith(TAG_DEITY))
		{
			if (thePC.getDeity() != null)
			{
				thePC.getDeity().addSelectedArmorProfs(aList);
			}
		}
		else if (objecttype.startsWith(TAG_CLASS))
		{
			final PCClass aClass = thePC.getClassKeyed(objectname);

			if (aClass != null)
			{
				aClass.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_CLASS, objectname);
			}
		}
		else if (objecttype.startsWith(TAG_FEAT))
		{
			final Ability aFeat = thePC.getFeatNamed(objectname);

			if (aFeat != null)
			{
				aFeat.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_FEAT, objectname);
			}
		}
		else if (objecttype.startsWith(TAG_SKILL))
		{
			final Skill aSkill = thePC.getSkillKeyed(objectname);

			if (aSkill != null)
			{
				aSkill.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_SKILL, objectname);
			}
		}
		else if (objecttype.startsWith(TAG_DOMAIN))
		{
			final Domain aDomain = thePC.getCharacterDomainKeyed(objectname);

			if (aDomain != null)
			{
				aDomain.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_DOMAIN, objectname);
			}
		}
		else if (objecttype.startsWith(TAG_EQUIPMENT))
		{
			final Equipment eq = thePC.getEquipmentNamed(objectname);

			if (eq != null)
			{
				eq.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_EQUIPMENT, objectname);
			}
		}
		else if (objecttype.startsWith(TAG_TEMPLATE))
		{
			final PCTemplate aTemplate = thePC.getTemplateKeyed(objectname);

			if (aTemplate != null)
			{
				aTemplate.addSelectedArmorProfs(aList);
			}
			else
			{
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.ObjectNotFound", //$NON-NLS-1$
					line, TAG_TEMPLATE, objectname);
			}
		}
		else
		{
			Logging.errorPrintLocalised("Errors.PCGenParser.UnknownObject", //$NON-NLS-1$
				line);

			return; // no object type found
		}
	}

	/**
	 * # Auto sort gear
	 * @param line
	 **/
	private void parseAutoSortGearLine(String line)
	{
		thePC.setAutoSortGear(line.endsWith(VALUE_Y));
	}

	/**
	 * # Auto sort skills - transition only, line is no longer saved
	 * @param line
	 **/
	private void parseAutoSortSkillsLine(String line)
	{
		if (line.endsWith(VALUE_Y))
		{
			thePC
				.setSkillsOutputOrder(GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC);
		}
		else
		{
			thePC
				.setSkillsOutputOrder(GuiConstants.INFOSKILLS_OUTPUT_BY_MANUAL);
		}
	}

	/**
	 * # Auto known spells
	 * @param line
	 **/
	private void parseAutoSpellsLine(String line)
	{
		thePC.setAutoSpells(line.endsWith(VALUE_Y));
	}

	/**
	 * Process the Use Higher Known Spell Slot line.
	 * @param buffer The buffer to append to.
	 */
	private void parseUseHigherKnownSpellSlotsLine(String line)
	{
		thePC.setUseHigherKnownSlots(line.endsWith(VALUE_Y));
	}

	/**
	 * Process the Use Higher Prepped Spell Slot line.
	 * @param buffer The buffer to append to.
	 */
	private void parseUseHigherPreppedSpellSlotsLine(String line)
	{
		thePC.setUseHigherPreppedSlots(line.endsWith(VALUE_Y));
	}

	private void parseBirthdayLine(String line)
	{
		thePC.setBirthday(EntityEncoder.decode(line.substring(TAG_BIRTHDAY
			.length() + 1)));
	}

	private void parseBirthplaceLine(String line)
	{
		thePC.setBirthplace(EntityEncoder.decode(line.substring(TAG_BIRTHPLACE
			.length() + 1)));
	}

	/**
	 * Does the actual work:<br>
	 * Retrieves cached lines and parses each line.
	 *
	 * Note: May have to change parse order!
	 *
	 * <br>author: Thomas Behr 31-07-02
	 *
	 * @throws PCGParseException
	 */
	private void parseCachedLines() throws PCGParseException
	{
		if (cache.containsKey(TAG_GAMEMODE))
		{
			parseGameMode(cache.get(TAG_GAMEMODE).get(0));
		}

		/*
		 * #System Information
		 * CAMPAIGN:CMP - Monkey Book I - Book For Monkeys
		 * CAMPAIGN:CMP - Monkey Book II - Book By Monkeys
		 * ...
		 *
		 * first thing to do is checking campaigns - no matter what!
		 */
		if (cache.containsKey(TAG_CAMPAIGN))
		{
			parseCampaignLines(cache.get(TAG_CAMPAIGN));
		}

		/*
		 * VERSION:x.x.x
		 */
		if (cache.containsKey(TAG_VERSION))
		{
			parseVersionLine(cache.get(TAG_VERSION).get(0));
		}

		/*
		 * #Character Attributes
		 * STAT:STR=18
		 * STAT:DEX=18
		 * STAT:CON=18
		 * STAT:INT=18
		 * STAT:WIS=18
		 * STAT:CHA=18
		 * ALIGN:LG
		 * RACE:Human
		 */
		if (cache.containsKey(TAG_STAT))
		{
			for (final String stat : cache.get(TAG_STAT))
			{
				parseStatLine(stat);
			}

			checkStats();
		}

		if (cache.containsKey(TAG_ALIGNMENT))
		{
			parseAlignmentLine(cache.get(TAG_ALIGNMENT).get(0));
		}

		/*
		 * # Kits - Just adds a reference to the character that the template
		 * was picked.
		 */
		if (cache.containsKey(TAG_KIT))
		{
			for (final String line : cache.get(TAG_KIT))
			{
				parseKitLine(line);
			}
		}

		if (cache.containsKey(TAG_RACE))
		{
			parseRaceLine(cache.get(TAG_RACE).get(0));
		}

		/*
		 * #System Information
		 * CAMPAIGNS:>:-delimited list<
		 * VERSION:x.x.x
		 * ROLLMETHOD:xxx
		 * PURCHASEPOINTS:Y or N|TYPE:>living City, Living greyhawk, etc<
		 * UNLIMITEDPOOLCHECKED:Y or N
		 * POOLPOINTS:>numeric value 0-?<
		 * POOLPOINTSAVAIL:>numeric value 0-?<
		 * GAMEMODE:DnD
		 * TABLABEL:0
		 * AUTOSPELLS:Y or N
		 * LOADCOMPANIONS:Y or N
		 * USETEMPMODS:Y or N
		 * AUTOSORTGEAR:Y or N
		 * SKILLSOUTPUTORDER:0
		 */
		if (cache.containsKey(TAG_POOLPOINTS))
		{
			parsePoolPointsLine(cache.get(TAG_POOLPOINTS).get(0));
		}
		if (cache.containsKey(TAG_POOLPOINTSAVAIL))
		{
			parsePoolPointsLine2(cache.get(TAG_POOLPOINTSAVAIL).get(0));
		}

		if (cache.containsKey(TAG_AUTOSPELLS))
		{
			parseAutoSpellsLine(cache.get(TAG_AUTOSPELLS).get(0));
		}

		if (cache.containsKey(TAG_USEHIGHERKNOWN))
		{
			parseUseHigherKnownSpellSlotsLine(cache.get(TAG_USEHIGHERKNOWN)
				.get(0));
		}
		if (cache.containsKey(TAG_USEHIGHERPREPPED))
		{
			parseUseHigherPreppedSpellSlotsLine(cache.get(TAG_USEHIGHERPREPPED)
				.get(0));
		}

		if (cache.containsKey(TAG_LOADCOMPANIONS))
		{
			parseLoadCompanionLine(cache.get(TAG_LOADCOMPANIONS).get(0));
		}

		if (cache.containsKey(TAG_USETEMPMODS))
		{
			parseUseTempModsLine(cache.get(TAG_USETEMPMODS).get(0));
		}

		if (cache.containsKey(TAG_HTMLOUTPUTSHEET))
		{
			parseHTMLOutputSheetLine(cache.get(TAG_HTMLOUTPUTSHEET).get(0));
		}

		if (cache.containsKey(TAG_PDFOUTPUTSHEET))
		{
			parsePDFOutputSheetLine(cache.get(TAG_PDFOUTPUTSHEET).get(0));
		}

		if (cache.containsKey(TAG_AUTOSORTGEAR))
		{
			parseAutoSortGearLine(cache.get(TAG_AUTOSORTGEAR).get(0));
		}

		if (cache.containsKey(TAG_AUTOSORTSKILLS))
		{
			parseAutoSortSkillsLine(cache.get(TAG_AUTOSORTSKILLS).get(0));
		}

		if (cache.containsKey(TAG_SKILLSOUTPUTORDER))
		{
			parseSkillsOutputOrderLine(cache.get(TAG_SKILLSOUTPUTORDER).get(0));
		}

		/*
		 * #Character Class(es)
		 * CLASS:Fighter|LEVEL=3
		 * CLASSABILITIESLEVEL:Fighter=1(>This would only display up to the level the character has already,)
		 * CLASSABILITIESLEVEL:Fighter=2(>with any special abilities not covered by other areas,)
		 * CLASSABILITIESLEVEL:Fighter=3(>such as skills, feats, etc., but would list SA's, and the like<)
		 * CLASS:Wizard|LEVEL=1
		 * CLASSABILITIESLEVEL:Wizard=1(SA's, MEMORIZE:Y, etc)
		 */
		if (cache.containsKey(TAG_CLASS))
		{
			for (String line : cache.get(TAG_CLASS))
			{
				parseClassLine(line);
			}

			checkSkillPools();
		}

		final List<PCLevelInfo> pcLevelInfoList =
				new ArrayList<PCLevelInfo>(thePC.getLevelInfo());
		thePC.getLevelInfo().clear();
		if (cache.containsKey(TAG_CLASSABILITIESLEVEL))
		{
			for (String line : cache.get(TAG_CLASSABILITIESLEVEL))
			{
				parseClassAbilitiesLevelLine(line, pcLevelInfoList);
			}
		}

		/*
		 * #Character Experience
		 * EXPERIENCE:6000
		 */
		if (cache.containsKey(TAG_EXPERIENCE))
		{
			parseExperienceLine(cache.get(TAG_EXPERIENCE).get(0));
		}

		/*
		 * #Character Templates
		 * TEMPLATESAPPLIED:If any, else this would just have the comment line, and skip to the next
		 */
		if (cache.containsKey(TAG_TEMPLATESAPPLIED))
		{
			for (String line : cache.get(TAG_TEMPLATESAPPLIED))
			{
				parseTemplateLine(line);
			}
		}

		if (cache.containsKey(TAG_REGION))
		{
			for (String line : cache.get(TAG_REGION))
			{
				parseRegionLine(line);
			}
		}

		/*
		 * ###############################################################
		 * Character Skills methods
		 * ###############################################################
		 */
		/*
		 * #Character Skills
		 * CLASSBOUGHT:Fighter
		 * SKILL:Alchemy|CLASS:N|COST:2|RANK:7  (Should be Obvious what each of these does, I hope ;p)
		 * SKILL:Survival|CLASS:Y|COST:1|SYNERGY:Wilderness Lore=5=2|RANK:10
		 * CLASSBOUGHT:Wizard
		 * SKILL:Spellcraft|CLASS:Y|COST:1|RANK:7
		 *
		 * CLASSBOUGHT not supported
		 */
		if (cache.containsKey(TAG_SKILL))
		{
			for (final String line : cache.get(TAG_SKILL))
			{
				parseSkillLine(line);
			}
		}

		/*
		 * #Character Languages
		 * LANGUAGE:Chondathan|LANGUAGE:Common|LANGUAGE:Literacy
		 */
		if (cache.containsKey(TAG_LANGUAGE))
		{
			for (final String line : cache.get(TAG_LANGUAGE))
			{
				parseLanguageLine(line);
			}
		}

		/*
		 * Anything that is already Pipe Delimited should be in
		 * parenthesis to avoid confusion on PCGen's part
		 *
		 * #Character Feats
		 * FEAT:Alertness|TYPE:General|(BONUS:SKILL|Listen,Spot|2)|DESC:+2 on Listen and Spot checks
		 * FEATPOOL:>number of remaining feats<
		 */
		if (cache.containsKey(TAG_FEAT))
		{
			for (final String line : cache.get(TAG_FEAT))
			{
				parseFeatLine(line);
			}
		}

		if (cache.containsKey(TAG_VFEAT))
		{
			for (final String line : cache.get(TAG_VFEAT))
			{
				parseVFeatLine(line);
			}
		}

		if (cache.containsKey(TAG_FEATPOOL))
		{
			for (final String line : cache.get(TAG_FEATPOOL))
			{
				parseFeatPoolLine(line);
			}
		}

		if (cache.containsKey(TAG_ABILITY))
		{
			for (final String line : cache.get(TAG_ABILITY))
			{
				parseAbilityLine(line);
			}
		}

		if (cache.containsKey(TAG_USERPOOL))
		{
			for (final String line : cache.get(TAG_USERPOOL))
			{
				parseUserPoolLine(line);
			}
		}

		/*
		 * Contains information about PC's equipment
		 * Money goes here as well
		 *
		 * #Character Equipment
		 * EQUIPNAME:Longsword|OUTPUTORDER:1|COST:5|WT:5|NOTE:It's very sharp!|>other info<
		 * EQUIPNAME:Backpack|OUTPUTORDER:-1|COST:5|WT:5|NOTE:on my back
		 * EQUIPNAME:Rope (Silk)|OUTPUTORDER:3|COST:5|WT:5
		 */
		if (cache.containsKey(TAG_MONEY))
		{
			for (final String line : cache.get(TAG_MONEY))
			{
				parseMoneyLine(line);
			}
		}

		if (cache.containsKey(TAG_EQUIPNAME))
		{
			for (final String line : cache.get(TAG_EQUIPNAME))
			{
				parseEquipmentLine(line);
			}
		}

		if (cache.containsKey(TAG_EQUIPSET))
		{
			/*
			 * strangely enough this works even if we create a
			 * EquipSet for content whose container EquipSet
			 * has not been created yet
			 * author: Thomas Behr 10-09-02
			 *
			 * Comment from EquipSet author:
			 * It only works because I've already sorted on output
			 * in PCGVer2Creator
			 * author: Jayme Cox 01-16-03
			 *
			 */

			//Collections.sort(cache.get(TAG_EQUIPSET), new EquipSetLineComparator());
			for (final String line : cache.get(TAG_EQUIPSET))
			{
				parseEquipmentSetLine(line);
			}
		}

		/**
		 * CALCEQUIPSET line contains the "working" equipment list
		 **/
		if (cache.containsKey(TAG_CALCEQUIPSET))
		{
			for (final String line : cache.get(TAG_CALCEQUIPSET))
			{
				parseCalcEquipSet(line);
			}
		}

		/*
		 * #Character Deity/Domain
		 * DEITY:Yondalla|DEITYDOMAINS:[DOMAIN:Good|DOMAIN:Law|DOMAIN:Protection]|ALIGNALLOW:013|DESC:Halflings, Protection, Fertility|SYMBOL:None|DEITYFAVWEAP:Sword (Short)|DEITYALIGN:ALIGN:LG
		 * DOMAIN:GOOD|DOMAINGRANTS:>list of abilities<
		 * DOMAINSPELLS:GOOD|SPELLLIST:[SPELL:bla|SPELL:blubber|...]
		 */
		if (cache.containsKey(TAG_DEITY))
		{
			for (final String line : cache.get(TAG_DEITY))
			{
				parseDeityLine(line);
			}
		}

		if (cache.containsKey(TAG_DOMAIN))
		{
			for (final String line : cache.get(TAG_DOMAIN))
			{
				parseDomainLine(line);
			}
		}

		if (cache.containsKey(TAG_DOMAINSPELLS))
		{
			for (final String line : cache.get(TAG_DOMAINSPELLS))
			{
				parseDomainSpellsLine(line);
			}
		}

		if (cache.containsKey(TAG_SPELLBOOK))
		{
			for (final String line : cache.get(TAG_SPELLBOOK))
			{
				parseSpellBookLines(line);
			}
		}
		/*
		 * This one is what will make spellcasters U G L Y!!!
		 *
		 * #Character Spells Information
		 * CLASS:Wizard|CANCASTPERDAY:2,4(Totals the levels all up + includes attribute bonuses)
		 * SPELLNAME:Blah|SCHOOL:blah|SUBSCHOOL:blah|Etc
		 */
		if (cache.containsKey(TAG_SPELLLIST))
		{
			for (final String line : cache.get(TAG_SPELLLIST))
			{
				parseSpellListLines(line);
			}
		}

		if (cache.containsKey(TAG_SPELLNAME))
		{
			for (final String line : cache.get(TAG_SPELLNAME))
			{
				parseSpellLine(line);
			}

			sortCharacterSpells();
		}

		/*
		 * #Character Description/Bio/Historys
		 * CHARACTERBIO:any text that's in the BIO field
		 * CHARACTERDESC:any text that's in the BIO field
		 */
		if (cache.containsKey(TAG_CHARACTERBIO))
		{
			parseCharacterBioLine(cache.get(TAG_CHARACTERBIO).get(0));
		}

		if (cache.containsKey(TAG_CHARACTERDESC))
		{
			parseCharacterDescLine(cache.get(TAG_CHARACTERDESC).get(0));
		}

		if (cache.containsKey(TAG_CHARACTERCOMP))
		{
			for (final String line : cache.get(TAG_CHARACTERCOMP))
			{
				parseCharacterCompLine(line);
			}
		}

		if (cache.containsKey(TAG_CHARACTERASSET))
		{
			for (final String line : cache.get(TAG_CHARACTERASSET))
			{
				parseCharacterAssetLine(line);
			}
		}

		if (cache.containsKey(TAG_CHARACTERMAGIC))
		{
			for (final String line : cache.get(TAG_CHARACTERMAGIC))
			{
				parseCharacterMagicLine(line);
			}
		}

		/*
		 * #Character Master/Followers
		 * MASTER:Mynex|TYPE:Follower|HITDICE:20|FILE:E$\DnD\dnd-chars\ravenlock.pcg
		 * FOLLOWER:Raven|TYPE:Animal Companion|HITDICE:5|FILE:E$\DnD\dnd-chars\raven.pcg
		 */
		if (cache.containsKey(TAG_MASTER))
		{
			for (final String line : cache.get(TAG_MASTER))
			{
				parseMasterLine(line);
			}
		}

		if (cache.containsKey(TAG_FOLLOWER))
		{
			for (final String line : cache.get(TAG_FOLLOWER))
			{
				parseFollowerLine(line);
			}
		}

		/*
		 * #Character Notes Tab
		 */
		if (cache.containsKey(TAG_NOTE))
		{
			for (final String line : cache.get(TAG_NOTE))
			{
				parseNoteLine(line);
			}
		}

		/*
		 * #Character Bio
		 * CHARACTERNAME:Code Monkey
		 * TABNAME:Code Monkey the Best Ever No Really!
		 * PLAYERNAME:Jason Monkey
		 * HEIGHT:75
		 * WEIGHT:198
		 * AGE:17
		 * GENDER:text
		 * HANDED:text
		 * SKIN:text
		 * EYECOLOR:text
		 * HAIRCOLOR:text
		 * HAIRSTYLE:text
		 * LOCATION:text
		 * CITY:text
		 * PERSONALITYTRAIT1:text
		 * PERSONALITYTRAIT2:text
		 * SPEECHPATTERN:text
		 * PHOBIAS:text
		 * INTERESTS:text
		 * CATCHPHRASE:text
		 */
		if (cache.containsKey(TAG_CHARACTERNAME))
		{
			parseCharacterNameLine(cache.get(TAG_CHARACTERNAME).get(0));
		}

		if (cache.containsKey(TAG_TABNAME))
		{
			parseTabNameLine(cache.get(TAG_TABNAME).get(0));
		}

		if (cache.containsKey(TAG_PLAYERNAME))
		{
			parsePlayerNameLine(cache.get(TAG_PLAYERNAME).get(0));
		}

		if (cache.containsKey(TAG_HEIGHT))
		{
			parseHeightLine(cache.get(TAG_HEIGHT).get(0));
		}

		if (cache.containsKey(TAG_WEIGHT))
		{
			parseWeightLine(cache.get(TAG_WEIGHT).get(0));
		}

		if (cache.containsKey(TAG_AGE))
		{
			parseAgeLine(cache.get(TAG_AGE).get(0));
		}

		if (cache.containsKey(TAG_GENDER))
		{
			parseGenderLine(cache.get(TAG_GENDER).get(0));
		}

		if (cache.containsKey(TAG_HANDED))
		{
			parseHandedLine(cache.get(TAG_HANDED).get(0));
		}

		if (cache.containsKey(TAG_SKINCOLOR))
		{
			parseSkinColorLine(cache.get(TAG_SKINCOLOR).get(0));
		}

		if (cache.containsKey(TAG_EYECOLOR))
		{
			parseEyeColorLine(cache.get(TAG_EYECOLOR).get(0));
		}

		if (cache.containsKey(TAG_HAIRCOLOR))
		{
			parseHairColorLine(cache.get(TAG_HAIRCOLOR).get(0));
		}

		if (cache.containsKey(TAG_HAIRSTYLE))
		{
			parseHairStyleLine(cache.get(TAG_HAIRSTYLE).get(0));
		}

		if (cache.containsKey(TAG_LOCATION))
		{
			parseLocationLine(cache.get(TAG_LOCATION).get(0));
		}

		//this tag is obsolete, but left in for backward-compatibility, replaced by TAG_CITY
		if (cache.containsKey(TAG_RESIDENCE))
		{
			parseResidenceLine(cache.get(TAG_RESIDENCE).get(0));
		}

		if (cache.containsKey(TAG_CITY))
		{
			parseCityLine(cache.get(TAG_CITY).get(0));
		}

		if (cache.containsKey(TAG_BIRTHDAY))
		{
			parseBirthdayLine(cache.get(TAG_BIRTHDAY).get(0));
		}

		if (cache.containsKey(TAG_BIRTHPLACE))
		{
			parseBirthplaceLine(cache.get(TAG_BIRTHPLACE).get(0));
		}

		if (cache.containsKey(TAG_PERSONALITYTRAIT1))
		{
			for (final String line : cache.get(TAG_PERSONALITYTRAIT1))
			{
				parsePersonalityTrait1Line(line);
			}
		}

		if (cache.containsKey(TAG_PERSONALITYTRAIT2))
		{
			for (final String line : cache.get(TAG_PERSONALITYTRAIT2))
			{
				parsePersonalityTrait2Line(line);
			}
		}

		if (cache.containsKey(TAG_SPEECHPATTERN))
		{
			parseSpeechPatternLine(cache.get(TAG_SPEECHPATTERN).get(0));
		}

		if (cache.containsKey(TAG_PHOBIAS))
		{
			parsePhobiasLine(cache.get(TAG_PHOBIAS).get(0));
		}

		if (cache.containsKey(TAG_INTERESTS))
		{
			parseInterestsLine(cache.get(TAG_INTERESTS).get(0));
		}

		if (cache.containsKey(TAG_CATCHPHRASE))
		{
			parseCatchPhraseLine(cache.get(TAG_CATCHPHRASE).get(0));
		}

		if (cache.containsKey(TAG_PORTRAIT))
		{
			parsePortraitLine(cache.get(TAG_PORTRAIT).get(0));
		}

		/*
		 * #Character Weapon proficiencies
		 */
		if (cache.containsKey(TAG_WEAPONPROF))
		{
			for (final String line : cache.get(TAG_WEAPONPROF))
			{
				parseWeaponProficienciesLine(line);
			}

			checkWeaponProficiencies();
		}

		if (cache.containsKey(TAG_ARMORPROF))
		{
			for (final String line : cache.get(TAG_ARMORPROF))
			{
				parseArmorProfLine(line);
			}
		}

		/*
		 * # Temporary Bonuses
		 */
		if (cache.containsKey(TAG_TEMPBONUS))
		{
			for (final String line : cache.get(TAG_TEMPBONUS))
			{
				parseTempBonusLine(line);
			}
		}

		/*
		 * # EquipSet Temporary bonuses
		 * Must be done after both EquipSet and TempBonuses are parsed
		 */
		if (cache.containsKey(TAG_EQSETBONUS))
		{
			for (final String line : cache.get(TAG_EQSETBONUS))
			{
				parseEquipSetTempBonusLine(line);
			}
		}

		if (cache.containsKey(TAG_AGESET))
		{
			for (final String line : cache.get(TAG_AGESET))
			{
				parseAgeSet(line);
			}
		}

	}

	/*
	 * ###############################################################
	 * System Information methods
	 * ###############################################################
	 */
	private void parseCampaignLines(final List<String> lines)
		throws PCGParseException
	{
		final List<Campaign> campaigns = new ArrayList<Campaign>();

		if (SettingsHandler.isLoadCampaignsWithPC())
		{
			PCGTokenizer tokens;

			for (final String line : lines)
			{
				try
				{
					tokens = new PCGTokenizer(line);
				}
				catch (PCGParseException pcgpex)
				{
					/*
					 * Campaigns are critical for characters,
					 * need to stop the load process
					 *
					 * Thomas Behr 14-08-02
					 */
					throw new PCGParseException(
						"parseCampaignLines", line, pcgpex.getMessage()); //$NON-NLS-1$
				}

				for (PCGElement element : tokens.getElements())
				{
					final Campaign aCampaign =
							Globals.getCampaignKeyed(element.getText());

					if (aCampaign != null
						&& aCampaign.isGameMode(SettingsHandler.getGame()
							.getName()))
					{
						if (!aCampaign.isLoaded())
						{
							campaigns.add(aCampaign);
						}
					}
				}
			}

			if (campaigns.size() > 0)
			{
				try
				{
					//PersistenceObserver observer = new PersistenceObserver();
					PersistenceManager pManager =
							PersistenceManager.getInstance();
					//pManager.addObserver( observer );
					pManager.loadCampaigns(campaigns);
					//pManager.deleteObserver( observer );
				}
				catch (PersistenceLayerException e)
				{
					throw new PCGParseException(
						"parseCampaignLines", "N/A", e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
				}

				if (Globals.getUseGUI())
				{
					pcgen.gui.PCGen_Frame1.getInst().getMainSource()
						.updateLoadedCampaignsUI();
				}
			}
		}

		if (!Globals.displayListsHappy())
		{
			throw new PCGParseException("parseCampaignLines", "N/A", //$NON-NLS-1$ //$NON-NLS-2$
				PropertyFactory
					.getString("Exceptions.PCGenParser.NoCampainInfo")); //$NON-NLS-1$
		}
	}

	private void parseCatchPhraseLine(final String line)
	{
		thePC.setCatchPhrase(EntityEncoder.decode(line
			.substring(TAG_CATCHPHRASE.length() + 1)));
	}

	private void parseCharacterAssetLine(final String line)
	{
		thePC.getMiscList().set(
			0,
			EntityEncoder.decode(line
				.substring(TAG_CHARACTERASSET.length() + 1)));
	}

	private void parseCharacterCompLine(final String line)
	{
		thePC.getMiscList().set(
			1,
			EntityEncoder
				.decode(line.substring(TAG_CHARACTERCOMP.length() + 1)));
	}

	private void parseCharacterDescLine(final String line)
	{
		thePC.setDescription(EntityEncoder.decode(line
			.substring(TAG_CHARACTERDESC.length() + 1)));
	}

	private void parseCharacterMagicLine(final String line)
	{
		thePC.getMiscList().set(
			2,
			EntityEncoder.decode(line
				.substring(TAG_CHARACTERMAGIC.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Bio methods
	 * ###############################################################
	 */
	private void parseCharacterNameLine(final String line)
	{
		thePC.setName(EntityEncoder.decode(line.substring(TAG_CHARACTERNAME
			.length() + 1)));
	}

	private void parseCityLine(final String line)
	{
		thePC.setResidence(EntityEncoder.decode(line.substring(TAG_CITY
			.length() + 1)));
	}

	private void parseClassAbilitiesLevelLine(final String line,
		final List<PCLevelInfo> pcLevelInfoList)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalClassAbility" //$NON-NLS-1$
						, line, pcgpex.getMessage());
			warnings.add(message);

			return;
		}

		int level = -1;
		PCClass aPCClass = null;
		String tag;
		PCGElement element;
		PCLevelInfo pcl = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the class key name and level
		// eg: Cleric=4
		if (it.hasNext())
		{
			element = it.next();

			final int index = element.getText().indexOf('=');

			if (index < 0)
			{
				final String message =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
							element.getText());
				warnings.add(message);

				return;
			}

			final String classKeyName =
					EntityEncoder.decode(element.getText().substring(0, index));
			aPCClass = thePC.getClassKeyed(classKeyName);

			if (aPCClass == null)
			{
				final String message =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.ClassNotFound", //$NON-NLS-1$
							classKeyName);
				warnings.add(message);

				return;
			}

			try
			{
				level =
						Integer
							.parseInt(element.getText().substring(index + 1));
			}
			catch (NumberFormatException nfe)
			{
				final String message =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
							element.getText());
				warnings.add(message);

				return;
			}

			if (level < 1)
			{
				final String message =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.InvalidClassLevel", //$NON-NLS-1$
							element.getText());
				warnings.add(message);

				return;
			}

			pcl = null;
			for (PCLevelInfo info : pcLevelInfoList)
			{
				if (classKeyName.equalsIgnoreCase(info.getClassKeyName())
					&& level == info.getLevel())
				{
					pcl = info;
					break;
				}
			}
			if (pcl == null)
			{
				pcl = thePC.saveLevelInfo(classKeyName);
				pcl.setLevel(level);
			}
			else
			{
				thePC.getLevelInfo().add(pcl);
			}
			pcl.setSkillPointsRemaining(0);
		}

		String specialAbilityName;
		SpecialAbility specialAbility;

		while (it.hasNext())
		{
			element = it.next();
			tag = element.getName();

			if (TAG_SUBSTITUTIONLEVEL.equals(tag))
			{
				final String substitutionClassKeyName =
						EntityEncoder.decode(element.getText());
				SubstitutionClass aSubstitutionClass =
						aPCClass
							.getSubstitutionClassKeyed(substitutionClassKeyName);

				if (aSubstitutionClass == null)
				{
					final String message =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.ClassNotFound", //$NON-NLS-1$
								substitutionClassKeyName);
					warnings.add(message);

					return;
				}
				aPCClass.setSubstitutionClassKey(substitutionClassKeyName,
					level);
				aSubstitutionClass.applyLevelArrayModsToLevel(aPCClass, level, thePC);
			}
			else if (TAG_HITPOINTS.equals(tag))
			{
				try
				{
					aPCClass.setHitPoint(level - 1, Integer.valueOf(element
						.getText()));
				}
				catch (NumberFormatException nfe)
				{
					final String message =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.InvalidHP", //$NON-NLS-1$
								tag, element.getText());
					warnings.add(message);
				}
			}
			else if (TAG_SAVES.equals(tag))
			{
				for (final PCGElement child : element.getChildren())
				{
					final String dString =
							EntityEncoder.decode(child.getText());

					// TODO This doesn't seem to be written in Creator.
					if (dString.startsWith(TAG_BONUS + TAG_SEPARATOR))
					{
						aPCClass.addBonusList(dString.substring(6));
					}

					aPCClass.addSave(dString);
				}
			}
			else if (TAG_SPECIALTIES.equals(tag))
			{
				for (final PCGElement child : element.getChildren())
				{
					aPCClass
						.addSpecialty(EntityEncoder.decode(child.getText()));
				}
			}
			else if (TAG_SPECIALABILITIES.equals(tag))
			{
				for (PCGElement child : element.getChildren())
				{
					// TODO - This looks like a bug.  Looks like it should
					// be child.getText().  I am not sure how to test this
					// though.
					//					specialAbilityName = EntityEncoder.decode(element.getText());
					specialAbilityName = EntityEncoder.decode(child.getText());
					if (pcgenVersion[0] <= 5 && pcgenVersion[1] <= 5
						&& pcgenVersion[2] < 6)
					{
						if (specialAbilityName.equals("Turn Undead")) //$NON-NLS-1$
						{
							parseFeatLine("FEAT:Turn Undead|TYPE:SPECIAL.TURNUNDEAD|DESC:"); //$NON-NLS-1$
							continue;
						}
						else if (specialAbilityName.equals("Rebuke Undead")) //$NON-NLS-1$
						{
							parseFeatLine("FEAT:Rebuke Undead|TYPE:SPECIAL.TURNUNDEAD|DESC:"); //$NON-NLS-1$
							continue;
						}
					}
					specialAbility = new SpecialAbility(specialAbilityName);

					// TODO This looks bogus.  PCClass should probably be the 
					// same case in both instances below.  
					if (specialAbilityName.endsWith(":-1")) //$NON-NLS-1$
					{
						specialAbilityName =
								specialAbilityName.substring(0,
									specialAbilityName.length() - 3);
						specialAbility = new SpecialAbility(specialAbilityName);
						specialAbility
							.setSASource("PCClass|" + aPCClass.getKeyName() + '|' + 0); //$NON-NLS-1$
					}
					else
					{
						specialAbility = new SpecialAbility(specialAbilityName);
						specialAbility
							.setSASource("PCCLASS|" + aPCClass.getKeyName() + '|' + level); //$NON-NLS-1$
					}

					if (!thePC.hasSpecialAbility(specialAbilityName))
					{
						aPCClass.addSpecialAbilityToList(specialAbility);
					}

					if (!aPCClass.containsInList(ListKey.SAVE,
						specialAbilityName))
					{
						aPCClass.addSave(specialAbilityName);
					}
				}
			}
			else if (tag.equals(TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aPCClass, level);
			}

			//
			// abbrev=score
			//
			else if (tag.equals(TAG_PRESTAT) || tag.equals(TAG_POSTSTAT))
			{
				boolean isPre = false;

				if (tag.equals(TAG_PRESTAT))
				{
					isPre = true;
				}

				final int idx = element.getText().indexOf('=');

				if (idx > 0)
				{
					final int idxStat =
							SettingsHandler.getGame().getStatFromAbbrev(
								element.getText().substring(0, idx));

					if (idxStat >= 0)
					{
						try
						{
							thePC.saveStatIncrease(element.getText().substring(
								0, idx), Integer.parseInt(element.getText()
								.substring(idx + 1)), isPre);
						}
						catch (NumberFormatException nfe)
						{
							final String msg =
									PropertyFactory.getFormattedString(
										"Warnings.PCGenParser.InvalidStatMod", //$NON-NLS-1$
										tag, element.getText());
							warnings.add(msg);
						}
					}
					else
					{
						final String msg =
								PropertyFactory.getFormattedString(
									"Warnings.PCGenParser.UnknownStat", //$NON-NLS-1$
									tag, element.getText());
						warnings.add(msg);
					}
				}
				else
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.MissingEquals", //$NON-NLS-1$
								tag, element.getText());
					warnings.add(msg);
				}
			}
			else if ((pcl != null) && TAG_SKILLPOINTSGAINED.equals(tag))
			{
				pcl.setSkillPointsGained(Integer.parseInt(element.getText()));
			}
			else if ((pcl != null) && TAG_SKILLPOINTSREMAINING.equals(tag))
			{
				pcl
					.setSkillPointsRemaining(Integer
						.parseInt(element.getText()));
			}
			else if (TAG_DATA.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else
			{
				final String msg =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.UnknownTag", //$NON-NLS-1$
							tag, element.getText());
				warnings.add(msg);
			}
		}

		// TODO:
		// process data
		//
		// need to add some consistency checks here to avoid
		// - duplicate entries for one and the same class/level pair
		// - missing entries for a given class/level pair
	}

	/*
	 * ###############################################################
	 * Character Class(es) methods
	 * ###############################################################
	 */
	private void parseClassLine(final String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Classes are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 14-08-02
			 */
			throw new PCGParseException(
				"parseClassLine", line, pcgpex.getMessage()); //$NON-NLS-1$
		}

		PCClass aPCClass = null;
		String tag;
		PCGElement element;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the class key name!!!
		if (it.hasNext())
		{
			element = it.next();

			aPCClass =
					Globals.getClassKeyed(EntityEncoder.decode(element
						.getText()));

			if (aPCClass != null)
			{
				// Icky: Need to redesign the way classes work!
				// Icky: Having to clone the class here is UGLY!
				aPCClass = aPCClass.clone();
			}
			else
			{
				final String msg =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.CouldntAddClass", //$NON-NLS-1$
							element.getText());
				warnings.add(msg);

				return;
			}
		}

		int level = -1;
		int skillPool = -1;

		while (it.hasNext())
		{
			element = it.next();
			tag = element.getName();

			if (TAG_SUBCLASS.equals(tag))
			{
				String subClassKey = EntityEncoder.decode(element.getText());
				if ((subClassKey.length() > 0) && !subClassKey.equals(Constants.s_NONE))
				{
					SubClass sc = aPCClass.getSubClassKeyed(subClassKey);
					if (sc == null)
					{
						if (subClassKey.equals(aPCClass.getKeyName()))
						{
							subClassKey = Constants.s_NONE;
						}
						else
						{
							final String msg =
									PropertyFactory.getFormattedString(
										"Warnings.PCGenParser.InvalidSubclass", //$NON-NLS-1$
										element.getText());
							warnings.add(msg);
						}
					}
				}
				aPCClass.setSubClassKey(subClassKey);
			}

			if (TAG_LEVEL.equals(tag))
			{
				try
				{
					level = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.InvalidLevel", //$NON-NLS-1$
								element.getText());
					warnings.add(msg);
				}
			}
			else if (TAG_SKILLPOOL.equals(tag))
			{
				try
				{
					skillPool = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.InvalidSkillPool", //$NON-NLS-1$
								element.getText());
					warnings.add(msg);
				}
			}
			else if (TAG_CANCASTPERDAY.equals(tag))
			{
				// TODO
			}
			else if (TAG_SPELLBASE.equals(tag))
			{
				final String spellBase =
						EntityEncoder.decode(element.getText());

				if ((SettingsHandler.getGame().getStatFromAbbrev(
					spellBase.toUpperCase()) > -1)
					|| Constants.s_NONE.equalsIgnoreCase(spellBase)
					|| "Any".equalsIgnoreCase(spellBase) //$NON-NLS-1$
					|| TAG_SPELL.equalsIgnoreCase(spellBase))
				{
					aPCClass.setSpellBaseStat(spellBase);
				}
			}
			else if (TAG_PROHIBITED.equals(tag))
			{
				PCClassLstToken token =
						(PCClassLstToken) TokenStore.inst().getTokenMap(
							PCClassLstToken.class).get(TAG_PROHIBITED);
				token.parse(aPCClass, EntityEncoder.decode(element.getText()),
					-9);
			}
		}

		if (level > -1)
		{
			thePC.getClassList().add(aPCClass);

			for (int i = 0; i < level; ++i)
			{
				PCLevelInfo levelInfo =
						thePC.saveLevelInfo(aPCClass.getKeyName());
				aPCClass.addLevel(levelInfo, false, thePC);
			}
		}

		if (skillPool > -1)
		{
			aPCClass.setSkillPool(skillPool);
		}
	}

	/**
	 * ###############################################################
	 * Character Deity/Domain methods
	 * ###############################################################
	 * @param line
	 **/
	private void parseDeityLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalDeity", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final String deityKey =
				EntityEncoder.decode(tokens.getElements().get(0).getText());
		final Deity aDeity = Globals.getDeityKeyed(deityKey);

		if (aDeity != null)
		{
			thePC.setDeity(aDeity);
		}
		else if (!Constants.s_NONE.equals(deityKey))
		{
			// TODO
			// create Deity object from information contained in pcg
			// for now issue a warning
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.DeityNotFound", //$NON-NLS-1$
						deityKey);
			warnings.add(msg);
		}
	}

	private void parseDomainLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalDomain", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		PCGElement element;
		String tag;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		if (it.hasNext())
		{
			element = it.next();

			// the first element defines the domain name
			final String domainKey = EntityEncoder.decode(element.getText());
			final Domain aDomain = Globals.getDomainKeyed(domainKey);

			if ((aDomain == null) && (!Constants.s_NONE.equals(domainKey)))
			{
				// TODO
				// create Domain object from
				// information contained in pcg
				// But for now just issue a warning
				final String msg =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.DomainNotFound", //$NON-NLS-1$
							domainKey);
				warnings.add(msg);
			}
			else if ((thePC.getCharacterDomainKeyed(domainKey) == null)
				&& (!Constants.s_NONE.equals(domainKey)))
			{
				// PC doesn't have the domain, so create a new
				// one and add it to the PC domain list
				CharacterDomain aCharacterDomain = new CharacterDomain();
				aCharacterDomain.setDomain(aDomain, thePC);

				while (it.hasNext())
				{
					element = it.next();
					tag = element.getName();

					if (TAG_SOURCE.equals(tag))
					{
						CharacterDomainParser parser =
								new CharacterDomainParser();
						parser.setDomainSource(aCharacterDomain,
							sourceElementToString(element));
					}
					else if (TAG_ASSOCIATEDDATA.equals(tag))
					{
						aCharacterDomain.getDomain().addAssociated(
							EntityEncoder.decode(element.getText()));
					}
				}

				thePC.addCharacterDomain(aCharacterDomain);
				aDomain.setIsLocked(true, thePC);

				// TODO
				// set associated list
			}
			else
			{
				// PC already has this domain
				Logging.errorPrintLocalised(
					"Errors.PCGenParser.DuplicateDomain", //$NON-NLS-1$
					domainKey);
			}
		}
	}

	private void parseDomainSpellsLine(@SuppressWarnings("unused")
	String line)
	{
		// TODO
	}

	/**
	 * ###############################################################
	 * EquipSet Temp Bonuses
	 * ###############################################################
	 * @param line
	 **/
	private void parseEquipSetTempBonusLine(final String line)
	{
		PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalEquipSetTempBonus", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		String tag;
		String tagString = null;

		for (PCGElement element : tokens.getElements())
		{
			tag = element.getName();

			if (TAG_EQSETBONUS.equals(tag))
			{
				tagString = EntityEncoder.decode(element.getText());
			}
		}

		if (tagString == null)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.InvalidEquipSetTempBonus", //$NON-NLS-1$
						line);
			warnings.add(msg);

			return;
		}

		final EquipSet eSet = thePC.getEquipSetByIdPath(tagString);

		if (eSet == null)
		{
			return;
		}

		//# EquipSet Temp Bonuses
		//EQSETBONUS:0.2|TEMPBONUS:SPELL=Haste|TBTARGET:PC|TEMPBONUS:SPELL=Shield of Faith|TBTARGET:PC
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for (final PCGElement element : tokens.getElements())
		{
			tag = element.getName();

			if (TAG_TEMPBONUSBONUS.equals(tag))
			{
				final String aString = EntityEncoder.decode(element.getText());

				// Parse aString looking for
				// TEMPBONUS and TBTARGET pairs
				StringTokenizer aTok =
						new StringTokenizer(aString, TAG_SEPARATOR);

				if (aTok.countTokens() < 2)
				{
					continue;
				}

				String sName = aTok.nextToken();
				String tName = aTok.nextToken();
				aList.addAll(getBonusFromName(sName, tName));
			}
		}

		eSet.setTempBonusList(aList);
	}

	/*
	 * ###############################################################
	 * Character Experience methods
	 * ###############################################################
	 */
	private void parseExperienceLine(final String line)
		throws PCGParseException
	{
		final StringTokenizer stok =
				new StringTokenizer(
					line.substring(TAG_EXPERIENCE.length() + 1), TAG_END, false);

		try
		{
			thePC.setXP(Integer.parseInt(stok.nextToken()));
		}
		catch (NumberFormatException nfe)
		{
			throw new PCGParseException(
				"parseExperienceLine", line, nfe.getMessage()); //$NON-NLS-1$
		}
	}

	private void parseEyeColorLine(final String line)
	{
		thePC.setEyeColor(EntityEncoder.decode(line.substring(TAG_EYECOLOR
			.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Ability methods
	 * ###############################################################
	 */

	private void parseAbilityLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalAbility", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		AbilityCategory category = null;
		Ability.Nature nature = Ability.Nature.NORMAL;
		String abilityCat = null;
		Ability ability = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the AbilityCategory key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String categoryKey = EntityEncoder.decode(element.getText());
			category =
					SettingsHandler.getGame().getAbilityCategory(categoryKey);
			if (category == null)
			{
				// emit a warning that the category doesn't exists.
				final String msg =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.AbilityCategoryNotFound", //$NON-NLS-1$
							categoryKey);
				warnings.add(msg);

				// Create one.
				category = new AbilityCategory(categoryKey);
				SettingsHandler.getGame().addAbilityCategory(category);
			}
		}

		// The next element will be the nature
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String natureKey = EntityEncoder.decode(element.getText());
			nature = Ability.Nature.valueOf(natureKey);
		}

		// The next element will be the ability's innate category
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			abilityCat = EntityEncoder.decode(element.getText());
		}

		// The next element will be the ability key
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String abilityKey = EntityEncoder.decode(element.getText());
			ability = Globals.getAbilityKeyed(abilityCat, abilityKey);
			if (ability == null)
			{
				warnings.add("Unable to Find Ability: " + abilityKey);
				return;
			}
			else
			{
				ability = ability.clone();
			}
		}
		
		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (tag.equals(TAG_APPLIEDTO))
			{
				final String appliedToKey =
						EntityEncoder.decode(element.getText());

				if (appliedToKey.startsWith(TAG_MULTISELECT))
				{
					//
					// Should be in the form:
					// MULTISELECCT:maxcount:#chosen:choice1:choice2:...:choicen
					//
					final StringTokenizer sTok =
							new StringTokenizer(appliedToKey, TAG_END, false);

					if (sTok.countTokens() > 2)
					{
						sTok.nextToken(); // should be TAG_MULTISELECT

						final int maxChoices =
								Integer.parseInt(sTok.nextToken());
						sTok.nextToken(); // toss this--number of choices made

						final FeatMultipleChoice fmc = new FeatMultipleChoice();
						fmc.setMaxChoices(maxChoices);

						while (sTok.hasMoreTokens())
						{
							fmc.addChoice(sTok.nextToken());
						}

						ability.addAssociated(fmc);
					}
					else
					{
						final String msg =
								PropertyFactory
									.getFormattedString(
										"Warnings.PCGenParser.IllegalAbilityIgnored", //$NON-NLS-1$
										line);
						warnings.add(msg);
					}
				}
				else if ((ability.isMultiples() && ability.isStacks())
					|| !ability.containsAssociated(appliedToKey))
				{
					String[] assoc = appliedToKey.split(Constants.COMMA, -1);
					for (String string : assoc)
					{
						ability.addAssociated(string);
					}
				}
			}
			else if (TAG_SAVE.equals(tag))
			{
				final String saveKey = EntityEncoder.decode(element.getText());

				// TODO - This never gets written to the file
				if (saveKey.startsWith(TAG_BONUS) && (saveKey.length() > 6))
				{
					ability.addBonusList(saveKey.substring(6));
				}

				ability.addSave(saveKey);
			}
		}
		if (ability != null && category != null && nature != null)
		{
			if (nature == Ability.Nature.NORMAL)
			{
				// If we weren't loading an old character who had feats stored as seperate
				// lines, save the feat now.
				if (!featsPresent || category != AbilityCategory.FEAT)
				{
					thePC.addAbility(category, ability, null);
				}
			}
			else if (nature == Ability.Nature.VIRTUAL)
			{
				ability =
						AbilityUtilities.addVirtualAbility(ability,
							new ArrayList<String>(), thePC
								.getVirtualAbilityList(category), null);
				ability.setNeedsSaving(true);
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Feats methods
	 * ###############################################################
	 */
	private void parseFeatLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalFeat", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the Ability key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String abilityKey = EntityEncoder.decode(element.getText());

			/* First, check to see if the PC already has this ability. If so,
			 * then we just need to mod it. Otherwise we need to create a new
			 * one and add it using non-aggregate (when using aggregate, we
			 * get clones of the PCs actual feats, which don't get saved or
			 * preserved) */
			Ability anAbility = thePC.getRealFeatKeyed(abilityKey);
			boolean added = false;

			if (anAbility != null)
			{
				added =
						parseFeatsHandleAppliedToAndSaveTags(it, anAbility,
							line);
			}
			else
			{
				// PC does not have the feat
				anAbility =
						Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
							abilityKey);

				if (anAbility != null)
				{
					// Clone the new feat
					anAbility = anAbility.clone();

					// parse all the tags for this Feat
					added =
							parseFeatsHandleAppliedToAndSaveTags(it, anAbility,
								line);

					if (!added)
					{
						// add it to the list
						thePC.addFeat(anAbility, null);
					}
					
					featsPresent  = true;
				}
				else
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.CouldntAddAbility", //$NON-NLS-1$
								abilityKey);
					warnings.add(msg);

					return;
				}
			}
		}
	}

	private void parseFeatPoolLine(final String line)
	{
		try
		{
			double featPool =  Double.parseDouble(line.substring(TAG_FEATPOOL
				.length() + 1));
			// In earlier versions the featpool included the bonus, so we need to counter it
			if (compareVersionTo(new int[]{5,11,1}) < 0)
			{
				calcFeatPoolAfterLoad = true;
				baseFeatPool = featPool;
			}
			else
			{
				thePC.setFeats(featPool);
			}
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalFeatPool", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private void parseUserPoolLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalAbilityPool", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();
		final String cat = EntityEncoder.decode(it.next().getText());
		final AbilityCategory category =
				SettingsHandler.getGame().getAbilityCategory(cat);
		try
		{
			thePC.setUserPoolBonus(category,
				new BigDecimal(it.next().getText()));
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalAbilityPool", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private boolean parseFeatsHandleAppliedToAndSaveTags(
		final Iterator<PCGElement> it, final Ability aFeat, final String line)
	{
		boolean added = false;

		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (TAG_APPLIEDTO.equals(tag))
			{
				final String appliedToKey =
						EntityEncoder.decode(element.getText());

				// This will delete a perfectly valid feat.  Removed 03/26/06 boomer70
				//				if (aFeat.getName().endsWith("Weapon Proficiency"))
				//				{
				//					aPC.addWeaponProf(updateProficiencyName(appliedToKey, false));
				//
				//					// addWeaponProf adds the feat to this
				//					// PC's list, so don't add it again!
				//					added = true;
				//				}

				if (appliedToKey.startsWith(TAG_MULTISELECT))
				{
					//
					// Should be in the form:
					// MULTISELECCT:maxcount:#chosen:choice1:choice2:...:choicen
					//
					final StringTokenizer sTok =
							new StringTokenizer(appliedToKey, TAG_END, false);

					if (sTok.countTokens() > 2)
					{
						sTok.nextToken(); // should be TAG_MULTISELECT

						final int maxChoices =
								Integer.parseInt(sTok.nextToken());
						sTok.nextToken(); // toss this--number of choices made

						final FeatMultipleChoice fmc = new FeatMultipleChoice();
						fmc.setMaxChoices(maxChoices);

						while (sTok.hasMoreTokens())
						{
							fmc.addChoice(sTok.nextToken());
						}

						aFeat.addAssociated(fmc);
					}
					else
					{
						final String msg =
								PropertyFactory.getFormattedString(
									"Warnings.PCGenParser.IllegalFeatIgnored", //$NON-NLS-1$
									line);
						warnings.add(msg);
					}
				}
				else if ((aFeat.isMultiples() && aFeat.isStacks())
					|| !aFeat.containsAssociated(appliedToKey))
				{
					aFeat.addAssociated(appliedToKey);
				}
			}
			else if (TAG_SAVE.equals(tag))
			{
				final String saveKey = EntityEncoder.decode(element.getText());

				if (saveKey.startsWith(TAG_BONUS) && (saveKey.length() > 6))
				{
					aFeat.addBonusList(saveKey.substring(6));
				}

				aFeat.addSave(saveKey);
			}
			else if (tag.equals(TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aFeat);
			}
		}

		return added;
	}

	/*
	 * ###############################################################
	 * Character Follower methods
	 * ###############################################################
	 */
	private void parseFollowerLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalFollower", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Follower aFollower =
				new Follower(Constants.EMPTY_STRING, Constants.EMPTY_STRING,
					Constants.EMPTY_STRING);

		for (final PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_FOLLOWER.equals(tag))
			{
				aFollower.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_TYPE.equals(tag))
			{
				aFollower.setType(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_RACE.equals(tag))
			{
				aFollower.setRace(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_HITDICE.equals(tag))
			{
				try
				{
					aFollower.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_FILE.equals(tag))
			{
				aFollower.setRelativeFileName(EntityEncoder.decode(element
					.getText()));
			}
		}

		if (!Constants.EMPTY_STRING.equals(aFollower.getFileName())
			&& !Constants.EMPTY_STRING.equals(aFollower.getName())
			&& !Constants.EMPTY_STRING.equals(aFollower.getType()))
		{
			thePC.addFollower(aFollower);
		}
	}

	private void parseGameMode(final String line) throws PCGParseException
	{
		final String requestedMode = line.substring(TAG_GAMEMODE.length() + 1);

		final GameMode currentGameMode = SettingsHandler.getGame();
		final String currentMode = currentGameMode.getName();

		if (!requestedMode.equals(currentMode))
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Exceptions.PCGenParser.WrongGameMode", //$NON-NLS-1$
						requestedMode, currentMode);
			throw new PCGParseException("ParseGameMode", line, msg); //$NON-NLS-1$
		}
	}

	private void parseGenderLine(final String line)
	{
		thePC.setGender(EntityEncoder.decode(line
			.substring(TAG_GENDER.length() + 1)));
	}

	/**
	 * # HTML Output Sheet location
	 * @param line
	 **/
	private void parseHTMLOutputSheetLine(final String line)
	{
		String aFileName =
				EntityEncoder.decode(line.substring(TAG_HTMLOUTPUTSHEET
					.length() + 1));

		if (aFileName.length() <= 0)
		{
			aFileName =
					SettingsHandler.getSelectedCharacterHTMLOutputSheet(thePC);
		}

		thePC.setSelectedCharacterHTMLOutputSheet(aFileName);
	}

	private void parseHairColorLine(final String line)
	{
		thePC.setHairColor(EntityEncoder.decode(line.substring(TAG_HAIRCOLOR
			.length() + 1)));
	}

	private void parseHairStyleLine(final String line)
	{
		thePC.setHairStyle(EntityEncoder.decode(line.substring(TAG_HAIRSTYLE
			.length() + 1)));
	}

	private void parseHandedLine(final String line)
	{
		thePC.setHanded(EntityEncoder.decode(line
			.substring(TAG_HANDED.length() + 1)));
	}

	private void parseHeightLine(final String line)
	{
		try
		{
			thePC.setHeight(Integer.parseInt(line
				.substring(TAG_HEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalHeight", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private void parseInterestsLine(final String line)
	{
		thePC.setInterests(EntityEncoder.decode(line.substring(TAG_INTERESTS
			.length() + 1)));
	}

	private void parseKitLine(final String line)
	{
		final StringTokenizer stok =
				new StringTokenizer(line.substring(TAG_KIT.length() + 1),
					TAG_SEPARATOR, false);

		if (stok.countTokens() != 2)
		{
			// TODO This if switch currently does nothing?
		}

		/** final String region = */
		stok.nextToken(); //TODO: Is this intended to be thrown away? The value is never used.

		/** final String kit = stok.nextToken(); */

		final Kit aKit =
				Globals.getKitKeyed(line.substring(TAG_KIT.length() + 1));

		if (aKit == null)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.KitNotFound", //$NON-NLS-1$
						line);
			warnings.add(msg);

			return;
		}

		thePC.addKit(aKit);
	}

	/*
	 * ###############################################################
	 * Character Languages methods
	 * ###############################################################
	 */
	private void parseLanguageLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalLanguage", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		for (PCGElement element : tokens.getElements())
		{
			final Language aLang = Globals.getLanguageKeyed(EntityEncoder
				.decode(element.getText()));
			if (aLang == null)
			{
				final String message =	"No longer speaks language: " + element.getText();  //$NON-NLS-1$
				warnings.add(message);
				continue;
			}
			thePC.addLanguage(aLang);
		}
	}

	/**
	 * # Load companions with master?
	 * @param line
	 **/
	private void parseLoadCompanionLine(final String line)
	{
		thePC.setLoadCompanion(line.endsWith(VALUE_Y));
	}

	private void parseLocationLine(final String line)
	{
		thePC.setLocation(EntityEncoder.decode(line.substring(TAG_LOCATION
			.length() + 1)));
	}

	private void parseMasterLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalMaster", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final Follower aMaster =
				new Follower(Constants.EMPTY_STRING, Constants.EMPTY_STRING,
					Constants.EMPTY_STRING);

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_MASTER.equals(tag))
			{
				aMaster.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_TYPE.equals(tag))
			{
				aMaster.setType(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_HITDICE.equals(tag))
			{
				try
				{
					aMaster.setUsedHD(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_FILE.equals(tag))
			{
				/*
				 * quick and dirty way to handle ':'
				 * need to come up with a clean solution before releasing
				 *
				 * author: Thomas Behr 09-09-02
				 */
				aMaster.setRelativeFileName(EntityEncoder.decode(element
					.getText()));
			}
			else if (TAG_ADJUSTMENT.equals(tag))
			{
				aMaster.setAdjustment(Integer.parseInt(element.getText()));
			}
		}

		if (!Constants.EMPTY_STRING.equals(aMaster.getFileName())
			&& !Constants.EMPTY_STRING.equals(aMaster.getName())
			&& !Constants.EMPTY_STRING.equals(aMaster.getType()))
		{
			thePC.setMaster(aMaster);
		}
	}

	/*
	 * ###############################################################
	 * Character Notes Tab methods
	 * ###############################################################
	 */
	private void parseNoteLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.IllegalNotes", //$NON-NLS-1$
						line, pcgpex.getMessage());
			warnings.add(msg);

			return;
		}

		final NoteItem ni =
				new NoteItem(-1, -1, Constants.EMPTY_STRING,
					Constants.EMPTY_STRING);

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_NOTE.equals(tag))
			{
				ni.setName(EntityEncoder.decode(element.getText()));
			}
			else if (TAG_ID.equals(tag))
			{
				try
				{
					ni.setIdValue(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.InvalidNotes", //$NON-NLS-1$
								line);
					warnings.add(msg);

					break;
				}
			}
			else if (TAG_PARENTID.equals(tag))
			{
				try
				{
					ni.setParentId(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					ni.setIdValue(-1);

					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.InvalidNotes", //$NON-NLS-1$
								line);
					warnings.add(msg);

					break;
				}
			}
			else if (TAG_VALUE.equals(tag))
			{
				ni.setValue(EntityEncoder.decode(element.getText()));
			}
		}

		if (ni.getId() > -1)
		{
			thePC.addNotesItem(ni);
		}
	}

	/**
	 * # PDF Output Sheet location
	 * @param line
	 **/
	private void parsePDFOutputSheetLine(final String line)
	{
		String aFileName =
				EntityEncoder.decode(line
					.substring(TAG_PDFOUTPUTSHEET.length() + 1));

		if (aFileName.length() <= 0)
		{
			aFileName =
					SettingsHandler.getSelectedCharacterPDFOutputSheet(thePC);
		}

		thePC.setSelectedCharacterPDFOutputSheet(aFileName);
	}

	private void parsePersonalityTrait1Line(final String line)
	{
		thePC.setTrait1(EntityEncoder.decode(line
			.substring(TAG_PERSONALITYTRAIT1.length() + 1)));
	}

	private void parsePersonalityTrait2Line(final String line)
	{
		thePC.setTrait2(EntityEncoder.decode(line
			.substring(TAG_PERSONALITYTRAIT2.length() + 1)));
	}

	private void parsePhobiasLine(final String line)
	{
		thePC.setPhobias(EntityEncoder.decode(line.substring(TAG_PHOBIAS
			.length() + 1)));
	}

	private void parsePlayerNameLine(final String line)
	{
		thePC.setPlayersName(EntityEncoder.decode(line.substring(TAG_PLAYERNAME
			.length() + 1)));
	}

	private void parsePoolPointsLine(final String line)
	{
		try
		{
			thePC.setPoolAmount(Integer.parseInt(line.substring(TAG_POOLPOINTS
				.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.InvalidPoolPoints", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private void parsePoolPointsLine2(final String line)
	{
		try
		{
			thePC.setPointBuyPoints(Integer.parseInt(line
				.substring(TAG_POOLPOINTSAVAIL.length() + 1)));
			thePC
				.setCostPool(thePC.getPointBuyPoints() - thePC.getPoolAmount());
		}
		catch (NumberFormatException nfe)
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Warnings.PCGenParser.InvalidPoolPoints", //$NON-NLS-1$
						line);
			warnings.add(msg);
		}
	}

	private void parsePortraitLine(final String line)
	{
		thePC.setPortraitPath(EntityEncoder.decode(line.substring(TAG_PORTRAIT
			.length() + 1)));
	}

	private void parseRaceLine(final String line) throws PCGParseException
	{
		final StringTokenizer sTok =
				new StringTokenizer(line.substring(TAG_RACE.length() + 1),
					TAG_SEPARATOR, false);
		final String race_name = EntityEncoder.decode(sTok.nextToken());
		final Race aRace = Globals.getRaceKeyed(race_name);

		if (aRace != null)
		{
			thePC.setRace(aRace);

			final int hitDice = aRace.hitDice(thePC);

			if (sTok.hasMoreTokens())
			{
				final String aString = sTok.nextToken();

				if (aString.startsWith(TAG_HITPOINTS))
				{
					final StringTokenizer aTok =
							new StringTokenizer(aString.substring(TAG_HITPOINTS
								.length()), TAG_END, false);
					int i = 0;

					if (hitDice > 0)
					{
						final HashMap<String, Integer> hitPointMap =
								new HashMap<String, Integer>();
						while (aTok.hasMoreTokens())
						{
							if (i >= hitDice)
							{
								final String msg =
										PropertyFactory.getFormattedString(
											"Warnings.PCGenParser.RaceFewerHD", //$NON-NLS-1$
											race_name);
								warnings.add(msg);

								break;
							}

							try
							{
								hitPointMap.put(Integer.toString(i++), Integer
									.valueOf(aTok.nextToken()));
							}
							catch (NumberFormatException ex)
							{
								throw new PCGParseException(
									"parseRaceLine", aString, ex.getMessage()); //$NON-NLS-1$
							}
						}

						if (i < hitDice)
						{
							final String msg =
									PropertyFactory.getFormattedString(
										"Warnings.PCGenParser.RaceMoreHD", //$NON-NLS-1$
										race_name);
							warnings.add(msg);
						}

						thePC.getRace().setHitPointMap(hitPointMap);
					}
					else
					{
						String msgKey = "Warnings.PCGenParser.RaceNoHD"; //$NON-NLS-1$

						if (!SettingsHandler.isMonsterDefault())
						{
							msgKey = "Warnings.PCGenParser.RaceNoHDDefMon"; //$NON-NLS-1$
						}
						final String msg =
								PropertyFactory.getFormattedString(msgKey,
									race_name);
						warnings.add(msg);
					}
				}
				else
				{
					final String msg =
							PropertyFactory.getFormattedString(
								"Warnings.PCGenParser.UnknownRaceInfo", //$NON-NLS-1$
								aString);
					warnings.add(msg);
				}
			}
		}
		else
		{
			final String msg =
					PropertyFactory.getFormattedString(
						"Exceptions.PCGenParser.RaceNotFound", //$NON-NLS-1$
						race_name);
			throw new PCGParseException("parseRaceLine", line, msg); //$NON-NLS-1$
		}

		// TODO
		// adjust for more information according to PCGVer1Creator.appendRaceLine
	}

	/*
	 * ###############################################################
	 * Character Region methods
	 * ###############################################################
	 */
	private void parseRegionLine(final String line)
	{
		final String r =
				EntityEncoder.decode(line.substring(TAG_REGION.length() + 1));
		thePC.setRegion(r);
	}

	//this method is obsolete, but left in for backward-compatibility, replaced by parseCityLine()
	private void parseResidenceLine(final String line)
	{
		thePC.setResidence(EntityEncoder.decode(line.substring(TAG_RESIDENCE
			.length() + 1)));
		thePC.setDirty(true); // trigger a save prompt so that the PCG will be updated
	}

	/*
	 * ###############################################################
	 * Character Skills methods
	 * ###############################################################
	 */
	private void parseSkillLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Skill line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		Skill aSkill = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the skill key name!!!
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String skillKey = EntityEncoder.decode(element.getText());
			aSkill = thePC.getSkillKeyed(skillKey);

			if (aSkill == null)
			{
				aSkill = Globals.getSkillKeyed(skillKey);

				if (aSkill != null)
				{
					// Icky: Need to redesign the way skills work!
					// Icky: Having to clone the skill here is UGLY!
					aSkill = aSkill.clone();
					thePC.getSkillList().add(aSkill);
				}
				else
				{
					final String message = "Could not add skill: " + skillKey;
					warnings.add(message);

					return;
				}
			}
		}

		while (it.hasNext())
		{
			final PCGElement element = it.next();
			final String tag = element.getName();

			if (TAG_SYNERGY.equals(tag))
			{
				// TODO
				// for now it's ok to ignore it!
			}
			else if (TAG_OUTPUTORDER.equals(tag))
			{
				int outputindex = 0;

				try
				{
					outputindex = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// This is not critical.
					// Maybe warn the user?
				}

				aSkill.setOutputIndex(outputindex);
			}
			else if (TAG_CLASSBOUGHT.equals(tag))
			{
				PCGElement childClass = null;
				PCGElement childRanks = null;

				for (PCGElement child : element.getChildren())
				{
					if (TAG_CLASS.equals(child.getName()))
					{
						childClass = child;
					}
					else if (TAG_RANKS.equals(child.getName()))
					{
						childRanks = child;
					}
				}

				if (childClass == null)
				{
					final String message =
							"Invalid class/ranks specification: " + line;
					warnings.add(message);

					continue;
				}

				if (childRanks == null)
				{
					final String message =
							"Invalid class/ranks specification: " + line;
					warnings.add(message);

					continue;
				}

				//None for a class is a special key word.  It is used when a familiar inherits a skill from its master
				PCClass aPCClass = null;
				if (!childClass.getText().equals("None")) //$NON-NLS-1$
				{
					final String childClassKey =
							EntityEncoder.decode(childClass.getText());
					aPCClass = thePC.getClassKeyed(childClassKey);

					if (aPCClass == null)
					{
						final String message =
								"Could not find class: " + childClassKey;
						warnings.add(message);

						continue;
					}
				}

				try
				{
					double ranks = Double.parseDouble(childRanks.getText());
					aSkill.modRanks(ranks, aPCClass, true, thePC);
				}
				catch (NumberFormatException nfe)
				{
					final String message =
							"Invalid ranks specification: "
								+ childRanks.getText();
					warnings.add(message);

					continue;
				}
			}
			else if (TAG_ASSOCIATEDDATA.equals(tag))
			{
				aSkill.addAssociated(EntityEncoder.decode(element.getText()));
			}
			else if (tag.equals(TAG_LEVELABILITY))
			{
				parseLevelAbilityInfo(element, aSkill);
			}
		}
	}

	/**
	 * # Skills Output order
	 * @param line
	 **/
	private void parseSkillsOutputOrderLine(final String line)
	{
		try
		{
			thePC.setSkillsOutputOrder(Integer.parseInt(line
				.substring(TAG_SKILLSOUTPUTORDER.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message =
					"Illegal Skills Output Order line ignored: " + line;
			warnings.add(message);
		}
	}

	private void parseSkinColorLine(final String line)
	{
		thePC.setSkinColor(EntityEncoder.decode(line.substring(TAG_SKINCOLOR
			.length() + 1)));
	}

	private void parseSpeechPatternLine(final String line)
	{
		thePC.setSpeechTendency(EntityEncoder.decode(line
			.substring(TAG_SPEECHPATTERN.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Spell Book Information methods
	 * ###############################################################
	 */
	/*
	 * #Spell Book Information
	 * SPELLBOOK:bookname|TYPE:spellbooktype
	 */
	private void parseSpellBookLines(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Spell book ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		SpellBook aSpellBook = null;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_SPELLBOOK.equals(tag))
			{
				final String bookName = EntityEncoder.decode(element.getText());

				aSpellBook =
						new SpellBook(bookName, SpellBook.TYPE_PREPARED_LIST);
			}
			else if (TAG_TYPE.equals(tag))
			{
				try
				{
					aSpellBook.setType(Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
					final String message =
							"Spell book " + aSpellBook.getName()
								+ " had an illegal type: " + element.getText()
								+ " in line " + line;
					warnings.add(message);
				}
			}
			else if (TAG_AUTOADDKNOWN.equals(tag))
			{
				if (VALUE_Y.equals(element.getText()))
				{
					thePC.setSpellBookNameToAutoAddKnown(aSpellBook.getName());
				}
			}
		}

		thePC.addSpellBook(aSpellBook);
	}

	/*
	 * ###############################################################
	 * Character Spells Information methods
	 * ###############################################################
	 */
	private void parseSpellLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Spell line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		Spell aSpell = null;
		PCClass aPCClass = null;
		PObject source = null;

		String spellBook = null;

		int times = 1;
		int spellLevel = 0;
		int numPages = 0;

		final List<Ability> metaFeats = new ArrayList<Ability>();

		Object obj = null;
		int ppCost = -1;

		for (final PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_SPELLNAME.equals(tag))
			{
				final String spellName =
						EntityEncoder.decode(element.getText());

				// either NULL (no spell), a Spell instance,
				// or ArrayList of Spells (with same name)
				obj = Globals.getSpellMap().get(spellName);

				if (obj instanceof Spell)
				{
					aSpell = (Spell) obj;
				}

				if (obj == null)
				{
					final String message =
							"Could not find spell named: " + spellName;
					warnings.add(message);

					return;
				}
			}
			else if (TAG_TIMES.equals(tag))
			{
				try
				{
					times = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_CLASS.equals(tag))
			{
				final String classKey = EntityEncoder.decode(element.getText());
				aPCClass = thePC.getClassKeyed(classKey);

				if (aPCClass == null)
				{
					final String message =
							"Invalid class specification: " + classKey;
					warnings.add(message);

					return;
				}
			}
			else if (TAG_SPELL_BOOK.equals(tag))
			{
				spellBook = EntityEncoder.decode(element.getText());
			}
			else if (TAG_SPELLLEVEL.equals(tag))
			{
				try
				{
					spellLevel = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_SPELLPPCOST.equals(tag))
			{
				try
				{
					ppCost = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_SPELLNUMPAGES.equals(tag))
			{
				try
				{
					numPages = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can do about it
				}
			}
			else if (TAG_SOURCE.equals(tag))
			{
				String typeName = Constants.EMPTY_STRING;
				String objectKey = Constants.EMPTY_STRING;

				for (final PCGElement child : element.getChildren())
				{
					final String childTag = child.getName();

					if (TAG_TYPE.equals(childTag))
					{
						typeName = child.getText().toUpperCase();
					}
					else if (TAG_NAME.equals(childTag))
					{
						objectKey = child.getText();
					}
				}

				if (TAG_DOMAIN.equals(typeName))
				{
					source = thePC.getCharacterDomainKeyed(objectKey);

					if (source == null)
					{
						final String message =
								"Could not find domain: " + objectKey;
						warnings.add(message);

						return;
					}
				}
				else
				{
					// it's either the class, sub-class or a cast-as class
					// first see if it's the class
					if (((aPCClass != null) && objectKey.equals(aPCClass
						.getKeyName()))
						|| (aPCClass.getSpellKey().indexOf(
							typeName + '|' + objectKey) >= 0))
					{
						source = aPCClass;
					}
					else
					{
						source = thePC.getClassKeyed(objectKey); // see if PC has the class
					}
				}
			}
			else if (TAG_FEATLIST.equals(tag))
			{
				for (PCGElement child : element.getChildren())
				{
					final String featKey =
							EntityEncoder.decode(child.getText());
					final Ability anAbility =
							Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
								featKey);

					if (anAbility != null)
					{
						metaFeats.add(anAbility);
					}
				}
			}
		}

		if ((obj == null) || (aPCClass == null) || (spellBook == null))
		{
			final String message = "Illegal Spell line ignored: " + line;
			warnings.add(message);

			return;
		}

		/*
		 * this can only happen if the source type was NOT DOMAIN!
		 */
		if (source == null)
		{
			source = aPCClass;
		}

		if (obj instanceof List)
		{
			// find the instance of Spell in this class
			// best suited to this spell
			for (final Spell spell : (ArrayList<Spell>) obj)
			{
				// valid spell has a non-negative spell level
				if ((spell != null)
					&& (spell.levelForKey(source.getSpellKey(), thePC)[0] >= 0))
				{
					aSpell = spell;
					break;
				}
			}
			if (aSpell == null)
			{
				Logging.errorPrint("Could not resolve spell " + obj.toString());
			}
		}

		if (aSpell == null)
		{
			final String message =
					"Could not find spell named: " + String.valueOf(obj);
			warnings.add(message);

			return;
		}

		// just to make sure the spellbook is present
		thePC.addSpellBook(spellBook);
		final SpellBook book = thePC.getSpellBookByName(spellBook);

		final int[] spellLevels =
				aSpell.levelForKey(source.getSpellKey(), thePC);
		boolean found = false;

		for (int sindex = 0; sindex < spellLevels.length; ++sindex)
		{
			final int level = spellLevels[sindex];

			if (level < 0)
			{
				final List<PCSpell> aList = source.getSpellList();

				if (aList == null)
				{
					continue;
				}

				for (PCSpell pcSpell : aList)
				{
					if (pcSpell == null)
					{
						continue;
					}

					found =
							(aSpell.getKeyName().equals(pcSpell.getKeyName()) && pcSpell
								.getSpellbook().equals(spellBook));

					if (found)
					{
						break;
					}
				}

				continue;
			}

			found = true;

			// do not load auto knownspells into default spellbook
			if (spellBook.equals(Globals.getDefaultSpellBook())
				&& aPCClass.isAutoKnownSpell(aSpell.getKeyName(), level, thePC)
				&& thePC.getAutoSpells())
			{
				continue;
			}

			CharacterSpell aCharacterSpell =
					aPCClass.getSpellSupport().getCharacterSpellForSpell(
						aSpell, aPCClass);

			// PC does not have that spell on that classes list
			// so we'll need to add it to the list
			if (aCharacterSpell == null)
			{
				aCharacterSpell = new CharacterSpell(source, aSpell);
				if (!(source instanceof Domain))
				{
					aCharacterSpell.addInfo(level, times, spellBook);
				}
				aPCClass.getSpellSupport().addCharacterSpell(aCharacterSpell);
			}

			SpellInfo aSpellInfo = null;

			if (source.getKeyName().equals(aPCClass.getKeyName())
				|| !spellBook.equals(Globals.getDefaultSpellBook()))
			{
				aSpellInfo =
						aCharacterSpell.getSpellInfoFor(spellBook, spellLevel,
							-1);

				// This doesn't make sense. What does the
				// metaFeats list have to do with this?
				if ((aSpellInfo == null) || !metaFeats.isEmpty())
				{
					aSpellInfo =
							aCharacterSpell.addInfo(spellLevel, times,
								spellBook);
				}
			}

			if (aSpellInfo != null)
			{
				if (!metaFeats.isEmpty())
				{
					aSpellInfo.addFeatsToList(metaFeats);
				}
				aSpellInfo.setActualPPCost(ppCost);
				aSpellInfo.setNumPages(numPages);
				book.setNumPagesUsed(book.getNumPagesUsed() + numPages);
				book.setNumSpells(book.getNumSpells() + 1);
			}
		}
		// end sindex for loop

		if (!found)
		{
			final String message =
					"Could not find spell " + aSpell.getDisplayName() + " in "
						+ shortClassName(source) + " "
						+ source.getDisplayName();
			warnings.add(message);
		}
	}

	/*
	 * ###############################################################
	 * Spell List Information methods
	 * ###############################################################
	 */
	/*
	 * #Spell List Information
	 * SPELLLIST:sourceclassname|spelllistentry|spelllistentry
	 */
	private void parseSpellListLines(final String line)
	{
		final String subLine = line.substring(TAG_SPELLLIST.length() + 1);
		final StringTokenizer stok =
				new StringTokenizer(subLine, TAG_SEPARATOR, false);

		final String classKey = stok.nextToken();
		final PCClass aClass = thePC.getClassKeyed(classKey);

		while ((aClass != null) && stok.hasMoreTokens())
		{
			final String tok = stok.nextToken();
			aClass.addClassSpellList(tok);
			PCClass spellClass = Globals.getClassKeyed(tok);
			if (spellClass != null)
			{
				aClass.getSpellSupport().addSpells(-1,
					spellClass.getSpellList());
			}
		}
	}

	/*
	 * ###############################################################
	 * Character Attributes methods
	 * ###############################################################
	 */
	private void parseStatLine(final String line) throws PCGParseException
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * Ability scores are critical for characters,
			 * need to stop the load process
			 *
			 * Thomas Behr 09-09-02
			 */
			throw new PCGParseException(
				"parseStatLine", line, pcgpex.getMessage()); //$NON-NLS-1$
		}

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		if (it.hasNext())
		{
			PCGElement element = it.next();
			final String statName = element.getText();

			final int index =
					SettingsHandler.getGame().getStatFromAbbrev(statName);

			if ((index > -1) && seenStats.add(statName.toUpperCase())
				&& (it.hasNext()))
			{
				element = it.next();

				try
				{
					thePC.getStatList().getStatAt(index).setBaseScore(
						Integer.parseInt(element.getText()));
				}
				catch (NumberFormatException nfe)
				{
					throw new PCGParseException(
						"parseStatLine", line, nfe.getMessage()); //$NON-NLS-1$
				}
			}
			else
			{
				final String message =
						"Invalid attribute specification. "
							+ "Cannot load character.";
				throw new PCGParseException("parseStatLine", line, message); //$NON-NLS-1$
			}
		}
		else
		{
			final String message =
					"Invalid attribute specification. "
						+ "Cannot load character.";
			throw new PCGParseException("parseStatLine", line, message); //$NON-NLS-1$
		}
	}

	private void parseTabNameLine(final String line)
	{
		thePC.setTabName(EntityEncoder.decode(line.substring(TAG_TABNAME
			.length() + 1)));
	}

	/*
	 * ###############################################################
	 * Character Templates methods
	 * ###############################################################
	 */
	private void parseTemplateLine(final String line)
	{
		if (line.charAt(TAG_TEMPLATESAPPLIED.length() + 1) == '[')
		{
			final PCGTokenizer tokens;

			try
			{
				tokens = new PCGTokenizer(line);
			}
			catch (PCGParseException pcgpex)
			{
				final String message =
						"Illegal Template line ignored: " + line
							+ Constants.s_LINE_SEP + "Error: "
							+ pcgpex.getMessage();
				warnings.add(message);

				return;
			}

			PCTemplate aPCTemplate = null;

			Iterator<PCGElement> it = tokens.getElements().iterator();

			if (it.hasNext())
			{
				final PCGElement element = it.next();

				for (final PCGElement child : element.getChildren())
				{
					final String childTag = child.getName();

					if (TAG_NAME.equals(childTag))
					{
						aPCTemplate = addKeyedTemplate(child.getText());

						if (aPCTemplate == null)
						{
							break;
						}
					}
					else if (TAG_CHOSENFEAT.equals(childTag))
					{
						String mapKey = null;
						String mapValue = null;

						for (PCGElement subChild : child.getChildren())
						{
							final String subChildTag = subChild.getName();

							if (TAG_MAPKEY.equals(subChildTag))
							{
								mapKey = subChild.getText();
							}
							else if (TAG_MAPVALUE.equals(subChildTag))
							{
								mapValue = subChild.getText();
							}
						}

						if ((mapKey != null) && (mapValue != null))
						{
							aPCTemplate
								.addChosenFeat(EntityEncoder.decode(mapKey),
									EntityEncoder.decode(mapValue));
						}
					}
					else if (TAG_CHOSENTEMPLATE.equals(childTag))
					{
						for (PCGElement subChild : child.getChildren())
						{
							final String subChildTag = subChild.getName();

							if (TAG_NAME.equals(subChildTag))
							{
								final String ownedTemplateKey =
										EntityEncoder
											.decode(subChild.getText());
								final PCTemplate ownedTemplate =
										Globals
											.getTemplateKeyed(ownedTemplateKey);
								if (ownedTemplate != null)
								{
									aPCTemplate.addTemplate(ownedTemplateKey);
								}
							}
						}
					}
				}
			}
		}
		else
		{
			addKeyedTemplate(line.substring(TAG_TEMPLATESAPPLIED.length() + 1));
		}
	}

	/**
	 * # Use temporary mods/bonuses?
	 * @param line
	 **/
	private void parseUseTempModsLine(final String line)
	{
		thePC.setUseTempMods(line.endsWith(VALUE_Y));
	}

	private void parseVFeatLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal VFeat line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		Ability anAbility = null;

		final Iterator<PCGElement> it = tokens.getElements().iterator();

		// the first element defines the Feat key name
		if (it.hasNext())
		{
			final PCGElement element = it.next();

			final String abilityKey = EntityEncoder.decode(element.getText());
			anAbility =
					Globals
						.getAbilityKeyed(Constants.FEAT_CATEGORY, abilityKey);

			if (anAbility == null)
			{
				final String message = "Could not add vfeat: " + abilityKey;
				warnings.add(message);

				return;
			}

			anAbility =
					AbilityUtilities.addVirtualAbility(Constants.FEAT_CATEGORY,
						abilityKey, thePC.getVirtualFeatList(), null);
			anAbility.setNeedsSaving(true);
			thePC.setDirty(true);
		}

		parseFeatsHandleAppliedToAndSaveTags(it, anAbility, line);

		// TODO
		// process all additional information
	}

	/**
	 * Parses the version information from a PCG file.
	 * @param line The line containing version information
	 * @throws PCGParseException if the line is not a valid version line
	 */
	protected void parseVersionLine(final String line) throws PCGParseException
	{
		int[] version = {0, 0, 0};

		// Check to make sure that this is a version line
		if (!line.startsWith(TAG_VERSION + TAG_END))
		{
			throw new PCGParseException(
				"parseVersionLine", line, "Not a Version Line."); //$NON-NLS-1$
		}

		// extract the tokens from the version line
		String[] tokens =
				line.substring(TAG_VERSION.length() + 1).split(" |\\.|\\-", 4); //$NON-NLS-1$

		for (int idx = 0; idx < 3 && idx < tokens.length; idx++)
		{

			try
			{
				version[idx] = Integer.parseInt(tokens[idx]);
			}
			catch (NumberFormatException e)
			{
				if (idx == 2 && (tokens[idx].startsWith("RC")))
				{
					pcgenVersionSuffix = tokens[2];
				}
				else
				{
					// Something in the first 3 digits was not an integer
					throw new PCGParseException(
						"parseVersionLine", line, "Invalid PCGen version."); //$NON-NLS-1$
				}
			}
		}
		if (tokens.length == 4)
		{
			pcgenVersionSuffix = tokens[3];
		}
		pcgenVersion = version;
	}

	/*
	 * ###############################################################
	 * Character Weapon proficiencies methods
	 * ###############################################################
	 */
	private void parseWeaponProficienciesLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Weapon proficiencies line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		PObject source = null;

		for (PCGElement element : tokens.getElements())
		{
			if (TAG_SOURCE.equals(element.getName()))
			{
				String type = Constants.EMPTY_STRING;
				String key = Constants.EMPTY_STRING;

				for (final PCGElement child : element.getChildren())
				{
					final String tag = child.getName();

					if (TAG_TYPE.equals(tag))
					{
						type = child.getText().toUpperCase();
					}
					else if (TAG_NAME.equals(tag))
					{
						key = child.getText();
					}
				}

				if (Constants.EMPTY_STRING.equals(type)
					|| Constants.EMPTY_STRING.equals(key))
				{
					final String message =
							"Illegal Weapon proficiencies line ignored: "
								+ line;
					warnings.add(message);

					return;
				}

				if (TAG_RACE.equals(type))
				{
					source = thePC.getRace();
				}
				else if (TAG_PCCLASS.equals(type))
				{
					source = thePC.getClassKeyed(key);
				}
				else if (TAG_DOMAIN.equals(type))
				{
					source = thePC.getCharacterDomainKeyed(key);
				}
				else if (TAG_DOMAIN.equals(type))
				{
					source = thePC.getFeatKeyed(key);
				}
				// Fix for bug 1185344
				else if (TAG_ABILITY.equals(type))
				{
					source = thePC.getFeatAutomaticKeyed(key);
				}
				// End of Fix

				if (source == null)
				{
					final String message =
							"Invalid source specification: " + line;
					warnings.add(message);
				}

				break;
			}
		}

		final PCGElement element = tokens.getElements().get(0);

		if (source == null)
		{
			for (PCGElement child : element.getChildren())
			{
				weaponprofs.add(updateProficiencyName(child.getText(), true));
			}
		}
		else
		{
			for (PCGElement child : element.getChildren())
			{
				source.addSelectedWeaponProfBonus(updateProficiencyName(child
					.getText(), true));
			}
		}
	}

	private void parseWeightLine(final String line)
	{
		try
		{
			thePC.setWeight(Integer.parseInt(line
				.substring(TAG_WEIGHT.length() + 1)));
		}
		catch (NumberFormatException nfe)
		{
			final String message = "Illegal Weight line ignored: " + line;
			warnings.add(message);
		}
	}

	private static String shortClassName(final Object o)
	{
		final Class<?> objClass = o.getClass();
		final String pckName = objClass.getPackage().getName();

		return objClass.getName().substring(pckName.length() + 1);
	}

	/**
	 * @deprecated
	 * @param aString
	 * @param decode
	 * @return
	 */
	@Deprecated
	private static String updateProficiencyName(final String aString,
		final boolean decode)
	{
		String profKey = aString;
		if (decode)
		{
			profKey = EntityEncoder.decode(aString);
		}

		// TODO This is totally bogus.  This will never really work properly.
		// This logic should be deprecated and removed.
		if (Globals.getWeaponProfKeyed(profKey) == null)
		{
			int idx = profKey.indexOf("1-H"); //$NON-NLS-1$

			if (idx >= 0)
			{
				profKey =
						profKey.substring(0, idx)
							+ "Exotic" + profKey.substring(idx + 3); //$NON-NLS-1$
			}
			else
			{
				idx = profKey.indexOf("2-H"); //$NON-NLS-1$

				if (idx >= 0)
				{
					profKey =
							profKey.substring(0, idx)
								+ "Martial" + profKey.substring(idx + 3); //$NON-NLS-1$
				}
			}
		}

		return aString;
	}

	private void checkWeaponProficiencies()
	{
		//		thePC.setAutomaticFeatsStable(false);
		thePC.setAutomaticAbilitiesStable(null, false);

		for (final Iterator<String> it = weaponprofs.iterator(); it.hasNext();)
		{
			if (thePC.hasWeaponProfKeyed(it.next()))
			{
				it.remove();
			}
		}

		//
		// For some reason, character had a proficiency that they should not have. Inform
		// the user that they no longer have the proficiency.
		//
		if (weaponprofs.size() > 0)
		{
			String s = weaponprofs.toString();
			s = s.substring(1, s.length() - 1);

			final String message =
					"No longer proficient with following weapon(s):"
						+ Constants.s_LINE_SEP + s;
			warnings.add(message);
		}
	}

	/**
	 * ###############################################################
	 * Character EquipSet Stuff
	 * ###############################################################
	 * @param line
	 **/
	private void parseCalcEquipSet(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			/*
			 * EquipSet is not critical for characters,
			 * no need to stop the load process
			 */
			final String message =
					"Illegal Calc EquipSet line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		final String calcEQId =
				EntityEncoder.decode(tokens.getElements().get(0).getText());

		if (calcEQId != null)
		{
			thePC.setCalcEquipSetId(calcEQId);
		}
	}

	/*
	 * ###############################################################
	 * Character Description/Bio/History methods
	 * ###############################################################
	 */
	private void parseCharacterBioLine(final String line)
	{
		thePC.setBio(EntityEncoder.decode(line.substring(TAG_CHARACTERBIO
			.length() + 1)));
	}

	private void parseEquipmentLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal Equipment line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		final String itemKey;
		Equipment aEquip;

		PCGElement element;
		String tag;

		// the first element defines the item key name
		element = tokens.getElements().get(0);
		itemKey = EntityEncoder.decode(element.getText());

		// might be dynamically created container
		aEquip = thePC.getEquipmentNamed(itemKey);

		if (aEquip == null)
		{
			// Must load custom equipment from the .pcg file
			// before we check the Global list (which may get
			// loaded from customeEquipment.lst) as equipment
			// in the PC's .pcg may contain additional info
			// such as Charges on a wand, etc
			//
			// Make sure that we are not picking up custom items!
			aEquip = EquipmentList.getEquipmentKeyedNoCustom(itemKey);
			if (aEquip != null)
			{
				// standard item
				aEquip = aEquip.clone();
			}
			if (line.indexOf(TAG_CUSTOMIZATION) >= 0)
			{
				// might be customized item
				for (Iterator<PCGElement> it = tokens.getElements().iterator(); it
					.hasNext();)
				{
					element = it.next();

					if (TAG_CUSTOMIZATION.equals(element.getName()))
					{
						String baseItemKey = Constants.EMPTY_STRING;
						String customProperties = Constants.EMPTY_STRING;

						for (PCGElement child : element.getChildren())
						{
							final String childTag = child.getName();

							if (TAG_BASEITEM.equals(childTag))
							{
								baseItemKey =
										EntityEncoder.decode(child.getText());
							}
							else if (TAG_DATA.equals(childTag))
							{
								customProperties =
										EntityEncoder.decode(child.getText());
							}
						}

						if (aEquip != null
							&& baseItemKey.equals(aEquip.getBaseItemName()))
						{
							// We clear out any eqmods that the base item has as the
							// EQMODs on the saved item override them.
							aEquip.clearAllEqModifiers();
							aEquip.load(customProperties, "$", "=", thePC); //$NON-NLS-1$ //$NON-NLS-2$
						}
						else
						{
							// Make sure we are not
							// getting custom item
							final Equipment aEquip2 =
									EquipmentList
										.getEquipmentKeyedNoCustom(baseItemKey);
							if (aEquip2 != null)
							{
								aEquip = aEquip2.clone();
								aEquip.load(customProperties, "$", "=", thePC); //$NON-NLS-1$//$NON-NLS-2$
								aEquip.setOutputName(Constants.EMPTY_STRING);
								if (!aEquip.isType(Constants.s_CUSTOM))
								{
									aEquip.addMyType(Constants.s_CUSTOM);
								}
								EquipmentList.addEquipment(aEquip
									.clone());
							}
						}

						break;
					}
				}

			}

			if (aEquip == null)
			{
				final String msg =
						PropertyFactory.getFormattedString(
							"Warnings.PCGenParser.EquipmentNotFound", //$NON-NLS-1$
							itemKey);
				warnings.add(msg);

				return;
			}

			thePC.addEquipment(aEquip);
		}

		for (final Iterator<PCGElement> it = tokens.getElements().iterator(); it
			.hasNext();)
		{
			element = it.next();
			tag = element.getName();

			if (TAG_QUANTITY.equals(tag))
			{
				float oldQty = aEquip.getQty();
				aEquip.setQty(element.getText());
				thePC.updateEquipmentQty(aEquip, oldQty, aEquip.getQty());
			}
			else if (TAG_OUTPUTORDER.equals(tag))
			{
				int index = 0;

				try
				{
					index = Integer.parseInt(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					// nothing we can or have to do about this
				}

				aEquip.setOutputIndex(index);
				if (aEquip.isAutomatic())
				{
					thePC.cacheOutputIndex(aEquip);
				}
			}
			else if (TAG_COST.equals(tag))
			{
				// TODO This else if switch currently does nothing?
			}
			else if (TAG_WT.equals(tag))
			{
				// TODO This else if switch currently does nothing?
			}
		}
	}

	private void parseEquipmentSetLine(final String line)
	{
		final PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal EquipSet line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		String setName = null;
		String setID = null;
		String itemKey = null;
		String setNote = null;
		Float itemQuantity = null;
		boolean useTempMods = false;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_EQUIPSET.equals(tag))
			{
				setName = EntityEncoder.decode(element.getText());
			}
			else if (TAG_ID.equals(tag))
			{
				setID = element.getText();
			}
			else if (TAG_VALUE.equals(tag))
			{
				itemKey = EntityEncoder.decode(element.getText());
			}
			else if (TAG_QUANTITY.equals(tag))
			{
				try
				{
					itemQuantity = new Float(element.getText());
				}
				catch (NumberFormatException nfe)
				{
					itemQuantity = new Float(0.0f);
				}
			}
			else if (TAG_NOTE.equals(tag))
			{
				setNote = EntityEncoder.decode(element.getText());
			}
			else if (TAG_USETEMPMODS.equals(tag))
			{
				useTempMods = element.getText().endsWith(VALUE_Y);
			}
		}

		if ((setName == null) || Constants.EMPTY_STRING.equals(setName)
			|| (setID == null) || Constants.EMPTY_STRING.equals(setID))
		{
			final String message = "Illegal EquipSet line ignored: " + line;
			warnings.add(message);

			return;
		}

		final EquipSet aEquipSet;
		Equipment aEquip;
		Equipment eqI;

		aEquipSet = new EquipSet(setID, setName);

		if (setNote != null)
		{
			aEquipSet.setNote(setNote);
		}

		if (itemKey != null)
		{
			aEquipSet.setValue(itemKey);
			eqI = thePC.getEquipmentNamed(itemKey);

			if (eqI == null)
			{
				eqI = EquipmentList.getEquipmentNamed(itemKey);
			}

			if (eqI == null)
			{
				final String message = "Could not find equipment: " + itemKey;
				warnings.add(message);

				return;
			}
			aEquip = eqI.clone();

			if (itemQuantity != null)
			{
				aEquipSet.setQty(itemQuantity);
				aEquip.setQty(itemQuantity);
				aEquip.setNumberCarried(itemQuantity);
			}

			// if the idPath is longer than 3
			// it's inside a container
			if ((new StringTokenizer(setID, ".")).countTokens() > 3) //$NON-NLS-1$
			{
				// get parent EquipSet
				final EquipSet aEquipSet2 =
						thePC.getEquipSetByIdPath(aEquipSet.getParentIdPath());

				// get the container
				Equipment aEquip2 = null;

				if (aEquipSet2 != null)
				{
					aEquip2 = aEquipSet2.getItem();
				}

				// add the child to container
				if (aEquip2 != null)
				{
					aEquip2.insertChild(thePC, aEquip);
					aEquip.setParent(aEquip2);
				}
			}

			aEquipSet.setItem(aEquip);
		}

		aEquipSet.setUseTempMods(useTempMods);

		thePC.addEquipSet(aEquipSet);
	}

	/*
	 * ###############################################################
	 * Character Equipment methods
	 * ###############################################################
	 */
	private void parseMoneyLine(final String line)
	{
		thePC.setGold(line.substring(TAG_MONEY.length() + 1));
	}

	/**
	 * ###############################################################
	 * Temporary Bonuses
	 * ###############################################################
	 * @param line
	 **/
	private void parseTempBonusLine(final String line)
	{
		PCGTokenizer tokens;

		try
		{
			tokens = new PCGTokenizer(line);
		}
		catch (PCGParseException pcgpex)
		{
			final String message =
					"Illegal TempBonus line ignored: " + line
						+ Constants.s_LINE_SEP + "Error: "
						+ pcgpex.getMessage();
			warnings.add(message);

			return;
		}

		String cTag = null;
		String tName = null;

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			if (TAG_TEMPBONUS.equals(tag))
			{
				cTag = EntityEncoder.decode(element.getText());
			}
			else if (TAG_TEMPBONUSTARGET.equals(tag))
			{
				tName = EntityEncoder.decode(element.getText());
			}
		}

		if ((cTag == null) || (tName == null))
		{
			warnings.add("Illegal TempBonus line ignored: " + line);

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(cTag, "=", false); //$NON-NLS-1$

		if (aTok.countTokens() < 2)
		{
			return;
		}

		final String cType = aTok.nextToken();
		final String cKey = aTok.nextToken();

		Equipment aEq = null;

		if (!tName.equals(TAG_PC))
		{
			// bonus is applied to an equipment item
			// so create a new one and add to PC
			final Equipment eq = thePC.getEquipmentNamed(tName);

			if (eq == null)
			{
				return;
			}

			aEq = eq.clone();

			//aEq.setWeight("0");
			aEq.resetTempBonusList();
		}

		for (PCGElement element : tokens.getElements())
		{
			final String tag = element.getName();

			final String bonus;
			if (TAG_TEMPBONUSBONUS.equals(tag))
			{
				bonus = EntityEncoder.decode(element.getText());
			}
			else
			{
				continue;
			}

			if ((bonus == null) || (bonus.length() <= 0))
			{
				continue;
			}

			BonusObj newB = null;

			// Check the Creator type so we know what
			// type of object to set as the creator
			if (cType.equals(TAG_FEAT))
			{
				final Ability aFeat =
						Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, cKey);

				//				if (aFeat == null)
				//				{
				//					aFeat = Globals.getAbilityNamed("FEAT", cKey);
				//				}

				if (aFeat != null)
				{
					newB = Bonus.newBonus(bonus);
					newB.setCreatorObject(aFeat);
				}
			}
			else if (cType.equals(TAG_EQUIPMENT))
			{
				Equipment aEquip = thePC.getEquipmentNamed(cKey);

				if (aEquip == null)
				{
					aEquip = EquipmentList.getEquipmentNamed(cKey);
				}

				if (aEquip != null)
				{
					newB = Bonus.newBonus(bonus);
					newB.setCreatorObject(aEquip);
				}
			}
			else if (cType.equals(TAG_CLASS))
			{
				final PCClass aClass = thePC.getClassKeyed(cKey);

				if (aClass == null)
				{
					continue;
				}

				int idx = bonus.indexOf('|');
				newB = Bonus.newBonus(bonus.substring(idx + 1));
				newB.setCreatorObject(aClass);
			}
			else if (cType.equals(TAG_TEMPLATE))
			{
				PCTemplate aTemplate = thePC.getTemplateKeyed(cKey);

				if (aTemplate == null)
				{
					aTemplate = Globals.getTemplateKeyed(cKey);
				}

				if (aTemplate != null)
				{
					newB = Bonus.newBonus(bonus);
					newB.setCreatorObject(aTemplate);
				}
			}
			else if (cType.equals(TAG_SKILL))
			{
				Skill aSkill = thePC.getSkillKeyed(cKey);

				if (aSkill == null)
				{
					aSkill = Globals.getSkillKeyed(cKey);
				}

				if (aSkill != null)
				{
					newB = Bonus.newBonus(bonus);
					newB.setCreatorObject(aSkill);
				}
			}
			else if (cType.equals(TAG_SPELL))
			{
				final Spell aSpell = Globals.getSpellKeyed(cKey);

				if (aSpell != null)
				{
					newB = Bonus.newBonus(bonus);
					newB.setCreatorObject(aSpell);
				}
			}

			if (newB == null)
			{
				return;
			}

			// Check to see if the target was the PC or an Item
			if (tName.equals(TAG_PC))
			{
				newB.setApplied(true);
				newB.setTargetObject(thePC);
				thePC.addTempBonus(newB);
			}
			else
			{
				newB.setApplied(true);
				newB.setTargetObject(aEq);
				aEq.addTempBonus(newB);
				thePC.addTempBonus(newB);
			}
		}

		if (aEq != null)
		{
			aEq.setAppliedName(cKey);
			thePC.addTempBonusItemList(aEq);
		}
	}

	private void sortCharacterSpells()
	{
		// now sort each classes spell list
		for (PCClass pcClass : thePC.getClassList())
		{
			pcClass.getSpellSupport().sortCharacterSpellList();
		}
	}

	/*
	 * currently source is either empty or
	 * PCCLASS|classname|classlevel (means it's a chosen special ability)
	 * PCCLASS=classname|classlevel (means it's a defined special ability)
	 * DEITY=deityname|totallevels
	 */
	private static String sourceElementToString(final PCGElement source)
	{
		String type = Constants.EMPTY_STRING;
		String name = Constants.EMPTY_STRING;
		String level = Constants.EMPTY_STRING;
		String defined = Constants.EMPTY_STRING;

		for (PCGElement child : source.getChildren())
		{
			final String tag = child.getName();

			if (TAG_TYPE.equals(tag))
			{
				type = child.getText();
			}
			else if (TAG_NAME.equals(tag))
			{
				name = child.getText();
			}
			else if (TAG_LEVEL.equals(tag))
			{
				level = child.getText();
			}
			else if (TAG_DEFINED.equals(tag))
			{
				defined = child.getText().toUpperCase();
			}
		}

		//TODO:gorm - guestimate good starting buffer size
		final StringBuffer buffer = new StringBuffer(1000);
		buffer.append(type);
		buffer.append((VALUE_Y.equals(defined)) ? '=' : '|');
		buffer.append(name);

		if (!Constants.EMPTY_STRING.equals(level))
		{
			buffer.append('|');
			buffer.append(level);
		}

		return buffer.toString();
	}

	/*
	 * ###############################################################
	 * Inner classes
	 * ###############################################################
	 */
	private static final class PCGElement
	{
		private final String name;
		private List<PCGElement> children;
		private String text;

		private PCGElement(final String aName)
		{
			this.name = aName;
		}

		/**
		 * Returns a string representation of the element.  This string is
		 * written in XML format.
		 * @return An XML formatted string.
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			//TODO:gorm - optimize stringbuffer size
			final StringBuffer buffer = new StringBuffer(1000);
			buffer.append('<').append(getName()).append('>').append(LINE_SEP);
			buffer
				.append("<text>").append(getText()).append("</text>").append(LINE_SEP); //$NON-NLS-1$//$NON-NLS-2$

			for (PCGElement child : getChildren())
			{
				buffer.append(child.toString()).append(LINE_SEP);
			}

			buffer.append("</").append(getName()).append('>'); //$NON-NLS-1$

			return buffer.toString();
		}

		/**
		 * Returns all the children of this element.
		 * <p><b>Note</b>: This has a side effect of initializing the children
		 * list for the element.
		 * @return A <code>List</code> of children
		 */
		public List<PCGElement> getChildren()
		{
			if (children == null)
			{
				this.children = new ArrayList<PCGElement>(0);
			}

			return children;
		}

		private String getName()
		{
			return name;
		}

		private String getText()
		{
			return (text != null) ? text : Constants.EMPTY_STRING;
		}

		private void addContent(final PCGElement child)
		{
			if (children == null)
			{
				this.children = new ArrayList<PCGElement>(0);
			}

			children.add(child);
		}

		private void addContent(final String argText)
		{
			text = argText;
		}
	}

	private static final class PCGTokenizer
	{
		private final List<PCGElement> elements;
		private final String innerDelimiter;
		private final String nestedStartDelimiter;
		private final String nestedStopDelimiter;
		private final String outerDelimiter;
		private final char nestedStartDelimiterChar;
		private final char nestedStopDelimiterChar;

		/**
		 * Constructor
		 * @param line
		 * @throws PCGParseException
		 */
		private PCGTokenizer(final String line) throws PCGParseException
		{
			this(line, ":|[]"); //$NON-NLS-1$
		}

		/**
		 * Constructor
		 * <br>
		 * @param line           a String to tokenize
		 * @param delimiters     a FOUR-character String specifying the four needed delimiters:
		 *                       <ol>
		 *                           <li>the inner delimiter for a PCGElement</li>
		 *                           <li>the outer delimiter for a PCGElement</li>
		 *                           <li>the start delimiter for nested PCGElements</li>
		 *                           <li>the stop delimiter for nested PCGElement</li>
		 *                       </ol>
		 * @throws PCGParseException
		 */
		private PCGTokenizer(final String line, final String delimiters)
			throws PCGParseException
		{
			final char[] dels = delimiters.toCharArray();

			this.innerDelimiter = String.valueOf(dels[0]);
			this.outerDelimiter = String.valueOf(dels[1]);
			this.nestedStartDelimiter = String.valueOf(dels[2]);
			this.nestedStopDelimiter = String.valueOf(dels[3]);

			this.nestedStartDelimiterChar = nestedStartDelimiter.charAt(0);
			this.nestedStopDelimiterChar = nestedStopDelimiter.charAt(0);

			this.elements = new ArrayList<PCGElement>(0);

			tokenizeLine(line);
		}

		private List<PCGElement> getElements()
		{
			return elements;
		}

		private void checkSyntax(final String line) throws PCGParseException
		{
			final char[] chars = line.toCharArray();

			int delimCount = 0;

			for (int i = 0; i < chars.length; ++i)
			{
				if (chars[i] == nestedStartDelimiterChar)
				{
					++delimCount;
				}
				else if (chars[i] == nestedStopDelimiterChar)
				{
					--delimCount;
				}
			}

			if (delimCount < 0)
			{
				final String message = "Missing " + nestedStartDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax", line,
					message);
			}
			else if (delimCount > 0)
			{
				final String message = "Missing " + nestedStopDelimiter;
				throw new PCGParseException("PCGTokenizer::checkSyntax", line,
					message);
			}
		}

		private void tokenizeLine(final String line) throws PCGParseException
		{
			checkSyntax(line);

			final PCGElement root = new PCGElement("root"); //$NON-NLS-1$
			tokenizeLine(root, line);
			elements.addAll(root.getChildren());
		}

		private void tokenizeLine(final PCGElement parent, final String line)
			throws PCGParseException
		{
			final String dels =
					outerDelimiter + nestedStartDelimiter + nestedStopDelimiter;
			final StringTokenizer tokens =
					new StringTokenizer(line, dels, true);

			int nestedDepth = 0;
			String tag = null;
			final StringBuffer buffer = new StringBuffer(1000);

			while (tokens.hasMoreTokens())
			{
				String token = tokens.nextToken().trim();

				if (token.equals(outerDelimiter))
				{
					if (nestedDepth == 0)
					{
						if (buffer.length() > 0)
						{
							token = buffer.toString();

							int index = token.indexOf(innerDelimiter);

							if (index >= 0)
							{
								buffer.delete(0, buffer.length());

								final PCGElement element =
										new PCGElement(token
											.substring(0, index));
								element.addContent(token.substring(index + 1));
								parent.addContent(element);
							}
							else
							{
								final String message =
										"Malformed PCG element: " + token;
								throw new PCGParseException(
									"PCGTokenizer::tokenizeLine", line, message);
							}
						}
					}
					else
					{
						buffer.append(token);
					}
				}
				else if (token.equals(nestedStartDelimiter))
				{
					if (nestedDepth == 0)
					{
						token = buffer.toString();

						int index = token.indexOf(innerDelimiter);

						if ((index >= 0) && (index == (token.length() - 1)))
						{
							buffer.delete(0, buffer.length());

							tag = token.substring(0, index);
						}
						else
						{
							final String message =
									"Malformed PCG element: " + token;
							throw new PCGParseException(
								"PCGTokenizer::tokenizeLine", line, message);
						}
					}
					else
					{
						buffer.append(token);
					}

					++nestedDepth;
				}
				else if (token.equals(nestedStopDelimiter))
				{
					--nestedDepth;

					if (nestedDepth == 0)
					{
						final PCGElement element = new PCGElement(tag);
						tokenizeLine(element, buffer.toString());
						parent.addContent(element);
						buffer.delete(0, buffer.length());
					}
					else
					{
						buffer.append(token);
					}
				}
				else
				{
					buffer.append(token);
				}
			}

			if (buffer.length() > 0)
			{
				final String token = buffer.toString();

				final int index = token.indexOf(innerDelimiter);

				if (index >= 0)
				{
					buffer.delete(0, buffer.length());

					final PCGElement element =
							new PCGElement(token.substring(0, index));
					element.addContent(token.substring(index + 1));
					parent.addContent(element);
				}
				else
				{
					final String message = "Malformed PCG element: " + token;
					throw new PCGParseException("PCGTokenizer::tokenizeLine",
						line, message);
				}
			}
		}
	}

	/**
	 * Returns the version of the application that wrote the file
	 * @return An <code>int</code> array containing the 3 digit version
	 */
	protected int[] getPcgenVersion()
	{
		return pcgenVersion;
	}
	
	/**
	 * Compare the PCG version with a supplied version number 
	 * @param inVer The version to compare with the PCG version. Must have at least 3 elements.
	 * @return the value 0 if the PCG version is equal to the supplied version; a 
	 * value less than 0 if the PCG version is less than the supplied version; 
	 * and a value greater than 0 if the PCG version is greater than the supplied version.
	 */
	protected int compareVersionTo(int inVer[])
	{
		return CoreUtility.compareVersions(pcgenVersion, inVer);
	}

	/**
	 * Returns any extra version info after the regular version number.
	 * @return String extra version information
	 */
	protected String getPcgenVersionSuffix()
	{
		return pcgenVersionSuffix;
	}

	private void parseLevelAbilityInfo(final PCGElement element,
		final PObject pObj)
	{
		parseLevelAbilityInfo(element, pObj, -9);
	}

	private void parseLevelAbilityInfo(final PCGElement element,
		final PObject pObj, final int level)
	{
		final Iterator<PCGElement> it2 = element.getChildren().iterator();

		if (it2.hasNext())
		{
			LevelAbility la;
			final PCLevelInfo pcLevelInfo;

			final String dString = EntityEncoder.decode(it2.next().getText());

			if (pObj instanceof PCClass)
			{
				la = pObj.addAddList(level, dString);
				pcLevelInfo = thePC.getLevelInfoFor(pObj.getKeyName(), level);
			}
			else
			{
				la = null;

				for (LevelAbility laTemp : pObj.getLevelAbilityList())
				{
					if (laTemp.getTagData().startsWith(dString))
					{
						la = laTemp;
						break;
					}
				}

				if (la == null)
				{
					warnings.add(pObj.getDisplayName() + "("
						+ pObj.getClass().getName()
						+ ")\nCould not find LevelAbility: " + dString);
					//
					// Couldn't find it, so add it and hope for the best...
					//
					la = pObj.addAddList(level, dString);
				}

				pcLevelInfo = null;
			}

			final List<String> choiceList = new ArrayList<String>();

			if (la != null)
			{
				la.process(choiceList, thePC, pcLevelInfo);
				choiceList.clear();

				while (it2.hasNext())
				{
					final String choice =
							EntityEncoder.decode(it2.next().getText());
					choiceList.add(choice);
				}

				if (pcLevelInfo != null)
				{
					la.processChoice(null, choiceList, thePC, pcLevelInfo);
				}
				else
				{
					la.addAllToAssociated(choiceList);
				}
			}
		}
	}

	/**
	 * @return the baseFeatPool
	 */
	public double getBaseFeatPool()
	{
		return baseFeatPool;
	}

	/**
	 * @return the calcFeatPoolAfterLoad
	 */
	public boolean isCalcFeatPoolAfterLoad()
	{
		return calcFeatPoolAfterLoad;
	}
}
