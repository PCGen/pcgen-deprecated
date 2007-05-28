package plugin.pretokens.test;

import org.junit.Test;

import pcgen.cdom.content.EquipmentSet;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.graph.PCGenGraph;
import pcgen.cdom.graph.PCGraphGrantsEdge;
import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;

public abstract class AbstractCDOMPreEquipTestCase extends
		AbstractCDOMPreTestTestCase<Equipment>
{

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public Class<? extends PObject> getFalseClass()
	{
		return PCTemplate.class;
	}

	public abstract String getKind();

	public abstract PrerequisiteTest getTest();

	public abstract int getProperLocation();

	public abstract int getFalseLocation();

	private EquipmentSet activeSet = new EquipmentSet();

	public Equipment equip(String s, int loc)
	{
		Equipment e = getObject(s);
		equip(e, loc);
		return e;
	}

	public void equip(Equipment e, int loc)
	{
		PCGenGraph graph = pc.getActiveGraph();
		graph.addNode(e);
		PCGraphGrantsEdge edge =
				new PCGraphGrantsEdge(activeSet, e, "TestCase");
		edge.setAssociation(AssociationKey.EQUIPMENT_LOCATION, Integer
			.valueOf(loc));
		graph.addEdge(edge);
	}

	public void unequip(Equipment e)
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
		Equipment wild = equip("Wild Mage", getProperLocation());
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		unequip(wild);
		equip("Winged Mage", getProperLocation());
		assertEquals(1, getTest().passesCDOM(prereq, pc));
		assertEquals(0, getTest().passesCDOM(getParenPrereq(), pc));
	}

	@Test
	public void testFalseObject() throws PrerequisiteException
	{
		Prerequisite prereq = getSimplePrereq();
		// PC Should start without
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		equip("Winged Mage", getFalseLocation());
		// Wrong loc
		assertEquals(0, getTest().passesCDOM(prereq, pc));
		equip("Wild Mage", getProperLocation());
		// Wrong item
		assertEquals(0, getTest().passesCDOM(prereq, pc));
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
			Equipment winged = equip("Winged Creature", getProperLocation());
			// Not qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			unequip(winged);
			equip("Wild Mage", getProperLocation());
			assertEquals(1, getTest().passesCDOM(prereq, pc));
		}
	}

	@Test
	public void testFalseWildcard() throws PrerequisiteException
	{
		if (isWildcardLegal())
		{
			Prerequisite prereq = getWildcard();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			equip("Winged Creature", getProperLocation());
			// Not qualifying
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			equip("Wild Mage", getFalseLocation());
			// Not enough
			assertEquals(0, getTest().passesCDOM(prereq, pc));
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
			equip("Winged Creature", getFalseLocation());
			// Still at zero qualifying
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
			PObject katana = equip("Katana", getProperLocation());
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
	public void testFalseTypeDot() throws PrerequisiteException
	{
		if (isTypeLegal())
		{

			Prerequisite prereq = getTypeDotPrereq();
			// PC Should start without
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = equip("Katana", getFalseLocation());
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			// Would be 1 if true
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.removeListFor(ListKey.TYPE);
			// Isn't the proper type anymore
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
			// Would be 1 if true
			assertEquals(0, getTest().passesCDOM(prereq, pc));
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
			PObject katana = equip("Katana", getProperLocation());
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

	@Test
	public void testFalseTypeEqual() throws PrerequisiteException
	{
		if (isTypeLegal())
		{
			Prerequisite prereq = getTypeEqualsPrereq();
			// PC Should start without the WeaponProf
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			PObject katana = equip("Katana", getFalseLocation());
			// Not yet the proper type
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			// Would be 1 if true
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.removeListFor(ListKey.TYPE);
			// Isn't the proper type anymore
			assertEquals(0, getTest().passesCDOM(prereq, pc));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Martial"));
			katana.addToListFor(ListKey.TYPE, Type.getConstant("Cool"));
			// Would be 1 if true
			assertEquals(0, getTest().passesCDOM(prereq, pc));
		}
	}

	// TODO Test where an EqMod has modified a TYPE of a piece of Equipment

	// TODO Need to consider inverted? !PRE?

	// TODO Complex Types (more than one type)

	// TODO Test WIELDCATEGORY
}
