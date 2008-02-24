package plugin.lstcompatibility.global;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChooseActionContainer;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.cdom.helper.NoChoiceSet;
import pcgen.cdom.inst.CDOMEqMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;

public class Choose512Lst_EqBuilder extends AbstractToken implements
		CDOMCompatibilityToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "CHOOSE";
	}

	public int compatibilityLevel()
	{
		return 5;
	}

	public int compatibilityPriority()
	{
		return 3;
	}

	public int compatibilitySubLevel()
	{
		return 12;
	}

	public boolean parse(LoadContext context, CDOMObject cdo, String value)
			throws PersistenceLayerException
	{
		if (!value.startsWith("EQBUILDER|"))
		{
			// Not valid compatibility
			return false;
		}
		if (!(cdo instanceof CDOMEqMod))
		{
			return false;
		}
		NoChoiceSet pcs = new NoChoiceSet();
		Formula maxFormula = FormulaFactory.getFormulaFor(Integer.MAX_VALUE);
		Formula countFormula = FormulaFactory.getFormulaFor(1);
		ChooseActionContainer cac = cdo.getChooseContainer();
		ChoiceSet<?> choiceSet = new ChoiceSet(Constants.CHOOSE, pcs);
		cac.setChoiceSet(choiceSet);
		cac.setAssociation(AssociationKey.CHOICE_COUNT, countFormula);
		cac.setAssociation(AssociationKey.CHOICE_MAXCOUNT, maxFormula);
		return true;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
