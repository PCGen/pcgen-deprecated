package pcgen.character;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.formula.Resolver;
import pcgen.base.graph.visitor.DirectedBreadthFirstTraverseAlgorithm;
import pcgen.base.graph.visitor.DirectedNodeWeightCalculation;
import pcgen.cdom.base.AssociatedObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.character.EquipmentSetFacade;
import pcgen.cdom.content.EquipmentSet;
import pcgen.cdom.content.SimpleMovement;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.Check;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphEdge;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.lists.PCGenLists;
import pcgen.core.Ability;
import pcgen.core.Alignment;
import pcgen.core.Deity;
import pcgen.core.Equipment;
import pcgen.core.PCCharacterLevel;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.rules.RulesDataStore;
import pcgen.util.enumeration.AttackType;

public class CharacterDataStore extends CDOMObject
{
	private static final Class<SpellResistance> SPELL_RESISTANCE_CLASS = SpellResistance.class;

	// TODO This should probably be a RunContext or some Facade of the
	// LoadContext
	// so that it isn't abused - thpr Jun 2, 2007

	private final RulesDataStore rules;

	public CharacterDataStore(RulesDataStore rulesData)
	{
		rules = rulesData;
	}
	
	public RulesDataStore getRulesData()
	{
		return rules;
	}

	private final PCGenGraph base = new PCGenGraph();
	/*
	 * TODO Need to convert this at some point to active, not base
	 */
	private final PCGenGraph activeGraph = base;

	public PCGenGraph getBaseGraph()
	{
		return base;
	}

	public int getLevel()
	{
		return activeGraph.getGrantedNodeList(PCCharacterLevel.class).size();
	}

	public int getLevel(PCClass cl)
	{
		List<PCClassLevel> levellist = activeGraph
				.getGrantedNodeList(PCClassLevel.class);
		int classLevel = 0;
		for (PCClassLevel pcl : levellist)
		{
			int level = cl.getCDOMLevel(pcl);
			classLevel = Math.max(classLevel, level);
		}
		return classLevel;
	}

	public PCGenGraph getActiveGraph()
	{
		return activeGraph;
	}

	public int getTotalCDOMPlayerLevels()
	{
		List<PCClass> classlist = activeGraph.getGrantedNodeList(PCClass.class);
		List<PCClassLevel> levellist = activeGraph
				.getGrantedNodeList(PCClassLevel.class);
		int totalLevels = 0;
		for (PCClass pcClass : classlist)
		{
			Boolean isM = pcClass.get(ObjectKey.IS_MONSTER);
			if (isM == null || !isM.booleanValue())
			{
				int classLevel = 0;
				for (PCClassLevel pcl : levellist)
				{
					int level = pcClass.getCDOMLevel(pcl);
					classLevel = Math.max(classLevel, level);
				}
				// Techically if classLevel == -1 we have a VERY funky PC
				// but we'll let that fly for now
				if (classLevel >= 0)
				{
					totalLevels += classLevel;
				}
			}
		}
		return totalLevels;
	}

	private PCGenLists activeLists = new PCGenLists();

	private Gender genderCDOM; // TODO Need a default that isn't Globals based

	// TODO This should (?) probably (?) be hidden
	public PCGenLists getActiveLists()
	{
		return activeLists;
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> Set<T> getCDOMLists(
			Class<T> name)
	{
		return activeLists.getLists(name);
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> Collection<LT> getCDOMListContents(
			T list)
	{
		return activeLists.getListContents(list);
	}

	public <LT extends CDOMObject, T extends CDOMList<LT>> AssociatedObject getCDOMListAssociation(
			T listObj, LT obj)
	{
		return activeLists.getListAssociation(listObj, obj);
	}

	public EquipmentSetFacade getEquipped()
	{
		List<EquipmentSet> list = activeGraph
				.getGrantedNodeList(EquipmentSet.class);
		int size = list.size();
		if (size == 0)
		{
			return null;
		}
		else if (size == 1)
		{
			return new EquipmentSetFacade(activeGraph, list.get(0));
		}
		throw new IllegalStateException(
				"Character can only have one Granted Equipment Set");
	}

	public int getTotalWeight(PrereqObject pro)
	{
		if (pro == null)
		{
			return -1;
		}
		DirectedNodeWeightCalculation<PrereqObject, PCGraphEdge> calc = new DirectedNodeWeightCalculation<PrereqObject, PCGraphEdge>(
				activeGraph)
		{
			@Override
			protected int getEdgeWeight(int weight, PCGraphEdge edge)
			{
				PrereqObject source = edge.getNodeAt(0);
				Integer i = edge.getAssociation(AssociationKey.WEIGHT);
				int edgeWeight = i == null ? 1 : i.intValue();
				if (source instanceof CDOMObject)
				{
					CDOMObject cdo = (CDOMObject) source;
					Boolean mult = cdo.get(ObjectKey.MULTIPLE_ALLOWED);
					if (mult == null || !mult.booleanValue())
					{
						return weight <= 0 ? 0 : edgeWeight;
					}
				}
				return weight * edgeWeight;
			}

		};
		return calc.calculateNodeWeight(pro);
	}

	/**
	 * Determine the number of hands the character has. This is based on their
	 * race and any applied templates.
	 * 
	 * @return The number of hands.
	 */
	public int getCDOMHands()
	{
		List<Race> list = activeGraph.getGrantedNodeList(Race.class);
		int hands = 0;
		for (Race r : list)
		{
			Integer rh = r.get(IntegerKey.HANDS);
			if (rh != null)
			{
				hands = rh.intValue();
			}
		}

		// Scan templates for any overrides
		List<PCTemplate> tlist = activeGraph
				.getGrantedNodeList(PCTemplate.class);
		for (PCTemplate t : tlist)
		{
			Integer th = t.get(IntegerKey.HANDS);
			if (th != null)
			{
				hands = th.intValue();
			}
		}
		return hands;
	}

	/**
	 * Determine the number of legs the character has. This is based on their
	 * race and any applied templates.
	 * 
	 * @return The number of legs.
	 */
	public int getCDOMLegs()
	{
		List<Race> list = activeGraph.getGrantedNodeList(Race.class);
		int legs = 0;
		for (Race r : list)
		{
			Integer rh = r.get(IntegerKey.LEGS);
			if (rh != null)
			{
				legs = rh.intValue();
			}
		}

		// Scan templates for any overrides
		List<PCTemplate> tlist = activeGraph
				.getGrantedNodeList(PCTemplate.class);
		for (PCTemplate t : tlist)
		{
			Integer th = t.get(IntegerKey.LEGS);
			if (th != null)
			{
				legs = th.intValue();
			}
		}
		return legs;
	}

	public void setCDOMGender(Gender g)
	{
		genderCDOM = g;
	}

	/**
	 * Returns the character's gender.
	 * 
	 * This method will return the stored gender or the template locked gender
	 * if there is one.
	 * 
	 * @return The Player Character's Gender
	 */
	public Gender getCDOMGender()
	{
		Gender gen = genderCDOM;
		List<PCTemplate> tlist = activeGraph
				.getGrantedNodeList(PCTemplate.class);
		for (PCTemplate t : tlist)
		{
			Gender g = t.get(ObjectKey.GENDER_LOCK);
			if (g != null)
			{
				gen = g;
			}
		}
		return gen;
	}

	public int getCDOMDamageReduction(String key)
	{
		int maxVal = -1;
		for (pcgen.cdom.content.DamageReduction dr : getActiveGraph()
				.getGrantedNodeList(pcgen.cdom.content.DamageReduction.class))
		{
			if (dr.getBypass().equalsIgnoreCase(key))
			{
				Formula f = dr.getReduction();
				int val = f.resolve(this, "getDR");
				if (val > maxVal)
				{
					maxVal = val;
				}
			}
		}
		// TODO add in BONUS
		// maxVal += (int) getTotalBonusTo("DR", key);
		return maxVal;
	}

	/**
	 * Determine the character's reach. This is based on their race, any applied
	 * templates and any other bonuses to reach.
	 * 
	 * @return The reach radius.
	 */
	public int getCDOMReach()
	{
		List<Race> list = activeGraph.getGrantedNodeList(Race.class);
		int reach = 0;
		for (Race r : list)
		{
			Integer rh = r.get(IntegerKey.REACH);
			if (rh != null)
			{
				reach = rh.intValue();
			}
		}

		// Scan templates for any overrides
		List<PCTemplate> tlist = activeGraph
				.getGrantedNodeList(PCTemplate.class);
		for (PCTemplate t : tlist)
		{
			Integer th = t.get(IntegerKey.REACH);
			if (th != null)
			{
				reach = th.intValue();
			}
		}
		// TODO add in BONUS
		// reach += (int) getTotalBonusTo("COMBAT", "REACH");
		return reach;
	}

	public int getTotalCDOMMonsterLevels()
	{
		List<PCClass> classlist = activeGraph.getGrantedNodeList(PCClass.class);
		List<PCClassLevel> levellist = activeGraph
				.getGrantedNodeList(PCClassLevel.class);
		int totalLevels = 0;
		for (PCClass pcClass : classlist)
		{
			Boolean isM = pcClass.get(ObjectKey.IS_MONSTER);
			if (isM != null && isM.booleanValue())
			{
				int classLevel = 0;
				for (PCClassLevel pcl : levellist)
				{
					int level = pcClass.getCDOMLevel(pcl);
					classLevel = Math.max(classLevel, level);
				}
				// Techically if classLevel == -1 we have a VERY funky PC
				// but we'll let that fly for now
				if (classLevel >= 0)
				{
					totalLevels += classLevel;
				}
			}
		}
		return totalLevels;
	}

	public int calcCharacterSR()
	{
		DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge> trav = new DirectedBreadthFirstTraverseAlgorithm<PrereqObject, PCGraphEdge>(
				activeGraph)
		{
			@Override
			protected boolean canTraverseEdge(PCGraphEdge edge,
					PrereqObject gn, int type)
			{
				return !(gn instanceof Equipment)
						&& super.canTraverseEdge(edge, gn, type);
			}
		};
		trav.traverseFromNode(activeGraph.getRoot());
		Set<PrereqObject> list = trav.getVisitedNodes();
		int res = 0;
		for (PrereqObject pro : list)
		{
			if (SPELL_RESISTANCE_CLASS.isInstance(pro))
			{
				SpellResistance sr = SPELL_RESISTANCE_CLASS.cast(pro);
				res = Math.max(res, sr.getReduction().resolve(this, ""));
			}
		}
		// TODO add in BONUS
		// res += (int) getTotalBonusTo("MISC", "SR");
		//
		// This would make more sense to just not add in the first place...
		//
		// TODO add in BONUS
		// res -= (int) getEquipmentBonusTo("MISC", "SR");
		return res;
	}

	public SizeAdjustment getCDOMSize()
	{
		Resolver<SizeAdjustment> resolver = null;
		List<Race> list = activeGraph.getGrantedNodeList(Race.class);
		int mod = 0;
		for (Race r : list)
		{
			Resolver<SizeAdjustment> res = r.get(ObjectKey.SIZE);
			if (res != null)
			{
				resolver = res;
			}
			// Now see if there is a HD advancement in size
			// (Such as for Dragons)
			for (int i = 0; i < r
					.sizesAdvancedCDOM(getTotalCDOMMonsterLevels()); ++i)
			{
				mod++;
			}
		}

		// Scan templates for any overrides
		List<PCTemplate> tlist = activeGraph
				.getGrantedNodeList(PCTemplate.class);
		for (PCTemplate t : tlist)
		{
			Resolver<SizeAdjustment> res = t.get(ObjectKey.SIZE);
			if (res != null)
			{
				resolver = res;
			}
		}
		// Now check and see if a class has modified
		// the size of the character with something like:
		// BONUS:SIZEMOD|NUMBER|+1
		// TODO add in BONUS
		// mod += (int) getTotalBonusTo("SIZEMOD", "NUMBER");

		if (resolver == null)
		{
			return rules.getDefaultSizeAdjustment();
		}

		SizeAdjustment size = resolver.resolve();
		while (mod < 0)
		{
			SizeAdjustment prev = rules.getPreviousSize(size);
			if (prev == null)
			{
				return size;
			}
			size = prev;
			mod++;
		}
		while (mod > 0)
		{
			SizeAdjustment next = rules.getNextSize(size);
			if (next == null)
			{
				return size;
			}
			size = next;
			mod--;
		}
		return size;
	}

	public int getAssociatedCount(PrereqObject pro)
	{
		List<PCGraphEdge> list = activeGraph.getInwardEdgeList(pro);
		// TODO Need to consider mult yes/no stack yes/no
		List set = new ArrayList();
		for (PCGraphEdge edge : list)
		{
			set.add(edge.getAssociation(AssociationKey.ABILITY_ASSOCIATION));
		}
		return set.size();
	}

	public <AT extends PObject> List<AT> getAssociated(Ability a)
	{
		List<PCGraphEdge> list = activeGraph.getInwardEdgeList(a);
		// TODO Need to consider mult yes/no stack yes/no
		List set = new ArrayList();
		for (PCGraphEdge edge : list)
		{
			set.add(edge.getAssociation(AssociationKey.ABILITY_ASSOCIATION));
		}
		return set;
	}

	public boolean containsAssociatedKey(Ability a, String assocKey)
	{
		if (assocKey == null)
		{
			return false;
		}
		List<PCGraphEdge> list = activeGraph.getInwardEdgeList(a);
		for (PCGraphEdge edge : list)
		{
			CDOMObject assoc = edge
					.getAssociation(AssociationKey.ABILITY_ASSOCIATION);
			if (assoc != null && assocKey.equals(assoc.getKeyName()))
			{
				return true;
			}
		}
		return false;
	}

	public Alignment getCDOMAlignment()
	{
		List<Alignment> align = activeGraph.getGrantedNodeList(Alignment.class);
		if (align.size() > 1)
		{
			// Error
		}
		return align.size() == 0 ? null : align.get(0);
	}

	public Deity getCDOMDeity()
	{
		return get(ObjectKey.DEITY);
	}

	public String getCDOMSubRegion()
	{
		String pcSubRegion = get(StringKey.SUB_REGION);
		if (pcSubRegion != null)
		{
			return pcSubRegion; // character's subregion trumps any from
			// templates
		}

		for (PCTemplate template : activeGraph
				.getGrantedNodeList(PCTemplate.class))
		{
			String tempSubRegion = template
					.get(pcgen.cdom.enumeration.StringKey.SUB_REGION);
			if (tempSubRegion != null)
			{
				pcSubRegion = tempSubRegion;
			}
		}

		return pcSubRegion;
	}

	public String getCDOMRegion()
	{
		String pcRegion = get(StringKey.REGION);
		if (pcRegion != null)
		{
			return pcRegion; // character's region trumps any from templates
		}

		for (PCTemplate template : activeGraph
				.getGrantedNodeList(PCTemplate.class))
		{
			String tempRegion = template
					.get(pcgen.cdom.enumeration.StringKey.REGION);
			if (tempRegion != null)
			{
				pcRegion = tempRegion;
			}
		}

		return pcRegion;
	}

	public int getCDOMMovement(String moveType)
	{
		BigDecimal maxMove = BigDecimal.ZERO;
		for (SimpleMovement move : activeGraph
				.getGrantedNodeList(SimpleMovement.class))
		{
			BigDecimal movement = move.getMovement();
			if (movement.compareTo(maxMove) > 0)
			{
				maxMove = movement;
			}
		}
		// TODO Handle MOVEA, BONUS, etc.
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTotalCDOMCheck(Check check)
	{
		// TODO These are saving throws... not even sure how they are stored ;)
		// TODO Auto-generated method stub
		return 0;
	}

	public int getBaseCDOMCheck(Check check)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAttackCount(AttackType unarmed)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCDOMhitPoints()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public Float getVariableValue(String formula, String source)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int baseAttackBonus()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean checkQualifyList(PObject caller)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canCastSpellTypeLevel(String castingType, int requiredLevel,
			int requiredNumber)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
