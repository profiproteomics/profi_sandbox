/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.parameter;

import java.awt.Component;
import javax.swing.*;


/**
 *
 * @author JM235353
 */
public class ObjectParameter<E> extends AbstractParameter {

    private E[] objects;
    private int defaultIndex;
    private AbstractParameterToString<E> paramToString = null;

    public ObjectParameter(String key, String name, E[] objects, int defaultIndex, AbstractParameterToString<E> paramToString) {
        super(key, name, Integer.class, JComboBox.class);
        this.objects = objects;
        if ((defaultIndex<0) || (defaultIndex>=objects.length)) {
            defaultIndex = 0;
        }
        this.defaultIndex = defaultIndex;
        this.paramToString = paramToString;
    }
    
    @Override
    public JComponent getComponent(Object value) {
        
        if (graphicalType.equals(JComboBox.class)) {
            JComboBox combobox = new JComboBox(objects);
            combobox.setRenderer(new ParameterComboboxRenderer());
            if (value != null) {
                combobox.setSelectedItem(value);
            } else {
                combobox.setSelectedIndex(defaultIndex);
            }
            parameterComponent = combobox;
            return combobox;
        }

        return null; // should not happen
    }

    @Override
    public void initDefault() {
        ((JComboBox) parameterComponent).setSelectedIndex(defaultIndex);
    }

    @Override
    public ParameterError checkParameter() {
        return null;
    }

    @Override
    public String getStringValue() {
        
        E obj = getObjectValue();
        
        if (obj == null) {
            return "";
        }
        
        if (paramToString != null) {
            return paramToString.toString(obj);
        }
        
        return obj.toString();
    }

    @Override
    public E getObjectValue() {
        if (graphicalType.equals(JComboBox.class)) {
            return (E) ((JComboBox) parameterComponent).getSelectedItem();
        }
        return null; // should not happen
    }
    
    
    private class ParameterComboboxRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String display = null;
            if (paramToString != null) {
                display = paramToString.toString((E) value);
            } else {
                display = value.toString();
            }
            l.setText(display);

            return l;
        }
    }
    
}
