package fr.proline.studio.rsmexplorer.gui;



import fr.proline.core.orm.msi.ProteinMatch;
import fr.proline.core.orm.msi.ProteinSet;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import fr.proline.studio.rsmexplorer.gui.model.ProteinTableModel;
import fr.proline.studio.utils.DecoratedTable;
import java.awt.Color;
import javax.swing.event.ListSelectionEvent;


/**
 * In : Window which display Protein Groups of a Result Summary
 * - Panel used to display Proteins of a Protein Group (at the center of the window)
 * 
 * @author JM235353
 */
public class RsmProteinsOfProteinSetPanel extends javax.swing.JPanel implements DataBoxPanelInterface {

    private AbstractDataBox dataBox;
    
    private ProteinSet proteinSetCur = null;
    
    /**
     * Creates new form RsmProteinsOfProteinSetPanel
     */
    public RsmProteinsOfProteinSetPanel() {
        initComponents();
        
        ((DecoratedTable)proteinTable).displayColumnAsPercentage(ProteinTableModel.COLTYPE_PROTEIN_SCORE);
 
    }

    public ProteinMatch getSelectedProteinMatch() {
        
        ProteinTable table = (ProteinTable) proteinTable;
        
            // Retrieve Selected Row
            int selectedRow = table.getSelectedRow();
            

            // nothing selected
            if (selectedRow == -1) {
                return null;
                
            }
            
            // convert according to the sorting
            selectedRow = table.convertRowIndexToModel(selectedRow);
            
            
            
            // Retrieve ProteinSet selected
            ProteinTableModel tableModel = (ProteinTableModel) table.getModel();
            
            return tableModel.getProteinMatch(selectedRow);
    }
 
    
    public void setData(ProteinSet proteinSet, String searchedText) {
        
        if (proteinSet == proteinSetCur) {
            return;
        }
        proteinSetCur = proteinSet;
        
        if (proteinSet == null) {
            clearData();
            return;
        }
        
        ProteinSet.TransientData proteinSetData = proteinSet.getTransientData();
        
        // retrieve sameset and subset
        ProteinMatch[] sameSetArray = proteinSetData.getSameSet(); 
        ProteinMatch[] subSetArray =  proteinSetData.getSubSet();
        
        // retrieve Typical Protein Match
        ProteinMatch typicalProtein = proteinSetData.getTypicalProteinMatch();
        
        // Modify Panel Border Title
        //((ProteinGroupProteinSelectedPanel) ViewTopComponent.getPanel(ProteinGroupProteinSelectedPanel.class)).updateTitle(typicalProtein.getAccession());
        //JPM.TODO
        
        
        // Modify protein description
        proteinNameTextField.setText(typicalProtein.getDescription() );
        
        
        // Modify the Model
        ((ProteinTableModel) proteinTable.getModel()).setData(proteinSet.getResultSummary().getId(), sameSetArray, subSetArray);
        
        // Select the Row
        int row = ((ProteinTableModel) proteinTable.getModel()).findRowToSelect(searchedText);
        proteinTable.getSelectionModel().setSelectionInterval(row, row);
        
    }
    
    private void clearData() {
        proteinNameTextField.setText("");
        //((ProteinGroupProteinSelectedPanel) ViewTopComponent.getPanel(ProteinGroupProteinSelectedPanel.class)).updateTitle(null); //JPM.TODO
        ((ProteinTableModel) proteinTable.getModel()).setData(null, null, null);

    }
    
    @Override
    public void setDataBox(AbstractDataBox dataBox) {
        this.dataBox = dataBox;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        proteinNameTextField = new javax.swing.JTextField();
        proteinNameTextField.setEditable(false);
        proteinNameTextField.setBackground(Color.white);
        scrollPane = new javax.swing.JScrollPane();
        proteinTable = new ProteinTable();

        proteinNameTextField.setText(org.openide.util.NbBundle.getMessage(RsmProteinsOfProteinSetPanel.class, "RsmProteinsOfProteinSetPanel.proteinNameTextField.text")); // NOI18N

        proteinTable.setModel(new ProteinTableModel());
        scrollPane.setViewportView(proteinTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(proteinNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proteinNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField proteinNameTextField;
    private javax.swing.JTable proteinTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    
    
    private class ProteinTable extends DecoratedTable  {
        
        /** 
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            super.valueChanged(e);
            
            dataBox.propagateDataChanged(ProteinMatch.class);
            

        }
    }


}
