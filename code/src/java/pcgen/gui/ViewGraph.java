package pcgen.gui;

import gmgen.pluginmgr.PluginLoader;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;

import pcgen.cdom.graph.PCGenGraph;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;
import pcgen.core.utils.ShowMessageConsoleObserver;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.graph.GraphViewFrame;
import pcgen.gui.utils.NonGuiChooser;
import pcgen.gui.utils.NonGuiChooserRadio;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

public class ViewGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Globals.setUseGUI(false);

		ShowMessageConsoleObserver messageObserver = new ShowMessageConsoleObserver();
		ShowMessageDelegate.getInstance().addObserver(messageObserver);

		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		try {
			SettingsHandler.readOptionsProperties();
			SettingsHandler.getOptionsFromProperties(null);
		} catch (Exception e) {
			e.printStackTrace(System.err);

			String message = e.getMessage();

			if ((message == null) || (message.length() == 0)) {
				message = "Unknown error whilst reading options.ini";
			}

			message += "\n\nIt MAY be possible to fix this problem by deleting your options.ini file.";
			ShowMessageDelegate.showMessageDialog(new MessageWrapper(message,
					"PCGen - Error processing Options.ini", MessageType.ERROR));

			System.exit(0);
		}

		if (args.length == 0) {
			Logging.errorPrint("You must specify Campaign Files (PCC files) "
					+ "on the command line");
		}

		ChooserFactory.setInterfaceClassname(NonGuiChooser.class.getName());
		ChooserFactory.setRadioInterfaceClassname(NonGuiChooserRadio.class
				.getName());

		final PersistenceManager pManager = PersistenceManager.getInstance();
		try {
			pManager.initialize();
			ArrayList<Campaign> arrayList = new ArrayList<Campaign>();
			for (String fn : args) {
				Campaign c = Globals.getCampaignByURI(new File(fn).toURI(), true);
				arrayList.add(c);
			}
			pManager.loadCampaigns(arrayList);
		} catch (PersistenceLayerException e) {
			ShowMessageDelegate.showMessageDialog(e.getMessage(),
					Constants.s_APPNAME, MessageType.WARNING);
		}
		
		//TODO need to update this
		//PCGenGraph master = Globals.getMasterGraph();
		//JFrame frame = new GraphViewFrame(master);
		//frame.pack();
		//frame.setVisible(true);
	}
}
