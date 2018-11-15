/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui.dialog.exporter;

import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.settings.FilePreferences;
import fr.proline.studio.settings.SettingsUtils;
import fr.proline.studio.utils.IconManager;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;

/**
 *
 * @author VD225637
 */
public class Export2MzIdentMLDialog  extends DefaultDialog {
    protected final static String MZIDENT_SETTINGS_KEY = "Export2MzIdentML";

    private static final int STEP_PANEL_EXPORT_PARAM_DEF = 0;
    private static final int STEP_PANEL_FILE_CHOOSER = 1;
    private int m_step = STEP_PANEL_EXPORT_PARAM_DEF;
    
    private DefaultDialog.ProgressTask m_task = null; //VDS ? 
    private Export2MzIdentMLParamPanel m_paramPanel; 
    private Export2MzIdentMLFilePanel m_filePanel;
    
    public Export2MzIdentMLDialog(Window parent) {
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        setTitle("Export to MzIdentML format");   
        setResizable(true);
        
//        setDocumentationSuffix(m_contact_FN_key); //VDS TODO
        setButtonName(BUTTON_OK, "Next");
        setButtonIcon(DefaultDialog.BUTTON_OK, IconManager.getIcon(IconManager.IconType.ARROW));
        setButtonVisible(BUTTON_LOAD, true);
        setButtonVisible(BUTTON_SAVE, true);
        
        m_paramPanel= new Export2MzIdentMLParamPanel(this);
        setInternalComponent(m_paramPanel);
    }
    
    public void setTask(DefaultDialog.ProgressTask task) {
        m_task = task;
    }
        
    @Override
    protected boolean okCalled() {
         if (m_step == STEP_PANEL_EXPORT_PARAM_DEF) {
            if(!m_paramPanel.checkParameters())
                return false;
             
            m_paramPanel.saveParameters(null);
             
            // change to ok button before call to last panel
            setButtonName(BUTTON_OK, "OK");            
            setButtonIcon(BUTTON_OK, IconManager.getIcon(IconManager.IconType.OK));

            setButtonVisible(BUTTON_LOAD, false);
            setButtonVisible(BUTTON_SAVE, false);
            
            m_filePanel = new Export2MzIdentMLFilePanel(this);
            replaceInternalComponent(m_filePanel);
            revalidate();
            repaint();
            m_step = STEP_PANEL_FILE_CHOOSER;    
            
            return false;
         } else {
            if (!m_filePanel.checkParameters()) {
                return false;
            } 
            startTask(m_task);
            return false;
         }                 
    }
    

    
    @Override
    protected boolean loadCalled() {
        if (m_step == STEP_PANEL_EXPORT_PARAM_DEF) {
            m_paramPanel.loadParameters();
        }
        return false;
    }   

    @Override
    protected boolean saveCalled() {

        if (m_step == STEP_PANEL_EXPORT_PARAM_DEF) {

            // check parameters 
            if(!m_paramPanel.checkParameters())
                return false;

            JFileChooser fileChooser = SettingsUtils.getFileChooser(MZIDENT_SETTINGS_KEY);
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile();
                FilePreferences filePreferences = new FilePreferences(f, null, "");

                m_paramPanel.saveParameters(filePreferences);
                SettingsUtils.addSettingsPath(MZIDENT_SETTINGS_KEY, f.getAbsolutePath());
                SettingsUtils.writeDefaultDirectory(MZIDENT_SETTINGS_KEY, f.getParent());
            }
        }
        return false;
    }
    
    public HashMap<String, Object> getExportParams() {
        if(m_paramPanel != null)
            return m_paramPanel.getExportParams();
        else 
            return null;       
    }
    
     public String getFileName() {
        if(m_filePanel != null)
            return m_filePanel.getFileName();
        else
            return null;
    }
}