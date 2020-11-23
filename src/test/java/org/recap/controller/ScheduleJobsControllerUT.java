package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.service.RestHeaderService;
import org.recap.model.ScheduleJobRequest;
import org.recap.model.ScheduleJobResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by rajeshbabuk on 20/4/17.
 */
public class ScheduleJobsControllerUT extends BaseControllerUT {

    @Value("${scsb.batch.schedule.url}")
    String scsbScheduleUrl;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ScheduleJobsController scheduleJobsController;

    @Autowired
    RestHeaderService restHeaderService;

    public String getScsbScheduleUrl() {
        return scsbScheduleUrl;
    }

    public void setScsbScheduleUrl(String scsbScheduleUrl) {
        this.scsbScheduleUrl = scsbScheduleUrl;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testScheduleJob() throws Exception {
        ScheduleJobRequest scheduleJobRequest = new ScheduleJobRequest();
        scheduleJobRequest.setJobName(RecapCommonConstants.PURGE_EXCEPTION_REQUESTS);
        scheduleJobRequest.setScheduleType(RecapConstants.SCHEDULE);
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        scheduleJobResponse.setMessage("Scheduled");
        ResponseEntity<ScheduleJobResponse> responseEntity = new ResponseEntity<>(scheduleJobResponse, HttpStatus.OK);
        HttpEntity<ScheduleJobRequest> httpEntity = new HttpEntity<>(scheduleJobRequest, restHeaderService.getHttpHeaders());
        Mockito.when(mockRestTemplate.exchange(getScsbScheduleUrl() + RecapCommonConstants.URL_SCHEDULE_JOBS, HttpMethod.POST, httpEntity, ScheduleJobResponse.class)).thenReturn(responseEntity);
        Mockito.when(scheduleJobsController.getRestTemplate()).thenReturn(mockRestTemplate);
        ReflectionTestUtils.setField(scheduleJobsController,"scsbScheduleUrl",scsbScheduleUrl);
        Mockito.when(scheduleJobsController.getScsbScheduleUrl()).thenCallRealMethod();
        Mockito.when(scheduleJobsController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(scheduleJobsController.scheduleJob(scheduleJobRequest)).thenCallRealMethod();
        ScheduleJobResponse scheduleJobResponse1 = scheduleJobsController.scheduleJob(scheduleJobRequest);
        assertNotNull(scheduleJobResponse1);
    }

    @Test
    public void testScheduleJob_Exception() throws Exception {
        ScheduleJobRequest scheduleJobRequest = new ScheduleJobRequest();
        Mockito.when(scheduleJobsController.scheduleJob(scheduleJobRequest)).thenCallRealMethod();
        ScheduleJobResponse scheduleJobResponse1 = scheduleJobsController.scheduleJob(scheduleJobRequest);
        assertNull(scheduleJobResponse1.getMessage());
    }

    @Test
    public void testcustomLoggerTest() throws Exception {
        Mockito.when(scheduleJobsController.customLoggerTest()).thenCallRealMethod();
        ScheduleJobResponse scheduleJobResponse1 = scheduleJobsController.customLoggerTest();
        assertEquals("Scheduler job response",scheduleJobResponse1.getMessage());
    }

}
