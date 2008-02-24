package plugin.primitive.spell;

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.inst.DomainSpellList;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

public class DomainListToken implements PrimitiveToken<CDOMSpell>
{

	private static final Formula MINFORMULA = FormulaFactory
			.getFormulaFor(Integer.MIN_VALUE);
	private static final Formula MAXFORMULA = FormulaFactory
			.getFormulaFor(Integer.MAX_VALUE);

	private CDOMSingleRef<DomainSpellList> ref;
	private Formula levelMax = MAXFORMULA;
	private Formula levelMin = MINFORMULA;
	private Boolean known = null;

	public boolean initialize(LoadContext context, String value, String args)
	{
		ref = context.ref.getCDOMReference(DomainSpellList.class, value);
		boolean ret = true;
		if (args != null)
		{
			ret |= initializeRestriction(args);
		}
		return ret;
	}

	public String getTokenName()
	{
		return "DOMAINLIST";
	}

	public Class<CDOMSpell> getReferenceClass()
	{
		return CDOMSpell.class;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(CharacterDataStore pc, CDOMSpell obj)
	{
		return pc.getActiveLists().listContains(ref.resolvesTo(), obj);
	}

	private boolean initializeRestriction(String restrString)
	{
		StringTokenizer restr = new StringTokenizer(restrString, ";");
		while (restr.hasMoreTokens())
		{
			String tok = restr.nextToken();
			if (tok.startsWith("LEVELMAX="))
			{
				levelMax = FormulaFactory.getFormulaFor(tok.substring(9));
			}
			else if (tok.startsWith("LEVELMIN="))
			{
				levelMin = FormulaFactory.getFormulaFor(tok.substring(9));
			}
			else if ("KNOWN=YES".equals(tok))
			{
				known = Boolean.TRUE;
			}
			else if ("KNOWN=NO".equals(tok))
			{
				known = Boolean.FALSE;
			}
			else
			{
				Logging.errorPrint("Unknown restriction: " + restr);
				return false;
			}
		}
		return true;
	}
}
