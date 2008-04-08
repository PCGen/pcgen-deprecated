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
import pcgen.base.util.MapToList;
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
import pcgen.cdom.inst.CDOMShieldProf;
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

public class ShieldProfToken extends AbstractToken implements AutoLstToken,
		CDOMSecondaryToken<CDOMObject>
{

	private static final Class<CDOMShieldProf> SHIELDPROF_CLASS = CDOMShieldProf.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "SHIELDPROF";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
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
				Logging.deprecationPrint("TYPE= in AUTO:SHIELDPROF is "
						+ "deprecated.  Use SHIELDTYPE=");
				break;
			}
		}
		target.addAutoArray(getTokenName(), value);
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		String shieldProfs;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			shieldProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			shieldProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.errorPrint("Unresolved Prerequisite in " + value
						+ " in " + getFullName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.errorPrint("Error generating Prerequisite " + prereq
						+ " in " + getFullName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', shieldProfs))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(shieldProfs, Constants.PIPE);
		List<PrimitiveChoiceSet<CDOMShieldProf>> pcsList = new ArrayList<PrimitiveChoiceSet<CDOMShieldProf>>();
		List<PrereqObject> applyList = new ArrayList<PrereqObject>();

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();
			if ("%LIST".equals(aProf))
			{
				ChooseActionContainer container = obj.getChooseContainer();
				GrantActor<CDOMShieldProf> actor = new GrantActor<CDOMShieldProf>();
				container.addActor(actor);
				actor.setAssociation(AssociationKey.TOKEN, getFullName());
				applyList.add(actor);
			}
			else
			{
				if (Constants.LST_ALL.equalsIgnoreCase(aProf))
				{
					foundAny = true;
					CDOMReference<CDOMShieldProf> ref = context.ref
							.getCDOMAllReference(SHIELDPROF_CLASS);
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getFullName(), obj, ref);
					applyList.add(edge);
				}
				else if (aProf.startsWith("SHIELDTYPE="))
				{
					foundOther = true;
					PrimitiveChoiceSet<CDOMShieldProf> pcs = context
							.getChoiceSet(SHIELDPROF_CLASS, "EQUIPMENT["
									+ aProf.substring(6) + ']');
					if (pcs == null)
					{
						Logging.errorPrint("BLAH!");
						return false;
					}
					pcsList.add(pcs);
				}
				else if (aProf.startsWith("TYPE=") || aProf.startsWith("TYPE."))
				{
					Logging.addParseMessage(Logging.LST_ERROR, aProf
							+ " is prohibited in " + getFullName()
							+ ". Do you mean SHIELDTYPE=?");
					return false;
				}
				else
				{
					foundOther = true;
					CDOMReference<CDOMShieldProf> ref = context.ref
							.getCDOMReference(SHIELDPROF_CLASS, aProf);
					AssociatedPrereqObject edge = context.getGraphContext()
							.grant(getFullName(), obj, ref);
					applyList.add(edge);
				}
			}
		}

		if (foundAny && foundOther)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Non-sensical "
					+ getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		if (!pcsList.isEmpty())
		{
			PrimitiveChoiceSet<CDOMShieldProf> pcs;
			if (pcsList.size() == 1)
			{
				pcs = pcsList.get(0);
			}
			else
			{
				pcs = new CompoundOrChoiceSet<CDOMShieldProf>(pcsList);
			}

			ChoiceSet<CDOMShieldProf> cs = new ChoiceSet<CDOMShieldProf>(
					"AUTO:SHIELDPROF", pcs);
			AutomaticActionContainer aac = new AutomaticActionContainer(
					"AUTO:SHIELDPROF");
			aac.setChoiceSet(cs);
			aac.addActor(new GrantActor<CDOMShieldProf>());
			AssociatedPrereqObject edge = context.getGraphContext().grant(
					getFullName(), obj, aac);
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
				if (!getFullName().equals(
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

		AssociatedChanges<CDOMShieldProf> changes = context.getGraphContext()
				.getChangesFromToken(getFullName(), obj, SHIELDPROF_CLASS);
		AssociatedChanges<AutomaticActionContainer> typechanges = context
				.getGraphContext().getChangesFromToken(getFullName(), obj,
						AutomaticActionContainer.class);
		HashMapToList<Set<Prerequisite>, String> m = new HashMapToList<Set<Prerequisite>, String>();
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
		mtl = typechanges.getAddedAssociations();
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
					String temp = lstFormat.substring(10,
							lstFormat.length() - 1);
					m.addToListFor(new HashSet<Prerequisite>(assoc
							.getPrerequisiteList()), "SHIELD"
							+ StringUtil.replaceAll(temp, "]|EQUIPMENT[",
									"|SHIELD"));
				}
				else
				{
					context.addWriteMessage("Unexpected CHOICE in AUTO: "
							+ lstFormat);
					return null;
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
							+ getFullName());
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
