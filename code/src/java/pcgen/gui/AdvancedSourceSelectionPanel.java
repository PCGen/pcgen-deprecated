/*
 * AdvancedSourceSelectionPanel.java
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Feb 21, 2009, 7:15:03 PM
 */
package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.facade.GameModeFacade;
import pcgen.gui.facade.SourceFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.tools.ChooserPane;
import pcgen.gui.tools.FilteredTreeViewSelectionPanel;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.JTreeViewSelectionPane.SelectionType;
import pcgen.gui.util.event.AbstractGenericListDataWrapper;
import pcgen.gui.util.treeview.DataView;
import pcgen.gui.util.treeview.DataViewColumn;
import pcgen.gui.util.treeview.TreeView;
import pcgen.gui.util.treeview.TreeViewPath;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class AdvancedSourceSelectionPanel extends ChooserPane
{

	private FilteredTreeViewSelectionPanel selectionPanel;
	private SourceTreeViewModel treeViewModel;
	private GameModeFacade gameMode;

	public AdvancedSourceSelectionPanel()
	{
		this.selectionPanel = new FilteredTreeViewSelectionPanel();
		this.treeViewModel = new SourceTreeViewModel();
		initComponents();
	}

	private void initComponents()
	{
		selectionPanel.restoreState(selectionPanel.createState(null,
															   treeViewModel));
		selectionPanel.setSelectionType(SelectionType.CHECKBOX);
		selectionPanel.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					setSelectedSource((SourceFacade) e.getItem());
				}
			}

		});
		setPrimaryChooserComponent(selectionPanel);
		JPanel panel = new JPanel();
		panel.add(new JLabel("GameModes"), BorderLayout.NORTH);
		JList gameModeList = new JList();
		final GenericListModel<GameModeFacade> gameModes = PCGenUIManager.getGameModes();
		gameModeList.setModel(gameModes);
		gameModeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gameModeList.addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					setSelectedGameMode(
							gameModes.getElementAt(e.getFirstIndex()));
				}
			}

		});
		gameModeList.setSelectedIndex(0);
		panel.add(gameModeList, BorderLayout.CENTER);
		setSecondaryChooserComponent(panel);
	}

	public GameModeFacade getSelectedGameMode()
	{
		return gameMode;
	}

	public List<SourceFacade> getSelectedSources()
	{
		List<SourceFacade> sources = new ArrayList<SourceFacade>();
		Object[] array = selectionPanel.getSelectedObjects();
		for (Object object : array)
		{
			sources.add((SourceFacade) object);
		}
		return sources;
	}

	private void setSelectedGameMode(GameModeFacade elementAt)
	{
		this.gameMode = elementAt;
		treeViewModel.setGameModel(elementAt);
	}

	private void setSelectedSource(SourceFacade source)
	{
		setInfoPaneText(source.getInfo());
	}

	private static class SourceTreeViewModel extends AbstractGenericListDataWrapper<SourceFacade>
			implements FilterableTreeViewModel<SourceFacade>,
					   DataView<SourceFacade>
	{

		private DefaultGenericListModel<SourceFacade> model;

		public SourceTreeViewModel()
		{
			this.model = new DefaultGenericListModel<SourceFacade>();
		}

		public Class<SourceFacade> getFilterClass()
		{
			return SourceFacade.class;
		}

		public List<? extends TreeView<SourceFacade>> getTreeViews()
		{
			return Arrays.asList(SourceTreeView.values());
		}

		public int getDefaultTreeViewIndex()
		{
			return 2;
		}

		public DataView<SourceFacade> getDataView()
		{
			return this;
		}

		public GenericListModel<SourceFacade> getDataModel()
		{
			return model;
		}

		public List<?> getData(SourceFacade obj)
		{
			return Collections.emptyList();
		}

		public List<? extends DataViewColumn> getDataColumns()
		{
			return Collections.emptyList();
		}

		public void setGameModel(GameModeFacade gameMode)
		{
			setModel(PCGenUIManager.getSources(gameMode));
		}

		@Override
		protected void addData(Collection<? extends SourceFacade> data)
		{
			model.addAll(data);
		}

		@Override
		protected void removeData(Collection<? extends SourceFacade> data)
		{
			model.removeAll(data);
		}

		private static enum SourceTreeView implements
				TreeView<SourceFacade>
		{

			NAME("Name"),
			PUBLISHER_NAME("Publisher/Name"),
			PUBLISHER_SETTING_NAME("Publisher/Setting/Name"),
			PUBLISHER_FORMAT_SETTING_NAME("Publisher/Format/Setting/Name");
			private String name;

			private SourceTreeView(String name)
			{
				this.name = name;
			}

			public String getViewName()
			{
				return name;
			}

			public List<TreeViewPath<SourceFacade>> getPaths(SourceFacade pobj)
			{
				switch (this)
				{
					case NAME:
						return Collections.singletonList(new TreeViewPath<SourceFacade>(
								pobj));
					case PUBLISHER_NAME:
						return Collections.singletonList(new TreeViewPath<SourceFacade>(
								pobj, pobj.getPublisher()));
					case PUBLISHER_SETTING_NAME:
						return Collections.singletonList(new TreeViewPath<SourceFacade>(
								pobj, pobj.getPublisher(), pobj.getSetting()));
					case PUBLISHER_FORMAT_SETTING_NAME:
						return Collections.singletonList(new TreeViewPath<SourceFacade>(
								pobj, pobj.getPublisher(), pobj.getFormat(),
								pobj.getSetting()));
					default:
						throw new InternalError();
				}
			}

		}
	}
}
