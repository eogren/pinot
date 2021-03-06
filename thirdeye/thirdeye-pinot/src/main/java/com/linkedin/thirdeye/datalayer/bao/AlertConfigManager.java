package com.linkedin.thirdeye.datalayer.bao;

import com.linkedin.thirdeye.datalayer.dto.AlertConfigDTO;
import java.util.List;

public interface AlertConfigManager extends AbstractManager<AlertConfigDTO> {
  List<AlertConfigDTO> findByActive(boolean active);
}
