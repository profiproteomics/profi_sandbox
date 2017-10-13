package fr.proline.mzscope.ui;

import fr.proline.mzscope.model.IRawFile;
import fr.proline.mzscope.ui.model.MapDataGroup;
import fr.proline.studio.table.DecoratedMarkerTable;
import fr.proline.studio.table.PropertiesTableModel;
import fr.proline.studio.table.TablePopupMenu;
import fr.proline.studio.utils.CyclicColorPalette;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelListener;

/**
 *
 * @author CB205360
 */
class PropertiesPanel extends JPanel {
    
    private List<IRawFile> rawfiles;
    private RawFilePropertiesTableModel model; 
    
    PropertiesPanel(List<IRawFile> rawfiles) {
        this.rawfiles = rawfiles;
        List<String> names = rawfiles.stream().map(f -> f.getName()).collect(Collectors.toList());
        model = new RawFilePropertiesTableModel(names);
        initComponents();
        initContent();
    }
    
    private void initComponents() {
        JScrollPane scroll = new JScrollPane();
        DecoratedMarkerTable table = new DecoratedMarkerTable() {
            @Override
            public TablePopupMenu initPopupMenu() {
                return new TablePopupMenu();
            }

            @Override
            public void prepostPopupMenu() {
            }

            @Override
            public void addTableModelListener(TableModelListener l) {
            }
        };

      table.setModel(model);
      
      scroll.setViewportView(table);
      table.setFillsViewportHeight(true);
      table.setViewport(scroll.getViewport());
      table.removeStriping();
      
      this.setLayout(new BorderLayout());
      this.add(scroll, BorderLayout.CENTER);
      
    }

    private void initContent() {
        List<Map<String, Object>> data = rawfiles.stream().map(f -> f.getFileProperties()).collect(Collectors.toList());
        model.addData(data, "Format", CyclicColorPalette.getColor(0));
    }
}

class RawFilePropertiesTableModel extends PropertiesTableModel {

    RawFilePropertiesTableModel(List<String> names) {
        m_datasetNameArray = new ArrayList<>(3);
        names.stream().forEach( n -> m_datasetNameArray.add(n));
        m_dataGroupList = new ArrayList<>();
        m_loaded = true;
    }

    public void addData(List<Map<String, Object>> data, String label, Color color) {
        m_dataGroupList.add(new MapDataGroup(data, color, label, getRowCount()));
        m_rowCount = -1;
        fireTableStructureChanged();
    }
}