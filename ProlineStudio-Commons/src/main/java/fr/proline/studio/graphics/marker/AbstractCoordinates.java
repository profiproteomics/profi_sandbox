package fr.proline.studio.graphics.marker;

import fr.proline.studio.graphics.BasePlotPanel;

/**
 *
 * @author JM235353
 */
public abstract class AbstractCoordinates {
    
    public abstract int getPixelX(BasePlotPanel plotPanel);
    public abstract int getPixelY(BasePlotPanel plotPanel);
    public abstract void setPixelPosition(BasePlotPanel plotPanel, int pixelX, int pixelY);
}
