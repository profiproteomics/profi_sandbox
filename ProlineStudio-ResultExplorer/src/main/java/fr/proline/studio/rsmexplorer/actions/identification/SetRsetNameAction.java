package fr.proline.studio.rsmexplorer.actions.identification;

import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.core.orm.uds.Project;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.DatabaseDataManager;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseDataSetTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.gui.ParameterDialog;
import fr.proline.studio.parameter.ObjectParameter;
import fr.proline.studio.parameter.ParameterList;
import fr.proline.studio.rsmexplorer.gui.ProjectExplorerPanel;
import fr.proline.studio.rsmexplorer.tree.DataSetNode;
import fr.proline.studio.rsmexplorer.tree.AbstractNode;
import fr.proline.studio.rsmexplorer.tree.AbstractTree;
import fr.proline.studio.rsmexplorer.tree.identification.IdentificationTree;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Action to Rename a dataset
 *
 * @author JM235353
 */
public class SetRsetNameAction extends AbstractRSMAction {

    private static final String GENERAL_APPLICATION_SETTINGS = "General Application Settings";
    private String m_parameterValue;
    private AbstractNode[] m_selectedNodes;
    private String m_naming;
    private AbstractTree m_tree;
    private boolean fail = false;
    private ArrayList<DataSetNode> toRename;

    // tree type: could be Identification or Quantitation
    AbstractTree.TreeType m_treeType = null;

    /**
     * Builds the RenameAction depending of the treeType
     *
     * @param treeType
     */
    public SetRsetNameAction(AbstractTree.TreeType treeType, String naming, String ctlProperty) {
        super(NbBundle.getMessage(SetRsetNameAction.class, ctlProperty), treeType);
        this.m_treeType = treeType;
        this.m_naming = naming;
    }

    @Override
    public void actionPerformed(AbstractNode[] selectedNodes, int x, int y) {

        m_selectedNodes = selectedNodes;

        //m_naming = showRenameDialog(x, y);
        if (m_naming != null) {

            int initialExpected = 0;

            m_tree = IdentificationTree.getCurrentTree();
            m_tree.subscribeRenamer(this);

            for (int i = 0; i < selectedNodes.length; i++) {
                if (selectedNodes[i].getType() == AbstractNode.NodeTypes.DATA_SET) {
                    initialExpected++;
                }
            }

            m_tree.setExpected(initialExpected);

            for (int i = 0; i < selectedNodes.length; i++) {
                if (selectedNodes[i].getType() == AbstractNode.NodeTypes.DATA_SET) {
                    m_tree.loadAllAtOnce((DataSetNode) selectedNodes[i], true);
                }
            }
        }

    }

    public void proceedWithRenaming() {

        fail = false;

        toRename = new ArrayList<DataSetNode>();

        for (int i = 0; i < m_selectedNodes.length; i++) {
            if (m_selectedNodes[i].getType() == AbstractNode.NodeTypes.DATA_SET) {
                DataSetNode datasetNode = (DataSetNode) m_selectedNodes[i];

                m_tree.expandNodeIfNeeded(datasetNode);

                if (datasetNode.getChildCount() > 0) {
                    Enumeration<AbstractNode> e = datasetNode.depthFirstEnumeration();
                    while (e.hasMoreElements()) {
                        AbstractNode currentElement = e.nextElement();
                        if (currentElement.getType() == AbstractNode.NodeTypes.DATA_SET && currentElement.isLeaf() && currentElement.getChildCount() == 0) {
                            toRename.add((DataSetNode) currentElement);
                        } else {
                            m_tree.expandNodeIfNeeded(currentElement);
                        }
                    }
                } else {
                    if (datasetNode.isLeaf()) {
                        toRename.add(datasetNode);
                    }
                }
            }
        }

        for (int i = 0; i < toRename.size(); i++) {

            DDataset dataset = toRename.get(i).getDataset();
            DataSetNode node = toRename.get(i);

            // we have to load the result set
            AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                    if (IdentificationTree.renameNode(dataset, m_naming, node, m_tree)) {
                        fail = true;
                    }

                }
            };

            if (dataset.getResultSet()==null) {
                // ask asynchronous loading of data
                DatabaseDataSetTask task = new DatabaseDataSetTask(callback);
                task.initLoadRsetAndRsm(dataset);
                AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
            } else {
                if (IdentificationTree.renameNode(dataset, m_naming, node, m_tree)) {
                    fail = true;
                }
            }
        }
        
        if (fail) {
            JOptionPane.showMessageDialog(null, "One or more ResultSet(s) were not renamed.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String showRenameDialog(int x, int y) {

        ParameterList parameterList = new ParameterList(GENERAL_APPLICATION_SETTINGS);
        Object[] objectTable = {ImportManager.SEARCH_RESULT_NAME_SOURCE, ImportManager.PEAKLIST_PATH_SOURCE, ImportManager.MSI_SEARCH_FILE_NAME_SOURCE};
        ObjectParameter parameter = new ObjectParameter(ImportManager.DEFAULT_SEARCH_RESULT_NAME_SOURCE_KEY, "Default Search Result Name Source", objectTable, 2, null);
        parameterList.add(parameter);
        parameterList.loadParameters(NbPreferences.root(), true);

        ParameterDialog dialog = new ParameterDialog(WindowManager.getDefault().getMainWindow(), "Rename Search Result(s)", parameter);
        dialog.setLocation(x, y);
        dialog.setVisible(true);

        if (dialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            this.m_parameterValue = (String) dialog.getParameterValue();
        }

        if ((this.m_parameterValue != null) && (this.m_parameterValue.length() > 0)) {
            return this.m_parameterValue;
        }

        return null;
    }

    @Override
    public void updateEnabled(AbstractNode[] selectedNodes) {

        // to execute this action, the user must be the owner of the project
        Project selectedProject = ProjectExplorerPanel.getProjectExplorerPanel().getSelectedProject();
        if (!DatabaseDataManager.getDatabaseDataManager().ownProject(selectedProject)) {
            setEnabled(false);
            return;
        }

        AbstractNode node = selectedNodes[0];
        AbstractNode.NodeTypes nodeType = node.getType();
        if ((nodeType != AbstractNode.NodeTypes.DATA_SET) || (nodeType == AbstractNode.NodeTypes.PROJECT_IDENTIFICATION)) {
            setEnabled(false);
            return;
        }

        for (int i = 0; i < selectedNodes.length; i++) {
            if (selectedNodes[i].getType() == AbstractNode.NodeTypes.PROJECT_IDENTIFICATION) {
                setEnabled(false);
                return;
            }
        }

        setEnabled(true);

    }

}