package org.recap.controller.swagger;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 10/4/17.
 */
public class PurgeRestControllerUT extends BaseTestCase{

    private static final Logger logger = LoggerFactory.getLogger(PurgeRestController.class);

    @Mock
    PurgeRestController purgeRestController;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    @Mock
    HttpEntity mockedHttpEntity;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void testPurgeEmailAddress(){
        Mockito.when(purgeRestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(purgeRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(purgeRestController.getLogger()).thenReturn(logger);
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        Mockito.when(purgeRestController.getHttpEntity()).thenReturn(requestEntity);
        Map<String,Integer> map = new HashMap<>();
        map.put("physicalRequest",1);
        map.put("eddRequest",1);
        ResponseEntity responseEntity1 = new ResponseEntity(map, HttpStatus.OK);
        Mockito.when(purgeRestController.getRestTemplate().exchange(getScsbCircUrl()+ RecapConstants.REST_URL_PURGE_EMAIL_ADDRESS, HttpMethod.GET,requestEntity,Map.class)).thenReturn(responseEntity1);
        Mockito.when(purgeRestController.purgeEmailAddress()).thenCallRealMethod();
        ResponseEntity responseEntity = purgeRestController.purgeEmailAddress();
        assertNotNull(responseEntity);
    }

    @Test
    public void testPurgeEmailAddressException(){
        Mockito.when(purgeRestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(purgeRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(purgeRestController.getLogger()).thenCallRealMethod();
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        Mockito.when(purgeRestController.getHttpEntity()).thenReturn(requestEntity);
        Map<String,Integer> map = new HashMap<>();
        map.put("physicalRequest",1);
        map.put("eddRequest",1);
        Mockito.when(purgeRestController.getRestTemplate().exchange(getScsbCircUrl()+ RecapConstants.REST_URL_PURGE_EMAIL_ADDRESS, HttpMethod.GET,requestEntity,Map.class)).thenThrow(new NullPointerException("Exception occured"));
        Mockito.when(purgeRestController.purgeEmailAddress()).thenCallRealMethod();
        ResponseEntity responseEntity = purgeRestController.purgeEmailAddress();
        assertNotNull(responseEntity);
    }

    @Test
    public void testPurgeExceptionRequests() {
        HttpEntity requestEntity = getHttpEntity();
        Mockito.when(purgeRestController.getHttpEntity()).thenReturn(requestEntity);
        Mockito.when(purgeRestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(purgeRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(purgeRestController.getLogger()).thenReturn(logger);
        Map responseMap = new HashMap();
        responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.SUCCESS);
        ResponseEntity<Map> responseEntity1 = new ResponseEntity<>(responseMap, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(getScsbCircUrl() + RecapConstants.REST_URL_PURGE_EXCEPTION_REQUESTS, HttpMethod.GET, requestEntity, Map.class)).thenReturn(responseEntity1);
        Mockito.when(purgeRestController.purgeExceptionRequests()).thenCallRealMethod();
        ResponseEntity responseEntity = purgeRestController.purgeExceptionRequests();
        assertEquals(responseMap,responseEntity.getBody());
    }

    @Test
    public void testPurgeExceptionRequestsException() {
        Mockito.when(purgeRestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(purgeRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(purgeRestController.getLogger()).thenCallRealMethod();
        HttpEntity requestEntity = new HttpEntity<>(getHttpHeaders());
        Mockito.when(purgeRestController.getHttpEntity()).thenReturn(requestEntity);
        Map responseMap = new HashMap();
        responseMap.put(RecapCommonConstants.STATUS, RecapCommonConstants.SUCCESS);
        Mockito.when(purgeRestController.getRestTemplate().exchange(getScsbCircUrl() + RecapConstants.REST_URL_PURGE_EMAIL_ADDRESS, HttpMethod.GET, requestEntity, Map.class)).thenThrow(new NullPointerException("Exception occured"));
        Mockito.when(purgeRestController.purgeExceptionRequests()).thenCallRealMethod();
        ResponseEntity responseEntity = purgeRestController.purgeExceptionRequests();
        assertNotNull(responseEntity);
    }

    @Test
    public void testGetterServices(){
        Mockito.when(purgeRestController.getRestTemplate()).thenCallRealMethod();
        Mockito.when(purgeRestController.getScsbCircUrl()).thenCallRealMethod();
        Mockito.when(purgeRestController.getLogger()).thenCallRealMethod();
        Mockito.when(purgeRestController.getHttpEntity()).thenCallRealMethod();
        assertNotEquals(purgeRestController.getRestTemplate(),restTemplate);
        assertNotEquals(purgeRestController.getScsbCircUrl(),scsbCircUrl);
        assertNotEquals(purgeRestController.getHttpEntity(),mockedHttpEntity);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }

}