package fr.proline.studio.pattern;

import fr.proline.studio.rsmexplorer.gui.ImagePanel;
import java.awt.Image;

/**
 *
 * @author JM235353
 */
public class DataBoxImage extends AbstractDataBox {

    public DataBoxImage() {
        super(DataboxType.DataBoxImage, DataboxStyle.STYLE_UNKNOWN);

        // Name of this databox
        m_typeName = "Image Display";
    }
    
    @Override
    public void createPanel() {
        ImagePanel p = new ImagePanel();
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }

    @Override
    public void dataChanged() {
        
    }
    
        @Override
    public void setEntryData(Object data) {

        ((ImagePanel)getDataBoxPanelInterface()).setImage((Image) data);

        dataChanged();

    }

    
}
