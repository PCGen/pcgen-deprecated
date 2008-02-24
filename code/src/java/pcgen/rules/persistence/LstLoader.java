package pcgen.rules.persistence;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public interface LstLoader<T extends PObject>
{
	public void parseLine(LoadContext context, T reference, String line,
		CampaignSourceEntry sourceEntry) throws PersistenceLayerException;
}
