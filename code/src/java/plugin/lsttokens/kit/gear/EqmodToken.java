/*
 * EqmodToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.gear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.cdom.kit.CDOMKitGear;
import pcgen.core.kit.KitGear;
import pcgen.persistence.lst.KitGearLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * EQMOD Token for KitGear
 */
public class EqmodToken extends AbstractToken implements KitGearLstToken,
		CDOMSecondaryToken<CDOMKitGear>
{
	private static final Class<CDOMEqMod> EQUIPMENT_MODIFIER_CLASS = CDOMEqMod.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "EQMOD";
	}

	/**
	 * parse
	 * 
	 * @param kitGear
	 *            KitGear
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitGear kitGear, String value)
	{
		kitGear.addEqMod(value);
		return true;
	}

	public Class<CDOMKitGear> getTokenClass()
	{
		return CDOMKitGear.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, CDOMKitGear kitGear, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer dotTok = new StringTokenizer(value, Constants.DOT);
		DoubleKeyMap<CDOMReference<CDOMEqMod>, AssociationKey<String>, String> dkm = new DoubleKeyMap<CDOMReference<CDOMEqMod>, AssociationKey<String>, String>();
		List<CDOMReference<CDOMEqMod>> mods = new ArrayList<CDOMReference<CDOMEqMod>>();

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

			StringTokenizer pipeTok = new StringTokenizer(aEqModName,
					Constants.PIPE);

			// The type of EqMod, eg: ABILITYPLUS
			final String eqModKey = pipeTok.nextToken();
			CDOMReference<CDOMEqMod> eqMod = context.ref.getCDOMReference(
					EQUIPMENT_MODIFIER_CLASS, eqModKey);
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
					StringTokenizer bracketTok = new StringTokenizer(assocTok,
							"]");
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
		for (CDOMReference<CDOMEqMod> eqMod : mods)
		{
			AssociatedPrereqObject edge = new SimpleAssociatedObject();
			for (AssociationKey<String> ak : dkm.getSecondaryKeySet(eqMod))
			{
				edge.setAssociation(ak, dkm.get(eqMod, ak));
			}
			kitGear.addEqMod(eqMod, edge);
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMKitGear kitGear)
	{
		if (!kitGear.hasEqMods())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (CDOMReference<CDOMEqMod> eqmod : kitGear.getEqMods())
		{
			AssociatedPrereqObject assoc = kitGear.getAssoc(eqmod);
			StringBuilder sb = new StringBuilder();
			sb.append(eqmod.getLSTformat());
			if (assoc.hasAssociations())
			{
				Collection<AssociationKey<?>> akColl = assoc
						.getAssociationKeys();
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
			set.add(sb.toString());
		}
		return new String[] { StringUtil.join(set, Constants.DOT) };
	}
}
