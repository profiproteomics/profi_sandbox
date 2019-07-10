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
package fr.proline.studio.dam.tasks.data.ptm;

import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DPeptideMatch;

import java.util.List;

public class PTMSitePeptideInstance {

  private PTMPeptideInstance m_ptmPeptideInstance;
  private PTMSite m_site;
  private DPeptideMatch m_bestPeptideMatch;
  private List<DPeptideInstance> m_leafPepInstances;

  public PTMSitePeptideInstance(PTMSite site, PTMPeptideInstance ptmPeptide, List<DPeptideInstance> leafPeptideInstances, DPeptideMatch bestPeptideMatch) {
    m_ptmPeptideInstance = ptmPeptide;
    m_bestPeptideMatch = bestPeptideMatch;
    m_leafPepInstances = leafPeptideInstances;
    m_site = site;
  }

  public PTMPeptideInstance getPTMPeptideInstance() {
    return m_ptmPeptideInstance;
  }

  public PTMSite getSite() {
    return m_site;
  }

  public DPeptideMatch getBestPeptideMatch() {
    return m_bestPeptideMatch;
  }

  public List<DPeptideInstance> getLeafPepInstances() {
    return m_leafPepInstances;
  }
}
