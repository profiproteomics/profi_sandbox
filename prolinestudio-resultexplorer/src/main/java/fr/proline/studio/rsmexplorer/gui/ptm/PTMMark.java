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
package fr.proline.studio.rsmexplorer.gui.ptm;

import fr.proline.core.orm.msi.dto.DInfoPTM;
import fr.proline.core.orm.msi.dto.DPeptidePTM;

/**
 *
 * @author CB205360
 */
public class PTMMark {
    
    private DPeptidePTM m_peptidePtm;
    private int m_proteinLocation;
    private int m_proteinDisplayLocation;
    private boolean m_ptmIsNorCTerm;

    public PTMMark(DPeptidePTM peptidePtm, int proteinLocation) {
        this(peptidePtm, proteinLocation, proteinLocation, false);
    }
    
    public PTMMark(DPeptidePTM peptidePtm, int proteinLocation, int proteinDisplayLocation, boolean isNCTermPTM) {
        this.m_peptidePtm = peptidePtm;
        this.m_proteinLocation = proteinLocation;
        m_proteinDisplayLocation = proteinDisplayLocation;
        m_ptmIsNorCTerm = isNCTermPTM;
    }

    public DPeptidePTM getPeptidePtm() {
        return m_peptidePtm;
    }
    
    public int getProteinLocation() {
        return m_proteinLocation;
    }
        
    public int getProteinLocationToDisplay() {
        return m_proteinDisplayLocation;
    }
    
    public boolean isPTMNorCterm(){
        return m_ptmIsNorCTerm;
    }

    public String getPtmShortName() {
        DInfoPTM ptm = DInfoPTM.getInfoPTMMap().get(m_peptidePtm.getIdPtmSpecificity());
        return ptm.getPtmShortName();
    }

    /**
     * 
     * @return the first letter of the modification name
     */
    public char getPtmSymbol() {
        DInfoPTM ptm = DInfoPTM.getInfoPTMMap().get(m_peptidePtm.getIdPtmSpecificity());
        return ptm.getPtmShortName().toUpperCase().charAt(0);
    }
    /**
     * The PtmSpecificity contains the informations of (location, amino acide, ptm_id and its name)
     * @return le identifient de PtmSpecificity
     */
    public Long getPtmSpecificityId() {
        return m_peptidePtm.getIdPtmSpecificity();
    }
          
}
