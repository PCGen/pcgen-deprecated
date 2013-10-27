package plugin.pretokens.test;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.EquipmentSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.cdom.inst.CDOMEquipment;
import pcgen.cdom.inst.CDOMTemplate;
import pcgen.core.Equipment;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public class PreEquipTesterTest extends AbstractCDOMPreTestTestCase<CDOMEquipment>
{

	PreEquipTester tester = new PreEquipTester();

	@Override
	public Class<CDOMEquipment> getCDOMClass()
	{
		return CDOMEquipment.class;
	}

	@Override
	public Class<? extends CDOMObject> getFalseClass()
	{
		return CDOMTemplate.class;
	}

	public String getKind()
	{
		return "PREEQUIP";
	}

	public PrerequisiteTest getTest()
	{
		return tester;
	}

	public int getProperLocation()
	{
		return Equipment.EQUIPPED_PRIMARY;
	}

	private EquipmentSet activeSet = new EquipmentSet();

	public CDOMEquipment equip(String s, int loc)
	{
		CDOMEquipment e = getObject(s);
		equip(e, loc);
		return e;
	}

	public void equip(CDOMEquipment e, int loc)
	{
		PCGenGraph graph = pc.getActiveGraph();
		graph.addNode(e);
		PCGraphGrantsEdge edge =
				new PCGraphGrantsEdge(activeSet, e, "TestCase");
		edge.setAssociation(AssociationKey.EQUIPMENT_LOCATION, Integer
			.valueOf(loc));
		graph.addEdge(edge);
	}

	public void unequip(CDOMEquipment e)
	{
		pc.getActiveGraph().removeNode(e);
	}

	public boolean isTypeLegal()
	{
		return true;
	}

	public boolean isWildcardLegal()
	{
		return true;
	}

	// public Prerequisite getAnyPrereq()
	// {
	// Prerequisite p;
	// p = new Prerequisite();
	// p.setKind(getKind());
	// p.setKey("ANY");
	// p.setOperand("2");
	// p.setOperator(PrerequisiteOperator.GTEQ);
	// return p;
	// }

	public Prerequisite getSimplePrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Winged Mage");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getWildcard()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Wild%");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getCountTemplates()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("%");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getGenericPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Crossbow");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getParenPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("Crossbow (Heavy)");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTypeDotPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("TYPE.Exotic");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	public Prerequisite getTypeEqualsPrereq()
	{
		Prerequisite p;
		p = new Prerequisite();
		p.setKind(getKind());
		p.setKey("TYPE=Martial");
		p.setOperand("1");
		p.setOperator(PrerequisiteOperator.GTEQ);
		return p;
	}

	@Test
	public void testSimple() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		CDOMEquipment wild = equip("Wild Mage", getProperLocation());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		unequip(wild);
		equip("Winged Mage", getProperLocation());
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		assertEquals(0, getTest().passesCDOM(getParenPrereq(), pc));
	}

	@Test
	public void testParen() throws PrerequisiteException
	{
		Prerequisite prereq = getParenPrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		equip("Crossbow (Heavy)", getProperLocation());
		// Has Crossbow (Heavy)
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		// But not Katana
		assertEquals(0, getTest().passesCDOM(getSimplePrereq(), pc));
		// And not Generic Crossbow
		assertEquals(0, getTest().passesCDOM(getGenericPrereq(), pc));
	}

	@Test
	public void testWildcard() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getWildcard();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMEquipment winged = equip("Winged Creature", getProperLocation());
			// Not qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			unequip(winged);
			equip("Wild Mage", getProperLocation());
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testCount() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getCountTemplates();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			equip("Wild Mage", getProperLocation());
			// enough
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeDot() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getTypeDotPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject katana = equip("Katana", getProperLocation());
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			assertEquals(1, getTest().passesCDOM(prereq, pc));
			katana.removeListFor(ListKey.TYPE);
			// Isn't the proper type anymore
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
			// Test having multiple types
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testTypeEqual() throws PrerequisiteException
	{
		if (isTypeLegal())
		{

			Prerequisite prereq = getTypeEqualsPrereq();
			// PC Should start without the WeaponProf
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			CDOMObject katana = equip("Katana", getProperLocation());
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			assertEquals(1, getTest().passesCDOM(prereq, pc));
			katana.removeListFor(ListKey.TYPE);
			// Isn't the proper type anymore
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
			// Test with WP having multiple types
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	// TODO Test where an EqMod has modified a TYPE of a piece of Equipment

	// TODO Need to consider inverted? !PRE?

	// TODO Complex Types (more than one type)
	
	// TODO Test WIELDCATEGORY
}
