package fr.proline.studio.rsmexplorer.gui;


import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.core.orm.msi.dto.DProteinPTMSite;
import fr.proline.studio.comparedata.AddDataAnalyzerButton;
import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.comparedata.GlobalTabelModelProviderInterface;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.export.ExportButton;
import fr.proline.studio.filter.FilterButton;
import fr.proline.studio.filter.actions.ClearRestrainAction;
import fr.proline.studio.filter.actions.RestrainAction;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.gui.HourglassPanel;
import fr.proline.studio.gui.SplittedPanelContainer;
import fr.proline.studio.markerbar.BookmarkMarker;
import fr.proline.studio.markerbar.MarkerContainerPanel;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import fr.proline.studio.pattern.DataMixerWindowBoxManager;
import fr.proline.studio.python.data.TableInfo;
import fr.proline.studio.rsmexplorer.gui.model.PtmProtenSiteTableModel;
import fr.proline.studio.search.SearchToggleButton;
import fr.proline.studio.table.CompoundTableModel;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.table.ImportTableSelectionInterface;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.TablePopupMenu;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.swingx.JXTable;

/**
 *
 * @author JM235353
 */
public class PTMProteinSitePanel extends HourglassPanel implements DataBoxPanelInterface, GlobalTabelModelProviderInterface {
    
    private AbstractDataBox m_dataBox;
    
    private JScrollPane m_ptmProteinSiteScrollPane;
    private PTMProteinSiteTable m_ptmProteinSiteTable;

    private MarkerContainerPanel m_markerContainerPanel;

    private SearchToggleButton m_searchToggleButton;
    
    private FilterButton m_filterButton;
    private ExportButton m_exportButton;
    private AddDataAnalyzerButton m_addCompareDataButton;
    
    /**
     * Creates new form PTMProteinSitePanel
     */
    public PTMProteinSitePanel() {

        initComponents();

    }

    public void setData(Long taskId, ArrayList<DProteinPTMSite> proteinPTMSiteArray, boolean finished) {

        ((PtmProtenSiteTableModel) ((CompoundTableModel)m_ptmProteinSiteTable.getModel()).getBaseModel()).setData(taskId, proteinPTMSiteArray);

        // select the first row
        if ((proteinPTMSiteArray != null) && (proteinPTMSiteArray.size() > 0)) {
            m_ptmProteinSiteTable.getSelectionModel().setSelectionInterval(0, 0);
            m_markerContainerPanel.setMaxLineNumber(proteinPTMSiteArray.size());

        }
        
        if (finished) {
            m_ptmProteinSiteTable.setSortable(true);
        }
    }

    public void dataUpdated(SubTask subTask, boolean finished) {
        m_ptmProteinSiteTable.dataUpdated(subTask, finished);
    }


    
    public DProteinPTMSite getSelectedProteinPTMSite() {

        // Retrieve Selected Row
        int selectedRow = m_ptmProteinSiteTable.getSelectedRow();


        // nothing selected
        if (selectedRow == -1) {
            return null;

        }

        // convert according to the sorting
        selectedRow = m_ptmProteinSiteTable.convertRowIndexToModel(selectedRow);

        CompoundTableModel compoundTableModel = ((CompoundTableModel)m_ptmProteinSiteTable.getModel());
        selectedRow = compoundTableModel.convertCompoundRowToBaseModelRow(selectedRow);

        // Retrieve ProteinPTMSite selected
        PtmProtenSiteTableModel tableModel = (PtmProtenSiteTableModel) compoundTableModel.getBaseModel();
        return tableModel.getProteinPTMSite(selectedRow);
    }

    @Override
    public void setDataBox(AbstractDataBox dataBox) {
        m_dataBox = dataBox;
    }
    @Override
    public AbstractDataBox getDataBox() {
        return m_dataBox;
    }
    
    @Override
    public void addSingleValue(Object v) {
        getGlobalTableModelInterface().addSingleValue(v);
    }
    
    @Override
    public GlobalTableModelInterface getGlobalTableModelInterface() {
        return (GlobalTableModelInterface) m_ptmProteinSiteTable.getModel();
    }
    
    @Override
    public JXTable getGlobalAssociatedTable() {
        return m_ptmProteinSiteTable;
    }
    
    @Override
    public CrossSelectionInterface getCrossSelectionInterface() {
        return m_ptmProteinSiteTable;
    }
    
    @Override
    public ActionListener getRemoveAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getRemoveAction(splittedPanel);
    }

    @Override
    public ActionListener getAddAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getAddAction(splittedPanel);
    }
    
    @Override
    public ActionListener getSaveAction(SplittedPanelContainer splittedPanel) {
        return m_dataBox.getSaveAction(splittedPanel);
    }

    private void initComponents() {


        setLayout(new BorderLayout());

        final JPanel proteinPTMSitePanel = createProteinPTMSitePanel();


        final JLayeredPane layeredPane = new JLayeredPane();

        layeredPane.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                final Component c = e.getComponent();

                proteinPTMSitePanel.setBounds(0, 0, c.getWidth(), c.getHeight());
                layeredPane.revalidate();
                layeredPane.repaint();

            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        add(layeredPane, BorderLayout.CENTER);

        layeredPane.add(proteinPTMSitePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(m_searchToggleButton.getSearchPanel(), JLayeredPane.PALETTE_LAYER);


    }
    
    
    private JPanel createProteinPTMSitePanel() {
        
        JPanel proteinPTMSitePanel = new JPanel();
        proteinPTMSitePanel.setBounds(0, 0, 500, 400);
        proteinPTMSitePanel.setLayout(new BorderLayout());
        
        JPanel internalPanel = createInternalPanel();

        JToolBar toolbar = initToolbar();
        proteinPTMSitePanel.add(toolbar, BorderLayout.WEST);
        proteinPTMSitePanel.add(internalPanel, BorderLayout.CENTER);


        return proteinPTMSitePanel;
    }
    
    private JToolBar initToolbar() {
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(false);

        
        // Search Button
        m_searchToggleButton = new SearchToggleButton(m_ptmProteinSiteTable, m_ptmProteinSiteTable, ((CompoundTableModel) m_ptmProteinSiteTable.getModel()));
        toolbar.add(m_searchToggleButton);
        
        m_filterButton = new FilterButton(((CompoundTableModel) m_ptmProteinSiteTable.getModel())) {

            @Override
            protected void filteringDone() {
                m_dataBox.propagateDataChanged(CompareDataInterface.class);
            }
            
        };

        m_exportButton = new ExportButton(((CompoundTableModel) m_ptmProteinSiteTable.getModel()), "Protein Sets", m_ptmProteinSiteTable);

        toolbar.add(m_filterButton);
        toolbar.add(m_exportButton);

        m_addCompareDataButton = new AddDataAnalyzerButton(((CompoundTableModel) m_ptmProteinSiteTable.getModel())) {
           
            @Override
            public void actionPerformed() {
                JXTable table = getGlobalAssociatedTable();
                TableInfo tableInfo = new TableInfo(m_dataBox.getId(), m_dataBox.getDataName(), m_dataBox.getTypeName(), table);
                Image i = m_dataBox.getIcon();
                if (i!=null) {
                    tableInfo.setIcon(new ImageIcon(i));
                }
                DataMixerWindowBoxManager.addTableInfo(tableInfo);
            }
        };
        toolbar.add(m_addCompareDataButton);
        
        return toolbar;
    }
    
    private JPanel createInternalPanel() {

        JPanel internalPanel = new JPanel();
        
        internalPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);
        
        // create objects
        m_ptmProteinSiteScrollPane = new JScrollPane();
        
        m_ptmProteinSiteTable = new PTMProteinSiteTable();
        m_ptmProteinSiteTable.setModel(new CompoundTableModel(new PtmProtenSiteTableModel((LazyTable)m_ptmProteinSiteTable), true));
        // hide the id column
        m_ptmProteinSiteTable.getColumnExt(m_ptmProteinSiteTable.convertColumnIndexToView(PtmProtenSiteTableModel.COLTYPE_PROTEIN_ID)).setVisible(false);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(m_ptmProteinSiteTable.getModel());
        m_ptmProteinSiteTable.setRowSorter(sorter);
            
        sorter.setComparator(PtmProtenSiteTableModel.COLTYPE_MODIFICATION_LOC, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                int pos1;
                if (s1.compareTo("N-term") == 0) {
                    pos1 = -1;
                } else if (s1.compareTo("C-term") == 0) {
                    pos1 = Integer.MAX_VALUE;
                } else {
                    pos1 = Integer.valueOf(s1);
                }
                int pos2;
                if (s2.compareTo("N-term") == 0) {
                    pos2 = 0;
                } else if (s2.compareTo("C-term") == 0) {
                    pos2 = Integer.MAX_VALUE;
                } else {
                    pos2 = Integer.valueOf(s2);
                }

                return pos2-pos1;
            }
 
                
        });
        
        sorter.setComparator(PtmProtenSiteTableModel.COLTYPE_PROTEIN_LOC, new Comparator<String>() {

            @Override
            public int compare(String s1, String s2) {
                int pos1;
                if (s1.isEmpty()) { // N-term or C-term Peptide
                    pos1 = Integer.MAX_VALUE;
                } else {
                    pos1 = Integer.valueOf(s1);
                }
                int pos2;
                if (s2.isEmpty()) { // N-term or C-term Peptide
                    pos2 = Integer.MAX_VALUE;
                } else {
                    pos2 = Integer.valueOf(s2);
                }

                return pos2-pos1;
            }
 
                
        });
        

        m_markerContainerPanel = new MarkerContainerPanel(m_ptmProteinSiteScrollPane, (PTMProteinSiteTable) m_ptmProteinSiteTable);
        
        m_ptmProteinSiteScrollPane.setViewportView(m_ptmProteinSiteTable);
        m_ptmProteinSiteTable.setFillsViewportHeight(true);
        m_ptmProteinSiteTable.setViewport(m_ptmProteinSiteScrollPane.getViewport());
       

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        internalPanel.add(m_markerContainerPanel, c);
        

        
        return internalPanel;
    }                 
    
    

    
    private class PTMProteinSiteTable extends LazyTable implements ImportTableSelectionInterface, CrossSelectionInterface  {

        
        public PTMProteinSiteTable() {
            super(m_ptmProteinSiteScrollPane.getVerticalScrollBar() );

        }
        
        @Override
        public void addTableModelListener(TableModelListener l) {
            getModel().addTableModelListener(l);
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
 
            m_dataBox.propagateDataChanged(DProteinMatch.class);
            m_dataBox.propagateDataChanged(DPeptideMatch.class);
            m_dataBox.propagateDataChanged(DProteinPTMSite.class);

        }



        public void dataUpdated(SubTask subTask, boolean finished) {
            
            LazyTable.LastAction keepLastAction = m_lastAction;
            try {
            
            
            // retrieve selected row
            int rowSelected = getSelectionModel().getMinSelectionIndex();
            int rowSelectedInModel = (rowSelected == -1) ? -1 : convertRowIndexToModel(rowSelected);

            // Update Model (but protein set table must not react to the model update)
            
            selectionWillBeRestored(true);
            try {
                ((PtmProtenSiteTableModel) (((CompoundTableModel) getModel()).getBaseModel())).dataUpdated();
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
                if (((keepLastAction == LazyTable.LastAction.ACTION_SELECTING ) || (keepLastAction == LazyTable.LastAction.ACTION_SORTING)) && (subTask.getSubTaskId() == ((CompoundTableModel) getModel()).getSubTaskId( getSortedColumnIndex() )) ) {
                    scrollRowToVisible(rowSelectedInView);
                }
                    
            }

            } finally {

                m_lastAction = keepLastAction;
 
            }
            
            if (finished) {
                setSortable(true);
            }
        }

        public void selectionWillBeRestored(boolean b) {
            selectionWillBeRestored = b;
        }
        private boolean selectionWillBeRestored = false;

        @Override
        public int getLoadingPercentage() {
            return m_dataBox.getLoadingPercentage();
        }

        @Override
        public boolean isLoaded() {
            return m_dataBox.isLoaded();
        }

        @Override
        public void importSelection(HashSet selectedData) {
            
            ListSelectionModel selectionTableModel = getSelectionModel();
            selectionTableModel.clearSelection();
            
            int firstRow = -1;
            PtmProtenSiteTableModel model = (PtmProtenSiteTableModel) ((CompoundTableModel) m_ptmProteinSiteTable.getModel()).getBaseModel();
            int rowCount = model.getRowCount();
            for (int i=0;i<rowCount;i++) {
                Object v = model.getValueAt(i, PtmProtenSiteTableModel.COLTYPE_PROTEIN_ID);
                if (selectedData.remove(v)) {
                    if (firstRow == -1) {
                        firstRow = i;
                    }
                    selectionTableModel.addSelectionInterval(i, i);
                        BookmarkMarker marker = new BookmarkMarker(i);
                        m_markerContainerPanel.addMarker(marker);
                    if (selectedData.isEmpty()) {
                        break;
                    }
                }
            }
            
            // scroll to the first row
            if (firstRow != -1) {
                final int row = firstRow;
                scrollToVisible(row);
            }
            
        }

        @Override
        public TablePopupMenu initPopupMenu() {
            TablePopupMenu popupMenu = new TablePopupMenu();

            popupMenu.addAction(new RestrainAction() {
                @Override
                public void filteringDone() {
                    m_dataBox.propagateDataChanged(CompareDataInterface.class);
                }
            });
            popupMenu.addAction(new ClearRestrainAction() {
                @Override
                public void filteringDone() {
                    m_dataBox.propagateDataChanged(CompareDataInterface.class);
                }
            });

            return popupMenu;
        }

        // set as abstract
        @Override
        public void prepostPopupMenu() {
            // nothing to do
        }

        
        
    }
    

    
}
