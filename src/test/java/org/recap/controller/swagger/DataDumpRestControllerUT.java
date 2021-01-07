package org.recap.controller.swagger;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 17/7/17.
 */
public class DataDumpRestControllerUT extends BaseTestCase{

    @Mock
    DataDumpRestController dataDumpRestController;

    @Mock
    RestTemplate restTemplate;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @Test
    public void testDataDumpRestController(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity requestEntity = new HttpEntity(headers);
        String institutionCodes = "PUL";
        String requestingInstitutionCode = "CUL";
        String fetchType = "1";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1";
        String transmissionType = "1";
        String emailToAddress = "hemalatha.s@htcindia.com";
        String imsDepositoryCodes = "RECAP";

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
        HttpHeaders responseHeaders = getHttpHeaders();
        Mockito.when(dataDumpRestController.getHttpHeaders()).thenReturn(responseHeaders);
        HttpEntity requestEntity1 = getSwaggerHttpEntity();
        Mockito.when(dataDumpRestController.getSwaggerHttpEntity()).thenReturn(requestEntity1);
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        Mockito.when(dataDumpRestController.getScsbEtlUrl()).thenReturn(scsbEtlUrl);
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap)).thenReturn(responseEntity);
        Mockito.when(dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress)).thenCallRealMethod();
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }

    public HttpEntity getSwaggerHttpEntity(){
        return new HttpEntity<>(getSwaggerHeaders());
    }

    public static HttpHeaders getSwaggerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(RecapCommonConstants.API_KEY, SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }

    @Test
    public void testDataDumpRestController_Exception(){
        String institutionCodes = "PUL";
        String requestingInstitutionCode = "CUL";
        String imsDepositoryCodes = "RECAP";
        String fetchType = "1";
        String outputFormat = "1";
        String date = new Date().toString();
        String collectionGroupIds = "1";
        String transmissionType = "1";
        String emailToAddress = "hemalatha.s@htcindia.com";
        Mockito.when(dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress)).thenCallRealMethod();
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    @Test
    public void testexportDataDumpWithToDate(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity requestEntity = new HttpEntity(headers);
        String institutionCodes = "PUL";
        String requestingInstitutionCode = "CUL";
        String imsDepositoryCodes = "RECAP";
        String fetchType = "1";
        String outputFormat = "1";
        String date = new Date().toString();
        String toDate = new Date().toString();
        String collectionGroupIds = "1";
        String transmissionType = "1";
        String emailToAddress = "hemalatha.s@htcindia.com";

        Map<String,String> inputMap = new HashMap<>();
        inputMap.put("institutionCodes",institutionCodes);
        inputMap.put("requestingInstitutionCode",requestingInstitutionCode);
        inputMap.put("fetchType",fetchType);
        inputMap.put("outputFormat",outputFormat);
        inputMap.put("date", date);
        inputMap.put("toDate",toDate);
        inputMap.put("collectionGroupIds",collectionGroupIds);
        inputMap.put("transmissionType",transmissionType);
        inputMap.put("emailToAddress",emailToAddress);
        HttpEntity requestEntity1 = getSwaggerHttpEntity();
        Mockito.when(dataDumpRestController.getSwaggerHttpEntity()).thenReturn(requestEntity1);
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.DATADUMP_PROCESS_STARTED, HttpStatus.OK);
        Mockito.when(dataDumpRestController.getScsbEtlUrl()).thenReturn(scsbEtlUrl);
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap)).thenReturn(responseEntity);
        Mockito.when(dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date,toDate,collectionGroupIds,transmissionType,emailToAddress)).thenCallRealMethod();
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress);
        assertNotNull(responseEntity1);
        assertEquals("Export process has started and we will send an email notification upon completion",responseEntity1.getBody());
    }
    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    @Test
    public void testexportDataDumpWithToDate_Exception(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key","recap");
        HttpEntity requestEntity = new HttpEntity(headers);
        String institutionCodes = "PUL";
        String requestingInstitutionCode = "CUL";
        String imsDepositoryCodes = "RECAP";
        String fetchType = "1";
        String outputFormat = "1";
        String date = new Date().toString();
        String toDate = new Date().toString();
        String collectionGroupIds = "1";
        String transmissionType = "1";
        String emailToAddress = "hemalatha.s@htcindia.com";
        Map<String,String> inputMap = new HashMap<>();
        Mockito.when(restTemplate.exchange(scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap)).thenReturn(null);
        Mockito.when(dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date,toDate,collectionGroupIds,transmissionType,emailToAddress)).thenCallRealMethod();
        ResponseEntity responseEntity1 = dataDumpRestController.exportDataDumpWithToDate(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat, date, toDate,collectionGroupIds,transmissionType,emailToAddress);
        assertEquals("Scsb Etl Service is Unavailable.",responseEntity1.getBody());
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(dataDumpRestController.getScsbEtlUrl()).thenCallRealMethod();
        assertNotEquals(dataDumpRestController.getScsbEtlUrl(),scsbEtlUrl);
    }

}