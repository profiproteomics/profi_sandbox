package fr.proline.studio.rsmexplorer.gui.calc.graphics;


import fr.proline.studio.rsmexplorer.gui.calc.GraphPanel;


/**
 *
 * @author JM235353
 */
public class BoxPlotGraphic extends AbstractMatrixPlotGraphic {

    public BoxPlotGraphic(GraphPanel panel) {
        super(panel, "boxPlot");
    }
   
    @Override
    public String getName() {
        return "Box Plot";
    }

    @Override
    public AbstractGraphic cloneGraphic(GraphPanel p) {
        return new BoxPlotGraphic(p);
    }


}