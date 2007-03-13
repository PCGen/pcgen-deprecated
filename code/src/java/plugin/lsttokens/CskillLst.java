/*
 * Copyright 2006-2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2005-2006 (C) Devon Jones
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
package plugin.lsttokens;

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.graph.PCGraphAllowsEdge;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.core.PObject;
import pcgen.core.Skill;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.TokenUtilities;

/**
 * @author djones4
 * 
 */
public class CskillLst implements GlobalLstToken
{

	private static final Class<Skill> SKILL_CLASS = Skill.class;

	public String getTokenName()
	{
		return "CSKILL";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		final StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens())
		{
			obj.addCSkill(tok.nextToken());
		}
		return true;
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			if (Constants.LST_CLEAR.equals(tokText))
			{
				context.graph.unlinkChildNodesOfClass(getTokenName(), obj,
					SKILL_CLASS);
			}
			else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				PrereqObject skill =
						TokenUtilities.getObjectReference(context, SKILL_CLASS,
							tokText.substring(7));
				if (skill == null)
				{
					return false;
				}
				context.graph.unlinkChildNode(getTokenName(), obj, skill);
			}
			else
			{
				/*
				 * Note this HAS to be done one-by-one, because the
				 * .clearChildNodeOfClass method above does NOT recognize the
				 * C/CC Skill object and therefore doesn't know how to search
				 * the sublists
				 */
				PrereqObject skill =
						TokenUtilities.getObjectReference(context, SKILL_CLASS,
							tokText);
				if (skill == null)
				{
					return false;
				}
				PCGraphAllowsEdge edge =
						context.graph.linkAllowIntoGraph(getTokenName(), obj,
							skill);
				edge.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Set<PCGraphEdge> edgeList =
				context.graph.getChildLinksFromToken(getTokenName(), obj,
					SKILL_CLASS);
		StringBuilder sb = new StringBuilder();
		boolean needsPipe = false;
		for (PCGraphEdge edge : edgeList)
		{
			if (!SkillCost.CLASS.equals(edge
				.getAssociation(AssociationKey.SKILL_COST)))
			{
				context.addWriteMessage("Skill Cost must be CLASS for Token "
					+ getTokenName());
				return null;
			}
			if (needsPipe)
			{
				sb.append(Constants.PIPE);
			}
			/*
			 * TODO FIXME This breaks for types... :(
			 */
			Skill sk = (Skill) edge.getSinkNodes().get(0);
			sb.append(sk.getKeyName());
			needsPipe = true;
		}
		return new String[]{sb.toString()};
	}
}
