package fr.proline.studio.rsmexplorer.gui;


import fr.proline.core.orm.msi.ProteinSet;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.*;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import fr.proline.studio.rsmexplorer.gui.model.ProteinGroupTableModel;
import fr.proline.studio.rsmexplorer.gui.renderer.FloatRenderer;
import fr.proline.studio.utils.LazyTable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import org.openide.util.ImageUtilities;

/**
 * In : Window which display Protein Sets of a Result Summary
 * - Panel used to display Protein Sets (at the top)
 * 
 * @author JM235353
 */
public class RsmProteinSetPanel extends javax.swing.JPanel implements DataBoxPanelInterface {

    private AbstractDataBox dataBox;
    
    /**
     * Creates new form ProteinGroupsTablePanel
     */
    public RsmProteinSetPanel() {
        initComponents();
        

        ((LazyTable) proteinGroupTable).displayColumnAsPercentage(ProteinGroupTableModel.COLTYPE_PROTEIN_SCORE);

        searchTextField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                searchButton.doClick();
            }
            
        });
        

    }

    public void setData(Long taskId, ProteinSet[] proteinSets) {
        ((ProteinGroupTableModel) proteinGroupTable.getModel()).setData(taskId, proteinSets);

        // select the first row
        if ((proteinSets != null) && (proteinSets.length > 0)) {
            proteinGroupTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    public void dataUpdated(SubTask subTask) {
        ((ProteinGroupTable) proteinGroupTable).dataUpdated(subTask);
    }

    public ProteinSet getSelectedProteinSet() {

        ProteinGroupTable table = ((ProteinGroupTable) proteinGroupTable);

        // Retrieve Selected Row
        int selectedRow = table.getSelectedRow();


        // nothing selected
        if (selectedRow == -1) {
            return null;

        }

        // convert according to the sorting
        selectedRow = table.convertRowIndexToModel(selectedRow);



        // Retrieve ProteinSet selected
        ProteinGroupTableModel tableModel = (ProteinGroupTableModel) table.getModel();
        return tableModel.getProteinSet(selectedRow);
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

        proteinGroupScrollPane = new javax.swing.JScrollPane();
        proteinGroupTable = new ProteinGroupTable();
        searchButton = new SearchButton();
        try {
            searchButton.setIcon(new javax.swing.ImageIcon(ImageUtilities.loadImage ("fr/proline/studio/images/search.png")));
        } catch (NullPointerException e) {
            // Hack : netbeans editor introspection does
            // not work when this panel is added to another component
        }
        searchTextField = new javax.swing.JTextField();

        proteinGroupScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        proteinGroupTable.setModel(new ProteinGroupTableModel((LazyTable)proteinGroupTable));
        proteinGroupTable.setFillsViewportHeight(true);
        proteinGroupScrollPane.setViewportView(proteinGroupTable);

        searchButton.setText(org.openide.util.NbBundle.getMessage(RsmProteinSetPanel.class, "RsmProteinSetPanel.searchButton.text")); // NOI18N

        searchTextField.setText(org.openide.util.NbBundle.getMessage(RsmProteinSetPanel.class, "RsmProteinSetPanel.searchTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proteinGroupScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(proteinGroupScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane proteinGroupScrollPane;
    private javax.swing.JTable proteinGroupTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchTextField;
    // End of variables declaration//GEN-END:variables

    
    private class SearchButton extends JButton  {

        String previousSearch = "";
        int searchIndex = 0;
        ArrayList<Integer> proteinSetIds = new ArrayList<Integer>();

        
        public SearchButton() {
            addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    doSearch();
                }
            });
        }
        
        public void sortingChanged() {
            if (proteinSetIds.isEmpty()) {
                return;
            }
            searchIndex = -1;
            ((ProteinGroupTableModel) proteinGroupTable.getModel()).sortAccordingToModel(proteinSetIds);
        }
        
        private void doSearch() {
            
            final String searchText = searchTextField.getText().trim();

            if (searchText.compareTo(previousSearch) == 0) {
                // search already done, display next result
                searchIndex++;
                if (searchIndex >= proteinSetIds.size()) {
                    searchIndex = 0;
                }
                
                if (!proteinSetIds.isEmpty()) {
                    ((ProteinGroupTable) proteinGroupTable).selectProteinSet(proteinSetIds.get(searchIndex), searchText);
                }
                
            } else {
                previousSearch = searchText;
                searchIndex = 0;

                // prepare callback for the search
                AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                    @Override
                    public boolean mustBeCalledInAWT() {
                        return true;
                    }

                    @Override
                    public void run(boolean success, long taskId, SubTask subTask) {

                        // contruct the Map of proteinSetId
                        
                        
                        if (!proteinSetIds.isEmpty()) {
                            
                            ((ProteinGroupTableModel) proteinGroupTable.getModel()).sortAccordingToModel(proteinSetIds);

                            ((ProteinGroupTable) proteinGroupTable).selectProteinSet(proteinSetIds.get(searchIndex), searchText);
                            
                        }


                        //System.out.println("Ids size "+proteinSetIds.size());
                        searchButton.setEnabled(true);
                    }
                };

                ResultSummary rsm = ((ProteinGroupTableModel) proteinGroupTable.getModel()).getResultSummary();


                // Load data if needed asynchronously
                AccessDatabaseThread.getAccessDatabaseThread().addTask(new DatabaseSearchProteinSetsTask(callback, rsm, searchText, proteinSetIds));

                searchButton.setEnabled(false);
            }
        }
        
    }
    
    private class ProteinGroupTable extends LazyTable  {
        /** 
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */

        ProteinSet proteinSetSelected = null;
        
        
        public ProteinGroupTable() {
            super(proteinGroupScrollPane.getVerticalScrollBar() );
            
            setDefaultRenderer(Float.class, new FloatRenderer( getDefaultRenderer(String.class) ) );
        }
        
        /** 
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            
            super.valueChanged(e);
            
            if (selectionWillBeRestored) {
                return;
            }
 
            dataBox.propagateDataChanged(ProteinSet.class);

        }
        
        public void selectProteinSet(Integer proteinSetId, String searchText) {
            ProteinGroupTableModel tableModel = (ProteinGroupTableModel) getModel();
            int row = tableModel.findRow(proteinSetId);
            if (row == -1) {
                return;
            }
            
            // JPM.hack we need to keep the search text
            // to be able to give it if needed to the panel
            // which display proteins of a protein set
            searchTextBeingDone = searchText;
            
            // must convert row index if there is a sorting
            row = convertRowIndexToView(row);
            
            // select the row
            getSelectionModel().setSelectionInterval(row, row);
            
            // scroll to the row
            scrollRowToVisible(row);

            searchTextBeingDone = null;
            
        }
        String searchTextBeingDone = null;

        public void dataUpdated(SubTask subTask) {
            
            LastAction keepLastAction = lastAction;
            try {
            
            
            // retrieve selected row
            int rowSelected = getSelectionModel().getMinSelectionIndex();
            int rowSelectedInModel = (rowSelected == -1) ? -1 : convertRowIndexToModel(rowSelected);

            // Update Model (but protein set table must not react to the model update)
            
            selectionWillBeRestored(true);
            try {
                ((ProteinGroupTableModel) getModel()).dataUpdated();
            } finally {
                selectionWillBeRestored(false);
            }

            
            
            // restore selected row
            if (rowSelectedInModel != -1) {
                int rowSelectedInView = convertRowIndexToView(rowSelectedInModel);
                //getSelectionModel().setSelectionInterval(rowSelectedInView, rowSelectedInView);
                setSelection(rowSelectedInView);

                
                // if the subtask correspond to the loading of the data of the sorted column,
                // we keep the row selected visible
                if (((keepLastAction == LastAction.ACTION_SELECTING ) || (keepLastAction == LastAction.ACTION_SORTING)) && (subTask.getSubTaskId() == ((ProteinGroupTableModel) getModel()).getSubTaskId( getSortedColumnIndex() )) ) {
                    ((ProteinGroupTable) proteinGroupTable).scrollRowToVisible(rowSelectedInView);
                }
                    
            }

            } finally {

                lastAction = keepLastAction;
 
            }
        }
        
        @Override
        public void sortingChanged(int col) {
            ((SearchButton)searchButton).sortingChanged();
        }
    
        public void selectionWillBeRestored(boolean b) {
            selectionWillBeRestored = b;
        }
        private boolean selectionWillBeRestored = false;
        
        
    }
    

    
    
}