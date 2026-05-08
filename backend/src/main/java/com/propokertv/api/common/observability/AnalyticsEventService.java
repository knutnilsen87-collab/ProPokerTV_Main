package com.propokertv.api.common.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AnalyticsEventService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsEventService.class);

    public void track(String eventName, Map<String, ?> properties) {
        log.info("PPTV_EVENT name={} properties={}", eventName, properties);
    }
}
