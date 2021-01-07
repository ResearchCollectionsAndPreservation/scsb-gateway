package org.recap.controller;

import java.util.Date;

import org.recap.RecapCommonConstants;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PostMapping(value="/scheduleJob")
    public ScheduleJobResponse scheduleJob(@RequestBody ScheduleJobRequest scheduleJobRequest) {
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        try {
            HttpEntity<ScheduleJobRequest> httpEntity = new HttpEntity<>(scheduleJobRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ScheduleJobResponse> responseEntity = restTemplate.exchange(getScsbScheduleUrl() + RecapCommonConstants.URL_SCHEDULE_JOBS, HttpMethod.POST, httpEntity, ScheduleJobResponse.class);
            scheduleJobResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
            scheduleJobResponse.setMessage(e.getMessage());
        }
        return scheduleJobResponse;
    }
    
    @GetMapping(value="/logger-test")
    public ScheduleJobResponse customLoggerTest() {
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        scheduleJobResponse.setMessage("Scheduler job response");
        scheduleJobResponse.setNextRunTime(new Date());
        try {
        	logger.info("Inside the customLoggerTest method - ScheduleJobResponse : {}", scheduleJobResponse);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);

        }
        return scheduleJobResponse;
    }
}
