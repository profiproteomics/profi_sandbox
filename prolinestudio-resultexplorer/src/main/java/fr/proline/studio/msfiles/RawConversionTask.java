/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.msfiles;

import fr.proline.studio.dam.taskinfo.TaskError;
import fr.proline.studio.dam.taskinfo.TaskInfo;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.AbstractDatabaseTask;
import java.io.File;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * @author AK249877
 */
public class RawConversionTask extends AbstractDatabaseTask {

    private final File m_file;
    private Process m_process = null;
    private ConversionSettings m_settings;

    public RawConversionTask(AbstractDatabaseCallback callback, File file, ConversionSettings settings) {
        super(callback, new TaskInfo("Convert .raw file " + file.getAbsolutePath(), false, "Generic Task", TaskInfo.INFO_IMPORTANCE_MEDIUM));
        m_file = file;
        m_settings = settings;
    }

    @Override
    public boolean fetchData() {
        try {

            String suffix = null;

            if (m_file.getAbsolutePath().endsWith(".RAW")) {
                suffix = ".RAW";
            } else if (m_file.getAbsolutePath().endsWith(".raw")) {
                suffix = ".raw";
            } else if (m_file.getAbsolutePath().endsWith(".WIFF")) {
                suffix = ".WIFF";
            } else if (m_file.getAbsolutePath().endsWith(".wiff")) {
                suffix = ".wiff";
            }

            m_process = new ProcessBuilder(m_settings.getConverterPath(), "-i", m_file.getAbsolutePath(), "-o", m_settings.getOutputPath() + File.separator + m_file.getName().substring(0, m_file.getName().lastIndexOf(suffix)) + ".mzdb").start();

        } catch (IOException ex) {
            if (m_process != null && m_process.isAlive()) {
                m_process.destroy();
            }
            m_taskError = new TaskError("Raw Conversion Error", "An IOException was encountered.");
            return false;
        }

        while (m_process != null && m_process.isAlive()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        if (m_process.exitValue() == 0) {

            if (m_settings.getDeleteRaw()) {
                FileUtility.deleteFile(m_file);
            }

        } else {
            if (m_process != null && m_process.isAlive()) {
                m_process.destroy();
            }
            m_taskError = new TaskError("Raw Conversion Error", "Process abnormal exit.");
            return false;
        }
        return true;
    }

    @Override
    public boolean needToFetch() {
        return true;
    }

}
