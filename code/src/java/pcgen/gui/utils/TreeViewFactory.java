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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pcgen.core.Deity;
import pcgen.core.Domain;
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

	public String getDisplayedName()
	{
	    return name;
	}

	public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<Race> objs)
	{
	    switch (this)
	    {
		case NAME:
		    return buildNameView(filter, pc, objs);
		case RACETYPE_NAME:
		    return buildRaceTypeView(filter, pc, objs);
		case RACETYPE_SUBTYPE_NAME:
		    return buildRaceTypeSubTypeView(filter, pc, objs);
		case TYPE_NAME:
		    return buildTypeView(filter, pc, objs);
		case ALL_TYPES:
		    return buildAllRaceTypesView(filter, pc, objs);
		case SOURCE_NAME:
		    return buildSourceView(filter, pc, objs);
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

	public String getDisplayedName()
	{
	    return name;
	}

	public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<PCTemplate> objs)
	{
	    switch (this)
	    {
		case NAME:
		    return buildNameView(filter, pc, objs);
		case TYPE_NAME:
		    return buildTypeView(filter, pc, objs);
		case SOURCE_NAME:
		    return buildSourceView(filter, pc, objs);
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
	public String getDisplayedName()
	{
	    return name;
	}

	public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<PCClass> objs)
	{
	    switch (this)
	    {
		case NAME:
		    return buildNameView(filter, pc, objs);
		case TYPE_NAME:
		    return buildTypeView(filter, pc, objs);
		case SOURCE_NAME:
		    return buildSourceView(filter, pc, objs);
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
	public String getDisplayedName()
	{
	    return name;
	}

	public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<Skill> objs)
	{
	    switch (this)
	    {
		case NAME:
		    return buildNameView(filter, pc, objs);
		case TYPE_NAME:
		    return buildTypeView(filter, pc, objs);
		// TODO implement the rest
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

    public static enum InventoryView
    {

	TYPE_SUBTYPE_NAME,
	TYPE_NAME,
	NAME,
	ALL_TYPES,
	SOURCE_NAME
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

	public String getDisplayedName()
	{
	    return name;
	}

	public PObjectNode buildTree(Filter filter, PlayerCharacter pc, Collection<Deity> objs)
	{
	    switch(this)
	    {
		case NAME:
		    return buildNameView(filter, pc, objs);
		case ALIGNMENT_NAME:
		    return buildAlignmentView(filter, pc, objs);
		case DOMAIN_NAME:
		    return buildDomainView(filter, pc, objs);
		case PANTHEON_NAME:
		    return buildPantheonView(filter, pc, objs);
		case SOURCE_NAME:
		    return buildSourceView(filter, pc, objs);
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

    public static PObjectNode buildAlignmentView(Filter filter, PlayerCharacter pc, Collection<Deity> pobjects)
    {
	return buildView(filter, pc, pobjects, new AlignmentViewBuilder());
    }

    public static PObjectNode buildDomainView(Filter filter, PlayerCharacter pc, Collection<Deity> pobjects)
    {
	return buildView(filter, pc, pobjects, new DomainViewBuilder());
    }

    public static PObjectNode buildPantheonView(Filter filter, PlayerCharacter pc, Collection<Deity> pobjects)
    {
	return buildView(filter, pc, pobjects, new PantheonViewBuilder());
    }

    public static PObjectNode buildNameView(Filter filter, PlayerCharacter pc, Collection<? extends PObject> pobjects)
    {
	return buildView(filter, pc, pobjects, new NameViewBuilder());
    }

    public static PObjectNode buildAllRaceTypesView(Filter filter, PlayerCharacter pc)
    {
	return buildAllRaceTypesView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildAllRaceTypesView(Filter filter, PlayerCharacter pc, Collection<Race> races)
    {
	return buildView(filter, pc, races, new AllRaceTypesViewBuilder());
    }

    public static PObjectNode buildRaceTypeView(Filter filter, PlayerCharacter pc)
    {
	return buildRaceTypeView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildRaceTypeView(Filter filter, PlayerCharacter pc, Collection<Race> races)
    {
	return buildView(filter, pc, races, new RaceTypeViewBuilder());
    }

    public static PObjectNode buildRaceTypeSubTypeView(Filter filter, PlayerCharacter pc)
    {
	return buildRaceTypeSubTypeView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildRaceTypeSubTypeView(Filter filter, PlayerCharacter pc, Collection<Race> races)
    {
	return buildView(filter, pc, races, new RaceTypeSubTypeViewBuilder());
    }

    public static PObjectNode buildTypeView(Filter filter, PlayerCharacter pc, Collection<? extends PObject> pobjects)
    {
	return buildView(filter, pc, pobjects, new TypeViewBuilder());
    }

    public static PObjectNode buildSourceView(Filter filter, PlayerCharacter pc, Collection<? extends PObject> pobjects)
    {
	return buildView(filter, pc, pobjects, new SourceViewBuilder());
    }

    private static <T extends PObject> PObjectNode buildView(Filter filter, PlayerCharacter pc, Collection<? extends T> pobjects, TreeViewBuilder<T> builder)
    {
	PObjectNode root = new PObjectNode();
	for (final T pobj : pobjects)
	{
	    if (filter.accept(pc, pobj))
	    {
		builder.buildBranch(root, pobj);
	    }
	}
	return root;
    }

    private interface TreeViewBuilder<E extends PObject>
    {

	public void buildBranch(PObjectNode root, E element);

    }

    private static abstract class AbstractViewBuilder<E extends PObject> implements TreeViewBuilder<E>
    {

	protected final Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();

	public void buildBranch(PObjectNode root, E element)
	{
	    final String key = getKey(element);
	    if (accept(key))
	    {
		PObjectNode node = nodeMap.get(key);
		if (node == null)
		{
		    node = new PObjectNode(key);
		    nodeMap.put(key, node);
		    root.addChild(node);
		}
		node.addChild(new PObjectNode(element));
	    }
	}

	public abstract String getKey(E element);

	public boolean accept(String key)
	{
	    return true;
	}

    }

    private static class NameViewBuilder implements TreeViewBuilder
    {

	public void buildBranch(PObjectNode root, PObject element)
	{
	    root.addChild(new PObjectNode(element));
	}

    }

    private static class SourceViewBuilder extends AbstractViewBuilder<PObject>
    {

	public String getKey(PObject element)
	{
	    return element.getSourceEntry().getSourceBook().getLongName();
	}

	@Override
	public boolean accept(String key)
	{
	    return key != null && key.length() > 0;
	}

    }

    private static class TypeViewBuilder extends AbstractViewBuilder<PObject>
    {

	@Override
	public String getKey(PObject element)
	{
	    return element.getType();
	}

    }

    private static class AlignmentViewBuilder extends AbstractViewBuilder<Deity>
    {

	@Override
	public String getKey(Deity deity)
	{
	    return deity.getAlignment();
	}

	@Override
	public boolean accept(String align)
	{
	    return align != null && align.length() > 0;
	}

    }

    private static class RaceTypeViewBuilder extends AbstractViewBuilder<Race>
    {

	@Override
	public String getKey(Race element)
	{
	    return element.getRaceType();
	}

    }

    private static class AllRaceTypesViewBuilder extends AbstractViewBuilder<Race>
    {

	@Override
	public void buildBranch(PObjectNode root, Race race)
	{
	    super.buildBranch(root, race);
	    for (String type : race.getTypeList(true))
	    {
		PObjectNode typeNode = nodeMap.get(type);
		if (typeNode == null)
		{
		    typeNode = new PObjectNode(type);
		    nodeMap.put(type, typeNode);
		    root.addChild(typeNode);
		}
		typeNode.addChild(new PObjectNode(race));
	    }
	}

	@Override
	public String getKey(Race race)
	{
	    return race.getRaceType();
	}

    }

    private static class RaceTypeSubTypeViewBuilder implements TreeViewBuilder<Race>
    {

	private final Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();

	public void buildBranch(PObjectNode root, Race race)
	{
	    String type = race.getRaceType();
	    PObjectNode typeNode = nodeMap.get(type);
	    if (typeNode == null)
	    {
		typeNode = new PObjectNode(type);
		nodeMap.put(type, typeNode);
		root.addChild(typeNode);
	    }
	    List<String> raceSubTypes = race.getRacialSubTypes();
	    if (raceSubTypes.size() > 0)
	    {
		for (String subtype : raceSubTypes)
		{
		    String key = type + ":" + subtype;
		    PObjectNode subtypeNode = nodeMap.get(key);
		    if (subtypeNode == null)
		    {
			subtypeNode = new PObjectNode(subtype);
			nodeMap.put(key, subtypeNode);
			typeNode.addChild(subtypeNode);
		    }
		    subtypeNode.addChild(new PObjectNode(race));
		}
	    }
	    else
	    {
		typeNode.addChild(new PObjectNode(race));
	    }
	}

    }

    private static class DomainViewBuilder implements TreeViewBuilder<Deity>
    {

	private Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();

	public void buildBranch(PObjectNode root, Deity deity)
	{
	    if (!deity.getKeyName().equalsIgnoreCase("NONE"))
	    {
		for (QualifiedObject<Domain> qualDomain : deity.getDomainList())
		{
		    String domain = qualDomain.getObject(null).getKeyName();
		    if (domain != null && domain.length() > 0)
		    {
			PObjectNode node = nodeMap.get(domain);
			if (node == null)
			{
			    node = new PObjectNode(domain);
			    nodeMap.put(domain, node);
			    root.addChild(node);
			}
			node.addChild(new PObjectNode(deity));
		    }
		}
	    }
	}

    }

    private static class PantheonViewBuilder implements TreeViewBuilder<Deity>
    {

	private Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();

	public void buildBranch(PObjectNode root, Deity deity)
	{
	    for (String pantheon : deity.getPantheonList())
	    {
		if (pantheon != null && pantheon.length() > 0)
		{
		    PObjectNode node = nodeMap.get(pantheon);
		    if (node == null)
		    {
			node = new PObjectNode(pantheon);
			nodeMap.put(pantheon, node);
			root.addChild(node);
		    }
		    node.addChild(new PObjectNode(deity));
		}
	    }
	}

    }
}
