package fr.proline.studio.rsmexplorer.gui.calc.graph;

import fr.proline.studio.python.data.TableInfo;
import java.awt.Color;


/**
 *
 * @author JM235353
 */
public class DataGraphNode extends GraphNode {
    
    private static final Color FRAME_COLOR = new Color(45,114,178);

    
    private TableInfo m_tableInfo = null;


    
    public DataGraphNode(TableInfo tableInfo) {
        m_tableInfo = tableInfo;
        
        m_outConnector = new GraphConnector(this, true);
    }

    @Override
    public String getName() {
        return m_tableInfo.getName();
    }

    @Override
    public Color getFrameColor() {
        return FRAME_COLOR;
    }

    

    
}
