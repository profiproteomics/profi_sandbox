package fr.proline.studio.rsmexplorer.gui;

/**
 *
 * @author JM235353
 */
public class ProteinGroupPeptidePanel extends javax.swing.JPanel {

    /**
     * Creates new form ProteinGroupPeptidePanel
     */
    public ProteinGroupPeptidePanel() {
        initComponents();
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
        peptideInfoPanel = new fr.proline.studio.rsmexplorer.gui.ProteinGroupPeptideSpectrumPanel();
        peptidesTablePanel = new fr.proline.studio.rsmexplorer.gui.ProteinGroupPeptideTablePanel();

        setBackground(new java.awt.Color(255, 51, 102));

        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setBottomComponent(peptideInfoPanel);
        splitPane.setTopComponent(peptidesTablePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.proline.studio.rsmexplorer.gui.ProteinGroupPeptideSpectrumPanel peptideInfoPanel;
    private fr.proline.studio.rsmexplorer.gui.ProteinGroupPeptideTablePanel peptidesTablePanel;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
}
