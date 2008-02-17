/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * Created on Feb 16, 2008, 11:44:12 PM
 */

package pcgen.gui.util.treeview;

import java.util.EnumSet;

/**
 *
 * @author Connor Petty
 */
public interface TreeViewModel<E>{
    EnumSet<? extends TreeView<E>> getTreeViews();
    DataView<E> getDataView();
}
