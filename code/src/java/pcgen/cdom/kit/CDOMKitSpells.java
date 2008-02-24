package pcgen.cdom.kit;

import java.util.ArrayList;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.inst.CDOMAbility;
import pcgen.cdom.inst.CDOMPCClass;
import pcgen.cdom.inst.CDOMSpell;

public class CDOMKitSpells extends AbstractCDOMKitObject
{

	private Formula count;
	private String spellBook;
	private CDOMSingleRef<CDOMPCClass> castingClass;

	public CDOMSingleRef<CDOMPCClass> getCastingClass()
	{
		return castingClass;
	}

	public void setCastingClass(CDOMSingleRef<CDOMPCClass> castingClass)
	{
		this.castingClass = castingClass;
	}

	public String getSpellBook()
	{
		return spellBook;
	}

	public void setSpellBook(String spellBook)
	{
		this.spellBook = spellBook;
	}

	public Formula getCount()
	{
		return count;
	}

	public void setCount(Formula count)
	{
		this.count = count;
	}

	public void addSpell(CDOMSingleRef<CDOMSpell> spell,
			ArrayList<CDOMSingleRef<CDOMAbility>> featList, int count2)
	{
		// TODO Auto-generated method stub

	}

}
