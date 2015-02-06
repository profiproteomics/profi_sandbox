package fr.proline.studio.filter;


import fr.proline.studio.table.DecoratedTableModel;
import java.util.ArrayList;
import java.util.HashSet;


/**
 *
 * @author JM235353
 */
public abstract class FilterTableModel extends DecoratedTableModel implements FilterTableModelInterface {

    protected Filter[] m_filters = null;
    protected HashSet<Integer> m_restrainIds = null;
    protected ArrayList<Integer> m_filteredIds = null;
    
    
    public FilterTableModel() {
    }
    
    @Override
    public Filter[] getFilters() {
       
        initFilters();
        
        int nbFilter = m_filters.length;
        int nbNull = 0;
        for (int i=0;i<nbFilter;i++) {
            if (m_filters[i] == null) {
                nbNull++;
            }
        }
        
        if (nbNull == 0) {
            return m_filters;
        }
        
        Filter[] filters = new Filter[nbFilter-nbNull];
        int j=0;
        for (int i=0;i<nbFilter;i++) {
            if (m_filters[i] != null) {
                filters[j++] = m_filters[i];
            }
        }
        
        return filters;
    }

    @Override
    public Filter getColumnFilter(int col) {
    
        initFilters();
        return m_filters[col];
        
    }

    @Override
    public boolean filter(int row) {
        int nbCol = getColumnCount();
        for (int i=0;i<nbCol;i++) {
            if (!filter(row, i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int convertRowToOriginalModel(int row) {
        if (m_filteredIds == null) {
            return row;
        }
        return m_filteredIds.get(row);
    }
    
    @Override
    public void restrain(HashSet<Integer> restrainRowSet) {
        m_restrainIds = restrainRowSet;
        filter();
    }
    
    @Override
    public HashSet<Integer> getRestrainRowSet() {
        return m_restrainIds;
    }
    
    public boolean hasRestrain() {
        return (m_restrainIds != null) && (!m_restrainIds.isEmpty());
    }
    
}
