/*
 * InfoCharacterSheet.java
 * Copyright 2007 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.EquipSet;
import pcgen.gui.CharacterInfoTab;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.io.ExportHandler;
import pcgen.util.PropertyFactory;
import pcgen.util.enumeration.Tab;

public class InfoCharacterSheet extends BaseCharacterInfoTab 
{
	private ExportHandler theHandler = null;
	private DocumentBuilderImpl theDocBuilder = null;
	private HtmlRendererContext theRendererContext = null;
	private HtmlPanel theSheetPanel = null;
	private SelectPanel theSelectPanel = null;
	
	public InfoCharacterSheet(final PlayerCharacter aPC)
	{
		super(aPC);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
			}
		});
	}

	@Override
	protected Tab getTab() 
	{
		return Tab.CHARACTERSHEET;
	}

	@Override
	public int getTabOrder() 
	{
		return 0;
	}

	@Override
	public void setTabOrder(int anOrder) 
	{
		// This doesn't seem to be used anyway
		
	}

	@Override
	public List<String> getToDos() 
	{
		return Collections.emptyList();
	}

	@Override
	public void setPc(final PlayerCharacter aPC)
	{
		super.setPc(aPC);
		if (theSelectPanel != null)
		{
			try
			{
				theSelectPanel.setPc(getPc());
			}
			catch (Exception e)
			{
				// Yuck but not sure what else to do.
			}
		}
	}
	
	@Override
	protected void updateCharacterInfo() 
	{
		if (theHandler == null)
		{
			return;
		}

		if (theSelectPanel != null)
		{
			theSelectPanel.refresh();
		}
		
		final StringWriter out = new StringWriter();
		final BufferedWriter buf = new BufferedWriter(out);
		theHandler.write(getPc(), buf);
		ByteArrayInputStream instream = new ByteArrayInputStream(out.toString().getBytes());
		try 
		{
			final Document doc = theDocBuilder.parse(instream);
			theSheetPanel.setDocument(doc, theRendererContext);
		} 
		catch (SAXException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initializeFilters() 
	{
		// We don't support filters for this tab
	}

	@Override
	public void refreshFiltering() 
	{
		// We don't support filters for this tab
	}

	private void initComponents()
	{
		setLayout(new BorderLayout());
		final JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BorderLayout());

		theRendererContext = new SimpleHtmlRendererContext(theSheetPanel);

		theDocBuilder = new DocumentBuilderImpl(theRendererContext.getUserAgentContext(), theRendererContext);

		theSheetPanel = new HtmlPanel();
		
		containerPanel.add(new CharacterSheetSelectionPanel(), BorderLayout.NORTH);
		containerPanel.add(theSheetPanel, BorderLayout.CENTER);

		theSelectPanel = new SelectPanel(this);
		final FlippingSplitPane splitPane = new FlippingSplitPane(JSplitPane.HORIZONTAL_SPLIT, containerPanel, theSelectPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		add(splitPane, BorderLayout.CENTER);

		theSheetPanel.setPreferredWidth(this.getWidth());
	}
	
	class CharacterSheetSelectionPanel extends JPanel implements ActionListener
	{
		private JComboBox theCombo;
		
		CharacterSheetSelectionPanel()
		{
			initComponents();
		}
		
		private void initComponents()
		{
			JLabel label = new JLabel(PropertyFactory.getString("in_character_sheet"));
			add(label);
			theCombo = new JComboBox();
			final File sheetDir = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "d20" + File.separator + "fantasy" + File.separator + "htmlxml");
			final File[] fileList = sheetDir.listFiles();

			theCombo.addItem("..\\..\\..\\Empty.html");
			for (int i = 0; i < fileList.length; ++i)
			{
				if (!fileList[i].isDirectory() && !fileList[i].isHidden())
				{
					theCombo.addItem(fileList[i].getName());
				}
			}

			theCombo.addActionListener(this);
			add(theCombo);
			
			final String csheet = SettingsHandler.getPCGenOption("InfoCharacterSheet." + SettingsHandler.getGame().getName() + ".CurrentSheet", "..\\..\\..\\Empty.html");
			final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "d20" + File.separator + "fantasy" + File.separator + "htmlxml"
					+ File.separator + csheet);
			theCombo.setSelectedItem(template.getName());
			theHandler = new ExportHandler(template);
		}

		public void actionPerformed(final ActionEvent e) 
		{
			final JComboBox combo = (JComboBox)e.getSource();
			final String csheet = (String)combo.getSelectedItem();
			SettingsHandler.setPCGenOption("InfoCharacterSheet." + SettingsHandler.getGame().getName() + ".CurrentSheet", csheet);
			final File template = new File(SettingsHandler.getPcgenOutputSheetDir() + File.separator + "d20" + File.separator + "fantasy" + File.separator + "htmlxml"
					+ File.separator + csheet);
			theHandler = new ExportHandler(template);
			updateCharacterInfo();
		}
	}
}

class SelectPanel extends JPanel implements ActionListener
{
	private JPanel eqSetPanel;
	private ButtonGroup eqSets;
	private JPanel modifiersPanel;
//	private FlippingSplitPane splitPane1;
	private PlayerCharacter pc;
	private int serial = 0;
	private Map<String, Component> tempBonusWidgets =
			new HashMap<String, Component>();
	private Map<String, JRadioButton> eqSetWidgets =
			new HashMap<String, JRadioButton>();
	private CharacterInfoTab theParent;

	/** Creates new form SelectPanel
	 * @param parent
	 */
	public SelectPanel(final CharacterInfoTab aParent)
	{
		theParent = aParent;
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		eqSets = new ButtonGroup();
		final FlippingSplitPane splitPane1 = new FlippingSplitPane();
		eqSetPanel = new JPanel();
		modifiersPanel = new JPanel();

		setLayout(new BorderLayout());

		splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane1.setResizeWeight(0.5);
		eqSetPanel.setLayout(new BoxLayout(eqSetPanel, BoxLayout.Y_AXIS));

		splitPane1.setLeftComponent(eqSetPanel);

		modifiersPanel
			.setLayout(new BoxLayout(modifiersPanel, BoxLayout.Y_AXIS));

		splitPane1.setRightComponent(modifiersPanel);

		add(splitPane1, BorderLayout.CENTER);

	}


	/**
	 * <code>setPc</code> updates the currently displayed character.
	 * Should typically be called when the user switches the PC tab.
	 *
	 * @param pc the new <code>PlayerCharacter</code> object
	 * @throws Exception
	 */
	public void setPc(final PlayerCharacter aPC) throws Exception
	{
		if (this.pc != aPC)
		{
			this.pc = aPC;
			serial = 0;
			removeAllBoxes();
			updateSelections();
		}
	}

	/**
	 * <code>refresh</code> updates all filters and eqsets. Should typically
	 * be called whenever there is a chance that the user has added/removed
	 * temporary bonuses or equipment sets.
	 *
	 */
	public void refresh()
	{
		if (pc == null)
		{
			return;
		}
		if (serial < pc.getSerial())
		{
			updateSelections();
			serial = pc.getSerial();
		}
	}

	/**
	 * <code>actionPerformed</code> is an event callback used
	 * whenever the user changes the selected equipment set.
	 * The <code>ActionEvent</code> object contains the information
	 * about which equipment set has been selected.
	 *
	 * @param e an <code>ActionEvent</code> object
	 */

	public void actionPerformed(ActionEvent e)
	{
		pc.setCalcEquipSetId(e.getActionCommand());
		theParent.refresh();
	}

	/**
	 *  <code>removeAllBoxes</code> clears out the GUI panel's
	 *
	 */
	private void removeAllBoxes()
	{
		tempBonusWidgets.clear();
		modifiersPanel.removeAll();
		eqSetPanel.removeAll();
		for (JRadioButton button : eqSetWidgets.values())
		{
			eqSets.remove(button);
		}
		eqSetWidgets.clear();
	}

	/**
	 * transforms a <code>List</code> of temporary bonuses(<code>BonusObj</code>)
	 * to a set containing the names of those bonus.
	 *
	 * @param tempBonuses a <code>List</code> of temporary bonuses
	 * @return a <code>Set</code> of bonus names(<code>String</code>'s)
	 */
	private Set<String> tempBonus2Set(List<BonusObj> tempBonuses)
	{
		final Set<String> ret = new TreeSet<String>();
		for (BonusObj bonus : tempBonuses)
		{
			ret.add(bonus.getName());
		}
		return ret;
	}

	/**
	 * <code>equipSet2Set</code> transforms a list of equipment set items
	 * to a set containing the names of all "root" equipment sets.
	 *
	 * @param eqSetList a <code>List</code> of Equipment set items
	 * @return a <code>Set</code> of equipment set names
	 */
	private Set<String> equipSet2Set(List<EquipSet> eqSetList)
	{
		final Set<String> ret = new TreeSet<String>();
		for (EquipSet e : eqSetList)
		{
			if (e.getRootIdPath().equals(e.getIdPath()))
			{
				ret.add(e.getIdPath());
			}
		}
		return ret;
	}

	private void updateSelections()
	{
		/* Use set intersection/join's to figure out what updates
		 have been done */

		/* First, find which temporary bonuses have been removed,
		 and which have been added */
		Set<String> newValues = tempBonus2Set(pc.getTempBonusList());
		Set<String> oldValues = new TreeSet<String>(tempBonusWidgets.keySet());
		oldValues.removeAll(newValues);
		newValues.removeAll(tempBonusWidgets.keySet());

		if (!newValues.isEmpty())
		{
			addTempBonus(newValues);
		}
		if (!oldValues.isEmpty())
		{
			removeTempBonus(oldValues);
		}

		/* Now, same for equipment sets. */
		newValues.clear();
		oldValues.clear();

		newValues.addAll(equipSet2Set(pc.getEquipSet()));
		oldValues.addAll(eqSetWidgets.keySet());

		oldValues.removeAll(newValues);
		newValues.removeAll(eqSetWidgets.keySet());
		if (!newValues.isEmpty())
		{
			addEquipSets(newValues);
		}
		if (!oldValues.isEmpty())
		{
			removeEquipSets(oldValues);
		}
	}

	private void addEquipSets(Set<String> eqSetIds)
	{
		/* just a temporary map to be able to lookup
		 the name of an equipment set, given its ID */
		final Map<String, String> setId2Name = new HashMap<String, String>();

		for (EquipSet eset : pc.getEquipSet())
		{
			setId2Name.put(eset.getIdPath(), eset.getName());
		}

		/* Create the buttons for the equipment sets. Note that we
		 keep an internal reference to the buttons in a map, so that
		 we can later remove them given an equipment set ID */
		for (String eqid : eqSetIds)
		{
			String setName = setId2Name.get(eqid);
			JRadioButton button = new JRadioButton(setName);
			button.setActionCommand(eqid);
			button.addActionListener(this);
			eqSets.add(button);
			eqSetPanel.add(button);
			eqSetWidgets.put(eqid, button);
		}
	}

	private void removeEquipSets(Set<String> eqSetIds)
	{
		for (String key : eqSetIds)
		{
			JRadioButton w = eqSetWidgets.remove(key);
			eqSets.remove(w);
			eqSetPanel.remove(w);
		}
	}

	private static class CheckBoxUpdater implements ItemListener
	{
		private String bonus;
		private PlayerCharacter playerCharacter;
		private CharacterInfoTab theParent;

		/**
		 * Constructor
		 * @param bonus
		 * @param aPc
		 * @param parent
		 */
		public CheckBoxUpdater(final String aBonus, PlayerCharacter aPc,
			CharacterInfoTab aParent)
		{
			this.theParent = aParent;
			this.bonus = aBonus;
			playerCharacter = aPc;
		}

		public void itemStateChanged(ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.DESELECTED)
			{
				playerCharacter.setTempBonusFilter(bonus);
			}
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				playerCharacter.unsetTempBonusFilter(bonus);
			}
			theParent.refresh();
		}
	}

	private void addTempBonus(final Set<String> names)
	{
		for (String name : names)
		{
			JCheckBox aBox =
					new JCheckBox(name, !pc.getTempBonusFilters()
						.contains(name));
			aBox.addItemListener(new CheckBoxUpdater(name, pc, theParent));
			modifiersPanel.add(aBox);
			tempBonusWidgets.put(name, aBox);
		}
	}

	private void removeTempBonus(final Set<String> names)
	{
		for (String name : names)
		{
			Component w = tempBonusWidgets.get(name);
			if (w != null)
			{
				modifiersPanel.remove(w);
				tempBonusWidgets.remove(name);
			}
		}
	}
}
