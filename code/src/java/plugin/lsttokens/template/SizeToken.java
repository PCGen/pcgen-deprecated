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
package plugin.lsttokens.template;

import java.util.Set;

import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.mode.Size;
import pcgen.core.PCTemplate;
import pcgen.persistence.LoadContext;
import pcgen.persistence.lst.PCTemplateLstToken;
import pcgen.util.Logging;

/**
 * Class deals with SIZE Token
 */
public class SizeToken implements PCTemplateLstToken
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(PCTemplate template, String value)
	{
		template.setTemplateSize(value);
		return true;
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			/*
			 * FIXME This doesn't work, because SIZE can be a formula as well:
			 * e.g. max(AnimalSize,var("RACESIZE"))
			 */
			Size size = Size.valueOf(value);
			context.graph.linkObjectIntoGraph(getTokenName(), template, size);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint("Invalid Size in " + getTokenName() + ": "
				+ value);
			return false;
		}
	}

	public String unparse(LoadContext context, PCTemplate pct)
	{
		Set<PCGraphEdge> links =
				context.graph.getChildLinksFromToken(getTokenName(), pct,
					Size.class);
		if (links == null || links.isEmpty())
		{
			return null;
		}
		if (links.size() > 1)
		{
			context.addWriteMessage("Only 1 Size is allowed per Template");
			return null;
		}
		Size s = (Size) links.iterator().next().getSinkNodes().get(0);
		return new StringBuilder().append(getTokenName()).append(':').append(s)
			.toString();
	}
}
