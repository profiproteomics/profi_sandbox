package fr.proline.studio.rsmexplorer.gui.calc.functions;

import fr.proline.studio.rsmexplorer.gui.calc.GraphPanel;



/**
 * Beta Binomial Function for the data analyzer
 * @author JM235353
 */
public class BBinomialFunction extends AbstractOnExperienceDesignFunction {

    public BBinomialFunction(GraphPanel panel) {
        super(panel, "bbinomial", "bbinomial", "bbinomial");
    }

    @Override
    public int getMinGroups() {
        return 2;
    }

    @Override
    public int getMaxGroups() {
        return 3;
    }
    
    @Override
    public AbstractFunction cloneFunction(GraphPanel p) {
        return new BBinomialFunction(p);
    }





}
