package fr.proline.studio.rsmexplorer.gui.xic;

import fr.proline.core.orm.msi.dto.DMasterQuantPeptide;
import fr.proline.core.orm.uds.dto.DQuantitationChannel;
import fr.proline.studio.comparedata.AddDataMixerButton;
import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.comparedata.GlobalTabelModelProviderInterface;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.DatabaseSearchPeptideInstanceTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.export.ExportButton;
import fr.proline.studio.export.ExportModelInterface;
import fr.proline.studio.filter.FilterButtonV2;
import fr.proline.studio.filter.actions.ClearRestrainAction;
import fr.proline.studio.filter.actions.RestrainAction;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.gui.HourglassPanel;
import fr.proline.studio.gui.JCheckBoxList;
import fr.proline.studio.gui.SplittedPanelContainer;
import fr.proline.studio.markerbar.MarkerContainerPanel;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import fr.proline.studio.pattern.DataMixerWindowBoxManager;
import fr.proline.studio.pattern.WindowBox;
import fr.proline.studio.pattern.WindowBoxFactory;
import fr.proline.studio.progress.ProgressBarDialog;
import fr.proline.studio.python.data.TableInfo;
import fr.proline.studio.rsmexplorer.DataBoxViewerTopComponent;
import fr.proline.studio.search.AbstractSearch;
import fr.proline.studio.search.SearchFloatingPanel;
import fr.proline.studio.search.SearchToggleButton;
import fr.proline.studio.table.CompoundTableModel;
import fr.proline.studio.table.CustomColumnControlButton;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.utils.IconManager;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.TablePopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;
import org.openide.windows.WindowManager;

/**
 *
 * @author JM235353
 */
public class XicPeptidePanel  extends HourglassPanel implements DataBoxPanelInterface, GlobalTabelModelProviderInterface {

    private AbstractDataBox m_dataBox;

    private JScrollPane m_peptideScrollPane;
    private QuantPeptideTable m_quantPeptideTable;

    private MarkerContainerPanel m_markerContainerPanel;
    
    private boolean m_displayForProteinSet;
    private DQuantitationChannel[] m_quantChannels;
    private boolean m_isXICMode;

    private FilterButtonV2 m_filterButton;
    private ExportButton m_exportButton;
    private JButton m_columnVisibilityButton;
    private JButton m_graphicsButton;
    private AddDataMixerButton m_addCompareDataButton;
    
    private SearchFloatingPanel m_searchPanel;
    private JToggleButton m_searchToggleButton;
    private XICPeptideSearch m_search = null;
    
    private JLabel m_titleLabel;
    private static final String TABLE_TITLE = "Peptides";
    
    private final boolean m_canGraph ;

    public XicPeptidePanel(boolean canGraph) {
        this.m_canGraph = canGraph ;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(5000);
        
        m_search = new XICPeptideSearch();
        m_searchPanel = new SearchFloatingPanel(m_search);
        final JPanel peptidePanel = createPeptidePanel();
        m_searchPanel.setToggleButton(m_searchToggleButton); 

        final JLayeredPane layeredPane = new JLayeredPane();

        layeredPane.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                final Component c = e.getComponent();

                peptidePanel.setBounds(0, 0, c.getWidth(), c.getHeight());
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

        layeredPane.add(peptidePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(m_searchPanel, JLayeredPane.PALETTE_LAYER); 


    }
        
    private JPanel createPeptidePanel() {

        JPanel peptidePanel = new JPanel();
        peptidePanel.setBounds(0, 0, 500, 400);
        peptidePanel.setLayout(new BorderLayout());

        JPanel internalPanel = createInternalPanel();

        JToolBar toolbar = initToolbar();
        m_titleLabel = new JLabel(TABLE_TITLE);
        peptidePanel.add(m_titleLabel, BorderLayout.NORTH);
        peptidePanel.add(toolbar, BorderLayout.WEST);
        peptidePanel.add(internalPanel, BorderLayout.CENTER);

        return peptidePanel;
    }
    
    
    private JToolBar initToolbar() {
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(false);

        // Search Button
        m_searchToggleButton = new SearchToggleButton(m_searchPanel);
        toolbar.add(m_searchToggleButton);
        
        m_filterButton = new FilterButtonV2(((CompoundTableModel) m_quantPeptideTable.getModel())) {

            @Override
            protected void filteringDone() {
                m_dataBox.propagateDataChanged(CompareDataInterface.class);
            }
            
        };

        m_exportButton = new ExportButton(((CompoundTableModel) m_quantPeptideTable.getModel()), "Peptides", m_quantPeptideTable);

        toolbar.add(m_filterButton);
        toolbar.add(m_exportButton);
        
        m_columnVisibilityButton = new JButton();
        m_columnVisibilityButton.setIcon(IconManager.getIcon(IconManager.IconType.COLUMNS_VISIBILITY));
        m_columnVisibilityButton.setToolTipText("Hide/Show Columns...");
        m_columnVisibilityButton.setEnabled(false);
        m_columnVisibilityButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                XICPeptideColumnsVisibilityDialog dialog = new XICPeptideColumnsVisibilityDialog(WindowManager.getDefault().getMainWindow(), m_quantPeptideTable, (QuantPeptideTableModel)((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel() );
                dialog.setLocation(m_columnVisibilityButton.getLocationOnScreen().x +m_columnVisibilityButton.getWidth(), m_columnVisibilityButton.getLocationOnScreen().y + m_columnVisibilityButton.getHeight());
                dialog.setVisible(true);
            }

        });
        toolbar.add(m_columnVisibilityButton);
        
        // graphics button
        m_graphicsButton = new JButton(IconManager.getIcon(IconManager.IconType.CHART));
        m_graphicsButton.setToolTipText("Graphics : Linear Plot");
        m_graphicsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!((CompoundTableModel) m_quantPeptideTable.getModel()).isLoaded()) {

                    ProgressBarDialog dialog = ProgressBarDialog.getDialog(WindowManager.getDefault().getMainWindow(), ((CompoundTableModel) m_quantPeptideTable.getModel()), "Data loading", "Histogram functionnality is not available while data is loading. Please Wait.");
                    dialog.setLocation(getLocationOnScreen().x + m_graphicsButton.getWidth() + 5, m_graphicsButton.getLocationOnScreen().y + getHeight() + 5);
                    dialog.setVisible(true);

                    if (!dialog.isWaitingFinished()) {
                        return;
                    }
                }
                // prepare window box
                WindowBox wbox = WindowBoxFactory.getMultiGraphicsWindowBox("Peptide Graphic", m_dataBox, false);
                wbox.setEntryData(m_dataBox.getProjectId(), m_dataBox.getData(false, List.class));

                // open a window to display the window box
                DataBoxViewerTopComponent win = new DataBoxViewerTopComponent(wbox);
                win.open();
                win.requestActive();
            }
        });
        if (m_canGraph){
            toolbar.add(m_graphicsButton);
        }
        
        m_addCompareDataButton = new AddDataMixerButton(((CompoundTableModel) m_quantPeptideTable.getModel())) {
            
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
        m_peptideScrollPane = new JScrollPane();
        
        m_quantPeptideTable = new QuantPeptideTable();
        m_quantPeptideTable.setModel(new CompoundTableModel(new QuantPeptideTableModel((LazyTable)m_quantPeptideTable), true));
        CustomColumnControlButton customColumnControl = new CustomColumnControlButton(m_quantPeptideTable);
        m_quantPeptideTable.setColumnControl(customColumnControl);
        // hide the id column
        m_quantPeptideTable.getColumnExt(m_quantPeptideTable.convertColumnIndexToView(QuantPeptideTableModel.COLTYPE_PEPTIDE_ID)).setVisible(false);
        
        m_quantPeptideTable.setSortable(false);

        m_markerContainerPanel = new MarkerContainerPanel(m_peptideScrollPane, m_quantPeptideTable);
        
        m_peptideScrollPane.setViewportView(m_quantPeptideTable);
        m_quantPeptideTable.setFillsViewportHeight(true);
        m_quantPeptideTable.setViewport(m_peptideScrollPane.getViewport());


        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        internalPanel.add(m_markerContainerPanel, c);
        

        
        return internalPanel;
    }                 
    
    public void setData(Long taskId, boolean displayForProteinSet, DQuantitationChannel[] quantChannels,  List<DMasterQuantPeptide> peptides, boolean isXICMode, boolean finished) {
        boolean qcChanged = true;
        if (m_quantChannels != null && m_quantChannels.length == quantChannels.length) {
            for (int q = 0; q < m_quantChannels.length; q++) {
                qcChanged = !(m_quantChannels[q].equals(quantChannels[q]));
            }
        }
        m_quantChannels = quantChannels;
        m_isXICMode = isXICMode;
        this.m_displayForProteinSet = displayForProteinSet;
        ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).setData(taskId, quantChannels, peptides, m_isXICMode);
        //m_quantPeptideTable.setColumnControlVisible(((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).getColumnCount() < XicProteinSetPanel.NB_MAX_COLUMN_CONTROL);
        m_titleLabel.setText(TABLE_TITLE +" ("+peptides.size()+")");
        if (!m_isXICMode){
            ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).setOverviewType(QuantPeptideTableModel.COLTYPE_RAW_ABUNDANCE);
        }
        // select the first row
        if ((peptides != null) && (peptides.size() > 0)) {
            m_quantPeptideTable.getSelectionModel().setSelectionInterval(0, 0);
            m_markerContainerPanel.setMaxLineNumber(peptides.size());
        }

        if (finished) {
            // allow to change column visibility
            m_columnVisibilityButton.setEnabled(true);
            m_quantPeptideTable.setSortable(true);
        }
        if (qcChanged){
            setColumnsVisibility();
        }
    }
    
    public void dataUpdated(SubTask subTask, boolean finished) {
        m_quantPeptideTable.dataUpdated(subTask, finished);
        if (finished) {
            // allow to change column visibility
            m_columnVisibilityButton.setEnabled(true);
            m_quantPeptideTable.setSortable(true);
        }
    }
    
    public void setColumnsVisibility() {
        // hide the rawAbundance  and selectionLevel columns
        List<Integer> listIdsToHide = ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).getDefaultColumnsToHide();
        List<TableColumn> columns = m_quantPeptideTable.getColumns(true);
        for (Integer id : listIdsToHide) {
            boolean columnVisible = ((TableColumnExt) columns.get(id)).isVisible();
            if (columnVisible) {
                m_quantPeptideTable.getColumnExt(m_quantPeptideTable.convertColumnIndexToView(id)).setVisible(false);
            }
        }
        if (!m_displayForProteinSet) {
            // hide the cluster column
            boolean columnVisible = ((TableColumnExt) columns.get(QuantPeptideTableModel.COLTYPE_PEPTIDE_CLUSTER)).isVisible();
            if(columnVisible) {
                m_quantPeptideTable.getColumnExt(m_quantPeptideTable.convertColumnIndexToView(QuantPeptideTableModel.COLTYPE_PEPTIDE_CLUSTER)).setVisible(false);
            }
        }
        // hide the id column
        boolean columnVisible = ((TableColumnExt) columns.get(QuantPeptideTableModel.COLTYPE_PEPTIDE_ID)).isVisible();
        if(columnVisible) {
            m_quantPeptideTable.getColumnExt(m_quantPeptideTable.convertColumnIndexToView(QuantPeptideTableModel.COLTYPE_PEPTIDE_ID)).setVisible(false);
        }
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
    public GlobalTableModelInterface getGlobalTableModelInterface() {
        return (GlobalTableModelInterface) m_quantPeptideTable.getModel();
    }
    @Override
    public JXTable getGlobalAssociatedTable() {
        return m_quantPeptideTable;
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

    public DMasterQuantPeptide getSelectedMasterQuantPeptide() {
        return m_quantPeptideTable.getSelectedMasterQuantPeptide();
    }

    @Override
    public CrossSelectionInterface getCrossSelectionInterface() {
        return m_quantPeptideTable;
    }
    
    private class QuantPeptideTable extends LazyTable implements ExportModelInterface  {

        public QuantPeptideTable() {
            super(m_peptideScrollPane.getVerticalScrollBar() );

        }
        
        @Override
        public void addTableModelListener(TableModelListener l) {
            getModel().addTableModelListener(l);
        }
        
        //Implement table cell tool tips.
        @Override
        public String getToolTipText(MouseEvent e) {
            Point p = e.getPoint();
            int rowIndex = rowAtPoint(p);
            if (rowIndex<0) {
                return null;
            }
            int colIndex = columnAtPoint(p);
            if (colIndex<0) {
                return null;
            }
            int realColumnIndex = convertColumnIndexToModel(colIndex);
            int realRowIndex = convertRowIndexToModel(rowIndex);
            CompoundTableModel tableModel = (CompoundTableModel) getModel();
            return tableModel.getTootlTipValue(realRowIndex, realColumnIndex);
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
 
            m_dataBox.propagateDataChanged(DMasterQuantPeptide.class);

        }
        
        public boolean selectPeptide(Long peptideId, String searchText) {
  
            QuantPeptideTableModel tableModel = (QuantPeptideTableModel) ((CompoundTableModel)getModel()).getBaseModel();
            int row = tableModel.findRow(peptideId);
            if (row == -1) {
                return false;
            }
            row = ((CompoundTableModel)getModel()).convertBaseModelRowToCompoundRow(row);
            if (row == -1) {
                return false;
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
            
            return true;
        }
        String searchTextBeingDone = null;

        public void dataUpdated(SubTask subTask, boolean finished) {
            
            LastAction keepLastAction = m_lastAction;
            try {
            
            
            // retrieve selected row
            int rowSelected = getSelectionModel().getMinSelectionIndex();
            int rowSelectedInModel = (rowSelected == -1) ? -1 : convertRowIndexToModel(rowSelected);

            // Update Model (but protein set table must not react to the model update)
            
            selectionWillBeRestored(true);
            try {
                ((QuantPeptideTableModel) (((CompoundTableModel) getModel()).getBaseModel())).dataUpdated();
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
                if (((keepLastAction == LastAction.ACTION_SELECTING ) || (keepLastAction == LastAction.ACTION_SORTING)) && (subTask.getSubTaskId() == ((CompoundTableModel) getModel()).getSubTaskId( getSortedColumnIndex() )) ) {
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
        
        @Override
        public void sortingChanged(int col) {
            m_search.reinitSearch(); 
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
        


        public DMasterQuantPeptide getSelectedMasterQuantPeptide() {

            // Retrieve Selected Row
            int selectedRow = getSelectedRow();

            // nothing selected
            if (selectedRow == -1) {
                return null;

            }
            
            CompoundTableModel compoundTableModel = (CompoundTableModel) getModel();
            if (compoundTableModel.getRowCount() == 0) {
                return null; // this is a wart, for an unknown reason, it happens that the first row
                // is selected although it does not exist.
            }

            // convert according to the sorting
            selectedRow = convertRowIndexToModel(selectedRow);
            selectedRow = compoundTableModel.convertCompoundRowToBaseModelRow(selectedRow);

            // Retrieve PeptideIon selected
            QuantPeptideTableModel tableModel = (QuantPeptideTableModel) compoundTableModel.getBaseModel();
            return tableModel.getPeptide(selectedRow);

        }

        @Override
        public String getExportColumnName(int col) {
            return ((CompoundTableModel) m_quantPeptideTable.getModel()).getExportColumnName(convertColumnIndexToModel(col));
        }

        @Override
        public String getExportRowCell(int row, int col) {
            return ((CompoundTableModel) m_quantPeptideTable.getModel()).getExportRowCell(convertRowIndexToModel(row),  convertColumnIndexToModel(col));
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

    
    private class XICPeptideColumnsVisibilityDialog extends DefaultDialog {

        private JCheckBoxList m_rsmList;
        private JCheckBoxList m_xicList;
        
        private JRadioButton m_noOverviewRB;
        private JRadioButton m_psmOverviewRB;
        private JRadioButton m_abundanceOverviewRB;
        private JRadioButton m_rawAbundanceOverviewRB;
        

        public XICPeptideColumnsVisibilityDialog(Window parent, QuantPeptideTable table, QuantPeptideTableModel quantPeptideTableModel) {
            super(parent, Dialog.ModalityType.APPLICATION_MODAL);

            // hide default and help buttons
            setButtonVisible(BUTTON_HELP, false);

            setStatusVisible(false);

            setResizable(true);

            setTitle("Select Columns to Display");

            // Prepare data for RSM
            List<String> rsmList = new ArrayList<>();
            List<Boolean> visibilityRsmList = new ArrayList<>();

            // Prepare data for different column types
            int nbTypes = quantPeptideTableModel.getByQCCount();
            List<String> typeList = new ArrayList<>();
            List<Boolean> visibilityTypeList = new ArrayList<>();
            boolean[] visibilityTypeArray = new boolean[nbTypes];
            for (int i=0;i<nbTypes;i++) {
                visibilityTypeArray[i] = false;
            }
            
            
            List<TableColumn> columns = table.getColumns(true);

            int rsmCount = quantPeptideTableModel.getQCCount();
            for (int i = 0; i < rsmCount; i++) {
                int start = quantPeptideTableModel.getColumStart(i);
                int stop = quantPeptideTableModel.getColumStop(i);

                boolean rsmVisible = false;
                for (int j = start; j <= stop; j++) {
                    boolean columnVisible = ((TableColumnExt) columns.get(j)).isVisible();
                    if (columnVisible) {
                        rsmVisible = true;
                        int type = j-start;
                        visibilityTypeArray[type] |= columnVisible;
                    }

                    
                }

                String rsmName = quantPeptideTableModel.getQCName(i);
                rsmList.add(rsmName);
                visibilityRsmList.add(rsmVisible);

            }
            
            for (int i = 0; i < nbTypes; i++) {
                String name = quantPeptideTableModel.getByQCMColumnName(i);
                typeList.add(name);
                visibilityTypeList.add(visibilityTypeArray[i]);
            }
            
             
            int overviewType = quantPeptideTableModel.getOverviewType();
            boolean overviewColumnVisible = ((TableColumnExt) columns.get(QuantPeptideTableModel.COLTYPE_OVERVIEW)).isVisible();
            
            
            JPanel internalPanel = createInternalPanel(rsmList, visibilityRsmList, typeList, visibilityTypeList, overviewColumnVisible, overviewType);
            setInternalComponent(internalPanel);


            
        }

        private JPanel createInternalPanel(List<String> rsmList, List<Boolean> visibilityList, List<String> typeList, List<Boolean> visibilityTypeList,  boolean overviewColumnVisible, int overviewType) {
            JPanel internalPanel = new JPanel();

            internalPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.NORTHWEST; 
            c.fill = GridBagConstraints.BOTH;
            c.insets = new java.awt.Insets(5, 5, 5, 5);

            JLabel idSummaryLabel = new JLabel("Quantitation channel");
            JLabel informationLabel = new JLabel("Information");
            JLabel overviewLabel = new JLabel("Overview");
            
            JSeparator separator1 = new JSeparator(JSeparator.VERTICAL);
            JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);

            JScrollPane rsmScrollPane = new JScrollPane();
            m_rsmList = new JCheckBoxList(rsmList, visibilityList);
            rsmScrollPane.setViewportView(m_rsmList);

            JScrollPane xicScrollPane = new JScrollPane();
            m_xicList = new JCheckBoxList(typeList, visibilityTypeList); 
            xicScrollPane.setViewportView(m_xicList);
            
            JScrollPane overviewScrollPane = new JScrollPane();
            overviewScrollPane.setViewportView(createOverviewPanel(overviewColumnVisible, overviewType));

            c.gridx = 0;
            c.gridy = 0;
            
            internalPanel.add(idSummaryLabel, c);
            
            c.gridy++;
            c.weightx = 1;
            c.weighty = 1;
            internalPanel.add(rsmScrollPane, c);

            c.gridy = 0;
            c.gridx++;
            c.weightx = 0;
            c.weighty = 1;
            c.gridheight = 2;
            internalPanel.add(separator1, c);
            

            c.gridx++;
            c.gridheight = 1;
            c.weightx = 0;
            c.weighty = 0;
            internalPanel.add(informationLabel, c);
            
            c.gridy++;
            c.weightx = 1;
            c.weighty = 1;
            internalPanel.add(xicScrollPane, c);
            
            c.gridy = 0;
            c.gridx++;
            c.weightx = 0;
            c.weighty = 1;
            c.gridheight = 2;
            internalPanel.add(separator2, c);
            

            c.gridx++;
            c.gridheight = 1;
            c.weightx = 0;
            c.weighty = 0;
            internalPanel.add(overviewLabel, c);
            
            c.gridy++;
            c.weightx = 1;
            c.weighty = 1;
            internalPanel.add(overviewScrollPane, c);

            return internalPanel;
        }
        
        private JPanel createOverviewPanel(boolean overviewColumnVisible, int overviewType) {
            JPanel overviewPanel = new JPanel();
            overviewPanel.setBackground(Color.white);

            overviewPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.NORTHWEST; 
            c.fill = GridBagConstraints.BOTH;
            c.insets = new java.awt.Insets(0, 0, 0, 0);
            
            m_noOverviewRB = new JRadioButton("No Overview");
            m_psmOverviewRB = new JRadioButton(m_isXICMode ? "Overview on Pep. Match Count" : "Overview on Basic SC");
            m_abundanceOverviewRB = new JRadioButton("Overview on Abundance");
            m_rawAbundanceOverviewRB = new JRadioButton(m_isXICMode ?"Overview on Raw Abundance" : "Overview on Specific SC");
            m_noOverviewRB.setBackground(Color.white);
            m_psmOverviewRB.setBackground(Color.white);
            m_abundanceOverviewRB.setBackground(Color.white);
            m_rawAbundanceOverviewRB.setBackground(Color.white);
            
            ButtonGroup group = new ButtonGroup();
            group.add(m_noOverviewRB);
            group.add(m_psmOverviewRB);
            group.add(m_abundanceOverviewRB);
            group.add(m_rawAbundanceOverviewRB);
            
            if (!overviewColumnVisible) {
                m_noOverviewRB.setSelected(true);
            } else {
                switch (overviewType) {
                    case QuantPeptideTableModel.COLTYPE_ABUNDANCE:
                        m_abundanceOverviewRB.setSelected(true);
                        break;
                    case QuantPeptideTableModel.COLTYPE_RAW_ABUNDANCE:
                        m_rawAbundanceOverviewRB.setSelected(true);
                        break;
                    case QuantPeptideTableModel.COLTYPE_PSM:
                        m_psmOverviewRB.setSelected(true);
                        break;

                }
            }
            
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            
            overviewPanel.add(m_noOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_psmOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_rawAbundanceOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_abundanceOverviewRB, c);
            
            c.gridy++;
            c.weighty = 1;
            overviewPanel.add(Box.createHorizontalGlue(), c);
            
            
            return overviewPanel;
        }

       
        
        @Override
        protected boolean okCalled() {

            QuantPeptideTableModel model = ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel());
            
            int nbColumnsModel = model.getColumnCount();
            List<TableColumn> columns = m_quantPeptideTable.getColumns(true);
            for (int i=QuantPeptideTableModel.LAST_STATIC_COLUMN+1;i<nbColumnsModel;i++) {
                int rsmCur = model.getQCNumber(i);
                int type = model.getTypeNumber(i);
                boolean visible = m_rsmList.isVisible(rsmCur) && m_xicList.isVisible(type);
                boolean columnVisible = ((TableColumnExt) columns.get(i)).isVisible();
                if (visible ^ columnVisible) {
                    ((TableColumnExt) columns.get(i)).setVisible(visible);
                }
            }
            
            if (m_abundanceOverviewRB.isSelected()) {
                model.setOverviewType(QuantPeptideTableModel.COLTYPE_ABUNDANCE);
            } else if (m_rawAbundanceOverviewRB.isSelected()) {
                model.setOverviewType(QuantPeptideTableModel.COLTYPE_RAW_ABUNDANCE);
            }else if (m_psmOverviewRB.isSelected()) {
                model.setOverviewType(QuantPeptideTableModel.COLTYPE_PSM);
            }
            
            boolean overviewVisible = !m_noOverviewRB.isSelected();
            boolean columnVisible = ((TableColumnExt) columns.get(QuantPeptideTableModel.COLTYPE_OVERVIEW)).isVisible();
            if (overviewVisible ^ columnVisible) {
                ((TableColumnExt) columns.get(QuantPeptideTableModel.COLTYPE_OVERVIEW)).setVisible(overviewVisible);
            }


            return true;
        }

        @Override
        protected boolean cancelCalled() {

            return true;
        }

    }
    
    
    
    private class XICPeptideSearch extends AbstractSearch {

        String previousSearch = "";
        int searchIndex = 0;
        ArrayList<Long> peptideInstanceIds = new ArrayList<>();

        @Override
        public void reinitSearch() {
            if (peptideInstanceIds.isEmpty()) {
                return;
            }
            searchIndex = -1;
            ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).sortAccordingToModel(peptideInstanceIds, (CompoundTableModel) m_quantPeptideTable.getModel());
        
        }

        @Override
        public void doSearch(String text) {
            final String searchText = text.trim().toUpperCase();

            if (searchText.compareTo(previousSearch) == 0) {
                
                int checkLoopIndex = -1;
                while (true) {
                    // search already done, display next result
                    searchIndex++;
                    if (searchIndex >= peptideInstanceIds.size()) {
                        searchIndex = 0;
                    }

                    if (checkLoopIndex == searchIndex) {
                        break;
                    }
                    
                    if (!peptideInstanceIds.isEmpty()) {
                        boolean found = ((QuantPeptideTable) m_quantPeptideTable).selectPeptide(peptideInstanceIds.get(searchIndex), searchText);
                        if (found) {
                            break;
                        }
                    } else {
                        break;
                    }
                    if (checkLoopIndex == -1) {
                        checkLoopIndex =  searchIndex;
                    }
                }
                
            } else {
                previousSearch = searchText;
                searchIndex = -1;

                // prepare callback for the search
                AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                    @Override
                    public boolean mustBeCalledInAWT() {
                        return true;
                    }

                    @Override
                    public void run(boolean success, long taskId, SubTask subTask, boolean finished) {

                        // contruct the Map of proteinSetId


                        if (!peptideInstanceIds.isEmpty()) {

                            ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).sortAccordingToModel(peptideInstanceIds, (CompoundTableModel) m_quantPeptideTable.getModel());
        
                             int checkLoopIndex = -1;
                             while (true) {
                                // search already done, display next result
                                searchIndex++;
                                if (searchIndex >= peptideInstanceIds.size()) {
                                    searchIndex = 0;
                                }

                                if (checkLoopIndex == searchIndex) {
                                    break;
                                }

                                if (!peptideInstanceIds.isEmpty()) {
                                    boolean found = ((QuantPeptideTable) m_quantPeptideTable).selectPeptide(peptideInstanceIds.get(searchIndex), searchText);
                                    if (found) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                                if (checkLoopIndex == -1) {
                                    checkLoopIndex = searchIndex;
                                }
                            }

                        }


                        //System.out.println("Ids size "+proteinSetIds.size());
                        m_searchPanel.enableSearch(true);
                    }
                };

                Long rsmId = ((QuantPeptideTableModel) ((CompoundTableModel) m_quantPeptideTable.getModel()).getBaseModel()).getResultSummaryId();

                // Load data if needed asynchronously
                AccessDatabaseThread.getAccessDatabaseThread().addTask(new DatabaseSearchPeptideInstanceTask(callback, m_dataBox.getProjectId(), rsmId, searchText, peptideInstanceIds));

                m_searchPanel.enableSearch(false);

            }
        }
    }
    
}
