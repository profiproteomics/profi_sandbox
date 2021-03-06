package fr.proline.studio.pattern;

import fr.proline.studio.extendedtablemodel.ExtraDataType;
import fr.proline.studio.extendedtablemodel.GlobalTabelModelProviderInterface;
import fr.proline.studio.graphics.CrossSelectionInterface;
import fr.proline.studio.rsmexplorer.gui.GenericPanel;
import fr.proline.studio.extendedtablemodel.GlobalTableModelInterface;
import java.util.ArrayList;
import fr.proline.studio.extendedtablemodel.ExtendedTableModelInterface;

/**
 *
 * @author JM235353
 */
public class DataboxGeneric extends AbstractDataBox {

    private GlobalTableModelInterface m_entryModel = null;
    
    private final boolean m_removeStripAndSort;
    
    public DataboxGeneric(String dataName, String typeName, boolean removeStripAndSort) {
        super(DataboxType.DataboxCompareResult, DataboxStyle.STYLE_UNKNOWN);
        
        // Name of this databox
        m_dataName = dataName;
        m_typeName = typeName;
        m_description = typeName;
        m_removeStripAndSort = removeStripAndSort;
        
        // Register Possible in parameters
        // One ResultSummary
        GroupParameter inParameter = new GroupParameter();
        inParameter.addParameter(ExtendedTableModelInterface.class, false);
        registerInParameter(inParameter);
        
        
        // Register possible out parameters
        GroupParameter outParameter = new GroupParameter();
        outParameter.addParameter(ExtendedTableModelInterface.class, false);
        registerOutParameter(outParameter);

        
        
    }
    
    @Override
    public void createPanel() {
        GenericPanel p = new GenericPanel(m_removeStripAndSort);
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }
    
    @Override
    public void setEntryData(Object data) {
        if (data instanceof GlobalTableModelInterface) {
            m_entryModel = (GlobalTableModelInterface) data;
            
            
             ArrayList<ExtraDataType> extraDataTypeList = m_entryModel.getExtraDataTypes();
             if (extraDataTypeList != null) {
                 for(ExtraDataType extraDataType : extraDataTypeList) {
                     Class c = extraDataType.getTypeClass();

                     GroupParameter outParameter = new GroupParameter();
                     outParameter.addParameter(c, false);
                     registerOutParameter(outParameter);
                 }
             }
            
        }
         dataChanged();
    }

    @Override
    public void dataChanged() {
        GlobalTableModelInterface dataInterface = m_entryModel;
        if (dataInterface == null) {
            dataInterface = (GlobalTableModelInterface) m_previousDataBox.getData(false, GlobalTableModelInterface.class);
        }

        ((GenericPanel) getDataBoxPanelInterface()).setData(dataInterface);

    }
    
        @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType != null) {
            if (parameterType.equals(ExtendedTableModelInterface.class)) {
                return ((GlobalTabelModelProviderInterface) getDataBoxPanelInterface()).getGlobalTableModelInterface();
            }
            if (parameterType.equals(CrossSelectionInterface.class)) {
                return ((GlobalTabelModelProviderInterface)getDataBoxPanelInterface()).getCrossSelectionInterface();
            }
            ArrayList<ExtraDataType> extraDataTypeList = m_entryModel.getExtraDataTypes();
             if (extraDataTypeList != null) {
                 for (ExtraDataType extraDataType : extraDataTypeList) {
                     if (extraDataType.getTypeClass().equals(parameterType)) {
                         return ((GenericPanel) getDataBoxPanelInterface()).getValue(parameterType, extraDataType.isList());
                     }
                 }
             }
        }
        return super.getData(getArray, parameterType);
    }
    
}
