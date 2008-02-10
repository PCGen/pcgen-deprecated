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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import javax.swing.tree.TreePath;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.gui.filter.Filter;
import pcgen.util.PropertyFactory;

/**
 *
 * @author Connor Petty
 */
public final class TreeViewFactory
{

    private TreeViewFactory()
    {
    }

    public static enum RaceView implements TreeView<Race>
    {

	NAME("in_nameLabel"),
	RACETYPE_NAME("in_racetypeName"),
	RACETYPE_SUBTYPE_NAME("in_racetypeSubtypeName"),
	TYPE_NAME("in_typeName"),
	ALL_TYPES("in_allTypes"),
	SOURCE_NAME("in_sourceName");
	private String name;

	private RaceView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(Race pobj)
	{
	    switch (this)
	    {
		case NAME:
		    return getNamePaths(pobj);
		case RACETYPE_NAME:
		    return getRaceTypePaths(pobj);
		case RACETYPE_SUBTYPE_NAME:
		    return getRaceTypeSubTypePaths(pobj);
		case TYPE_NAME:
		    return getTypePaths(pobj);
		case ALL_TYPES:
		    return getAllTypePaths(pobj);
		case SOURCE_NAME:
		    return getSourcePaths(pobj);
		default:
		    throw new InternalError();
	    }
	}

    }

    public static enum TemplateView implements TreeView<PCTemplate>
    {

	NAME("in_nameLabel"),
	TYPE_NAME("in_typeName"),
	SOURCE_NAME("in_sourceName");
	private String name;

	private TemplateView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(PCTemplate pobj)
	{
	    switch (this)
	    {
		case NAME:
		    return getNamePaths(pobj);
		case TYPE_NAME:
		    return getTypePaths(pobj);
		case SOURCE_NAME:
		    return getSourcePaths(pobj);
		default:
		    throw new InternalError();
	    }
	}

    }

    public static enum ClassView implements TreeView<PCClass>
    {

	NAME("in_nameLabel"),
	TYPE_NAME("in_typeName"),
	SOURCE_NAME("in_sourceName");
	private String name;

	private ClassView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(PCClass pobj)
	{
	    switch (this)
	    {
		case NAME:
		    return getNamePaths(pobj);
		case TYPE_NAME:
		    return getTypePaths(pobj);
		case SOURCE_NAME:
		    return getSourcePaths(pobj);
		default:
		    throw new InternalError();
	    }
	}

    }

    public static enum SkillsView implements TreeView<Skill>
    {

	STAT_TYPE_NAME("in_iskKeyStat_SubType_Name"),
	STAT_NAME("in_iskKeyStat_Name"),
	TYPE_NAME("in_iskSubType_Name"),
	COST_TYPE_NAME("in_iskCost_SubType_Name"),
	COST_NAME("in_iskCost_Name"),
	NAME("in_iskName");
	private String name;

	private SkillsView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(Skill pobj)
	{
	    switch (this)
	    {
		case STAT_TYPE_NAME:
		    return getKeyStatSubTypePaths(pobj);
		case STAT_NAME:
		    return getKeyStatPaths(pobj);
		case TYPE_NAME:
		    return getSubTypePaths(pobj);
		case COST_TYPE_NAME:
		case COST_NAME:
		//pobj.
		case NAME:
		    return getNamePaths(pobj);
		default:
		    throw new InternalError();
	    }
	}

    }

    public static enum FeatsView
    {

	TYPE_NAME,
	NAMEONLY,
	PREREQTREE,
	SOURCE_NAME
    }

    public static enum InventoryView implements TreeView<Equipment>
    {

	TYPE_SUBTYPE_NAME("in_typeSubtypeName"),
	TYPE_NAME("in_typeName"),
	NAME("in_nameLabel"),
	ALL_TYPES("in_allTypes"),
	SOURCE_NAME("in_sourceName");
	private String name;

	private InventoryView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(Equipment pobj)
	{
	    throw new UnsupportedOperationException("Not supported yet.");
	}

    }

    public static enum EquipingView
    {

	NAME,
	LOCATION_NAME,
	EQUIPPED,
	TYPE_NAME
    }

    public static enum DomainView implements TreeView<Deity>
    {

	NAME("in_nameLabel"),
	ALIGNMENT_NAME("in_alignmentName"),
	DOMAIN_NAME("in_domainName"),
	PANTHEON_NAME("in_pantheonName"),
	SOURCE_NAME("in_sourceName");
	private String name;

	private DomainView(String key)
	{
	    this.name = PropertyFactory.getString(key);
	}

	public String getViewName()
	{
	    return name;
	}

	public List<TreePath> getTreePaths(Deity pobj)
	{
	    switch (this)
	    {
		case NAME:
		    return getNamePaths(pobj);
		case ALIGNMENT_NAME:
		    return getAlignmentPaths(pobj);
		case DOMAIN_NAME:
		    return getDomainPaths(pobj);
		case PANTHEON_NAME:
		    return getPantheonPaths(pobj);
		case SOURCE_NAME:
		    return getSourcePaths(pobj);
		default:
		    throw new InternalError();
	    }
	}

    }

    public static enum SpellsView
    {

	CLASS,
	LEVEL,
	DESCRIPTOR,
	RANGE,
	DURATION,
	TYPE,
	SCHOOL,
	NOTHING
    }

    private static List<TreePath> getAlignmentPaths(Deity pobj)
    {
	String align = pobj.getAlignment();
	if (align != null && align.length() > 0)
	{
	    return Collections.singletonList(new TreePath(new Object[]{align, pobj}));
	}

	return Collections.emptyList();
    }

    private static List<TreePath> getDomainPaths(Deity deity)
    {
	List<TreePath> paths = new ArrayList<TreePath>(2);
	for (QualifiedObject<Domain> qualDomain : deity.getDomainList())
	{
	    String domain = qualDomain.getObject(null).getKeyName();
	    if (domain != null && domain.length() > 0)
	    {
		paths.add(new TreePath(new Object[]{domain, deity}));
	    }

	}
	return paths;
    }

    private static List<TreePath> getPantheonPaths(Deity deity)
    {
	List<TreePath> paths = new ArrayList<TreePath>(2);
	for (String pantheon : deity.getPantheonList())
	{
	    if (pantheon != null && pantheon.length() > 0)
	    {
		paths.add(new TreePath(new Object[]{pantheon, deity}));
	    }

	}
	return paths;
    }

    private static List<TreePath> getAllTypePaths(Race race)
    {
	List<TreePath> paths = new ArrayList<TreePath>(getRaceTypePaths(race));
	for (String type : race.getTypeList(true))
	{
	    paths.add(new TreePath(new Object[]{type, race}));
	}

	return paths;
    }

    private static List<TreePath> getRaceTypePaths(Race race)
    {
	return Collections.singletonList(new TreePath(
					 new Object[]{race.getRaceType(), race}));
    }

    private static List<TreePath> getRaceTypeSubTypePaths(Race race)
    {
	String type = race.getRaceType();
	List<String> raceSubTypes = race.getRacialSubTypes();
	if (raceSubTypes.isEmpty())
	{
	    return Collections.singletonList(new TreePath(
					     new Object[]{type, race}));
	}
	else
	{
	    List<TreePath> paths = new ArrayList<TreePath>(raceSubTypes.size());
	    for (String subtype : raceSubTypes)
	    {
		paths.add(new TreePath(new Object[]{type, subtype, race}));
	    }

	    return paths;
	}

    }

    private static List<TreePath> getKeyStatSubTypePaths(Skill skill)
    {
	String keystat = skill.getMyType(0);
	if (!Globals.isSkillTypeHidden(keystat))
	{
	    List<TreePath> paths = new ArrayList<TreePath>();
	    for (String subtype : skill.getSubtypes())
	    {
		if (!Globals.isSkillTypeHidden(subtype))
		{
		    paths.add(new TreePath(new Object[]{keystat, subtype, skill}));
		}

	    }
	    return paths;
	}

	return Collections.emptyList();
    }

    private static List<TreePath> getKeyStatPaths(Skill skill)
    {
	String keystat = skill.getMyType(0);
	if (!Globals.isSkillTypeHidden(keystat))
	{
	    return Collections.singletonList(new TreePath(
					     new Object[]{keystat, skill}));
	}

	return Collections.emptyList();
    }

    private static List<TreePath> getSubTypePaths(Skill skill)
    {
	List<TreePath> paths = new ArrayList<TreePath>();
	for (String subtype : skill.getSubtypes())
	{
	    if (!Globals.isSkillTypeHidden(subtype))
	    {
		paths.add(new TreePath(new Object[]{subtype, skill}));
	    }

	}
	return paths;
    }

    private static List<TreePath> getNamePaths(PObject obj)
    {
	return Collections.singletonList(new TreePath(obj));
    }

    private static List<TreePath> getTypePaths(PObject obj)
    {
	return Collections.singletonList(new TreePath(
					 new Object[]{obj.getType(), obj}));
    }

    private static List<TreePath> getSourcePaths(PObject obj)
    {
	String source = obj.getSourceEntry().getSourceBook().getLongName();
	if (source != null && source.length() > 0)
	{
	    return Collections.singletonList(new TreePath(
					     new Object[]{source, obj}));
	}

	return Collections.emptyList();
    }

    public static <T extends PObject> PObjectNode buildView(
	    TreeView<T> view, Filter filter, PlayerCharacter pc, 
	    Collection<T> objs, Comparator<TreePath> comparator)
    {
	Queue<TreePath> pathqueue = new PriorityQueue<TreePath>(objs.size(), comparator);
	for (T obj : objs)
	{
	    if (filter.accept(pc, obj))
	    {
		for (TreePath path : view.getTreePaths(obj))
		{
		    pathqueue.add(path);
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
