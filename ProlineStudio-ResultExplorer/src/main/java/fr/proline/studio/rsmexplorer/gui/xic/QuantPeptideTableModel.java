package fr.proline.studio.rsmexplorer.gui.xic;

import fr.proline.core.orm.msi.PeptideReadablePtmString;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.msi.dto.DCluster;
import fr.proline.core.orm.msi.dto.DMasterQuantPeptide;
import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DQuantPeptide;
import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.core.orm.uds.dto.DQuantitationChannel;
import fr.proline.studio.comparedata.ExtraDataType;
import fr.proline.studio.dam.tasks.xic.DatabaseLoadXicMasterQuantTask;
import fr.proline.studio.filter.DoubleFilter;
import fr.proline.studio.filter.Filter;
import fr.proline.studio.filter.IntegerFilter;
import fr.proline.studio.filter.StringDiffFilter;
import fr.proline.studio.filter.StringFilter;
import fr.proline.studio.graphics.PlotInformation;
import fr.proline.studio.graphics.PlotType;
import fr.proline.studio.table.renderer.BigFloatOrDoubleRenderer;
import fr.proline.studio.rsmexplorer.gui.renderer.CompareValueRenderer;
import fr.proline.studio.table.renderer.DefaultLeftAlignRenderer;
import fr.proline.studio.table.renderer.DefaultRightAlignRenderer;
import fr.proline.studio.rsmexplorer.gui.renderer.FloatRenderer;
import fr.proline.studio.rsmexplorer.gui.renderer.PeptideRenderer;
import fr.proline.studio.table.CompoundTableModel;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.utils.CyclicColorPalette;
import fr.proline.studio.table.LazyData;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.LazyTableModel;
import fr.proline.studio.table.TableDefaultRendererManager;
import fr.proline.studio.types.QuantitationType;
import fr.proline.studio.types.XicGroup;
import fr.proline.studio.types.XicMode;
import fr.proline.studio.utils.StringUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author JM235353
 */
public class QuantPeptideTableModel extends LazyTableModel implements GlobalTableModelInterface {

    public static final int COLTYPE_PEPTIDE_ID = 0;
    public static final int COLTYPE_PEPTIDE_NAME = 1;
    public static final int COLTYPE_PEPTIDE_PTM = 2;
    public static final int COLTYPE_PEPTIDE_PROTEINSET_COUNT = 3;
    public static final int COLTYPE_OVERVIEW = 4;
    public static final int COLTYPE_PEPTIDE_CLUSTER = 5;
    public static final int LAST_STATIC_COLUMN = COLTYPE_PEPTIDE_CLUSTER;
    private static final String[] m_columnNames = {"Id", "Peptide Sequence", "PTM", "Protein Set Count", "Overview", "Cluster"};
    private static final String[] m_toolTipColumns = {"MasterQuantPeptide Id", "Identified Peptide Sequence", "Post Translational Modifications", "Protein Set Count", "Overview", "Cluster Number"};

    public static final int COLTYPE_SELECTION_LEVEL = 0;
    public static final int COLTYPE_IDENT_PSM = 1;
    public static final int COLTYPE_PSM = 2;
    public static final int COLTYPE_RAW_ABUNDANCE = 3;
    public static final int COLTYPE_ABUNDANCE =4 ;
    
    private int m_overviewType = COLTYPE_ABUNDANCE;

    private static final String[] m_columnNamesQC = {"Sel. level", "Ident. Pep. match count",  "Pep. match count","Raw abundance", "Abundance" };
    private static final String[] m_toolTipQC = {"Selection level", "Identification peptides match count","Peptides match count",  "Raw abundance", "Abundance" };
    
    private static final String[] m_columnNamesQC_SC = {"Sel. level", "Ident. Pep. match count", "Basic SC",  "Specific SC", "Abundance" };
    private static final String[] m_toolTipQC_SC = {"Selection level", "Identification peptides match count", "Basic Spectral Count", "Specific Spectral Count",  "Abundance" };

    private List<DMasterQuantPeptide> m_quantPeptides = null;
    private DQuantitationChannel[] m_quantChannels = null;
    private int m_quantChannelNumber;


    private String m_modelName;
    
    private boolean m_isXICMode;

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
            if(m_isXICMode){
                sb.append(m_columnNamesQC[id]);
            }else{
                sb.append(m_columnNamesQC_SC[id]);
            }
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
            if(m_isXICMode){
                sb.append(m_columnNamesQC[id]);
            }else{
                sb.append(m_columnNamesQC_SC[id]);
            }
            sb.append(" ");
            sb.append(m_quantChannels[nbQc].getResultFileName());
            
            return sb.toString();
        }else {
            return ""; // should not happen
        }
        
    }
    
    @Override
    public String getExportRowCell(int row, int col) {
        
        /*int rowFiltered = row;
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            rowFiltered = m_filteredIds.get(row).intValue();
        }*/

        // Retrieve Quant Peptide
        DMasterQuantPeptide peptide = m_quantPeptides.get(row);
        DPeptideInstance peptideInstance = peptide.getPeptideInstance();

        switch (col) {
            case COLTYPE_PEPTIDE_ID: {
                return peptide.getId() == -1 ? "" : Long.toString(peptide.getId());
            }
            case COLTYPE_PEPTIDE_NAME: {
                if (peptideInstance == null ) {
                    return "";
                } else {
                    if (peptideInstance.getBestPeptideMatch() != null) {
                        return peptideInstance.getBestPeptideMatch().getPeptide().getSequence();
                    }else {
                        return "";
                    }
                }
            }
            case COLTYPE_PEPTIDE_PTM: {
                if (peptideInstance == null) {
                    return "";
                } else if (peptideInstance.getBestPeptideMatch() != null) {
                    boolean ptmStringLoadeed = peptideInstance.getBestPeptideMatch().getPeptide().getTransientData().isPeptideReadablePtmStringLoaded();
                    if (!ptmStringLoadeed) {
                        return null; 
                    }
                    String ptm = "";
                    PeptideReadablePtmString ptmString = peptideInstance.getBestPeptideMatch().getPeptide().getTransientData().getPeptideReadablePtmString();
                    if (ptmString != null) {
                        ptm = ptmString.getReadablePtmString();
                    }

                    return ptm;
                } else {
                    return "";
                }
            }
            
            case COLTYPE_PEPTIDE_PROTEINSET_COUNT: {
                if (peptideInstance == null) {
                    return "";
                } else {
                    return Integer.toString(peptideInstance.getValidatedProteinSetCount());
                }
            }
            case COLTYPE_OVERVIEW:
                return "";
                
            case COLTYPE_PEPTIDE_CLUSTER: {
                if (peptideInstance == null ) {
                    return "";
                } else {
                    DCluster cluster = peptide.getCluster();
                    if (cluster == null) {
                        return "";
                    }else{
                        return Integer.toString(cluster.getClusterId());
                    }
                }
            }
            default: {
                // Quant Channel columns 
                int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                if (peptideInstance == null) {
                    switch (id) {
                        case COLTYPE_SELECTION_LEVEL:
                            return Integer.toString(0);
                        case COLTYPE_ABUNDANCE:
                            return Float.toString(0);
                        case COLTYPE_RAW_ABUNDANCE:
                            Float.toString(0);
                        case COLTYPE_PSM:
                            return Integer.toString(0);
                        case COLTYPE_IDENT_PSM:
                            return Integer.toString(0);
                    }
                } else {

                    // retrieve quantPeptide for the quantChannelId
                    Map<Long, DQuantPeptide> quantPeptideByQchIds = peptide.getQuantPeptideByQchIds();
                    if (quantPeptideByQchIds == null) {
                        switch (id) {
                            case COLTYPE_SELECTION_LEVEL:
                                return Integer.toString(0);
                            case COLTYPE_ABUNDANCE:
                                return Float.toString(0);
                            case COLTYPE_RAW_ABUNDANCE:
                                Float.toString(0);
                            case COLTYPE_PSM:
                                return Integer.toString(0);
                            case COLTYPE_IDENT_PSM:
                                return Integer.toString(0);
                        }
                    } else {
                        
                        DQuantPeptide quantPeptide = quantPeptideByQchIds.get(m_quantChannels[nbQc].getId());
                        if (quantPeptide == null) {
                           switch (id) {
                                case COLTYPE_SELECTION_LEVEL : return Integer.toString(0);
                                case COLTYPE_ABUNDANCE : return Float.toString(0);
                                case COLTYPE_RAW_ABUNDANCE : Float.toString(0);
                                case COLTYPE_PSM : return Integer.toString(0);
                                case COLTYPE_IDENT_PSM : return Integer.toString(0);
                            }
                        } else {
                            switch (id) {
                                case COLTYPE_SELECTION_LEVEL : return (quantPeptide.getSelectionLevel() == null?Integer.toString(0):Integer.toString(quantPeptide.getSelectionLevel()));
                                case COLTYPE_ABUNDANCE : return ((quantPeptide.getAbundance() == null || quantPeptide.getAbundance().isNaN())? Float.toString(0):Float.toString(quantPeptide.getAbundance()));
                                case COLTYPE_RAW_ABUNDANCE : return ((quantPeptide.getRawAbundance() == null || quantPeptide.getRawAbundance().isNaN())? Float.toString(0):Float.toString(quantPeptide.getRawAbundance()));
                                case COLTYPE_PSM : return (quantPeptide.getPeptideMatchesCount()== null?Integer.toString(0):Integer.toString(quantPeptide.getPeptideMatchesCount()));
                                case COLTYPE_IDENT_PSM : return (quantPeptide.getIdentPeptideMatchCount()== null?Integer.toString(0):Integer.toString(quantPeptide.getIdentPeptideMatchCount()));
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getToolTipForHeader(int col) {
        if (col == COLTYPE_OVERVIEW) {
            return m_toolTipColumns[col]+" on "+(m_isXICMode ? m_toolTipQC[m_overviewType] : m_toolTipQC_SC[m_overviewType]) ;
        }
        if (col <= LAST_STATIC_COLUMN) {
            return m_toolTipColumns[col];
        } else if (m_quantChannels != null) {
            int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
            int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
            String rawFilePath = StringUtils.truncate(m_quantChannels[nbQc].getRawFilePath(), 50);

            StringBuilder sb = new StringBuilder();
            String rsmHtmlColor = CyclicColorPalette.getHTMLColor(nbQc);
            sb.append("<html><font color='").append(rsmHtmlColor).append("'>&#x25A0;&nbsp;</font>");
            if (m_isXICMode){
                sb.append(m_toolTipQC[id]);
            }else{
                sb.append(m_toolTipQC_SC[id]);
            }
            sb.append("<br/>");
            sb.append(m_quantChannels[nbQc].getResultFileName());
            sb.append("<br/>");
            sb.append(rawFilePath);
            

            sb.append("</html>");
            return sb.toString();
        } else {
            return ""; // should not happen
        }
    }

    @Override
    public Class getColumnClass(int col) {
        if (col == COLTYPE_PEPTIDE_ID) {
            return String.class;
        }else if (col == COLTYPE_OVERVIEW) {
            return CompareValueRenderer.CompareValue.class; 
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

        // Retrieve Quant Peptide
        DMasterQuantPeptide peptide = m_quantPeptides.get(row);
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
            sb.append("</html>");
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
                    sb.append("</td>");
                    sb.append("</tr><tr> ");
                    // we suppose that the abundances are in the "good" order
                    sb.append("<td>");
                    sb.append(abundances.get(a).isNaN()?"":abundances.get(a));
                    sb.append("</td>");
                    sb.append("</tr></table>");
                }
                sb.append("</html>");
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
    public Object getValueAt(final int row, int col) {

        /*int rowFiltered = row;
        if ((!m_isFiltering) && (m_filteredIds != null)) {
            rowFiltered = m_filteredIds.get(row).intValue();
        }*/
        // Retrieve Quant Peptide
        DMasterQuantPeptide peptide = m_quantPeptides.get(row);
        DPeptideInstance peptideInstance = peptide.getPeptideInstance();

        switch (col) {
            case COLTYPE_PEPTIDE_ID: {
                return peptide.getId() == -1 ? "" : Long.toString(peptide.getId());
            }
            case COLTYPE_PEPTIDE_NAME: {
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null ) {
                    lazyData.setData(null);
                    givePriorityTo(m_taskId, row, col);
                } else {
                    if (peptideInstance.getBestPeptideMatch() != null) {
                        lazyData.setData(peptideInstance.getBestPeptideMatch());
                    }else {
                        lazyData.setData("");
                    }
                }
                return lazyData;

            }
            
            case COLTYPE_PEPTIDE_PTM: {
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null) {
                   lazyData.setData(null);
                   givePriorityTo(m_taskId, row, col);
                } else if (peptideInstance.getBestPeptideMatch() != null) {
                    boolean ptmStringLoaded = peptideInstance.getBestPeptideMatch().getPeptide().getTransientData().isPeptideReadablePtmStringLoaded();
                    if (!ptmStringLoaded) {
                        return null; 
                    }
                    String ptm = "";
                    PeptideReadablePtmString ptmString = peptideInstance.getBestPeptideMatch().getPeptide().getTransientData().getPeptideReadablePtmString();
                    if (ptmString != null) {
                        ptm = ptmString.getReadablePtmString();
                    }

                    lazyData.setData(ptm);
                } else {
                    lazyData.setData("");
                }
                
                return lazyData;
            }
            
            case COLTYPE_PEPTIDE_PROTEINSET_COUNT: {
                LazyData lazyData = getLazyData(row, col);
                if (peptideInstance == null) {
                    lazyData.setData(null);
                    givePriorityTo(m_taskId, row, col);
                } else {
                    lazyData.setData(peptideInstance.getValidatedProteinSetCount());
                }
                return lazyData;
            }
            
            case COLTYPE_OVERVIEW:
                return new CompareValueRenderer.CompareValue() {

                    @Override
                    public int getNumberColumns() {
                        return m_quantChannels.length ;
                    }

                    @Override
                    public Color getColor(int col) {
                        return CyclicColorPalette.getColor(col);
                    }

                    @Override
                    public double getValue(int col) {
                        if (m_overviewType == -1) {
                            return 0; // should not happen
                        }
                       int realCol = LAST_STATIC_COLUMN+1 + m_overviewType+col*m_columnNamesQC.length;
                       LazyData lazyData = (LazyData)getValueAt(row, realCol);
                       if (lazyData != null && lazyData.getData() != null){
                            if (Number.class.isAssignableFrom(lazyData.getData().getClass())) {
                                return ((Number)lazyData.getData()).floatValue();
                            }
                       }
                       return 0;
                    }
                    
                    public double getValueNoNaN(int col) {
                        double val = getValue(col);
                        if (val != val) { // NaN value
                            return 0;
                        }
                        return val;
                    }

                    @Override
                    public double getMaximumValue() {
                        int nbCols = getNumberColumns();
                        double maxValue = 0;
                        for (int i=0;i<nbCols;i++) {
                            double v = getValue(i);
                            if (v>maxValue) {
                                maxValue = v;
                            }
                        }
                        return maxValue;
                        
                    }

                    @Override
                    public double calculateComparableValue() {
                        int nbColumns = getNumberColumns();
                        double mean = 0;
                        for (int i=0;i<nbColumns;i++) {
                            mean += getValueNoNaN(i);
                        }
                        mean /= nbColumns;
                        
                        double maxDiff = 0;
                        for (int i=0;i<nbColumns;i++) {
                            double diff = getValueNoNaN(i)-mean;
                            if (diff<0) {
                                diff = -diff;
                            }
                            if (diff>maxDiff) {
                                maxDiff = diff;
                            }
                        }
                        return maxDiff / mean;
                    }
                    
                    @Override
                    public int compareTo(CompareValueRenderer.CompareValue o) {
                        return Double.compare(calculateComparableValue(), o.calculateComparableValue());
                    }
                };
                
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
                        lazyData.setData(String.valueOf(cluster.getClusterId()));
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
                    int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                    int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                    Map<Long, DQuantPeptide> quantPeptideByQchIds = peptide.getQuantPeptideByQchIds();
                    if (quantPeptideByQchIds == null) {
                         switch (id) {
                                case COLTYPE_SELECTION_LEVEL:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                                case COLTYPE_ABUNDANCE:
                                    lazyData.setData(Float.valueOf(0));
                                    break;
                                case COLTYPE_RAW_ABUNDANCE:
                                    lazyData.setData(Float.valueOf(0));
                                    break;
                                case COLTYPE_PSM:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                                case COLTYPE_IDENT_PSM:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                            }
                    } else {
                        DQuantPeptide quantPeptide = quantPeptideByQchIds.get(m_quantChannels[nbQc].getId());
                        if (quantPeptide == null) {
                             switch (id) {
                                case COLTYPE_SELECTION_LEVEL:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                                case COLTYPE_ABUNDANCE:
                                    lazyData.setData(Float.valueOf(0));
                                    break;
                                case COLTYPE_RAW_ABUNDANCE:
                                    lazyData.setData(Float.valueOf(0));
                                    break;
                                case COLTYPE_PSM:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                                case COLTYPE_IDENT_PSM:
                                    lazyData.setData(Integer.valueOf(0));
                                    break;
                            }
                        } else {
                            switch (id) {
                                case COLTYPE_SELECTION_LEVEL:
                                    lazyData.setData(quantPeptide.getSelectionLevel());
                                    break;
                                case COLTYPE_ABUNDANCE:
                                    lazyData.setData((quantPeptide.getAbundance() == null || quantPeptide.getAbundance().isNaN()) ? Float.valueOf(0) : quantPeptide.getAbundance());
                                    break;
                                case COLTYPE_RAW_ABUNDANCE:
                                    lazyData.setData((quantPeptide.getRawAbundance() == null || quantPeptide.getRawAbundance().isNaN()) ? Float.valueOf(0) : quantPeptide.getRawAbundance());
                                    break;
                                case COLTYPE_PSM:
                                    lazyData.setData(quantPeptide.getPeptideMatchesCount() == null ? Integer.valueOf(0) : quantPeptide.getPeptideMatchesCount());
                                    break;
                                case COLTYPE_IDENT_PSM:
                                    lazyData.setData(quantPeptide.getIdentPeptideMatchCount() == null ? Integer.valueOf(0) : quantPeptide.getIdentPeptideMatchCount());
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

    public void setData(Long taskId, DQuantitationChannel[] quantChannels, List<DMasterQuantPeptide> peptides, boolean isXICMode) {
        boolean structureChanged = true;
        m_isXICMode = isXICMode;
        if (this.m_quantChannels !=null && m_quantChannels.length == quantChannels.length ) {
            for (int i=0; i<m_quantChannels.length; i++) {
                structureChanged = !(m_quantChannels[i].equals(quantChannels[i]));
            }
        }
        m_quantPeptides = peptides;
        m_quantChannels = quantChannels;
        m_quantChannelNumber = quantChannels.length;

        if (structureChanged) {
            fireTableStructureChanged();
        }

        m_taskId = taskId;

        fireTableDataChanged();

    }

    public void dataUpdated() {

        fireTableDataChanged();

    }

    public DMasterQuantPeptide getPeptide(int i) {

        return m_quantPeptides.get(i);
    }

    public int findRow(long peptideId) {

        int nb = m_quantPeptides.size();
        for (int i = 0; i < nb; i++) {
            if (peptideId == m_quantPeptides.get(i).getPeptideInstanceId()) {
                return i;
            }
        }
        return -1;

    }

    public void sortAccordingToModel(ArrayList<Long> peptideIds, CompoundTableModel compoundTableModel) {

        if (m_quantPeptides == null) {
            // data not loaded 
            return;
        }

        HashSet<Long> peptideIdMap = new HashSet<>(peptideIds.size());
        peptideIdMap.addAll(peptideIds);

        int nb = m_table.getRowCount();
        int iCur = 0;
        for (int iView = 0; iView < nb; iView++) {
            int iModel = m_table.convertRowIndexToModel(iView);
            if (compoundTableModel != null) {
                iModel = compoundTableModel.convertCompoundRowToBaseModelRow(iModel);
            }
            // Retrieve Peptide
            DMasterQuantPeptide p = getPeptide(iModel);
            if (peptideIdMap.contains(p.getPeptideInstanceId())) {
                peptideIds.set(iCur++, p.getPeptideInstanceId());
            }
        }

    }


    
   @Override
   public void addFilters(LinkedHashMap<Integer, Filter> filtersMap) {
        filtersMap.put(COLTYPE_PEPTIDE_NAME, new StringDiffFilter(getColumnName(COLTYPE_PEPTIDE_NAME), null, COLTYPE_PEPTIDE_NAME));
        filtersMap.put(COLTYPE_PEPTIDE_PTM, new StringFilter(getColumnName(COLTYPE_PEPTIDE_PTM), null, COLTYPE_PEPTIDE_PTM));
        filtersMap.put(COLTYPE_PEPTIDE_PROTEINSET_COUNT, new IntegerFilter(getColumnName(COLTYPE_PEPTIDE_PROTEINSET_COUNT), null, COLTYPE_PEPTIDE_PROTEINSET_COUNT));
        filtersMap.put(COLTYPE_PEPTIDE_CLUSTER, new StringFilter(getColumnName(COLTYPE_PEPTIDE_CLUSTER), null, COLTYPE_PEPTIDE_CLUSTER));
        int nbCol = getColumnCount();
        for (int i=LAST_STATIC_COLUMN+1; i< nbCol; i++){
            int nbQc = (i - m_columnNames.length) / m_columnNamesQC.length;
            int id = i - m_columnNames.length - (nbQc * m_columnNamesQC.length);
            switch (id) {
                case COLTYPE_SELECTION_LEVEL:
                    filtersMap.put(i, new IntegerFilter(getColumnName(i), null, i));
                    break;
                case COLTYPE_ABUNDANCE:
                    filtersMap.put(i, new DoubleFilter(getColumnName(i), null, i));
                    break;
                case COLTYPE_RAW_ABUNDANCE:
                    filtersMap.put(i, new DoubleFilter(getColumnName(i), null, i));
                    break;
                case COLTYPE_PSM:
                    filtersMap.put(i, new IntegerFilter(getColumnName(i), null, i));
                    break;
                default:
                    filtersMap.put(i, new DoubleFilter(getColumnName(i), null, i));
                    break;
            }
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
        return m_isXICMode ? m_columnNamesQC[index] : m_columnNamesQC_SC[index];
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
    
    public void setOverviewType(int overviewType) {
        m_overviewType = overviewType;
        fireTableDataChanged();
    }
    
    public int getOverviewType() {
        return m_overviewType;
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
                listIds.add(m_columnNames.length + COLTYPE_IDENT_PSM + (i * m_columnNamesQC.length));
                if (m_isXICMode){
                    listIds.add(m_columnNames.length + COLTYPE_RAW_ABUNDANCE + (i * m_columnNamesQC.length));
                }else{
                    listIds.add(m_columnNames.length + COLTYPE_ABUNDANCE + (i * m_columnNamesQC.length));
                }
                listIds.add(m_columnNames.length + COLTYPE_SELECTION_LEVEL + (i * m_columnNamesQC.length));
                
            }
        }
        if (!m_isXICMode){
            listIds.add(COLTYPE_PEPTIDE_CLUSTER);
        }
        return listIds;
    }

 

    @Override
    public String getDataColumnIdentifier(int columnIndex) {
        if (columnIndex <= LAST_STATIC_COLUMN) {
            return m_columnNames[columnIndex];
        } else {
            int nbQc = (columnIndex - m_columnNames.length) / m_columnNamesQC.length;
            int id = columnIndex - m_columnNames.length - (nbQc * m_columnNamesQC.length);

            StringBuilder sb = new StringBuilder();
            if(m_isXICMode){
                sb.append(m_columnNamesQC[id]);
            }else{
                sb.append(m_columnNamesQC_SC[id]);
            }
            sb.append(' ');
            sb.append(m_quantChannels[nbQc].getResultFileName());
            
            return sb.toString();
        }
    }

    @Override
    public Class getDataColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLTYPE_PEPTIDE_ID: {
                return Long.class;
            }
            case COLTYPE_PEPTIDE_NAME: {
                return LazyData.class;
            }
            case COLTYPE_PEPTIDE_PTM: {
                return String.class;
            }
            case COLTYPE_PEPTIDE_PROTEINSET_COUNT: {
                return Integer.class;
            }
            case COLTYPE_OVERVIEW: {
                return CompareValueRenderer.CompareValue.class; 
            }
            case COLTYPE_PEPTIDE_CLUSTER: {
                return String.class; 
            }
            default: {
                int nbQc = (columnIndex - m_columnNames.length) / m_columnNamesQC.length;
                int id = columnIndex - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                switch (id) {
                    case COLTYPE_SELECTION_LEVEL:
                        return Integer.class;
                    case COLTYPE_ABUNDANCE:
                        return Float.class;
                    case COLTYPE_RAW_ABUNDANCE:
                        return Float.class;
                    case COLTYPE_PSM:
                    case COLTYPE_IDENT_PSM:
                        return Integer.class;
                }

            }
        }
        return null; // should never happen

    }

    @Override
    public Object getDataValueAt(int rowIndex, int columnIndex) {
        Object data = getValueAt(rowIndex, columnIndex);
        if (data instanceof LazyData) {
            data = ((LazyData) data).getData();
            if (data instanceof DPeptideMatch) {
                data = ((DPeptideMatch) data).getPeptide().getSequence();
            }
        }

        DMasterQuantPeptide peptide = m_quantPeptides.get(rowIndex);

        if(columnIndex == COLTYPE_PEPTIDE_ID) {
            return peptide.getId();
        }
        return data;
    }

    @Override
    public int[] getKeysColumn() {
        int[] keys = { COLTYPE_PEPTIDE_NAME };
        return keys;
    }

    @Override
    public int getInfoColumn() {
         return COLTYPE_PEPTIDE_NAME;
    }

    @Override
    public void setName(String name) {
        m_modelName = name;
    }

    @Override
    public String getName() {
        return m_modelName;
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
    public TableCellRenderer getRenderer(int row, int col) {

        if (m_rendererMap.containsKey(col)) {
            return m_rendererMap.get(col);
        }

        TableCellRenderer renderer = null;

        switch (col) {
            case COLTYPE_PEPTIDE_NAME: {
                renderer = new PeptideRenderer();
                break;
            }
            case COLTYPE_PEPTIDE_PTM:
            case COLTYPE_PEPTIDE_CLUSTER: {
                renderer = new DefaultLeftAlignRenderer(TableDefaultRendererManager.getDefaultRenderer(String.class));
                break;
            }
            case COLTYPE_OVERVIEW: {
                renderer = new CompareValueRenderer();
                break;
            }
            default: {
                int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                switch (id) {
                    case COLTYPE_SELECTION_LEVEL:
                    case COLTYPE_PSM:
                    case COLTYPE_IDENT_PSM: {
                        renderer = new DefaultRightAlignRenderer(TableDefaultRendererManager.getDefaultRenderer(Integer.class));
                        break;
                    }
                    case COLTYPE_ABUNDANCE:
                    case COLTYPE_RAW_ABUNDANCE: {
                        if (m_isXICMode){
                            renderer = new BigFloatOrDoubleRenderer( new DefaultRightAlignRenderer(TableDefaultRendererManager.getDefaultRenderer(String.class)), 0 );
                        }else{
                            renderer = new FloatRenderer(new DefaultRightAlignRenderer(TableDefaultRendererManager.getDefaultRenderer(String.class)));
                        }
                        break;
                    }

                }

            }
        }
        
        m_rendererMap.put(col, renderer);
        return renderer;
    }
    private final HashMap<Integer, TableCellRenderer> m_rendererMap = new HashMap();

    @Override
    public GlobalTableModelInterface getFrozzenModel() {
        return this;
    }

    @Override
    public ArrayList<ExtraDataType> getExtraDataTypes() {
        ArrayList<ExtraDataType> list = new ArrayList<>();
        list.add(new ExtraDataType(DMasterQuantPeptide.class, true));
        list.add(new ExtraDataType(ResultSummary.class, false));
        list.add(new ExtraDataType(DDataset.class, false));
        list.add(new ExtraDataType(QuantChannelInfo.class, false));
        list.add(new ExtraDataType(XicMode.class, false));
        registerSingleValuesAsExtraTypes(list);
        return list;
    }

    @Override
    public Object getValue(Class c) {
        return getSingleValue(c);
    }

    @Override
    public Object getRowValue(Class c, int row) {
        if (c.equals(DMasterQuantPeptide.class)) {
            return m_quantPeptides.get(row);
        }
        return null;
    }
    
    @Override
    public Object getColValue(Class c, int col) {
        if (c.equals(XicGroup.class)) {
            if (col <= LAST_STATIC_COLUMN) {
                return null;
            } else {
                int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                return new XicGroup(m_quantChannels[nbQc].getBiologicalGroupId(), null); //biologicalGroupName.getBiologicalGroupName(); JPM.TODO
            }

            
        }
        if (c.equals(QuantitationType.class)) {
            if (col <= LAST_STATIC_COLUMN) {
                return null;
            } else {
                int nbQc = (col - m_columnNames.length) / m_columnNamesQC.length;
                int id = col - m_columnNames.length - (nbQc * m_columnNamesQC.length);
                if (m_isXICMode) {
                    switch (id) {
                        case COLTYPE_ABUNDANCE:
                            return QuantitationType.getQuantitationType(QuantitationType.ABUNDANCE);
                        case COLTYPE_RAW_ABUNDANCE:
                            return QuantitationType.getQuantitationType(QuantitationType.RAW_ABUNDANCE);
                    }
                } else {
                    switch (id) {
                        case COLTYPE_PSM:
                            return QuantitationType.getQuantitationType(QuantitationType.BASIC_SC);
                        case COLTYPE_RAW_ABUNDANCE:
                            return QuantitationType.getQuantitationType(QuantitationType.SPECIFIC_SC);
                        case COLTYPE_ABUNDANCE:
                            return QuantitationType.getQuantitationType(QuantitationType.WEIGHTED_SC);
                    }
                }

                
                return null;
            }

        }
        return null;
    }

}
