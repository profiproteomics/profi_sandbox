package fr.proline.studio.python.interpreter;

import fr.proline.studio.python.data.ColBooleanData;
import fr.proline.studio.python.data.ColDoubleData;
import fr.proline.studio.python.data.PythonImage;
import fr.proline.studio.python.data.Table;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyObject;

/**
 * 
 * Encapsulation of a python result and the name of this result
 * 
 * @author JM235353
 */
public class ResultVariable {

    private final String m_name;
    private final PyObject m_value;
 
    private static int m_incIndix = 0;
    
    public ResultVariable(PyObject v) {
        int i = m_incIndix++;
        m_name = "_ivar_" + i;
        m_value = v;
    }
    
    public ResultVariable(String name, PyObject v) {
        m_name = name;
        m_value = v;
    }


    @Override
    public String toString() {
        if (m_value instanceof ColDoubleData) {
            return m_name;
        } else if (m_value instanceof ColBooleanData) {
            return m_name;
        } else if (m_value instanceof Table) {
          return m_name;  
        } else if (m_value instanceof PyFloat) {
            return m_name + "=" + ((PyFloat) m_value).getValue();
        } else if (m_value instanceof PyInteger) {
            return m_name + "=" + ((PyInteger) m_value).getValue();
        } else if (m_value instanceof PythonImage) {
            return m_name;
        }
        return null; // should not happen
    }

    public PyObject getValue() {
        return m_value;
    }
    
    public String getName() {
        return m_name;
    }
    

    
    
    
}
