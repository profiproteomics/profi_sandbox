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
package fr.proline.studio.pattern;

import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.studio.dam.tasks.data.ptm.PTMDataset;
import fr.proline.studio.dam.tasks.data.ptm.PTMPeptideInstance;
import fr.proline.studio.dam.tasks.data.ptm.PTMSite;
import fr.proline.studio.rsmexplorer.gui.PTMPeptidesTablePanel;
import fr.proline.studio.rsmexplorer.gui.ptm.PTMPeptidesGraphicView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is very similaire as DataBoxPTMSitePeptides
 *
 * @author Karine XUE
 */
public class DataBoxPTMPeptidesGraphic extends DataBoxPTMPeptides {

    /**
     * Create a DataBoxPTMPeptidesGraphic : graphical view of PTMPeptideInstances. 
    */
    public DataBoxPTMPeptidesGraphic() {
        super(false);

        // Name of this databox
        m_typeName = "PTM Site's Peptides";
        m_description = "Peptides of a PTM Protein Sites graphical display.";
    }

    private Long m_previousTaskId = null;
            
    @Override
    public void createPanel() {
        PTMPeptidesGraphicView p = new PTMPeptidesGraphicView();
        p.setName(m_typeName);
        p.setDataBox(this);
        setDataBoxPanelInterface(p);
    }

    
    @Override
    public void dataChanged() {
        
        PTMPeptidesGraphicView graphicView = (PTMPeptidesGraphicView) getDataBoxPanelInterface();
        
        //Get information from prevous box:     
        // -- PTM Dataset & RSM the ptm peptides belong to
        // -- List of PTM Peptides to display
        PTMDataset newPtmDataset = (PTMDataset) m_previousDataBox.getData(false, PTMDataset.class);
        List<PTMPeptideInstance> newPtmPepInstancesPtmSite = (List<PTMPeptideInstance> ) m_previousDataBox.getData(false, PTMPeptideInstance.class, true);
        
        boolean valueUnchanged  = Objects.equals(newPtmDataset, m_ptmDataset) && Objects.equals(newPtmPepInstancesPtmSite,m_ptmPepInstancesPtmSite);
        
        if(valueUnchanged){       
            //selection may have changed 
            PTMPeptideInstance selectedPep = (PTMPeptideInstance)  m_previousDataBox.getData(false, PTMPeptideInstance.class);
            graphicView.setSelectedPTMPeptide(selectedPep);            
            return;
        }

        m_ptmDataset = newPtmDataset;
        m_ptmPepInstancesPtmSite = newPtmPepInstancesPtmSite;        

        // -- Selected PTM Peptide
        //DPeptideInstance selectedInsts = (DPeptideInstance) m_previousDataBox.getData(false, DPeptideInstance.class);
        //m_logger.debug("selected peptide Match, ptm {}", m_selecedDPeptideMatch.getPeptide().getTransientData().getPeptideReadablePtmString().getReadablePtmString());
        
        if (m_ptmPepInstancesPtmSite == null || m_ptmPepInstancesPtmSite.isEmpty()) {
            graphicView.setData(null);
            return;
        }

        //Get QuantInfo        
        final List<PTMSite> notLoadedPtmSite = new ArrayList<>();
        for(PTMPeptideInstance ptmPepInst : m_ptmPepInstancesPtmSite){
            ptmPepInst.getSites().stream().forEach(ptmSite -> {
                if(!ptmSite.isLoaded())
                   notLoadedPtmSite.add(ptmSite); 
            });               
        }
        
        if (notLoadedPtmSite.isEmpty()) {
            resetPrevPTMTaskId();
            graphicView.setData(m_ptmPepInstancesPtmSite); 
            propagateDataChanged(PTMPeptideInstance.class);                
            
        } else
            super.loadPtmSite(notLoadedPtmSite);
 
    }
    
    @Override
    public Object getData(boolean getArray, Class parameterType) {
        if (parameterType != null) {
            if (parameterType.equals(PTMPeptideInstance.class)) {
                PTMPeptideInstance selectedParentPepInstance = ((PTMPeptidesGraphicView) getDataBoxPanelInterface()).getSelectedPTMPeptideInstance();
                if (selectedParentPepInstance != null) {
                    return selectedParentPepInstance;
                }
                return null;
            }
            if (parameterType.equals(DPeptideMatch.class)) {
                PTMPeptideInstance selectedParentPepInstance = ((PTMPeptidesGraphicView) getDataBoxPanelInterface()).getSelectedPTMPeptideInstance();
                if (selectedParentPepInstance != null) {
                    return selectedParentPepInstance.getBestPepMatch();
                }
            }
        }

        return super.getData(getArray, parameterType);
    }
    
}
