package fr.proline.studio.rsmexplorer.gui.dialog.spectralcount;


import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.rsmexplorer.node.IdentificationTree;
import fr.proline.studio.rsmexplorer.node.RSMDataSetNode;
import fr.proline.studio.rsmexplorer.node.RSMNode;
import fr.proline.studio.utils.IconManager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.TreePath;

/**
 *
 * @author JM235353
 */
public class TreeSelectionPanel extends JPanel {
    
   
    private IdentificationTree m_tree = null;
    /*private ArrayList<DDataset> m_selectedDatasetList = null;
    private ArrayList<RSMDataSetNode> m_selectedRSMDSNodeList = null;*/

    private static TreeSelectionPanel m_treeSelectionPanel = null;
    
    public static TreeSelectionPanel getTreeSelectionPanel() {
        return m_treeSelectionPanel;
    }
    
    public static TreeSelectionPanel getTreeSelectionPanel(IdentificationTree tree) {
        m_treeSelectionPanel =  new TreeSelectionPanel(tree);
        return m_treeSelectionPanel;
    }
    
    private TreeSelectionPanel(IdentificationTree tree) {

        m_tree = tree;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        add(createWizardPanel(), c);
        
        c.gridy++;
        c.weighty = 1;
        add(createMainPanel(), c);


 
    }

    private JComponent createMainPanel() {
        JScrollPane scrollPane = new JScrollPane(m_tree);
        return scrollPane;
    }
    
    private JPanel createWizardPanel() {
        JPanel wizardPanel = new JPanel();
        wizardPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        JLabel wizardLabel = new JLabel("<html><b>Step 2:</b> Select Identification Summaries.</html>");
        wizardLabel.setIcon(IconManager.getIcon(IconManager.IconType.WAND_HAT));
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        wizardPanel.add(wizardLabel, c);
        
        return wizardPanel;
    }
    
    public void setSelection(ArrayList<ResultSummary> rsmArray) {
        m_tree.setSelection(rsmArray);
    }
    
    public ArrayList<DDataset> getSelectedDatasetList() {
        ArrayList<DDataset> returnedList = new ArrayList<>();
        getSelectedData(returnedList, null);
        return returnedList;
    }
    
    public ArrayList<RSMDataSetNode> getSelectedRSMDSNodeList() {
        ArrayList<RSMDataSetNode> returnedList = new ArrayList<>();
        getSelectedData(null, returnedList);
        return returnedList;
    }
    
    public TreePath[] getSelectionPaths() {
        return m_tree.getSelectionPaths();
    }
    
    public IdentificationTree getTree() {
        return m_tree;
    }

    public void getSelectedData(ArrayList<DDataset> selectedDatasetList, ArrayList<RSMDataSetNode> selectedRSMDSNodeList) {
        TreePath[] paths = m_tree.getSelectionPaths();
        int size = paths.length;
        for (int i = 0; i < size; i++) {
            RSMNode node = (RSMNode) paths[i].getLastPathComponent();
            if (node.getType() == RSMNode.NodeTypes.DATA_SET) {
                RSMDataSetNode dataSetNode = (RSMDataSetNode) node;

                if (selectedDatasetList!=null) {
                    selectedDatasetList.add(dataSetNode.getDataset());
                }
                if (selectedRSMDSNodeList!=null) {
                    selectedRSMDSNodeList.add(dataSetNode);
                }
            }
        }
    }

    
}
