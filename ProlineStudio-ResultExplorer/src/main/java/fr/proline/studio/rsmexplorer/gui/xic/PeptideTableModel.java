package fr.proline.studio.rsmexplorer.gui.xic;

import fr.proline.core.orm.msi.dto.DMasterQuantPeptide;
import fr.proline.core.orm.msi.dto.DQuantPeptide;
import fr.proline.core.orm.uds.dto.DQuantitationChannel;
import fr.proline.studio.filter.Filter;
import fr.proline.studio.graphics.PlotInformation;
import fr.proline.studio.graphics.PlotType;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.table.LazyData;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.LazyTableModel;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * data for one masterQuantPeptide : for the different conditions (quant channels), abundances values
 * @author MB243701
 */
public class PeptideTableModel extends LazyTableModel implements  GlobalTableModelInterface /*, CrossSelectionInterface*/ {
    
    
    public static final int COLTYPE_QC_ID = 0;
    public static final int COLTYPE_QC_NAME = 1;
    public static final int COLTYPE_ABUNDANCE = 2;
    public static final int COLTYPE_RAW_ABUNDANCE = 3;
    private static final String[] m_columnNames = {"Id", "Quant. Channel", "Abundance", "Raw Abundance"};
    
    private DMasterQuantPeptide m_quantPeptide = null;
    private DQuantitationChannel[] m_quantChannels = null;

    private boolean m_isFiltering = false;
    private boolean m_filteringAsked = false;
    
    private String m_modelName;

    public PeptideTableModel(LazyTable table) {
        super(table);
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
    public int getSubTaskId(int col) {
        return -1;
    }

    @Override
    public String getToolTipForHeader(int col) {
        return getColumnName(col);
    }
    
    @Override
    public String getTootlTipValue(int row, int col) {
        return null;
    }

    @Override
    public int getRowCount() {
        if (m_quantChannels == null) {
            return 0;
        }

        return m_quantChannels.length ;
    }


    @Override
    public Object getValueAt(int row, int col) {

        // Retrieve QuantChannel
        DQuantitationChannel qc = m_quantChannels[row];
        // retrieve quantPeptide for the quantChannelId
        Map<Long, DQuantPeptide> quantPeptideByQchIds = m_quantPeptide.getQuantPeptideByQchIds();
        DQuantPeptide quantPeptide = quantPeptideByQchIds.get(qc.getId());
        switch (col) {
            case COLTYPE_QC_ID: {
                return qc.getId() ;
            }
            case COLTYPE_QC_NAME: {
                return qc.getResultFileName() ;
            }
            case COLTYPE_ABUNDANCE: {
                if (quantPeptide == null || quantPeptide.getAbundance() == null) {
                    return null;
                }
                return quantPeptide.getAbundance().isNaN() ? null : quantPeptide.getAbundance();
            }
            case COLTYPE_RAW_ABUNDANCE: {
                if (quantPeptide == null || quantPeptide.getRawAbundance() == null) {
                    return null;
                }
                return quantPeptide.getRawAbundance().isNaN() ? null : quantPeptide.getRawAbundance();
            }
        }
        return null; // should never happen
    }

    public void setData(DQuantitationChannel[] quantChannels, DMasterQuantPeptide peptide) {
        this.m_quantChannels  =quantChannels ;
        this.m_quantPeptide = peptide ;
        
        fireTableDataChanged();

    }
    
    public void dataUpdated() {

        // no need to do an updateMinMax : scores are known at once
        fireTableDataChanged();
    }
    
    
    @Override
    public void addFilters(LinkedHashMap<Integer, Filter> filtersMap) {
        
    }



    @Override
    public boolean isLoaded() {
        return m_table.isLoaded();
    }

    @Override
    public int getLoadingPercentage() {
        return m_table.getLoadingPercentage();
    }

    @Override
    public String getDataColumnIdentifier(int columnIndex) {
        return m_columnNames[columnIndex];
    }

    @Override
    public Class getDataColumnClass(int columnIndex) {
        switch (columnIndex) {
            case COLTYPE_QC_ID: {
                return Long.class;
            }
            case COLTYPE_QC_NAME: {
                return String.class;
            }
            case COLTYPE_ABUNDANCE: {
                return Float.class;
            }
            case COLTYPE_RAW_ABUNDANCE: {
                return Float.class;
            }
        }
        return null;
    }

    @Override
    public Object getDataValueAt(int rowIndex, int columnIndex) {
        Object data = getValueAt(rowIndex, columnIndex);
        if (data instanceof LazyData) {
            data = ((LazyData) data).getData();
        }
        return data;
    }

    @Override
    public int[] getKeysColumn() {
        int[] keys = { COLTYPE_QC_ID };
        return keys;
    }

    @Override
    public int getInfoColumn() {
        return COLTYPE_QC_NAME;
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
        PlotInformation plotInformation = new PlotInformation();
        if (m_quantPeptide.getPeptideInstance() != null && m_quantPeptide.getPeptideInstance().getBestPeptideMatch() != null) {
            plotInformation.setPlotTitle(m_quantPeptide.getPeptideInstance().getBestPeptideMatch().getPeptide().getSequence());
        }
        plotInformation.setDrawPoints(true);
        plotInformation.setDrawGap(true);
        return plotInformation;
    }

    @Override
    public PlotType getBestPlotType() {
        return PlotType.LINEAR_PLOT;
    }

    @Override
    public int getBestXAxisColIndex(PlotType plotType) {
        switch (plotType) {
            case LINEAR_PLOT:
                return COLTYPE_QC_NAME;
        }
        return -1;
    }

    @Override
    public int getBestYAxisColIndex(PlotType plotType) {
        return COLTYPE_RAW_ABUNDANCE;
    }

    /*@Override
    public void select(ArrayList<Integer> rows) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<Integer> getSelection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    @Override
    public String getExportRowCell(int row, int col) {
        return null;
    }

    @Override
    public String getExportColumnName(int col) {
        return getColumnName(col);
    }
    
}
