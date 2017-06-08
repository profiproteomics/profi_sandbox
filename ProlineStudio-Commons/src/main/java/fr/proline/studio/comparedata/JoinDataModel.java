package fr.proline.studio.comparedata;

import fr.proline.studio.export.ExportFontData;
import fr.proline.studio.filter.DoubleFilter;
import fr.proline.studio.filter.Filter;
import fr.proline.studio.graphics.PlotInformation;
import fr.proline.studio.graphics.PlotType;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.table.LazyData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.table.TableCellRenderer;

/**
 * Model to perform a join between two models (joined thanks to key columns)
 * @author JM235353
 */
public class JoinDataModel extends AbstractJoinDataModel {

    private final ArrayList<Integer> m_keysColumns1 = new ArrayList<>();
    private final ArrayList<Integer> m_keysColumns2 = new ArrayList<>();
    private final ArrayList<Integer> m_allColumns1 = new ArrayList<>();
    private final ArrayList<Integer> m_allColumns2 = new ArrayList<>();

    
    public JoinDataModel() {
    }
    
    @Override
    protected void setColumns() {

        // construct column array
        int nbColumn = m_data1.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            if ((i == m_selectedTable1Key1) || (i == m_selectedTable1Key2)) {
                continue;
            }
            m_allColumns1.add(i);
        }
        nbColumn = m_data2.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            if ((i == m_selectedTable2Key1) || (i == m_selectedTable2Key2)) {
                continue;
            }
            m_allColumns2.add(i);
        }
        
        // First Key column(s)
        Class c1 = m_data1.getDataColumnClass(m_selectedTable1Key1);
        if (c1.equals(Double.class) || c1.equals(Float.class)) {
            m_keysColumns1.add(m_selectedTable1Key1);   // For Double and Float keys, we show both values from Table 1 and Table 2
            m_keysColumns2.add(-1);
            m_keysColumns1.add(-1);
            m_keysColumns2.add(m_selectedTable2Key1);
        } else {
            m_keysColumns1.add(m_selectedTable1Key1);  // For Other keys (String, Integer) the key is the same, or absent in one table
            m_keysColumns2.add(m_selectedTable2Key1);
        }
        
        // Second Key column(s)
        if (m_selectedTable1Key2 != -1) {
            Class c2 = m_data2.getDataColumnClass(m_selectedTable1Key2);
            if (c2.equals(Double.class) || c2.equals(Float.class)) {
                m_keysColumns1.add(m_selectedTable1Key2);   // For Double and Float keys, we show both values from Table 1 and Table 2
                m_keysColumns2.add(-1);
                m_keysColumns1.add(-1);
                m_keysColumns2.add(m_selectedTable2Key2);
            } else {
                m_keysColumns1.add(m_selectedTable1Key2);  // For Other keys (String, Integer) the key is the same, or absent in one table
                m_keysColumns2.add(m_selectedTable2Key2);
            }
        }

    }
    


    @Override
    public int getColumnCount() {
        if (!joinPossible()) {
            return 0;
        }
        
        return m_keysColumns1.size()+m_allColumns1.size()+m_allColumns2.size() + (m_showSourceColumn ? 1 : 0);
    }

    @Override
    public Object getDataValueAt(int rowIndex, int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        Integer row1 = m_rowsInTable1.get(rowIndex);
        Integer row2 = m_rowsInTable2.get(rowIndex);
        
        
        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int colIndexCur = m_keysColumns1.get(columnIndex);
            if ((colIndexCur != -1) && (row1 != null)) {
                return m_data1.getDataValueAt(row1, colIndexCur);
            } else {
                colIndexCur = m_keysColumns2.get(columnIndex);
                 if ((colIndexCur !=-1) && (row2 != null)) {
                    return m_data2.getDataValueAt(row2, colIndexCur);
                } else {
                    return null;
                }
            }
        }
        
        columnIndex -= m_keysColumns1.size();
        
        // Source Column
        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                if ((row1 == null) && (row2 != null)) {
                    return m_data2.getName();
                } else if ((row1 != null) && (row2 == null)) {
                    return m_data1.getName();
                } else {
                    return m_data1.getName()+","+m_data2.getName();
                }
                
            }
            columnIndex--;
        }
        
        
        
        
        // Other columns
        if (columnIndex<m_allColumns1.size()) {
            if (row1 == null) {
                return null;
            }
            return m_data1.getDataValueAt(row1, m_allColumns1.get(columnIndex));
        }
        columnIndex-=m_allColumns1.size();
        if (columnIndex<m_allColumns2.size()) {
            if (row2 == null) {
                return null;
            }
            return m_data2.getDataValueAt(row2, m_allColumns2.get(columnIndex));
        }

        return null;
    }

    @Override
    public String getDataColumnIdentifier(int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int colIndexCur = m_keysColumns1.get(columnIndex);
            String col1 = null;
            String col2 = null;
            if (colIndexCur != -1) {
                col1 = m_data1.getDataColumnIdentifier(colIndexCur);
            }
            colIndexCur = m_keysColumns2.get(columnIndex);
             if (colIndexCur != -1) {
                col2 = m_data2.getDataColumnIdentifier(colIndexCur);
            }
            
            if (col1 == null) {
                return col2+" "+m_data2.getName();
            } else if (col2 == null) {
                return col1+" "+m_data1.getName();
            } else {
                if (col1.compareTo(col2) == 0) {
                    return col1;
                } else {
                    return col1 + "/" + col2;
                }
            }
        }
        
        columnIndex -= m_keysColumns1.size();

        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                return "Source";
            }
            
            columnIndex--;
        }
        
        

        if (columnIndex < m_allColumns1.size()) {
            return m_data1.getDataColumnIdentifier(m_allColumns1.get(columnIndex));
        }
        columnIndex -= m_allColumns1.size();
        if (columnIndex < m_allColumns2.size()) {
            return m_data2.getDataColumnIdentifier(m_allColumns2.get(columnIndex));
        }

        return null;
    }

    @Override
    public Class getDataColumnClass(int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int index1 = m_keysColumns1.get(columnIndex);
            if (index1 != -1) {
                return m_data1.getDataColumnClass(index1);
            }
            return m_data1.getDataColumnClass(m_keysColumns2.get(columnIndex));
        }
        
        columnIndex -= m_keysColumns1.size();

        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                return String.class;
            }
            columnIndex--;
        }
        
        

        if (columnIndex < m_allColumns1.size()) {
            return m_data1.getDataColumnClass(m_allColumns1.get(columnIndex));
        }
        columnIndex -= m_allColumns1.size();
        if (columnIndex < m_allColumns2.size()) {
            return m_data2.getDataColumnClass(m_allColumns2.get(columnIndex));
        }

        return null;
    }

    @Override
    public String getColumnName(int column) {
        return getDataColumnIdentifier(column);
    }
    
    @Override
    public int getInfoColumn() {
        return 0;
    }

    @Override
    public Map<String, Object> getExternalData() {
        return null;
    }

    @Override
    public PlotInformation getPlotInformation() {
        return null;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        Integer row1 = m_rowsInTable1.get(rowIndex);
        Integer row2 = m_rowsInTable2.get(rowIndex);
        
        
        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int colIndexCur = m_keysColumns1.get(columnIndex);
            if ((colIndexCur != -1) && (row1 != null)) {
                return m_data1.getValueAt(row1, colIndexCur);
            } else {
                colIndexCur = m_keysColumns2.get(columnIndex);
                if ((colIndexCur !=-1) && (row2 != null)) {
                    return m_data2.getValueAt(row2, colIndexCur);
                } else {
                    return null;
                }
            }
        }
        
        columnIndex -= m_keysColumns1.size();
        
        // Source Column
        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                if ((row1 == null) && (row2 != null)) {
                    return m_data2.getName();
                } else if ((row1 != null) && (row2 == null)) {
                    return m_data1.getName();
                } else {
                    return m_data1.getName()+","+m_data2.getName();
                }
                
            }
            columnIndex--;
        }
        
        
        
        
        // Other columns
        if (columnIndex<m_allColumns1.size()) {
            if (row1 == null) {
                return null;
            }
            return m_data1.getValueAt(row1, m_allColumns1.get(columnIndex));
        }
        columnIndex-=m_allColumns1.size();
        if (columnIndex<m_allColumns2.size()) {
            if (row2 == null) {
                return null;
            }
            return m_data2.getValueAt(row2, m_allColumns2.get(columnIndex));
        }

        
        return null;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int index1 = m_keysColumns1.get(columnIndex);
            if (index1 != -1) {
                return m_data1.getColumnClass(index1);
            }
            return m_data1.getColumnClass(m_keysColumns2.get(columnIndex));
        }
        
        columnIndex -= m_keysColumns1.size();

        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                return String.class;
            }
            
            columnIndex--;
        }
        
        

        if (columnIndex < m_allColumns1.size()) {
            return m_data1.getColumnClass(m_allColumns1.get(columnIndex));
        }
        columnIndex -= m_allColumns1.size();
        if (columnIndex < m_allColumns2.size()) {
            return m_data2.getColumnClass(m_allColumns2.get(columnIndex));
        }

        
        return null;
    }
    
    @Override
    public Long getTaskId() {
        return -1l; // not used
    }

    @Override
    public LazyData getLazyData(int row, int col) {
        return null; // not used
    }

    @Override
    public void givePriorityTo(Long taskId, int row, int col) {
        // not used
    }

    @Override
    public void sortingChanged(int col) {
        return; // not used
    }

    @Override
    public int getSubTaskId(int col) {
        return -1; // not used
    }

    @Override
    public String getToolTipForHeader(int col) {
        return getColumnName(col);
    }

    @Override
    public String getTootlTipValue(int row, int col) {
        return null; // not used
    }

    @Override
    public void addFilters(LinkedHashMap<Integer, Filter> filtersMap) {
        if (!joinPossible()) {
            return;
        }

        LinkedHashMap<Integer, Filter> filtersMap1 = new LinkedHashMap<>();
        m_data1.addFilters(filtersMap1);

        LinkedHashMap<Integer, Filter> filtersMap2 = new LinkedHashMap<>();
        m_data2.addFilters(filtersMap2);

        int nbColumns = getColumnCount();
        for (int i = 0; i < nbColumns; i++) {

            int col = i;

            if (i < m_keysColumns1.size()) {
                int colIndexCur = m_keysColumns1.get(col);
                if (colIndexCur != -1) {
                    Filter f = filtersMap2.get(colIndexCur);
                    if (f != null) {
                        filtersMap.put(i, f);
                        continue;
                    }
                }
                colIndexCur = m_keysColumns2.get(col);
                if (colIndexCur != -1) {
                    Filter f = filtersMap2.get(colIndexCur);
                    if (f != null) {
                        filtersMap.put(i, f);
                        continue;
                    }
                }
            }

            col -= m_keysColumns1.size();

            if (m_showSourceColumn) {
                if (col == 0) {
                    filtersMap.put(i, new DoubleFilter(getColumnName(i), null, i));
                    continue;
                }
                col--;
            }

            if (col < m_allColumns1.size()) {
                Filter f = filtersMap1.get(m_allColumns1.get(col));
                if (f != null) {
                    filtersMap.put(i, f);
                    
                }
                continue;
            }
            col -= m_allColumns1.size();
            if (col < m_allColumns2.size()) {
                Filter f = filtersMap2.get(m_allColumns2.get(col));
                if (f != null) {
                    filtersMap.put(i, f);
                }
                
            }

        }
    }



    @Override
    public PlotType getBestPlotType() {
         return null; //JPM.TODO
    }

    @Override
    public int getBestXAxisColIndex(PlotType plotType) {
         return -1; //JPM.TODO
    }

    @Override
    public int getBestYAxisColIndex(PlotType plotType) {
        return -1; //JPM.TODO
    }

    @Override
    public String getExportRowCell(int row, int col) {
        Object o = getValueAt(row, col);
        if (o == null) {
            return "";
        }
        return o.toString();
    }
    
    @Override
    public ArrayList<ExportFontData> getExportFonts(int row, int col) {
        return null;
    }

    @Override
    public String getExportColumnName(int col) {
        return getColumnName(col);
    }

    @Override
    public TableCellRenderer getRenderer(int rowIndex, int columnIndex) {
        if (!joinPossible()) {
            return null;
        }

        Integer row1 = m_rowsInTable1.get(rowIndex);
        Integer row2 = m_rowsInTable2.get(rowIndex);
        
        // Columns with keys
        if (columnIndex<m_keysColumns1.size()) {
            int index1 = m_keysColumns1.get(columnIndex);
            if (row1 == null) {
                return null;
            }
            if (index1 != -1) {
                return m_data1.getRenderer(row1, index1);
            }
            return m_data1.getRenderer(row1, m_keysColumns2.get(columnIndex));
        }
        
        columnIndex -= m_keysColumns1.size();

        if (m_showSourceColumn) {
            if (columnIndex == 0) {
                return null;
            }
            columnIndex--;
        }
                

        if (columnIndex < m_allColumns1.size()) {
            if (row1 == null) {
                return null;
            }
            return m_data1.getRenderer(row1, m_allColumns1.get(columnIndex));
        }
        columnIndex -= m_allColumns1.size();
        if (columnIndex < m_allColumns2.size()) {
            if (row2 == null) {
                return null;
            }
            return m_data2.getRenderer(row2, m_allColumns2.get(columnIndex));
        }

        return null;

    }

    @Override
    public GlobalTableModelInterface getFrozzenModel() {
        return this;
    }
    
    @Override
    public Object getColValue(Class c, int col) {
        return null; // could be enhanced
    }
    
    
}
