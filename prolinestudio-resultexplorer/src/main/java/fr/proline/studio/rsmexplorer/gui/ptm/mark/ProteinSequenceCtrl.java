/* 
 * Copyright (C) 2019 VD225637
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the CeCILL FREE SOFTWARE LICENSE AGREEMENT
 * ; either version 2.1 
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * CeCILL License V2.1 for more details.
 *
 * You should have received a copy of the CeCILL License 
 * along with this program; If not, see <http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.html>.
 */
package fr.proline.studio.rsmexplorer.gui.ptm.mark;

import fr.proline.studio.rsmexplorer.gui.ptm.ViewContext;

import java.awt.Graphics2D;

/**
 *
 * @author Karine XUE
 */
public class ProteinSequenceCtrl {
    
    
    ProteinSequenceView m_view;

    public ProteinSequenceCtrl() {
        m_view = new ProteinSequenceView();
    }
    
    public void setData(String s){
        m_view.setSequence(s);
    }
    
    /**
     * set graphic begin location
     * @param x
     * @param y 
     */
    public void setBeginPoint(int x, int y) {
       this.m_view.setBeginPoint(x, y);
    }

    public void paint(Graphics2D g2, ViewContext viewContext) {
        this.m_view.paint(g2, viewContext);
    }

    public void setPTMSequencePosition(int i) {
        this.m_view.setPTMSequencePosition(i);
    }
    
    @Override
    public String toString(){
        return m_view.toString();
    }
}
