package fr.proline.studio.dam.tasks;

import fr.proline.core.orm.msi.Peptide;
import fr.proline.core.orm.msi.PeptideInstance;
import fr.proline.core.orm.msi.PeptideReadablePtmString;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.msi.SequenceMatch;
import fr.proline.core.orm.msi.dto.DMsQuery;
import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.core.orm.msi.dto.DSpectrum;
import fr.proline.core.orm.msi.dto.DInfoPTM;
import fr.proline.core.orm.msi.dto.DPeptidePTM;
import fr.proline.core.orm.msi.dto.DProteinPTMSite;
import fr.proline.core.orm.util.DataStoreConnectorFactory;
import fr.proline.studio.dam.taskinfo.TaskError;
import fr.proline.studio.dam.taskinfo.TaskInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;


/**
 *
 * @author JM235353
 */
public class DatabasePTMProteinSiteTask extends AbstractDatabaseTask {

    private long m_projectId = -1;
    private ResultSummary m_rsm = null;
    
    private ArrayList<DProteinPTMSite> m_proteinPTMSiteArray = null;
    
    final int SLICE_SIZE = 1000;
    
    public DatabasePTMProteinSiteTask(AbstractDatabaseCallback callback, long projectId, ResultSummary rsm, ArrayList<DProteinPTMSite> proteinPTMSiteArray) {
        super(callback, new TaskInfo("Load All PTM Sites for Proteins Sets for " + rsm.getId(), false, TASK_LIST_INFO, TaskInfo.INFO_IMPORTANCE_MEDIUM));

        m_projectId = projectId;
        m_rsm = rsm;
        
        m_proteinPTMSiteArray = proteinPTMSiteArray;

    }

    @Override
    public boolean needToFetch() {
        return true;
    }

    @Override
    public boolean fetchData() {
        if (needToFetch()) {
            // first data are fetched
            return fetchAllProteinSets();
        }
        return true; // should not happen
    }

    private boolean fetchAllProteinSets() {
        EntityManager entityManagerMSI = DataStoreConnectorFactory.getInstance().getMsiDbConnector(m_projectId).getEntityManagerFactory().createEntityManager();
        try {

            entityManagerMSI.getTransaction().begin();

            Long rsmId = m_rsm.getId();
            
            // Load Typical Protein Matches
            TypedQuery<DProteinMatch> typicalProteinQuery = entityManagerMSI.createQuery("SELECT new fr.proline.core.orm.msi.dto.DProteinMatch(pm.id, pm.accession, pm.score, pm.peptideCount, pm.resultSet.id, pm.description, pepset) FROM PeptideSetProteinMatchMap pset_to_pm JOIN pset_to_pm.proteinMatch as pm JOIN pset_to_pm.peptideSet as pepset JOIN pepset.proteinSet as ps WHERE ps.resultSummary.id=:rsmId AND ps.isValidated=true AND ps.representativeProteinMatchId=pm.id  ORDER BY pepset.score DESC", DProteinMatch.class);
            typicalProteinQuery.setParameter("rsmId", rsmId);

            List<DProteinMatch> typicalProteinMatchesArray = typicalProteinQuery.getResultList();
            
            
            HashMap<Long, DProteinMatch> proteinMatchMap = new HashMap<>(typicalProteinMatchesArray.size());
            HashMap<Long, ArrayList<DPeptideInstance>> proteinMatchIdToPeptideInstancehMap = new HashMap<>(typicalProteinMatchesArray.size());
            
            ArrayList<Long> typicalProteinMatchesIdsArray = new ArrayList<>(typicalProteinMatchesArray.size());
            for (DProteinMatch proteinMatch : typicalProteinMatchesArray) {
                //PeptideSet peptideSet = proteinMatch.getPeptideSet(rsmId);
                Long id = proteinMatch.getId();
                typicalProteinMatchesIdsArray.add(id);
                proteinMatchMap.put(id, proteinMatch);
            }
            
            HashMap<Long, Peptide> allPeptidesMap = new HashMap();

            ArrayList<DPeptideMatch> peptideMatchArray = new ArrayList<>();
            
            SubTaskManager subTaskManager = new SubTaskManager(1);
            SubTask subTask = subTaskManager.sliceATaskAndGetFirst(0, typicalProteinMatchesIdsArray.size(), SLICE_SIZE);
            while (subTask != null) {
                            // SELECT sm.bestPeptideMatchId, pi, pm.id, pm.rank, pm.charge, pm.deltaMoz, pm.experimentalMoz, pm.missedCleavage, pm.score, pm.resultSet.id, p, sm, ms.id, ms.initialId, pm.cdPrettyRank, pm.sdPrettyRank, sp.firstTime, sp.precursorIntensity, sp.title
                // FROM fr.proline.core.orm.msi.SequenceMatch sm, fr.proline.core.orm.msi.PeptideInstancePeptideMatchMap pipm, fr.proline.core.orm.msi.PeptideMatch pm, fr.proline.core.orm.msi.PeptideInstance pi, fr.proline.core.orm.msi.Peptide p, fr.proline.core.orm.msi.MsQuery ms, fr.proline.core.orm.msi.Spectrum sp   
                // WHERE sm.id.proteinMatchId IN (:proteinMatchList) AND sm.bestPeptideMatchId=pipm.id.peptideMatchId AND pipm.id.peptideInstanceId=pi.id AND pipm.resultSummary.id=:rsmId AND pipm.id.peptideMatchId=pm.id AND pm.peptideId=p.id AND pm.msQuery=ms AND ms.spectrum=sp
                Query peptidesQuery = entityManagerMSI.createQuery("SELECT pi, pm.id, pm.rank, pm.charge, pm.deltaMoz, pm.experimentalMoz, pm.missedCleavage, pm.score, pm.resultSet.id, p, sm, ms.id, ms.initialId, pm.cdPrettyRank, pm.sdPrettyRank, pm.serializedProperties, sp.firstTime, sp.precursorIntensity, sp.title, sm.id.proteinMatchId\n"
                        + "              FROM fr.proline.core.orm.msi.SequenceMatch sm, fr.proline.core.orm.msi.PeptideInstancePeptideMatchMap pipm, fr.proline.core.orm.msi.PeptideMatch pm, fr.proline.core.orm.msi.PeptideInstance pi, fr.proline.core.orm.msi.Peptide p, fr.proline.core.orm.msi.MsQuery ms, fr.proline.core.orm.msi.Spectrum sp  \n"
                        + "              WHERE sm.id.proteinMatchId IN (:proteinMatchList) AND sm.bestPeptideMatchId=pipm.id.peptideMatchId AND pipm.id.peptideInstanceId=pi.id AND pipm.resultSummary.id=:rsmId AND pipm.id.peptideMatchId=pm.id AND pm.peptideId=p.id AND pm.msQuery=ms AND ms.spectrum=sp ");
                peptidesQuery.setParameter("rsmId", rsmId);
                peptidesQuery.setParameter("proteinMatchList", subTask.getSubList(typicalProteinMatchesIdsArray));
                
                List l = peptidesQuery.getResultList(); 
                Iterator<Object[]> itPeptidesQuery = l.iterator();
                
                while (itPeptidesQuery.hasNext()) {
                    Object[] resCur = itPeptidesQuery.next();
                    PeptideInstance pi = (PeptideInstance) resCur[0];
                    DPeptideInstance dpi = new DPeptideInstance(pi.getId(), pi.getPeptide().getId(), pi.getValidatedProteinSetCount(), pi.getElutionTime());

                    Long pmId = (Long) resCur[1];
                    Integer pmRank = (Integer) resCur[2];
                    Integer pmCharge = (Integer) resCur[3];
                    Float pmDeltaMoz = (Float) resCur[4];
                    Double pmExperimentalMoz = (Double) resCur[5];
                    Integer pmMissedCleavage = (Integer) resCur[6];
                    Float pmScore = (Float) resCur[7];
                    Long pmResultSetId = (Long) resCur[8];
                    Integer pmCdPrettyRank = (Integer) resCur[13];
                    Integer pmSdPrettyRank = (Integer) resCur[14];
                    String serializedProperties = (String) resCur[15];
                    Float firstTime = (Float) resCur[16];
                    Float precursorIntensity = (Float) resCur[17];
                    String title = (String) resCur[18];
                    Long proteinMatchId = (Long) resCur[19];

                    DSpectrum spectrum = new DSpectrum();
                    spectrum.setFirstTime(firstTime);
                    spectrum.setPrecursorIntensity(precursorIntensity);
                    spectrum.setTitle(title);

                    DPeptideMatch pm = new DPeptideMatch(pmId, pmRank, pmCharge, pmDeltaMoz, pmExperimentalMoz, pmMissedCleavage, pmScore, pmResultSetId, pmCdPrettyRank, pmSdPrettyRank);
                    pm.setRetentionTime(firstTime);
                    pm.setSerializedProperties(serializedProperties);
                    peptideMatchArray.add(pm);

                    Peptide p = (Peptide) resCur[9];
                    p.getTransientData().setPeptideReadablePtmStringLoaded();
                    allPeptidesMap.put(p.getId(), p);

                    SequenceMatch sm = (SequenceMatch) resCur[10];
                    Long msqId = (Long) resCur[11];
                    Integer msqInitialId = (Integer) resCur[12];

                    DMsQuery msq = new DMsQuery(pmId, msqId, msqInitialId, precursorIntensity);
                    msq.setDSpectrum(spectrum);

                    dpi.setBestPeptideMatch(pm);

                    pm.setSequenceMatch(sm);


                    pm.setPeptide(p);
                    pm.setMsQuery(msq);

                    ArrayList<DPeptideInstance> peptideInstanceList = proteinMatchIdToPeptideInstancehMap.get(proteinMatchId);
                    if (peptideInstanceList == null) {
                        peptideInstanceList = new ArrayList<>();
                        proteinMatchIdToPeptideInstancehMap.put(proteinMatchId, peptideInstanceList);
                    }
                    peptideInstanceList.add(dpi);
                    
                    
                }

                subTask = subTaskManager.getNextSubTask();
                
            }
            
            
            // Retrieve PeptideReadablePtmString
            Long rsetId = m_rsm.getResultSet().getId();
            Query ptmStingQuery = entityManagerMSI.createQuery("SELECT p.id, ptmString FROM fr.proline.core.orm.msi.Peptide p, fr.proline.core.orm.msi.PeptideReadablePtmString ptmString WHERE p.id IN (:listId) AND ptmString.peptide=p AND ptmString.resultSet.id=:rsetId");
            ptmStingQuery.setParameter("listId", allPeptidesMap.keySet());
            ptmStingQuery.setParameter("rsetId", rsetId);

            List<Object[]> ptmStrings = ptmStingQuery.getResultList();
            Iterator<Object[]> it = ptmStrings.iterator();
            while (it.hasNext()) {
                Object[] res = it.next();
                Long peptideId = (Long) res[0];
                PeptideReadablePtmString ptmString = (PeptideReadablePtmString) res[1];
                Peptide peptide = allPeptidesMap.get(peptideId);
                peptide.getTransientData().setPeptideReadablePtmString(ptmString);
            }
            
            
            for (DProteinMatch pm : proteinMatchMap.values()) {
                ArrayList<DPeptideInstance> peptideInstanceList = proteinMatchIdToPeptideInstancehMap.get(pm.getId());
                if (peptideInstanceList == null) {
                    continue; // JPM.BUG ???, it is odd, it can happens, to be checked
                }
                DPeptideInstance[] peptideInstanceArray = peptideInstanceList.toArray(new DPeptideInstance[peptideInstanceList.size()]);
                pm.getPeptideSet(rsmId).setTransientDPeptideInstances(peptideInstanceArray);
            }
            
            // fetch Generic PTM Data
            fetchGenericPTMData();
            
            // fetch Specific Data for the Peptides Found
            HashMap<Long, ArrayList<DPeptidePTM>> ptmMap = fetchPTMDataForPeptides(new ArrayList(allPeptidesMap.keySet()));
        
            for (DPeptideMatch pm : peptideMatchArray) {
                Peptide p = pm.getPeptide();
                Long idPeptide = p.getId();
                pm.setPeptidePTMArray(ptmMap.get(idPeptide));
                
                HashMap<Integer, DPeptidePTM> mapToPtm = new HashMap<>();
                ArrayList<DPeptidePTM> ptmList = ptmMap.get(p.getId());
                if (ptmList != null) {
                    for (DPeptidePTM peptidePTM : ptmList) {
                        mapToPtm.put((int) peptidePTM.getSeqPosition(), peptidePTM);
                    }
                    p.getTransientData().setDPeptidePtmMap(mapToPtm);
                }
            }
            
            
            // create the list of DProteinPTMSite
            for (DProteinMatch proteinMatch : typicalProteinMatchesArray) {
                DPeptideInstance[] peptideInstanceList = proteinMatch.getPeptideSet(rsmId).getTransientDPeptideInstances();
                if (peptideInstanceList != null) {
                    for (int i = 0; i < peptideInstanceList.length; i++) {
                        DPeptideInstance peptideInstance = peptideInstanceList[i];
                        DPeptideMatch peptideMatch = peptideInstance.getBestPeptideMatch();
                        ArrayList<DPeptidePTM> peptidePTMArray = peptideMatch.getPeptidePTMArray();
                        if (peptidePTMArray == null) {
                            continue;
                        }
                        double deltaMass = 0;
                        for (DPeptidePTM peptidePTM : peptidePTMArray) {
                            DInfoPTM infoPtm = DInfoPTM.getInfoPTMMap().get(peptidePTM.getIdPtmSpecificity());
                            deltaMass += infoPtm.getMonoMass();
                        }
                        for (DPeptidePTM peptidePTM : peptidePTMArray) {
                            m_proteinPTMSiteArray.add(new DProteinPTMSite(proteinMatch, peptideMatch, peptidePTM, deltaMass));
                        }

                    }
                }
            }
            
            entityManagerMSI.getTransaction().commit();
        } catch (Exception e) {
            m_logger.error(getClass().getSimpleName() + " failed", e);
            m_taskError = new TaskError(e);
            try {
                entityManagerMSI.getTransaction().rollback();
            } catch (Exception rollbackException) {
                m_logger.error(getClass().getSimpleName() + " failed : potential network problem", rollbackException);
            }
            return false;
        } finally {
            entityManagerMSI.close();
        }

        return true;
    }

    private boolean fetchGenericPTMData() {

        HashMap<Long, DInfoPTM> infoPTMMAp = DInfoPTM.getInfoPTMMap();
        if (!infoPTMMAp.isEmpty()) {
            return true; // already loaded
        }

        EntityManager entityManagerPS = DataStoreConnectorFactory.getInstance().getPsDbConnector().getEntityManagerFactory().createEntityManager();
        try {

            entityManagerPS.getTransaction().begin();

            TypedQuery<DInfoPTM> ptmInfoQuery = entityManagerPS.createQuery("SELECT new fr.proline.core.orm.msi.dto.DInfoPTM(spec.id, spec.residue, spec.location, ptm.shortName, evidence.composition, evidence.monoMass) \n"
                    + "FROM fr.proline.core.orm.ps.PtmSpecificity as spec, fr.proline.core.orm.ps.Ptm as ptm, fr.proline.core.orm.ps.PtmEvidence as evidence \n"
                    + "WHERE spec.ptm=ptm AND ptm=evidence.ptm AND evidence.type='Precursor' ", DInfoPTM.class);

            List<DInfoPTM> ptmInfoList = ptmInfoQuery.getResultList();

            Iterator<DInfoPTM> it = ptmInfoList.iterator();
            while (it.hasNext()) {
                DInfoPTM infoPTM = it.next();
                DInfoPTM.addInfoPTM(infoPTM);
            }

            entityManagerPS.getTransaction().commit();

            return true;
        } catch (Exception e) {
            m_logger.error(getClass().getSimpleName() + " failed", e);
            m_taskError = new TaskError(e);
            try {
                entityManagerPS.getTransaction().rollback();
            } catch (Exception rollbackException) {
                m_logger.error(getClass().getSimpleName() + " failed : potential network problem", rollbackException);
            }
            return false;
        } finally {
            entityManagerPS.close();
        }

    }
    
    private HashMap<Long, ArrayList<DPeptidePTM>> fetchPTMDataForPeptides(ArrayList<Long> allPeptidesIds) {


        EntityManager entityManagerPS = DataStoreConnectorFactory.getInstance().getPsDbConnector().getEntityManagerFactory().createEntityManager();
        try {

            entityManagerPS.getTransaction().begin();

            HashMap<Long, ArrayList<DPeptidePTM>> ptmMap = new HashMap<>();
            
            SubTaskManager subTaskManager = new SubTaskManager(1);
            SubTask subTask = subTaskManager.sliceATaskAndGetFirst(0, allPeptidesIds.size(), SLICE_SIZE);
            while (subTask != null) {
  
                TypedQuery<DPeptidePTM> ptmQuery = entityManagerPS.createQuery("SELECT new fr.proline.core.orm.msi.dto.DPeptidePTM(pptm.peptide.id, pptm.specificity.id, pptm.seqPosition) \n"
                    + "FROM PeptidePtm as pptm  \n"
                    + "WHERE pptm.peptide.id IN (:peptideList) ", DPeptidePTM.class);
                ptmQuery.setParameter("peptideList", subTask.getSubList(allPeptidesIds));

                List<DPeptidePTM> ptmList = ptmQuery.getResultList();

                Iterator<DPeptidePTM> it = ptmList.iterator();
                while (it.hasNext()) {
                    DPeptidePTM ptm = it.next();
                    Long peptideId = ptm.getIdPeptide();
                    ArrayList<DPeptidePTM> list = ptmMap.get(peptideId);
                    if (list == null) {
                        list =new ArrayList<>();
                        ptmMap.put(peptideId, list);
                    }
                    list.add(ptm);
                    
                    //Peptide
                    
                }
 
                
                subTask = subTaskManager.getNextSubTask();
            }

            entityManagerPS.getTransaction().commit();

            return ptmMap;
        } catch (Exception e) {
            m_logger.error(getClass().getSimpleName() + " failed", e);
            m_taskError = new TaskError(e);
            try {
                entityManagerPS.getTransaction().rollback();
            } catch (Exception rollbackException) {
                m_logger.error(getClass().getSimpleName() + " failed : potential network problem", rollbackException);
            }
            return null;
        } finally {
            entityManagerPS.close();
        }

    }
    

    
}