package fr.proline.studio.rsmexplorer.actions.xic;

import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.dam.data.DataSetData;
import fr.proline.studio.pattern.WindowBox;
import fr.proline.studio.pattern.WindowBoxFactory;
import fr.proline.studio.rsmexplorer.DataBoxViewerTopComponent;
import fr.proline.studio.rsmexplorer.actions.identification.AbstractRSMAction;
import fr.proline.studio.rsmexplorer.tree.AbstractNode;
import fr.proline.studio.rsmexplorer.tree.AbstractTree;
import fr.proline.studio.rsmexplorer.tree.DataSetNode;
import org.openide.util.NbBundle;

/**
 * Action to see all peptides information per quant Channel
 * @author JM235353
 */
public class DisplayXICPeptideSetAction extends AbstractRSMAction {

    public DisplayXICPeptideSetAction(AbstractTree tree) {
        super(NbBundle.getMessage(DisplayXICPeptideSetAction.class, "CTL_DisplayXicPeptideSetAction"), tree);
    }

    @Override
    public void actionPerformed(AbstractNode[] selectedNodes, int x, int y) {

        int nbNodes = selectedNodes.length;
        for (int i = 0; i < nbNodes; i++) {
            DataSetNode dataSetNode = (DataSetNode) selectedNodes[i];

            actionImpl(dataSetNode);
        }

    }

    private void actionImpl(DataSetNode dataSetNode) {

        final DDataset dataset = ((DataSetData) dataSetNode.getData()).getDataset();

        WindowBox wbox = WindowBoxFactory.getXicQuantPeptideSetWindowBox(dataset.getName(), dataset.getName() + " Peptides", dataSetNode.isQuantXIC());
        wbox.setEntryData(dataset.getProject().getId(), dataset);

        // open a window to display the window box
        DataBoxViewerTopComponent win = new DataBoxViewerTopComponent(wbox);
        win.open();
        win.requestActive();
    }


    @Override
    public void updateEnabled(AbstractNode[] selectedNodes) {

        // only one node selected
        if (selectedNodes.length != 1) {
            setEnabled(false);
            return;
        }

        AbstractNode node = (AbstractNode) selectedNodes[0];

        // the node must not be in changing state
        if (node.isChanging()) {
            setEnabled(false);
            return;
        }

        // must be a dataset 
        if (node.getType() != AbstractNode.NodeTypes.DATA_SET) {
            setEnabled(false);
            return;
        }

        DataSetNode datasetNode = (DataSetNode) node;

        // must be a quantitation XIC
        if (! datasetNode.isQuantXIC() && !datasetNode.isQuantSC()) {
         //if (! datasetNode.isQuantXIC()) {
            setEnabled(false);
            return;
        }

        setEnabled(true);
    }
}