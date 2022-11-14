package org.recap.controller;


import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.service.RequestItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author Chittoor Charan Raj
 */

public class RequestItemLogControllerUT extends BaseTestCase {

    @InjectMocks
    RequestItemLogController requestItemLogController;

    @Mock
    RequestItemService requestItemService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(requestItemLogController, "requestItemService", requestItemService);
    }

    @Test
    public void getRequestLogReportsTest() {
        RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
        Mockito.when(requestItemService.submitRequests(any())).thenReturn(requestLogReportRequest);
        ResponseEntity<RequestLogReportRequest> requestLogReports = requestItemLogController.getRequestLogReports(requestLogReportRequest);
        assertNotNull(requestLogReports);
    }

    @Test
    public void getRequestLogSubmitReportsTest() {
        RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
        Mockito.when(requestItemService.submitRequests(requestLogReportRequest)).thenReturn(requestLogReportRequest);
        ResponseEntity<RequestLogReportRequest> requestLogReports = requestItemLogController.getRequestLogSubmitReports(requestLogReportRequest);
        assertNotNull(requestLogReports);
    }

    @Test
    public void getRequestLogSubmitReportstest() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        Mockito.when(requestItemService.submitRequests(requestLogReportRequest)).thenThrow(new RuntimeException("Exception occured"));
        ResponseEntity<RequestLogReportRequest> requestLogReports = requestItemLogController.getRequestLogSubmitReports(requestLogReportRequest);
        assertNotNull(requestLogReports);
    }


    public RequestLogReportRequest getRequestLogReportRequest() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        requestLogReportRequest.setStatus("FAILED");
        requestLogReportRequest.setPageSize(1);
        requestLogReportRequest.setId(1);
        requestLogReportRequest.setInstitution("TEST");
        requestLogReportRequest.setFromDate(new Date().toString());
        return requestLogReportRequest;
    }

}
