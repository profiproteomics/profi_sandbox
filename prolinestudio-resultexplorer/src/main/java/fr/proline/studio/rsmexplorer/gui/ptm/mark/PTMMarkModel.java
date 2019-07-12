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

import fr.proline.studio.dam.tasks.data.ptm.PTMSite;
import fr.proline.studio.rsmexplorer.gui.ptm.PTMMark;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * @cea 
 * http://www.profiproteomics.fr
 * create date: 27 nov. 2018
 */
/**
 *
 * @author Karine XUE
 */
public class PTMMarkModel {

    List<PTMMarkView> m_ptmMarkList;

    public PTMMarkModel() {
        m_ptmMarkList = new ArrayList<>();
    }

    public List<PTMMarkView> getPTMMarkList() {
        return m_ptmMarkList;
    }

    /**
     * treat data
     *
     * @param ptmSiteAA2Mark
     */
    public void setPTM(Collection<PTMMark> ptmMarks) {
        m_ptmMarkList = new ArrayList<>();
        for (PTMMark pa : ptmMarks) {
            PTMMarkView p = new PTMMarkView(pa);
            m_ptmMarkList.add(p);
        }

    }

    public void addPTM(PTMMark ptm) {
        PTMMarkView p = new PTMMarkView(ptm);
        m_ptmMarkList.add(p);
    }


}
