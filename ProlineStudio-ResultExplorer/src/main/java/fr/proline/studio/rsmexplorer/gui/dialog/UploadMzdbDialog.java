/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui.dialog;

import fr.proline.studio.gui.DefaultDialog;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_CANCEL;
import static fr.proline.studio.gui.DefaultDialog.BUTTON_OK;
import fr.proline.studio.parameter.BooleanParameter;
import fr.proline.studio.parameter.ParameterError;
import fr.proline.studio.parameter.ParameterList;
import fr.proline.studio.utils.IconManager;
import fr.proline.studio.wizard.UploadBatch;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.util.NbPreferences;

/**
 *
 * @author AK249877
 */
public class UploadMzdbDialog extends DefaultDialog {

    private static UploadMzdbDialog m_singletonDialog = null;
    private JList m_fileList;
    private JScrollPane m_fileListScrollPane;
    private JButton m_addFileButton, m_removeFileButton;
    private ParameterList m_parameterList;
    private BooleanParameter m_parameter;

    public static UploadMzdbDialog getDialog(Window parent) {
        if (m_singletonDialog == null) {
            m_singletonDialog = new UploadMzdbDialog(parent);
        }

        return m_singletonDialog;
    }

    public UploadMzdbDialog(Window parent) {
        super(parent, Dialog.ModalityType.MODELESS);

        setTitle("Upload mzDB file(s)");

        setSize(new Dimension(360, 480));
        setResizable(true);

        this.setHelpURL("");

        setButtonVisible(BUTTON_CANCEL, true);
        setButtonName(BUTTON_OK, "OK");
        setStatusVisible(true);

        setInternalComponent(this.createInternalComponent());

    }

    private Component createInternalComponent() {
        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new BorderLayout());
        internalPanel.add(createFileSelectionPanel(), BorderLayout.CENTER);
        internalPanel.add(createParameterPanel(), BorderLayout.SOUTH);
        return internalPanel;
    }

    private JPanel createParameterPanel() {
        m_parameterList = new ParameterList("mzDB Settings");
        JCheckBox checkbox = new JCheckBox("Delete mzdb file after a successful upload");
        m_parameter = new BooleanParameter("Delete_mzdb_file_after_a_successful_upload", "Delete mzdb file after a successful upload", checkbox, false);
        m_parameterList.add(m_parameter);
        m_parameterList.loadParameters(NbPreferences.root());
        return m_parameterList.getPanel();
    }

    private JPanel createFileSelectionPanel() {

        // Creation of Objects for File Selection Panel
        JPanel fileSelectionPanel = new JPanel(new GridBagLayout());
        fileSelectionPanel.setBorder(BorderFactory.createTitledBorder(" Files Selection "));

        m_fileList = new JList<>(new DefaultListModel());
        m_fileListScrollPane = new JScrollPane(m_fileList) {

            private Dimension preferredSize = new Dimension(360, 200);

            @Override
            public Dimension getPreferredSize() {
                return preferredSize;
            }
        };

        m_addFileButton = new JButton(IconManager.getIcon(IconManager.IconType.OPEN_FILE));
        m_addFileButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        m_removeFileButton = new JButton(IconManager.getIcon(IconManager.IconType.ERASER));
        m_removeFileButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        // Placement of Objects for File Selection Panel
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        fileSelectionPanel.add(m_fileListScrollPane, c);

        c.gridx++;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        fileSelectionPanel.add(m_addFileButton, c);

        c.gridy++;
        fileSelectionPanel.add(m_removeFileButton, c);

        c.gridy++;
        fileSelectionPanel.add(Box.createVerticalStrut(30), c);

        // Actions on objects
        m_fileList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean sometingSelected = (m_fileList.getSelectedIndex() != -1);
                m_removeFileButton.setEnabled(sometingSelected);
            }
        });

        m_addFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fchooser = new JFileChooser();

                fchooser.setMultiSelectionEnabled(true);

                fchooser.addChoosableFileFilter(new FileNameExtensionFilter(".mzdb", "mzDB"));
                fchooser.setAcceptAllFileFilterUsed(false);

                //put the one and only filter here! (.mzdb)
                int result = fchooser.showOpenDialog(m_singletonDialog);
                if (result == JFileChooser.APPROVE_OPTION) {

                    File[] files = fchooser.getSelectedFiles();
                    int nbFiles = files.length;
                    for (int i = 0; i < nbFiles; i++) {
                        ((DefaultListModel) m_fileList.getModel()).addElement(files[i]);
                    }
                }
            }
        });

        m_removeFileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<File> selectedValues = m_fileList.getSelectedValuesList();
                Iterator<File> it = selectedValues.iterator();
                while (it.hasNext()) {
                    ((DefaultListModel) m_fileList.getModel()).removeElement(it.next());
                }
                m_removeFileButton.setEnabled(false);
            }
        });

        return fileSelectionPanel;

    }

    @Override
    protected boolean okCalled() {
        if (m_fileList.getModel().getSize() == 0) {
            setStatus(true, "No files are selected.");
            highlight(m_fileList);
            return false;
        }
        ParameterError error = m_parameterList.checkParameters();
        if (error != null) {
            setStatus(true, error.getErrorMessage());
            highlight(error.getParameterComponent());
            return false;
        }
        m_parameterList.saveParameters(NbPreferences.root());
        
        ArrayList<File> mzdbFiles = new ArrayList<File>();
        for(int i=0; i<m_fileList.getModel().getSize(); i++){
            mzdbFiles.add((File) m_fileList.getModel().getElementAt(i));
        }
        
        UploadBatch uploadBatch = new UploadBatch(mzdbFiles, true);
        
        return true;

    }

    @Override
    protected boolean cancelCalled() {
        return true;
    }

}
