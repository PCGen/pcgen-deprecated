/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.lsttokens.auto;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.EquipmentNature;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AbstractToken;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.persistence.lst.utils.TokenUtilities;
import pcgen.util.Logging;

public class EquipToken extends AbstractToken implements AutoLstToken
{

	private static final Integer INTEGER_ONE = Integer.valueOf(1);

	@Override
	public String getTokenName()
	{
		return "EQUIP";
	}

	public boolean parse(PObject target, String value)
	{
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, PObject obj, String value)
	{
		String armorProfs;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		/*
		 * CONSIDER There is the ability to consolidate this PREREQ processing
		 * into AutoLst.java (since it's the same across AUTO SubTokens)
		 */
		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			armorProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			armorProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in "
					+ getTokenName() + " " + value + " in " + getTokenName());
				return false;
			}
			prereq =
					getPrerequisite(value.substring(openBracketLoc + 1, value
						.length() - 1));
			if (prereq == null)
			{
				Logging.errorPrint("Error generating Prerequisite " + prereq
					+ " in " + getTokenName());
				return false;
			}
		}

		if (armorProfs.charAt(0) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not start with | : " + value);
			return false;
		}
		if (armorProfs.charAt(armorProfs.length() - 1) == '|')
		{
			Logging.errorPrint(getTokenName()
				+ " arguments may not end with | : " + value);
			return false;
		}
		if (armorProfs.indexOf("||") != -1)
		{
			Logging.errorPrint(getTokenName()
				+ " arguments uses double separator || : " + value);
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(armorProfs, Constants.PIPE);
		List<CDOMReference<Equipment>> refs =
				new ArrayList<CDOMReference<Equipment>>();

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(value))
			{
				/*
				 * FIXME Need to figure out how to handle this!!!
				 */
				// for (Iterator<AssociatedChoice<String>> e =
				// getAssociatedList()
				// .iterator(); e.hasNext();) {
				// aList.add(e.next().getDefaultChoice());
				// }
			}
			else
			{
				CDOMReference<Equipment> ref;
				if (Constants.LST_ANY.equalsIgnoreCase(aProf))
				{
					foundAny = true;
					ref = context.ref.getCDOMAllReference(Equipment.class);
				}
				else
				{
					foundOther = true;
					ref =
							TokenUtilities.getTypeOrPrimitive(context,
								Equipment.class, aProf);
				}
				if (ref == null)
				{
					return false;
				}
				refs.add(ref);
			}
		}

		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		TOKENS: for (CDOMReference<Equipment> ref : refs)
		{
			Set<PCGraphEdge> edges =
					context.graph.getChildLinksFromToken(getTokenName(), obj,
						Equipment.class);
			for (PCGraphEdge edge : edges)
			{
				CDOMReference<Equipment> ab =
						(CDOMReference<Equipment>) edge.getSinkNodes().get(0);
				if (ab.equals(ref))
				{
					List<Prerequisite> prl = edge.getPrerequisiteList();
					if (prereq == null && (prl == null || prl.isEmpty()))
					{
						Integer q =
								edge.getAssociation(AssociationKey.QUANTITY);
						edge.setAssociation(AssociationKey.QUANTITY, Integer
							.valueOf(q.intValue() + 1));
						continue TOKENS;
					}
					if (prereq != null)
					{
						if (prl == null || prl.isEmpty())
						{
							// Can't use
						}
						else if (prl.get(0).equals(prereq))
						{
							Integer q =
									edge
										.getAssociation(AssociationKey.QUANTITY);
							edge.setAssociation(AssociationKey.QUANTITY,
								Integer.valueOf(q.intValue() + 1));
							continue TOKENS;
						}
						else
						{
							// Can't use
						}
					}
				}
			}
			PCGraphGrantsEdge edge =
					context.graph.linkObjectIntoGraph(getTokenName(), obj, ref);
			if (prereq != null)
			{
				edge.addPreReq(prereq);
			}
			edge.setAssociation(AssociationKey.EQUIPMENT_NATURE,
				EquipmentNature.AUTOMATIC);
			edge.setAssociation(AssociationKey.QUANTITY, INTEGER_ONE);
			// TODO Need to account for these
			// newEq.setOutputIndex(aList.size());
		}

		return true;
	}

	public String[] unparse(LoadContext context, PObject obj)
	{
		Set<PCGraphEdge> edges =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					Equipment.class);
		if (edges == null || edges.isEmpty())
		{
			return null;
		}
		DoubleKeyMap<Set<Prerequisite>, CDOMReference<Equipment>, Integer> m =
				new DoubleKeyMap<Set<Prerequisite>, CDOMReference<Equipment>, Integer>();
		for (PCGraphEdge edge : edges)
		{
			CDOMReference<Equipment> ab =
					(CDOMReference<Equipment>) edge.getSinkNodes().get(0);
			m.put(new HashSet<Prerequisite>(edge.getPrerequisiteList()), ab,
				edge.getAssociation(AssociationKey.QUANTITY));
		}
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
		SortedSet<CDOMReference<Equipment>> set =
				new TreeSet<CDOMReference<Equipment>>(
					TokenUtilities.REFERENCE_SORTER);

		String[] array = new String[m.firstKeyCount()];
		int index = 0;
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			set.clear();
			set.addAll(m.getSecondaryKeySet(prereqs));
			StringBuilder sb = new StringBuilder();
			boolean needPipe = false;
			for (CDOMReference<Equipment> ref : set)
			{
				String lstFormat = ref.getLSTformat();
				for (int i = 0; i < m.get(prereqs, ref).intValue(); i++)
				{
					if (needPipe)
					{
						sb.append(Constants.PIPE);
					}
					needPipe = true;
					sb.append(lstFormat);
				}
			}
			if (prereqs != null && !prereqs.isEmpty())
			{
				if (prereqs.size() > 1)
				{
					context.addWriteMessage("Error: "
						+ obj.getClass().getSimpleName()
						+ " had more than one Prerequisite for "
						+ getTokenName());
					return null;
				}
				Prerequisite p = prereqs.iterator().next();
				StringWriter swriter = new StringWriter();
				try
				{
					prereqWriter.write(swriter, p);
				}
				catch (PersistenceLayerException e)
				{
					context.addWriteMessage("Error writing Prerequisite: " + e);
					return null;
				}
				sb.append('[').append(swriter.toString()).append(']');
			}
			array[index++] = sb.toString();
		}
		return array;
	}
}
