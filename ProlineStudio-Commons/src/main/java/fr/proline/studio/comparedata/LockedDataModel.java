package fr.proline.studio.comparedata;

import fr.proline.studio.graphics.BestGraphicsInterface;
import fr.proline.studio.graphics.PlotInformation;
import fr.proline.studio.graphics.PlotType;
import java.util.Map;

/**
 * Model which copy the data of the source model, so the data are frozen
 * (even if the source model is filtered for instance)
 * @author JM235353
 */
public class LockedDataModel implements CompareDataInterface, BestGraphicsInterface {
    
    private CompareDataInterface m_src;
    
    private final int m_rowCount;
    private final int m_columnCount;
    private final String[] m_columnIdentifiers;
    private final Class[] m_columnClasses;
    private final int[] m_keysColumns;
    private final int m_infoCol;
    private final String m_name;
    private final Object[][] m_data;
    
    private final long[] m_uniqueIDs;
    
    public LockedDataModel(CompareDataInterface src) {
        m_src = src;
        
        m_rowCount = src.getRowCount();
        m_columnCount = src.getColumnCount();
        m_columnIdentifiers = new String[m_columnCount];
        m_columnClasses = new Class[m_columnCount];
        for (int i=0;i<m_columnCount;i++) {
            m_columnIdentifiers[i] = src.getDataColumnIdentifier(i);
            m_columnClasses[i] = src.getDataColumnClass(i);
        }
        m_keysColumns = src.getKeysColumn();
        m_name = src.getName();
        m_data = new Object[m_rowCount][m_columnCount];
        m_uniqueIDs = new long[m_rowCount];
        for (int i=0;i<m_rowCount;i++) {
            m_uniqueIDs[i] = src.row2UniqueId(i);
            for (int j=0;j<m_columnCount;j++) {
                m_data[i][j] = src.getDataValueAt(i, j);
            }
        }
        m_infoCol= src.getInfoColumn();
        
        
        
    }

    public CompareDataInterface getSrcDataInterface() {
        return m_src;
    }
    
    @Override
    public int getInfoColumn() {
        return m_infoCol;
    }
    
    @Override
    public int getRowCount() {
        return m_rowCount;
    }

    @Override
    public int getColumnCount() {
        return m_columnCount;
    }

    @Override
    public String getDataColumnIdentifier(int columnIndex) {
        return m_columnIdentifiers[columnIndex];
    }

    @Override
    public Class getDataColumnClass(int columnIndex) {
        return m_columnClasses[columnIndex];
    }

    @Override
    public Object getDataValueAt(int rowIndex, int columnIndex) {
        return m_data[rowIndex][columnIndex];
    }

    @Override
    public int[] getKeysColumn() {
        return m_keysColumns;
    }

    @Override
    public void setName(String name) {
        // nothing to do, should not happen
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public PlotType getBestPlotType() {
        if (m_src instanceof BestGraphicsInterface) {
            return ((BestGraphicsInterface) m_src).getBestPlotType();
        }
        return null;
    }

    @Override
    public int getBestXAxisColIndex(PlotType plotType) {
        if (m_src instanceof BestGraphicsInterface) {
            return ((BestGraphicsInterface) m_src).getBestXAxisColIndex(plotType);
        }
        return -1;
    }

    @Override
    public int getBestYAxisColIndex(PlotType plotType) {
        if (m_src instanceof BestGraphicsInterface) {
            return ((BestGraphicsInterface) m_src).getBestYAxisColIndex(plotType);
        }
        return -1;
    }

    @Override
    public Map<String, Object> getExternalData() {
        return m_src.getExternalData();
    }
    
    @Override
    public PlotInformation getPlotInformation() {
        return m_src.getPlotInformation();
    }
    
    @Override
    public long row2UniqueId(int rowIndex) {
        return m_uniqueIDs[rowIndex];
    }
    
    @Override
    public int uniqueId2Row(long id) {
        int nb = m_uniqueIDs.length;
        for (int i=0;i<nb;i++) {
            if (id == m_uniqueIDs[i]) {
                return i;
            }
        }
        return -1; // should never happen
    }

}
