/*
 * InfoViewModelBuilder.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on January 24, 2008, 5:15 PM
 */
package pcgen.gui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.swing.tree.TreePath;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.gui.filter.Filter;

/**
 *
 * @author Connor Petty
 */
public final class TreeViewFactory
{

    private TreeViewFactory()
    {
    }

    public static List<TreeViewPath<Deity>> getAlignmentPaths(Deity pobj)
    {
	String align = pobj.getAlignment();
	if (align != null && align.length() > 0)
	{
	    return Collections.singletonList(new TreeViewPath<Deity>(pobj, align));
	}

	return Collections.emptyList();
    }

    public static List<TreeViewPath<Deity>> getDomainPaths(Deity deity)
    {
	List<TreeViewPath<Deity>> paths = new ArrayList<TreeViewPath<Deity>>(2);
	for (QualifiedObject<Domain> qualDomain : deity.getDomainList())
	{
	    String domain = qualDomain.getObject(null).getKeyName();
	    if (domain != null && domain.length() > 0)
	    {
		paths.add(new TreeViewPath<Deity>(deity, domain));
	    }

	}
	return paths;
    }

    public static List<TreeViewPath<Deity>> getPantheonPaths(Deity deity)
    {
	List<TreeViewPath<Deity>> paths = new ArrayList<TreeViewPath<Deity>>(2);
	for (String pantheon : deity.getPantheonList())
	{
	    if (pantheon != null && pantheon.length() > 0)
	    {
		paths.add(new TreeViewPath<Deity>(deity, pantheon));
	    }

	}
	return paths;
    }

    public static List<TreeViewPath<Race>> getAllTypePaths(Race race)
    {
	List<TreeViewPath<Race>> paths = new ArrayList<TreeViewPath<Race>>(getRaceTypePaths(race));
	for (String type : race.getTypeList(true))
	{
	    paths.add(new TreeViewPath<Race>(race, type));
	}
	return paths;
    }

    public static List<TreeViewPath<Race>> getRaceTypePaths(Race race)
    {
	return Collections.singletonList(new TreeViewPath<Race>(race, race.getRaceType()));
    }

    public static List<TreeViewPath<Race>> getRaceTypeSubTypePaths(Race race)
    {
	String type = race.getRaceType();
	List<String> raceSubTypes = race.getRacialSubTypes();
	if (raceSubTypes.isEmpty())
	{
	    return Collections.singletonList(new TreeViewPath<Race>(race, type));
	}
	else
	{
	    List<TreeViewPath<Race>> paths = new ArrayList<TreeViewPath<Race>>(raceSubTypes.size());
	    for (String subtype : raceSubTypes)
	    {
		paths.add(new TreeViewPath<Race>(race, type, subtype));
	    }
	    return paths;
	}

    }

    public static List<TreeViewPath<Skill>> getKeyStatSubTypePaths(Skill skill)
    {
	String keystat = skill.getMyType(0);
	if (!Globals.isSkillTypeHidden(keystat))
	{
	    List<TreeViewPath<Skill>> paths = new ArrayList<TreeViewPath<Skill>>();
	    for (String subtype : skill.getSubtypes())
	    {
		if (!Globals.isSkillTypeHidden(subtype))
		{
		    paths.add(new TreeViewPath<Skill>(skill, keystat, subtype));
		}

	    }
	    return paths;
	}

	return Collections.emptyList();
    }

    public static List<TreeViewPath<Skill>> getKeyStatPaths(Skill skill)
    {
	String keystat = skill.getMyType(0);
	if (!Globals.isSkillTypeHidden(keystat))
	{
	    return Collections.singletonList(new TreeViewPath<Skill>(skill, keystat));
	}

	return Collections.emptyList();
    }

    public static List<TreeViewPath<Skill>> getSubTypePaths(Skill skill)
    {
	List<TreeViewPath<Skill>> paths = new ArrayList<TreeViewPath<Skill>>();
	for (String subtype : skill.getSubtypes())
	{
	    if (!Globals.isSkillTypeHidden(subtype))
	    {
		paths.add(new TreeViewPath<Skill>(skill, subtype));
	    }

	}
	return paths;
    }

    public static <T extends PObject> List<TreeViewPath<T>> getNamePaths(T obj)
    {
	return Collections.singletonList(new TreeViewPath<T>(obj));
    }

    public static <T extends PObject> List<TreeViewPath<T>> getTypePaths(T obj)
    {
	return Collections.singletonList(new TreeViewPath<T>(obj, obj.getType()));
    }

    public static <T extends PObject> List<TreeViewPath<T>> getSourcePaths(T obj)
    {
	String source = obj.getSourceEntry().getSourceBook().getLongName();
	if (source != null && source.length() > 0)
	{
	    return Collections.singletonList(new TreeViewPath<T>(obj, source));
	}

	return Collections.emptyList();
    }

    public static <T extends PObject> PObjectNode buildView(
	    TreeView<T> view, Filter filter, PlayerCharacter pc,
	    Collection<T> objs, TreeViewPathComparator<T> comparator)
    {
	Queue<TreeViewPath<T>> pathqueue = new PriorityQueue<TreeViewPath<T>>(objs.size(), comparator);
	for (T obj : objs)
	{
	    if (filter.accept(pc, obj))
	    {
		for (TreeViewPath<T> path : view.getPaths(obj))
		{
		    pathqueue.offer(path);
		}
	    }
	}
	PObjectNode root = new PObjectNode();
	Map<TreePath, PObjectNode> nodeMap = new HashMap<TreePath, PObjectNode>(pathqueue.size());
	while (!pathqueue.isEmpty())
	{
	    PObjectNode last = null;
	    for (TreePath path = pathqueue.poll(); path != null; path = path.getParentPath())
	    {
		PObjectNode node = nodeMap.get(path);
		if (node == null)
		{
		    node = new PObjectNode(path.getLastPathComponent());
		    nodeMap.put(path, node);
		}
		if (last != null)
		{
		    node.addChild(last);
		}
		last = node;
	    }
	    root.addChild(last);
	}
	return root;
    }

}
