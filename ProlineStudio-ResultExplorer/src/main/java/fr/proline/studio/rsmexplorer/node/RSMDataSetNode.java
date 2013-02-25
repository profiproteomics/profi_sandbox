package fr.proline.studio.rsmexplorer.node;

import fr.proline.core.orm.msi.ResultSet;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.uds.Aggregation;
import fr.proline.core.orm.uds.Dataset;
import fr.proline.core.orm.uds.Dataset.DatasetType;
import fr.proline.studio.dam.data.AbstractData;
import fr.proline.studio.dam.data.DataSetData;
import fr.proline.studio.utils.IconManager;
import javax.swing.ImageIcon;

/**
 * Node for Dataset
 * @author JM235353
 */
public class RSMDataSetNode extends RSMNode {

   
    
    
    public RSMDataSetNode(AbstractData data) {
        super(NodeTypes.DATA_SET, data);
    }

    @Override
    public ImageIcon getIcon() {
        
        Dataset.DatasetType type = ((DataSetData) getData()).getDatasetType();
        switch(type) {
            case IDENTIFICATION:
                Dataset dataset = ((DataSetData) getData()).getDataset();
                if (dataset != null) {
                    if (dataset.getResultSummaryId() == null) {
                        return getIcon(IconManager.IconType.RSET);
                    } else {
                        return getIcon(IconManager.IconType.RSM);
                    }
                } else {
                    return getIcon(IconManager.IconType.RSET);
                }
                
            case AGGREGATE:
                //Aggregation.ChildNature aggregateType = ((DataSetData) getData()).getAggregateType();
                //JPM.TODO : according to aggregateType type :icon must be different
                return getIcon(IconManager.IconType.VIAL);
            default:
                // sould not happen
                
        }
        
        return null;

    }
    

    
    public Dataset getDataset() {
        return ((DataSetData) getData()).getDataset();
    }
    
    public boolean hasResultSummary() {
        Dataset dataSet = ((DataSetData) getData()).getDataset();
        return (dataSet.getResultSummaryId() != null);
    }
    

    public Integer getResultSummaryId() {
        return ((DataSetData) getData()).getDataset().getResultSummaryId();
    }
    
    public ResultSummary getResultSummary() {
        // getResultSummary() can return null if the resultSummary has not been loaded previously
        Dataset dataSet = ((DataSetData) getData()).getDataset();
        return dataSet.getTransientData().getResultSummary();
    }
    
    
    public boolean hasResultSet() {
        Dataset dataSet = ((DataSetData) getData()).getDataset();
        return (dataSet.getResultSetId() != null);
    }
    
    public Integer getResultSetId() {
        return ((DataSetData) getData()).getDataset().getResultSetId();
    }
    
    public ResultSet getResultSet() {
        // getResultSet() can return null if the resultSet has not been loaded previously
        Dataset dataSet = ((DataSetData) getData()).getDataset();
        return dataSet.getTransientData().getResultSet();
    }
    
    @Override
    public boolean canBeDeleted() {
        
        // for the moment, we can delete only empty DataSet with no leaf
        if (hasResultSet()) {
            return false;
        }
        if (hasResultSummary()) {
            return false;
        }
        
        return isLeaf();
        
    }
    
    
}
