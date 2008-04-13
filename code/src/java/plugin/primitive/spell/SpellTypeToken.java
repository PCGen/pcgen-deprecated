package plugin.primitive.spell;

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.SpellType;
import pcgen.cdom.inst.CDOMSpell;
import pcgen.character.CharacterDataStore;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

public class SpellTypeToken implements PrimitiveToken<CDOMSpell>
{

	private SpellType spellType;
	private Formula levelMax = null;
	private Formula levelMin = null;
	private Boolean known = null;

	public boolean initialize(LoadContext context, String value, String args)
	{
		spellType = SpellType.getConstant(value);
		boolean ret = true;
		if (args != null)
		{
			ret |= initializeRestriction(args);
		}
		return ret;
	}

	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	public Class<CDOMSpell> getReferenceClass()
	{
		return CDOMSpell.class;
	}

	public Set<CDOMSpell> getSet(CharacterDataStore pc)
	{
		/*
		 * TODO Define how this works
		 */
		return null;
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(spellType.toString());
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
		/*
		 * TODO Define how this works
		 */
		return false;
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
