package fr.proline.studio.rsmexplorer.gui;

import fr.proline.core.orm.msi.*;
import fr.proline.core.orm.ps.PeptidePtm;
import fr.proline.studio.rsmexplorer.DataViewerTopComponent;
import fr.proline.studio.rsmexplorer.gui.model.PeptideTableModel;
import fr.proline.studio.utils.DecoratedTable;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import fr.proline.studio.utils.GlobalValues;
/**
 *
 * @author JM235353
 */
public class ProteinGroupPeptideTablePanel extends javax.swing.JPanel {

    ProteinMatch currentProteinMatch = null;
    
    /**
     * Creates new form ProteinGroupPeptideTablePanel
     */
    public ProteinGroupPeptideTablePanel() {
        initComponents();
        
        ((DecoratedTable)peptidesTable).displayColumnAsPercentage(PeptideTableModel.COLTYPE_PEPTIDE_SCORE);
   
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        peptidesTable = new PeptideTable();

        peptidesTable.setModel(new PeptideTableModel());
        scrollPane.setViewportView(peptidesTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable peptidesTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables


    public void setData(ProteinMatch proteinMatch) {

        currentProteinMatch = proteinMatch;
        
        if (proteinMatch == null) {
            ((PeptideTableModel) peptidesTable.getModel()).setData(null);
        } else {
            PeptideSet peptideSet = proteinMatch.getTransientPeptideSet();
            PeptideInstance[] peptideInstances = peptideSet.getTransientPeptideInstances();

            ((PeptideTableModel) peptidesTable.getModel()).setData(peptideInstances);

            // select the first peptide
            if ((peptideInstances != null) && (peptideInstances.length > 0)) {
                peptidesTable.getSelectionModel().setSelectionInterval(0, 0);
            }
        }
        
    }
    
    
    private class PeptideTable extends DecoratedTable  {
        
        public PeptideTable() {
            setDefaultRenderer(Peptide.class, new PeptideRenderer());
        } 
        
        
        /** 
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            super.valueChanged(e);
            
            ProteinGroupPeptideSpectrumPanel p = (ProteinGroupPeptideSpectrumPanel) DataViewerTopComponent.getPanel(ProteinGroupPeptideSpectrumPanel.class);

            int selectedIndex = peptidesTable.getSelectionModel().getMinSelectionIndex();
            
            if (selectedIndex == -1) {
                 p.setData(null, -1, null);
            } else {
                int indexInModelSelected = peptidesTable.convertRowIndexToModel(selectedIndex);
                p.setData(currentProteinMatch, indexInModelSelected, ((PeptideTableModel) peptidesTable.getModel()).getPeptideInstances());
            }
        }
        
        private class PeptideRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                Peptide p = (Peptide) value;
                
                String displayString = constructPeptideDisplay(p);
                
                return super.getTableCellRendererComponent(table, displayString, isSelected, hasFocus, row, column);
            }
            
            private String constructPeptideDisplay(Peptide peptide) {

                SequenceMatch sequenceMatch = peptide.getTransientData().getSequenceMatch();

                if (sequenceMatch != null) {

                    HashMap<Integer, PeptidePtm> ptmMap = peptide.getTransientData().getPeptidePtmMap();
                    if (ptmMap != null) {
                        displaySB.append("<HTML>");
                    }

                    // Add Before residue of the peptide
                    String residueBefore = sequenceMatch.getResidueBefore();
                    if (residueBefore != null) {
                        displaySB.append(residueBefore.toUpperCase());
                        displaySB.append('-');
                    }


                    String sequence = peptide.getSequence();

                    if (ptmMap == null) {
                        displaySB.append(sequence);
                    } else {


                        int nb = sequence.length();
                        for (int i = 0; i < nb; i++) {

                            boolean nTerOrCterModification = false;
                            if (i == 0) {
                                PeptidePtm nterPtm = ptmMap.get(0);
                                if (nterPtm != null) {
                                    nTerOrCterModification = true;
                                }
                            } else if (i == nb - 1) {
                                PeptidePtm cterPtm = ptmMap.get(-1);
                                if (cterPtm != null) {
                                    nTerOrCterModification = true;
                                }
                            }

                            PeptidePtm ptm = ptmMap.get(i + 1);
                            boolean aminoAcidModification = (ptm != null);

                            if (nTerOrCterModification || aminoAcidModification) {
                                if (nTerOrCterModification && aminoAcidModification) {
                                    displaySB.append("<span style='color:").append(GlobalValues.HTML_COLOR_VIOLET).append("'>");
                                } else if (nTerOrCterModification) {
                                    displaySB.append("<span style='color:").append(GlobalValues.HTML_COLOR_GREEN).append("'>");
                                } else if (aminoAcidModification) {
                                    displaySB.append("<span style='color:").append(GlobalValues.HTML_COLOR_ORANGE).append("'>");
                                }
                                displaySB.append(sequence.charAt(i));
                                displaySB.append("</span>");
                            } else {
                                displaySB.append(sequence.charAt(i));
                            }

                        }

                    }

                    // Add After residue of the peptide
                    String residueAfter = sequenceMatch.getResidueAfter();
                    if (residueAfter != null) {
                        displaySB.append('-');
                        displaySB.append(residueAfter.toUpperCase());
                    }

                    if (ptmMap != null) {
                        displaySB.append("</HTML>");
                    }

                    String res = displaySB.toString();
                    displaySB.setLength(0);
                    return res;
                }

                return peptide.getSequence();


            }
            private  StringBuilder displaySB = new StringBuilder();
        }   
        
       
    }
    
    

    
    
    
}
