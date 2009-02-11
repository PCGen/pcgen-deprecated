/*
 * StatGeneratorSelectionModel.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Sep 15, 2008, 3:21:57 PM
 */
package pcgen.gui.generator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.beans.Customizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import pcgen.gui.generator.GeneratorManager.GeneratorType;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.SelectionDialog;
import pcgen.gui.tools.SelectionModel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.GenericListModelWrapper;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class StatGeneratorSelectionModel implements SelectionModel<StatGenerationMethod>
{

	private static final String STANDARD_MODE = "dieRoll";
	private static final String PURCHASE_MODE = "purchase";
	private static final Properties props = new Properties();

	static
	{
		props.setProperty(SelectionModel.AVAILABLE_TEXT_PROP,
						  "availStatGen");
		props.setProperty(SelectionModel.SELECTION_TEXT_PROP, "selStatGen");
		props.setProperty(SelectionModel.NEW_TOOLTIP_PROP, "newStatGen");
		props.setProperty(SelectionModel.COPY_TOOLTIP_PROP, "copyStatGen");
		props.setProperty(SelectionModel.DELETE_TOOLTIP_PROP,
						  "deleteStatGen");
		props.setProperty(SelectionModel.ADD_TOOLTIP_PROP, "addStatGen");
		props.setProperty(SelectionModel.REMOVE_TOOLTIP_PROP,
						  "removeStatGen");
	}

	private final ModeSelectionPanel modePanel;
	private final Map<String, Customizer> panelMap;
	private GenericListModel<StatGenerationMethod> availableList;
	private GenericListModel<StatGenerationMethod> selectedList;
	private GeneratorManager manager;

	public StatGeneratorSelectionModel(GeneratorManager manager)
	{
		this.manager = manager;
		this.modePanel = new ModeSelectionPanel();
		this.panelMap = new HashMap<String, Customizer>();
		panelMap.put(STANDARD_MODE, new RollMethodPanel());
		panelMap.put(PURCHASE_MODE, new PurchaseMethodPanel());
	}

	public GenericListModel<StatGenerationMethod> getAvailableList()
	{
		return availableList;
	}

	public GenericListModel<StatGenerationMethod> getSelectedList()
	{
		return selectedList;
	}

	public void setAvailableList(GenericListModel<StatGenerationMethod> availableList)
	{
//		if (this.availableList != null)
//		{
//			GenericListModelWrapper<StatGenerationMethod> oldList = new GenericListModelWrapper<StatGenerationMethod>(this.availableList);
//			for (StatGenerationMethod generator : oldList)
//			{
//				GeneratorType<?> type;
//				if (generator instanceof RollMethod)
//				{
//					type = GeneratorType.STANDARDMODE;
//				}
//				else
//				{
//					type = GeneratorType.PURCHASEMODE;
//				}
//				manager.generatorMap.remove(type, null, generator.toString());
//			}
//			GenericListModelWrapper<StatGenerationMethod> newList = new GenericListModelWrapper<StatGenerationMethod>(availableList);
//			for (StatGenerationMethod generator : newList)
//			{
//				GeneratorType<?> type;
//				if (generator instanceof RollMethod)
//				{
//					type = GeneratorType.STANDARDMODE;
//				}
//				else
//				{
//					type = GeneratorType.PURCHASEMODE;
//				}
//				manager.generatorMap.put(type, null, generator.toString(),
//										 generator);
//			}
//		}
		this.availableList = availableList;
	}

	public void setSelectedList(GenericListModel<StatGenerationMethod> selectedList)
	{
		this.selectedList = selectedList;
	}

	@SuppressWarnings("unchecked")
	public Component getCustomizer(Component currentItemPanel,
									StatGenerationMethod selectedItem)
	{
		String mode = getMode(selectedItem);
		Customizer panel = panelMap.get(mode);
		panel.setObject(selectedItem);
		return (Component) panel;
	}

	public StatGenerationMethod createMutableItem(SelectionDialog<StatGenerationMethod> selectionDialog,
												   StatGenerationMethod templateItem)
	{
		StatGenerationMethod generator = null;
		String name;
		String mode;
		if (templateItem == null)
		{
			modePanel.resetGeneratorName();
			int ret = JOptionPane.showConfirmDialog(selectionDialog, modePanel,
													"Generator Details",
													JOptionPane.OK_CANCEL_OPTION);
			if (ret != JOptionPane.OK_OPTION)
			{
				return null;
			}
			name = modePanel.getGeneratorName();
			mode = modePanel.getMode();
		}
		else
		{
			name = JOptionPane.showInputDialog(selectionDialog,
											   ResourceManager.getText("createGen"),
											   "Select Name",
											   JOptionPane.QUESTION_MESSAGE);
			mode = getMode(templateItem);
		}
		if (name != null)
		{
			if (mode == STANDARD_MODE)
			{
			//generator = GeneratorManager.c
			}
//            if (mode == STANDARD_MODE)
//            {
//                generator = GeneratorManager.createMutableStandardModeGenerator(name,
//                                                                                (RollMethod) templateItem);
//            }
//            else
//            {
//                generator = GeneratorManager.createMutablePurchaseModeGenerator(name,
//                                                                                (PurchaseMethod) templateItem);
//            }
		}
		return generator;
	}

	private static String getMode(StatGenerationMethod generator)
	{
		return generator instanceof RollMethod ? STANDARD_MODE
				: generator instanceof PurchaseMethod ? PURCHASE_MODE
				: null;
	}

	public boolean isAddable(StatGenerationMethod item)
	{
		return true;
	}

	public Color getItemColor(StatGenerationMethod item)
	{
		if (isMutable(item))
		{
			return Color.BLUE;
		}
		else
		{
			return Color.BLACK;
		}
	}

	public boolean isCopyable(StatGenerationMethod item)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean isMutable(StatGenerationMethod item)
	{
		return item instanceof MutablePurchaseMethod ||
				item instanceof MutableRollMethod;
	}

	public Properties getDisplayProperties()
	{
		return props;
	}

	private static class ModeSelectionPanel extends JPanel implements AncestorListener
	{

		private final JTextField nameField;
		private final ButtonGroup group;

		public ModeSelectionPanel()
		{
			super(new GridBagLayout());
			this.nameField = new JTextField();
			this.group = new ButtonGroup();
			initComponents();
		}

		private void initComponents()
		{
			addAncestorListener(this);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			add(new JLabel(ResourceManager.getText("genName") + ":"),
				gridBagConstraints);

			gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			add(nameField, gridBagConstraints);

			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.setBorder(new TitledBorder(null,
											 ResourceManager.getText("genMode"),
											 TitledBorder.DEFAULT_JUSTIFICATION,
											 TitledBorder.DEFAULT_POSITION,
											 new Font("Tahoma",
													  Font.BOLD,
													  12)));
			initRadioButton(panel, STANDARD_MODE).setSelected(true);
			initRadioButton(panel, PURCHASE_MODE);
			gridBagConstraints.weightx = 1.0;
			add(panel, gridBagConstraints);
		}

		private JRadioButton initRadioButton(JPanel panel, String command)
		{
			JRadioButton button = new JRadioButton(ResourceManager.getText(command));
			button.setActionCommand(command);
			group.add(button);
			panel.add(button);
			return button;
		}

		public void resetGeneratorName()
		{
			nameField.setText(null);
		}

		public String getGeneratorName()
		{
			return nameField.getText();
		}

		public String getMode()
		{
			return group.getSelection().getActionCommand();
		}

		public void ancestorAdded(AncestorEvent event)
		{
			nameField.requestFocusInWindow();
		}

		public void ancestorRemoved(AncestorEvent event)
		{

		}

		public void ancestorMoved(AncestorEvent event)
		{

		}

	}
}
