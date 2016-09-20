package fr.proline.studio.gui;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import javax.swing.TransferHandler;

/**
 * Class used for Drag and Drop of nodes
 *
 * @author JM235353
 */
public class TreeFileChooserTransferHandler extends TransferHandler {

    private Hashtable<JComponent, JComponent> m_components;

    public TreeFileChooserTransferHandler() {
        m_components = new Hashtable<JComponent, JComponent>();
    }

    public void addComponent(JComponent component) {
        m_components.put(component, component);
    }
    
    public void clearHighlights(){
        Enumeration<JComponent> enumKey = m_components.keys();
        while (enumKey.hasMoreElements()) {
            JComponent key = enumKey.nextElement();
            key.setBackground(null);
        }
    }

    //private Logger m_logger = LoggerFactory.getLogger("ProlineStudio.ResultExplorer");
    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        if (c instanceof TreeFileChooser) {
            TreeFileChooser tree = (TreeFileChooser) c;

            ArrayList<File> selectedFiles = tree.getSelectedFiles();
            if (selectedFiles.isEmpty()) {
                return null;
            }

            return new FilesTransferable(selectedFiles);

        }
        return null;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        clearHighlights();
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        
        support.setShowDropLocation(true);

        if (support.isDataFlavorSupported(FilesTransferable.Files_FLAVOR)) {
            DropLocation dropLocation = support.getDropLocation();
            if (dropLocation instanceof JTable.DropLocation) {
                return true;
            } else if (m_components.containsKey((JComponent)support.getComponent())) {
                m_components.get((JComponent)support.getComponent()).setBackground(Color.WHITE);
                return true;
            }   
        }

        clearHighlights();
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (support.getComponent() instanceof JTable) {

            try {
                JTable table = (JTable) support.getComponent();
                JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();

                int index = table.convertRowIndexToModel(dl.getRow());

                int max = table.getModel().getRowCount();
                if (index < 0 || index > max) {
                    return false;
                }

                FilesTransferable transferable = (FilesTransferable) support.getTransferable().getTransferData(FilesTransferable.Files_FLAVOR);
                ArrayList<File> transferredFiles = transferable.getFiles();
                TreeFileChooserTableModelInterface model = (TreeFileChooserTableModelInterface) table.getModel();
                model.setFiles(transferredFiles, index);

                ArrayList<Integer> indices = new ArrayList<Integer>();
                for(int i = 0; i<transferredFiles.size(); i++){
                    indices.add(index+i);
                }
                
                System.out.println("breakpoint");
                
                if(model.isCorruptionPossible(indices)){
                    JOptionPane.showMessageDialog(null, "Existing links may be corrupted by the association(s).", "Warning", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception e) {
                //should not happen
                return false;
            }
        } else if (support.getComponent() instanceof DropZoneInterface) {
            try {
                
                DropZoneInterface dropZone = (DropZoneInterface) support.getComponent();

                FilesTransferable transferable = (FilesTransferable) support.getTransferable().getTransferData(FilesTransferable.Files_FLAVOR);
                ArrayList<File> transferredFiles = transferable.getFiles();

                ArrayList<File> samples = new ArrayList<File>();

                for (int i = 0; i < transferredFiles.size(); i++) {
                    //this.getFilesRecursive(transferredFiles.get(i), recursiveTransferredFiles);      

                    if (transferredFiles.get(i).isFile()) {
                        samples.add(transferredFiles.get(i));
                    } else {
                        File[] listOfFiles = transferredFiles.get(i).listFiles();
                        for (File file : listOfFiles) {
                            if (file.isFile()) {
                                samples.add(file);
                            }
                        }

                    }
                }
                
                dropZone.addSamples(samples);

            } catch (Exception e) {
                //should not happen
                return false;
            }

            return false;
        }
        return true;

    }

    private static void getFilesRecursive(File pFile, ArrayList<File> filesList) {
        for (File currentFile : pFile.listFiles()) {
            if (currentFile.isDirectory()) {
                getFilesRecursive(currentFile, filesList);
            } else {
                //if(currentFile.getName().endsWith(".mzdb")){
                filesList.add(currentFile);
                //}
            }
        }
    }

    public static class FilesTransferable implements Transferable, Serializable {

        public final static DataFlavor Files_FLAVOR = new DataFlavor(FilesTransferable.class, "Drag and drop Files Selection");

        private static final DataFlavor[] DATA_FLAVORS = {Files_FLAVOR};

        private final ArrayList<File> m_selectedFiles;

        public FilesTransferable(ArrayList<File> selectedFiles) {
            m_selectedFiles = selectedFiles;
        }

        public ArrayList<File> getFiles() {
            return m_selectedFiles;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(flavor)) {
                return this;
            }

            return null;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return DATA_FLAVORS;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return (DATA_FLAVORS[0].equals(flavor));
        }

    }

}
