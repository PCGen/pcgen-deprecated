package pcgen.cdom.inst;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.KitApply;
import pcgen.core.QualifiedObject;
import pcgen.core.kit.KitAbilities;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitStat;
import pcgen.util.enumeration.Visibility;

public class CDOMKit extends CDOMObject
{

	private Visibility visibility;
	private QualifiedObject<Formula> equipBuy;
	private KitApply apply;

	/*
	 * CDOM DefaultMonsterCompatibility System
	 */
	private int endingBab = Integer.MIN_VALUE;

	public void setCompatEndingBAB(int i)
	{
		endingBab = i;
	}

	private int endingWill = Integer.MIN_VALUE;

	public void setCompatEndingWillCheck(int checkValue)
	{
		endingWill = checkValue;
	}

	private int endingFortitude = Integer.MIN_VALUE;

	public void setCompatEndingFortitudeCheck(int checkValue)
	{
		endingFortitude = checkValue;
	}

	private int endingReflex = Integer.MIN_VALUE;

	public void setCompatEndingReflexCheck(int checkValue)
	{
		endingReflex = checkValue;
	}

	private int hitDice = Integer.MIN_VALUE;

	public void setCompatHitDice(int i)
	{
		hitDice = i;
	}

	private int dieSize = Integer.MIN_VALUE;

	public void setCompatHitDiceSize(int i)
	{
		dieSize = i;
	}

	public void addObject(KitSkill ks)
	{
		// TODO Auto-generated method stub

	}

	public void addObject(KitAbilities kitAbilities)
	{
		// TODO Auto-generated method stub

	}

	public void addStat(KitStat kitStat)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CDOMKit)
		{
			CDOMKit other = (CDOMKit) o;
			return other.isCDOMEqual(this) && other.equalsPrereqObject(this);
		}
		return false;
	}

	public Visibility getVisibility()
	{
		return visibility;
	}

	public void setVisibility(Visibility visibility)
	{
		this.visibility = visibility;
	}

	public QualifiedObject<Formula> getEquipBuy()
	{
		return equipBuy;
	}

	public void setEquipBuy(QualifiedObject<Formula> equipBuy)
	{
		this.equipBuy = equipBuy;
	}

	public KitApply getApply()
	{
		return apply;
	}

	public void setApply(KitApply apply)
	{
		this.apply = apply;
	}
}
