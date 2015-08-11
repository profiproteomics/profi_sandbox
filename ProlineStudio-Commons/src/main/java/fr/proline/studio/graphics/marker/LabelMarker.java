package fr.proline.studio.graphics.marker;

import fr.proline.studio.graphics.marker.coordinates.AbstractCoordinates;
import fr.proline.studio.graphics.BasePlotPanel;
import fr.proline.studio.graphics.MoveableInterface;
import fr.proline.studio.graphics.marker.coordinates.PixelCoordinates;
import java.awt.*;

/**
 * Marker to display text at a specific position
 *
 * @author JM235353
 */
public class LabelMarker extends AbstractMarker implements MoveableInterface {

    public static final int ORIENTATION_X_LEFT = 0;
    public static final int ORIENTATION_X_RIGHT = 1;
    public static final int ORIENTATION_XY_MIDDLE = 2;
    public static final int ORIENTATION_Y_TOP = 3;
    public static final int ORIENTATION_Y_BOTTOM = 4;
    
    public static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    
    private Font m_font = TEXT_FONT;
    private int m_fontAscent;
    
    private PointMarker m_anchorMarker;
    private AbstractCoordinates m_labelCoordinates = null;
    
    private String m_valueLabel = null;
    
    private final int m_orientationX;
    private final int m_orientationY;
    
    private static final int LINE_DELTA = 18;
    private final static int COLOR_WIDTH = 5;
    private final static int DELTA_COLOR = 4;
    private final static int DELTA = 3;


    private int m_heightBox;
    private int m_widthBox;
    
    // show a color point in the marker
    private Color m_referenceColor = null;
    
    // draw the line to anchor
    private boolean m_drawLineToAnchor = true;
    
    // draw frame around the text
    private boolean m_drawFrame  = true;

    /**
     * Constructor to have a label without anchor
     * @param plotPanel
     * @param coordinates
     * @param valueLabel 
     */
    public LabelMarker(BasePlotPanel plotPanel, AbstractCoordinates coordinates, String valueLabel) {
        super(plotPanel);

        m_valueLabel = valueLabel;
        m_anchorMarker = new PointMarker(plotPanel, coordinates);
        m_orientationX = ORIENTATION_XY_MIDDLE;
        m_orientationY = ORIENTATION_XY_MIDDLE;
        m_drawLineToAnchor = false;
    }
    
    /**
     * Constructor to have a label with an anchor but without a reference color
     * @param plotPanel
     * @param coordinates
     * @param valueLabel
     * @param orientationX
     * @param orientationY 
     */
    public LabelMarker(BasePlotPanel plotPanel, AbstractCoordinates coordinates, String valueLabel, int orientationX, int orientationY) {
        super(plotPanel);

        m_valueLabel = valueLabel;
        m_anchorMarker = new PointMarker(plotPanel, coordinates);
        m_orientationX = orientationX;
        m_orientationY = orientationY;
        m_drawLineToAnchor = (m_orientationX != ORIENTATION_XY_MIDDLE);
    }

    /**
     * Constructor to have a label with a reference color to remember another graphical object
     * @param plotPanel
     * @param coordinates
     * @param valueLabel
     * @param orientationX
     * @param orientationY
     * @param referenceColor 
     */
    public LabelMarker(BasePlotPanel plotPanel, AbstractCoordinates coordinates, String valueLabel, int orientationX, int orientationY, Color referenceColor) {
        super(plotPanel);

        m_valueLabel = valueLabel;
        m_anchorMarker = new PointMarker(plotPanel, coordinates);
        m_orientationX = orientationX;
        m_orientationY = orientationY;
        m_referenceColor = referenceColor;
        m_drawLineToAnchor = (m_orientationX != ORIENTATION_XY_MIDDLE);
    }

    public void setFont(Font f) {
        m_font = f;
    }
    
    public void setDrawFrame(boolean v) {
        m_drawFrame = v;
    }
     
    private boolean hasReferenceColor() {
        return m_referenceColor != null;
    }

    private void prepareCoordinates() {

        int deltaX;
        switch (m_orientationX) {
            case ORIENTATION_X_LEFT:
                deltaX = -LINE_DELTA;
                break;
            case ORIENTATION_X_RIGHT:
                deltaX = LINE_DELTA;
                break;
            case ORIENTATION_XY_MIDDLE:
            default:
                deltaX = 0;
                break;
        }

        int deltaY;
        switch (m_orientationY) {
            case ORIENTATION_Y_TOP:
                deltaY = -LINE_DELTA;
                break;
            case ORIENTATION_Y_BOTTOM:
                deltaY = LINE_DELTA;
                break;
            case ORIENTATION_XY_MIDDLE:
            default:
                deltaY = 0;
                break;
        }

        m_labelCoordinates = new PixelCoordinates(deltaX, deltaY);

    }
    
    @Override
    public void paint(Graphics2D g) {

        AbstractCoordinates anchorCoordinates = m_anchorMarker.getCoordinates();
        int pixelX = anchorCoordinates.getPixelX(m_plotPanel);
        int pixelY = anchorCoordinates.getPixelY(m_plotPanel);
        
        // set font
        g.setFont(m_font);
        
        // calculate height and width if needed
        
        if (m_firstPaint) {
            
            prepareCoordinates();
            
            int deltaX = m_labelCoordinates.getPixelX(m_plotPanel);
            int deltaY = m_labelCoordinates.getPixelY(m_plotPanel);
            
            FontMetrics metrics = g.getFontMetrics();
            m_fontAscent = metrics.getAscent();
            int stringWidth = metrics.stringWidth(m_valueLabel);
            int stringHeight = metrics.getHeight();
            stringWidth += DELTA * 2;
            if (hasReferenceColor()) {
                stringWidth += 2 * DELTA_COLOR + COLOR_WIDTH;
            }

            m_heightBox = stringHeight + DELTA * 2;
            m_widthBox = stringWidth;
            
            int xBox = 0;
            int yBox = 0;
            if (m_orientationX == ORIENTATION_X_RIGHT) {
                xBox = pixelX + deltaX;
                yBox = pixelY + deltaY - stringHeight / 2 - DELTA;
            } else if ((m_orientationX == ORIENTATION_X_LEFT) || (m_orientationX == ORIENTATION_XY_MIDDLE)) {
                xBox = pixelX + deltaX - stringWidth ;
                yBox = pixelY + deltaY - stringHeight / 2 - DELTA;
            }
            
            m_labelCoordinates.setPixelPosition(m_plotPanel, xBox-pixelX, yBox-pixelY);
            
            m_firstPaint = false;
        }
        
        int xBox = pixelX+m_labelCoordinates.getPixelX(m_plotPanel);
        int yBox = pixelY+m_labelCoordinates.getPixelY(m_plotPanel);


        // draw label
        int delta = 0;
        g.setColor(new Color(255, 255, 255, 196));
        g.fillRect(xBox, yBox, m_widthBox, m_heightBox);
        g.setColor(Color.black);
        if (m_drawFrame) {
            g.drawRect(xBox, yBox, m_widthBox, m_heightBox);
        }
        if (hasReferenceColor()) {
            g.setColor(m_referenceColor);
            g.fillRect(xBox + DELTA_COLOR, yBox + ((m_heightBox - COLOR_WIDTH) / 2), COLOR_WIDTH, COLOR_WIDTH);
            delta = 2 * DELTA_COLOR + COLOR_WIDTH;
            g.setColor(Color.black);
        } else {
            delta = DELTA;
        }
        g.drawString(m_valueLabel, xBox + delta, yBox + m_fontAscent);
        
        // draw line from anchor to label
        if (m_drawLineToAnchor) {
            g.setColor(Color.black);
            if (pixelX < xBox) {
                g.drawLine(pixelX, pixelY, xBox, yBox + m_heightBox / 2);
            } else if (pixelX > xBox + m_widthBox) {
                g.drawLine(pixelX, pixelY, xBox + m_widthBox, yBox + m_heightBox / 2);
            } else if (pixelY < yBox) {
                g.drawLine(pixelX, pixelY, xBox + m_widthBox / 2, yBox);
            } else if (pixelY > yBox) {
                g.drawLine(pixelX, pixelY, xBox + m_widthBox / 2, yBox + m_heightBox);
            }
        }



    }
    private boolean m_firstPaint = true;

    @Override
    public boolean inside(int x, int y) {
        AbstractCoordinates anchorCoordinates = m_anchorMarker.getCoordinates();
        int pixelX = anchorCoordinates.getPixelX(m_plotPanel);
        int pixelY = anchorCoordinates.getPixelY(m_plotPanel);
        int xBox = pixelX+m_labelCoordinates.getPixelX(m_plotPanel);
        int yBox = pixelY+m_labelCoordinates.getPixelY(m_plotPanel);
        return (x>=xBox) && (x<=xBox+m_widthBox) && (y>=yBox) && (y<=yBox+m_heightBox);
    }

    @Override
    public void move(int deltaX, int deltaY) {
        m_labelCoordinates.setPixelPosition(m_plotPanel, m_labelCoordinates.getPixelX(m_plotPanel)+deltaX, m_labelCoordinates.getPixelY(m_plotPanel)+deltaY);
    }

    @Override
    public boolean isMoveable() {
        return true;
    }

}
