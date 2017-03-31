/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui;

import fr.proline.mzscope.utils.IPopupMenuDelegate;
import fr.proline.studio.mzscope.MzdbInfo;
import fr.proline.studio.pattern.MzScopeWindowBoxManager;
import fr.proline.studio.rsmexplorer.gui.dialog.ConvertRawDialog;
import fr.proline.studio.rsmexplorer.gui.dialog.UploadMzdbDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.openide.windows.WindowManager;

/**
 *
 * @author AK249877
 */
public class LocalFileSystemView extends JPanel implements IPopupMenuDelegate {

    private LocalFileSystemModel m_fileSystemDataModel;
    private JTree m_tree;
    private JPopupMenu m_popupMenu;
    private JMenuItem m_detectPeakelsItem, m_viewRawFileItem, m_convertRawFileItem, m_uploadMzdbFileItem;
    private ActionListener viewRawFileAction;
    private ArrayList<File> m_selectedFiles;
    private final LocalFileSystemTransferHandler m_transferHandler;

    public LocalFileSystemView(LocalFileSystemTransferHandler transferHandler) {
        m_transferHandler = transferHandler;
        initComponents();
    }

    private void initComponents() {
                
        m_selectedFiles = new ArrayList<File>();

        setBorder(BorderFactory.createTitledBorder("Local Site"));
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 0;

        File[] roots = File.listRoots();
        JComboBox rootsComboBox = new JComboBox(roots);

        rootsComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent ie) {
                if (m_fileSystemDataModel != null) {
                    m_tree.setModel(null);
                    m_fileSystemDataModel = new LocalFileSystemModel(rootsComboBox.getSelectedItem().toString());
                    m_tree.setModel(m_fileSystemDataModel);
                }
            }

        });

        add(rootsComboBox, c);

        c.weighty = 1;
        c.weightx = 1;
        c.gridy++;

        m_popupMenu = new JPopupMenu();
        initPopupMenu(m_popupMenu);

        m_fileSystemDataModel = new LocalFileSystemModel(roots[0].getAbsolutePath());
        m_tree = new JTree(m_fileSystemDataModel);
        
        m_tree.setTransferHandler(m_transferHandler);
        m_tree.setDragEnabled(true);

        m_tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    updatePopupMenu();
                    m_popupMenu.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(m_tree);
        add(scrollPane, c);

    }
    
    private ArrayList<String> getSelectedURLs() {
        
        m_selectedFiles.clear();
        
        ArrayList<String> selectedURLs = new ArrayList<String>();
        TreePath[] paths = m_tree.getSelectionPaths();
        for (TreePath path : paths) {
            selectedURLs.add(path.getLastPathComponent().toString());
            m_selectedFiles.add(new File(path.getLastPathComponent().toString()));
        }
        return selectedURLs;
    }

    private boolean isSelectionHomogeneous(ArrayList<String> selectedURLs) {
        if (selectedURLs.size() > 0) {
            String firstSuffix = selectedURLs.get(0).substring(selectedURLs.get(0).lastIndexOf("."));
            for (String url : selectedURLs) {
                if (!url.endsWith(firstSuffix)) {
                    return false;
                }
            }
        }
        return true;

    }

    private void displayRaw(File rawfile) {
        if (rawfile != null) {
            MzScope mzScope = new MzScope(MzdbInfo.MZSCOPE_VIEW, rawfile);
            MzScopeWindowBoxManager.addMzdbScope(mzScope);
        }
    }

    private void displayRaw(ArrayList<File> rawfiles) {
        MzScope mzScope = new MzScope(MzdbInfo.MZSCOPE_VIEW, rawfiles);
        MzScopeWindowBoxManager.addMzdbScope(mzScope);
    }

    private void detectPeakels(ArrayList<File> rawfiles) {
        MzScope mzScope = new MzScope(MzdbInfo.MZSCOPE_DETECT_PEAKEL, rawfiles);
        MzScopeWindowBoxManager.addMzdbScope(mzScope);
    }

    @Override
    public void initPopupMenu(JPopupMenu popupMenu) {

        // view data
        viewRawFileAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                displayRaw(m_selectedFiles);
            }
        };
        m_viewRawFileItem = new JMenuItem();
        m_viewRawFileItem.setText("View");
        m_viewRawFileItem.addActionListener(viewRawFileAction);
        popupMenu.add(m_viewRawFileItem);

        // detect peakels
        m_detectPeakelsItem = new JMenuItem();
        m_detectPeakelsItem.setText("Detect Peakels");
        m_detectPeakelsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                detectPeakels(m_selectedFiles);
            }
        });
        popupMenu.add(m_detectPeakelsItem);

        // convert raw file
        m_convertRawFileItem = new JMenuItem();
        m_convertRawFileItem.setText("Convert to mzDB");
        m_convertRawFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ConvertRawDialog dialog = ConvertRawDialog.getDialog(null);        
                dialog.setFiles(m_selectedFiles);
                dialog.setVisible(true);
            }
        });
        popupMenu.add(m_convertRawFileItem);

        // upload mzdb file
        m_uploadMzdbFileItem = new JMenuItem();
        m_uploadMzdbFileItem.setText("Upload");
        m_uploadMzdbFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                UploadMzdbDialog dialog = UploadMzdbDialog.getDialog(WindowManager.getDefault().getMainWindow());        
                dialog.setFiles(m_selectedFiles);
                dialog.setVisible(true);
            }
        });
        popupMenu.add(m_uploadMzdbFileItem);

    }

    @Override
    public void updatePopupMenu() {
        ArrayList<String> selectedURLs = getSelectedURLs();
        if (isSelectionHomogeneous(selectedURLs)) {
            String firstURL = selectedURLs.get(0).toLowerCase();
            if (firstURL.endsWith(".mzdb") || firstURL.endsWith(".wiff")) {
                setPopupEnabled(true);
                m_convertRawFileItem.setEnabled(false);
            } else if (firstURL.endsWith(".raw")) {
                setPopupEnabled(false);
                m_convertRawFileItem.setEnabled(true);
            } else {
                setPopupEnabled(false);
            }
        } else {
            setPopupEnabled(false);
        }
    }

    private void setPopupEnabled(boolean b) {
        m_viewRawFileItem.setEnabled(b);
        m_detectPeakelsItem.setEnabled(b);
        m_convertRawFileItem.setEnabled(b);
        m_uploadMzdbFileItem.setEnabled(b);
    }

    @Override
    public ActionListener getDefaultAction() {
        return null;
    }

}