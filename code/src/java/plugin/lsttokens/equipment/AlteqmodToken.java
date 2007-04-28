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
package plugin.lsttokens.equipment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

/**
 * Deals with ALTEQMOD token
 */
public class AlteqmodToken implements EquipmentLstToken
{
	private static final Class<EquipmentModifier> EQUIPMENT_MODIFIER_CLASS =
			EquipmentModifier.class;

	public String getTokenName()
	{
		return "ALTEQMOD";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addEqModifiers(value, false);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " may not have empty argument");
			return false;
		}
		if (Constants.LST_NONE.equals(value))
		{
			return true;
		}
		if (value.charAt(0) == '.')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with . : " + value);
			return false;
		}
		if (value.charAt(value.length() - 1) == '.')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with . : " + value);
			return false;
		}
		if (value.indexOf("..") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator .. : " + value);
			return false;
		}

		StringTokenizer dotTok = new StringTokenizer(value, Constants.DOT);
		DoubleKeyMap<CDOMReference<EquipmentModifier>, AssociationKey<String>, String> dkm =
				new DoubleKeyMap<CDOMReference<EquipmentModifier>, AssociationKey<String>, String>();
		List<CDOMReference<EquipmentModifier>> mods =
				new ArrayList<CDOMReference<EquipmentModifier>>();

		while (dotTok.hasMoreTokens())
		{
			String aEqModName = dotTok.nextToken();

			if (aEqModName.equalsIgnoreCase(Constants.LST_NONE))
			{
				Logging.errorPrint("Embedded " + Constants.LST_NONE
					+ " is prohibited in " + getTokenName());
				return false;
			}
			if (aEqModName.charAt(0) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not start with | : " + value);
				return false;
			}
			if (aEqModName.charAt(aEqModName.length() - 1) == '|')
			{
				Logging.errorPrint(getTokenName()
					+ " arguments may not end with | : " + value);
				return false;
			}
			if (aEqModName.indexOf("||") != -1)
			{
				Logging.errorPrint(getTokenName()
					+ " arguments uses double separator || : " + value);
				return false;
			}
			StringTokenizer pipeTok = new StringTokenizer(aEqModName, "|");

			// The type of EqMod, eg: ABILITYPLUS
			final String eqModKey = pipeTok.nextToken();

			/*
			 * TODO Need to handle these special cases???
			 */
			// if (eqModKey.equals(EQMOD_WEIGHT)) {
			// if (pipeTok.hasMoreTokens()) {
			// setWeightMod(pipeTok.nextToken().replace(',', '.'));
			// }
			// return;
			// }
			//
			// if (eqModKey.equals(EQMOD_DAMAGE)) {
			// if (pipeTok.hasMoreTokens()) {
			// setDamageMod(pipeTok.nextToken());
			// }
			// return;
			// }
			CDOMReference<EquipmentModifier> eqMod =
					context.ref.getCDOMReference(EQUIPMENT_MODIFIER_CLASS,
						eqModKey);
			mods.add(eqMod);

			while (pipeTok.hasMoreTokens())
			{
				String assocTok = pipeTok.nextToken();
				if (assocTok.indexOf(']') == -1)
				{
					/*
					 * TODO Can this be done in some way to learn from the EqMod
					 * what the association actually is??
					 */
					dkm.put(eqMod, AssociationKey.ONLY, assocTok);
				}
				else
				{
					if (assocTok.indexOf("[]") != -1)
					{
						Logging.errorPrint("Found empty assocation in "
							+ getTokenName() + ": " + value);
						return false;
					}
					StringTokenizer bracketTok =
							new StringTokenizer(assocTok, "]");
					while (bracketTok.hasMoreTokens())
					{
						String assoc = bracketTok.nextToken();
						int openBracketLoc = assoc.indexOf('[');
						if (openBracketLoc == -1)
						{
							Logging
								.errorPrint("Found close bracket without open bracket in assocation in "
									+ getTokenName() + ": " + value);
							return false;
						}
						if (openBracketLoc != assoc.lastIndexOf('['))
						{
							Logging
								.errorPrint("Found open bracket without close bracket in assocation in "
									+ getTokenName() + ": " + value);
							return false;
						}
						String assocKey = assoc.substring(0, openBracketLoc);
						String assocVal = assoc.substring(openBracketLoc + 1);
						dkm.put(eqMod, AssociationKey.getKeyFor(String.class,
							assocKey), assocVal);
					}
				}
			}
		}
		EquipmentHead altHead = getEquipmentHead(context, eq, 2);
		for (CDOMReference<EquipmentModifier> eqMod : mods)
		{
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), altHead,
						eqMod);
			for (AssociationKey<String> ak : dkm.getSecondaryKeySet(eqMod))
			{
				edge.setAssociation(ak, dkm.get(eqMod, ak));
			}
		}

		return true;
	}

	protected EquipmentHead getEquipmentHead(LoadContext context, Equipment eq,
		int index)
	{
		EquipmentHead head = getEquipmentHeadReference(context, eq, index);
		if (head == null)
		{
			// Isn't there already, so create new
			head = new EquipmentHead(this, index);
			context.graph.linkObjectIntoGraph(Constants.VT_EQ_HEAD, eq, head);
		}
		return head;
	}

	private EquipmentHead getEquipmentHeadReference(LoadContext context,
		Equipment eq, int index)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(Constants.VT_EQ_HEAD, eq,
					EquipmentHead.class);
		for (PCGraphEdge edge : edges)
		{
			EquipmentHead head =
					(EquipmentHead) edge.getSinkNodes().iterator().next();
			if (head.getHeadIndex() == index)
			{
				return head;
			}
		}
		return null;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EquipmentHead head = getEquipmentHeadReference(context, eq, 2);
		if (head == null)
		{
			return null;
		}
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), head,
					EquipmentModifier.class);
		if (edgeList == null || edgeList.isEmpty())
		{
			return null;
		}
		SortedMap<CDOMReference<EquipmentModifier>, PCGraphEdge> set =
				new TreeMap<CDOMReference<EquipmentModifier>, PCGraphEdge>(
					TokenUtilities.REFERENCE_SORTER);
		StringBuilder sb = new StringBuilder();
		boolean needDot = false;
		for (PCGraphEdge edge : edgeList)
		{
			CDOMReference<EquipmentModifier> eqMod =
					(CDOMReference<EquipmentModifier>) edge.getSinkNodes().get(
						0);
			set.put(eqMod, edge);
		}
		for (Entry<CDOMReference<EquipmentModifier>, PCGraphEdge> me : set
			.entrySet())
		{
			if (needDot)
			{
				sb.append('.');
			}
			needDot = true;
			sb.append(me.getKey().getLSTformat());
			PCGraphEdge edge = me.getValue();
			if (edge.hasAssociations())
			{
				/*
				 * TODO FIXME These need to be sorted... :(
				 */
				sb.append(Constants.PIPE);
				Collection<AssociationKey<?>> akColl =
						edge.getAssociationKeys();
				if (akColl.size() == 1)
				{
					AssociationKey<?> ak = akColl.iterator().next();
					if (AssociationKey.ONLY.equals(ak))
					{
						sb.append((String) edge.getAssociation(ak));
					}
					else
					{
						String st = (String) edge.getAssociation(ak);
						sb.append(ak).append('[').append(st).append(']');
					}
				}
				else
				{
					TreeMap<String, String> map = new TreeMap<String, String>();
					for (AssociationKey<?> ak : edge.getAssociationKeys())
					{
						if (AssociationKey.ONLY.equals(ak))
						{
							context.addWriteMessage("Edge Association ONLY is "
								+ "not valid if more than one association "
								+ "is required");
							return null;
						}
						map
							.put(ak.toString(), (String) edge
								.getAssociation(ak));
					}
					for (Entry<String, String> ae : map.entrySet())
					{
						sb.append(ae.getKey()).append('[')
							.append(ae.getValue()).append(']');
					}

				}
			}
		}
		return new String[]{sb.toString()};
	}
}
