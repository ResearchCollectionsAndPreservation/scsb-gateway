package org.recap.controller;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.ScheduleJobRequest;
import org.recap.model.ScheduleJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 5/4/17.
 */
@RestController
@RequestMapping("/scheduleService")
public class ScheduleJobsController extends  AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleJobsController.class);

    @Value("${scsb.batch.schedule.url}")
    private String scsbScheduleUrl;

    /**
     * Gets scsb schedule url.
     *
     * @return the scsb schedule url
     */
    public String getScsbScheduleUrl() {
        return scsbScheduleUrl;
    }


    /**
     *  This method is exposed as scheduler service for other microservices to schedule or reschedule or unschedule a job.
     *
     * @param scheduleJobRequest the schedule job request
     * @return the schedule job response
     */
    @RequestMapping(value="/scheduleJob", method = RequestMethod.POST)
    public ScheduleJobResponse scheduleJob(@RequestBody ScheduleJobRequest scheduleJobRequest) {
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        try {
            HttpEntity<ScheduleJobRequest> httpEntity = new HttpEntity<>(scheduleJobRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ScheduleJobResponse> responseEntity = getRestTemplate().exchange(getScsbScheduleUrl() + RecapCommonConstants.URL_SCHEDULE_JOBS, HttpMethod.POST, httpEntity, ScheduleJobResponse.class);
            scheduleJobResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
            scheduleJobResponse.setMessage(e.getMessage());
        }
        return scheduleJobResponse;
    }
}
