package fr.proline.studio.rsmexplorer.gui;

import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.studio.comparedata.AddDataMixerButton;
import fr.proline.studio.rsmexplorer.gui.dialog.CalcDialog;
import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.comparedata.GlobalTabelModelProviderInterface;
import fr.proline.studio.dpm.data.SpectralCountResultData;
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
import fr.proline.studio.python.data.TableInfo;
import fr.proline.studio.rsmexplorer.gui.model.WSCProteinTableModel;
import fr.proline.studio.search.AbstractSearch;
import fr.proline.studio.search.SearchFloatingPanel;
import fr.proline.studio.search.SearchToggleButton;
import fr.proline.studio.table.CompoundTableModel;
import fr.proline.studio.table.GlobalTableModelInterface;
import fr.proline.studio.utils.IconManager;
import fr.proline.studio.table.LazyTable;
import fr.proline.studio.table.TablePopupMenu;
import fr.proline.studio.utils.URLCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.jdesktop.swingx.table.TableColumnExt;
import org.openide.windows.WindowManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.jdesktop.swingx.JXTable;

/**
 * Panel for Protein Matches
 *
 * @author JM235353
 */
public class WSCResultPanel extends HourglassPanel implements DataBoxPanelInterface, GlobalTabelModelProviderInterface {

    private AbstractDataBox m_dataBox;
    
    private SpectralCountResultData m_weightedSCResult = null;
    
    private WSCProteinTable m_WSCProteinTable;
    private JScrollPane m_scrollPane;
    private SearchFloatingPanel m_searchPanel;
    private JToggleButton m_searchToggleButton;
    private Search m_search = null;
    private FilterButtonV2 m_filterButton;
    private ExportButton m_exportButton;    
    private JButton m_columnVisibilityButton;
    private AddDataMixerButton m_addCompareDataButton;
    private JButton m_testButton;
    
    private MarkerContainerPanel m_markerContainerPanel;
    
    /**
     * Creates new form RsmProteinsOfProteinSetPanel
     */
    public WSCResultPanel() {
        initComponents();

        CompoundTableModel model = (CompoundTableModel) m_WSCProteinTable.getModel();
        
         URLCellRenderer renderer = (URLCellRenderer) model.getRenderer(WSCProteinTableModel.COLTYPE_PROTEIN_NAME);
        m_WSCProteinTable.addMouseListener(renderer);
        m_WSCProteinTable.addMouseMotionListener(renderer);


        //TODO
        List<TableColumn> columns = m_WSCProteinTable.getColumns(true);
        ((TableColumnExt) columns.get(0)).setVisible(false);

        
    }

    public void setData(SpectralCountResultData scResult) {

        if (scResult == m_weightedSCResult) {
            return;
        }
        m_weightedSCResult = scResult;

        if (m_weightedSCResult == null) {
            clearData();
            return;
        }

        // Modify the Model
        ((WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel()).setData(scResult);

        // allow to change column visibility
        m_columnVisibilityButton.setEnabled(true);
        
        // update the number of lines
        m_markerContainerPanel.setMaxLineNumber(((CompoundTableModel) m_WSCProteinTable.getModel()).getRowCount());
        
        // table is sortable
        m_WSCProteinTable.setSortable(true);

    }

    private void clearData() {
        ((WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel()).setData(null);

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

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {

        setLayout(new BorderLayout());

        m_search = new Search();
        m_searchPanel = new SearchFloatingPanel(m_search);
        final JPanel spectralCountPanel = createSpectralCountPanel();
        m_searchPanel.setToggleButton(m_searchToggleButton);

        final JLayeredPane layeredPane = new JLayeredPane();

        layeredPane.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                final Component c = e.getComponent();

                spectralCountPanel.setBounds(0, 0, c.getWidth(), c.getHeight());
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

        layeredPane.add(spectralCountPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(m_searchPanel, JLayeredPane.PALETTE_LAYER);

    }

    private JPanel createSpectralCountPanel() {

        JPanel spectralCountPanel = new JPanel();
        spectralCountPanel.setBounds(0, 0, 500, 400);
        spectralCountPanel.setLayout(new BorderLayout());

        JPanel internalPanel = createInternalPanel();

        JToolBar toolbar = initToolbar();
        spectralCountPanel.add(toolbar, BorderLayout.WEST);
        spectralCountPanel.add(internalPanel, BorderLayout.CENTER);


        return spectralCountPanel;
    }

    private JPanel createInternalPanel() {
        JPanel internalPanel = new JPanel();

        internalPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(5, 5, 5, 5);


        m_scrollPane = new javax.swing.JScrollPane();
        m_WSCProteinTable = new WSCProteinTable();

        m_markerContainerPanel = new MarkerContainerPanel(m_scrollPane,  m_WSCProteinTable);
        
        m_WSCProteinTable.setModel(new CompoundTableModel(new WSCProteinTableModel(m_WSCProteinTable), true));
        m_scrollPane.setViewportView(m_WSCProteinTable);
        m_WSCProteinTable.setViewport(m_scrollPane.getViewport());


        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        internalPanel.add(m_markerContainerPanel, c);

        return internalPanel;
    }

    private JToolBar initToolbar() {
        JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
        toolbar.setFloatable(false);

        // Search Button
        m_searchToggleButton = new SearchToggleButton(m_searchPanel);
        toolbar.add(m_searchToggleButton);

        m_filterButton = new FilterButtonV2(((CompoundTableModel) m_WSCProteinTable.getModel())) {

            @Override
            protected void filteringDone() {
                m_dataBox.propagateDataChanged(CompareDataInterface.class);
            }
            
        };

        m_exportButton = new ExportButton(((CompoundTableModel) m_WSCProteinTable.getModel()), "Spectral Counts", m_WSCProteinTable);

        
        m_columnVisibilityButton = new JButton();
        m_columnVisibilityButton.setIcon(IconManager.getIcon(IconManager.IconType.COLUMNS_VISIBILITY));
        m_columnVisibilityButton.setToolTipText("Hide/Show Columns...");
        m_columnVisibilityButton.setEnabled(false);
        m_columnVisibilityButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnsVisibilityDialog dialog = new ColumnsVisibilityDialog(WindowManager.getDefault().getMainWindow(), m_WSCProteinTable, (WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel() );
                dialog.setLocation(m_columnVisibilityButton.getLocationOnScreen().x +m_columnVisibilityButton.getWidth(), m_columnVisibilityButton.getLocationOnScreen().y + m_columnVisibilityButton.getHeight());
                dialog.setVisible(true);
            }
        });
        
        m_addCompareDataButton = new AddDataMixerButton(((CompoundTableModel) m_WSCProteinTable.getModel())) {

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
        
        
        m_testButton = new JButton(IconManager.getIcon(IconManager.IconType.CALCULATOR));
        m_testButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CalcDialog dialog = CalcDialog.getCalcDialog(WindowManager.getDefault().getMainWindow(), m_WSCProteinTable);
                dialog.centerToWindow(WindowManager.getDefault().getMainWindow());
                dialog.setVisible(true);

            }

        });
        
        
        toolbar.add(m_filterButton);
        toolbar.add(m_exportButton);
        toolbar.add(m_columnVisibilityButton);
        toolbar.add(m_addCompareDataButton);
        toolbar.add(m_testButton);
        
        return toolbar;
    }

    @Override
    public GlobalTableModelInterface getGlobalTableModelInterface() {
        return (GlobalTableModelInterface) m_WSCProteinTable.getModel();
    }
    
    @Override
    public JXTable getGlobalAssociatedTable() {
        return m_WSCProteinTable;
    }

    @Override
    public CrossSelectionInterface getCrossSelectionInterface() {
        return m_WSCProteinTable;
    }

    private class WSCProteinTable extends LazyTable implements ExportModelInterface {

        public WSCProteinTable() {
            super(m_scrollPane.getVerticalScrollBar());
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            getModel().addTableModelListener(l);
        }
        
        public boolean selectProtein(Integer row) {

            // must convert row index if there is a sorting
            row = convertRowIndexToView(row);

            // select the row
            getSelectionModel().setSelectionInterval(row, row);

            // scroll to the row
            scrollRowToVisible(row);

            return true;
        }

        /**
         * Called whenever the value of the selection changes.
         *
         * @param e the event that characterizes the change.
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {

            super.valueChanged(e);

            m_dataBox.propagateDataChanged(DProteinMatch.class);


        }

        @Override
        public void sortingChanged(int col) {
            m_search.reinitSearch();
        }

        @Override
        public boolean isLoaded() {
            return true; // not used
        }

        @Override
        public int getLoadingPercentage() {
            return 0; // not used
        }

        @Override
        public String getExportColumnName(int col) {
            return ((CompoundTableModel) m_WSCProteinTable.getModel()).getExportColumnName(convertColumnIndexToModel(col));
        }

        @Override
        public String getExportRowCell(int row, int col) {
            return ((CompoundTableModel) m_WSCProteinTable.getModel()).getExportRowCell(convertRowIndexToModel(row),  convertColumnIndexToModel(col));
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

    private class Search extends AbstractSearch {

        private String previousSearch = "";
        private int searchIndex = 0;
        private ArrayList<Integer> proteinNamesRow = new ArrayList<>();

        @Override
        public void reinitSearch() {
            if (proteinNamesRow.isEmpty()) {
                return;
            }
            searchIndex = -1;
            ((WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel()).sortAccordingToModel(proteinNamesRow, (CompoundTableModel) m_WSCProteinTable.getModel());
        }

        @Override
        public void doSearch(String text) {
            final String searchText = text.trim().toUpperCase();

            if (searchText.compareTo(previousSearch) == 0) {

                int checkLoopIndex = -1;
                while (true) {
                    // search already done, display next result
                    searchIndex++;
                    if (searchIndex >= proteinNamesRow.size()) {
                        searchIndex = 0;
                    }

                    if (checkLoopIndex == searchIndex) {
                        break;
                    }

                    if (!proteinNamesRow.isEmpty()) {
                        boolean found = m_WSCProteinTable.selectProtein(proteinNamesRow.get(searchIndex));
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

            } else {
                previousSearch = searchText;
                searchIndex = -1;

                String regex = wildcardToRegex(searchText);

                WSCProteinTableModel model = (WSCProteinTableModel) (((CompoundTableModel) m_WSCProteinTable.getModel())).getBaseModel();

                proteinNamesRow.clear();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String name = (String) model.getValueAt(i, WSCProteinTableModel.COLTYPE_PROTEIN_NAME);
                    if (name.matches(regex)) {
                        proteinNamesRow.add(i);
                    }
                }

                if (!proteinNamesRow.isEmpty()) {

                    ((WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel()).sortAccordingToModel(proteinNamesRow, (CompoundTableModel) m_WSCProteinTable.getModel());

                    int checkLoopIndex = -1;
                    while (true) {
                        // search already done, display next result
                        searchIndex++;
                        if (searchIndex >= proteinNamesRow.size()) {
                            searchIndex = 0;
                        }

                        if (checkLoopIndex == searchIndex) {
                            break;
                        }

                        if (!proteinNamesRow.isEmpty()) {
                            boolean found = m_WSCProteinTable.selectProtein(proteinNamesRow.get(searchIndex));
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


            }

        }
    }
    
    private class ColumnsVisibilityDialog extends DefaultDialog {

        private JCheckBoxList m_rsmList;
        private JCheckBoxList m_spectralCountList;
        
        private JRadioButton m_noOverviewRB;
        private JRadioButton m_basicSCOverviewRB;
        private JRadioButton m_specificSCOverviewRB;
        private JRadioButton m_weightedSCOverviewRB;

        public ColumnsVisibilityDialog(Window parent, WSCProteinTable table, WSCProteinTableModel proteinTableModel) {
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
            int nbTypes = proteinTableModel.getByRsmCount();
            List<String> typeList = new ArrayList<>();
            List<Boolean> visibilityTypeList = new ArrayList<>();
            boolean[] visibilityTypeArray = new boolean[nbTypes];
            for (int i=0;i<nbTypes;i++) {
                visibilityTypeArray[i] = false;
            }
            
            
            List<TableColumn> columns = table.getColumns(true);

            int rsmCount = proteinTableModel.getRsmCount();
            for (int i = 0; i < rsmCount; i++) {
                int start = proteinTableModel.getColumStart(i);
                int stop = proteinTableModel.getColumStop(i);

                boolean rsmVisible = false;
                for (int j = start; j <= stop; j++) {
                    boolean columnVisible = ((TableColumnExt) columns.get(j)).isVisible();
                    if (columnVisible) {
                        rsmVisible = true;
                        int type = j-start;
                        visibilityTypeArray[type] |= columnVisible;
                    }

                    
                }

                String rsmName = proteinTableModel.getRsmName(i);
                rsmList.add(rsmName);
                visibilityRsmList.add(rsmVisible);

            }
            
            for (int i = 0; i < nbTypes; i++) {
                String name = proteinTableModel.getByRsmColumnName(i);
                typeList.add(name);
                visibilityTypeList.add(visibilityTypeArray[i]);
            }
            
            
             int overviewType = proteinTableModel.getOverviewType();
            boolean overviewColumnVisible = ((TableColumnExt) columns.get(WSCProteinTableModel.COLTYPE_OVERVIEW)).isVisible();
            
            
            JPanel internalPanel = createInternalPanel(rsmList, visibilityRsmList, typeList, visibilityTypeList, overviewColumnVisible, overviewType);
            setInternalComponent(internalPanel);


            
        }

        private JPanel createInternalPanel(List<String> rsmList, List<Boolean> visibilityList, List<String> typeList, List<Boolean> visibilityTypeList, boolean overviewColumnVisible, int overviewType) {
            JPanel internalPanel = new JPanel();

            internalPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.NORTHWEST; 
            c.fill = GridBagConstraints.BOTH;
            c.insets = new java.awt.Insets(5, 5, 5, 5);

            JLabel idSummaryLabel = new JLabel("Identification Summaries");
            JLabel informationLabel = new JLabel("Information");
            JLabel overviewLabel = new JLabel("Overview");
            
            JSeparator separator1 = new JSeparator(JSeparator.VERTICAL);
            JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);

            JScrollPane rsmScrollPane = new JScrollPane();
            m_rsmList = new JCheckBoxList(rsmList, visibilityList);
            rsmScrollPane.setViewportView(m_rsmList);

            JScrollPane spectralCountScrollPane = new JScrollPane();
            m_spectralCountList = new JCheckBoxList(typeList, visibilityTypeList); 
            spectralCountScrollPane.setViewportView(m_spectralCountList);

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
            internalPanel.add(spectralCountScrollPane, c);


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
            m_basicSCOverviewRB = new JRadioButton("Overview on Basic SC");
            m_specificSCOverviewRB = new JRadioButton("Overview on Specific SC");
            m_weightedSCOverviewRB = new JRadioButton("Overview on Weighted SC");
            m_noOverviewRB.setBackground(Color.white);
            m_basicSCOverviewRB.setBackground(Color.white);
            m_specificSCOverviewRB.setBackground(Color.white);
            m_weightedSCOverviewRB.setBackground(Color.white);
            
            ButtonGroup group = new ButtonGroup();
            group.add(m_noOverviewRB);
            group.add(m_basicSCOverviewRB);
            group.add(m_specificSCOverviewRB);
            group.add(m_weightedSCOverviewRB);
            
            if (!overviewColumnVisible) {
                m_noOverviewRB.setSelected(true);
            } else {
                switch (overviewType) {
                    case WSCProteinTableModel.COLTYPE_BSC:
                        m_basicSCOverviewRB.setSelected(true);
                        break;
                    case WSCProteinTableModel.COLTYPE_SSC:
                        m_specificSCOverviewRB.setSelected(true);
                        break;
                    case WSCProteinTableModel.COLTYPE_WSC:
                        m_weightedSCOverviewRB.setSelected(true);
                        break;

                }
            }
            
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            
            overviewPanel.add(m_noOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_basicSCOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_specificSCOverviewRB, c);
            
            c.gridy++;
            overviewPanel.add(m_weightedSCOverviewRB, c);

            c.gridy++;
            c.weighty = 1;
            overviewPanel.add(Box.createHorizontalGlue(), c);
            
            
            return overviewPanel;
        }
        
        @Override
        protected boolean okCalled() {

            WSCProteinTableModel model = (WSCProteinTableModel) ((CompoundTableModel) m_WSCProteinTable.getModel()).getBaseModel();
            
            int nbColumnsModel = model.getColumnCount();
            List<TableColumn> columns = m_WSCProteinTable.getColumns(true);
            for (int i=WSCProteinTableModel.COLTYPE_STATUS;i<nbColumnsModel;i++) {
                int rsmCur = model.getRsmNumber(i);
                int type = model.getTypeNumber(i);
                boolean visible = m_rsmList.isVisible(rsmCur) && m_spectralCountList.isVisible(type);
                boolean columnVisible = ((TableColumnExt) columns.get(i)).isVisible();
                if (visible ^ columnVisible) {
                    ((TableColumnExt) columns.get(i)).setVisible(visible);
                }
            }
            
            if (m_basicSCOverviewRB.isSelected()) {
                model.setOverviewType(WSCProteinTableModel.COLTYPE_BSC);
            } else if (m_specificSCOverviewRB.isSelected()) {
                model.setOverviewType(WSCProteinTableModel.COLTYPE_SSC);
            } else if (m_weightedSCOverviewRB.isSelected()) {
                model.setOverviewType(WSCProteinTableModel.COLTYPE_WSC);
            }
            
            boolean overviewVisible = !m_noOverviewRB.isSelected();
            boolean columnVisible = ((TableColumnExt) columns.get(WSCProteinTableModel.COLTYPE_OVERVIEW)).isVisible();
            if (overviewVisible ^ columnVisible) {
                ((TableColumnExt) columns.get(WSCProteinTableModel.COLTYPE_OVERVIEW)).setVisible(overviewVisible);
            }

            
            

            return true;
        }

        @Override
        protected boolean cancelCalled() {

            return true;
        }

    }
    
}
