/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens.spell;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.SpellList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.SpellLoader;
import pcgen.persistence.lst.SpellLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractToken implements SpellLstToken
{

	private static final Class<SpellList> SPELLLIST_CLASS = SpellList.class;

	@Override
	public String getTokenName()
	{
		return "CLASSES";
	}

	public boolean parse(Spell spell, String value)
	{
		if (value.equals(".CLEARALL"))
		{
			spell.clearLevelInfo("CLASS");
			return true;
		}
		try
		{
			SpellLoader.setLevelList(spell, "CLASS", value);
			return true;
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error in CLASSES token: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (Constants.LST_DOT_CLEARALL.equals(value))
		{
			Collection<CDOMReference> masterLists =
					context.list.getMasterLists(SPELLLIST_CLASS);
			if (masterLists != null)
			{
				for (CDOMReference list : masterLists)
				{
					context.list.clearMasterList(getTokenName(), spell, list);
				}
			}
			return true;
		}

		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		if (value.indexOf(",,") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator ,, : " + value);
			return false;
		}

		// Note: May contain PRExxx
		String classKey;
		Prerequisite prereq = null;

		int openBracketLoc = value.indexOf('[');
		if (openBracketLoc == -1)
		{
			classKey = value;
		}
		else
		{
			if (value.indexOf(']') != value.length() - 1)
			{
				Logging.errorPrint("Invalid " + getTokenName()
					+ " must end with ']' if it contains a PREREQ tag");
				return false;
			}
			classKey = value.substring(0, openBracketLoc);
			String prereqString =
					value.substring(openBracketLoc + 1, value.length() - 1);
			if (prereqString.length() == 0)
			{
				Logging.errorPrint(getTokenName()
					+ " cannot have empty prerequisite : " + value);
				return false;
			}
			prereq = getPrerequisite(prereqString);
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer pipeTok = new StringTokenizer(classKey, Constants.PIPE);
		HashMapToList<Integer, CDOMReference<SpellList>> map =
				new HashMapToList<Integer, CDOMReference<SpellList>>();

		while (pipeTok.hasMoreTokens())
		{
			// could be name=x or name,name=x
			String tokString = pipeTok.nextToken();

			int equalLoc = tokString.indexOf(Constants.EQUALS);
			if (equalLoc == -1)
			{
				Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (expecting an =): " + tokString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}
			if (equalLoc != tokString.lastIndexOf(Constants.EQUALS))
			{
				Logging.errorPrint("Malformed " + getTokenName()
					+ " Token (more than one =): " + tokString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}

			String nameList = tokString.substring(0, equalLoc);
			String levelString = tokString.substring(equalLoc + 1);
			Integer level;
			try
			{
				level = Integer.valueOf(levelString);
				if (level.intValue() < 0)
				{
					Logging.errorPrint(getTokenName()
						+ " may not use a negative level: " + value);
					return false;
				}
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Malformed Level in " + getTokenName()
					+ " (expected an Integer): " + levelString);
				Logging.errorPrint("Line was: " + value);
				return false;
			}

			if (nameList.charAt(0) == ',')
			{
				Logging.errorPrint(getTokenName()
					+ " classes may not start with , : " + value);
				return false;
			}
			if (nameList.charAt(nameList.length() - 1) == ',')
			{
				Logging.errorPrint(getTokenName()
					+ " classes may not end with , : " + value);
				return false;
			}

			StringTokenizer commaTok =
					new StringTokenizer(nameList, Constants.COMMA);

			while (commaTok.hasMoreTokens())
			{
				CDOMReference<SpellList> ref;
				String token = commaTok.nextToken();
				if (Constants.LST_ALL.equals(token))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(SPELLLIST_CLASS);
				}
				else
				{
					foundOther = true;
					// FIXME I think this really should be a SpellList, not the
					// SpellList itself?
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								SPELLLIST_CLASS, token);
					if (ref == null)
					{
						Logging.errorPrint("  error was in " + getTokenName());
						return false;
					}
				}
				map.addToListFor(level, ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		for (Integer level : map.getKeySet())
		{
			for (CDOMReference<SpellList> ref : map.getListFor(level))
			{
				AssociatedPrereqObject edge =
						context.list.addToMasterList(getTokenName(), spell,
							ref, spell);
				edge.setAssociation(AssociationKey.SPELL_LEVEL, level);
				if (prereq != null)
				{
					edge.addPrerequisite(prereq);
				}
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		DoubleKeyMapToList<Prerequisite, Integer, CDOMReference<SpellList>> dkmtl =
				new DoubleKeyMapToList<Prerequisite, Integer, CDOMReference<SpellList>>();
		for (CDOMReference<SpellList> swl : context.list
			.getMasterLists(SPELLLIST_CLASS))
		{
			GraphChanges<Spell> changes =
					context.list.getChangesInMasterList(getTokenName(), spell,
						swl);
			if (changes == null)
			{
				// Legal if no CLASSES was present in the Spell
				continue;
			}
			// List<String> list = new ArrayList<String>();
			if (changes.hasRemovedItems())
			{
				context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
				return null;
			}
			if (changes.includesGlobalClear())
			{
				// list.add(Constants.LST_DOT_CLEARALL);
			}
			if (changes.hasAddedItems())
			{
				for (LSTWriteable added : changes.getAdded())
				{
					AssociatedPrereqObject assoc =
							changes.getAddedAssociation(added);
					// TODO Check it's actually THIS spell...
					List<Prerequisite> prereqs = assoc.getPrerequisiteList();
					Prerequisite prereq;
					if (prereqs == null || prereqs.size() == 0)
					{
						prereq = null;
					}
					else if (prereqs.size() == 1)
					{
						prereq = prereqs.get(0);
					}
					else
					{
						context.addWriteMessage("Incoming Edge to "
							+ spell.getKey() + " had more than one "
							+ "Prerequisite: " + prereqs.size());
						return null;
					}
					Integer level =
							assoc.getAssociation(AssociationKey.SPELL_LEVEL);
					if (level == null)
					{
						context.addWriteMessage("Incoming Allows Edge to "
							+ spell.getKey() + " had no Spell Level defined");
						return null;
					}
					if (level.intValue() < 0)
					{
						context.addWriteMessage("Incoming Allows Edge to "
							+ spell.getKey() + " had invalid Level: " + level
							+ ". Must be >= 0.");
						return null;
					}
					dkmtl.addToListFor(prereq, level, swl);
				}
			}
		}
		if (dkmtl.isEmpty())
		{
			// Legal if no CLASSES was present in the Spell
			return null;
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CDOMReference<SpellList>> set =
				new TreeSet<CDOMReference<SpellList>>(
					TokenUtilities.REFERENCE_SORTER);
		SortedSet<Integer> levelSet = new TreeSet<Integer>();
		List<String> list = new ArrayList<String>(dkmtl.firstKeyCount());
		for (Prerequisite prereq : dkmtl.getKeySet())
		{
			StringBuilder sb = new StringBuilder();
			boolean needPipe = false;
			levelSet.clear();
			levelSet.addAll(dkmtl.getSecondaryKeySet(prereq));
			for (Integer i : levelSet)
			{
				set.clear();
				set.addAll(dkmtl.getListFor(prereq, i));
				if (needPipe)
				{
					sb.append(Constants.PIPE);
				}
				boolean needComma = false;
				for (LSTWriteable wr : set)
				{
					if (needComma)
					{
						sb.append(',');
					}
					needComma = true;
					sb.append(wr.getLSTformat());
				}
				sb.append('=').append(i);
				needPipe = true;
			}
			if (prereq != null)
			{
				sb.append('[');
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, prereq);
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				sb.append(swriter.toString());
				sb.append(']');
			}
			list.add(sb.toString());
		}
		return list.toArray(new String[list.size()]);
	}
}
