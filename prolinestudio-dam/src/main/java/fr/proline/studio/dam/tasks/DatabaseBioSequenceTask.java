package fr.proline.studio.dam.tasks;

import fr.proline.core.orm.msi.dto.DBioSequence;
import fr.proline.core.orm.msi.dto.DPeptideInstance;
import fr.proline.core.orm.msi.dto.DPeptideMatch;
import fr.proline.core.orm.msi.dto.DPeptideSet;
import fr.proline.core.orm.msi.dto.DProteinMatch;
import fr.proline.core.orm.util.DStoreCustomPoolConnectorFactory;
import fr.proline.module.seq.BioSequenceProvider;
import fr.proline.studio.dam.DatabaseDataManager;
import fr.proline.studio.dam.taskinfo.TaskInfo;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Find biosequence for a list of ProteinMatch, check the biosequence according to the validated peptides when it is possible
 * @author JM235353
 */
public class DatabaseBioSequenceTask {

    public static boolean fetchData(List<DProteinMatch> proteinMatchList, Long projectId) {

        Map<Long, DProteinMatch> proteinMatchById = proteinMatchList.stream().collect(Collectors.toMap(pm -> pm.getId(), pm -> pm));
        EntityManager entityManagerMSI = DStoreCustomPoolConnectorFactory.getInstance().getMsiDbConnector(projectId).createEntityManager();
        try {

            Query query = entityManagerMSI.createQuery("SELECT pm.id, bs.sequence, bs.mass, bs.pi FROM fr.proline.core.orm.msi.ProteinMatch pm, fr.proline.core.orm.msi.BioSequence bs WHERE bs.id = pm.bioSequenceId AND pm.id IN (:proteinMatchIds)");
            query.setParameter("proteinMatchIds", proteinMatchById.keySet());
            Iterator<Object[]> itProteinGroupsQuery = query.getResultList().iterator();
            while (itProteinGroupsQuery.hasNext()) {
                Object[] resCur = itProteinGroupsQuery.next();
                Long proteinMatchId = (Long) resCur[0];
                String sequence = (String) resCur[1];
                Integer mass = (Integer) resCur[2];
                Float pI = (Float) resCur[3];
                proteinMatchById.get(proteinMatchId).setDBioSequence(new DBioSequence(sequence, mass, pI));
            }

        } catch (RuntimeException e) {

        } finally {
            entityManagerMSI.close();
        }
        return true;
    }

    public static boolean fetchData_PreviousImpl(List<DProteinMatch> proteinMatchList, Long rsmId) {

        int nbProteinMatches = proteinMatchList.size();
        
        if (! DatabaseDataManager.getDatabaseDataManager().isSeqDatabaseExists()) {
            // Seq Database does not exists : no data is available
            for (int i = 0; i < nbProteinMatches; i++) {
                DProteinMatch proteinMatch = proteinMatchList.get(i);
                proteinMatch.setDBioSequence(null);
            }
            return true;
        }
        
        
        ArrayList<String> values = new ArrayList<>(nbProteinMatches);
        for (int i = 0; i < nbProteinMatches; i++) {
            String accession = proteinMatchList.get(i).getAccession();
            values.add(accession);
        }

        Map<String, BioSequenceProvider.RelatedIdentifiers> result = BioSequenceProvider.findSEDbIdentRelatedData(values);

        for (int i = 0; i < nbProteinMatches; i++) {
            DProteinMatch proteinMatch = proteinMatchList.get(i);

            BioSequenceProvider.RelatedIdentifiers relatedObjects = result.get(proteinMatch.getAccession());
            if(relatedObjects == null || relatedObjects.getDBioSequences() == null || relatedObjects.getDBioSequences().isEmpty()){
                proteinMatch.setDBioSequence(null);
                continue;
            }

            List<fr.proline.module.seq.dto.DBioSequence> bioSequenceWrapperList = relatedObjects.getDBioSequences();

            DPeptideSet peptideSet = null;
            if (rsmId != null) {
                peptideSet = proteinMatch.getPeptideSet(rsmId);
            }


            DPeptideInstance[] peptideInstances = (peptideSet == null) ? null : peptideSet.getPeptideInstances();
            if (peptideInstances == null) {
                // we can not check with peptides, we return the first biosequence
                fr.proline.module.seq.dto.DBioSequence biosequenceWrapperSelected = null;
                if (bioSequenceWrapperList.size() > 0) {
                    biosequenceWrapperSelected = bioSequenceWrapperList.get(0);
                }
                if (biosequenceWrapperSelected == null) {
                    proteinMatch.setDBioSequence(null);
                } else {
                    proteinMatch.setDBioSequence(new DBioSequence(biosequenceWrapperSelected.getSequence(), biosequenceWrapperSelected.getMass(), biosequenceWrapperSelected.getPI()));
                }
            } else {

                // we check the biosequence according to the peptides found for the ProteinMatch

                fr.proline.module.seq.dto.DBioSequence biosequenceWrapperSelected = null;
                int nb = bioSequenceWrapperList.size();
                for (int j = 0; j < nb; j++) {
                    biosequenceWrapperSelected = bioSequenceWrapperList.get(j);

                    String sequence = biosequenceWrapperSelected.getSequence();

                    // check for different peptides that the sequences math to the biosequence
                    int nbPeptides = peptideInstances.length;
                    for (int k = 0; k < nbPeptides; k++) {

                        DPeptideMatch peptideMatch = peptideInstances[k].getBestPeptideMatch();
                        String peptideSequence = peptideMatch.getPeptide().getSequence();
                        int start = peptideMatch.getSequenceMatch().getId().getStart();
                        int stop = peptideMatch.getSequenceMatch().getId().getStop();
                        if (stop > sequence.length()) {
                            biosequenceWrapperSelected = null;
                            break;
                        }

                        String subSequence = sequence.substring(start - 1, stop);
                        if (subSequence.compareTo(peptideSequence) != 0) {
                            biosequenceWrapperSelected = null;
                            break;
                        }
                    }
                    if (biosequenceWrapperSelected != null) {
                        break;
                    }
                }

                if (biosequenceWrapperSelected == null) {
                    proteinMatch.setDBioSequence(null);
                } else {
                    proteinMatch.setDBioSequence(new DBioSequence(biosequenceWrapperSelected.getSequence(), biosequenceWrapperSelected.getMass(), biosequenceWrapperSelected.getPI()));
                }
            }



        }

        return true;

    }

}
