/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence;

import pcgen.cdom.base.CDOMGroupRef;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;

public class LoadContext
{

	public final GraphContext graph;
	public final ListContext list;

	public final ReferenceContext ref;
	
	public final ObjectContext obj;

	public final GameMode gameMode;

	public LoadContext(PCGenGraph pgg)
	{
		graph = new GraphContext(pgg);
		obj = new ObjectContext();
		list = new ListContext();
		ref = new ReferenceContext();
		// TODO FIXME This is a hack
		gameMode = SettingsHandler.getGame();
	}

	public <T extends PrereqObject> CDOMGroupRef<T> groupChildNodesOfClass(
		PrereqObject parent, Class<T> child)
	{
		/*
		 * Create a new Group in the graph and then (defer to end of build)
		 * create edges between the new Group and all of the children of the
		 * given parent.
		 */
		// TODO FIXME
		return null;
	}

	public GameMode getGameMode()
	{
		return gameMode;
	}

	private int writeMessageCount = 0;

	public void addWriteMessage(String string)
	{
		// TODO FIXME Silently consume for now - these are message generated
		// during LST write...
		writeMessageCount++;
	}

	public int getWriteMessageCount()
	{
		return writeMessageCount;
	}

	public ContextQueue getContextQueue()
	{
		return new ContextQueue(graph);
	}

}
