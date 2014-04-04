package fr.proline.studio.rsmexplorer.node;

import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.uds.Project;
import fr.proline.studio.dam.data.AbstractData;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.SubTask;
import java.awt.event.MouseListener;
import java.util.*;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author JM235353
 */
public abstract class RSMTree extends JTree implements MouseListener {
 
    
    
    
    protected RSMTreeModel m_model;
    
    protected HashMap<AbstractData, RSMNode> loadingMap = new HashMap<>();
    
    protected void initTree(RSMNode top) {
        m_model = new RSMTreeModel(top);
        setModel(m_model);

        // rendering of the tree
        putClientProperty("JTree.lineStyle", "Horizontal");

        IdentificationTree.RSMTreeRenderer renderer = new IdentificationTree.RSMTreeRenderer();
        setCellRenderer(renderer);
        if (isEditable()) {
            setCellEditor(new IdentificationTree.RSMTreeCellEditor(this, renderer));
        }

        // -- listeners
        addMouseListener(this);         // used for popup triggering

    }
    
    
    protected void startLoading(final RSMNode nodeToLoad) {

        
        
        // check if the loading is necessary :
        // it is necessary only if we have an hour glass child
        // which correspond to data not already loaded
        if (nodeToLoad.getChildCount() == 0) {
            return;
        }
        RSMNode childNode = (RSMNode) nodeToLoad.getChildAt(0);
        if (childNode.getType() != RSMNode.NodeTypes.HOUR_GLASS) {
            return;
        }

        // register hour glass which is expanded
        loadingMap.put(nodeToLoad.getData(), nodeToLoad);

        final ArrayList<AbstractData> childrenList = new ArrayList<>();
        final AbstractData parentData = nodeToLoad.getData();

        if (nodeToLoad.getType() == RSMNode.NodeTypes.TREE_PARENT) {
            nodeToLoad.setIsChanging(true);
            m_model.nodeChanged(nodeToLoad);
        }
        
        // Callback used only for the synchronization with the AccessDatabaseThread
        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return false;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        
                        if (nodeToLoad.getType() == RSMNode.NodeTypes.TREE_PARENT) {
                            nodeToLoad.setIsChanging(false);
                            m_model.nodeChanged(nodeToLoad);
                        }
                        
                        dataLoaded(parentData, childrenList);

                        if (loadingMap.isEmpty() && (selectionFromrsmArray != null)) {
                            // A selection has been postponed, we do it now
                            setSelection(selectionFromrsmArray);
                            selectionFromrsmArray = null;
                        }
                    }
                });

            }
        };

        

        parentData.load(callback, childrenList);
    }
    
    protected void dataLoaded(AbstractData data, List<AbstractData> list) {

        RSMNode parentNode = loadingMap.remove(data);


        parentNode.remove(0); // remove the first child which correspond to the hour glass


        int indexToInsert = 0;
        Iterator<AbstractData> it = list.iterator();
        while (it.hasNext()) {
            AbstractData dataCur = it.next();
            parentNode.insert(RSMChildFactory.createNode(dataCur), indexToInsert);
            indexToInsert++;
        }

        m_model.nodeStructureChanged(parentNode);


    }
    
        public void setSelection(ArrayList<ResultSummary> rsmArray) {

        if (!loadingMap.isEmpty()) {
            // Tree is loading, we must postpone the selection
            selectionFromrsmArray = rsmArray;
            return;
        }

        RSMNode rootNode = (RSMNode) m_model.getRoot();
        ArrayList<TreePath> selectedPathArray = new ArrayList<>(rsmArray.size());

        ArrayList<RSMNode> nodePath = new ArrayList<>();
        nodePath.add(rootNode);
        setSelectionImpl(rootNode, nodePath, rsmArray, selectedPathArray);


        TreePath[] selectedPaths = selectedPathArray.toArray(new TreePath[selectedPathArray.size()]);

        setSelectionPaths(selectedPaths);

    }
    private ArrayList<ResultSummary> selectionFromrsmArray = null;

    private void setSelectionImpl(RSMNode parentNode, ArrayList<RSMNode> nodePath, ArrayList<ResultSummary> rsmArray, ArrayList<TreePath> selectedPathArray) {
        Enumeration en = parentNode.children();
        while (en.hasMoreElements()) {
            RSMNode node = (RSMNode) en.nextElement();
            if (node.getType() == RSMNode.NodeTypes.DATA_SET) {

                RSMDataSetNode dataSetNode = (RSMDataSetNode) node;
                Long rsmId = dataSetNode.getResultSummaryId();
                if (rsmId != null) {


                    int size = rsmArray.size();
                    for (int i = 0; i < size; i++) {
                        ResultSummary rsmItem = rsmArray.get(i);
                        if (rsmItem.getId() == rsmId.longValue()) {
                            nodePath.add(node);
                            TreePath path = new TreePath(nodePath.toArray());
                            selectedPathArray.add(path);
                            nodePath.remove(nodePath.size() - 1);
                            break;
                        }
                    }
                }
            }
            if (!node.isLeaf()) {
                nodePath.add(node);
                setSelectionImpl(node, nodePath, rsmArray, selectedPathArray);
            }
        }
        nodePath.remove(nodePath.size() - 1);
    }
    
        /**
     * Return an array of all selected nodes of the tree
     * @return 
     */
    public RSMNode[] getSelectedNodes() {
        TreePath[] paths = getSelectionModel().getSelectionPaths();
        
        int nbPath = paths.length;
        
        RSMNode[] nodes = new RSMNode[nbPath];
        
        for (int i=0;i<nbPath;i++) {
            nodes[i] = (RSMNode) paths[i].getLastPathComponent();
        }

        return nodes;
    }
    
    
    protected class RSMTreeModel extends DefaultTreeModel {

        public RSMTreeModel(TreeNode root) {
            super(root, false);
        }

        /**
         * This sets the user object of the TreeNode identified by path and
         * posts a node changed. If you use custom user objects in the TreeModel
         * you're going to need to subclass this and set the user object of the
         * changed node to something meaningful.
         */
        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
            RSMNode rsmNode = (RSMNode) path.getLastPathComponent();

            if (rsmNode.getType()== RSMNode.NodeTypes.PROJECT_IDENTIFICATION) {
                RSMProjectIdentificationNode projectNode = (RSMProjectIdentificationNode) rsmNode;
                Project project = projectNode.getProject();
                ((RSMProjectIdentificationNode) rsmNode).changeNameAndDescription(newValue.toString(), project.getDescription());
            } else if (rsmNode.getType() == RSMNode.NodeTypes.PROJECT_QUANTITATION) {  //JPM.TODO
                RSMProjectIdentificationNode projectNode = (RSMProjectIdentificationNode) rsmNode;
                Project project = projectNode.getProject();
                ((RSMProjectIdentificationNode) rsmNode).changeNameAndDescription(newValue.toString(), project.getDescription());
            } else if (rsmNode.getType() == RSMNode.NodeTypes.DATA_SET) {
                ((RSMDataSetNode) rsmNode).rename(newValue.toString());
            }
        }
    }
}
