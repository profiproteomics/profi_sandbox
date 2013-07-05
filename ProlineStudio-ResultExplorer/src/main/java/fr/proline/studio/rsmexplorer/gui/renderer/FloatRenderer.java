package fr.proline.studio.rsmexplorer.gui.renderer;

import fr.proline.studio.utils.DataFormat;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer for a Float Value in a Table Cell which is displayed as 0.00
 * @author JM235353
 */
public class FloatRenderer implements TableCellRenderer {
    
    TableCellRenderer defaultRenderer;
    
    public FloatRenderer(TableCellRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Float f = (Float) value;
        String formatedValue = ((f == null) || (f.isNaN())) ? "" : DataFormat.format(f.floatValue(), 2);

        

        return defaultRenderer.getTableCellRendererComponent(table, formatedValue, isSelected, hasFocus, row, column);

    }
}
