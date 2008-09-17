/*
 * BasicGeneratorSelectionModel.java
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
 * Created on Aug 26, 2008, 2:45:25 PM
 */
package pcgen.gui.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import pcgen.gui.facade.CharacterFacade;
import pcgen.gui.facade.InfoFacade;
import pcgen.gui.filter.FilterableTreeViewModel;
import pcgen.gui.tools.FilteredTreeViewSelectionPanel;
import pcgen.gui.tools.ResourceManager;
import pcgen.gui.tools.SelectionDialog;
import pcgen.gui.tools.SelectionModel;
import pcgen.gui.util.DefaultGenericListModel;
import pcgen.gui.util.GenericListModel;
import pcgen.gui.util.JTreeViewSelectionPane.SelectionType;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class BasicGeneratorSelectionModel<E extends InfoFacade> implements SelectionModel<FacadeGenerator<E>>
{

    private static final Properties props = new Properties();

    static
    {
        props.setProperty(SelectionModel.AVAILABLE_TEXT_PROP, "availGen");
        props.setProperty(SelectionModel.SELECTION_TEXT_PROP, "selGen");
        props.setProperty(SelectionModel.NEW_TOOLTIP_PROP, "newGen");
        props.setProperty(SelectionModel.COPY_TOOLTIP_PROP, "copyGen");
        props.setProperty(SelectionModel.DELETE_TOOLTIP_PROP, "deleteGen");
        props.setProperty(SelectionModel.ADD_TOOLTIP_PROP, "addGen");
        props.setProperty(SelectionModel.REMOVE_TOOLTIP_PROP, "removeGen");
    }

    private final FilteredTreeViewSelectionPanel selectionPanel;
    private final AddAsAction addAsAction;

    public BasicGeneratorSelectionModel()
    {
        this.addAsAction = new AddAsAction();
        this.selectionPanel = new FilteredTreeViewSelectionPanel();

        selectionPanel.getSelectionModel().addListSelectionListener(addAsAction);
        selectionPanel.add(new JButton(addAsAction), BorderLayout.SOUTH);
        selectionPanel.addItemListener(addAsAction);
    //TODO: initialize the filters for selectionPanel
    }

    public CharacterFacade getCharacter()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FilterableTreeViewModel<E> getTreeViewModel()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericListModel<FacadeGenerator<E>> getAvailableList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public GenericListModel<FacadeGenerator<E>> getSelectedList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAvailableList(GenericListModel<FacadeGenerator<E>> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSelectedList(GenericListModel<FacadeGenerator<E>> list)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getItemPanel(SelectionDialog<FacadeGenerator<E>> selectionDialog,
                                   Component currentItemPanel,
                                   FacadeGenerator<E> selectedItem)
    {
        DefaultGenericListModel<FacadeGenerator<E>> model = selectionDialog.getAvailableModel();
        boolean mutable = isMutable(selectedItem);
        if (model.contains(selectedItem))
        {
            selectionPanel.setEditable(mutable);
            selectionPanel.setSelectionType(SelectionType.CHECKBOX);
            selectionPanel.setSelectedObjects(selectedItem.getAll());
        }
        else
        {
            selectionPanel.setEditable(false);
            selectionPanel.setSelectionType(SelectionType.RADIO);
            selectionPanel.setSelectedObjects(selectedItem.getAll());
        }
        addAsAction.setSelectionDialog(selectionDialog);
        if (mutable)
        {
            addAsAction.setGenerator((MutableFacadeGenerator<E>) selectedItem);
        }
        return selectionPanel;
    }

    public FacadeGenerator<E> createMutableItem(SelectionDialog<FacadeGenerator<E>> selectionDialog,
                                                 FacadeGenerator<E> templateItem)
    {
        return GeneratorFactory.createMutableFacadeGenerator(JOptionPane.showInputDialog(selectionDialog,
                                                                                         ResourceManager.getText("createGen")),
                                                             templateItem);

    }

    public boolean isMutable(FacadeGenerator<E> item)
    {
        return item instanceof MutableFacadeGenerator;
    }

    public boolean isAddable(FacadeGenerator<E> item)
    {
        return getCharacter().getDataSet().getSources().containsAll(item.getSources());
    }

    public Color getItemColor(FacadeGenerator<E> item)
    {
        if (!isAddable(item))
        {
            return Color.RED;
        }
        else if (isMutable(item))
        {
            return Color.BLUE;
        }
        else
        {
            return Color.BLACK;
        }
    }

    public Properties getDisplayProperties()
    {
        return props;
    }

    private class AddAsAction extends AbstractAction implements ListSelectionListener,
                                                                 ItemListener
    {

        private SelectionDialog<FacadeGenerator<E>> selectionDialog;
        private MutableFacadeGenerator<E> generator;

        public AddAsAction()
        {
            putValue(NAME, ResourceManager.getText("addGenAs"));
            setEnabled(false);
        }

        public void setSelectionDialog(SelectionDialog<FacadeGenerator<E>> selectionDialog)
        {
            this.selectionDialog = selectionDialog;
        }

        public void setGenerator(MutableFacadeGenerator<E> generator)
        {
            this.generator = generator;
        }

        @SuppressWarnings("unchecked")
        public void actionPerformed(ActionEvent e)
        {
            DefaultGenericListModel model = selectionDialog.getSelectedModel();
            for (Object data : selectionPanel.getSelectedData())
            {
                model.add(data);
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            setEnabled(!selectionPanel.getSelectedData().isEmpty());
        }

        public void itemStateChanged(ItemEvent e)
        {
            @SuppressWarnings("unchecked")
            E item = (E) e.getItem();
            if (e.getStateChange() == ItemEvent.SELECTED)
            {
                generator.add(item);
            }
            else
            {
                generator.remove(item);
            }
        }

    }
}
