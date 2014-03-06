package fr.proline.studio.dpm.task;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.rpc2.JsonRpcRequest;
import com.google.api.client.util.ArrayMap;
import fr.proline.core.orm.msi.ResultSummary;
import fr.proline.core.orm.uds.Dataset;
import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.dam.taskinfo.TaskError;
import fr.proline.studio.dam.taskinfo.TaskInfo;
import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author JM235353
 */
public class SpectralCountTask extends AbstractServiceTask {

    private DDataset m_refDataset = null;
    private List<DDataset> m_rsmDataset = null;
    private Long[] m_quantiDatasetId = null;
    private String[] m_spCountJSONResult = null;

    public SpectralCountTask(AbstractServiceCallback callback, DDataset refDataset, List<DDataset> rsmDataset, Long[] quantiDatasetId, String[] spectralCountResultList) {
        super(callback, false /*
                 * asynchronous
                 */, new TaskInfo("Spectral Count on " + refDataset.getName(), true, TASK_LIST_INFO));
        m_refDataset = refDataset;
        m_rsmDataset = rsmDataset;
        m_quantiDatasetId = quantiDatasetId;
        m_spCountJSONResult = spectralCountResultList;
    }

    @Override
    public boolean askService() {
        try {
            // create the request
            JsonRpcRequest request = new JsonRpcRequest();

            request.setId(m_id);
            request.setMethod("run_job");


            Map<String, Object> params = new HashMap<>();
            params.put("name", m_refDataset.getName() + " Spectral Count");
            params.put("description", m_refDataset.getName() + " Spectral Count");
            params.put("project_id", m_refDataset.getProject().getId());
            params.put("ref_rsm_id", m_refDataset.getResultSummaryId());

            // experimental_design
            Map<String, Object> experimentalDesignParams = new HashMap<>();


            List sampleNumbers = new ArrayList();
            List biologicalSampleList = new ArrayList();
            List quantChanneList = new ArrayList();
            int number = 1;
            Iterator<DDataset> itDataset = m_rsmDataset.iterator();
            while (itDataset.hasNext()) {
                DDataset d = itDataset.next();
                String name = d.getName();

                Map<String, Object> biologicalSampleParams = new HashMap<>();
                biologicalSampleParams.put("number", Integer.valueOf(number));
                biologicalSampleParams.put("name", name);

                biologicalSampleList.add(biologicalSampleParams);

                Map<String, Object> quantChannelParams = new HashMap<>();
                quantChannelParams.put("number", Integer.valueOf(number));
                quantChannelParams.put("sample_number", Integer.valueOf(number));
                quantChannelParams.put("ident_result_summary_id", d.getResultSummaryId());

                quantChanneList.add(quantChannelParams);

                sampleNumbers.add(Integer.valueOf(number));

                number++;
            }
            experimentalDesignParams.put("biological_samples", biologicalSampleList);

            List biologicalGroupList = new ArrayList();
            Map<String, Object> biologicalGroupParams = new HashMap<>();
            biologicalGroupParams.put("number", Integer.valueOf(0));
            biologicalGroupParams.put("name", m_refDataset.getName());
            biologicalGroupParams.put("sample_numbers", sampleNumbers);
            biologicalGroupList.add(biologicalGroupParams);
            experimentalDesignParams.put("biological_groups", biologicalGroupList);


            List masterQuantChannelsList = new ArrayList();
            Map<String, Object> masterQuantChannelParams = new HashMap<>();
            masterQuantChannelParams.put("number", 0);
            masterQuantChannelParams.put("name", m_refDataset.getName() + " Spectral Count");
            masterQuantChannelParams.put("quant_channels", quantChanneList);
            masterQuantChannelsList.add(masterQuantChannelParams);
            experimentalDesignParams.put("master_quant_channels", masterQuantChannelsList);

            params.put("experimental_design", experimentalDesignParams);


            request.setParameters(params);
            //m_loggerProline.debug("Will postRequest with params  project_id "+m_refDataset.getProject().getId()+" ; ref_result_summary_id "+m_refDataset.getResultSummaryId()+" ; compute_result_summary_ids "+m_resultSummaryIds);
            HttpResponse response = postRequest("dps.msq/quantifysc/" + request.getMethod() + getIdString(), request);

            GenericJson jsonResult = response.parseAs(GenericJson.class);

            ArrayMap errorMap = (ArrayMap) jsonResult.get("error");

            if (errorMap != null) {
                String message = (String) errorMap.get("message");

                if (message != null) {
                    m_taskError = new TaskError(message);
                }

                String data = (String) errorMap.get("data");
                if (data != null) {
                    if (m_taskError == null) {
                        m_taskError = new TaskError(data);
                    } else {
                        m_taskError.setErrorText(data);
                    }
                }

                if (m_taskError != null) {
                    m_loggerWebcore.error(getClass().getSimpleName() + " failed : " + m_taskError.toString());
                }

                return false;
            }


            BigDecimal jobId = (BigDecimal) jsonResult.get("result");
            if (jobId != null) {
                m_id = jobId.intValue();
            } else {
                m_loggerProline.error(getClass().getSimpleName() + " failed : id not defined");
            }



        } catch (Exception e) {
            m_taskError = new TaskError(e.getMessage());
            m_loggerProline.error(getClass().getSimpleName() + " failed", e);
            return false;
        }

        return true;
    }

    @Override
    public ServiceState getServiceState() {

        try {

            // create the request
            JsonRpcRequest request = new JsonRpcRequest();

            request.setId(m_idIncrement++);
            request.setMethod("get_job_status");

            Map<String, Object> params = new HashMap<>();
            params.put("job_id", m_id);

            request.setParameters(params);

            HttpResponse response = postRequest("dps.msq/quantifysc/" + request.getMethod() + getIdString(), request);

            GenericJson jsonResult = response.parseAs(GenericJson.class);

            ArrayMap errorMap = (ArrayMap) jsonResult.get("error");

            if (errorMap != null) {

                String message = (String) errorMap.get("message");

                if (message != null) {
                    m_taskError = new TaskError(message);
                }

                String data = (String) errorMap.get("data");
                if (data != null) {
                    if (m_taskError == null) {
                        m_taskError = new TaskError(data);
                    } else {
                        m_taskError.setErrorText(data);
                    }
                }

                if (m_taskError != null) {
                    m_loggerWebcore.error(getClass().getSimpleName() + " failed : " + m_taskError.toString());
                }

                return ServiceState.STATE_FAILED; // should not happen !
            }

            ArrayMap resultMap = (ArrayMap) jsonResult.get("result");

            if (resultMap != null) {
                Boolean success = (Boolean) resultMap.get("success");
                // key not used : "duration", "progression" JPM.TODO

                if (success == null) {

                    String message = (String) resultMap.get("message");
                    if ((message != null) && message.startsWith("Running")) {
                        getTaskInfo().setRunning(false);
                    }

                    return ServiceState.STATE_WAITING;
                }

                if (success) {

                    BigDecimal duration = (BigDecimal) resultMap.get("duration");
                    if (duration != null) {
                        getTaskInfo().setDuration(duration.longValue());
                    }

                    Map returnedValues = (Map) resultMap.get("result");
                    if ((returnedValues == null) || (returnedValues.isEmpty())) {
                        m_loggerProline.error(getClass().getSimpleName() + " failed : No returned values");
                        return ServiceState.STATE_FAILED;
                    }

//                    ArrayMap returnedValuesMap = (ArrayMap) returnedValues.get(0);

                    // retrieve Quanti Dataset ID
                    BigDecimal quantiDatasetIdBD = (BigDecimal) returnedValues.get("dataset_quanti_id");
                    if (quantiDatasetIdBD == null) {
                        m_loggerProline.error(getClass().getSimpleName() + " failed : No returned Quanti Dataset Id");
                        return ServiceState.STATE_FAILED;
                    }
                    m_quantiDatasetId[0] = new Long(quantiDatasetIdBD.longValue());

                    //retrieve SC Values as JSON String 
                    String scValues = (String) returnedValues.get("spectral_count_result");
                    if (scValues == null) {
                        m_loggerProline.error(getClass().getSimpleName() + " failed : No Spectral Count returned.");
                        return ServiceState.STATE_FAILED;
                    }
                    m_spCountJSONResult[0] = scValues;

                    return ServiceState.STATE_DONE;
                } else {
                    String errorMessage = (String) resultMap.get("message");
                    if (errorMessage == null) {
                        errorMessage = "";
                    } else {
                        m_taskError = new TaskError(errorMessage);
                    }
                    m_loggerWebcore.error(getClass().getSimpleName() + " failed " + errorMessage);
                    return ServiceState.STATE_FAILED;
                }

            }



        } catch (Exception e) {
            m_taskError = new TaskError(e);
            m_loggerProline.error(getClass().getSimpleName() + " failed", e);
            return ServiceState.STATE_FAILED; // should not happen !
        }

        return ServiceState.STATE_WAITING;

    }

public static class WSCResultData {

        final String rootPropName = "\"spectral_count_result\"";
        final String rsmIDPropName = "\"rsm_id\"";
        final String protSCsListPropName = "\"proteins_spectral_counts\"";
        final String protACPropName = "\"protein_accession\"";
        final String bscPropName = "\"bsc\"";
        final String sscPropName = "\"ssc\"";
        final String wscPropName = "\"wsc\"";
        final String protMatchIdPropName = "\"prot_match_id\"";
        final String protSetIdPropName  = "\"prot_set_id\"";
        final String protMatchStatusPropName ="\"prot_status\"";
        final String pepNbrPropName ="\"pep_nbr\"";
        
        private Map<Long, Map<String, SpectralCountsStruct>> scsByProtByRSMId;
        private DDataset m_refDS;
        private List<DDataset> m_datasetRSMs;

        public WSCResultData(DDataset refDataset, List<DDataset> datasets, String spectralCountResult) {
            m_refDS = refDataset;
            m_datasetRSMs = datasets;
            scsByProtByRSMId = new HashMap<>();
            try {
                initData(spectralCountResult);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        public ResultSummary getRSMReference() {
            return m_refDS.getResultSummary();
        }

        public DDataset getDataSetReference() {
            return m_refDS;
        }

        public List<DDataset> getComputedSCDatasets() {
            return m_datasetRSMs;
        }

        public Map<String, SpectralCountsStruct> getRsmSCResult(Long rsmId) {
            return scsByProtByRSMId.get(rsmId);
        }

        /**
         * Parse SC Result to created formatted data m_scResult is formatted as
         * : "{"spectral_count_result":{[ { "rsm_id":Long,
         * "proteins_spectral_counts":[ {
         * "protein_accession"=Acc,"prot_match_id"=Long, "prot_set_id"=Long, "prot_status"=String, "bsc"=Float,"ssc"=Float,"wsc"=Float}, {...} ]
         * }, { "rsm_id"... } ]}}"
         *
         */
        private void initData(String scResult) {
            //first xx char are constant
            int firstRSMEntryIndex = scResult.indexOf("{" + rsmIDPropName);            
            String parsingSC = scResult.substring(firstRSMEntryIndex);

            String[] rsmEntries = parsingSC.split("\\{" + rsmIDPropName);
            for (String rsmEntry : rsmEntries) { //{"rsm_id":Long,"proteins_spectral_counts":[...
                if (rsmEntry.isEmpty()) {
                    continue;
                }
                String rsmSCResult = rsmEntry.substring(rsmEntry.indexOf(":") + 1);
                //ToDO : Verify rsmId belongs to m_datasetRSMs ?
                Long rsmId = Long.parseLong(rsmSCResult.substring(0, rsmSCResult.indexOf(",")).trim());

                Map<String, SpectralCountsStruct> rsmSCRst = parseRsmSC(rsmSCResult.substring(rsmSCResult.indexOf(protSCsListPropName)));
                scsByProtByRSMId.put(rsmId, rsmSCRst);
            }
        }

        /**
         * Parse one RSM Sc entry
         *
         *
         * "proteins_spectral_counts":[ {
         * "protein_accession"=Acc,"prot_match_id"=Long, "prot_set_id"=Long, "prot_status"=String,"bsc"=Float,"ssc"=Float,"wsc"=Float}, {...} ]
         * },
         *
         * @return Map of spectralCounts for each Protein Matches
         */
        private Map<String, SpectralCountsStruct> parseRsmSC(String rsmsSCResult) {
            m_loggerProline.debug(" parseRsmSC :   " + rsmsSCResult);

            //"proteins_spectral_counts":[{"protein_accession"=MyProt,"bsc"=123.6,"ssc"=45.6,"wsc"=55.5}, {"protein_accession"=OtherProt,"bsc"=17.2,"ssc"=2.6,"wsc"=1.5} ]
            Map<String, SpectralCountsStruct> scByProtAcc = new HashMap<>();

            //Remove "proteins_spectral_counts":[
            String protEntries = rsmsSCResult.substring(rsmsSCResult.indexOf("[") + 1);
            protEntries = protEntries.substring(0, protEntries.indexOf("]"));

            String[] protAccEntries = protEntries.split("}"); //Each ProtAcc entry
            int protIndex = 0;
            for (String protAcc : protAccEntries) {
                //For each protein ...            
                String[] protAccPropertiesEntries = protAcc.split(","); //Get properties list : Acc / bsc / ssc / wsc 
                String protAccStr = null;
                Float bsc = null;
                Float ssc = null;
                Float wsc = null;
                String protMatchStatus = null;
                for (String protProperty : protAccPropertiesEntries) { //Should create 2 entry : key -> value 
                    String[] propKeyValues = protProperty.split("="); //split prop key / value 
                    if (propKeyValues[0].contains(protACPropName)) {
                        protAccStr = propKeyValues[1];
                    }
                    if (propKeyValues[0].contains(bscPropName)) {
                        bsc = Float.valueOf(propKeyValues[1]);
                    }
                    if (propKeyValues[0].contains(sscPropName)) {
                        ssc = Float.valueOf(propKeyValues[1]);
                    }
                    if (propKeyValues[0].contains(wscPropName)) {
                        wsc = Float.valueOf(propKeyValues[1]);
                    }
                    if (propKeyValues[0].contains(protMatchStatusPropName)) {
                        protMatchStatus = propKeyValues[1];
                    }
                }
                if (bsc == null || ssc == null || wsc == null || protAccStr == null) {
                    throw new IllegalArgumentException("Invalid Spectral Count result. Value missing : " + protAcc);
                }
                scByProtAcc.put(protAccStr, new SpectralCountsStruct(bsc, ssc, wsc,protMatchStatus));
                protIndex++;
            }

            return scByProtAcc;

        }
    }


    public static class SpectralCountsStruct {

        Float m_basicSC;
        Float m_specificSC;
        Float m_weightedSC;
        String m_pmStatus;

        public SpectralCountsStruct(Float bsc, Float ssc, Float wsc, String pmStatus) {
            this.m_basicSC = bsc;
            this.m_specificSC = ssc;
            this.m_weightedSC = wsc;
            this.m_pmStatus = pmStatus;
        }

        public Float getBsc() {
            return m_basicSC;
        }

        public Float getSsc() {
            return m_specificSC;
        }

        public Float getWsc() {
            return m_weightedSC;
        }
        
        public String getProtMatchStatus() {
            return m_pmStatus;
        }
        
    }
}
