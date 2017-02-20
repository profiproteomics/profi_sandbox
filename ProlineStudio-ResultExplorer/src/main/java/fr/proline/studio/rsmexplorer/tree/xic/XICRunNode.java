package fr.proline.studio.rsmexplorer.tree.xic;

import fr.proline.core.orm.msi.Peaklist;
import fr.proline.core.orm.uds.Dataset;
import fr.proline.core.orm.uds.RawFile;
import fr.proline.core.orm.uds.Run;
import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.data.AbstractData;
import fr.proline.studio.dam.data.RunInfoData;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseLoadSinglePeaklist;
import fr.proline.studio.dam.tasks.DatabaseRunsTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.rsmexplorer.tree.AbstractNode;
import fr.proline.studio.rsmexplorer.tree.AbstractTree;
import fr.proline.studio.utils.IconManager;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.openide.nodes.Sheet;

/**
 * Tree Node representing a Run
 *
 * @author JM235353
 */
public class XICRunNode extends AbstractNode {

    public enum Search {

        BASED_ON_IDENTIFIER, BASED_ON_PATH
    }

    private DefaultTreeModel m_treeModel = null;
    private final AbstractTree m_tree;

    public XICRunNode(AbstractData data, AbstractTree tree) {
        super(NodeTypes.RUN, data);
        m_tree = tree;
    }

    public void init(final DDataset dataset, DefaultTreeModel treeModel, final AbstractTableModel tableModel) {
        m_treeModel = treeModel;

        setIsChanging(true);

        // look if we find a Raw File
        if (dataset.getType() == Dataset.DatasetType.IDENTIFICATION) {

            final HashMap<String, RawFile> rawfileFounds = new HashMap<String, RawFile>(1);
            final Run[] runOut = new Run[1];
            final XICRunNode xicRunNode = this;

            AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                    if (success) {
                        if (!rawfileFounds.isEmpty()) {

                            setIsChanging(false);
                            m_treeModel.nodeChanged(xicRunNode);

                            RawFile rawFile = rawfileFounds.entrySet().iterator().next().getValue();

                            RunInfoData runInfoData = ((RunInfoData) getData());
                            runInfoData.setLinkedRawFile(rawFile);
                            runInfoData.setRun(runOut[0]);

                            runInfoData.setStatus(RunInfoData.Status.LINKED_IN_DATABASE);

                        } else {
                            //recreate a raw file from msi
                            if (tableModel != null) {
                                
                                final Peaklist peaklist = null;

                                AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                                    @Override
                                    public boolean mustBeCalledInAWT() {
                                        return true;
                                    }

                                    @Override
                                    public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                                        if (success) {
                                            if(peaklist!=null){
                                                if(peaklist.getRawFileIdentifier()!=null && !peaklist.getRawFileIdentifier().equalsIgnoreCase("")){
                                                    
                                                    ((RunInfoData) getData()).setMessage("Search " + peaklist.getRawFileIdentifier());
                                                    ((DefaultTreeModel) m_tree.getModel()).nodeChanged(xicRunNode);
                                                    searchPotentialRawFiles(peaklist.getRawFileIdentifier(), tableModel, Search.BASED_ON_IDENTIFIER);
                                                    
                                                }else if(peaklist.getPath()!=null && !peaklist.getRawFileIdentifier().equalsIgnoreCase("")){
                                                    
                                                    String searchString = peaklist.getPath();
                                                    
                                                    ((RunInfoData) getData()).setPeakListPath(searchString);

                                                    if ((searchString == null) || (searchString.isEmpty())) {
                                                        searchString = "*";
                                                    } else {
                                                        searchString = "*" + searchString + "*";
                                                    }

                                                    ((RunInfoData) getData()).setMessage("Search " + searchString);
                                                    ((RunInfoData) getData()).setStatus(RunInfoData.Status.MISSING);

                                                    ((DefaultTreeModel) m_tree.getModel()).nodeChanged(xicRunNode);

                                                    searchPotentialRawFiles(searchString, tableModel, Search.BASED_ON_PATH);
                                                    
                                                }
                                            }
                                        }
                                    }
                                };
                                
                                DatabaseLoadSinglePeaklist peaklistTask = new DatabaseLoadSinglePeaklist(callback, dataset.getId(), dataset.getProject().getId(), peaklist);
                                AccessDatabaseThread.getAccessDatabaseThread().addTask(peaklistTask);

                                //searchPeaklistRawFileIdentifier(dataset, tableModel);

                            }
                        }
                    } else {
                        // it failed !
                        //popup
                        m_treeModel.removeNodeFromParent((MutableTreeNode) xicRunNode.getParent());
                    }

                    if (tableModel != null) {
                        // a table model display data in this Xic Run Node, so it must be updated
                        tableModel.fireTableDataChanged();
                    }

                }
            };

            DatabaseRunsTask task = new DatabaseRunsTask(callback);
            task.initLoadRawFile(dataset.getId(), rawfileFounds, runOut);
            AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
        } else {
            if (tableModel != null) {
                searchPeaklistPath(dataset, tableModel);
            }
        }
    }


    private void searchPeaklistPath(DDataset dataset, final AbstractTableModel tableModel) {

        Long rsetId = dataset.getResultSetId();

        final String[] path = new String[1];

        final XICRunNode _this = this;

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                //setIsChanging(false);

                String searchString = path[0];
                ((RunInfoData) getData()).setPeakListPath(searchString);

                if ((searchString == null) || (searchString.isEmpty())) {
                    searchString = "*";
                } else {
                    searchString = "*" + searchString + "*";
                }

                ((RunInfoData) getData()).setMessage("Search " + searchString);
                ((RunInfoData) getData()).setStatus(RunInfoData.Status.MISSING);

                ((DefaultTreeModel) m_tree.getModel()).nodeChanged(_this);

                searchPotentialRawFiles(searchString, tableModel, Search.BASED_ON_PATH);
            }
        };

        // ask asynchronous loading of data
        Long projectId = dataset.getProject().getId();
        DatabaseRunsTask task = new DatabaseRunsTask(callback);
        task.initLoadPeakListPathForRset(projectId, rsetId, path);
        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);

    }

    private void searchPotentialRawFiles(String searchString, final AbstractTableModel tableModel, Search search) {

        final HashMap<String, RawFile> m_rawFileMap = new HashMap<String, RawFile>();

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                setIsChanging(false);

                RunInfoData runInfoData = ((RunInfoData) getData());

                if (m_rawFileMap.isEmpty()) {
                    runInfoData.setMessage("<html><font color='#FF0000'>Missing Raw File</font></html>");
                    runInfoData.setStatus(RunInfoData.Status.MISSING);
                } else if (m_rawFileMap.size() == 1) {
                    RawFile rawFile = m_rawFileMap.get(0);
                    runInfoData.setSelectedRawFile(rawFile);
                    runInfoData.setRun(rawFile.getRuns().get(0));

                    if (search == Search.BASED_ON_PATH) {
                        runInfoData.setStatus(RunInfoData.Status.SYSTEM_PROPOSED);
                    } else if (search == Search.BASED_ON_IDENTIFIER) {
                        runInfoData.setStatus(RunInfoData.Status.LAST_DEFINED);
                    }

                } else {
                    runInfoData.setPotentialRawFiles(m_rawFileMap);
                    if (searchString.equalsIgnoreCase("*")) {
                        runInfoData.setMessage("<html><font color='#FF0000'>Unavailable Peaklist</font></html>");
                        runInfoData.setStatus(RunInfoData.Status.MISSING);
                    } else {
                        runInfoData.setMessage("<html><font color='#FF0000'>Multiple Raw Files</font></html>");
                        runInfoData.setStatus(RunInfoData.Status.MISSING);
                    }
                }

                if (tableModel != null) {
                    tableModel.fireTableDataChanged();
                }
            }
        };

        // ask asynchronous loading of data
        DatabaseRunsTask task = new DatabaseRunsTask(callback);
        task.initSearchRawFile(searchString, m_rawFileMap);
        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
    }

    @Override
    public String toString() {
        AbstractData data = getData();
        if (data == null) {
            return "Loading";
        }
        return super.toString();
    }

    @Override
    public ImageIcon getIcon() {
        return getIcon(IconManager.IconType.FILE); //JPM.TODO : to be changed
    }

    @Override
    public Sheet createSheet() {
        return null;
    }

    @Override
    public AbstractNode copyNode() {
        return null;
    }

    @Override
    public void loadDataForProperties(Runnable callback) {
    }

    public void setRawFile(final File selectedFile, ActionListener doneCallback) {

        // we search the raw file in the database, if we found it, we set this one
        // if we do not find it, we use the one choosed by the user
        setIsChanging(true);
        m_treeModel.nodeChanged(this);

        String searchString = selectedFile.getName().substring(0, selectedFile.getName().lastIndexOf('.'));

        final HashMap<String, RawFile> m_rawFilesMap = new HashMap<String, RawFile>();
        final TreeNode _this = this;

        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                RunInfoData runInfoData = ((RunInfoData) getData());
                if (m_rawFilesMap.size() == 1) {

                    // we have found the raw file in the database, we use this one
                    RawFile rawFile = m_rawFilesMap.get(0);
                    runInfoData.setSelectedRawFile(rawFile);
                    runInfoData.setRun(rawFile.getRuns().get(0));

                } else {
                    runInfoData.setRawFileOnDisk(selectedFile);
                }
                setIsChanging(false);
                m_treeModel.nodeChanged(_this);

                if (doneCallback != null) {
                    doneCallback.actionPerformed(null);
                }

            }
        };

        // ask asynchronous loading of data
        DatabaseRunsTask task = new DatabaseRunsTask(callback);
        task.initSearchRawFile(searchString, m_rawFilesMap);
        AccessDatabaseThread.getAccessDatabaseThread().addTask(task);

    }

    public String getPeakListPath() {
        RunInfoData data = ((RunInfoData) getData());
        if (data == null) {
            return null;
        }
        return data.getPeakListPath();
    }

}
