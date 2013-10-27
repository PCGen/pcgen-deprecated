package pcgen.gui;

import gmgen.pluginmgr.PluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import pcgen.cdom.transition.CompoundCampaign;
import pcgen.cdom.transition.CustomCampaign;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;
import pcgen.core.utils.ShowMessageConsoleObserver;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.utils.NonGuiChooser;
import pcgen.gui.utils.NonGuiChooserRadio;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

public class ReportUnconstructed
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		Globals.setUseGUI(false);

		ShowMessageConsoleObserver messageObserver =
				new ShowMessageConsoleObserver();
		ShowMessageDelegate.getInstance().addObserver(messageObserver);

		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		try
		{
			SettingsHandler.readOptionsProperties();
			SettingsHandler.getOptionsFromProperties(null);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);

			String message = e.getMessage();

			if ((message == null) || (message.length() == 0))
			{
				message = "Unknown error whilst reading options.ini";
			}

			message +=
					"\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			ShowMessageDelegate.showMessageDialog(new MessageWrapper(message,
				"PCGen - Error processing Options.ini", MessageType.ERROR));

			System.exit(0);
		}

		if (args.length == 0)
		{
			Logging.errorPrint("You must specify Campaign Files (PCC files) "
				+ "on the command line");
		}

		ChooserFactory.setInterfaceClassname(NonGuiChooser.class.getName());
		ChooserFactory.setRadioInterfaceClassname(NonGuiChooserRadio.class
			.getName());

		final PersistenceManager pManager = PersistenceManager.getInstance();
		try
		{
			pManager.initialize();
			CompoundCampaign cc = new CompoundCampaign();

			boolean first = true;
			boolean notfound = false;

			for (String fn : args)
			{
				if (first)
				{
					if (fn.startsWith("-outfile="))
					{
						FileHandler ch = new FileHandler(fn.substring(9));
						Logger.getLogger("pcgen").addHandler(ch);
						Logger.getLogger("plugin").addHandler(ch);
						continue;
					}
					if (fn.equalsIgnoreCase("-warning"))
					{
						Logger.getLogger("pcgen").setLevel(Logging.LST_INFO);
						Logger.getLogger("plugin").setLevel(Logging.LST_INFO);
						continue;
					}
				}
				Campaign c =
						Globals.getCampaignByURI(new File(fn).toURI(), true);
				if (c == null)
				{
					notfound = true;
					System.err.println("Can't find: " + fn);
				}
				else
				{
					cc.addCampaign(c);
				}
				first = false;
			}
			if (notfound)
			{
				System.err.println("Didn't find at least one campaign.  Known campaigns are:");
				for (Campaign c : Globals.getCampaignList())
				{
					System.err.println(c.getSourceURI());
				}
			}
			cc.addCampaign(new CustomCampaign());
			new SystemLoader().loadCampaign(new RuntimeLoadContext(), cc);
		}
		catch (PersistenceLayerException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(),
				Constants.s_APPNAME, MessageType.WARNING);
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ArrayList arrayList = new ArrayList();
		// for (Campaign c : Globals.getCampaignList())
		// {
		// arrayList.clear();
		// arrayList.add(c);
		// try
		// {
		// final PersistenceManager pManager = PersistenceManager.getInstance();
		// pManager.initialize();
		// pManager.loadCampaigns(arrayList);
		// System.err.println(c.getSourceURI());
		// try
		// {
		// Thread.sleep(10000);
		// }
		// catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// catch (PersistenceLayerException e)
		// {
		// e.printStackTrace();
		// }
		// }
	}
}
