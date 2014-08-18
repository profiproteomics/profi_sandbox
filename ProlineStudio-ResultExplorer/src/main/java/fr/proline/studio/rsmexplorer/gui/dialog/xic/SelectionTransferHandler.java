package fr.proline.studio.rsmexplorer.gui.dialog.xic;

import fr.proline.core.orm.uds.Aggregation;
import fr.proline.core.orm.uds.Dataset;
import fr.proline.studio.dam.data.DataSetData;
import fr.proline.studio.dam.data.RunInfoData;
import fr.proline.studio.rsmexplorer.node.xic.RSMBiologicalSampleAnalysisNode;
import fr.proline.studio.rsmexplorer.node.xic.RSMBiologicalSampleNode;
import fr.proline.studio.rsmexplorer.node.RSMDataSetNode;
import fr.proline.studio.rsmexplorer.node.RSMNode;
import fr.proline.studio.rsmexplorer.node.xic.RSMBiologicalGroupNode;
import fr.proline.studio.rsmexplorer.node.xic.RSMRunNode;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Management of the drag and drop from the SelectionTree to the XIC DesignTree
 * @author JM235353
 */
public class SelectionTransferHandler extends TransferHandler {
    
    private Logger m_logger = LoggerFactory.getLogger("ProlineStudio.ResultExplorer");
    
    private boolean m_isSelectionTree;
    
    public SelectionTransferHandler(boolean isSelectionTree) {
        m_isSelectionTree = isSelectionTree;
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }
    
    

    
    @Override
    protected Transferable createTransferable(JComponent c) {

        if (m_isSelectionTree) {
            SelectionTree tree = (SelectionTree) c;

            RSMNode[] selectedNodes = tree.getSelectedNodes();
            ArrayList<RSMDataSetNode> keptNodes = new ArrayList<>();
            
            retrieveLeaves(keptNodes, selectedNodes);
            
            if (keptNodes.isEmpty()) {
                return null;
            }

            SelectionTransferable.TransferData data = new SelectionTransferable.TransferData();
            data.setDatasetList(keptNodes);
            Integer transferKey =  SelectionTransferable.register(data);

            return new SelectionTransferable(transferKey);




        } else {
            DesignTree tree = (DesignTree) c;

            RSMNode[] selectedNodes = tree.getSelectedNodes();
            
            ArrayList<RSMNode> keptNodes = new ArrayList<>();
            
            // only the nodes of the same type can be transferred
            RSMNode.NodeTypes commonType = null;
            int nbSelectedNode = selectedNodes.length;
            for (int i=0;i<nbSelectedNode;i++) {
                RSMNode node = selectedNodes[i];
               
                RSMNode.NodeTypes type = node.getType();
                
                if ((type != RSMNode.NodeTypes.BIOLOGICAL_GROUP) && (type != RSMNode.NodeTypes.BIOLOGICAL_SAMPLE_ANALYSIS) &&  (type !=RSMNode.NodeTypes.BIOLOGICAL_SAMPLE)) {
                    return null;
                }
                if (commonType != null) {
                    if (commonType != type) {
                        return null;
                    }
                } else {
                    commonType = type;
                }
                
                keptNodes.add(node);
            } 
            
            
            SelectionTransferable.TransferData data = new SelectionTransferable.TransferData();
            data.setDesignList(keptNodes);
            Integer transferKey =  SelectionTransferable.register(data);

            
            
            return new SelectionTransferable(transferKey);

            
            /*
            RSMNode[] selectedNodes = tree.getSelectedNodes();
            ArrayList<RSMBiologicalSampleAnalysisNode> keptNodes = new ArrayList<>();
            
            int nbSelectedNode = selectedNodes.length;
            for (int i=0;i<nbSelectedNode;i++) {
                RSMNode node = selectedNodes[i];


                RSMNode.NodeTypes type = node.getType();
                if (type!= RSMNode.NodeTypes.BIOLOGICAL_SAMPLE_ANALYSIS) {
                    return null;
                }
                RSMBiologicalSampleAnalysisNode sampleAnalysisNode = (RSMBiologicalSampleAnalysisNode) node;
                if (!sampleAnalysisNode.hasResultSummary()) {
                    return null;
                }

                keptNodes.add(sampleAnalysisNode);


                
                
            } 
            
            SelectionTransferable.TransferData data = new SelectionTransferable.TransferData();
            data.setSampleAnalysisList(keptNodes);
            Integer transferKey =  SelectionTransferable.register(data);

            
            
            return new SelectionTransferable(transferKey);*/


        }

    }

    private void retrieveLeaves(ArrayList<RSMDataSetNode> keptNodes, RSMNode[] selectedNodes) {
        int nbSelectedNode = selectedNodes.length;
        for (int i = 0; i < nbSelectedNode; i++) {
            RSMNode node = selectedNodes[i];
            retrieveLeaves(keptNodes, node);
        }
    }

    private void retrieveLeaves(ArrayList<RSMDataSetNode> keptNodes, RSMNode node) {
        if (node.isChanging()) {
            return;
        }
        RSMNode.NodeTypes type = node.getType();
        if (type != RSMNode.NodeTypes.DATA_SET) {
            return;
        }

        RSMDataSetNode datasetNode = (RSMDataSetNode) node;

        if (node.isLeaf()) {
            if (!datasetNode.hasResultSummary()) {
                return;
            }
            keptNodes.add(datasetNode);
        } else {
            int nbChildren = node.getChildCount();
            for (int i = 0; i < nbChildren; i++) {
                retrieveLeaves(keptNodes, (RSMNode) node.getChildAt(i));
            }
        }
    }
    
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {

        // clean all transferred data
        if (m_isSelectionTree) {
            SelectionTransferable.clearRegisteredData();
        }
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {

        if (!m_isSelectionTree) {

            support.setShowDropLocation(true);
            
            if (support.isDataFlavorSupported(SelectionTransferable.RSMNodeList_FLAVOR)) {

                // drop path
                TreePath dropTreePath = ((JTree.DropLocation) support.getDropLocation()).getPath();
                if (dropTreePath == null) {
                    // should not happen
                    return false;
                }
                
                
                boolean designData;
                RSMNode.NodeTypes designNodeType = null;
                try {
                    SelectionTransferable transfer = (SelectionTransferable) support.getTransferable().getTransferData(SelectionTransferable.RSMNodeList_FLAVOR);
                    SelectionTransferable.TransferData data = SelectionTransferable.getData(transfer.getTransferKey());
                    designData = data.isDesignData();
                    if (designData) {
                        designNodeType = data.getDesignNodeType();
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    // should never happen
                    m_logger.error(getClass().getSimpleName() + " DnD error ", e);
                    return false;
                }


                // Determine whether we accept the location
                Object dropComponent = dropTreePath.getLastPathComponent();
                if (designData) {
                    if (!(dropComponent instanceof RSMNode)) {
                        return false;
                    }
                    
                    RSMNode.NodeTypes dropType = ((RSMNode) dropComponent).getType();
                    switch (dropType) {
                        case DATA_SET:
                            return (designNodeType == RSMNode.NodeTypes.BIOLOGICAL_GROUP);
                        case BIOLOGICAL_GROUP:
                            return (designNodeType == RSMNode.NodeTypes.BIOLOGICAL_SAMPLE);
                        case BIOLOGICAL_SAMPLE:
                            return (designNodeType == RSMNode.NodeTypes.BIOLOGICAL_SAMPLE_ANALYSIS);
                        default:
                            return false;
                            
                    }

                } else {

                    if ((dropComponent instanceof RSMBiologicalSampleNode) || // Sample Node
                            (dropComponent instanceof RSMDataSetNode) || // XIC Node
                            (dropComponent instanceof RSMBiologicalGroupNode)) {    // Group Node
                        return true;
                    }

                }
            }
        }

        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {

        if (canImport(support)) {

            try {
                SelectionTransferable transfer = (SelectionTransferable) support.getTransferable().getTransferData(SelectionTransferable.RSMNodeList_FLAVOR);
                SelectionTransferable.TransferData data = SelectionTransferable.getData(transfer.getTransferKey());
                return importNodes(support, data);


            } catch (UnsupportedFlavorException | IOException e) {
                // should never happen
                m_logger.error(getClass().getSimpleName() + " DnD error ", e);
                return false;
            }

            
        }
        
        return false;
    }

    
    private boolean importNodes(TransferSupport support, SelectionTransferable.TransferData data) {

        JTree.DropLocation location = ((JTree.DropLocation) support.getDropLocation());
        TreePath dropTreePath = location.getPath();
        int childIndex = location.getChildIndex();
        RSMNode dropRSMNode = (RSMNode) dropTreePath.getLastPathComponent();

        
        DesignTree tree = DesignTree.getDesignTree();
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        // no insert index specified -> we insert at the end
        if (childIndex == -1) {

            childIndex = dropRSMNode.getChildCount();
        }


        ArrayList<RSMDataSetNode> datasetList = (ArrayList<RSMDataSetNode>) data.getDatasetList();
        if (datasetList != null) {
            
            tree.expandNodeIfNeeded(dropRSMNode);
            
            if (dropRSMNode instanceof RSMDataSetNode) {
                // top node, we create a group now
                String groupName = "Group "+Integer.toString(dropRSMNode.getChildCount()+1);
                RSMBiologicalGroupNode biologicalGroupNode = new RSMBiologicalGroupNode(new DataSetData(groupName, Dataset.DatasetType.AGGREGATE, Aggregation.ChildNature.OTHER));
                treeModel.insertNodeInto(biologicalGroupNode, dropRSMNode, childIndex);
                dropRSMNode = biologicalGroupNode;
                childIndex = 0;
                tree.expandNodeIfNeeded(dropRSMNode);
            }
            
            if (dropRSMNode instanceof RSMBiologicalGroupNode) {
                // Group Node, we create a sample node
                String sampleName = "Sample " + Integer.toString(dropRSMNode.getChildCount() + 1);
                RSMBiologicalSampleNode biologicalSampleNode = new RSMBiologicalSampleNode(new DataSetData(sampleName, Dataset.DatasetType.AGGREGATE, Aggregation.ChildNature.OTHER));
                treeModel.insertNodeInto(biologicalSampleNode, dropRSMNode, childIndex);
                dropRSMNode = biologicalSampleNode;
                childIndex = 0;
                tree.expandNodeIfNeeded(dropRSMNode);
            }
            
            
            if (dropRSMNode instanceof RSMBiologicalSampleNode) {
                int nbNodes = datasetList.size();
                for (int i = 0; i < nbNodes; i++) {
                    RSMDataSetNode node = datasetList.get(i);

                    // create the new node
                    RSMBiologicalSampleAnalysisNode sampleAnalysisNode = new RSMBiologicalSampleAnalysisNode(node.getData());

                    // put a Run node in it

                    RSMRunNode runNode = new RSMRunNode(new RunInfoData(), node.getDataset().getProject().getId(), node.getResultSetId());
                    sampleAnalysisNode.add(runNode);

                    // add to new parent
                    treeModel.insertNodeInto(sampleAnalysisNode, dropRSMNode, childIndex);

                    childIndex++;

                }
            }
            

        } else {
            ArrayList<RSMNode> rsmList = (ArrayList<RSMNode>) data.getDesignList();
            int nbNodes = rsmList.size();
            for (int i = 0; i < nbNodes; i++) {
                RSMNode node = rsmList.get(i);


                // specific case when the node is moved in its parent
                int indexChild;
                if (dropRSMNode.isNodeChild(node)) {
                    // we are moving the node in its parent
                    indexChild = dropRSMNode.getIndex(node);
                    if (indexChild < childIndex) {
                        childIndex--;
                    }
                }

                // remove from parent (required when drag and dropped in the same parent)
                treeModel.removeNodeFromParent(node);

                // add to new parent
                treeModel.insertNodeInto(node, dropRSMNode, childIndex);

                childIndex++;

            }
        }
        return true;


    }

    
}

