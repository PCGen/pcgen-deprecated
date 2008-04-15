package plugin.primitive.spell;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMSingleRef;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

public class ClassListToken implements PrimitiveToken<CDOMSpell>
{

	private CDOMSingleRef<ClassSpellList> ref;
	private Formula levelMax = null;
	private Formula levelMin = null;
	private Boolean known = null;

	public boolean initialize(LoadContext context, String value, String args)
	{
		ref = context.ref.getCDOMReference(ClassSpellList.class, value);
		boolean ret = true;
		if (args != null)
		{
			ret |= initializeRestriction(args);
		}
		return ret;
	}

	public String getTokenName()
	{
		return "CLASSLIST";
	}

	public Class<CDOMSpell> getReferenceClass()
	{
		return CDOMSpell.class;
	}

	public Set<CDOMSpell> getSet(CharacterDataStore pc)
	{
		return new HashSet<CDOMSpell>(pc.getActiveLists().getListContents(
				ref.resolvesTo()));
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ref.getLSTformat());
		boolean usedBracket = false;
		if (known != null)
		{
			sb.append('[');
			sb.append("KNOWN=");
			sb.append(known.booleanValue() ? "YES" : "NO");
			usedBracket = true;
		}
		if (levelMin != null)
		{
			if (!usedBracket)
			{
				sb.append('[');
			}
			else
			{
				sb.append(';');
			}
			sb.append("LEVELMIN=");
			sb.append(levelMin.toString());
			usedBracket = true;
		}
		if (levelMax != null)
		{
			if (!usedBracket)
			{
				sb.append('[');
			}
			else
			{
				sb.append(';');
			}
			sb.append("LEVELMAX=");
			sb.append(levelMax.toString());
			usedBracket = true;
		}
		if (usedBracket)
		{
			sb.append(']');
		}
		return sb.toString();
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
