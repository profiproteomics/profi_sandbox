package fr.proline.studio.rsmexplorer.gui.calc;



import fr.proline.studio.gui.SplittedPanelContainer;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import javax.swing.JPanel;
import fr.proline.studio.python.data.TableInfo;
import fr.proline.studio.rsmexplorer.gui.calc.functions.AbstractFunction;
import fr.proline.studio.rsmexplorer.gui.calc.graph.DataGraphNode;
import fr.proline.studio.rsmexplorer.gui.calc.graph.FunctionGraphNode;
import fr.proline.studio.rsmexplorer.gui.calc.graph.GraphNode;
import fr.proline.studio.rsmexplorer.gui.calc.graph.GraphicGraphNode;
import fr.proline.studio.rsmexplorer.gui.calc.graphics.AbstractGraphic;
import fr.proline.studio.rsmexplorer.gui.calc.macros.AbstractMacro;
import fr.proline.studio.utils.IconManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JButton;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;



/**
 * Panel for the DataAnalyzer (combine tree + graph panel)
 * @author JM235353
 */
public class DataAnalyzerPanel extends JPanel implements DataBoxPanelInterface {

    private AbstractDataBox m_dataBox;

    
    private GraphPanel m_graphPanel;

    
    public DataAnalyzerPanel() {
        setLayout(new BorderLayout());
        setBounds(0, 0, 500, 400);
        setBackground(Color.white);

        JPanel internalPanel = initComponents();
        add(internalPanel, BorderLayout.CENTER);

    }
    
    
    private JPanel initComponents() {


        JPanel internalPanel = new JPanel();
        internalPanel.setBackground(Color.white);

        internalPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        

        // create tree objects
        
        JPanel treePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.fill = GridBagConstraints.BOTH;
        c2.insets = new java.awt.Insets(5, 5, 5, 5);
        
        

        
        
        JScrollPane treeScrollPane = new JScrollPane();
        final DataMixerTree dataMixerTree = new DataMixerTree();
        treeScrollPane.setViewportView(dataMixerTree);
        treeScrollPane.setMinimumSize(new Dimension(180,50));
        
        JButton refreshButton = new JButton("Refresh Data", IconManager.getIcon(IconManager.IconType.REFRESH));
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dataMixerTree.updataDataNodes();
            }
        });
        
        c2.gridx = 0;
        c2.gridy = 0;
        treePanel.add(refreshButton, c2);
        c2.gridx++;
        c.weightx = 1;
        treePanel.add(Box.createHorizontalGlue(), c2);
        
        c2.gridx = 0;
        c2.gridy++;
        c2.gridwidth = 2;
        c2.weightx = 1;
        c2.weighty = 1;
        treePanel.add(treeScrollPane, c2);
        

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, createGraphZonePanel());

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        internalPanel.add(splitPane, c);


        return internalPanel;

    }
    
    private JPanel createGraphZonePanel() {
        JPanel graphZonePanel = new JPanel();
        

        graphZonePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        
        
        //JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        //toolbar.setFloatable(false);
        
        final JButton playButton = new JButton("Process Graph",IconManager.getIcon(IconManager.IconType.CONTROL_PLAY));
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playButton.setEnabled(false);
                ProcessEngine.getProcessEngine().run(m_graphPanel.getNodes(), m_graphPanel, playButton);
            }
        });
        
        //toolbar.add(playButton);
        
        // create graph objects
        m_graphPanel = new GraphPanel();
        JScrollPane graphScrollPane = new JScrollPane();
        graphScrollPane.setBackground(Color.white);
        graphScrollPane.setViewportView(m_graphPanel);
        
        
        c.gridx = 0;
        c.gridy = 0;
        
        graphZonePanel.add(playButton, c);
        
        c.gridx++;
        c.weightx = 1;
        graphZonePanel.add(Box.createHorizontalGlue(), c);
        
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy++;
        c.weightx = 1;
        c.weighty = 1;
        graphZonePanel.add(graphScrollPane, c);
        
        graphZonePanel.setBackground(playButton.getBackground());
        
        return graphZonePanel;
    }

    
  

    public void addTableInfoToGraph(TableInfo tableInfo) {
        DataGraphNode graphNode = new DataGraphNode(tableInfo, m_graphPanel);
        m_graphPanel.addGraphNode(graphNode);
    }
    
    public void addFunctionToGraph(AbstractFunction function) {
        FunctionGraphNode graphNode = new FunctionGraphNode(function, m_graphPanel);
        m_graphPanel.addGraphNode(graphNode);
    }
    
    public void addMacroToGraph(AbstractMacro macro) {
        ArrayList<DataTree.DataNode> nodes = macro.getNodes();

        HashMap<DataTree.DataNode, GraphNode> graphNodeMap = new HashMap<>();
        
        boolean firstNode = true;
        Point position = null;
        
        for (DataTree.DataNode node : nodes) {
            DataTree.DataNode.DataNodeType type = node.getType();
            int levelX = macro.getLevelX(node);
            int levelY = macro.getLevelY(node);

            GraphNode graphNode = null;
            switch (type) {
                case FUNCTION: {
                    DataTree.FunctionNode functionNode = (DataTree.FunctionNode) node;
                    AbstractFunction function = functionNode.getFunction().cloneFunction(m_graphPanel);
                    graphNode = new FunctionGraphNode(function, m_graphPanel);
                    break;
                }
                case GRAPHIC: {
                    DataTree.GraphicNode graphicNode = (DataTree.GraphicNode) node;
                    AbstractGraphic graphic = graphicNode.getGraphic().cloneGraphic(m_graphPanel);
                    graphNode = new GraphicGraphNode(m_graphPanel, graphic);
                    break;
                }
            }
            if (firstNode) {
                firstNode = false;
                position = m_graphPanel.getNextGraphNodePosition(graphNode);
                if (position.x<GraphNode.WIDTH*1.4) {
                    position.x = (int) (GraphNode.WIDTH*1.4);
                }
                if (position.y<GraphNode.HEIGHT*1.4) {
                    position.y = (int) (GraphNode.HEIGHT*1.4);
                }
            }
            graphNodeMap.put(node, graphNode);
            m_graphPanel.addGraphNode(graphNode, position.x+(int) (levelX*GraphNode.WIDTH* 2.2), position.y+levelY*GraphNode.HEIGHT*2);
        }

        for (DataTree.DataNode node : nodes) {
            
            GraphNode nodeOut = graphNodeMap.get(node);
            ArrayList<DataTree.DataNode> links = macro.getLinks(node);
            for (DataTree.DataNode linkedNode : links) {
                GraphNode nodeIn = graphNodeMap.get(linkedNode);
                nodeOut.connectTo(nodeIn);
            }
        }
        
    }
    
    @Override
    public void setDataBox(AbstractDataBox dataBox) {
        m_dataBox = dataBox;
    }
    @Override
    public AbstractDataBox getDataBox() {
        return m_dataBox;
    }
    
    @Override
    public void addSingleValue(Object v) {
        // not used for the moment JPM.TODO ?
    }

    @Override
    public void setLoading(int id) {}

    @Override
    public void setLoading(int id, boolean calculating) {}

    @Override
    public void setLoaded(int id) {}

    @Override
    public ActionListener getRemoveAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getRemoveAction(splittedPanel);
    }

    @Override
    public ActionListener getAddAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getAddAction(splittedPanel);
    }

    @Override
    public ActionListener getSaveAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getSaveAction(splittedPanel);
    }


    

    
    public class DataMixerTree extends DataTree {

        public DataMixerTree() {
            super(new RootDataMixerNode(), false, m_graphPanel);
        }

        @Override
        public void action(DataTree.DataNode node) {
            switch (node.getType()) {

                case VIEW_DATA: {
                    TableInfo tableInfo = ((ViewDataNode) node).getTableInfo();
                    addTableInfoToGraph(tableInfo);
                    break;
                }
                case FUNCTION: {
                    AbstractFunction function = ((FunctionNode) node).getFunction().cloneFunction(m_graphPanel);
                    addFunctionToGraph(function);
                    break;
                }
                case MACRO: {
                    AbstractMacro macro = ((MacroNode) node).getMacro();
                    addMacroToGraph(macro);
                    break;
                }

            }
        }

    }
    

    
}

