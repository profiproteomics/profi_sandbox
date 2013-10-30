package fr.proline.studio.graphics.marker;

import fr.proline.studio.graphics.PlotPanel;
import fr.proline.studio.graphics.XAxis;
import fr.proline.studio.graphics.YAxis;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 *
 * @author JM235353
 */
public class LabelMarker extends AbstractMarker {

    public static final int ORIENTATION_X_LEFT = 0;
    public static final int ORIENTATION_X_RIGHT = 1;
    public static final int ORIENTATION_Y_TOP = 2;
    public static final int ORIENTATION_Y_BOTTOM = 3;
    private static final Font MARKER_FONT = new Font("Arial", Font.PLAIN, 12);
    private String m_valueLabel = null;
    private double m_x;
    private double m_y;
    private int m_orientationX;
    private int m_orientationY;
    private static final int LINE_DELTA = 18;

    public LabelMarker(PlotPanel plotPanel, double x, double y, String valueLabel, int orientationX, int orientationY) {
        super(plotPanel);

        m_valueLabel = valueLabel;
        m_x = x;
        m_y = y;
        m_orientationX = orientationX;
        m_orientationY = orientationY;
    }

    @Override
    public void paint(Graphics g) {
        XAxis xAxis = m_plotPanel.getXAxis();
        YAxis yAxis = m_plotPanel.getYAxis();

        int pixelX = xAxis.valueToPixel(m_x);
        int pixelY = yAxis.valueToPixel(m_y);

        int deltaX = (m_orientationX == ORIENTATION_X_LEFT) ? -LINE_DELTA : (m_orientationX == ORIENTATION_X_RIGHT) ? LINE_DELTA : 0;
        int deltaY = (m_orientationY == ORIENTATION_Y_TOP) ? -LINE_DELTA : (m_orientationY == ORIENTATION_Y_BOTTOM) ? LINE_DELTA : 0;

        g.setFont(MARKER_FONT);
        FontMetrics metrics = g.getFontMetrics();
        int stringWidth = metrics.stringWidth(m_valueLabel);
        int stringHeight = metrics.getHeight();

        g.setColor(Color.black);
        g.drawLine(pixelX, pixelY, pixelX + deltaX, pixelY + deltaY);

        final int DELTA = 3;

        if (m_orientationX == ORIENTATION_X_RIGHT) {
            int xBox = pixelX + deltaX;
            int yBox = pixelY + deltaY - stringHeight / 2 - DELTA;
            int widthBox = stringWidth + DELTA * 2;
            int heightBox = stringHeight + DELTA * 2;
            g.setColor(new Color(255,255,255,128));
            g.fillRect(xBox, yBox, widthBox, heightBox);
            g.setColor(Color.black);
            g.drawRect(xBox, yBox, widthBox, heightBox);
            g.drawString(m_valueLabel, xBox + DELTA, yBox + metrics.getAscent());
        } else if (m_orientationX == ORIENTATION_X_LEFT) {
            int xBox = pixelX + deltaX - stringWidth + DELTA * 2;
            int yBox = pixelY + deltaY - stringHeight / 2 - DELTA;
            int widthBox = stringWidth + DELTA * 2;
            int heightBox = stringHeight + DELTA * 2;
            g.setColor(new Color(255,255,255,128));
            g.fillRect(xBox, yBox, widthBox, heightBox);
            g.setColor(Color.black);
            g.drawRect(xBox, yBox, widthBox, heightBox);
            g.drawString(m_valueLabel, xBox + DELTA, yBox + metrics.getAscent());
        } else if (m_orientationY == ORIENTATION_Y_TOP) {
            int xBox = pixelX + deltaX - stringWidth / 2 - DELTA;
            int yBox = pixelY + deltaY - stringHeight - DELTA * 2;
            int widthBox = stringWidth + DELTA * 2;
            int heightBox = stringHeight + DELTA * 2;
            g.setColor(new Color(255,255,255,128));
            g.fillRect(xBox, yBox, widthBox, heightBox);
            g.setColor(Color.black);
            g.drawRect(xBox, yBox, widthBox, heightBox);
            g.drawString(m_valueLabel, xBox + DELTA, yBox + metrics.getAscent());
        } else if (m_orientationY == ORIENTATION_Y_BOTTOM) {
            int xBox = pixelX + deltaX - stringWidth / 2 - DELTA;
            int yBox = pixelY + deltaY;
            int widthBox = stringWidth + DELTA * 2;
            int heightBox = stringHeight + DELTA * 2;
            g.setColor(new Color(255,255,255,128));
            g.fillRect(xBox, yBox, widthBox, heightBox);
            g.setColor(Color.black);
            g.drawRect(xBox, yBox, widthBox, heightBox);
            g.drawString(m_valueLabel, xBox + DELTA, yBox + metrics.getAscent());
        }

        
    }
    
}
