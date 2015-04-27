package fr.proline.studio.rsmexplorer.gui.calc.graph;

import fr.proline.studio.rsmexplorer.gui.calc.GraphPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author JM235353
 */
public abstract class GraphNode extends AbstractGraphObject {
    
    public enum NodeState {
        UNSET,
        NOT_CONNECTED,
        CONNECTED,
        PARAMETERS_SET,
        DATA_LOADING,
        READY_TO_CALCULATE,
        READY
    }
    
    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;
    private static final int MARGIN = 5;
    
    protected LinkedList<GraphConnector> m_inConnectors = null;
    protected GraphConnector m_outConnector = null;
    
   
    protected int m_x = 0;
    protected int m_y = 0;

    protected static Font m_font = null;
    protected static Font m_fontBold = null;
    
    protected static int m_hgtBold;
    protected static int m_hgtPlain;
    protected static int m_ascentBold;
    
    protected NodeState m_state = NodeState.UNSET;

    public GraphNode() {
        super(TypeGraphObject.GRAPH_NODE);
    }

    
    public void setCenter(int x, int y) {
        m_x = x-WIDTH/2;
        m_y = y-HEIGHT/2;
        if (m_x<40) {
            m_x = 40;
        }
        if (m_y<40) {
            m_y = 40;
        }
        setConnectorsPosition();
    }
    
    public int getCenterX() {
        return m_x+WIDTH/2;
    }
    
    public int getCenterY() {
        return m_y+HEIGHT/2;
    }
    

    
    @Override
    public void draw(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
                
        
        initFonts(g);

        String name = getName();

        LinearGradientPaint gradient = getBackgroundGradient();
        g2.setPaint(gradient);
        g.fillRoundRect(m_x, m_y, WIDTH, HEIGHT, 8, 8);
        
        g.setColor(getFrameColor());
        
        Stroke previousStroke = g2.getStroke();
        BasicStroke stroke = m_selected ? STROKE_SELECTED : STROKE_NOT_SELECTED;
        g2.setStroke(stroke);
        g.drawRoundRect(m_x, m_y, WIDTH, HEIGHT, 8, 8);
        g2.setStroke(previousStroke);
        
        FontMetrics metricsBold = g.getFontMetrics(m_fontBold);
        int stringWidth = metricsBold.stringWidth(name);
        
        int xText = m_x+(WIDTH-stringWidth)/2;
        int yText = m_y-m_hgtBold;
        
        ImageIcon icon = getIcon();
        if (icon != null) {
            g.drawImage(icon.getImage(), m_x+MARGIN, m_y+MARGIN, null);
        }
        
        g.setFont(m_fontBold);
        g.setColor(Color.black);
        g.drawString(name, xText, yText);
        
        
        
        if (m_inConnectors != null) {
            for (GraphConnector connector : m_inConnectors) {
                connector.draw(g);
            }
        }
        if (m_outConnector != null) {
            m_outConnector.draw(g);
        }
        
        
    }
    
    public abstract String getName();
    public abstract Color getFrameColor();
    public abstract ImageIcon getIcon();
    
    public abstract boolean isConnected();
    public abstract NodeState getState();
    public abstract void process();
    public abstract void display();


    public LinearGradientPaint getBackgroundGradient() {
        
        if ((m_gradient == null) || (m_gradientStartY != m_y)) {

            Color frameColor = getFrameColor();

            Point2D start = new Point2D.Float(m_x, m_y);
            Point2D end = new Point2D.Float(m_x, m_y + HEIGHT);
            float[] dist = {0.0f, 0.5f, 0.501f, 1.0f};
            Color[] colors = {Color.white, frameColor.brighter(), frameColor, frameColor.brighter()};
            m_gradient =  new LinearGradientPaint(start, end, dist, colors);
            m_gradientStartY = m_y;
        }
        
        return m_gradient;
            
    }
    private LinearGradientPaint m_gradient = null;
    private int m_gradientStartY = -1;

    protected void initFonts(Graphics g) {
        if (m_font != null) {
            return;
        }
        
        m_font = new Font(" TimesRoman ", Font.PLAIN, 11);
        m_fontBold = m_font.deriveFont(Font.BOLD);

        FontMetrics metricsBold = g.getFontMetrics(m_fontBold);
        FontMetrics metricsPlain = g.getFontMetrics(m_font);

        m_hgtBold = metricsBold.getHeight();
        m_ascentBold = metricsBold.getAscent();

        m_hgtPlain = metricsPlain.getHeight();
        //m_ascentPlain = metricsPlain.getAscent();
    }

    @Override
    public AbstractGraphObject inside(int x, int y) {
        
        if (m_inConnectors != null) {
            for (GraphConnector connector : m_inConnectors) {
                AbstractGraphObject object = connector.inside(x, y);
                if (object != null) {
                    return object;
                }
            }
        }
        if (m_outConnector != null) {
            AbstractGraphObject object = m_outConnector.inside(x, y);
            if (object != null) {
                return object;
            }
        }
        
        
        if ((x>=m_x) && (y>=m_y) && (x<=m_x+WIDTH) && (y<=m_y+HEIGHT)) {
            return this;
        }
        
        return null;
    }
    
    @Override
    public void move(int dx, int dy) {
        m_x += dx;
        m_y += dy;
        setConnectorsPosition();
    }
    
    private void setConnectorsPosition() {
        if (m_inConnectors != null) {
            int nb = m_inConnectors.size();
            int i = 0;
            for (GraphConnector connector : m_inConnectors) {
                int y = m_y + (int) Math.round((((double) HEIGHT)/(nb+1))*(i+1));
                connector.setRightPosition(m_x, y);
                i++;
            }
        }
        if (m_outConnector != null) {
            m_outConnector.setPosition(m_x + WIDTH, m_y + HEIGHT / 2);
        }
    }
    
    @Override
    public void delete() {
        if (m_inConnectors != null) {
            for (GraphConnector connector : m_inConnectors) {
                connector.delete();
            }
        }
        if (m_outConnector != null) {
            m_outConnector.delete();
        }
    }
    
    @Override
    public JPopupMenu createPopup(final GraphPanel panel) {
        JPopupMenu popup = new JPopupMenu();
        popup.add(new DisplayAction(this));
        popup.addSeparator();
        popup.add(new DeleteAction(panel, this));
        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                setSelected(false); 
                panel.repaint();
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
            }
        });
        return popup;
    }
 
    public class DeleteAction  extends AbstractAction {
        
        private AbstractGraphObject m_graphObject = null;
        private GraphPanel m_graphPanel = null;
        
        public DeleteAction(GraphPanel panel, AbstractGraphObject graphObject) {
            super("Delete");
            m_graphPanel = panel;
            m_graphObject = graphObject;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            m_graphObject.delete();
            m_graphPanel.removeGraphNode(m_graphObject);
            m_graphPanel.repaint();
        }
    }
    
    public class DisplayAction extends AbstractAction {

        private GraphNode m_graphNode = null;

        public DisplayAction(GraphNode graphNode) {
            super("Display");
            m_graphNode = graphNode;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            m_graphNode.display();
        }
    }
    
    @Override
    public void resetState() {
        m_state = NodeState.UNSET;
    }
    
}