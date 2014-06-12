package fr.proline.studio.rsmexplorer.gui.model;

import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.core.orm.msi.dto.DBioSequence;
import fr.proline.studio.filter.*;
import fr.proline.studio.progress.ProgressInterface;
import fr.proline.studio.utils.IconManager;
import java.io.File;
import java.util.ArrayList;

/**
 * Table Model for Proteins
 * @author JM235353
 */
public class ProteinTableModel extends FilterTableModel {

    public static final int COLTYPE_PROTEIN_ID = 0;
    public static final int COLTYPE_PROTEIN_NAME = 1;
    public static final int COLTYPE_PROTEIN_DESCRIPTION = 2;
    public static final int COLTYPE_SAMESET_SUBSET = 3;
    public static final int COLTYPE_PROTEIN_SCORE = 4;
    public static final int COLTYPE_PROTEIN_PEPTIDES_COUNT = 5;
    public static final int COLTYPE_PROTEIN_MASS = 6;
    private static final String[] m_columnNames = {"Id", "Protein", "Description", "Sameset / Subset", "Score", "Peptides", "Mass"};
     
    private DProteinMatch[] m_sameSetMatches = null;
    private DProteinMatch[] m_subSetMatches = null;
    private Long m_rsmId = null;
    private long m_typicalProteinMatchId;
    private ArrayList<Integer> m_filteredIds = null;
    private boolean m_isFiltering = false;
    private boolean m_filteringAsked = false;
    private ProgressInterface m_progressInterface = null;

    public ProteinTableModel(ProgressInterface progressInterface) {
        m_progressInterface = progressInterface;
    }

    public DProteinMatch getProteinMatch(int row) {

        int rowFiltered = row;
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            rowFiltered = m_filteredIds.get(row).intValue();
        }

        int sameSetNb = m_sameSetMatches.length;
        if (rowFiltered < sameSetNb) {
            return m_sameSetMatches[rowFiltered];
        }
        return m_subSetMatches[rowFiltered - sameSetNb];
    }

    @Override
    public int getColumnCount() {
        return m_columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return m_columnNames[col];
    }

    @Override
    public String getToolTipForHeader(int col) {

        if (col == COLTYPE_SAMESET_SUBSET) {
            String urlTypical = IconManager.getURLForIcon(IconManager.IconType.TYPICAL);
            String urlSameset = IconManager.getURLForIcon(IconManager.IconType.SAME_SET);
            String urlSubset = IconManager.getURLForIcon(IconManager.IconType.SUB_SET);
            return "<html><img src=\"" + urlTypical + "\">&nbsp;Typical Protein in Sameset<br><img src=\"" + urlSameset + "\">&nbsp;Sameset<br><img src=\"" + urlSubset + "\">&nbsp;Subset</html>";
        } else {
            return getColumnName(col);
        }

    }


    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case COLTYPE_PROTEIN_ID:
                return Long.class;
            case COLTYPE_PROTEIN_NAME:
            case COLTYPE_PROTEIN_DESCRIPTION:
                return String.class;
            case COLTYPE_PROTEIN_PEPTIDES_COUNT:
                return Integer.class;
            case COLTYPE_PROTEIN_MASS:
            case COLTYPE_PROTEIN_SCORE:
                return Float.class;
            case COLTYPE_SAMESET_SUBSET:
                return Sameset.class;
        }
        return null;
    }

    @Override
    public int getRowCount() {
        if (m_sameSetMatches == null) {
            return 0;
        }

        if ((!m_isFiltering) && (m_filteredIds != null)) {
            return m_filteredIds.size();
        }

        return m_sameSetMatches.length + m_subSetMatches.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        // Retrieve Protein Match
        DProteinMatch proteinMatch = getProteinMatch(row);

        switch (col) {
            case COLTYPE_PROTEIN_ID:
                return proteinMatch.getId();
            case COLTYPE_PROTEIN_NAME:
                return proteinMatch.getAccession();
            case COLTYPE_PROTEIN_DESCRIPTION:
                return proteinMatch.getDescription();
            case COLTYPE_SAMESET_SUBSET:
                if (proteinMatch.getId() == m_typicalProteinMatchId) {
                    return Sameset.getSameset(Sameset.TYPICAL_SAMESET);
                } else if (row < m_sameSetMatches.length) {
                    return Sameset.getSameset(Sameset.SAMESET);
                } else {
                    return Sameset.getSameset(Sameset.SUBSET);
                }
            case COLTYPE_PROTEIN_SCORE:
                Float score = Float.valueOf(proteinMatch.getPeptideSet(m_rsmId).getScore());
                return score;
            case COLTYPE_PROTEIN_PEPTIDES_COUNT:
                return proteinMatch.getPeptideSet(m_rsmId).getPeptideCount();
            case COLTYPE_PROTEIN_MASS:
                DBioSequence bioSequence = proteinMatch.getDBioSequence();
                if (bioSequence != null) {
                    return new Float(bioSequence.getMass());
                }
                return null;
        }
        return null; // should never happen
    }

    public void setData(long rsmId, long typicalProteinMatchId, DProteinMatch[] sameSetMatches, DProteinMatch[] subSetMatches) {
        m_rsmId = rsmId;
        m_typicalProteinMatchId = typicalProteinMatchId;
        m_sameSetMatches = sameSetMatches;
        m_subSetMatches = subSetMatches;

        if (m_filteringAsked) {
            m_filteringAsked = false;
            filter();
        } else {
            fireTableDataChanged();
        }


    }

    public int findRowToSelect(String searchedText) {

        //JPM.TODO ?

        int rowToBeSelected = 0;
        if ((searchedText != null) && (searchedText.length() > 0)) {

            String searchedTextUpper = searchedText.toUpperCase();

            for (int row = 0; row < getRowCount(); row++) {
                String proteinName = ((String) getValueAt(row, COLTYPE_PROTEIN_NAME)).toUpperCase();
                if (proteinName.indexOf(searchedTextUpper) != -1) {
                    rowToBeSelected = row;
                }
            }
        }

        return rowToBeSelected;
    }

    @Override
    public void filter() {

        if (m_sameSetMatches == null) {
            // filtering not possible for the moment
            m_filteringAsked = true;
            return;
        }

        m_isFiltering = true;
        try {

            int nbData = m_sameSetMatches.length + m_subSetMatches.length;
            if (m_filteredIds == null) {
                m_filteredIds = new ArrayList<>(nbData);
            } else {
                m_filteredIds.clear();
            }

            for (int i = 0; i < nbData; i++) {
                if (!filter(i)) {
                    continue;
                }
                m_filteredIds.add(Integer.valueOf(i));
            }

        } finally {
            m_isFiltering = false;
        }
        fireTableDataChanged();
    }

    @Override
    public boolean filter(int row, int col) {
        Filter filter = getColumnFilter(col);
        if ((filter == null) || (!filter.isUsed())) {
            return true;
        }

        Object data = getValueAt(row, col);
        if (data == null) {
            return true; // should not happen
        }

        switch (col) {
            case COLTYPE_PROTEIN_NAME: {
                return ((StringFilter) filter).filter((String) data);
            }
            case COLTYPE_PROTEIN_DESCRIPTION: {
                return ((StringFilter) filter).filter((String) data);
            }
            case COLTYPE_PROTEIN_SCORE:
            case COLTYPE_PROTEIN_MASS: {
                return ((DoubleFilter) filter).filter((Float) data);
            }
            case COLTYPE_PROTEIN_PEPTIDES_COUNT: {
                return ((IntegerFilter) filter).filter((Integer) data);
            }

        }

        return true; // should never happen
    }

    @Override
    public void initFilters() {
        if (m_filters == null) {
            int nbCol = getColumnCount();
            m_filters = new Filter[nbCol];
            m_filters[COLTYPE_PROTEIN_NAME] = null;
            m_filters[COLTYPE_PROTEIN_NAME] = new StringFilter(getColumnName(COLTYPE_PROTEIN_NAME));
            m_filters[COLTYPE_PROTEIN_DESCRIPTION] = new StringFilter(getColumnName(COLTYPE_PROTEIN_DESCRIPTION));
            m_filters[COLTYPE_SAMESET_SUBSET] = null; //JPM.TODO
            m_filters[COLTYPE_PROTEIN_SCORE] = new DoubleFilter(getColumnName(COLTYPE_PROTEIN_SCORE));
            m_filters[COLTYPE_PROTEIN_PEPTIDES_COUNT] = new IntegerFilter(getColumnName(COLTYPE_PROTEIN_PEPTIDES_COUNT));
            m_filters[COLTYPE_PROTEIN_MASS] = new DoubleFilter(getColumnName(COLTYPE_PROTEIN_MASS));
        }
    }

    @Override
    public int getLoadingPercentage() {
        return m_progressInterface.getLoadingPercentage();
    }

    @Override
    public boolean isLoaded() {
        return m_progressInterface.isLoaded();
    }
    
    
    public static class Sameset {
        
        private static final int TYPICAL_SAMESET  = 0;
        private static final int SAMESET  = 1;
        private static final int SUBSET  = 2;

        private int m_type;
        
        private static Sameset[] samesetArray = { new Sameset(TYPICAL_SAMESET), new Sameset(SAMESET), new Sameset(SUBSET) };
        
        private Sameset(int type) {
            m_type = type;
        }
        
        public static Sameset getSameset(int type) {
            return samesetArray[type];
        }
        
        public boolean isTypical() {
            return (m_type == TYPICAL_SAMESET);
        }
        
        public boolean isSameset() {
            return (m_type == SAMESET);
        }
    }
}
