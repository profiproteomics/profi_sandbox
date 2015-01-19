package fr.proline.studio.rsmexplorer.gui.xic;

import fr.proline.core.orm.msi.dto.DCluster;
import fr.proline.core.orm.msi.dto.DMasterQuantPeptide;
import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DQuantPeptide;
import fr.proline.core.orm.uds.dto.DQuantitationChannel;
import fr.proline.studio.dam.tasks.xic.DatabaseLoadXicMasterQuantTask;
import fr.proline.studio.export.ExportColumnTextInterface;
import fr.proline.studio.filter.Filter;
import fr.proline.studio.filter.StringDiffFilter;
import fr.proline.studio.filter.StringFilter;
import fr.proline.studio.table.ExportTableSelectionInterface;
import fr.proline.studio.utils.CyclicColorPalette;
import fr.proline.studio.table.LazyData;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.LazyTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author JM235353
 */
public class QuantPeptideTableModel extends LazyTableModel implements ExportTableSelectionInterface, ExportColumnTextInterface {

    public static final int COLTYPE_PEPTIDE_ID = 0;
    public static final int COLTYPE_PEPTIDE_NAME = 1;
    public static final int COLTYPE_PEPTIDE_CLUSTER = 2;
    public static final int LAST_STATIC_COLUMN = COLTYPE_PEPTIDE_CLUSTER;
    private static final String[] m_columnNames = {"Id", "Peptide Sequence", "Cluster"};
    private static final String[] m_toolTipColumns = {"MasterQuantPeptide Id", "Identified Peptide Sequence", "Cluster Number"};

    public static final int COLTYPE_SELECTION_LEVEL = 0;
    public static final int COLTYPE_ABUNDANCE = 1;
    public static final int COLTYPE_RAW_ABUNDANCE = 2;
    public static final int COLTYPE_PSM = 3;

    private static final String[] m_columnNamesQC = {"Sel. level", "Abundance", "Raw abundance", "Pep. match count"};
    private static final String[] m_toolTipQC = {"Selection level", "Abundance", "Raw abundance", "Peptides match count"};

    private List<DMasterQuantPeptide> m_quantPeptides = null;
    private DQuantitationChannel[] m_quantChannels = null;
    private int m_quantChannelNumber;

    private ArrayList<Integer> m_filteredIds = null;
    private boolean m_isFiltering = false;
    private boolean m_filteringAsked = false;

    public QuantPeptideTableModel(LazyTable table) {
        super(table);
    }

    @Override
    public int getColumnCount() {
        if (m_quantChannels == null) {
            return m_columnNames.length;
        } else {
            return m_columnNames.length + m_quantChannelNumber * m_columnNamesQC.length;
        }
    }

    @Override
    public String getColumnName(int col) {
        if (col <= LAST_STATIC_COLUMN) {
            return m_columnNames[col];
        } else if (m_quantChannels != null) {
            int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
            int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);

            StringBuilder sb = new StringBuilder();
            String rsmHtmlColor = CyclicColorPalette.getHTMLColor(nbQc);
            sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
            sb.append(m_columnNamesQC[id]);
            sb.append("<br/>");
            sb.append(m_quantChannels[nbQc].getResultFileName());
            /*sb.append("<br/>");
            sb.append(m_quantChannels[nbQc].getRawFileName());*/

            sb.append("</html>");
            return sb.toString();
        } else {
            return ""; // should not happen
        }
    }
    
    @Override
    public String getExportColumnName(int col) {
        if (col <= LAST_STATIC_COLUMN) {
            return m_columnNames[col];
        } else if (m_quantChannels != null) {
            int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
            int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
            
            StringBuilder sb = new StringBuilder();
            sb.append(m_columnNamesQC[id]);
            sb.append(" ");
            sb.append(m_quantChannels[nbQc].getResultFileName());
            
            return sb.toString();
        }else {
            return ""; // should not happen
        }
        
    }

    @Override
    public String getToolTipForHeader(int col) {
        if (col <= LAST_STATIC_COLUMN) {
            return m_toolTipColumns[col];
        } else if (m_quantChannels != null) {
            int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
            int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);

            StringBuilder sb = new StringBuilder();
            String rsmHtmlColor = CyclicColorPalette.getHTMLColor(nbQc);
            sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
            sb.append(m_toolTipQC[id]);
            sb.append("<br/>");
            sb.append(m_quantChannels[nbQc].getResultFileName());
            sb.append("<br/>");
            sb.append(m_quantChannels[nbQc].getRawFilePath());
            

            sb.append("</html>");
            return sb.toString();
        } else {
            return ""; // should not happen
        }
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == COLTYPE_PEPTIDE_ID) {
            return Long.class;
        }
        return LazyData.class;
    }

    @Override
    public int getSubTaskId(int col) {
        return DatabaseLoadXicMasterQuantTask.SUB_TASK_PEPTIDE_INSTANCE;
    }

    @Override
    public int getRowCount() {
        if (m_quantPeptides == null) {
            return 0;
        }
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            return m_filteredIds.size();
        }
        return m_quantPeptides.size();
    }

    /**
     * returns the tooltip to display for a given row and a given col
     * for the cluster returns the abundances list
     * @param row
     * @param col
     * @return 
     */
    public String getTootlTipValue(int row, int col) {
        if (m_quantPeptides == null || row <0) {
            return "";
        }
        int rowFiltered = row;
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            rowFiltered = m_filteredIds.get(row).intValue();
        }
        // Retrieve Quant Peptide
        DMasterQuantPeptide peptide = m_quantPeptides.get(rowFiltered);
        DCluster cluster = peptide.getCluster();
        if (col == COLTYPE_PEPTIDE_CLUSTER && cluster != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html>");
            sb.append("Cluster ");
            sb.append(cluster.getClusterId());
            sb.append("<br/>");
            if (cluster.getAbundances() != null) {
                sb.append("<table><tr> ");
                List<Float> abundances = cluster.getAbundances();
                for (int a = 0; a < m_quantChannels.length; a++) {
                    sb.append("<td>");
                    String rsmHtmlColor = CyclicColorPalette.getHTMLColor(a);
                    sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
                    sb.append("Abundance");
                    sb.append("<br/>");
                    sb.append(m_quantChannels[a].getResultFileName());
                    sb.append("<br/>");
                    sb.append(m_quantChannels[a].getRawFileName());
                    sb.append("</td>");
                }
                sb.append("</tr><tr> ");
                // we suppose that the abundances are in the "good" order
                for (Float abundance : abundances) {
                    sb.append("<td>");
                    sb.append(abundance.isNaN()?"":abundance);
                    sb.append("</td>");
                }
                sb.append("</tr></table>");
            }

            return sb.toString();
        } else if (cluster != null ) {
            int a = getAbundanceCol(col);
            if (a >= 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("<html>");
                sb.append("Cluster ");
                sb.append(cluster.getClusterId());
                sb.append("<br/>");
                if (cluster.getAbundances() != null) {
                    sb.append("<table><tr> ");
                    List<Float> abundances = cluster.getAbundances();
                    sb.append("<td>");
                    String rsmHtmlColor = CyclicColorPalette.getHTMLColor(a);
                    sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
                    sb.append("Abundance");
                    sb.append("<br/>");
                    sb.append(m_quantChannels[a].getResultFileName());
                    sb.append("<br/>");
                    sb.append(m_quantChannels[a].getRawFileName());
                    sb.append("</td>");
                    sb.append("</tr><tr> ");
                    // we suppose that the abundances are in the "good" order
                    sb.append("<td>");
                    sb.append(abundances.get(a).isNaN()?"":abundances.get(a));
                    sb.append("</td>");
                    sb.append("</tr></table>");
                }
                return sb.toString();
            }
        }
        return "";
    }
    
    /**
     * returns -1 if the col is not an Abundance Col, otherwise the id in the quantChannel tab
     * @param col
     * @return 
     */
    private int getAbundanceCol(int col) {
        if (m_quantChannels != null) {
            int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
            int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
            if (id == COLTYPE_ABUNDANCE) {
                return nbQc;
            }
            return -1;
        }
        return -1;
    }
    
    
    @Override
    public Object getValueAt(int row, int col) {

        int rowFiltered = row;
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            rowFiltered = m_filteredIds.get(row).intValue();
        }
        // Retrieve Quant Peptide
        DMasterQuantPeptide peptide = m_quantPeptides.get(rowFiltered);
        DPeptideInstance peptideInstance = peptide.getPeptideInstance();

        switch (col) {
            case COLTYPE_PEPTIDE_ID: {
                return peptide.getId() == -1 ? "" : peptide.getId();
            }
            case COLTYPE_PEPTIDE_NAME: {
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null ) {
                    lazyData.setData(null);
                    givePriorityTo(m_taskId, row, col);
                } else {
                    if (peptideInstance.getBestPeptideMatch() != null) {
                        lazyData.setData(peptideInstance.getBestPeptideMatch().getPeptide().getSequence());
                    }else {
                        lazyData.setData("");
                    }
                }
                return lazyData;

            }
            case COLTYPE_PEPTIDE_CLUSTER: {
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null ) {
                    lazyData.setData(null);
                    givePriorityTo(m_taskId, row, col);
                } else {
                    DCluster cluster = peptide.getCluster();
                    if (cluster == null) {
                        lazyData.setData("");
                    }else{
                        lazyData.setData(cluster.getClusterId());
                    }
                }
            }
            default: {
                // Quant Channel columns 
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null) {
                    lazyData.setData(null);
                    givePriorityTo(m_taskId, row, col);
                } else {

                    // retrieve quantPeptide for the quantChannelId
                    Map<Long, DQuantPeptide> quantPeptideByQchIds = peptide.getQuantPeptideByQchIds();
                    if (quantPeptideByQchIds == null) {
                        lazyData.setData("");
                    } else {
                        int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                        int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                        DQuantPeptide quantPeptide = quantPeptideByQchIds.get(m_quantChannels[nbQc].getId());
                        if (quantPeptide == null) {
                            lazyData.setData("");
                        } else {
                            switch (id) {
                                case COLTYPE_SELECTION_LEVEL:
                                    lazyData.setData(quantPeptide.getSelectionLevel());
                                    break;
                                case COLTYPE_ABUNDANCE:
                                    lazyData.setData(quantPeptide.getAbundance().isNaN() ? "" : quantPeptide.getAbundance());
                                    break;
                                case COLTYPE_RAW_ABUNDANCE:
                                    lazyData.setData(quantPeptide.getRawAbundance().isNaN() ? "" : quantPeptide.getRawAbundance());
                                    break;
                                case COLTYPE_PSM:
                                    lazyData.setData(quantPeptide.getPeptideMatchesCount());
                                    break;
                            }
                        }
                    }
                }
                return lazyData;
            }
        }
        //return null; // should never happen
    }

    public void setData(Long taskId, DQuantitationChannel[] quantChannels, List<DMasterQuantPeptide> peptides) {
        this.m_quantPeptides = peptides;
        this.m_quantChannels = quantChannels;
        this.m_quantChannelNumber = quantChannels.length;
        m_filteredIds = null;
        m_isFiltering = false;
        fireTableStructureChanged();

        m_taskId = taskId;

        if (m_filteringAsked) {
            m_filteringAsked = false;
            filter();
        } else {
            fireTableDataChanged();
        }

    }

    public void dataUpdated() {

        // no need to do an updateMinMax : scores are known at once
        if (m_filteredIds != null) {
            filter();
        } else {
            fireTableDataChanged();
        }
    }

    public DMasterQuantPeptide getPeptide(int i) {

        if (m_filteredIds != null) {
            i = m_filteredIds.get(i).intValue();
        }

        return m_quantPeptides.get(i);
    }

    public int findRow(long peptideId) {

        if (m_filteredIds != null) {
            int nb = m_filteredIds.size();
            for (int i = 0; i < nb; i++) {
                if (peptideId == m_quantPeptides.get(m_filteredIds.get(i)).getPeptideInstanceId()) {
                    return i;
                }
            }
            return -1;
        }

        int nb = m_quantPeptides.size();
        for (int i = 0; i < nb; i++) {
            if (peptideId == m_quantPeptides.get(i).getPeptideInstanceId()) {
                return i;
            }
        }
        return -1;

    }

    public void sortAccordingToModel(ArrayList<Long> peptideIds) {

        if (m_quantPeptides == null) {
            // data not loaded 
            return;
        }

        HashSet<Long> peptideIdMap = new HashSet<>(peptideIds.size());
        peptideIdMap.addAll(peptideIds);

        int nb = getRowCount();
        int iCur = 0;
        for (int iView = 0; iView < nb; iView++) {
            int iModel = m_table.convertRowIndexToModel(iView);
            // Retrieve Peptide
            DMasterQuantPeptide p = getPeptide(iModel);
            if (peptideIdMap.contains(p.getPeptideInstanceId())) {
                peptideIds.set(iCur++, p.getPeptideInstanceId());
            }
        }

        // need to refilter
        if (m_filteredIds != null) { // NEEDED ????
            filter();
        }
    }

    @Override
    public void filter() {

        if (m_quantPeptides == null) {
            // filtering not possible for the moment
            m_filteringAsked = true;
            return;
        }

        m_isFiltering = true;
        try {

            int nbData = m_quantPeptides.size();
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

        Object data = ((LazyData) getValueAt(row, col)).getData();
        if (data == null) {
            return true; // should not happen
        }

        switch (col) {
            case COLTYPE_PEPTIDE_NAME: {
                return ((StringDiffFilter) filter).filter((String) data);
            }
            case COLTYPE_PEPTIDE_CLUSTER: {
                return ((StringFilter) filter).filter((String) data);
            }
        }

        return true; // should never happen
    }

    @Override
    public void initFilters() {
        if (m_filters == null) {
            int nbCol = getColumnCount();
            m_filters = new Filter[nbCol];
            m_filters[COLTYPE_PEPTIDE_ID] = null;
            m_filters[COLTYPE_PEPTIDE_NAME] = new StringDiffFilter(getColumnName(COLTYPE_PEPTIDE_NAME));
            m_filters[COLTYPE_PEPTIDE_CLUSTER] = new StringFilter(getColumnName(COLTYPE_PEPTIDE_CLUSTER));
        }
    }

    @Override
    public int getLoadingPercentage() {
        return m_table.getLoadingPercentage();
    }

    @Override
    public boolean isLoaded() {
        return m_table.isLoaded();
    }

    public int getByQCCount() {
        return m_columnNamesQC.length;
    }

    public int getQCCount() {
        return m_quantChannels.length;
    }

    public int getColumStart(int index) {
        return m_columnNames.length + index * m_columnNamesQC.length;
    }

    public int getColumStop(int index) {
        return m_columnNames.length + (1 + index) * m_columnNamesQC.length - 1;
    }

    public String getQCName(int i) {

        StringBuilder sb = new StringBuilder();

        String rsmHtmlColor = CyclicColorPalette.getHTMLColor(i);
        sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
        sb.append(m_quantChannels[i].getResultFileName());
       /* sb.append("<br/>");
        sb.append(m_quantChannels[i].getRawFileName());*/
        sb.append("</html>");

        return sb.toString();
    }

    public String getByQCMColumnName(int index) {
        return m_columnNamesQC[index];
    }

    public int getQCNumber(int col) {
        return (col - m_columnNames.length) / m_columnNamesQC.length;
    }

    public int getTypeNumber(int col) {
        return (col - m_columnNames.length) % m_columnNamesQC.length;
    }

    public Long getResultSummaryId() {
        if ((m_quantPeptides == null) || (m_quantPeptides.size() == 0)) {
            return null;
        }

        return m_quantPeptides.get(0).getQuantResultSummaryId();
    }

    /**
     * by default the rawAbundance and selectionLevel are hidden return the list
     * of columns ids of these columns
     *
     * @return
     */
    public List<Integer> getDefaultColumnsToHide() {
        List<Integer> listIds = new ArrayList();
        if (m_quantChannels != null) {
            for (int i = m_quantChannels.length - 1; i >= 0; i--) {
                listIds.add(m_columnNames.length + COLTYPE_RAW_ABUNDANCE + (i * m_columnNamesQC.length));
                listIds.add(m_columnNames.length + COLTYPE_SELECTION_LEVEL + (i * m_columnNamesQC.length));
            }
        }
        return listIds;
    }

    @Override
    public HashSet exportSelection(int[] rows) {

        int nbRows = rows.length;
        HashSet selectedObjects = new HashSet();
        for (int i = 0; i < nbRows; i++) {

            int row = rows[i];
            int rowFiltered = row;
            if ((!m_isFiltering) && (m_filteredIds != null)) {
                rowFiltered = m_filteredIds.get(row).intValue();
            }

            // Retrieve Peptide
            DMasterQuantPeptide peptide = m_quantPeptides.get(rowFiltered);

            selectedObjects.add(peptide.getId());
        }
        return selectedObjects;
    }
}
