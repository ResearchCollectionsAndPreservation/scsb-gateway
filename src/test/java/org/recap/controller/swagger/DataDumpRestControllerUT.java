package org.recap.controller.swagger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/7/17.
 */
public class DataDumpRestControllerUT extends BaseTestCase{

    @InjectMocks
    DataDumpRestController dataDumpRestController;

    @Mock
    RestTemplate restTemplate;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(dataDumpRestController,"scsbEtlUrl",scsbEtlUrl);
    }

    String institutionCodes = "PUL";
    String requestingInstitutionCode = "CUL";
    String fetchType = "1";
    String outputFormat = "1";
    String date = new Date().toString();
    String collectionGroupIds = "1";
    String transmissionType = "1";
    String imsDepositoryCodes = "RECAP";
    String emailToAddress = "hemalatha.s@htcindia.com";
    String toDate = new Date().toString();

    @Test
    public void testDataDumpRestController(){
        Map<String, String> inputMap = getInputMap();
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&imsDepositoryCodes={imsDepositoryCodes}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, getHttpEntity(), String.class, inputMap)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }

    @Test
    public void testDataDumpRestController_Exception(){
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    @Test
    public void testexportDataDumpWithToDate(){
        Map<String,String> inputMap = getInputMap();
        inputMap.put("toDate",toDate);
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, getHttpEntity(), String.class, inputMap)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }


    @Test
    public void testexportDataDumpWithToDate_Exception(){
        Map<String,String> inputMap = new HashMap<>();
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, getHttpEntity(), String.class, inputMap)).thenReturn(null);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    private Map<String, String> getInputMap() {
        Map<String,String> inputMap = new HashMap<>();
        inputMap.put("institutionCodes",institutionCodes);
        inputMap.put("requestingInstitutionCode",requestingInstitutionCode);
        inputMap.put("imsDepositoryCodes",imsDepositoryCodes);
        inputMap.put("fetchType",fetchType);
        inputMap.put("outputFormat",outputFormat);
        inputMap.put("date",date);
        inputMap.put("collectionGroupIds",collectionGroupIds);
        inputMap.put("transmissionType",transmissionType);
        inputMap.put("emailToAddress",emailToAddress);
        return inputMap;
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        return new HttpEntity(headers);
    }

}