package pcgen.cdom.choiceset;

import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.character.CharacterDataStore;

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

	public Set<Integer> getSet(CharacterDataStore pc)
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
