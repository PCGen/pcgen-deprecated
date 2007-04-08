package pcgen.persistence.lst;

import pcgen.core.PObject;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;

public interface LstLoader<T extends PObject>
{
	public void parseLine(LoadContext context, T reference, String line,
		CampaignSourceEntry sourceEntry) throws PersistenceLayerException;
}
