package fr.proline.studio.table;

import fr.proline.studio.extendedtablemodel.ExtraDataType;
import fr.proline.studio.graphics.PlotDataSpec;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

/**
 * Table model with extra decoration info (tooltip and renderer row/col). It
 * contains extra data added to the model
 *
 * @author JM235353
 */
public abstract class DecoratedTableModel extends AbstractTableModel implements DecoratedTableModelInterface {

    protected HashMap<Class, Object> m_extraValues = null;

    public long row2UniqueId(int rowIndex) {
        return rowIndex;
    }

    public int uniqueId2Row(long id) {
        return (int) id;
    }

    public void addSingleValue(Object v) {
        if (m_extraValues == null) {
            m_extraValues = new HashMap<>();
        }
        m_extraValues.put(v.getClass(), v);
    }

    public Object getSingleValue(Class c) {
        if (m_extraValues == null) {
            return null;
        }
        return m_extraValues.get(c);
    }

    public void registerSingleValuesAsExtraTypes(ArrayList<ExtraDataType> extraDataTypeList) {
        if (m_extraValues == null) {
            return;
        }
        for (Class c : m_extraValues.keySet()) {
            extraDataTypeList.add(new ExtraDataType(c, false));
        }
    }

   /**
    * DecoratedTableModel often implement GlobalTableModelInterface, who extends ExtendedTableModelInterface
    * @param i
    * @return 
    */
    public PlotDataSpec getDataSpecAt(int i) {
        return null;
    }

}
