/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created on Feb 16, 2008, 11:44:12 PM
 */
package pcgen.gui.util.treeview;

import pcgen.gui.util.event.TreeViewModelListener;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Connor Petty
 */
public interface TreeViewModel<E>
{

    void addTreeViewModelListener(TreeViewModelListener<E> listener);

    void removeTreeViewModelListener(TreeViewModelListener<?> listener);

    List<? extends TreeView<E>> getTreeViews();

    int getDefaultTreeViewIndex();

    DataView<E> getDataView();

    Collection<E> getData();

}
