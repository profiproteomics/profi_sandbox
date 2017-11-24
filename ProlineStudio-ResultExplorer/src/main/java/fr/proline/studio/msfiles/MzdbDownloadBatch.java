/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.msfiles;

import fr.proline.studio.dpm.AccessJMSManagerThread;
import fr.proline.studio.dpm.task.jms.AbstractJMSCallback;
import fr.proline.studio.dpm.task.jms.DownloadMzdbTask;
import fr.proline.studio.rsmexplorer.MzdbFilesTopComponent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.tree.TreePath;

/**
 *
 * @author AK249877
 */
public class MzdbDownloadBatch implements Runnable {

    private TreePath m_pathToExpand;
    private final String m_localURL;
    private final ArrayList<File> m_files;
    private final String m_root;
    private MsListener m_listener;
    private ArrayList<MsListenerParameter> m_list;
    
    public MzdbDownloadBatch(ArrayList<File> files, TreePath pathToExpand, String root) {
        m_files = files;
        m_pathToExpand = pathToExpand;
        m_root = root;

        m_list = new ArrayList<MsListenerParameter>();

        StringBuilder temp = new StringBuilder();

        Object elements[] = pathToExpand.getPath();
        for (int i = 0, n = elements.length; i < n; i++) {
            temp.append(elements[i]).append("\\");
        }

        m_localURL = temp.toString();
    }

    public MzdbDownloadBatch(ArrayList<File> files, String localURL, String root) {
        m_files = files;
        m_root = root;

        m_localURL = localURL;
        
        m_list = new ArrayList<MsListenerParameter>();
    }

    public void addMsListener(MsListener listener) {
        m_listener = listener;
    }

    private void download(File remoteFile) {
        
        if (remoteFile.getAbsolutePath().toLowerCase().endsWith(".mzdb")) {

            AbstractJMSCallback callback = new AbstractJMSCallback() {

                @Override
                public boolean mustBeCalledInAWT() {
                    return true;
                }

                @Override
                public void run(boolean success) {

                    if (success) {
                        if (m_pathToExpand != null) {
                            MzdbFilesTopComponent.getExplorer().getLocalFileSystemView().updateTree();
                        }
                        m_list.add(new MsListenerParameter(remoteFile, true));
                    } else {
                        m_list.add(new MsListenerParameter(remoteFile, false));
                    }

                    if (m_listener != null) {
                        if (m_list.size() == m_files.size()) { 
                            m_listener.downloadPerformed(m_list);
                        }
                    }
                }
            };

            DownloadMzdbTask task = new DownloadMzdbTask(callback, m_root + m_localURL, remoteFile.getAbsolutePath());

            AccessJMSManagerThread.getAccessJMSManagerThread().addTask(task);

        }
    }

    @Override
    public void run() {
        if (m_pathToExpand != null) {
            MzdbFilesTopComponent.getExplorer().getLocalFileSystemView().expandTreePath(m_pathToExpand);
        }
        for (int i = 0; i < m_files.size(); i++) {
            download(m_files.get(i));
        }
    }

}
