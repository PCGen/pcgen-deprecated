package plugin.lsttokens.eqslot;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.EquipSlotLstToken;

/**
 * Class deals with NUMSLOTS Token
 */
public class NumslotsToken implements EquipSlotLstToken
{

	public String getTokenName()
	{
		return "NUMSLOTS";
	}

	public boolean parse(EquipSlot eqSlot, String value)
	{
		//TODO: (DJ) this sucks, and means we have tokens that we don't know the names of.  we need new syntax here.
		//TODO: revisit in 5.11.x
		final StringTokenizer token =
				new StringTokenizer(value, SystemLoader.TAB_DELIM);

		while (token.hasMoreTokens())
		{
			// parse the default number of each type
			final String cString = token.nextToken().trim();
			final StringTokenizer cTok = new StringTokenizer(cString, ":");

			if (cTok.countTokens() == 2)
			{
				final String eqSlotType = cTok.nextToken();
				final String aNum = cTok.nextToken();
				Globals.setEquipSlotTypeCount(eqSlotType, aNum);
			}
		}
		return true;
	}
}
