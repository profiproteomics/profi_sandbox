package fr.proline.studio.comparedata;

/**
 *
 * @author JM235353
 */
public class LockedDataModel implements CompareDataInterface {
    
    private CompareDataInterface m_src;
    
    private final int m_rowCount;
    private final int m_columnCount;
    private final String[] m_columnIdentifiers;
    private final Class[] m_columnClasses;
    private final int[] m_keysColumns;
    private final String m_name;
    private final Object[][] m_data;
    
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
        for (int i=0;i<m_rowCount;i++) {
            for (int j=0;j<m_columnCount;j++) {
                m_data[i][j] = src.getDataValueAt(i, j);
            }
        }
    }

    public CompareDataInterface getSrcDataInterface() {
        return m_src;
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
}