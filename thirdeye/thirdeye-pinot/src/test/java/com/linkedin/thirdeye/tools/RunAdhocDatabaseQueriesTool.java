package com.linkedin.thirdeye.tools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.linkedin.thirdeye.anomaly.task.TaskConstants;
import com.linkedin.thirdeye.autoload.pinot.metrics.ConfigGenerator;
import com.linkedin.thirdeye.datalayer.bao.AlertConfigManager;
import com.linkedin.thirdeye.datalayer.bao.AnomalyFunctionManager;
import com.linkedin.thirdeye.datalayer.bao.ClassificationConfigManager;
import com.linkedin.thirdeye.datalayer.bao.DashboardConfigManager;
import com.linkedin.thirdeye.datalayer.bao.DataCompletenessConfigManager;
import com.linkedin.thirdeye.datalayer.bao.DatasetConfigManager;
import com.linkedin.thirdeye.datalayer.bao.DetectionStatusManager;
import com.linkedin.thirdeye.datalayer.bao.EmailConfigurationManager;
import com.linkedin.thirdeye.datalayer.bao.JobManager;
import com.linkedin.thirdeye.datalayer.bao.MergedAnomalyResultManager;
import com.linkedin.thirdeye.datalayer.bao.MetricConfigManager;
import com.linkedin.thirdeye.datalayer.bao.OverrideConfigManager;
import com.linkedin.thirdeye.datalayer.bao.RawAnomalyResultManager;
import com.linkedin.thirdeye.datalayer.bao.TaskManager;
import com.linkedin.thirdeye.datalayer.bao.jdbc.AlertConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.AnomalyFunctionManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.ClassificationConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.DashboardConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.DataCompletenessConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.DatasetConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.DetectionStatusManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.EmailConfigurationManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.JobManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.MergedAnomalyResultManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.MetricConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.OverrideConfigManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.RawAnomalyResultManagerImpl;
import com.linkedin.thirdeye.datalayer.bao.jdbc.TaskManagerImpl;
import com.linkedin.thirdeye.datalayer.dto.AlertConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.AnomalyFunctionDTO;
import com.linkedin.thirdeye.datalayer.dto.ClassificationConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.DashboardConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.DataCompletenessConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.DatasetConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.DetectionStatusDTO;
import com.linkedin.thirdeye.datalayer.dto.EmailConfigurationDTO;
import com.linkedin.thirdeye.datalayer.dto.JobDTO;
import com.linkedin.thirdeye.datalayer.dto.MergedAnomalyResultDTO;
import com.linkedin.thirdeye.datalayer.dto.MetricConfigDTO;
import com.linkedin.thirdeye.datalayer.dto.OverrideConfigDTO;
import com.linkedin.thirdeye.datalayer.pojo.AlertConfigBean.EmailConfig;
import com.linkedin.thirdeye.datalayer.util.DaoProviderUtil;
import com.linkedin.thirdeye.util.ThirdEyeUtils;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run adhoc queries to db
 */
public class RunAdhocDatabaseQueriesTool {

  private static final Logger LOG = LoggerFactory.getLogger(RunAdhocDatabaseQueriesTool.class);

  private AnomalyFunctionManager anomalyFunctionDAO;
  private EmailConfigurationManager emailConfigurationDAO;
  private RawAnomalyResultManager rawResultDAO;
  private MergedAnomalyResultManager mergedResultDAO;
  private MetricConfigManager metricConfigDAO;
  private DashboardConfigManager dashboardConfigDAO;
  private OverrideConfigManager overrideConfigDAO;
  private JobManager jobDAO;
  private TaskManager taskDAO;
  private DataCompletenessConfigManager dataCompletenessConfigDAO;
  private DatasetConfigManager datasetConfigDAO;
  private DetectionStatusManager detectionStatusDAO;
  private AlertConfigManager alertConfigDAO;
  private ClassificationConfigManager classificationConfigDAO;

  public RunAdhocDatabaseQueriesTool(File persistenceFile)
      throws Exception {
    init(persistenceFile);
  }

  public void init(File persistenceFile) throws Exception {
    DaoProviderUtil.init(persistenceFile);
    anomalyFunctionDAO = DaoProviderUtil.getInstance(AnomalyFunctionManagerImpl.class);
    emailConfigurationDAO = DaoProviderUtil.getInstance(EmailConfigurationManagerImpl.class);
    rawResultDAO = DaoProviderUtil.getInstance(RawAnomalyResultManagerImpl.class);
    mergedResultDAO = DaoProviderUtil.getInstance(MergedAnomalyResultManagerImpl.class);
    metricConfigDAO = DaoProviderUtil.getInstance(MetricConfigManagerImpl.class);
    dashboardConfigDAO = DaoProviderUtil.getInstance(DashboardConfigManagerImpl.class);
    overrideConfigDAO = DaoProviderUtil.getInstance(OverrideConfigManagerImpl.class);
    jobDAO = DaoProviderUtil.getInstance(JobManagerImpl.class);
    taskDAO = DaoProviderUtil.getInstance(TaskManagerImpl.class);
    dataCompletenessConfigDAO = DaoProviderUtil.getInstance(DataCompletenessConfigManagerImpl.class);
    datasetConfigDAO = DaoProviderUtil.getInstance(DatasetConfigManagerImpl.class);
    detectionStatusDAO = DaoProviderUtil.getInstance(DetectionStatusManagerImpl.class);
    alertConfigDAO = DaoProviderUtil.getInstance(AlertConfigManagerImpl.class);
    classificationConfigDAO = DaoProviderUtil.getInstance(ClassificationConfigManagerImpl.class);
  }

  private void toggleAnomalyFunction(Long id) {
    AnomalyFunctionDTO anomalyFunction = anomalyFunctionDAO.findById(id);
    anomalyFunction.setActive(true);
    anomalyFunctionDAO.update(anomalyFunction);
  }

  private void updateFields() {
    List<EmailConfigurationDTO> emailConfigs = emailConfigurationDAO.findAll();
    for (EmailConfigurationDTO emailConfig : emailConfigs) {
      LOG.info(emailConfig.getId() + " " + emailConfig.getToAddresses());
    }
  }

  private void updateField(Long id) {
    AnomalyFunctionDTO anomalyFunction = anomalyFunctionDAO.findById(id);
    //anomalyFunction.setCron("0/10 * * * * ?");
    anomalyFunction.setActive(true);
    anomalyFunctionDAO.update(anomalyFunction);
  }

  private void customFunction() {
    List<AnomalyFunctionDTO> anomalyFunctionDTOs = anomalyFunctionDAO.findAll();
    for (AnomalyFunctionDTO anomalyFunctionDTO : anomalyFunctionDTOs) {
      anomalyFunctionDTO.setActive(false);
      anomalyFunctionDAO.update(anomalyFunctionDTO);
    }
  }

  private void updateNotified() {
    List<MergedAnomalyResultDTO> mergedResults = mergedResultDAO.findAll();
    for (MergedAnomalyResultDTO mergedResult : mergedResults) {
      mergedResult.setNotified(true);
      mergedResultDAO.update(mergedResult);
    }
  }

  private void updateEmailConfigs() {
    List<EmailConfigurationDTO> emailConfigs = emailConfigurationDAO.findAll();
    for (EmailConfigurationDTO emailConfig : emailConfigs) {
      emailConfig.setActive(false);
      emailConfigurationDAO.update(emailConfig);
    }
  }

  private void createDashboard(String dataset) {

    String dashboardName = ThirdEyeUtils.getDefaultDashboardName(dataset);
    DashboardConfigDTO dashboardConfig = dashboardConfigDAO.findByName(dashboardName);
    dashboardConfig.setMetricIds(ConfigGenerator.getMetricIdsFromMetricConfigs(metricConfigDAO.findByDataset(dataset)));
    dashboardConfigDAO.update(dashboardConfig);
  }

  private void setAlertFilterForFunctionInCollection(String collection, List<String> metricList,
      Map<String, Map<String, String>> metricRuleMap, Map<String, String> defaultAlertFilter) {
    List<AnomalyFunctionDTO> anomalyFunctionDTOs =
        anomalyFunctionDAO.findAllByCollection(collection);
    for (AnomalyFunctionDTO anomalyFunctionDTO : anomalyFunctionDTOs) {
      String topicMetricName = anomalyFunctionDTO.getTopicMetric();
      if (metricList.contains(topicMetricName)) {
        Map<String, String> alertFilter = defaultAlertFilter;
        if (metricRuleMap.containsKey(topicMetricName)) {
          alertFilter = metricRuleMap.get(topicMetricName);
        }
        anomalyFunctionDTO.setAlertFilter(alertFilter);
        anomalyFunctionDAO.update(anomalyFunctionDTO);
        LOG.info("Add alert filter {} to function {} (dataset: {}, topic metric: {})", alertFilter,
            anomalyFunctionDTO.getId(), collection, topicMetricName);
      }
    }
  }

  private Long createOverrideConfig(OverrideConfigDTO overrideConfigDTO) {
    // Check if there exist duplicate override config
    List<OverrideConfigDTO> existingOverrideConfigDTOs = overrideConfigDAO
        .findAllConflictByTargetType(overrideConfigDTO.getTargetEntity(),
            overrideConfigDTO.getStartTime(), overrideConfigDTO.getEndTime());

    for (OverrideConfigDTO existingOverrideConfig : existingOverrideConfigDTOs) {
      if (existingOverrideConfig.equals(overrideConfigDTO)) {
        LOG.warn("Exists a duplicate override config: {}", existingOverrideConfig.toString());
        return null;
      }
    }

    return overrideConfigDAO.save(overrideConfigDTO);
  }

  private void updateOverrideConfig(long id, OverrideConfigDTO overrideConfigDTO) {
    OverrideConfigDTO overrideConfigToUpdated = overrideConfigDAO.findById(id);
    if (overrideConfigToUpdated == null) {
      LOG.warn("Failed to update config {}", id);
    } else {
      overrideConfigToUpdated.setStartTime(overrideConfigDTO.getStartTime());
      overrideConfigToUpdated.setEndTime(overrideConfigDTO.getEndTime());
      overrideConfigToUpdated.setTargetLevel(overrideConfigDTO.getTargetLevel());
      overrideConfigToUpdated.setTargetEntity(overrideConfigDTO.getTargetEntity());
      overrideConfigToUpdated.setOverrideProperties(overrideConfigDTO.getOverrideProperties());
      overrideConfigToUpdated.setActive(overrideConfigDTO.getActive());
      overrideConfigDAO.update(overrideConfigToUpdated);
      LOG.info("Updated config {}" + id);
    }
  }


  private void deleteAllDataCompleteness() {
    for (DataCompletenessConfigDTO dto : dataCompletenessConfigDAO.findAll()) {
      dataCompletenessConfigDAO.delete(dto);
    }
  }

  private void disableAnomalyFunctions(String dataset) {
    List<AnomalyFunctionDTO> anomalyFunctionDTOs = anomalyFunctionDAO.findAllByCollection(dataset);
    for (AnomalyFunctionDTO anomalyFunctionDTO : anomalyFunctionDTOs) {
    anomalyFunctionDTO.setActive(false);
    anomalyFunctionDAO.update(anomalyFunctionDTO);
    }
  }

  private void addRequiresCompletenessCheck(List<String> datasets) {
    for (String dataset : datasets) {
      DatasetConfigDTO dto = datasetConfigDAO.findByDataset(dataset);
      dto.setActive(false);
      datasetConfigDAO.update(dto);
    }
  }

  private void updateDetectionRun(String dataset) {
    for (DetectionStatusDTO dto : detectionStatusDAO.findAll()) {
      if (dto.getDataset().equals(dataset)) {
        dto.setDetectionRun(false);
        detectionStatusDAO.update(dto);
      }
    }
  }

  private void enableDataCompleteness(String dataset) {
    List<DataCompletenessConfigDTO> dtos = dataCompletenessConfigDAO.findAllByDataset(dataset);
    for (DataCompletenessConfigDTO dto : dtos) {
      dto.setDataComplete(true);
      dataCompletenessConfigDAO.update(dto);
    }
  }

  private void disableAlertConfigs() {
    List<AlertConfigDTO> alertConfigs = alertConfigDAO.findAll();
    for (AlertConfigDTO alertConfigDTO : alertConfigs) {
      alertConfigDTO.setActive(false);
      alertConfigDAO.save(alertConfigDTO);
    }
  }

  private void playWithAlertCOnfigs() {
    List<EmailConfigurationDTO> emailConfigs = emailConfigurationDAO.findAll();
    Multimap<String, EmailConfigurationDTO> datasetToEmailConfig = ArrayListMultimap.create();
    for (EmailConfigurationDTO emailConfig : emailConfigs) {
      if (emailConfig.isActive() && !emailConfig.getFunctionIds().isEmpty()) {
        datasetToEmailConfig.put(emailConfig.getCollection(), emailConfig);
      }
    }
    for (String dataset : datasetToEmailConfig.keySet()) {
      List<EmailConfigurationDTO> emailConfigsList = Lists.newArrayList(datasetToEmailConfig.get(dataset));

      String name = "Beta " + dataset + " Alert Config";
      String cron = emailConfigsList.get(0).getCron();
      boolean active = true;
      long watermark = 0L;
      Set<Long> functionIds = new HashSet<>();
      for (EmailConfigurationDTO config : emailConfigsList) {
        functionIds.addAll(config.getFunctionIds());
      }
      EmailConfig emailConfig = new EmailConfig();
      emailConfig.setAnomalyWatermark(watermark);
      emailConfig.setFunctionIds(Lists.newArrayList(functionIds));
      String recipients = "thirdeyeproductteam@linkedin.com";
      String fromAddress = "thirdeye-dev@linkedin.com";

      AlertConfigDTO alertConfig = new AlertConfigDTO();
      alertConfig.setName(name);
      alertConfig.setCronExpression(cron);
      alertConfig.setActive(active);
      alertConfig.setEmailConfig(emailConfig);
      alertConfig.setRecipients(recipients);
      alertConfig.setFromAddress(fromAddress);
      System.out.println(alertConfig);
      alertConfigDAO.save(alertConfig);

    }
  }

  private void createClassificationConfig(String name, long mainFunctionId, List<Long> functionIdList, boolean active) {
    ClassificationConfigDTO configDTO = new ClassificationConfigDTO();
    configDTO.setName(name);
    configDTO.setMainFunctionId(mainFunctionId);
    configDTO.setFunctionIdList(functionIdList);
    configDTO.setActive(active);
    System.out.println(configDTO);
    classificationConfigDAO.save(configDTO);
  }

  private void updateJobIndex() {
    List<JobDTO> jobDTOs = jobDAO.findAll();
    for (JobDTO jobDTO : jobDTOs) {
      String name = jobDTO.getJobName();
      if (!name.contains("-")) {
        continue;
      }
      String[] names = name.split("-");
      try {
        long anomalyFunctionId = Long.parseLong(names[0]);
        jobDTO.setAnomalyFunctionId(anomalyFunctionId);
        jobDTO.setTaskType(TaskConstants.TaskType.ANOMALY_DETECTION);
        jobDTO.setLastModified(new Timestamp(System.currentTimeMillis()));
        jobDAO.save(jobDTO);
      } catch (Exception e) {
        continue;
      }
    }
  }

  private void cleanupDataset(String dataset) {
    List<DashboardConfigDTO> dashboardConfigs = dashboardConfigDAO.findByDataset(dataset);
    for (DashboardConfigDTO dashboardConfig : dashboardConfigs) {
      dashboardConfig.setActive(false);
      dashboardConfigDAO.update(dashboardConfig);
    }
    List<MetricConfigDTO> metricConfigs = metricConfigDAO.findByDataset(dataset);
    for (MetricConfigDTO metricConfig : metricConfigs) {
      metricConfig.setActive(false);
      metricConfigDAO.update(metricConfig);
    }
    DatasetConfigDTO datasetConfig = datasetConfigDAO.findByDataset(dataset);
    datasetConfig.setActive(false);
    datasetConfigDAO.update(datasetConfig);
  }


  public static void main(String[] args) throws Exception {

    File persistenceFile = new File(args[0]);
    if (!persistenceFile.exists()) {
      System.err.println("Missing file:" + persistenceFile);
      System.exit(1);
    }
    RunAdhocDatabaseQueriesTool dq = new RunAdhocDatabaseQueriesTool(persistenceFile);
  }

}
