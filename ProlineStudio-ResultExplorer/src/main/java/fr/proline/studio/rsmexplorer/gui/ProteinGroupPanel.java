package fr.proline.studio.rsmexplorer.gui;

import fr.proline.studio.rsmexplorer.DataViewerTopComponent;

/**
 *
 * @author JM235353
 */
public class ProteinGroupPanel extends javax.swing.JPanel {


    
    /**
     * Creates new form ProteinGroupsPanel
     */
    public ProteinGroupPanel() {
        initComponents();
        
        // register children panels
        DataViewerTopComponent.addPanel(proteinGroupTablePanel);
        DataViewerTopComponent.addPanel(proteinSelectedPanel);
        
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        proteinGroupTablePanel = new fr.proline.studio.rsmexplorer.gui.ProteinGroupTablePanel();
        proteinSelectedPanel = new fr.proline.studio.rsmexplorer.gui.ProteinGroupProteinSelectedPanel();

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(proteinGroupTablePanel);
        splitPane.setBottomComponent(proteinSelectedPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 867, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 815, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.proline.studio.rsmexplorer.gui.ProteinGroupTablePanel proteinGroupTablePanel;
    private fr.proline.studio.rsmexplorer.gui.ProteinGroupProteinSelectedPanel proteinSelectedPanel;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
}
