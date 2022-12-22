package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbConstants;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.service.RequestItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dinakar N created on 26/09/22
 */
@RestController
@RequestMapping("/request-log")
@Slf4j
public class RequestItemLogController extends AbstractController {
    @Autowired
    private RequestItemService requestItemService;

    @PostMapping(path = "/reports", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestLogReportRequest> getRequestLogReports(@RequestBody RequestLogReportRequest requestLogReportRequest) {
        RequestLogReportRequest requestLogReportResponse = requestItemService.getRequests(requestLogReportRequest);
        return new ResponseEntity<>(requestLogReportResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/submit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RequestLogReportRequest> getRequestLogSubmitReports(@RequestBody RequestLogReportRequest requestLogReportRequest) {
        RequestLogReportRequest requestLogReportResponse = null;
        try {
            requestLogReportResponse = requestItemService.submitRequests(requestLogReportRequest);
        } catch (Exception e) {
            requestLogReportResponse = new RequestLogReportRequest();
            requestLogReportResponse.setStatus(ScsbConstants.FAILED);
        }
        return new ResponseEntity<>(requestLogReportResponse, HttpStatus.OK);
    }

}
