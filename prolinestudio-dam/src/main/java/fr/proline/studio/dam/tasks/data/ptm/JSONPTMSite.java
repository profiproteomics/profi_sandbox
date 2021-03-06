/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.dam.tasks.data.ptm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author CB205360
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONPTMSite extends AbstractJSONPTMSite {

    //List of all PeptidesInstances Ids (in leaf RSM)
    public Long[] peptideInstanceIds;
    
    //List of PepidesInstances which don't match this PTM but with same mass
    public Long[] isomericPeptideInstanceIds;
}
