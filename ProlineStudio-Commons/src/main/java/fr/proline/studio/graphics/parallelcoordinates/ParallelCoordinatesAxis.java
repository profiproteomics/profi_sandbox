package fr.proline.studio.graphics.parallelcoordinates;

import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.graphics.MoveableInterface;
import fr.proline.studio.graphics.PlotParallelCoordinates;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;   



/**
 *
 * @author JM235353
 */
public class ParallelCoordinatesAxis implements MoveableInterface {
    
    public static final int AXIS_WIDTH = 10;
    public static final int PAD_Y_UP = 40;
    public static final int PAD_Y_DOWN = 20;
    public static final int PAD_HANDLE = 2;
    
    private static final Color COLOR_TRANSPARENT_GRAY = new Color(164,164,164,128);
    private static final Color COLOR_SELECTION = new Color(164,80,40,128);

    protected Font m_valuesFont = null;
    protected FontMetrics m_valuesFontMetrics = null;
    
    private  ArrayList<AbstractValue> m_values = null;
    private HashMap<Integer, AbstractValue> m_rowIndexToValueMap = null;
    private String m_columnName = null;
    
    private int m_x;
    private int m_y = PAD_Y_UP;
    private int m_height;
    
    private boolean m_numericAxis;
    
    private final int m_id;
    private final PlotParallelCoordinates m_plot;
    
    private double m_selectionMinPercentage = 0;
    private double m_selectionMaxPercentage = 1;
    
    private enum OverSubObject {
      HANDLE_UP,
      HANDLE_BOTTOM,
      SCROLL,
      NONE
    };
    
    private OverSubObject m_overSubObject = OverSubObject.NONE;
    
    
    public ParallelCoordinatesAxis(int id, CompareDataInterface compareDataInterface, int colId, PlotParallelCoordinates plot) {
        Class dataClass = compareDataInterface.getDataColumnClass(colId);

        m_id = id;
        m_plot = plot;

        
        m_values = new ArrayList<>(compareDataInterface.getColumnCount());
        m_rowIndexToValueMap = new HashMap<>();
        
        if (dataClass.equals(String.class)) {
            prepareStringData(compareDataInterface, colId);
            m_numericAxis = false;
        } else /*if (dataClass.equals(Number.class))*/ {
            prepareNumberData(compareDataInterface, colId);
            m_numericAxis = true;
        }
        
        Collections.sort(m_values);
        
        m_columnName = compareDataInterface.getDataColumnIdentifier(colId);
        
        

    }
    
    public int getId() {
        return m_id;
    }
    

    public void paintBackground(Graphics2D g) {
        
        if (m_valuesFont == null) {
            m_valuesFont = g.getFont().deriveFont(Font.PLAIN, 10);
            m_valuesFontMetrics = g.getFontMetrics(m_valuesFont);
        }
        
        g.setColor(Color.black);
        int x = getX();
        g.drawLine(x, m_y, x, m_y+m_height);
    }
    
    public void paintForeground(Graphics2D g, boolean selected) {
        
        int y1 = (int) Math.round(m_y+m_height*m_selectionMinPercentage);
        int y2 =(int) Math.round(m_y+m_height*m_selectionMaxPercentage);
        int height = (int) Math.round(m_height*(m_selectionMaxPercentage-m_selectionMinPercentage));
        
        g.setColor(selected ? COLOR_SELECTION : COLOR_TRANSPARENT_GRAY);
        g.fillRect(m_x, y1, AXIS_WIDTH, height);
        
        g.setColor(Color.white);
        g.drawRect(m_x, y1, AXIS_WIDTH, height);
        int handleSize = AXIS_WIDTH-PAD_HANDLE*2;
        g.drawRect(m_x+PAD_HANDLE,y1+PAD_HANDLE,handleSize,handleSize);
        g.drawRect(m_x+PAD_HANDLE,y1+height-PAD_HANDLE-handleSize,handleSize,handleSize);
        
        // Display Column Name
        g.setColor(Color.black);
        g.setFont(m_valuesFont);
        int stringWidth = m_valuesFontMetrics.stringWidth(m_columnName);
        int fontHeight = m_valuesFontMetrics.getHeight();
        g.drawString(m_columnName, getX()-stringWidth/2, fontHeight+4);
        
        // Display Column Min
        if (m_numericAxis) {
            
            
            // Min Value
            NumberValue vMin = (NumberValue) m_values.get(0);
            String valueMin = vMin.toString();
            stringWidth = m_valuesFontMetrics.stringWidth(valueMin);
            g.drawString(valueMin, getX()-stringWidth/2, PAD_Y_UP/2+fontHeight+4);
            
            
            // Max Value
            NumberValue vMax = (NumberValue) m_values.get(m_values.size()-1);
            String valueMax = vMax.toString();
            stringWidth = m_valuesFontMetrics.stringWidth(valueMax);
            g.drawString(valueMax, getX()-stringWidth/2, m_y+m_height+fontHeight+4);
            
            // Min Value Selected
            if (y1>m_y) {
                double v = (vMax.doubleValue()-vMin.doubleValue())*m_selectionMinPercentage+vMin.doubleValue();
                String minValueSelected = String.valueOf(v);
                stringWidth = m_valuesFontMetrics.stringWidth(minValueSelected);
                g.drawString(minValueSelected, getX()-stringWidth/2, y1-fontHeight);
            }
            
            // Max Value Selected
             if (y2 < m_y+m_height) {
                double v = (vMax.doubleValue() - vMin.doubleValue()) * m_selectionMaxPercentage + vMin.doubleValue();
                String maxValueSelected = String.valueOf(v);
                stringWidth = m_valuesFontMetrics.stringWidth(maxValueSelected);
                g.drawString(maxValueSelected, getX() - stringWidth / 2, y2 + fontHeight + 4);
            }
            
        }
        
    }
    
    public void setPosition(int x, int height) {
        m_x = x;
        m_height = height-PAD_Y_UP-PAD_Y_DOWN;
    }
    
    public boolean isRowIndexSelected(int rowIndex) {
        
        if ((m_selectionMinPercentage<=1e-10) && (m_selectionMaxPercentage-1>=-1e-10)) {
            return true;
        }

        AbstractValue v = m_rowIndexToValueMap.get(rowIndex);
        if (m_numericAxis) {
            double value = ((NumberValue) v).doubleValue();
            double vMin = ((NumberValue) m_values.get(0)).doubleValue();
            double vMax = ((NumberValue) m_values.get(m_values.size()-1)).doubleValue();
            double minSelected = (vMax-vMin)*m_selectionMinPercentage+vMin;
            if (value<minSelected) {
                return false;
            }
            double maxSelected = (vMax - vMin) * m_selectionMaxPercentage +vMin;
            if (value>maxSelected) {
                return false;
            }
            
        }
        
        return true;
    }
    
    /*public int getPositionByIndex(int index) {
        return getRelativePositionByIndex(index)+m_y;
    }
    
    public int getRelativePositionByIndex(int index) {
        AbstractValue v = m_values.get(index);

        if (m_numericAxis) {
            double min = ((NumberValue) m_values.get(0)).doubleValue();
            double max = ((NumberValue) m_values.get(m_values.size() - 1)).doubleValue();
            double value = ((NumberValue) v).doubleValue();

            return (int) (((value - min) / (max - min)) * m_height);
        }

        return 0; //JPM.TODO
    }*/

    
    public int getPositionByRowIndex(int rowIndex) {
        return getRelativePositionByRowIndex(rowIndex)+m_y;
    }
    
    public int getRelativePositionByRowIndex(int rowIndex) {
        AbstractValue v = m_rowIndexToValueMap.get(rowIndex);

        if (m_numericAxis) {
            double min = ((NumberValue) m_values.get(0)).doubleValue();
            double max = ((NumberValue) m_values.get(m_values.size() - 1)).doubleValue();
            double value = ((NumberValue) v).doubleValue();

            return (int) (((value - min) / (max - min)) * m_height);
        }

        return 0; //JPM.TODO
    }
    
    public int getX() {
        return m_x + AXIS_WIDTH/2;
    }

    public int getHeight() {
        return m_height;
    }
    
    public int getRowIndexFromIndex(int index) {
        return m_values.get(index).getRowIndex();
    }
    
    private void prepareStringData(CompareDataInterface compareDataInterface, int colId) {

        int nbRows = compareDataInterface.getRowCount();
        for (int i = 0; i < nbRows; i++) {
            String value = (String) compareDataInterface.getDataValueAt(i, colId);
            StringValue nValue = new StringValue(value, i);
            m_values.add(nValue);
            m_rowIndexToValueMap.put(i, nValue);
        }
        
        

    }
    
    private void prepareNumberData(CompareDataInterface compareDataInterface, int colId) {

        int nbRows = compareDataInterface.getRowCount();
        for (int i = 0; i < nbRows; i++) {
            Number v = (Number) compareDataInterface.getDataValueAt(i, colId);
            NumberValue nValue = new NumberValue(v, i);
            m_values.add(nValue);
            m_rowIndexToValueMap.put(i, nValue);
        }

    }

    public MoveableInterface getOverMovable(int x, int y) {

        if ((x>=m_x) && (x<=m_x+AXIS_WIDTH) && (y>=m_y) && (y<=m_y+m_height)) {
            
            int y1 = (int) Math.round(m_y + m_height * m_selectionMinPercentage);
            int y2 = (int) Math.round(m_y + m_height * m_selectionMaxPercentage);

            if ((y>=y1) && (y<=y1+AXIS_WIDTH)) {
                m_overSubObject = OverSubObject.HANDLE_UP;
            } else if ((y <= y2) && (y >= y2 - AXIS_WIDTH)) {
                m_overSubObject = OverSubObject.HANDLE_BOTTOM;
            } else if ((y>=y1) && (y<=y2)) {
                m_overSubObject = OverSubObject.SCROLL;
            } else {
                m_overSubObject = OverSubObject.NONE; // should not happen !
            }
            
            return this;
        }
        m_overSubObject = OverSubObject.NONE;
        return null;
    }

    @Override
    public boolean inside(int x, int y) {
        return true;
    }

    @Override
    public void move(int deltaX, int deltaY) {
        if (deltaY==0) {
            return;
        }
        
        double deltaMinHandle = ((double) AXIS_WIDTH+PAD_HANDLE)/m_height;
        double percentageMove = ((double) deltaY) / ((double) m_height);
        switch (m_overSubObject) {
            case HANDLE_UP:
                m_selectionMinPercentage += percentageMove;
                if (m_selectionMinPercentage <= 0) {
                    m_selectionMinPercentage = 0;
                } else if (m_selectionMinPercentage+deltaMinHandle >= m_selectionMaxPercentage) {
                    m_selectionMinPercentage = m_selectionMaxPercentage-deltaMinHandle;
                }
                m_plot.axisChanged();
                break;
            case HANDLE_BOTTOM:
                m_selectionMaxPercentage += percentageMove;
                if (m_selectionMaxPercentage >=1) {
                    m_selectionMaxPercentage = 1;
                } else if (m_selectionMinPercentage+deltaMinHandle >= m_selectionMaxPercentage) {
                    m_selectionMaxPercentage = m_selectionMinPercentage+deltaMinHandle;
                }
                m_plot.axisChanged();
                break;
            case SCROLL:
                if ((m_selectionMinPercentage+percentageMove<0) || (m_selectionMaxPercentage+percentageMove>1)) {
                    // we do nothing, scroll impossible
                } else {
                    m_selectionMinPercentage += percentageMove;
                    m_selectionMaxPercentage += percentageMove;
                    m_plot.axisChanged();
                }
                break;
            case NONE:
                // should not happen
                break;
        }
        
    }
    


    @Override
    public boolean isMoveable() {
        return true;
    }

    @Override
    public void snapToData() {
        setSelected(true);
    }

    @Override
    public void setSelected(boolean s) {
        m_plot.selectAxis(this);
    }
    
}
