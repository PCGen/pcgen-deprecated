package pcgen.rules.persistence;

import java.util.Collection;

import pcgen.cdom.enumeration.CDOMAbilityCategory;
import pcgen.cdom.enumeration.ObjectKey;
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
		CDOMAbilityCategory ac = CDOMAbilityCategory.getConstant(tok);
		ac.put(ObjectKey.SOURCE_URI, context.ref.getSourceURI());
		return ac;
	}

	@Override
	protected Collection<CDOMAbilityCategory> getConstructedObjects(
			LoadContext lc)
	{
		return CDOMAbilityCategory.getAllConstants();
	}

}
