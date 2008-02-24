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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.content.AutomaticActionContainer;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.ChooseActor;
import pcgen.cdom.helper.CompoundOrChoiceSet;
import pcgen.cdom.helper.GrantActor;
import pcgen.cdom.helper.PrimitiveChoiceSet;
import pcgen.cdom.inst.CDOMArmorProf;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;
import pcgen.util.MapToList;

public class ArmorProfToken extends AbstractToken implements AutoLstToken, CDOMSecondaryToken<CDOMObject>
{

	private static final Class<CDOMArmorProf> ARMORPROF_CLASS = CDOMArmorProf.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "ARMORPROF";
	}

	public boolean parse(PObject target, String value, int level)
	{
		if (level > 1)
		{
			Logging.errorPrint("AUTO:" + getTokenName()
					+ " is not supported on class level lines");
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			if (st.nextToken().startsWith("TYPE"))
			{
				Logging.deprecationPrint("TYPE= in AUTO:ARMORPROF is "
						+ "deprecated.  Use ARMORTYPE=");
				break;
			}
		}
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
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
						+ getTokenName() + " " + value + " in "
						+ getTokenName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.errorPrint("Error generating Prerequisite " + prereq
						+ " in " + getTokenName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', armorProfs))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(armorProfs, Constants.PIPE);
		List<PrimitiveChoiceSet<CDOMArmorProf>> pcsList = new ArrayList<PrimitiveChoiceSet<CDOMArmorProf>>();
		List<PrereqObject> applyList = new ArrayList<PrereqObject>();

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(value))
			{
				ChooseActionContainer container = obj.getChooseContainer();
				GrantActor<CDOMArmorProf> actor = new GrantActor<CDOMArmorProf>();
				container.addActor(actor);
				actor.setAssociation(AssociationKey.TOKEN, getTokenName());
				applyList.add(actor);
			}
			else
			{
				if (Constants.LST_ALL.equalsIgnoreCase(aProf))
				{
					foundAny = true;
					CDOMReference<CDOMArmorProf> ref = context.ref
							.getCDOMAllReference(ARMORPROF_CLASS);
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getTokenName(), obj, ref);
					applyList.add(edge);
				}
				else if (aProf.startsWith("ARMORTYPE="))
				{
					foundOther = true;
					PrimitiveChoiceSet<CDOMArmorProf> pcs = context
							.getChoiceSet(ARMORPROF_CLASS, "EQUIPMENT["
									+ aProf.substring(5) + ']');
					if (pcs == null)
					{
						return false;
					}
					pcsList.add(pcs);
				}
				else if (aProf.startsWith("TYPE=") || aProf.startsWith("TYPE."))
				{
					Logging.addParseMessage(Logging.LST_ERROR, aProf
							+ " is prohibited in AUTO:" + getTokenName()
							+ ". Do you mean ARMORTYPE=?");
					return false;
				}
				else
				{
					foundOther = true;
					CDOMReference<CDOMArmorProf> ref = context.ref
							.getCDOMReference(ARMORPROF_CLASS, aProf);
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getTokenName(), obj, ref);
					applyList.add(edge);
				}
			}
		}

		if (foundAny && foundOther)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Non-sensical "
					+ getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		if (!pcsList.isEmpty())
		{
			PrimitiveChoiceSet<CDOMArmorProf> pcs;
			if (pcsList.size() == 1)
			{
				pcs = pcsList.get(0);
			}
			else
			{
				pcs = new CompoundOrChoiceSet<CDOMArmorProf>(pcsList);
			}

			ChoiceSet<CDOMArmorProf> cs = new ChoiceSet<CDOMArmorProf>(
					"AUTO:ARMORPROF", pcs);
			AutomaticActionContainer aac = new AutomaticActionContainer(
					"AUTO:ARMORPROF");
			aac.setChoiceSet(cs);
			aac.addActor(new GrantActor<CDOMArmorProf>());
			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getTokenName(), obj, aac);
			applyList.add(edge);

		}

		if (prereq != null)
		{
			for (PrereqObject pro : applyList)
			{
				pro.addPrerequisite(prereq);
			}
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		PrerequisiteWriter prereqWriter = new PrerequisiteWriter();

		ChooseActionContainer container = obj.getChooseContainer();
		Collection<ChooseActor> actors = container.getActors();
		for (ChooseActor actor : actors)
		{
			if (actor instanceof GrantActor)
			{
				GrantActor<?> ga = GrantActor.class.cast(actor);
				if (!getTokenName().equals(
						ga.getAssociation(AssociationKey.TOKEN)))
				{
					continue;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("%LIST");
				List<Prerequisite> prereqList = ga.getPrerequisiteList();
				if (prereqList != null && !prereqList.isEmpty())
				{
					String prereqs = getPrerequisiteString(context, prereqList);
					sb.append('[').append(prereqs).append(']');
				}
				list.add(sb.toString());
			}
		}

		AssociatedChanges<CDOMArmorProf> changes = context.getGraphContext()
				.getChangesFromToken(getTokenName(), obj, ARMORPROF_CLASS);
		AssociatedChanges<AutomaticActionContainer> typechanges = context
				.getGraphContext().getChangesFromToken(getTokenName(), obj,
						AutomaticActionContainer.class);
		if (list.isEmpty() && changes == null && typechanges == null)
		{
			return null;
		}
		HashMapToList<Set<Prerequisite>, String> m = new HashMapToList<Set<Prerequisite>, String>();
		if (changes != null)
		{
			MapToList<LSTWriteable, AssociatedPrereqObject> mtl = changes
					.getAddedAssociations();
			if (mtl != null && !mtl.isEmpty())
			{
				// Zero indicates no Content, others processed here
				for (LSTWriteable ab : mtl.getKeySet())
				{
					List<AssociatedPrereqObject> assocList = mtl.getListFor(ab);
					if (assocList.size() != 1)
					{
						context
								.addWriteMessage("Only one Association to AUTO can be made per object");
						return null;
					}
					AssociatedPrereqObject assoc = assocList.get(0);
					m.addToListFor(new HashSet<Prerequisite>(assoc
							.getPrerequisiteList()), ab.getLSTformat());
				}
			}
		}
		if (typechanges != null)
		{
			MapToList<LSTWriteable, AssociatedPrereqObject> mtl = typechanges
					.getAddedAssociations();
			if (mtl != null && !mtl.isEmpty())
			{
				// Zero indicates no Content, others processed here
				for (LSTWriteable ab : mtl.getKeySet())
				{
					List<AssociatedPrereqObject> assocList = mtl.getListFor(ab);
					if (assocList.size() != 1)
					{
						context
								.addWriteMessage("Only one Association to AUTO can be made per object");
						return null;
					}
					AssociatedPrereqObject assoc = assocList.get(0);
					String lstFormat = ab.getLSTformat();
					if (lstFormat.startsWith("EQUIPMENT[")
							&& lstFormat.endsWith("]"))
					{
						String temp = lstFormat.substring(10, lstFormat
								.length() - 1);
						m.addToListFor(new HashSet<Prerequisite>(assoc
								.getPrerequisiteList()), "ARMOR"
								+ StringUtil.replaceAll(temp, "]|EQUIPMENT[",
										"|ARMOR"));
					}
					else
					{
						context.addWriteMessage("Unexpected CHOICE in AUTO: "
								+ lstFormat);
						return null;
					}
				}
			}
		}
		for (Set<Prerequisite> prereqs : m.getKeySet())
		{
			String ab = StringUtil.join(m.getListFor(prereqs), Constants.PIPE);
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
				ab = ab + '[' + swriter.toString() + ']';
			}
			list.add(ab);
		}

		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
