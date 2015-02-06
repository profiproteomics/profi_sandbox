package fr.proline.studio.table;

import com.thierry.filtering.TableSelection;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.utils.RelativePainterHighlighter;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.AbstractLayoutPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Table which has a bi-color striping, an ability to select columns viewed and
 * the possibility to display an "histogram" on a column
 *
 * @author JM235353
 */
public abstract class DecoratedTable extends JXTable implements CrossSelectionInterface {

    private RelativePainterHighlighter.NumberRelativizer m_relativizer = null;
    
    private TablePopupMenu m_popupMenu;
    
    public DecoratedTable() {

        // allow user to hide/show columns
        setColumnControlVisible(true);

        // highlight one line of two
        addHighlighter(HighlighterFactory.createSimpleStriping());

        TableSelection.installCopyAction(this);
  
        TablePopupMenu popup = initPopupMenu();
        if (popup != null) {
            setTablePopup(popup);
        }
    }

    

    public void displayColumnAsPercentage(int column) {
        displayColumnAsPercentage(column, AbstractLayoutPainter.HorizontalAlignment.RIGHT);
    }
    public void displayColumnAsPercentage(int column, AbstractLayoutPainter.HorizontalAlignment alignment) {
        // Display of the Score Column as a percentage
        Color base = PaintUtils.setSaturation(Color.GREEN, .7f);
        MattePainter matte = new MattePainter(PaintUtils.setAlpha(base, 125));
        RelativePainterHighlighter highlighter = new RelativePainterHighlighter(matte);
        highlighter.setHorizontalAlignment(alignment);

        m_relativizer = new RelativePainterHighlighter.NumberRelativizer(column, 0, 100);
        highlighter.setRelativizer(m_relativizer);
        highlighter.setHighlightPredicate(new HighlightPredicate.ColumnHighlightPredicate(column));
        addHighlighter(highlighter);

    }
    
    public RelativePainterHighlighter.NumberRelativizer getRelativizer() {
        return m_relativizer;
    }
    
    public String getToolTipForHeader(int modelColumn) {
        return ((DecoratedTableModel) getModel()).getToolTipForHeader(modelColumn);
    }
    
    
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new CustomTooltipTableHeader(this);
    }

    @Override
    public void select(ArrayList<Integer> rows) {
        ListSelectionModel model = getSelectionModel();
        model.clearSelection();
        for (Integer row1 : rows) {
            int row = convertRowIndexToView(row1);
            model.addSelectionInterval(row, row); 
       }
       
        int minRow = model.getMinSelectionIndex();
        if (minRow != -1) {
            scrollRowToVisible(minRow);
        }
    }
    
    @Override
    public ArrayList<Integer> getSelection() {
        
        ArrayList<Integer> selectionList = new ArrayList<>();
        
        ListSelectionModel selectionModel = getSelectionModel();
        DecoratedTableModel model = (DecoratedTableModel) getModel();
        
        for (int i=0;i<model.getRowCount();i++) {
            int row = convertRowIndexToView(i);
            if (selectionModel.isSelectedIndex(row)) {
                selectionList.add(i);
            }
        }
        
        return selectionList;
    }
    
    private class CustomTooltipTableHeader extends JXTableHeader {
        
        private DecoratedTable m_table;
        
        public CustomTooltipTableHeader(DecoratedTable table) {
            super(table.getColumnModel());
            m_table = table;
        }
        
        @Override
        public String getToolTipText(MouseEvent e) {
            
            Point p = e.getPoint();
            int column = columnAtPoint(p);
            if (column != -1) {
                column = m_table.convertColumnIndexToModel(column);
                String tooltip = getToolTipForHeader(column);
                if (tooltip != null) {
                    return tooltip;
                }
            }

            return super.getToolTipText(e);
        }
    }
    
    private void setTablePopup(TablePopupMenu popupMenu) {
        if (m_popupMenu == null) {
            addMouseListener(new TablePopupMouseAdapter(this));
        }
                    
        popupMenu.preparePopup();
        m_popupMenu = popupMenu;
    }
    
    public TablePopupMenu getTablePopup() {
        return m_popupMenu;
    }
 
    // set as abstract
    public /*abstract*/ TablePopupMenu initPopupMenu() {
        return null;
    }
    
    // set as abstract
    public void prepostPopupMenu() {
        
    };
    
    
}
