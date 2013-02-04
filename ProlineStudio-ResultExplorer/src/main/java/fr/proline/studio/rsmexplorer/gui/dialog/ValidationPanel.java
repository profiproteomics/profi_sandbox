/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui.dialog;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Panel used in ValidationDialog
 * @author JM235353
 */
public class ValidationPanel extends javax.swing.JPanel {

    // default values
    private static final int FDR_MAX = 10;
    private static final int FDR_PEPTIDE_DEFAULT = 5;
    private static final int FDR_PROTEIN_DEFAULT = 1;
    private static final int SEQUENCE_LENGTH_DEFAULT = 6;

    
    
    /**
     * Creates new form ValidationPanel
     */
    public ValidationPanel() {
        initComponents();
        initDefaults();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        generalParametersPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        descriptionTextField = new javax.swing.JTextField();
        peptideParametersPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        peptideFdrSlider = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        minPeptideLengthInPeptideParamSpinner = new javax.swing.JSpinner();
        peptideFdrTextfield = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        proteinParametersPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        proteinFdrSlider = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        minPeptideLengthInProteinParamSpinner = new javax.swing.JSpinner();
        proteinFdrTextfield = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        generalParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.generalParametersPanel.border.title"))); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel1.text")); // NOI18N

        descriptionTextField.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.descriptionTextField.text")); // NOI18N

        javax.swing.GroupLayout generalParametersPanelLayout = new javax.swing.GroupLayout(generalParametersPanel);
        generalParametersPanel.setLayout(generalParametersPanelLayout);
        generalParametersPanelLayout.setHorizontalGroup(
            generalParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalParametersPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        generalParametersPanelLayout.setVerticalGroup(
            generalParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(descriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        peptideParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.peptideParametersPanel.border.title"))); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel2.text")); // NOI18N

        peptideFdrSlider.setMaximum(FDR_MAX);
        peptideFdrSlider.setPaintTicks(true);
        peptideFdrSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                peptideFdrSliderStateChanged(evt);
            }
        });

        jLabel3.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel3.text")); // NOI18N

        minPeptideLengthInPeptideParamSpinner.setModel(new SpinnerNumberModel(new Integer(SEQUENCE_LENGTH_DEFAULT), new Integer(1), null, new Integer(1)));

        peptideFdrTextfield.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.peptideFdrTextfield.text")); // NOI18N
        peptideFdrTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                peptideFdrTextfieldActionPerformed(evt);
            }
        });

        jLabel6.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout peptideParametersPanelLayout = new javax.swing.GroupLayout(peptideParametersPanel);
        peptideParametersPanel.setLayout(peptideParametersPanelLayout);
        peptideParametersPanelLayout.setHorizontalGroup(
            peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideParametersPanelLayout.createSequentialGroup()
                .addGroup(peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peptideParametersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(peptideFdrSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(peptideFdrTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addGroup(peptideParametersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(minPeptideLengthInPeptideParamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        peptideParametersPanelLayout.setVerticalGroup(
            peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(peptideParametersPanelLayout.createSequentialGroup()
                .addGroup(peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(peptideParametersPanelLayout.createSequentialGroup()
                        .addGroup(peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(peptideFdrSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(minPeptideLengthInPeptideParamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(peptideParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(peptideFdrTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
                .addGap(3, 3, 3))
        );

        proteinParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.proteinParametersPanel.border.title"))); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel4.text")); // NOI18N

        proteinFdrSlider.setMaximum(FDR_MAX);
        proteinFdrSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                proteinFdrSliderStateChanged(evt);
            }
        });

        jLabel5.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel5.text")); // NOI18N

        minPeptideLengthInProteinParamSpinner.setModel(new SpinnerNumberModel(new Integer(SEQUENCE_LENGTH_DEFAULT), new Integer(1), null, new Integer(1)));

        proteinFdrTextfield.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.proteinFdrTextfield.text")); // NOI18N
        proteinFdrTextfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proteinFdrTextfieldActionPerformed(evt);
            }
        });

        jLabel7.setText(org.openide.util.NbBundle.getMessage(ValidationPanel.class, "ValidationPanel.jLabel7.text")); // NOI18N

        javax.swing.GroupLayout proteinParametersPanelLayout = new javax.swing.GroupLayout(proteinParametersPanel);
        proteinParametersPanel.setLayout(proteinParametersPanelLayout);
        proteinParametersPanelLayout.setHorizontalGroup(
            proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proteinParametersPanelLayout.createSequentialGroup()
                .addGroup(proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(proteinParametersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(proteinFdrSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(proteinFdrTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addGroup(proteinParametersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(minPeptideLengthInProteinParamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        proteinParametersPanelLayout.setVerticalGroup(
            proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proteinParametersPanelLayout.createSequentialGroup()
                .addGroup(proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(proteinFdrSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(minPeptideLengthInProteinParamSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(proteinParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(proteinFdrTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel7))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(proteinParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(generalParametersPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(peptideParametersPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(generalParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(peptideParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(proteinParametersPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void peptideFdrTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_peptideFdrTextfieldActionPerformed
        JTextField source = (JTextField) evt.getSource();
        
        try {
            int value = Integer.parseInt(source.getText());
            peptideFdrSlider.setValue(value);
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_peptideFdrTextfieldActionPerformed

    private void peptideFdrSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_peptideFdrSliderStateChanged

        JSlider source = (JSlider) evt.getSource();
        int fdr = (int) source.getValue();
            peptideFdrTextfield.setText(String.valueOf(fdr));
    }//GEN-LAST:event_peptideFdrSliderStateChanged

    private void proteinFdrSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_proteinFdrSliderStateChanged
        JSlider source = (JSlider) evt.getSource();
        int fdr = (int) source.getValue();
        proteinFdrTextfield.setText(String.valueOf(fdr));
    }//GEN-LAST:event_proteinFdrSliderStateChanged

    private void proteinFdrTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proteinFdrTextfieldActionPerformed
        JTextField source = (JTextField) evt.getSource();

        try {
            int value = Integer.parseInt(source.getText());
            proteinFdrSlider.setValue(value);
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_proteinFdrTextfieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JPanel generalParametersPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSpinner minPeptideLengthInPeptideParamSpinner;
    private javax.swing.JSpinner minPeptideLengthInProteinParamSpinner;
    private javax.swing.JSlider peptideFdrSlider;
    private javax.swing.JTextField peptideFdrTextfield;
    private javax.swing.JPanel peptideParametersPanel;
    private javax.swing.JSlider proteinFdrSlider;
    private javax.swing.JTextField proteinFdrTextfield;
    private javax.swing.JPanel proteinParametersPanel;
    // End of variables declaration//GEN-END:variables

    public final void initDefaults() {
        
        // General parameters
        descriptionTextField.setText("");
        
        // peptides parameters
        minPeptideLengthInPeptideParamSpinner.setValue(new Integer(SEQUENCE_LENGTH_DEFAULT));

        peptideFdrSlider.setValue(FDR_PEPTIDE_DEFAULT);
        peptideFdrTextfield.setText(String.valueOf(FDR_PEPTIDE_DEFAULT));
        
        // protein parameters
        minPeptideLengthInProteinParamSpinner.setValue(new Integer(SEQUENCE_LENGTH_DEFAULT));
        
        proteinFdrSlider.setValue(FDR_PROTEIN_DEFAULT);
        proteinFdrTextfield.setText(String.valueOf(FDR_PROTEIN_DEFAULT));
        
        
        
    }
    
    public String getDescription() {
        return descriptionTextField.getText();
    }
    
    public int getPeptideFDR() {
        return peptideFdrSlider.getValue();
    }
    
    public int getPeptideMinPepSequence() {
        return ((Integer)minPeptideLengthInPeptideParamSpinner.getValue()).intValue();
    }
    
    public int getProteinFDR() {
        return proteinFdrSlider.getValue();
    }
    
    public int getProteinMinPepSequence() {
        return ((Integer)minPeptideLengthInProteinParamSpinner.getValue()).intValue();
    }

}
