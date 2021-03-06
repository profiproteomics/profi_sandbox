/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.rsmexplorer.gui.dialog;

import fr.proline.studio.dam.data.ClearProjectData;
import fr.proline.studio.rsmexplorer.gui.DataClearProjectTable;
import fr.proline.studio.rsmexplorer.gui.model.DataClearProjectTableModel;
import fr.proline.studio.extendedtablemodel.CompoundTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Panel used to clear project (rsm and rs)
 * @author MB243701
 */
public class ClearProjectPanel extends JPanel{
    private List<ClearProjectData> m_dataToClear;
    
    private JScrollPane m_scrollPane;
    private DataClearProjectTable m_dataTable;
    
    public ClearProjectPanel(){
        super();
        initComponents();
    }
    
    private void initComponents(){
        this.setLayout(new BorderLayout());
        JPanel internalPanel = new JPanel();

        internalPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);

        // create objects
        m_scrollPane = new JScrollPane();

        m_dataTable = new DataClearProjectTable();
        m_dataTable.setModel(new CompoundTableModel(new DataClearProjectTableModel(), true));
         
        m_scrollPane.setViewportView(m_dataTable);
        m_dataTable.setFillsViewportHeight(true);
        m_dataTable.setViewport(m_scrollPane.getViewport());
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        internalPanel.add(m_scrollPane, c);
        
        this.add(internalPanel, BorderLayout.CENTER);
        
        TableColumnModel columnModel = m_dataTable.getColumnModel();   
        TableColumn col = columnModel.getColumn(1);
        col.setWidth(20);
        col.setPreferredWidth(20);
        col = columnModel.getColumn(2);
        col.setWidth(100);
        col.setPreferredWidth(200);

    }
    
    public void setData(List<ClearProjectData> dataToClear){
        m_dataToClear = dataToClear;
        ((DataClearProjectTableModel) ((CompoundTableModel) m_dataTable.getModel()).getBaseModel()).setData(m_dataToClear);
    }
    
    
    public List<ClearProjectData> getSelectedData(){
        return ((DataClearProjectTableModel) ((CompoundTableModel) m_dataTable.getModel()).getBaseModel()).getSelectedData();
    }
    
    
}

