package fr.proline.studio.rsmexplorer.gui.renderer;

import fr.proline.studio.utils.DataFormat;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer to display a float as a percentage
 * @author JM235353
 */
public class PercentageRenderer implements TableCellRenderer {
    
    private TableCellRenderer m_defaultRenderer;
    
    public PercentageRenderer(TableCellRenderer defaultRenderer) {
        m_defaultRenderer = defaultRenderer;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        Float f = (Float) value;
        String formatedValue = ((f == null) || (f.isNaN())) ? "" : DataFormat.format(f.floatValue(), 2)+" %";

        

        return m_defaultRenderer.getTableCellRendererComponent(table, formatedValue, isSelected, hasFocus, row, column);

    }
}

