package fr.proline.studio.rsmexplorer.actions.identification;

import fr.proline.studio.dpm.jms.AccessJMSManagerThread;
import fr.proline.studio.dpm.task.jms.AbstractJMSCallback;
import fr.proline.studio.dpm.task.jms.DownloadFileTask;
import fr.proline.studio.dpm.task.jms.ExportRSMTask;
import fr.proline.studio.export.ExportDialog;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.rsmexplorer.tree.AbstractNode;
import fr.proline.studio.rsmexplorer.tree.AbstractTree;
import fr.proline.studio.rsmexplorer.tree.DataSetNode;
import java.util.HashMap;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author VD225637
 */
public class ExportSpectraListJMSAction extends AbstractRSMAction {
 
    protected static final Logger m_logger = LoggerFactory.getLogger("ProlineStudio.ResultExplorer");


    public ExportSpectraListJMSAction(AbstractTree.TreeType treeType) {
        super(NbBundle.getMessage(ExportSpectraListJMSAction.class, "CTL_ExportSpectraListAction"), treeType);
    }
    
    public ExportSpectraListJMSAction(AbstractTree.TreeType treeType, boolean exportTitle) {
        super(NbBundle.getMessage(ExportSpectraListJMSAction.class, "CTL_ExportAction")+" "+NbBundle.getMessage(ExportSpectraListJMSAction.class, "CTL_ExportSpectraListAction") , treeType);
    }

    @Override
    public void actionPerformed(final AbstractNode[] selectedNodes, final int x, final int y) {

        final DataSetNode dataSetNode = (DataSetNode) selectedNodes[0];
        final ExportDialog  dialog = ExportDialog.getDialog(WindowManager.getDefault().getMainWindow(), false);

//        final LoadWaitingDialog loadWaitingDialog = new LoadWaitingDialog(WindowManager.getDefault().getMainWindow());

        DefaultDialog.ProgressTask task = new DefaultDialog.ProgressTask() {
            @Override
            public int getMinValue() {
                return 0;
            }

            @Override 
            public int getMaxValue() {
                return 100;
            }

            @Override
            protected Object doInBackground() throws Exception {
                final AbstractJMSCallback downloadCallback = new AbstractJMSCallback() {

                    @Override
                    public boolean mustBeCalledInAWT() {
                        return true;
                    }

                    @Override
                    public void run(boolean success) {
                        if (success) {

                            setProgress(100);

                        } else {
                                    // nothing to do
                            // failed
                            setProgress(100);
                        }
                    }
                };

                // used as out parameter for the service
                final String[] _filePath = new String[1];
                final String[] _jmsNodeId = new String[1];

                AbstractJMSCallback exportCallback = new AbstractJMSCallback() {

                    @Override
                    public boolean mustBeCalledInAWT() {
                        return true;
                    }

                    @Override
                    public void run(boolean success) {
                        if (success) {

                            String fileName = dialog.getFileName();
                            if (!fileName.endsWith(".tsv") && !fileName.endsWith(".TSV")) {
                                fileName += ".tsv";
                            }
                            if (_filePath.length == 1) {
                                DownloadFileTask task = new DownloadFileTask(downloadCallback, fileName, _filePath[0], _jmsNodeId[0]);
                                AccessJMSManagerThread.getAccessJMSManagerThread().addTask(task);
                            }                            

                        } else {
                            // nothing to do
                            // failed
                            setProgress(100);
                        }
                    }
                };

                HashMap<String,Object> exportConfig = new HashMap<>();
                ExportRSMTask task = new ExportRSMTask(exportCallback, dataSetNode.getDataset(), false, _filePath, _jmsNodeId, ExportRSMTask.ExporterFormat.SPECTRA_LIST, exportConfig);
                AccessJMSManagerThread.getAccessJMSManagerThread().addTask(task);

                return null;
            }
        };

        dialog.setTask(task);
        dialog.setLocation(x, y);
        dialog.setVisible(true);        
    }
    
//    public String getDatasetName(String fileName, AbstractNode[] selectedNodes){
//        int id0 = fileName.indexOf("-");
//        int id1 = fileName.lastIndexOf("_");
//        if (id0 > -1 && id1 > -1&& id0<id1){
//            String dsIdStr = fileName.substring(id0+1, id1);
//            try{
//                Long dsId  = Long.parseLong(dsIdStr);
//                for (AbstractNode node : selectedNodes) {
//                    if (((DataSetNode)node).getDataset().getId() == dsId){
//                        return "_"+((DataSetNode)node).getDataset().getName()+"_";
//                    }
//                }
//            }catch(NumberFormatException e){
//                
//            }
//            
//        }
//        return "";
//    }

    @Override
    public void updateEnabled(AbstractNode[] selectedNodes) {

        int nbSelectedNodes = selectedNodes.length;

        if (nbSelectedNodes != 1) {
            setEnabled(false);
            return;
        }

        AbstractNode node = selectedNodes[0];
        AbstractNode.NodeTypes nodeType = node.getType();
        if (nodeType != AbstractNode.NodeTypes.DATA_SET && node.getType() != AbstractNode.NodeTypes.BIOLOGICAL_SAMPLE_ANALYSIS) {
            setEnabled(false);
            return;
        }
       
        if (node.isChanging()) {
            setEnabled(false);
            return;
        }



        // We can only export a RSM
        DataSetNode datasetNode = (DataSetNode) node;
        if (!datasetNode.hasResultSummary()) {
            setEnabled(false);
            return;
        }
            
        setEnabled(true);
    }
}