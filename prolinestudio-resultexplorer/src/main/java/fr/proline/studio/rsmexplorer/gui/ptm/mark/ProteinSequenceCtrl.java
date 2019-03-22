/*
 * @cea 
 * http://www.profiproteomics.fr
 * create date: 28 nov. 2018
 */
package fr.proline.studio.rsmexplorer.gui.ptm.mark;

import fr.proline.studio.rsmexplorer.gui.ptm.ViewContext;

import java.awt.Graphics2D;

/**
 *
 * @author Karine XUE
 */
public class ProteinSequenceCtrl {
    
    
    ProteinSequenceView _view;

    public ProteinSequenceCtrl() {
        _view = new ProteinSequenceView();
    }
    
    public void setData(String s){
        _view.setSequence(s);
    }
    /**
     * set graphic begin location
     * @param x
     * @param y 
     */
    public void setBeginPoint(int x, int y) {
       this._view.setBeginPoint(x, y);
    }

    public void paint(Graphics2D g2, ViewContext viewContext) {
        this._view.paint(g2, viewContext);
    }

    public void setPTMSequencePosition(int i) {
        this._view.setPTMSequencePosition(i);
    }
    
}