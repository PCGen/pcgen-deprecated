/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  Spell.java
 *
 *  Created on January 16, 2002, 12:27 PM
 */
package gmgen.plugin;

import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import org.jdom.Element;
import pcgen.core.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *@author     devon
 *@since    March 20, 2003
 *@version $Revision$
 */
public class PcgCombatant extends Combatant
{
	protected PlayerCharacter pc;
	protected PcRenderer renderer;
	protected float crAdj = 0;

	/**
	 *  Creates new PcgCombatant
	 *
	 *@param  pc  PCGen pc that this combatant represents
	 */
	public PcgCombatant(PlayerCharacter pc)
	{
		this.pc = pc;
		Globals.setCurrentPC(pc);
		this.init = new PcgSystemInitiative(pc);

		StatList sl = pc.getStatList();
		this.hitPoints = new SystemHP(new SystemAttribute("Constitution", sl.getTotalStatFor("CON")), pc.hitPoints(),
				pc.hitPoints());
		setCombatantType("PC");
	}

	/**
	 *  Constructor for the PcgCombatant object
	 *
	 *@param  pc    PCGen pc that this combatant represents
	 *@param  type  PC/Enemy/Ally/Non Combatant
	 */
	public PcgCombatant(PlayerCharacter pc, String type)
	{
		this(pc);
		setCombatantType(type);
	}

	public PcgCombatant(Element combatant, GMBComponent comp)
	{
		try
		{
			File pcgFile = new File(combatant.getChild("PCG").getAttribute("file").getValue());
			OpenPCGRequestMessage msg = new OpenPCGRequestMessage(comp, pcgFile, true);
			GMBus.send(msg);
			this.pc = msg.getPlayerCharacter();
			Globals.setCurrentPC(pc);
			this.init = new PcgSystemInitiative(pc);

			StatList sl = pc.getStatList();
			this.hitPoints = new SystemHP(new SystemAttribute("Constitution", sl.getTotalStatFor("CON")),
					pc.hitPoints(), pc.hitPoints());

			setStatus(combatant.getAttribute("status").getValue());
			setCombatantType(combatant.getAttribute("type").getValue());

			init.setBonus(combatant.getChild("Initiative").getAttribute("bonus").getIntValue());

			try
			{
				init.setCurrentInitiative(combatant.getChild("Initiative").getAttribute("current").getIntValue());
			}
			catch (Exception e)
			{
				//Not necessarily set
			}

			hitPoints.setMax(combatant.getChild("HitPoints").getAttribute("max").getIntValue());
			hitPoints.setCurrent(combatant.getChild("HitPoints").getAttribute("current").getIntValue());
			hitPoints.setSubdual(combatant.getChild("HitPoints").getAttribute("subdual").getIntValue());
			hitPoints.setState(combatant.getChild("HitPoints").getAttribute("state").getValue());
		}
		catch (Exception e)
		{
			Logging.errorPrint("Initiative", e);
		}
	}

	/**
	 *  Adjusts the CR for this combatant
	 *
	 *@param  cr  new CR value
	 */
	public void setCR(float cr)
	{
		Globals.setCurrentPC(pc);
		this.crAdj = cr - pc.calcCR();
	}

	/**
	 *  Gets the CR for the character
	 *
	 *@return    CR
	 */
	public float getCR()
	{
		Globals.setCurrentPC(pc);

		return pc.calcCR() + crAdj;
	}

	/**
	 *  Sets the name of the character
	 *
	 *@param  name  The new name
	 */
	public void setName(String name)
	{
		pc.setName(name);
		pc.setDirty(true);
	}

	/**
	 *  Gets the name of the PC
	 *
	 *@return    The name
	 */
	public String getName()
	{
		return pc.getName();
	}

	/**
	 *  Gets the PCGen PC of the PcgCombatant object
	 *
	 *@return    The PCGen PC
	 */
	public PlayerCharacter getPC()
	{
		return pc;
	}

	/**
	 *  Sets the player's name of the PcgCombatant object
	 *
	 *@param  player  The new player's name
	 */
	public void setPlayer(String player)
	{
		pc.setPlayersName(player);
		pc.setDirty(true);
	}

	/**
	 *  Gets the player's name of the PcgCombatant object
	 *
	 *@return    The player's name
	 */
	public String getPlayer()
	{
		return pc.getPlayersName();
	}

	public Element getSaveElement()
	{
		Element retElement = new Element("PcgCombatant");
		Element initiative = new Element("Initiative");
		Element hp = new Element("HitPoints");
		Element pcg = new Element("PCG");

		pcg.setAttribute("file", pc.getFileName() + "");
		retElement.addContent(pcg);

		initiative.setAttribute("bonus", init.getModifier() + "");

		if (init.getCurrentInitiative() > 0)
		{
			initiative.setAttribute("current", init.getCurrentInitiative() + "");
		}

		retElement.addContent(initiative);

		hp.setAttribute("current", hitPoints.getCurrent() + "");
		hp.setAttribute("subdual", hitPoints.getSubdual() + "");
		hp.setAttribute("max", hitPoints.getMax() + "");
		hp.setAttribute("state", hitPoints.getState() + "");
		retElement.addContent(hp);

		retElement.setAttribute("name", getName());
		retElement.setAttribute("player", getPlayer());
		retElement.setAttribute("status", getStatus());
		retElement.setAttribute("type", getCombatantType());

		return retElement;
	}

	/**
	 *  Set the experience value for this character
	 *
	 *@param  experience  Experience value
	 */
	public void setXP(int experience)
	{
		pc.setXP(experience);
		pc.setDirty(true);
	}

	/**
	 *  Gets the experience value for the character
	 *
	 *@return    Experience value
	 */
	public int getXP()
	{
		return pc.getXP();
	}

	/**
	 *  changes the value of a table field in the backend data set
	 *
	 *@param  columnOrder  A list of columns in order for the table
	 *@param  colNumber    What column number has been edited
	 *@param  data         The new value for the field
	 */
	public void editRow(List<String> columnOrder, int colNumber, Object data)
	{
		String columnName = columnOrder.get(colNumber);
		String strData = String.valueOf(data);

		//Determine which row was edited
		if (columnName.equals("Name"))
		{
			// Character's Name
			setName(strData);
		}
		else if (columnName.equals("Player"))
		{
			// Player's Name
			setPlayer(strData);
		}
		else if (columnName.equals("Status"))
		{
			// XML Combatant's Status
			setStatus(strData);
		}
		else if (columnName.equals("+"))
		{
			// Initative bonus
			Integer intData = Integer.valueOf(strData);
			init.setBonus(intData.intValue());
		}
		else if (columnName.equals("Init"))
		{
			// Initative
			Integer intData = Integer.valueOf(strData);
			init.setCurrentInitiative(intData.intValue());
		}
		else if (columnName.equals("#"))
		{
			// Number (for tokens)
			Integer intData = Integer.valueOf(strData);
			setNumber(intData.intValue());
		}
		else if (columnName.equals("HP"))
		{
			// Current Hit Points
			Integer intData = Integer.valueOf(strData);
			hitPoints.setCurrent(intData.intValue());
		}
		else if (columnName.equals("HP Max"))
		{
			// Maximum Hit Points
			Integer intData = Integer.valueOf(strData);
			hitPoints.setMax(intData.intValue());
		}
		else if (columnName.equals("Dur"))
		{
			// Duration
			Integer intData = Integer.valueOf(strData);
			setDuration(intData.intValue());
		}
		else if (columnName.equals("Type"))
		{
			// Type
			setCombatantType(strData);
		}
	}

	public String toHtmlString()
	{
		if(renderer == null) {
			renderer = new PcRenderer();
		}
		return renderer.getHtmlText();
	}

	protected class PcRenderer {
		protected String htmlString;
		protected int serial = 0;

		public PcRenderer()
		{
			// Do Nothing
		}

		/**
		 * <p>
		 * This sets the text of the JTextPane for the specified PC. It uses an
		 * output sheet template, specified by the templateName option; it uses
		 * <code>pcgen.io.ExportHandler</code> to transform the template file
		 * into an StringWriter, and then sets the text of the text pane as html.
		 * This allows us easy access to changing the content or format of the stat
		 * block, and also allows us to easily use a different output format if
		 * necessary.
		 * </p>
		 * @return HTML text
		 */
		public String getHtmlText()
		{
			if(serial < pc.getSerial() || htmlString == null) {
				StringBuffer statBuf = new StringBuffer();

				statBuf.append("<html>");

				Globals.setCurrentPC(pc);
				statBuf.append(getStatBlockHeader());
				statBuf.append("<body class='Normal' lang='EN-US'>");
				statBuf.append(getStatBlockTitle());
				statBuf.append(getStatBlockCore());
				statBuf.append("<DIV style='MARGIN: 0px 10px'>");
				statBuf.append(getStatBlockLineSkills());
				statBuf.append(getStatBlockLinePossessions());

				try
				{
					statBuf.append(getStatBlockLineSpells());
				}
				catch (Exception e)
				{
					Logging.errorPrint(e.getMessage(), e);
				}

				statBuf.append("</DIV>");

				statBuf.append("<br>");

				statBuf.append("</html>");

				serial = pc.getSerial();
				htmlString = statBuf.toString();
			}
			return htmlString;

			//TODO: As Outputsheet is *always* false, I commented out the unreachable code.
			//TODO: I will delete it sometime after january unless this has been changed. JK 2003-12-28
			//TODO: This was changed this way so we could easily re-enable the bypassed code if we managed
			//      to get the ExportHandler stuff working any faster.  RML 2004-01-30
			//boolean outputSheet = false;
			//if(outputSheet) {
			//	String baseDir = SettingsHandler.getGmgenPluginDir().toString();
			//	String initiativeDir =
			//		SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".initiativeDir", "Initiative");
			//	String templateName = SettingsHandler.getGMGenOption(InitiativePlugin.LOG_NAME + ".templateName", "csheet_gmgen_statblock.htm");
			//	File template = new File(baseDir + File.separator + initiativeDir + File.separator + templateName);
			//	ExportHandler export = new ExportHandler(template);
			//	StringWriter sWriter = new StringWriter();
			//	export.write(pc,new BufferedWriter(sWriter));
			//	aPane.setEditorKit(aPane.getEditorKitForContentType("text/html"));
			//	aPane.setText(sWriter.toString());
			//}
			//else {
			//int bonus = 0;
		}

		protected String getStatBlockCore()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<font class='type'>CR</font> ");
			statBuf.append(pcOut.getCR()); //|CR|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Size</font> ");
			statBuf.append(pcOut.getSize()); //|SIZE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Type</font> ");
			statBuf.append(pcOut.getRaceType()); //|TYPE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>HD</font> ");
			statBuf.append(pcOut.getHitDice()); //|HITDICE|
			statBuf.append("; ");

			statBuf.append("<font class='type'>hp</font> ");
			statBuf.append(pcOut.getHitPoints()); //|HP|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Init</font> <font class='highlight'>");
			statBuf.append(pcOut.getInitTotal()); //|INITIATIVEMOD|
			statBuf.append("</font> (");
			statBuf.append(pcOut.getInitStatMod()); //|STAT.1.MOD|
			statBuf.append("Dex, ");
			statBuf.append(pcOut.getInitMiscMod()); //|INITIATIVEMISC|
			statBuf.append("Misc); ");

			statBuf.append("<font class='type'>Spd</font> ");
			statBuf.append(pcOut.getSpeed()); //|MOVEMENT|
			statBuf.append("; ");

			statBuf.append("<font class='type'>AC</font> <font class='highlight'>");
			statBuf.append(pcOut.getAC()); //|AC.Total|
			statBuf.append("</font> (flatfooted <font class='highlight'>");
			statBuf.append(pcOut.getACFlatFooted()); //|AC.Flatfooted|
			statBuf.append("</font>, touch <font class='highlight'>");
			statBuf.append(pcOut.getACTouch()); //|AC.Touch|
			statBuf.append("</font>); ");

			statBuf.append("<font class='type'>Melee:</font> <a href='attack:Melee\\");
			statBuf.append(pcOut.getMeleeTotal()); //|ATTACK.MELEE.TOTAL|
			statBuf.append("' class='highlight'>");
			statBuf.append(pcOut.getMeleeTotal()); //|ATTACK.MELEE.TOTAL|
			statBuf.append("</a>; ");

			statBuf.append("<font class='type'>Ranged:</font> <a href='attack:Ranged\\");
			statBuf.append(pcOut.getRangedTotal()); //|ATTACK.RANGED.TOTAL|
			statBuf.append("' class='highlight'>");
			statBuf.append(pcOut.getRangedTotal()); //|ATTACK.RANGED.TOTAL|
			statBuf.append("</a>; ");

			statBuf.append("<font class='type'>Weapons:</font>");

			List<Equipment> weaponList = pc.getExpandedWeapons(Constants.MERGE_ALL);

			for (int i = 0; i < weaponList.size(); i++)
			{
				Equipment eq = weaponList.get(i);
				statBuf.append("<a href=" + '"' + "attack:");
				statBuf.append(pcOut.getWeaponName(eq)); //|WEAPON.%weap.NAME|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponToHit(i)); //|WEAPON.%weap.TOTALHIT|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponRange(eq)); //|WEAPON.%weap.RANGE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponType(eq)); //|WEAPON.%weap.TYPE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponDamage(i)); //|WEAPON.%weap.DAMAGE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponCritRange(i)); //|WEAPON.%weap.CRIT|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponCritMult(i)); //|WEAPON.%weap.MULT|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponHand(eq)); //|WEAPON.%weap.HAND|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponSize(eq)); //|WEAPON.%weap.SIZE|
				statBuf.append("\\");
				statBuf.append(pcOut.getWeaponSpecialProperties(eq)); //|WEAPON.%weap.SPROP|
				statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

				statBuf.append(pcOut.getWeaponName(eq)); //|WEAPON.%weap.NAME|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponToHit(i)); //|WEAPON.%weap.TOTALHIT|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponRange(eq)); //|WEAPON.%weap.RANGE|
				statBuf.append("/");
				statBuf.append(pcOut.getWeaponType(eq)); //|WEAPON.%weap.TYPE|
				statBuf.append(" (");
				statBuf.append(pcOut.getWeaponDamage(i)); //|WEAPON.%weap.DAMAGE|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponCritRange(i)); //|WEAPON.%weap.CRIT|
				statBuf.append("/x");
				statBuf.append(pcOut.getWeaponCritMult(i)); //|WEAPON.%weap.MULT|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponHand(eq)); //|WEAPON.%weap.HAND|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponSize(eq)); //|WEAPON.%weap.SIZE|
				statBuf.append(" ");
				statBuf.append(pcOut.getWeaponSpecialProperties(eq)); //|WEAPON.%weap.SPROP|
				statBuf.append(") </a> or ");
			}

			//Unarmed attack
			statBuf.append("<a href=" + '"' + "attack:Unarmed\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT")); //|WEAPONH.TOTALHIT|
			statBuf.append("\\\\B\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

			statBuf.append("Unarmed ");
			statBuf.append(pcOut.getExportToken("WEAPONH.TOTALHIT")); //|WEAPONH.TOTALHIT|
			statBuf.append(" (");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append(" ");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("/x");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append(") </a> or ");
			//End unarmed attack

			//Grapple
			statBuf.append("<a href=" + '"' + "attack:Grapple\\");
			statBuf.append(pcOut.getExportToken("ATTACK.GRAPPLE.TOTAL")); //|WEAPONH.TOTALHIT|
			statBuf.append("\\\\B\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("\\");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append('"' + " class=" + '"' + "dialog" + '"' + "> ");

			statBuf.append("Grapple ");
			statBuf.append(pcOut.getExportToken("ATTACK.GRAPPLE.TOTAL")); //|WEAPONH.TOTALHIT|
			statBuf.append(" (");
			statBuf.append(pcOut.getExportToken("WEAPONH.DAMAGE")); //|WEAPONH.DAMAGE|
			statBuf.append(" ");
			statBuf.append(pcOut.getExportToken("WEAPONH.CRIT")); //|WEAPONH.CRIT|
			statBuf.append("/x");
			statBuf.append(pcOut.getExportToken("WEAPONH.MULT")); //|WEAPONH.MULT|
			statBuf.append(")</a>; ");
			//End Grapple

			statBuf.append("<font class='type'>SA:</font> ");
			statBuf.append(pcOut.getSpecialAbilities()); //|SPECIALLIST|

			int turnTimes = pc.getVariableValue("TurnTimesUndead", "").intValue();
			if (turnTimes > 0)
			{
				int turnDieNumber = pc.getVariableValue("TurnDiceUndead", "").intValue();
				int turnDieSize = pc.getVariableValue("TurnDieSizeUndead", "").intValue();
				int turnDamage = pc.getVariableValue("TurnDamagePlusUndead", "").intValue();
				int turnLevel = pc.getVariableValue("TurnLevelUndead", "").intValue();
				int turnCheck = pc.getVariableValue("TurnCheckUndead", "").intValue();

				statBuf.append("; <font class='type'>Turn/Rebuke Undead:</font> Turning level "
						+ "<a href=" + '"' + "dice:Turn Undead (Max HD Affected)\\"
						+ "max(min(max((ceil((1d20" + (turnCheck > 0 ? "+" : "") + turnCheck + ")/3)-4),-4),4)+" + turnLevel + ",0)"
						+ '"' + " class=" + '"' + "dialog" + '"' + "> "
						+ turnLevel
						+ "</a>, Turn Damage: "
						+ "<a href=" + '"' + "dice:Turn Damage (Total HD Affected)\\"
						+ "max(" + turnDieNumber + "d" + turnDieSize + (turnDamage > 0 ? "+" : "") + turnDamage + ",0)"
						+ '"' + " class=" + '"' + "dialog" + '"' + "> "
						+ turnDieNumber + "d" + turnDieSize + (turnDamage > 0 ? "+" : "") + turnDamage
						+ "</a>, "
						+ turnTimes
						+ "/day");
			}
			statBuf.append("; ");

			statBuf.append("<font class='type'>Vision:</font> ");
			statBuf.append(pcOut.getVision()); //|VISION|
			statBuf.append(" ");

			statBuf.append("<font class='type'>AL:</font> ");
			statBuf.append(pcOut.getAlignmentShort()); //|ALIGNMENT.SHORT|
			statBuf.append("; ");

			statBuf.append("<font class='type'>Sv:</font> Fort <font class='highlight'>");
			statBuf.append("<a href='save:FORTITUDE\\");
			statBuf.append(pcOut.getSaveFort()); //|CHECK.FORTITUDE.TOTAL|
			statBuf.append("' class='highlight'> ");
			statBuf.append(pcOut.getSaveFort()); //|CHECK.FORTITUDE.TOTAL|
			statBuf.append("</a>");
			statBuf.append("</font>, Ref <font class='highlight'>");
			statBuf.append("<a href='save:REFLEX\\");
			statBuf.append(pcOut.getSaveRef()); //|CHECK.REFLEX.TOTAL|
			statBuf.append("' class='highlight'> ");
			statBuf.append(pcOut.getSaveRef()); //|CHECK.REFLEX.TOTAL|
			statBuf.append("</a>");
			statBuf.append("</font>, Will <font class='highlight'>");
			statBuf.append("<a href='save:WILL\\");
			statBuf.append(pcOut.getSaveWill()); //|CHECK.WILL.TOTAL|
			statBuf.append("' class='highlight'> ");
			statBuf.append(pcOut.getSaveWill()); //|CHECK.WILL.TOTAL|
			statBuf.append("</a>");
			statBuf.append("</font>; ");

			StatList sl = pcOut.getStatList();

			for (int i = 0; i < sl.size(); i++)
			{
				PCStat stat = sl.getStatAt(i);

				if (pc.isNonAbility(i))
				{
					statBuf.append("<font class='type'>");
					statBuf.append(stat.getAbb()); //|STAT.%stat.NAME|
					statBuf.append("</font>");

					statBuf.append("*"); //|STAT.%stat|
					statBuf.append("&nbsp;(");
					statBuf.append("0"); //|STAT.%stat.MOD|
					statBuf.append(") ");
				}
				else
				{
					statBuf.append("<font class='type'>");
					statBuf.append(stat.getAbb()); //|STAT.%stat.NAME|
					statBuf.append("</font> ");

					statBuf.append(pcOut.getStat(stat.getAbb())); //|STAT.%stat|
					statBuf.append("&nbsp;(");
					statBuf.append("<a href='check:");
					statBuf.append(stat.getAbb()); //|STAT.%stat.NAME|
					statBuf.append("\\1d20");
					statBuf.append(pcOut.getStatMod(stat.getAbb())); //|STAT.%stat.MOD|
					statBuf.append("' class='dialog'>");
					statBuf.append(pcOut.getStatMod(stat.getAbb())); //|STAT.%stat.MOD|
					statBuf.append("</a>) ");
				}
			}

			statBuf.append("</p>");

			return statBuf.toString();
		}

		protected String getStatBlockHeader()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<head><title>");
			statBuf.append(pcOut.getName()); //|NAME|
			statBuf.append(" - ");
			statBuf.append(pc.getPlayersName()); //|PLAYERNAME|
			statBuf.append("(");
			statBuf.append(pc.getCostPool()); //|POOL.COST|
			statBuf.append(" Points) in GMGEN Statblock Format");
			statBuf.append("</title>");
			statBuf.append("<style type='text/css'>");
			statBuf.append("a:link {color: #006699}");
			statBuf.append("a:visited {color: #006699}");
			statBuf.append("a:hover {color: #006699}");
			statBuf.append("a:active {color: #006699}");
			statBuf.append(".type {color:#555555;font-weight:bold}");
			statBuf.append(".highlight {color:#FF0000}");
			statBuf.append(".dialog {color:#006699}");
			statBuf.append("</style></head>");

			return statBuf.toString();
		}

		protected String getStatBlockLinePossessions()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p><font class='type'>Possessions:</font>&nbsp;");
			statBuf.append(pcOut.getEquipmentList()); //|FOR.0,(COUNT[EQUIPMENT]+1),1,&nbsp;\EQ.%.QTY\&nbsp;\EQ.%.NAME\, ,COMMA,1|
			statBuf.append("</p>");

			return statBuf.toString();
		}

		protected String getStatBlockLineSkills()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p><font class='type'>Skills and Feats:</font>&nbsp;");

			pc.getAllSkillList(true); //force refresh of skills

			/*
			 * TODO includeSkill is written and never read... so what does it do?
			 * - thpr 10/27/06
			 */
			int includeSkills = SettingsHandler.getIncludeSkills();

			if (includeSkills == 3)
			{
				includeSkills = SettingsHandler.getSkillsTab_IncludeSkills();
			}

			ArrayList<Skill> skillList = pc.getSkillListInOutputOrder(pc
				.getPartialSkillList(Visibility.EXPORT));
			boolean firstLine = true;

			for ( Skill skill : skillList )
			{
				if (!firstLine)
				{
					statBuf.append(", ");
				}

				firstLine = false;

				int modSkill;

				if (skill.getKeyStat().compareToIgnoreCase(Constants.s_NONE) != 0)
				{
					modSkill = skill.modifier(pc).intValue() - pc.getStatList().getStatModFor(skill.getKeyStat());
					Logging.debugPrint("modSkill: " + modSkill);
				}

				int temp = skill.modifier(pc).intValue() + skill.getTotalRank(pc).intValue();

				statBuf.append("<a href='skill:");
				statBuf.append(skill.getOutputName()); //|SKILL.%skill|
				statBuf.append("\\1d20");
				statBuf.append(((temp < 0) ? Integer.toString(temp) : "+" + temp)); //|SKILL.%skill.TOTAL|
				statBuf.append("' class='dialog'> ");

				statBuf.append(skill.getOutputName()); //|SKILL.%skill|
				statBuf.append(" (");
				statBuf.append(temp); //|SKILL.%skill.TOTAL|
				statBuf.append(")</a>");
			}

			statBuf.append("; ");
			statBuf.append(pcOut.getFeatList()); //|FEATLIST|
			statBuf.append("</p>");

			return statBuf.toString();
		}

		protected String getStatBlockLineSpells()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);
			if (pc.hasCharacterDomainList())
			{
				//Domains
				//Deity
				statBuf.append("<p>");
				statBuf.append("<font class='type'>Deity:</font>");
				statBuf.append(pcOut.getDeity());
				statBuf.append("<br>");
				statBuf.append("<font class='type'>Domains:</font>&nbsp;");

				//Domain List with powers
				boolean firstLine = true;

				for ( CharacterDomain cd : pc.getCharacterDomainList() )
				{
					if (!firstLine)
					{
						statBuf.append(", ");
					}

					firstLine = false;

					Domain dom = cd.getDomain();
					statBuf.append(pcOut.getDomainName(dom)); //|DOMAIN|
					statBuf.append(" (");
					statBuf.append(pcOut.getDomainPower(pc, dom)); //|DOMAIN.POWER|
					statBuf.append(")");
				}

				statBuf.append("</p>");
			}

			statBuf.append("<p>");

			/*
				 <p>
				 <!-- Start Racial Innate Spells -->
				 |FOR,%spellrace,COUNT[SPELLRACE],COUNT[SPELLRACE],1,0|
				 |IIF(%spellrace:0)|
				 <!-- No innate spells -->
				 |ELSE|
				 |FOR,%spellbook,1,1,1,1|
				 |FOR,%class,0,0,1,1|
				 |FOR,%level,0,0,1,1|
				 |%SPELLLISTBOOK%class.%level.%spellbook|
				 <font class="type">Racial Innate Spells</font>
				 <br>
				 <!-- Start Racial Innate Spell listing -->
				 |FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
				 <a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
				 |SPELLMEM.%class.%spellbook.%level.%spell.NAME|
				 </a>
				 (|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
				 |ENDFOR|
				 |%|
				 |ENDFOR|
				 |ENDFOR|
				 |ENDFOR|
				 <!-- End Racial Innate Spells -->
				 <!-- Start Other Innate Spells -->
				 |FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0|
				 <br>
				 |FOR,%class,0,0,1,1|
				 |FOR,%level,0,0,1,1|
				 |%SPELLLISTBOOK%class.%level.%spellbook|
				 <br>
				 <font class="type">|SPELLBOOKNAME.%spellbook| Innate Spells</font>
				 <br>
				 |FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
				 <a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
				 |SPELLMEM.%class.%spellbook.%level.%spell.NAME|
				 </a>
				 (|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
				 |ENDFOR|
				 |%|
				 |ENDFOR|
				 |ENDFOR|
				 |ENDFOR|
				 <!-- End Other Innate Spells -->
				 |ENDIF|
				 |ENDFOR|
				 <!-- End Innate Spells -->
				 |FOR,%spellbook,0,0,1,0|
				 |FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1|
				 |%SPELLLISTCLASS%class|
				 <!-- START Spell list Header Table (Known) -->
				 <br>
				 <font class="type">|SPELLLISTCLASS.%class|
				 |IIF(SPELLLISTCLASS.%class:Psychic Warrior.OR.SPELLLISTCLASS.%class:Psion)|
				 Powers
				 |ELSE|
				 Spells Known
				 |ENDIF|
				 </font>
				 <br>
				 <!-- End Spell List Header Table (Known) -->
				 <!-- Start Known Spells -->
				 |FOR,%level,0,9,1,1|
				 |FOR,%spellcount,COUNT[SPELLSINBOOK%class.%spellbook.%level],COUNT[SPELLSINBOOK%class.%spellbook.%level],1,0|
				 |IIF(%spellcount:0)|
				 |ELSE|
				 <br>
				 <font class="type">Level %level</font>
				 <br>
				 |FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
				 <a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
				 |SPELLMEM.%class.%spellbook.%level.%spell.NAME|
				 </a>,
				 |IIF(SPELLLISTCLASS.%class:Psychic Warrior.OR.SPELLLISTCLASS.%class:Psion)|
				 |FOR,%ppcost,(%level*2)-1,(%level*2)-1,1,1|
				 |IIF(%ppcost:-1)|
				 <i>PP:</i> 0/1
				 |ELSE|
				 <i>PP:</i> %ppcost
				 |ENDIF|
				 |ENDFOR|
				 |ENDIF|
				 |ENDFOR|
				 |ENDIF|
				 |ENDFOR|
				 |ENDFOR|
				 <br>
				 |%|
				 |ENDFOR|
				 |ENDFOR|
				 <!-- End Known Spells -->
				 <!-- ================================================================ -->
				 <!-- Start Prepared Spells -->
				 |FOR,%memorised,COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2,COUNT[SPELLRACE]+COUNT[SPELLBOOKS]-2,1,0|
				 |IIF(%memorised:0)|
				 |ELSE|
				 <!-- Start Regular Prepared -->
				 |FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0|
				 |FOR,%foo,COUNT[SPELLRACE],COUNT[SPELLRACE],1,1|
				 |FOR,%bar,COUNT[SPELLSINBOOK0.%spellbook.0],COUNT[SPELLSINBOOK0.%spellbook.0],1,1|
				 |IIF(%foo:0.OR.%bar:0)|
				 <br>
				 <font class="type">|SPELLBOOKNAME.%spellbook| Spellbook:</font>
				 <br>
				 |FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1|
				 <br>
				 <font class="type">|SPELLLISTCLASS.%class|</font>
				 <br>
				 |FOR,%level,0,9,1,1|
				 |FOR,%spelllevelcount,COUNT[SPELLSINBOOK%class.%spellbook.%level],COUNT[SPELLSINBOOK%class.%spellbook.%level],1,0|
				 |IIF(%spelllevelcount:0)|
				 <!-- no memorized spells for SPELLSINBOOK%class %spellbook %level -->
				 |ELSE|
				 <br>
				 <font class="type">Level %level</font>
				 <br>
				 |FOR,%spell,0,COUNT[SPELLSINBOOK%class.%spellbook.%level]-1,1,0|
				 <a href="spell:|SPELLMEM.%class.%spellbook.%level.%spell.NAME|\|SPELLMEM.%class.%spellbook.%level.%spell.DESC|\|SPELLMEM.%class.%spellbook.%level.%spell.RANGE|\|SPELLMEM.%class.%spellbook.%level.%spell.CASTINGTIME|\|SPELLMEM.%class.%spellbook.%level.%spell.SAVEINFO|\|SPELLMEM.%class.%spellbook.%level.%spell.DURATION|\|SPELLMEM.%class.%spellbook.%level.%spell.TARGET|" class="dialog">
				 |SPELLMEM.%class.%spellbook.%level.%spell.NAME|
				 </a>
				 (|SPELLMEM.%class.%spellbook.%level.%spell.TIMES|)(DC:|SPELLMEM.%class.%spellbook.%level.%spell.DC|),
				 |ENDFOR|
				 |ENDIF|
				 |ENDFOR|
				 <!-- END FOR,%spellcount,COUNT[SPELLSINBOOK%class.%spellbook.0],COUNT[SPELLSINBOOK%class.%spellbook.0],1,0 -->
				 |ENDFOR|
				 <!-- END SPELLLISTCLASS%class -->
				 |%|
				 <!-- END FOR,%class,COUNT[SPELLRACE],COUNT[SPELLRACE]+COUNT[CLASSES]-1,1,1 -->
				 |ENDFOR|
				 |ELSE|
				 |ENDIF|
				 <!-- END FOR,%bar,COUNT[SPELLSINBOOK0.%spellbook.0],COUNT[SPELLSINBOOK0.%spellbook.0],1,1 -->
				 |ENDFOR|
				 <!-- END FOR,%foo,COUNT[SPELLRACE],COUNT[SPELLRACE],1,1 -->
				 |ENDFOR|
				 <!-- END FOR,%spellbook,2,COUNT[SPELLBOOKS]-1,1,0 -->
				 |ENDFOR|
				 <!-- ### END class Spellbook memorized spells ### -->
				 <!-- START FALSE IIF(%memorised:0) -->
				 |ENDIF|
				 |ENDFOR|
				 <!-- ### END MEMORIZED ### -->
				 <!-- End Prepared Spells -->
			 */
			ArrayList<PObject> classList = new ArrayList<PObject>(pc.getClassList());
			classList.add(pc.getRace());

			List<String> bookList = new ArrayList<String>(pc.getSpellBooks());
			bookList.add(Globals.getDefaultSpellBook());
			for ( String book : bookList )
			{
				statBlockLineSpellBook(pc, statBuf, classList, book);
			}

			return statBuf.toString();
		}

		protected void statBlockLineSpellBook(PlayerCharacter aPC, StringBuffer statBuf, ArrayList<PObject> classList, String spellBookName)
		{
			boolean printedFirst = false;
			for ( PObject pObj : classList )
			{
				if (pObj != null)
				{
					int level = 0;
					List<CharacterSpell> spellList = pObj.getSpellSupport().getCharacterSpell(null, spellBookName, level);

					if (spellList.size() >= 1)
					{
						if (!printedFirst)
						{
							statBuf.append("<br><font class='type'>" + spellBookName + ":</font><br> ");
						}
						statBuf.append("<font class='type'>" + pObj.getDisplayName() + ":</font><br> ");
						printedFirst = true;
					}

					while (spellList.size() >= 1)
					{
						statBuf.append("<font class='type'>Level " + level + ":</font> ");

						boolean firstLine = true;

						for ( CharacterSpell cs : spellList )
						{
							if (!firstLine)
							{
								statBuf.append(", ");
							}

							firstLine = false;

							Spell spell = cs.getSpell();
							statBuf.append("<a href=" + '"' + "spell:");
							statBuf.append(spell.getDisplayName());
							statBuf.append("\\");
							statBuf.append(aPC.parseSpellString(spell, spell.getDescription(aPC), cs.getOwner()));
							statBuf.append("\\");
							statBuf.append(spell.getRange());
							statBuf.append("\\");
							statBuf.append(spell.getCastingTime());
							statBuf.append("\\");
							statBuf.append(spell.getSaveInfo());
							statBuf.append("\\");
							statBuf.append(aPC.parseSpellString(spell, spell.getDuration(), cs.getOwner()));
							statBuf.append("\\");
							statBuf.append(aPC.parseSpellString(spell, spell.getTarget(), cs.getOwner()));
							statBuf.append('"' + " class=" + '"' + "dialog" + '"' + ">");

							statBuf.append(spell.getDisplayName());
							statBuf.append("</a>");
						}

						level++;
						statBuf.append("<br>");
						spellList = pObj.getSpellSupport().getCharacterSpell(null, spellBookName, level);
					}
				}
			}
		}

		protected String getStatBlockTitle()
		{
			StringBuffer statBuf = new StringBuffer();
			PlayerCharacterOutput pcOut = new PlayerCharacterOutput(pc);

			statBuf.append("<p class='gork'><font size='+1'><b>");
			statBuf.append(pcOut.getName()); //|NAME|
			statBuf.append(", ");
			statBuf.append(pcOut.getGender()); //|GENDER|
			statBuf.append(" ");
			statBuf.append(pcOut.getRaceName()); //|RACE|

			String region = pcOut.getRegion(); //|REGION|.|%|

			if (!"".equals(region) && (region != null) && !"None".equals(region))
			{
				statBuf.append(" From " + region + " ");
			}

			statBuf.append(pcOut.getClasses() + " "); //|CLASSLIST|
			statBuf.append("</b></font></p>");

			return statBuf.toString();
		}
	}
}
