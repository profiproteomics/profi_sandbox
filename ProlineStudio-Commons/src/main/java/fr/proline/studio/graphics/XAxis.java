package fr.proline.studio.graphics;

import fr.proline.studio.utils.CyclicColorPalette;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * X Axis
 *
 * @author JM235353
 */
public class XAxis extends Axis {

    private static final double FONT_ROTATE = Math.PI/6;
    
    private int m_lastWidth;
    
    private AxisTicks m_ticks;
    
    public XAxis(PlotPanel p) {
        super(p);
    }

    @Override
    public void paint(Graphics2D g) {

        if (m_selected) {
            int stringWidth = m_valuesFontMetrics.stringWidth("    ");
            g.setColor(Color.darkGray);
            g.fillRect(m_x-stringWidth, m_y, m_width+stringWidth*2, m_height);
        }

        

        if (m_selected) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.black);
        }

        if (m_log) {
            paintLog(g, m_ticks);
        } else {
            paintLinear(g, m_ticks);
        }

        // display title
        if (m_title != null) {
            if (m_titleFont == null) {
                m_titleFont = g.getFont().deriveFont(Font.BOLD, 11);
                m_titleFontMetrics = g.getFontMetrics(m_titleFont);
            }
            g.setFont(m_titleFont);
            if (m_selected) {
                g.setColor(Color.white);
            } else {
                g.setColor(Color.black);
            }
            int titleWidth = m_titleFontMetrics.stringWidth(m_title);
            int bottom = m_y + m_height;
            int top = m_y + m_height - PlotPanel.GAP_AXIS_TITLE;
            int ascent = m_titleFontMetrics.getAscent();
            int descent = m_titleFontMetrics.getDescent();
            int baseline = top + ((bottom + 1 - top) / 2) - ((ascent + descent) / 2) + ascent;
            g.drawString(m_title, (m_width - titleWidth) / 2, baseline);
        }

    }

    /**
     * Main purpose of this function is to evaluate if we need to plot labels in diagonal
     * when there is not enough space
     * @param g 
     */
    public void preparePaint(Graphics2D g) {
        
        m_labelMaxWidth = 0;
        m_mustDrawDiagonalLabels = false;

        if (m_valuesFont == null) {
            m_valuesFont = g.getFont().deriveFont(Font.PLAIN, 10);
            m_valuesFontMetrics = g.getFontMetrics(m_valuesFont);
        }
        
        int maxTicks = m_width / 30;
        m_ticks = new AxisTicks(m_minValue, m_maxValue, maxTicks, m_log, m_isInteger, m_isEnum);
        
                
        m_minTick = m_ticks.getTickMin();
        m_maxTick = m_ticks.getTickMax();
        m_tickSpacing = m_ticks.getTickSpacing();
        
        if (m_log) {
            preparePaintLog(g, m_ticks);
        } else {
            preparePaintLinear(g, m_ticks);
        }
        
        if (m_mustDrawDiagonalLabels) {
            m_labelMinWidth = m_valuesFontMetrics.stringWidth("000");
            if (m_valuesDiagonalFont == null) {
                AffineTransform rotateText = new AffineTransform();
                rotateText.rotate(FONT_ROTATE);
                m_valuesDiagonalFont = m_valuesFont.deriveFont(rotateText);
            }
            
            m_minimumAxisHeight = 8+m_valuesFontMetrics.getHeight()+(int) Math.round(StrictMath.ceil(StrictMath.sin(FONT_ROTATE)*m_labelMaxWidth));
        } else {
            m_labelMinWidth = m_valuesFontMetrics.stringWidth("0");
            m_minimumAxisHeight = 8+m_valuesFontMetrics.getHeight();
        }
    }
    private void preparePaintLinear(Graphics2D g, AxisTicks ticks) {

        int fractionalDigits = ticks.getFractionalDigits();
        int integerDigits = ticks.getIntegerDigits();
        if ((fractionalDigits != m_fractionalDigits) || (integerDigits != m_integerDigits) || (m_df == null)) {
            m_df = selectDecimalFormat(fractionalDigits, integerDigits);
            m_dfPlot = selectDecimalFormat(fractionalDigits + 2, integerDigits);
            m_fractionalDigits = fractionalDigits;
            m_integerDigits = integerDigits;
        }
        
        int pixelStart = valueToPixel(m_minTick);
        int pixelStop = valueToPixel(m_maxTick);

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }

        double multForRounding = Math.pow(10, fractionalDigits);

        String maxLabel = "";
        double x = m_minTick;
        int pX = pixelStart;
        int previousEndX = -Integer.MAX_VALUE;
        while (true) {
            

            int stringWidth;
            String label;
            if (m_isEnum) {
                label = m_plotPanel.getEnumValueX((int) Math.round(x), false); //JPM.WART
                if (label.isEmpty()) {
                    label = " "; //JPM.WART
                }
                stringWidth = m_valuesFontMetrics.stringWidth(label);

            } else {
                // round x
                double xDisplay = x;
                if (fractionalDigits > 0) {
                    xDisplay = StrictMath.round(xDisplay * multForRounding) / multForRounding;
                }

                label = m_df.format(xDisplay);
                stringWidth = m_valuesFontMetrics.stringWidth(label);
            }

            if (stringWidth>m_labelMaxWidth) {
                m_labelMaxWidth = stringWidth;
                maxLabel = label;
            }

            int posX = pX - stringWidth / 2;
            if (posX > previousEndX + 2) { // check to avoid to overlap labels
                previousEndX = posX + stringWidth;
            } else {
                m_mustDrawDiagonalLabels = true;
                previousEndX = posX + m_valuesFontMetrics.stringWidth("000");
            }

            x += m_tickSpacing;
            pX = valueToPixel(x);
            if (pX > pixelStop) {
                break;
            }
            
        }
        
        if (m_mustDrawDiagonalLabels) {
            if (maxLabel.length()>20) {
                maxLabel = maxLabel.substring(0, 19)+"..";
                m_labelMaxWidth = m_valuesFontMetrics.stringWidth(maxLabel);
            }
        }
    }
    
    private void preparePaintLog(Graphics2D g, AxisTicks ticks) {

        if (m_df == null) {
            m_df = selectLogDecimalFormat();
            m_dfPlot = selectLogDecimalFormat();
        }

        int pixelStart = valueToPixel(Math.pow(10, m_minTick));
        int pixelStop = valueToPixel(Math.pow(10, m_maxTick));

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }

        double x = m_minTick;
        int pX = pixelStart;
        int previousEndX = -Integer.MAX_VALUE;
        while (true) {


            // round x
            double xDisplay = x;

            String s = m_df.format(xDisplay);
            int stringWidth = m_valuesFontMetrics.stringWidth(s);

            if (stringWidth>m_labelMaxWidth) {
                m_labelMaxWidth = stringWidth;
            }
            
            int posX = pX - stringWidth / 2;
            if (posX > previousEndX + 2) { // check to avoid to overlap labels
                previousEndX = posX + stringWidth;
            } else {
                m_mustDrawDiagonalLabels = true;
                previousEndX = posX + m_valuesFontMetrics.stringWidth("000");
            }

            x += m_tickSpacing;
            pX = valueToPixel(Math.pow(10, x));
            if (pX > pixelStop) {
                break;
            }

        }

    }
 
    private void paintLinear(Graphics2D g, AxisTicks ticks) {

        int pixelStart = valueToPixel(m_minTick);
        int pixelStop = valueToPixel(m_maxTick);
        g.drawLine(pixelStart, m_y, pixelStop, m_y);

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }

        
        if (m_selected) {
            g.setColor(Color.white);
        } else {
            g.setColor(CyclicColorPalette.GRAY_TEXT_DARK);
        }

        
        g.setFont(m_mustDrawDiagonalLabels ? m_valuesDiagonalFont : m_valuesFont);

        int height = m_valuesFontMetrics.getHeight();

        int fractionalDigits = ticks.getFractionalDigits();
        double multForRounding = Math.pow(10, fractionalDigits);

        double x = m_minTick;
        int pX = pixelStart;
        int previousEndX = -Integer.MAX_VALUE;
        m_lastWidth = -1;
        while (true) {

            String label;
            if (m_isEnum) {
                label = m_plotPanel.getEnumValueX((int) Math.round(x), false); //JPM.WART
                if (m_mustDrawDiagonalLabels) {
                    if (label.length() > 20) {
                        label = label.substring(0, 19) + "..";
                    }
                }
            } else {
                // round x
                double xDisplay = x;
                if (fractionalDigits > 0) {
                    xDisplay = StrictMath.round(xDisplay * multForRounding) / multForRounding;
                }

                label = m_df.format(xDisplay);
                
            }

            int posX;
            if (m_mustDrawDiagonalLabels) {
                posX = pX;
            } else {
                int stringWidth = m_valuesFontMetrics.stringWidth(label);
                posX = pX - stringWidth / 2;
            }

            if (posX > previousEndX + 2) { // check to avoid to overlap labels
                
                g.drawLine(pX, m_y, pX, m_y + 4);
                g.drawString(label, posX, m_y + height + 4);

                
                previousEndX = posX + m_labelMinWidth;
            }

            x += m_tickSpacing;
            pX = valueToPixel(x);
            if (pX > pixelStop) {
                break;
            }

        }

    }

    private void paintLog(Graphics2D g, AxisTicks ticks) {
        m_minTick = ticks.getTickMin();
        m_maxTick = ticks.getTickMax();
        m_tickSpacing = ticks.getTickSpacing();

        if (m_df == null) {
            m_df = selectLogDecimalFormat();
            m_dfPlot = selectLogDecimalFormat();
        }

        int pixelStart = valueToPixel(Math.pow(10, m_minTick));
        int pixelStop = valueToPixel(Math.pow(10, m_maxTick));
        g.drawLine(pixelStart, m_y, pixelStop, m_y);

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }
 
        g.setFont(m_mustDrawDiagonalLabels ? m_valuesDiagonalFont : m_valuesFont);

        int height = m_valuesFontMetrics.getHeight();

        double x = m_minTick;
        int pX = pixelStart;
        int previousEndX = -Integer.MAX_VALUE;
        while (true) {
            
            if (m_selected) {
                g.setColor(Color.white);
            } else {
                g.setColor(CyclicColorPalette.GRAY_TEXT_DARK);
            }
            
            g.drawLine(pX, m_y, pX, m_y + 4);

            // round x
            double xDisplay = x;

            String s = m_df.format(xDisplay);
            int stringWidth = m_valuesFontMetrics.stringWidth(s);

            int posX = pX - stringWidth / 2;
            if (posX > previousEndX + 2) { // check to avoid to overlap labels
                g.drawString(s, posX, m_y + height + 4);
                previousEndX = posX + stringWidth;
            }

            x += m_tickSpacing;
            pX = valueToPixel(Math.pow(10, x));
            if (pX > pixelStop) {
                break;
            }
            
            if (m_selected) {
                g.setColor(Color.white);
            } else {
                g.setColor(CyclicColorPalette.GRAY_TEXT_LIGHT);
            }
            
            // display min ticks between two major ticks
            for (int i=2;i<=9;i++) {
                double xMinTick = Math.pow(10, x)*(((double)i)*0.1d);
                int pMinTick = valueToPixel(xMinTick);
                 g.drawLine(pMinTick, m_y, pMinTick, m_y + 4);                
            }

        }

    }

        public void paintGrid(Graphics2D g, int y, int height) {

        if (m_log) {
            paintGridLog(g, y, height);
        } else {
            paintGridLinear(g, y, height);
        }

    }
    
    public void paintGridLinear(Graphics2D g, int y, int height) {

        int pixelStart = valueToPixel(m_minTick+m_tickSpacing);
        int pixelStop = valueToPixel(m_maxTick);

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }

        g.setColor(CyclicColorPalette.GRAY_GRID);
        Stroke s = g.getStroke();
        g.setStroke(dashed);
        
        double x = m_minTick;
        int pX = pixelStart;
        int previousEndX = -Integer.MAX_VALUE;
        while (true) {
            
            if (pX > previousEndX + 2) { // check to avoid to display grid for overlap labels
                g.drawLine(pX, y, pX, y + height - 1);
                previousEndX = pX + m_lastWidth;
            }
            

            x += m_tickSpacing;
            pX = valueToPixel(x);
            if (pX > pixelStop) {
                break;
            }
        }
        g.setStroke(s);
    }
    
    public void paintGridLog(Graphics2D g, int y, int height) {
        int pixelStart = valueToPixel(Math.pow(10, m_minTick+m_tickSpacing));
        int pixelStop = valueToPixel(Math.pow(10, m_maxTick));

        if (pixelStart >= pixelStop) { // avoid infinite loop 
            return;
        }
        
        Stroke s = g.getStroke();
        g.setStroke(dashed);
        
        double x = m_minTick;
        int pX = pixelStart;
        while (true) {
            g.setColor(CyclicColorPalette.GRAY_GRID);
            g.drawLine(pX, y, pX, y + height - 1);

            x += m_tickSpacing;
            pX = valueToPixel(Math.pow(10, x));
            if (pX > pixelStop) {
                break;
            }
            
            g.setColor(CyclicColorPalette.GRAY_GRID_LOG);
            
            // display min ticks between two major ticks
            for (int i=2;i<=9;i++) {
                double xMinTick = Math.pow(10, x)*(((double)i)*0.1d);
                int pMinTick = valueToPixel(xMinTick);
                g.drawLine(pMinTick, y, pMinTick, y + height - 1);
            }
        }
        g.setStroke(s);
    }

    @Override
    public int valueToPixel(double v) {
        if (m_log) {
            v = Math.log10(v);
        }
        return m_x + (int) Math.round(((v - m_minTick) / (m_maxTick - m_minTick)) * m_width);
    }

    @Override
    public double pixelToValue(int pixel) {
        double v = m_minTick + ((((double) pixel) - m_x) / ((double) m_width)) * (m_maxTick - m_minTick);
        if (m_log) {
            v = Math.pow(10, v);
        }
        return v;
    }

}
