/*
 * MainSource.java
 * Copyright 2002 (C) Bryan McRoberts
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 * ReCreated on Feb 28, 2002 5:10 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import pcgen.core.Campaign;
import pcgen.core.CampaignURL;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SourceEntry;
import pcgen.core.CampaignURL.URLKind;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.tabs.InfoTabUtils;
import pcgen.gui.tabs.spells.InfoPreparedSpells;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.SponsorLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;
import pcgen.util.SwingWorker;
import pcgen.util.enumeration.Tab;

/**
 *  <code>MainSource</code> .
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com), Jason Buchanan (lonejedi70@hotmail.com)
 * @version $Revision$
 *
 * Campaigns is kind of misleading - it's meant to be a collection of
 * sources from which players can pick their character's
 * options. Campaign is also used by most groups to refer to their
 * game, so Campaign is kind of misleading.  Therefore we've decided
 * to change the "Campaign" tab to "Source Materials" to avoid this
 * confusion.  cu merton_monk 6 Sept, 2001.
 */
public class MainSource extends FilterAdapterPanel
{
	/** For I18N and so that PCGen_Frame1 can use this as a tooltip */
	public static final String SOURCE_MATERIALS_TAB = "Select and load source materials";
	static final long serialVersionUID = -2654080650560664447L;
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	//column positions for tables
	private static final int COL_NAME = 0;
	private static final int COL_LOADED = 1;

	//view modes for tables
	private static final int VIEW_PRODUCT = 0;
	private static final int VIEW_PUBLISH = 1;
	private static final int VIEW_PUBSET = 2;
	private static final int VIEW_PUBFMTSET = 3;
	//private static final int VIEW_RANK = 4;
	private static int viewMode = VIEW_PUBFMTSET; // keep track of what view mode we're in for Available
	private static int viewSelectMode = VIEW_PRODUCT; // keep track of what view mode we're in for Selected. defaults to "Name"

	//table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECT = 1;
	private final JLabel avaLabel = new JLabel( /*"Available"*/
		);
	private final JLabel selLabel = new JLabel( /*"Selected"*/
		);
	private CampaignModel availableModel = null; // Model for the TreeTable.
	private CampaignModel selectedModel = null; // Model for the JTreeTable.
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane splitPane;
	private JButton leftButton;
	private JButton loadButton = new JButton();

	//bottom right pane ("Interaction" Pane)
	private JButton refreshButton = new JButton();
	private JButton removeAllButton;
	private JButton rightButton;
	private JButton unloadAllButton = new JButton();
	private JButton websiteButton;
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private JLabelPane infoLabel;
	private JPanel bLeftPane;
	private JPanel center = new JPanel();
	private JPanel jPanel1n = new JPanel();
	private JPanel jPanel1s = new JPanel();
	private JTreeTable availableTable; // the available Campaigns
	private JTreeTable selectedTable; // the selected Campaigns
	private List<Campaign> selectedCampaigns = new ArrayList<Campaign>();
	private PObjectNode lastSelection = null; //keep track of which PObjectNode was last selected from either table
	private TreePath selPath;
	private boolean hasBeenSized = false;
	private boolean sourcesLoaded = false;

	private final JLabel lblQFilter = new JLabel("Filter:");
	private JTextField textQFilter = new JTextField();
	private JButton clearQFilterButton = new JButton("Clear");
	private static Integer saveViewMode = null;

	// Right-click table item
	private int selRow;

	/**
	 * Constructor
	 */
	public MainSource()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Tab.SOURCES.toString());

		try
		{
			initComponents();
		}
		catch (Exception e)
		{
			ShowMessageDelegate.showMessageDialog("Error in MainSource whilst initialising:\n " + e.toString() + "\n"
			+ "PCGen may not operate correctly as a result of this error. ",
				Constants.s_APPNAME, MessageType.ERROR);
			Logging.errorPrint("Error initialising MainSource: " + e.toString(), e);
		}

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * specifies whether the "match any" option should be available
	 * @return true
	 */
	@Override
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 * @return true
	 */
	@Override
	public boolean isNegateEnabled()
	{
		return true;
	}

	/*
	 * currently there are no useful filters for this tab.
	 * maybe we should remove filtering altogether.
	 *
	 * author: Thomas Behr 04-03-02
	 */

	/**
	 * specifies the filter selection mode
	 * @return FilterConstants.DISABLED_MODE = -2
	 */
	@Override
	public int getSelectionMode()
	{
		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * Change the game mode
	 */
	public void changedGameMode()
	{
		selectedCampaigns.clear();
		//resetViewNodes();
		unloadAllCampaigns_actionPerformed();
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		// Do Nothing
	}

	/**
	 * Refresh the campaigns
	 */
	public void refreshCampaigns()
	{
		PersistenceManager.getInstance().refreshCampaigns();
		updateModels();
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		/*
		 * this does probably too much ...
		 * but I'm too lazy to get into the details here!
		 *
		 * author: Thomas Behr 04-03-02
		 */
		updateModels();
		selectCampaignsByURI(PersistenceManager.getInstance().getChosenCampaignSourcefiles());
	}

	private void clearQFilter()
	{
		availableModel.clearQFilter();
		if (saveViewMode != null)
		{
			viewMode = saveViewMode.intValue();
			saveViewMode = null;
		}
		textQFilter.setText("");
		availableModel.resetModel(viewMode, true, false);
		clearQFilterButton.setEnabled(false);
		viewComboBox.setEnabled(true);
		updateAvailableModel();
	}

	private void setQFilter()
	{
		String aString = textQFilter.getText();

		if (aString.length() == 0)
		{
			clearQFilter();
			return;
		}
		availableModel.setQFilter(aString);

		if (saveViewMode == null)
		{
			saveViewMode = Integer.valueOf(viewMode);
		}
		viewMode = VIEW_PRODUCT;
		availableModel.resetModel(viewMode, true, false);
		clearQFilterButton.setEnabled(true);
		viewComboBox.setEnabled(false);
		updateAvailableModel();
	}

	/**
	 * Update the UI with the loaded campaigns
	 */
	public void updateLoadedCampaignsUI()
	{
		//The original intent of this method was to allow MainSource to
		// resync it's list of selected campaigns (and refresh the UI)
		// in the event that an external class updated them
		//That code has since dissappeared, so I'll reinstate it
		// along with the code that came to take its place
		// -Lone Jedi (Aug. 14, 2002)
		selectedCampaigns.clear();

		for (Campaign aCamp : Globals.getCampaignList())
		{
			if (aCamp.isLoaded())
			{
				selectedCampaigns.add(aCamp);
			}
		}

		updateModels();

		if ((getParent() != null) && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
			parent.enableLstEditors(true);
		}
	}

	private void setInfoLabelText(PObjectNode aNode)
	{
		lastSelection = aNode; //even in the case where this is null

		if (aNode != null)
		{
			if (aNode.getItem() instanceof Campaign)
			{
				final Campaign aCamp = (Campaign) aNode.getItem();

				// We can turn on the website button, since now there is a source, if there is a URL for the campaign
				final String web = aCamp.getSourceEntry().getSourceBook().getWebsite();
				websiteButton.setEnabled(web != null);

				infoLabel.setText(buildInfoLabel(aCamp));
			}
			else //must just be a branch node
			{
				// We off the website button since our source went away
				websiteButton.setEnabled(false);

				PObjectNode pathNode = aNode;
				String path = pathNode.getItem().toString();

				while ((pathNode.getParent() != availableTable.getTree().getModel().getRoot())
					&& (pathNode.getParent() != selectedTable.getTree().getModel().getRoot()))
				{
					pathNode = pathNode.getParent();
					path = pathNode.getItem().toString() + "." + path;
				}

				StringBuffer sb = new StringBuffer();

				// enclose the node-path name with the <p> tag so that we can parse for it later
				sb.append("<html><b>").append(path).append("</b><br>");
				if(Globals.getSponsor(path) != null) {
					Map<String, String> sponsor = Globals.getSponsor(path);
					sb.append("<img src='")
						.append(SponsorLoader.getConvertedSponsorPath(sponsor.get("IMAGELARGE")))
						.append("'><br>");
				}
				sb.append("</html>");
				infoLabel.setText(sb.toString());
			}
		}
	}

	/**
	 * Create the html text content for the information label for a campaign
	 * @param aCamp The campaign.
	 */
	static String buildInfoLabel(final Campaign aCamp)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>")
			.append(aCamp.getDisplayName())
			.append("</b><br>");
		if (aCamp.getCoverFiles().size() > 0)
		{
			CampaignSourceEntry image = aCamp.getCoverFiles().get(0);
			sb.append("<img src='").append(image.getURI()).append("'> ");
		}
		if (aCamp.getLogoFiles().size() > 0)
		{
			CampaignSourceEntry image = aCamp.getLogoFiles().get(0);
			sb.append("<img src='").append(image.getURI()).append("'>");
		}
		if (aCamp.getCoverFiles().size() > 0 || aCamp.getLogoFiles().size() > 0)
		{
			sb.append("<br>");
		}


		String bString = aCamp.getDefaultSourceString();
		if (bString.length() == 0)
		{
			bString = aCamp.getSourceEntry().getFormattedString(
				SourceEntry.SourceFormat.LONG, true);
		}
		sb.append("<b>SOURCE</b>: ");
		sb.append(bString);
		sb.append(" <b>by</b> ");
		sb.append(aCamp.getPubNameLong());
		sb.append("<br>\n");
		
		if (!aCamp.getDescription().equals(Constants.EMPTY_STRING))
		{
			sb.append("<b>DESCRIPTION</b>: ");
			sb.append(aCamp.getDescription());
			sb.append("<br>\n");
		}
		// Add the website URLs
		List<CampaignURL> webURLs = aCamp.getUrlListForKind(URLKind.WEBSITE);
		if (!webURLs.isEmpty())
		{
			sb.append("<b>WEBSITE</b>: ");
			sb.append(buildURLListString(webURLs));
			sb.append("<br>\n");
		}
		
		if (aCamp.getType().length() > 0)
		{
			sb.append("<b>TYPE</b>: ")
				.append(aCamp.getType())
				.append("&nbsp; ");
		}
		sb.append("<b>RANK</b>: ")
			.append(aCamp.getRank());
		if (aCamp.getGameModeString().length() > 0)
		{
			sb.append("&nbsp; <b>GAME MODE</b>: ")
				.append(aCamp.getGameModeString());
		}

		// Add the purchase URLs
		List<CampaignURL> purchaseURLs = aCamp.getUrlListForKind(URLKind.PURCHASE);
		if (!purchaseURLs.isEmpty())
		{
			sb.append("<br><b>PURCHASE</b>: ");
			sb.append(buildURLListString(purchaseURLs));
			sb.append("\n");
		}

		// Add the purchase URLs
		List<CampaignURL> surveyURLs = aCamp.getUrlListForKind(URLKind.SURVEY);
		if (!surveyURLs.isEmpty())
		{
			sb.append("<br><b>SURVEY</b>: ");
			sb.append(buildURLListString(surveyURLs));
			sb.append("\n");
		}
		
		boolean infoDisplayed = false;
		bString = aCamp.getInfoText();

		if (bString.length() > 0)
		{
			sb.append("<p><b>INFORMATION</b>:<br>")
				.append(bString)
				.append("</p>");
			infoDisplayed = true;
		}

		bString = aCamp.getSection15String();

		if (bString.length() != 0)
		{
			if (!infoDisplayed)
			{
				sb.append("<br");
			}

			sb.append("<b>COPYRIGHT</b>:<br>")
				.append(bString);
		}

		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * Builds a html display string based on the list of campaign urls.
	 * 
	 * @param urlList the list of urls
	 * 
	 * @return the display string
	 */
	private static String buildURLListString(List<CampaignURL> urlList)
	{
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (CampaignURL campaignURL : urlList)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				sb.append(" | ");
			}
			sb.append("<a href=\"").append(campaignURL.getUrl().toString());
			sb.append("\">").append(campaignURL.getUrlDesc());
			sb.append("</a>");
		}
		return sb.toString();
	}

	private void setLoadedColMaxWidth()
	{
		//make the "Loaded" checkbox column a reasonable size
		selectedTable.getColumnModel().getColumn(COL_LOADED).setMaxWidth(50);
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();

		if (model == null)
		{
			return -1;
		}

		return model.getMinSelectionIndex();
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new CampaignModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true, false);
		}
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	private void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new CampaignModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false, false);
		}
	}

	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();

						/////////////////////////
						if ((temp == null) || !(temp instanceof PObjectNode))
						{
							ShowMessageDelegate.showMessageDialog("No campaign selected. Try again.", Constants.s_APPNAME, MessageType.ERROR);

							return;
						}

						setInfoLabelText((PObjectNode) temp);

						rightButton.setEnabled(true);
						leftButton.setEnabled(false);
					}
				}
			});

		final JTree tree = availableTable.getTree();

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		MouseListener ml = new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					final int mlSelRow = tree.getRowForLocation(e.getX(), e.getY());
					final TreePath mlSelPath = tree.getPathForLocation(e.getX(), e.getY());

					if (mlSelRow != -1)
					{
						if ((e.getClickCount() == 1) && (mlSelPath != null))
						{
							tree.setSelectionPath(mlSelPath);
						}
						else if (e.getClickCount() == 2)
						{
							// We run this after the event has been processed so that
							// we don't confuse the table when we change its contents
							SwingUtilities.invokeLater(new Runnable()
								{
									public void run()
									{
										doCampaign(true);
									}
								});
						}
					}
				}
			};

		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
			{
				public void valueChanged(ListSelectionEvent e)
				{
					if (!e.getValueIsAdjusting())
					{
						final int idx = getSelectedIndex(e);

						if (idx < 0)
						{
							return;
						}

						final Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();

						if ((temp == null) || !(temp instanceof PObjectNode))
						{
							lastSelection = null;
							infoLabel.setText();

							return;
						}

						setInfoLabelText((PObjectNode) temp);

						leftButton.setEnabled(true);
						rightButton.setEnabled(false);
					}
				}
			});
		ml = new MouseAdapter()
				{
					@Override
					public void mousePressed(MouseEvent e)
					{
						final int mlSelRow = btree.getRowForLocation(e.getX(), e.getY());
						final TreePath mlSelPath = btree.getPathForLocation(e.getX(), e.getY());

						if (mlSelRow != -1)
						{
							if (e.getClickCount() == 1)
							{
								btree.setSelectionPath(mlSelPath);
							}
							else if (e.getClickCount() == 2)
							{
								// We run this after the event has been processed so that
								// we don't confuse the table when we change its contents
								SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											doCampaign(false);
										}
									});
							}
						}
					}
				};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void doCampaign(boolean select)
	{
		if (lastSelection == null)
		{
			return;
		}

		if (lastSelection.getItem() instanceof Campaign)
		{
			final Campaign theCamp = (Campaign) lastSelection.getItem();

			if (select)
			{
				if (!selectedCampaigns.contains(theCamp))
				{
					selectedCampaigns.add(theCamp);
				}
			}
			else
			{
				selectedCampaigns.remove(theCamp);
			}
		}

		// if we didn't get a Campaign back, then it must be a tree branch
		else
		{
			selectAllLeaves(lastSelection, select);
		}

		Collections.sort(selectedCampaigns);

		updateModels();

		//ensure that the target skill gets displayed in the selectedTable if you've just added a source
		if (select)
		{
			selectedTable.expandByPObjectName(lastSelection.getItem().toString());
		}

		//Remember what we just did...
		rememberSourceChanges();
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(SOURCE_MATERIALS_TAB);

		if (!hasBeenSized)
		{
			hasBeenSized = true;

			// DividerSizes need to be reset here because the UIFactory invocation in main()
			// messes them up after initComponents tries to set them
			splitPane.setDividerSize(10);
			splitPane.setDividerLocation(.5);
			bsplit.setDividerSize(10);
			bsplit.setDividerLocation(.75);
			asplit.setDividerSize(10);
			asplit.setDividerLocation(.5);
			setLoadedColMaxWidth();
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new CampaignPopupListener(treeTable, new CampaignPopupMenu(treeTable)));
	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
			{
				@Override
				public void componentShown(ComponentEvent evt)
				{
					formComponentShown();
				}
			});
		rightButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					doCampaign(true);
				}
			});
		leftButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					doCampaign(false);
				}
			});
		viewComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					viewComboBoxActionPerformed();
				}
			});
		viewSelectComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					viewSelectComboBoxActionPerformed();
				}
			});
		textQFilter.getDocument().addDocumentListener(new DocumentListener()
			{
				public void changedUpdate(DocumentEvent evt)
				{
					setQFilter();
				}
				public void insertUpdate(DocumentEvent evt)
				{
					setQFilter();
				}
				public void removeUpdate(DocumentEvent evt)
				{
					setQFilter();
				}
			});
		clearQFilterButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					clearQFilter();
				}
			});

		infoLabel.addHyperlinkListener(new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent event)
			{
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					try
					{
						BrowserLauncher.openURL(event.getURL());
					}
					catch (IOException e)
					{
						Logging.errorPrint("Failed to open URL " //$NON-NLS-1$
							+ event.getURL() + " due to ", e); //$NON-NLS-1$
						ShowMessageDelegate.showMessageDialog(PropertyFactory
							.getFormattedString("in_Src_browser", event //$NON-NLS-1$
								.getURL().toString()), Constants.s_APPNAME,
							MessageType.ERROR);
					}
				}
			}
		});
		
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		viewComboBox.addItem("Name Only");
		viewComboBox.addItem("Company");
		viewComboBox.addItem("Company/Setting");
		viewComboBox.addItem("Comp/Fmt/Setting"); //abbr. here so that all GUI elements will fit when started at default window size
		//viewComboBox.addItem("Rank");
		Utility.setDescription(viewComboBox, "You can change how the Sources in the Tables are listed.");
		viewMode = SettingsHandler.getPCGenOption("pcgen.options.sourceTab.availableListMode", VIEW_PUBFMTSET);
		viewComboBox.setSelectedIndex(viewMode); // must be done before createModels call
		viewSelectComboBox.addItem("Name Only");
		viewSelectComboBox.addItem("Company");
		viewSelectComboBox.addItem("Company/Setting");
		viewSelectComboBox.addItem("Company/Format/Setting");
		viewSelectMode = SettingsHandler.getPCGenOption("pcgen.options.sourceTab.selectedListMode", VIEW_PRODUCT);
		Utility.setDescription(viewSelectComboBox, "You can change how the Sources in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		createModels();

		// create available table of Campaigns
		createTreeTables();

		center.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		avaLabel.setText(PropertyFactory.getString("in_available"));
		leftPane.add(InfoTabUtils.createFilterPane(avaLabel, viewComboBox, lblQFilter, textQFilter, clearQFilterButton), BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(availableTable);
		leftPane.add(scrollPane, BorderLayout.CENTER);

		rightButton = new JButton(IconUtilitities.getImageIcon("Forward16.gif"));
		leftPane.add(buildModSpellPanel(rightButton, "Click to add the source"), BorderLayout.SOUTH);

		rightPane.setLayout(new BorderLayout());

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		selLabel.setText(PropertyFactory.getString("in_selected"));
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		rightPane.add(aPanel, BorderLayout.NORTH);

		scrollPane = new JScrollPane(selectedTable);
		rightPane.add(scrollPane, BorderLayout.CENTER);

		leftButton = new JButton(IconUtilitities.getImageIcon("Back16.gif"));
		rightPane.add(buildModSpellPanel(leftButton, "Click to remove the source"), BorderLayout.SOUTH);

		selectedTable.setColAlign(COL_LOADED, SwingConstants.CENTER);

		bLeftPane = new JPanel(new BorderLayout());
		JPanel bRightPane = new JPanel();

		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Source Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		infoLabel = new JLabelPane();
		JScrollPane infoScroll = new JScrollPane(infoLabel);
		infoScroll.setBorder(title1);
		bLeftPane.add(infoScroll, BorderLayout.CENTER);
		infoLabel.setBackground(bLeftPane.getBackground());

		FlowLayout aFlow = new FlowLayout();
		aFlow.setAlignment(FlowLayout.CENTER);
		bRightPane.setLayout(new BorderLayout());
		bRightPane.add(jPanel1n, BorderLayout.NORTH);
		bRightPane.add(jPanel1s, BorderLayout.CENTER);
		jPanel1n.setLayout(aFlow);
		aFlow = new FlowLayout();
		aFlow.setAlignment(FlowLayout.CENTER);
		jPanel1s.setLayout(aFlow);

		loadButton.setText("Load");
		loadButton.setMnemonic(KeyEvent.VK_L);
		loadButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					loadCampaigns_actionPerformed();
				}
			});
		jPanel1n.add(loadButton);
		loadButton.setToolTipText("This loads all the sources listed in the above table");

		unloadAllButton.setText("Unload All");
		unloadAllButton.setMnemonic(KeyEvent.VK_U);
		unloadAllButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					unloadAllCampaigns_actionPerformed();
				}
			});
		jPanel1n.add(unloadAllButton);

		removeAllButton = new JButton("Remove All");
		removeAllButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selectAll_actionPerformed(false);
				}
			});
		removeAllButton.setToolTipText("Remove all sources from the above table");
		jPanel1n.add(removeAllButton);

		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh the list of sources");
		refreshButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					refreshCampaigns();
				}
			});
		jPanel1s.add(refreshButton);

		websiteButton = new JButton("Website");
		websiteButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					launchProductWebsite(false, true);
				}
			});
		websiteButton.setToolTipText("Go to the selected product's website");
		websiteButton.setEnabled(false);
		jPanel1s.add(websiteButton);

		JButton pccButton = new JButton("Customise");

		{
			final MainSource t = this;
			pccButton.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//Create and display pcccreator popup dialog
						new PCCCreator(t);
					}
				});
		}

		pccButton.setToolTipText("Customise your own source to ease your loading process");
		jPanel1s.add(pccButton);

		asplit = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, bLeftPane, bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new FlippingSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		//go ahead and auto-load campaigns now, if that option is set
		if (SettingsHandler.isLoadCampaignsAtStart())
		{
			selectCampaignsByURI(PersistenceManager.getInstance().getChosenCampaignSourcefiles());

			if (selectedCampaigns.size() > 0)
			{
				loadCampaigns();
			}
		}
	}

	/**
	 * Build the panel with the controls to add a spell to a
	 * prepared list.
	 * @param button
	 * @param title
	 * @return The panel.
	 */
	private JPanel buildModSpellPanel(JButton button, String title)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		Utility.setDescription(button, title); //$NON-NLS-1$
		button.setEnabled(false);
		button.setMargin(new Insets(1, 14, 1, 14));
		panel.add(button);

		return panel;
	}

	private void launchProductWebsite(boolean avail, boolean isProductNotHelp)
	{
		JTreeTable treeTable;

		if (avail)
		{
			treeTable = availableTable;
		}
		else
		{
			treeTable = selectedTable;
		}

		final PObjectNode pon = (PObjectNode) treeTable.getTree().getLastSelectedPathComponent();

		if (pon != null)
		{
			if (pon.getItem() instanceof Campaign)
			{
				final Campaign theCamp = (Campaign) pon.getItem();
				final String theURL = isProductNotHelp ? theCamp.getSourceEntry().getSourceBook().getWebsite() : theCamp.getHelp();

				if (theURL!=null && !theURL.equals(""))
				{
					try
					{
						BrowserLauncher.openURL(theURL);
					}
					catch (IOException ioEx)
					{
						Logging.errorPrint("Could not open browser to " + theURL, ioEx);
						ShowMessageDelegate.showMessageDialog("Could not open browser to " + theURL, Constants.s_APPNAME, MessageType.ERROR);
					}
				}
				else
				{
					final String type = isProductNotHelp ? "web" : "help";
					ShowMessageDelegate.showMessageDialog("No "+type+" information found for Source: " + theCamp.getDisplayName(), Constants.s_APPNAME,
						MessageType.WARNING);
				}
			}
		}
		else
		{
			ShowMessageDelegate.showMessageDialog("Please select a source.", Constants.s_APPNAME, MessageType.ERROR);
		}
	}

	private void loadCampaigns()
	{
		if (selectedCampaigns.size() < 1)
		{
			return;
		}

		final PersistenceObserver observer = new PersistenceObserver();
		final PersistenceManager pManager = PersistenceManager.getInstance();
		try
		{
			pManager.addObserver( observer );
			Logging.registerHandler( observer.getHandler() );
			pManager.loadCampaigns(selectedCampaigns);
			Logging.removeHandler( observer.getHandler() );
			pManager.deleteObserver( observer );
		}
		catch (PersistenceLayerException e)
		{
			ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.s_APPNAME, MessageType.WARNING);
			unloadAllCampaigns_actionPerformed();
		}
		//observer.end();


		Globals.sortCampaigns();

	}

	private void loadCampaigns_actionPerformed()
	{
		final String oldStatus = showLoadingSources();
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				loadCampaigns();
				return "";
			}

	    @Override
			public void finished() {

				if ((getParent() != null) && Globals.displayListsHappy())
				{
					PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(MainSource.this);
					parent.enableLstEditors(true);
				}

				updateModels();
				showSourcesLoaded(oldStatus);

				if (Globals.getRootFrame() != null)
				{
					PCGen_Frame1.getInst().refreshCharInfoTabs();
				}

			}

		};
		worker.start();


	}

	/**
	 * Update the display to indicate that sources are in the process of
	 * being loaded.
	 * @return The status that was replaced with the loading message.
	 */
	String showLoadingSources()
	{
		sourcesLoaded = true;

		loadButton.setEnabled(false);
		refreshButton.setEnabled(false);
		refreshButton.setToolTipText("Can't refresh while sources are loaded.");

		final String oldStatus = PCGen_Frame1.getMessageAreaText();
		// TODO: I18N
		PCGen_Frame1.setMessageAreaTextWithoutSaving("Loading Sources...");

		return oldStatus;
	}

	/**
	 * Update the display to indicate that the sources have completed loading.
	 * @param statusMsg The status message that should be displayed.
	 */
	void showSourcesLoaded(final String statusMsg)
	{
		PCGen_Frame1.enableDisableMenuItems();
		PCGen_Frame1.setMessageAreaTextWithoutSaving(statusMsg);
		PCGen_Frame1.restoreFilterSettings(null);
	}

	private void rememberSourceChanges()
	{
		List<URI> campaignStrings = new ArrayList<URI>(selectedCampaigns.size());

		for (Campaign aCamp : selectedCampaigns)
		{
			campaignStrings.add(aCamp.getSourceURI());
		}

		PersistenceManager.getInstance().setChosenCampaignSourcefiles(campaignStrings);
	}

	/**
	 * Reset the UI
	 */
	public void resetUI() {
		infoLabel.setBackground(bLeftPane.getBackground());
	}

	/**
	 * NOTE: this function adds all sources to the selected list in memory, but does NOT update the GUI itself
	 * @param node
	 * @param select
	 */
	private void selectAllLeaves(PObjectNode node, boolean select)
	{
		for (int count = 0; count < node.getChildCount(); ++count)
		{
			// if this child is a leaf, then select it...
			PObjectNode child = node.getChild(count);

			if (child.isLeaf() && child.getItem() instanceof Campaign)
			{
				Campaign aCamp = (Campaign) child.getItem();

				if (aCamp != null)
				{
					if (select)
					{
						if (!selectedCampaigns.contains(aCamp))
						{
							selectedCampaigns.add(aCamp);
						}
					}
					else
					{
						selectedCampaigns.remove(aCamp);
					}
				}
			}

			// ...otherwise recurse, using it as the new parent node
			else
			{
				selectAllLeaves(child, select);
			}
		}
	}

	private void selectAll_actionPerformed(boolean select)
	{
		if (select)
		{
			selectAllLeaves((PObjectNode) availableTable.getTree().getModel().getRoot(), true);
		}
		else
		{
			selectedCampaigns.clear();
			unloadAllCampaigns_actionPerformed();
		}

		updateModels();

		//Remember what we just did...
		rememberSourceChanges();
	}

	/**
	 *  Pass this a Collection of campaign file names. These will be selected in the
	 *  table.
	 *
	 * @param  campaigns  A Collection of campaign file names.
	 * @since
	 */
	private void selectCampaignsByURI(Collection<URI> campaigns)
	{
		for (URI element : campaigns)
		{
			final Campaign aCampaign = Globals.getCampaignByURI(element);

			if (aCampaign != null)
			{
				if (!selectedCampaigns.contains(aCampaign))
				{
					selectedCampaigns.add(aCampaign);
					updateModels();
				}
			}
		}
	}

	//this method will now unload all current sources, but will not remove them from the selected table
	private void unloadAllCampaigns_actionPerformed()
	{
		PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);

		if (Logging.isDebugMode()) //don't force PC closure if we're in debug mode, per request
		{
			ShowMessageDelegate.showMessageDialog("PC's are not closed in debug mode.  " + "Please be aware that they may not function correctly "
			+ "until campaign data is loaded again.",
				Constants.s_APPNAME, MessageType.WARNING);
		}
		else
		{
			parent.closeAllPCs();

			if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
			{
				ShowMessageDelegate.showMessageDialog("Can't unload campaigns until all PC's are closed.", Constants.s_APPNAME,
					MessageType.INFORMATION);

				return;
			}
			PCGen_Frame1.setCharacterPane(null);
		}

		Globals.emptyLists();
		PersistenceManager.getInstance().emptyLists();
		PersistenceManager.getInstance().setChosenCampaignSourcefiles(new ArrayList<URI>());

		for (Campaign aCamp : Globals.getCampaignList())
		{
			aCamp.setIsLoaded(false);
		}

		parent.enableLstEditors(false);

		sourcesLoaded = false;

		refreshButton.setEnabled(true);
		refreshButton.setToolTipText("Refresh the list of sources");

		PCGen_Frame1.enableDisableMenuItems();
		updateModels();
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List<String> pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	private void updateModels()
	{
		updateAvailableModel();
		updateSelectedModel();

		setLoadedColMaxWidth();

		/* Toggle the Load, et al, buttons */
		if (selectedTable.getTree().getRowCount() == 0)
		{
			loadButton.setEnabled(false);
			unloadAllButton.setEnabled(false);
			removeAllButton.setEnabled(false);
		}
		else
		{
			loadButton.setEnabled(!sourcesLoaded);
			unloadAllButton.setEnabled(true);
			removeAllButton.setEnabled(true);
		}
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		List<String> pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();

		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.availableListMode", viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();

		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.selectedListMode", viewSelectMode);
			updateSelectedModel();
		}
	}
	
	/**
	 * The basic idea of the TreeTableModel is that there is a single
	 * <code>root</code> object. This root object has a null
	 * <code>parent</code>.  All other objects have a parent which
	 * points to a non-null object.  parent objects contain a list of
	 * <code>children</code>, which are all the objects that point
	 * to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs.
	 * Leafs are like files and non-leafs are like directories.
	 **/
	final class CampaignModel extends AbstractTreeTableModel
	{
		final String[] availNameList = { "Source Material" };
		final String[] selNameList = { "Source Material", "Loaded" };

		// Types of the columns.
		int modelType = MODEL_AVAIL;

		/**
		 * Creates a CampaignModel
		 * @param mode
		 * @param available
		 */
		public CampaignModel(int mode, boolean available)
		{
			super(null);

			if (!available)
			{
				modelType = MODEL_SELECT;
			}
			resetModel(mode, available, true);
		}

		// true for first column so that it highlights
		@Override
		public boolean isCellEditable(Object node, int column)
		{
			return (column == 0);
		}

		/**
		 * Returns Campaign for the column.
		 * @param column
		 * @return Class
		 */
		@Override
		public Class<?> getColumnClass(int column)
		{
			switch (column)
			{
				case COL_LOADED: //is source loaded?
					return String.class;

				case COL_NAME: //source material name
					return TreeTableModel.class;

				default:
					Logging.errorPrint("In MainSource.CampaignModel.getColumnClass the column " + column
						+ " is not handled.");

					break;
			}

			return String.class;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 * @return column count
		 */
		public int getColumnCount()
		{
			if (modelType == MODEL_AVAIL)
			{
				return availNameList.length;
			}
			return selNameList.length;
		}

		/**
		 * Returns String name of a column.
		 * @param column
		 * @return column name
		 */
		public String getColumnName(int column)
		{
			if (modelType == MODEL_AVAIL)
			{
				return availNameList[column];
			}
			return selNameList[column];
		}

		/**
		 * There must be a root object, but we keep it "hidden"
		 * @param aNode
		 */
		public void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		@Override
		public final Object getRoot()
		{
			return super.getRoot();
		}

		/**
		 * Returns Object value of the column.
		 * @param node
		 * @param column
		 * @return value
		 */
		public Object getValueAt(Object node, int column)
		{
			try {
				final PObjectNode fn = (PObjectNode) node;
				Campaign aCamp = null;

				if ((fn != null) && (fn.getItem() instanceof Campaign))
				{
					aCamp = (Campaign) fn.getItem();
				}

				switch (column)
				{
					case COL_NAME: // Name

						if (fn != null)
						{
							return fn.toString();
						}

						Logging.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return "";

					case COL_LOADED: //is source loaded?

						if (aCamp != null)
						{
							if (aCamp.isLoaded())
							{
								return "Y";
							}
							return "N";
						}

						break;

					case -1:

						if (fn != null)
						{
							return fn.getItem();
						}

						Logging.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return null;

					default:
						Logging.errorPrint("In MainSource.CampaignModel.getValueAt the column " + column
							+ " is not handled.");

						break;
				}
			} catch(Exception e) {
				// TODO Handle this?
			}

			return null;
		}

		/**
		 * This assumes the CampaignModel exists but needs to be repopulated
		 * @param mode
		 * @param available
		 * @param newCall
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
		    final List<String> allowedModes = Globals.getAllowedGameModes();
		    PCGen_Frame1 mainFrame = PCGen_Frame1.getInst();
		    PlayerCharacter aPC = null;
		    if (mainFrame != null)
		    {
			aPC = mainFrame.getCurrentPC();
		    }

		    List<Campaign> campList;

		    if (available)
		    {
			campList = Globals.getCampaignList();
		    }
		    else
		    {
			campList = selectedCampaigns;
		    }

		    switch (mode)
		    {
			case VIEW_PUBFMTSET: // by Publisher/Format/Setting/Product Name
			case VIEW_PUBSET:
			case VIEW_PUBLISH:
			    /*
			     * The following algorithm by cpmeister aka The Dragon Monkey
			     */
			    PObjectNode root = new PObjectNode();
			    Map<String, PObjectNode> nodeMap = new TreeMap<String, PObjectNode>();
			    for (Campaign camp : campList)
			    {
				if (camp.isGameMode(allowedModes) && shouldDisplayThis(camp, aPC) && (available ^ selectedCampaigns.contains(camp)))
				{
				    int typeCount = camp.getMyTypeCount();
				    if (typeCount > 0)
				    {
					String pub = camp.getMyType(0);
					if (!nodeMap.containsKey(pub))
					{
					    PObjectNode node = new PObjectNode(pub);
					    nodeMap.put(pub, node);
					}
					if (mode == VIEW_PUBLISH)
					{
					    PObjectNode node = new PObjectNode(camp);
					    nodeMap.get(pub).addChild(node);
					}
					else if (typeCount > 1)
					{
					    String fmt = camp.getMyType(1);
					    String pubfmt = pub + "." + fmt;
					    if (mode == VIEW_PUBFMTSET && !nodeMap.containsKey(pubfmt))
					    {
						PObjectNode node = new PObjectNode(fmt);
						nodeMap.put(pubfmt, node);
						nodeMap.get(pub).addChild(node);
					    }
					    if (typeCount > 2)
					    {
						String set = camp.getMyType(2);
						if (mode == VIEW_PUBFMTSET)
						{
						    String pubfmtset = pubfmt + "." + set;
						    if (!nodeMap.containsKey(pubfmtset))
						    {
							PObjectNode node = new PObjectNode(set);
							nodeMap.put(pubfmtset, node);
							nodeMap.get(pubfmt).addChild(node);
						    }
						    nodeMap.get(pubfmtset).addChild(new PObjectNode(camp));
						}
						else
						{
						    String pubset = pub + "." + set;
						    if (!nodeMap.containsKey(pubset))
						    {
							PObjectNode node = new PObjectNode(set);
							nodeMap.put(pubset, node);
							nodeMap.get(pub).addChild(node);
						    }
						    nodeMap.get(pubset).addChild(new PObjectNode(camp));
						}
					    }
					    else if (mode == VIEW_PUBFMTSET)
					    {
						nodeMap.get(pubfmt).addChild(new PObjectNode(camp));
					    }
					    else
					    {
						nodeMap.get(pub).addChild(new PObjectNode(camp));
					    }
					}
					else
					{
					    nodeMap.get(pub).addChild(new PObjectNode(camp));
					}
				    }
				    else
				    {
					String key = "Other";
					if(!nodeMap.containsKey(key))
					{
					    nodeMap.put(key, new PObjectNode(key));
					}
					nodeMap.get(key).addChild(new PObjectNode(camp));
				    }
				}
			    }
			    for (String key : nodeMap.keySet())
			    {
				if (key.indexOf('.') == -1)
				{
				    root.addChild(nodeMap.get(key));
				}
			    }
			    setRoot(root);
			    break;

			case VIEW_PRODUCT: // by Product Name
			    setRoot(new PObjectNode()); // just need a blank one
			    String qFilter = this.getQFilter();

			    for (Campaign aCamp : campList)
			    {
				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

				// filter out campaigns here
				if (!shouldDisplayThis(aCamp, aPC) || !aCamp.isGameMode(allowedModes))
				{
				    continue;
				}

				//don't display selected campaigns in the available table
				if (available && selectedCampaigns.contains(aCamp))
				{
				    continue;
				}

				if (qFilter == null ||
					(aCamp.getKeyName().toLowerCase().indexOf(qFilter) >= 0 ||
					aCamp.getType().toLowerCase().indexOf(qFilter) >= 0))
				{
				    PObjectNode aFN = new PObjectNode();
				    aFN.setParent(rootAsPObjectNode);
				    aFN.setItem(aCamp);
				    PrereqHandler.passesAll(aCamp.getPreReqList(), aPC, aCamp);
				    rootAsPObjectNode.addChild(aFN);
				}
			    }

			    break;

			default:
			    Logging.errorPrint("In MainSource.CampaignlModel.resetModel the mode " + mode + " is not handled.");

			    break;
		    }

		    PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

		    if (rootAsPObjectNode.getChildCount() > 0)
		    {
			fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
		    }
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 * @param aCamp
		 * @param aPC
		 * @return true or false
		 */
		private boolean shouldDisplayThis(final Campaign aCamp, final PlayerCharacter aPC)
		{
			if (aCamp.getDisplayName().length() == 0)
			{
				return false;
			}

			return ((modelType == MODEL_SELECT) || accept(aPC, aCamp));
		}
	}

	private class CampaignPopupListener extends MouseAdapter
	{
		private CampaignPopupMenu menu;
		private JTree tree;

		CampaignPopupListener(JTreeTable treeTable, CampaignPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;

			KeyListener myKeyListener = new KeyListener()
				{
					public void keyTyped(KeyEvent e)
					{
						dispatchEvent(e);
					}

					public void keyPressed(KeyEvent e)
					{
						final int keyCode = e.getKeyCode();

						if (keyCode != KeyEvent.VK_UNDEFINED)
						{
							final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);

							for (int i = 0; i < menu.getComponentCount(); ++i)
							{
								final JMenuItem menuItem = (JMenuItem) menu.getComponent(i);
								KeyStroke ks = menuItem.getAccelerator();

								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									menuItem.doClick(2);

									return;
								}
							}
						}

						dispatchEvent(e);
					}

					public void keyReleased(KeyEvent e)
					{
						dispatchEvent(e);
					}
				};

			treeTable.addKeyListener(myKeyListener);
		}

		@Override
		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		@Override
		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());

				if (selRow == -1)
				{
					return;
				}

				selPath = tree.getPathForLocation(evt.getX(), evt.getY());

				if (selPath == null)
				{
					return;
				}

				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private class CampaignPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -2654080650560664447L;

		CampaignPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				CampaignPopupMenu.this.add(createAddMenuItem("Select", "shortcut EQUALS"));
				CampaignPopupMenu.this.add(createAddAllMenuItem("Select All", "alt A"));
				CampaignPopupMenu.this.add(createWebMenuItem("Product Website...", "alt W", true));
				CampaignPopupMenu.this.add(createHelpFileMenuItem("Product Help...", "alt H", true));
			}

			else // selectedTable
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				CampaignPopupMenu.this.add(createRemoveMenuItem("Remove", "shortcut MINUS"));
				CampaignPopupMenu.this.add(createRemoveAllMenuItem("Remove All", "alt A"));
				CampaignPopupMenu.this.add(createWebMenuItem("Product Website...", "alt W", false));
				CampaignPopupMenu.this.add(createHelpFileMenuItem("Product Help...", "alt H", false));
			}
		}

		private JMenuItem createAddAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddAllCampaignActionListener(), "selectall", (char) 0,
				accelerator, "Select All Source material to load", "Add16.gif", true);
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddCampaignActionListener(), "select", (char) 0, accelerator,
				"Select Source material to load", "Add16.gif", true);
		}

		private JMenuItem createRemoveAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveAllCampaignActionListener(), "deselectall", (char) 0,
				accelerator, "Remove All Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveCampaignActionListener(), "deselect", (char) 0, accelerator,
				"Remove Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createHelpFileMenuItem(String label, String accelerator, boolean fromAvail)
		{
			return Utility.createMenuItem(label, new HelpfileActionListener(fromAvail), "helpfile", (char) 0, accelerator, "Launch browser to product's helpfile", "Bookmarks16.gif", true);
		}

		private JMenuItem createWebMenuItem(String label, String accelerator, boolean fromAvail)
		{
			return Utility.createMenuItem(label, new WebActionListener(fromAvail), "website", (char) 0, accelerator,
				"Launch browser to product's website", "Bookmarks16.gif", true);
		}

		private class AddAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(true);
			}
		}

		private class AddCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(true);
			}
		}

		private class RemoveAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(false);
			}
		}

		private class RemoveCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(false);
			}
		}

		private class HelpfileActionListener implements ActionListener
		{
			boolean available = true;

			HelpfileActionListener(boolean fromAvail)
			{
				available = fromAvail;
			}

			public void actionPerformed(ActionEvent evt)
			{
				launchProductWebsite(available, false);
			}
		}

		private class WebActionListener implements ActionListener
		{
			boolean available = true;

			WebActionListener(boolean fromAvail)
			{
				available = fromAvail;
			}

			public void actionPerformed(ActionEvent evt)
			{
				launchProductWebsite(available, true);
			}
		}

	}
}
