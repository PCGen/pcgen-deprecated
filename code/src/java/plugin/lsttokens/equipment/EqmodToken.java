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
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.GraphChanges;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with EQMOD token
 */
public class EqmodToken extends AbstractToken implements EquipmentLstToken
{
	private static final Class<EquipmentModifier> EQUIPMENT_MODIFIER_CLASS =
			EquipmentModifier.class;

	@Override
	public String getTokenName()
	{
		return "EQMOD";
	}

	public boolean parse(Equipment eq, String value)
	{
		eq.addEqModifiers(value, true);
		return true;
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (Constants.LST_NONE.equals(value))
		{
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
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
			if (hasIllegalSeparator('|', aEqModName))
			{
				return false;
			}

			StringTokenizer pipeTok =
					new StringTokenizer(aEqModName, Constants.PIPE);

			// The type of EqMod, eg: ABILITYPLUS
			final String eqModKey = pipeTok.nextToken();
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
					 * Unfortunately, can't learn from the EquipmentModifier
					 * what the assocaition is because of order of processing
					 * (no guarantee an EqMod has been imported yet)
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
		EquipmentHead primHead = getEquipmentHead(context, eq, 1);
		for (CDOMReference<EquipmentModifier> eqMod : mods)
		{
			PCGraphGrantsEdge edge =
					context.graph.grant(getTokenName(), primHead, eqMod);
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
			context.graph.grant(Constants.VT_EQ_HEAD, eq, head);
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
		EquipmentHead head = getEquipmentHeadReference(context, eq, 1);
		if (head == null)
		{
			return null;
		}

		GraphChanges<EquipmentModifier> changes =
				context.graph.getChangesFromToken(getTokenName(), head,
					EQUIPMENT_MODIFIER_CLASS);
		if (changes == null)
		{
			return null;
		}
		Collection<LSTWriteable> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean needDot = false;
		for (LSTWriteable mod : added)
		{
			AssociatedPrereqObject assoc = changes.getAddedAssociation(mod);
			if (needDot)
			{
				sb.append('.');
			}
			needDot = true;
			sb.append(mod.getLSTformat());
			if (assoc.hasAssociations())
			{
				Collection<AssociationKey<?>> akColl =
						assoc.getAssociationKeys();
				akColl.remove(AssociationKey.SOURCE_URI);
				if (!akColl.isEmpty())
				{
					sb.append(Constants.PIPE);
				}
				if (akColl.size() == 1)
				{
					AssociationKey<?> ak = akColl.iterator().next();
					if (AssociationKey.ONLY.equals(ak))
					{
						sb.append((String) assoc.getAssociation(ak));
					}
					else
					{
						String st = (String) assoc.getAssociation(ak);
						sb.append(ak).append('[').append(st).append(']');
					}
				}
				else if (akColl.size() != 0)
				{
					TreeMap<String, String> map = new TreeMap<String, String>();
					for (AssociationKey<?> ak : akColl)
					{
						if (AssociationKey.ONLY.equals(ak))
						{
							context.addWriteMessage("Edge Association ONLY is "
								+ "not valid if more than one association "
								+ "is required");
							return null;
						}
						map.put(ak.toString(), (String) assoc
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
