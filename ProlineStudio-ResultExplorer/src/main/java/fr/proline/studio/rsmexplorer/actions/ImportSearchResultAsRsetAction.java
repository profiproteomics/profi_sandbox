package fr.proline.studio.rsmexplorer.actions;

import fr.proline.core.orm.uds.Project;
import fr.proline.studio.dpm.AccessServiceThread;
import fr.proline.studio.dpm.task.AbstractServiceCallback;
import fr.proline.studio.dpm.task.CertifyIdentificationTask;
import fr.proline.studio.dpm.task.ImportIdentificationTask;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.rsmexplorer.gui.dialog.ImportIdentificationDialog;
import fr.proline.studio.rsmexplorer.node.RSMAllImportedNode;
import fr.proline.studio.rsmexplorer.node.RSMNode;
import fr.proline.studio.rsmexplorer.node.RSMProjectNode;
import fr.proline.studio.rsmexplorer.node.IdentificationTree;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultTreeModel;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 * Action to import one or multiple identification
 * @author JM235353
 */
public class ImportSearchResultAsRsetAction extends AbstractRSMAction {

    private static int m_beingImportedNumber = 0;
    
    private static HashMap<Long, ArrayList<ChangeListener>> m_listenerMap = new HashMap<>();
    
    public ImportSearchResultAsRsetAction() {
        super(NbBundle.getMessage(ImportSearchResultAsRsetAction.class, "CTL_AddSearchResult"));
    }
    
    @Override
    public void actionPerformed(RSMNode[] selectedNodes, int x, int y) {
        
        // only RSMAllImportedNode selected for this action
        final RSMAllImportedNode allImportedNode = (RSMAllImportedNode) selectedNodes[0];
        
        RSMProjectNode projectNode = (RSMProjectNode) allImportedNode.getParent();
        Project project =  projectNode.getProject();
        
        
        ImportIdentificationDialog dialog = ImportIdentificationDialog.getDialog(WindowManager.getDefault().getMainWindow());
        dialog.setLocation(x, y);
        dialog.setVisible(true);
        
        if (dialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
            
             // retrieve parameters
            final File[] filePaths = dialog.getFilePaths();
            final HashMap<String, String> parserArguments = dialog.getParserArguments();
            

            
            final String parserId = dialog.getParserId();
            final String decoyRegex = dialog.getDecoyRegex();
            final long instrumentId = dialog.getInstrumentId();
            final long peaklistSoftwareId = dialog.getPeaklistSoftwareId();
            final boolean saveSpectrumMatches = dialog.getSaveSpectrumMatches();
            
            IdentificationTree tree = IdentificationTree.getCurrentTree();
            final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            
            allImportedNode.setIsChanging(true);
            treeModel.nodeChanged(allImportedNode);
            
            final Long projectId = project.getId();


            // --------------------- Pre-Import --------------------------

            int nbFiles = filePaths.length;
            String[] canonicalPathArray = new String[nbFiles];
            for (int i = 0; i < nbFiles; i++) {
                File f = filePaths[i];

                // use canonicalPath when it is possible to be sure to have an unique path
                String canonicalPath;
                try {
                    canonicalPath = f.getCanonicalPath();
                } catch (IOException ioe) {
                    canonicalPath = f.getAbsolutePath(); // should not happen
                }
                canonicalPathArray[i] = canonicalPath;
            }

 
            final String[] result = new String[1];
            
            AbstractServiceCallback callback = new AbstractServiceCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success) {

                    if (success) {
                        // start imports
                        startImport(projectId, allImportedNode, filePaths, treeModel, parserId, parserArguments, decoyRegex, instrumentId, peaklistSoftwareId, saveSpectrumMatches  );
                    } else {
                        allImportedNode.setIsChanging(false);
                        treeModel.nodeChanged(allImportedNode);
                    }
                }
            };

            CertifyIdentificationTask task = new CertifyIdentificationTask(callback, parserId, parserArguments, canonicalPathArray, projectId, result);
            AccessServiceThread.getAccessServiceThread().addTask(task);



            
            
        }
    }
    
    private void startImport(final Long projectId, final RSMAllImportedNode allImportedNode, File[] filePaths, final DefaultTreeModel treeModel, String parserId, HashMap<String, String> parserArguments, String decoyRegex, long instrumentId, long peaklistSoftwareId,  boolean saveSpectrumMatches ) {



        AbstractServiceCallback callback = new AbstractServiceCallback() {

            @Override
            public boolean mustBeCalledInAWT() {
                return true;
            }

            @Override
            public void run(boolean success) {

                fireListener(projectId);

                m_beingImportedNumber--;
                if (m_beingImportedNumber == 0) {
                    allImportedNode.setIsChanging(false);
                    treeModel.nodeChanged(allImportedNode);
                }
            }
        };


        // Start identification for each file
        int nbFiles = filePaths.length;
        m_beingImportedNumber += nbFiles;
        for (int i = 0; i < nbFiles; i++) {
            File f = filePaths[i];

            // use canonicalPath when it is possible to be sure to have an unique path
            String canonicalPath;
            try {
                canonicalPath = f.getCanonicalPath();
            } catch (IOException ioe) {
                canonicalPath = f.getAbsolutePath(); // should not happen
            }
            Long[] resultSetId = new Long[1];


            ImportIdentificationTask task = new ImportIdentificationTask(callback, parserId, parserArguments, canonicalPath, decoyRegex, instrumentId, peaklistSoftwareId, saveSpectrumMatches, projectId, resultSetId);
            AccessServiceThread.getAccessServiceThread().addTask(task);
        }
    }
    
    @Override
    public void updateEnabled(RSMNode[] selectedNodes) {
        
        int nbSelectedNodes = selectedNodes.length;

        // identification must be added only in "All imported Node"
        if (nbSelectedNodes != 1) {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }
    
    
    public static synchronized void addEventListener(Long key, ChangeListener l) {
        ArrayList<ChangeListener> listeners = m_listenerMap.get(key);
        if (listeners == null) {
            listeners = new ArrayList<>(1);
            m_listenerMap.put(key, listeners);
        }
        listeners.add(l);
    }
    public static synchronized void removeEventListener(Long key, ChangeListener l) {
        ArrayList<ChangeListener> listeners = m_listenerMap.get(key);
        if (listeners == null) {
            return;
        }
        listeners.remove(l);
        if (listeners.isEmpty()) {
            m_listenerMap.remove(key);
        }
    }
    public static synchronized void fireListener(Long key) {
        
        ArrayList<ChangeListener> listeners = m_listenerMap.get(key);
        if (listeners == null) {
            return;
        }
        
        int nb = listeners.size();
        for (int i=0;i<nb;i++) {
            listeners.get(i).stateChanged(null);
        }
    }
    
}
