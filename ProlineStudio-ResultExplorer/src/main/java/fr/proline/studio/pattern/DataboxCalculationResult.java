package fr.proline.studio.pattern;

import fr.proline.studio.comparedata.CompareDataInterface;
import fr.proline.studio.comparedata.GlobalTabelModelProviderInterface;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.rsmexplorer.gui.GenericPanel;
import fr.proline.studio.table.GlobalTableModelInterface;

/**
 *
 * @author JM235353
 */
public class DataboxCalculationResult extends AbstractDataBox {

    private GlobalTableModelInterface m_entryModel = null;
    
    public DataboxCalculationResult(String dataName, String typeName) {
        super(DataboxType.DataboxCompareResult);
        
        // Name of this databox
        m_dataName = dataName;
        m_typeName = typeName;
        m_description = typeName;
        
        // Register Possible in parameters
        // One ResultSummary
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(CompareDataInterface.class, false);
        registerInParameter(inParameter);
        
        
        // Register possible out parameters
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(CompareDataInterface.class, true);
        registerOutParameter(outParameter);

        outParameter = new GroupParameter();
        outParameter.addParameter(CrossSelectionInterface.class, true);
        registerOutParameter(outParameter);
        

        
        
    }
    
    @Override
    public void createPanel() {
        GenericPanel p = new GenericPanel();
        p.setName(m_typeName);
        p.setDataBox(this);
        m_panel = p;
    }
    
    @Override
    public void setEntryData(Object data) {
        if (data instanceof GlobalTableModelInterface) {
            m_entryModel = (GlobalTableModelInterface) data;
        }
         dataChanged();
    }

    @Override
    public void dataChanged() {
        GlobalTableModelInterface dataInterface = m_entryModel;
        if (dataInterface == null) {
            dataInterface = (GlobalTableModelInterface) m_previousDataBox.getData(false, GlobalTableModelInterface.class);
        }

        ((GenericPanel) m_panel).setData(dataInterface);

    }
    
        @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType != null) {
            if (parameterType.equals(CompareDataInterface.class)) {
                return ((GlobalTabelModelProviderInterface) m_panel).getGlobalTableModelInterface();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                return ((GlobalTabelModelProviderInterface)m_panel).getCrossSelectionInterface();
            }
        }
        return super.getData(getArray, parameterType);
    }
    
}
