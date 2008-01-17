package pcgen.cdom.helper;

import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.core.PlayerCharacter;

public class FormulaChoiceSet implements PrimitiveChoiceSet<Integer>
{

	private final Formula minimum;
	private final Formula maximum;

	public FormulaChoiceSet(Formula min, Formula max)
	{
		minimum = min;
		maximum = max;
	}

	public Class<Integer> getReferenceClass()
	{
		return Integer.class;
	}

	public Set<Integer> getSet(PlayerCharacter pc)
	{
		/*
		 * TODO Need to resolve Formulas
		 */
		return null;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder().append("MIN=").append(minimum)
				.append("|MAX=").append(maximum);
		return sb.toString();
	}

	public Class<Integer> getChoiceClass()
	{
		return Integer.class;
	}
}
