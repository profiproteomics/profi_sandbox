package fr.proline.studio.rsmexplorer.gui.calc.functions;

import fr.proline.studio.rsmexplorer.gui.calc.GraphPanel;
import fr.proline.studio.types.PValue;


/**
 * PValue Function for the data analyzer
 * @author JM235353
 */
public class PValueFunction extends AbstractOnExperienceDesignFunction {

    public PValueFunction(GraphPanel panel) {
        super(panel, FUNCTION_TYPE.PValueFunction, "pvalue", "pvalue", "pvalue", null, new PValue());
    }

    @Override
    public int getMinGroups() {
        return 2;
    }
    @Override
    public int getMaxGroups() {
        return 8;
    }

    @Override
    public AbstractFunction cloneFunction(GraphPanel p) {
        AbstractFunction clone = new PValueFunction(p);
        clone.cloneInfo(this);
        return clone;
    }





}
