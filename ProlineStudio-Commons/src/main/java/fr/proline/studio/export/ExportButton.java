package fr.proline.studio.export;

import fr.proline.studio.progress.ProgressBarDialog;
import fr.proline.studio.progress.ProgressInterface;
import fr.proline.studio.utils.IconManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTable;
import org.openide.windows.WindowManager;

/**
 * Button to export data of a table or image of a JPanel
 * @author JM235353
 */
public class ExportButton extends JButton implements ActionListener {

    private String m_exportName;
    private JXTable m_table = null;
    private JPanel m_panel = null;
    private ExportPictureWrapper m_fileWrapper = null;
    
    private ProgressInterface m_progressInterface = null;
            
    public ExportButton(ProgressInterface progressInterface, String exportName, JXTable table) {

        m_progressInterface = progressInterface;
        
        m_exportName = exportName;
        m_table = table;
        
        setIcon(IconManager.getIcon(IconManager.IconType.EXPORT));
        setToolTipText("Export Data...");

        addActionListener(this);
    }
    
    public ExportButton(String exportName, JPanel panel, ExportPictureWrapper fileWrapper) {


        m_exportName = exportName;
        m_panel = panel;
        m_fileWrapper = fileWrapper;

        setIcon(IconManager.getIcon(IconManager.IconType.EXPORT_IMAGE));
        setToolTipText("Export Image...");

        addActionListener(this);
    }
    
    public ExportButton(String exportName, JPanel panel) {


        m_exportName = exportName;
        m_panel = panel;
        m_fileWrapper = null;

        setIcon(IconManager.getIcon(IconManager.IconType.EXPORT_IMAGE));
        setToolTipText("Export Image...");

        addActionListener(this);
    }
    
//    public ExportButton(String exportName, ExportPictureWrapper fileWrapper) {
//
//
//        m_exportName = exportName;
//        //m_panel = panel;
//        m_fileWrapper = fileWrapper;
//
//        setIcon(IconManager.getIcon(IconManager.IconType.EXPORT_IMAGE));
//        setToolTipText("Export svg Image...");
//
//        addActionListener(this);
//    }
    

    @Override
    public void actionPerformed(ActionEvent e) {

        
        if ((m_progressInterface != null) && (!m_progressInterface.isLoaded())) {

            ProgressBarDialog dialog = ProgressBarDialog.getDialog(WindowManager.getDefault().getMainWindow(), m_progressInterface, "Data loading", "Export is not available while data is loading. Please Wait.");
            dialog.setLocation(getLocationOnScreen().x + getWidth() + 5, getLocationOnScreen().y + getHeight() + 5);
            dialog.setVisible(true);

            if (!dialog.isWaitingFinished()) {
                return;
            }
        }

        ExportDialog dialog;
        
        if (m_table != null) {
            dialog = ExportDialog.getDialog(WindowManager.getDefault().getMainWindow(), m_table, m_exportName);
        }

        else if (m_fileWrapper == null){ // then png output only
            dialog = ExportDialog.getDialog(WindowManager.getDefault().getMainWindow(), m_panel, m_exportName);
        }
        else { 
            dialog = ExportDialog.getDialog(WindowManager.getDefault().getMainWindow(), m_panel,m_fileWrapper, m_exportName);
        }
        
        dialog.setLocation(getLocationOnScreen().x + getWidth() + 5, getLocationOnScreen().y + getHeight() + 5);
        dialog.setVisible(true);

    }
}
