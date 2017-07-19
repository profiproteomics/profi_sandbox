/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.msfiles;

import fr.proline.studio.rsmexplorer.MzdbFilesTopComponent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.tree.TreePath;

/**
 *
 * @author AK249877
 */
public class MzdbUploadBatch implements Runnable, ConversionListener {

    private final ThreadPoolExecutor m_executor;
    private final HashMap<File, MzdbUploadSettings> m_uploads;
    private TreePath m_pathToExpand;
    private int m_successfulUploads, m_failedUploads;
    private HashSet<String> m_directories;

    public MzdbUploadBatch(HashMap<File, MzdbUploadSettings> uploads) {
        m_uploads = uploads;
        m_executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        m_successfulUploads = 0;
        m_failedUploads = 0;
    }

    public MzdbUploadBatch(HashMap<File, MzdbUploadSettings> uploads, TreePath pathToExpand) {
        this(uploads);
        m_pathToExpand = pathToExpand;
    }

    public TreePath getPathToExpand() {
        return m_pathToExpand;
    }

    public void upload(File f, MzdbUploadSettings uploadSettings) {
        if (f.getAbsolutePath().toLowerCase().endsWith(".mzdb")) {
            MzdbUploader uploader = new MzdbUploader(f, uploadSettings);
            uploader.addUploadListener(this);
            m_executor.execute(uploader);
        }
    }

    @Override
    public void run() {

        m_directories = new HashSet<String>();

        Iterator it = m_uploads.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            File f = (File) pair.getKey();
            MzdbUploadSettings settings = (MzdbUploadSettings) pair.getValue();

            if (m_pathToExpand == null) {

                if (!settings.getDestination().equalsIgnoreCase("")) {
                    if (settings.getDestination().startsWith(File.separator)) {
                        m_directories.add(settings.getDestination().substring(1));
                    } else {
                        m_directories.add(settings.getDestination());
                    }
                }

            }

            upload(f, settings);
        }

    }

    @Override
    public void conversionPerformed(File f, Object settings, boolean success) {
        if (success) {
            m_successfulUploads++;
        } else {
            m_failedUploads++;
        }

        if ((m_successfulUploads + m_failedUploads) == m_uploads.size()) {
            
            m_executor.shutdown();
            try {
                m_executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                ;
            }

            if (m_pathToExpand != null) {
                MzdbFilesTopComponent.getExplorer().getTreeFileChooserPanel().expandTreePath(m_pathToExpand);
            } else {
                MzdbFilesTopComponent.getExplorer().getTreeFileChooserPanel().expandMultipleTreePath(m_directories, m_uploads.entrySet().iterator().next().getValue().getMountLabel());
            }
            MzdbFilesTopComponent.getExplorer().getTreeFileChooserPanel().updateTree();
        }
    }

}
