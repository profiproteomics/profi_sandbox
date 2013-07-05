package fr.proline.studio.rsmexplorer.gui;

import fr.proline.core.orm.msi.ProteinMatch;
import fr.proline.core.orm.msi.ResultSet;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.uds.Dataset;
import fr.proline.studio.dam.AccessDatabaseThread;
import fr.proline.studio.dam.tasks.AbstractDatabaseCallback;
import fr.proline.studio.dam.tasks.AbstractDatabaseTask;
import fr.proline.studio.dam.tasks.DatabaseDataSetTask;
import fr.proline.studio.dam.tasks.SubTask;
import fr.proline.studio.gui.DefaultDialog;
import fr.proline.studio.gui.HourglassPanel;
import fr.proline.studio.gui.SplittedPanelContainer;
import fr.proline.studio.pattern.AbstractDataBox;
import fr.proline.studio.pattern.DataBoxPanelInterface;
import fr.proline.studio.pattern.DataBoxProteinSetsCmp;
import fr.proline.studio.rsmexplorer.gui.dialog.TreeSelectionDialog;
import fr.proline.studio.rsmexplorer.node.RSMTree;
import fr.proline.studio.utils.IconManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

/**
 * Panel to compare Protein Sets
 * @author JM235353
 */
public class RsetProteinGroupComparePanel extends HourglassPanel implements DataBoxPanelInterface {

    private JPanel m_internalPanel;
    private ProteinSetComparePanel m_proteinSetComparePanel;
    private LegendPanel m_legendPanel;

    /**
     * Creates new form RsetProteinGroupComparePanel
     */
    public RsetProteinGroupComparePanel() {


        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        m_internalPanel = new JPanel(new GridBagLayout());
        m_internalPanel.setBackground(Color.white);
        add(m_internalPanel, c);


        GridBagConstraints cInternal = new GridBagConstraints();
        cInternal.insets = new Insets(0, 0, 0, 0);
        cInternal.fill = GridBagConstraints.BOTH;
        cInternal.gridx = 0;
        cInternal.gridy = 0;
        cInternal.weightx = 1;
        cInternal.weighty = 1;
        m_proteinSetComparePanel = new ProteinSetComparePanel();
        m_internalPanel.add(m_proteinSetComparePanel, cInternal);

        cInternal.gridy++;
        cInternal.weighty = 0;

        m_legendPanel = new LegendPanel();
        m_internalPanel.add(m_legendPanel, cInternal);

    }

    @Override
    public void setDataBox(AbstractDataBox dataBox) {
        m_proteinSetComparePanel.setDataBox(dataBox);
    }
    
    @Override
    public ActionListener getRemoveAction(SplittedPanelContainer splittedPanel) {
        return m_proteinSetComparePanel.getDataBox().getRemoveAction(splittedPanel);
    }

    @Override
    public ActionListener getAddAction(SplittedPanelContainer splittedPanel) {
        return m_proteinSetComparePanel.getDataBox().getAddAction(splittedPanel);
    }

    public void setData(ArrayList<ProteinMatch> proteinMatchArray, HashMap<Long, ArrayList<Long>> rsmIdMap) {
        m_proteinSetComparePanel.setData(proteinMatchArray, rsmIdMap);
    }

    public ProteinSetComparePanel getComparePanel() {
        return m_proteinSetComparePanel;
    }

    private class LegendPanel extends JPanel {

        public LegendPanel() {

            setLayout(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(8, 8, 8, 8);
            c.anchor = GridBagConstraints.NORTHWEST;
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0;
            c.weighty = 1;

            JLabel sameSetLabel = new JLabel("Same Set");
            sameSetLabel.setIcon(IconManager.getIcon(IconManager.IconType.SAME_SET));
            add(sameSetLabel, c);

            c.gridx++;
            add(Box.createHorizontalStrut(6), c);


            c.gridx++;

            JLabel subSetLabel = new JLabel("Sub Set");
            subSetLabel.setIcon(IconManager.getIcon(IconManager.IconType.SUB_SET));
            add(subSetLabel, c);

            c.gridx++;
            c.weightx = 1;
            add(Box.createHorizontalGlue(), c);

            c.gridx++;
            c.weightx = 0;
            c.anchor = GridBagConstraints.NORTHEAST;
            final JButton rsmButton = new JButton("RSM", IconManager.getIcon(IconManager.IconType.ADD_REMOVE));
            add(rsmButton, c);


            rsmButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    ResultSet rset = m_proteinSetComparePanel.getFirstResultSet();
                    if (rset == null) {
                        // no data ready (should not happen)
                        return;
                    }
                    long projectId = m_proteinSetComparePanel.getDataBox().getProjectId();
                    RSMTree tree = RSMTree.getTree().copyResultSetRootSubTree(rset, projectId);

                    Window window = SwingUtilities.getWindowAncestor(m_legendPanel);

                    TreeSelectionDialog treeSelectionDialog = new TreeSelectionDialog(window, tree, "Select Result Summaries");
                    treeSelectionDialog.setLocationRelativeTo(m_proteinSetComparePanel);
                    treeSelectionDialog.setSelection(m_proteinSetComparePanel.getResultSummaryList());

                    treeSelectionDialog.setVisible(true);

                    if (treeSelectionDialog.getButtonClicked() == DefaultDialog.BUTTON_OK) {
                        final ArrayList<Dataset> selectedDatasetList = treeSelectionDialog.getSelectedDatasetList();

                        // load data

                        //JPM.TODO : load RSM 


                        AbstractDatabaseCallback callback = new AbstractDatabaseCallback() {

                            @Override
                            public boolean mustBeCalledInAWT() {
                                return true;
                            }

                            @Override
                            public void run(boolean success, long taskId, SubTask subTask, boolean finished) {
                                updataData(selectedDatasetList);
                            }
                        };


                        // ask asynchronous loading of data
                        
                        DatabaseDataSetTask task = new DatabaseDataSetTask(callback);
                        task.initLoadRsetAndRsm(selectedDatasetList);

                        if (task.needToFetch()) {
                            task.setPriority(AbstractDatabaseTask.Priority.HIGH_3); // must be done as fast as possible to avoid to let the use wait
                            AccessDatabaseThread.getAccessDatabaseThread().addTask(task);
                        } else {
                            // all ResultSummaries are already loaded, no need to execute the task
                            updataData(selectedDatasetList);
                        }


                    }

                }
            });

        }

        private void updataData(ArrayList<Dataset> selectedDatasetList) {

       
            DataBoxProteinSetsCmp dataBox = (DataBoxProteinSetsCmp) m_proteinSetComparePanel.getDataBox();
            ProteinMatch proteinMatch = (ProteinMatch) dataBox.getData(false, ProteinMatch.class);
            String proteinMatchName = proteinMatch.getAccession();

            ArrayList<ProteinMatch> proteinMatchArrayList = new ArrayList<>();
            ArrayList<Long> resultSetIdArrayList = new ArrayList<>();
            int size = selectedDatasetList.size();
            ArrayList<ResultSummary> selectedRsmList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ResultSummary rsm = selectedDatasetList.get(i).getTransientData().getResultSummary();
                selectedRsmList.add(rsm);
                long resultSetId = rsm.getResultSet().getId();
                resultSetIdArrayList.add(resultSetId);
                ProteinMatch pm = m_proteinSetComparePanel.getProteinMatch(resultSetId);
                proteinMatchArrayList.add(pm);
            }


            dataBox.loadData(proteinMatchArrayList, resultSetIdArrayList, proteinMatchName, selectedRsmList);

        }
    }
}
