package org.recap.controller.swagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.spring.SwaggerAPIProvider;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 17/7/17.
 */
public class DataDumpRestControllerUT extends BaseTestCase{

    @InjectMocks
    DataDumpRestController dataDumpRestController;

    @Mock
    RestTemplate restTemplate;

    @Value("${" + PropertyKeyConstants.SCSB_ETL_URL + "}")
    private String scsbEtlUrl;

    @BeforeEach
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
    String emailToAddress = "test@email.com";
    String toDate = new Date().toString();
    String userName = "UT";

    @Test
    public void testDataDumpRestController(){
        Map<String, String> inputMap = getInputMap();
        ResponseEntity responseEntity = new ResponseEntity(ScsbConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        HttpEntity requestEntity = getSwaggerHttpEntity();
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&imsDepositoryCodes={imsDepositoryCodes}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}&userName={userName}", HttpMethod.GET, requestEntity, String.class, inputMap)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress,userName);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }

    @Test
    public void testDataDumpRestController_Exception(){
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress,userName);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    @Test
    public void testexportDataDumpWithToDate(){
        Map<String,String> inputMap = getInputMap();
        inputMap.put("toDate",toDate);
        ResponseEntity responseEntity = new ResponseEntity(ScsbConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        HttpEntity requestEntity = getSwaggerHttpEntity();
        Mockito.when(restTemplate.exchange(scsbEtlUrl +"dataDump/exportDataDump?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&imsDepositoryCodes={imsDepositoryCodes}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}&userName={userName}", HttpMethod.GET, requestEntity, String.class, inputMap)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress,userName);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }


    @Test
    public void testexportDataDumpWithToDate_Exception(){
        Map<String,String> inputMap = new HashMap<>();
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}&userName={userName}", HttpMethod.GET, getHttpEntity(), String.class, inputMap)).thenReturn(null);
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress,userName);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    private Map<String, String> getInputMap() {
        Map<String,String> inputMap = new HashMap<>();
        inputMap.put("institutionCodes",institutionCodes);
        inputMap.put("requestingInstitutionCode",requestingInstitutionCode);
        inputMap.put("fetchType",fetchType);
        inputMap.put("outputFormat",outputFormat);
        inputMap.put("date",date);
        inputMap.put("collectionGroupIds",collectionGroupIds);
        inputMap.put("transmissionType",transmissionType);
        inputMap.put("emailToAddress",emailToAddress);
        inputMap.put("imsDepositoryCodes",imsDepositoryCodes);
        inputMap.put("userName",userName);
        return inputMap;
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ScsbCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private HttpEntity getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","scsb");
        return new HttpEntity(headers);
    }

    public HttpEntity getSwaggerHttpEntity(){
        return new HttpEntity<>(getSwaggerHeaders());
    }

    public static HttpHeaders getSwaggerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ScsbCommonConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }

}