/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.mzscope.ui;

import fr.proline.mzscope.model.Chromatogram;
import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.graphics.PlotInformation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 * data for chromatogram
 * @author MB243701
 */
public class ChromatogramXICModel  extends AbstractTableModel implements CrossSelectionInterface, CompareDataInterface{

    private static final String[] m_columnNames = {"Time", "intensities"};
    
    public static final int COLTYPE_CHROMATOGRAM_XIC_TIME = 0;
    public static final int COLTYPE_CHROMATOGRAM_XIC_INTENSITIES = 1;
    
    private Chromatogram chromato;
    
    private String m_modelName;
    
    private Color chromatoColor;

    public ChromatogramXICModel(Chromatogram chromato) {
        this.chromato = chromato;
    }
    
    @Override
    public int getRowCount() {
        return chromato.time.length;
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
    public Object getValueAt(int rowIndex, int columnIndex) {
       
        switch (columnIndex) {
            case COLTYPE_CHROMATOGRAM_XIC_TIME: {
                return chromato.time[rowIndex];
            }
            case COLTYPE_CHROMATOGRAM_XIC_INTENSITIES:{
                return chromato.intensities[rowIndex];
            }
        }
        return null; // should never happen
    }

    @Override
    public void select(ArrayList<Integer> rows) {
        
    }

    @Override
    public ArrayList<Integer> getSelection() {
        return new ArrayList();
    }

    @Override
    public String getDataColumnIdentifier(int columnIndex) {
        return getColumnName(columnIndex);
    }

    @Override
    public Class getDataColumnClass(int columnIndex) {
        return Double.class;
    }

    @Override
    public Object getDataValueAt(int rowIndex, int columnIndex) {
        return getValueAt(rowIndex, columnIndex);
    }

   @Override
    public int[] getKeysColumn() {
        int[] keys = { COLTYPE_CHROMATOGRAM_XIC_TIME };
        return keys;
    }

    @Override
    public int getInfoColumn() {
        return COLTYPE_CHROMATOGRAM_XIC_INTENSITIES;
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
       plotInformation.setPlotColor(chromatoColor);
       return plotInformation;
    }
    
    public void setColor(Color c){
        this.chromatoColor = c;
    }
    
}