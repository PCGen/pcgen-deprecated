package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.LoadContext;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.LstLoader;
import pcgen.persistence.lst.PCClassLoader;

public class PCClassLoaderFacade implements LstLoader<PCClass>
{

	PCClassLoader loader = new PCClassLoader();

	public void parseLine(LoadContext context, PCClass reference, String line,
		CampaignSourceEntry sourceEntry) throws PersistenceLayerException
	{
		loader.parseLine(context, reference, line, sourceEntry);
	}
}
