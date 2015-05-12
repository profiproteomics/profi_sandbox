/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.proline.studio.dpm.task;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.rpc2.JsonRpcRequest;
import com.google.api.client.util.ArrayMap;
import fr.proline.core.orm.uds.dto.DDataset;
import fr.proline.studio.dam.taskinfo.TaskError;
import fr.proline.studio.dam.taskinfo.TaskInfo;
import static fr.proline.studio.dpm.task.AbstractServiceTask.TASK_LIST_INFO;
import static fr.proline.studio.dpm.task.AbstractServiceTask.m_idIncrement;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * task to retrieve the information for export configuration
 */
public class GetExportInformationTask extends AbstractServiceTask {

    private static String m_request = "dps.uds/get_export_information/";

    private DDataset m_dataset;
    private String m_mode;

    // contains the json string representing the configuration
    private List<String> m_customizableExport;

    public GetExportInformationTask(AbstractServiceCallback callback, DDataset dataset, List<String> config) {
        super(callback, false /**
                 * asynchronous
                 */
                , new TaskInfo("Get Export Information " + (dataset == null ? "null" : dataset.getName()), true, TASK_LIST_INFO, TaskInfo.INFO_IMPORTANCE_MEDIUM));
        m_dataset = dataset;
        m_customizableExport = config;
    }

    public GetExportInformationTask(AbstractServiceCallback callback, String mode, List<String> config) {
        super(callback, false /**
                 * asynchronous
                 */
                , new TaskInfo("Get Export Information " + mode, true, TASK_LIST_INFO, TaskInfo.INFO_IMPORTANCE_MEDIUM));
        m_mode = mode;
        m_customizableExport = config;
    }

    @Override
    public boolean askService() {

        try {
            // create the request
            JsonRpcRequest request = new JsonRpcRequest();

            request.setId(m_id);
            request.setMethod("run_job");

            Map<String, Object> params = new HashMap<>();
            Map<String, Object> extraParams = new HashMap<>();
            if (m_dataset != null) {
                params.put("project_id", m_dataset.getProject().getId());
                params.put("dataset_id", m_dataset.getId());
            } else {
                extraParams.put("export_mode", m_mode);
            }
            params.put("extra_params", extraParams);

            request.setParameters(params);

            HttpResponse response = postRequest(m_request + request.getMethod() + getIdString(), request);

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
            m_taskError = new TaskError(e);
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

            HttpResponse response = postRequest(m_request + request.getMethod() + getIdString(), request);

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
                Boolean success = (Boolean) resultMap.get("success");  //JPM.TODO : get ResultSummary created

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

                    // retrieve configuration 
                    String configStr = (String) resultMap.get("result");
                    if (configStr == null || configStr.isEmpty()) {
                        m_loggerProline.error(getClass().getSimpleName() + " failed : No configuration returned.");
                        return ServiceState.STATE_FAILED;
                    }
                    m_customizableExport.add(configStr);

                    return ServiceState.STATE_DONE;
                } else {
                    String errorMessage = (String) resultMap.get("message");

                    if (errorMessage != null) {
                        m_taskError = new TaskError(errorMessage);
                        m_loggerWebcore.error(getClass().getSimpleName() + " failed : " + errorMessage);
                    }

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
}