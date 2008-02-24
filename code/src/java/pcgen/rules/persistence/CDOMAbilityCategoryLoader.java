package pcgen.rules.persistence;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.rules.context.LoadContext;

public class CDOMAbilityCategoryLoader extends CDOMLineLoader<CDOMAbilityCategory>
{

	private static final Class<CDOMAbilityCategory> AC_CLASS = CDOMAbilityCategory.class;

	public CDOMAbilityCategoryLoader()
	{
		super("ABILITYCATEGORY", AC_CLASS);
	}

	@Override
	public CDOMAbilityCategory getCDOMObject(LoadContext context, String tok)
	{
		return CDOMAbilityCategory.getConstant(tok);
	}

}
