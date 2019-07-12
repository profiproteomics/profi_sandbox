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
import fr.proline.studio.rsmexplorer.gui.ptm.ViewPtmAbstract;
import fr.proline.studio.rsmexplorer.gui.ptm.ViewSetting;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Karine XUE
 */
public class PTMMarkSetView extends ViewPtmAbstract {

    private List<PTMMarkView> m_ptmMarkList = new ArrayList<>();

    @Override
    public void paint(Graphics2D g, ViewContext viewContext) {
        for (PTMMarkView pm : m_ptmMarkList) {
            pm.setBeginPoint(m_x, m_y);
            pm.paint(g, viewContext);
        }
    }

    void setPtmMarkList(List<PTMMarkView> ptmMarkList) {
        if(ptmMarkList == null)
            ptmMarkList = new ArrayList<>();
        this.m_ptmMarkList = ptmMarkList;
    }

    @Override
    public void setBeginPoint(int x, int y) {
        this.m_x = x;
        this.m_y = y;
    }

    protected String getToolTipText(int x, int y, int ajustedLocation) {
        if (y >= this.m_y && y <= (this.m_y + ViewSetting.HEIGHT_AA * 3) && m_ptmMarkList != null) {
            int index = (x - this.m_x) / ViewSetting.WIDTH_AA + ajustedLocation;
            for (PTMMarkView pm : m_ptmMarkList) {
                if (pm.getLocationProtein()== index) {
                    
                    return pm.getPTMShortName()+"(Protein Loc. " + pm.getDisplayedLocationProtein()+")";
                } 
            }
        }
        return null;
    }
}
