package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EncryptEmailAddressRestControllerUT extends BaseControllerUT{

    @Mock
    EncryptEmailAddressRestController encryptEmailAddressRestController;

    @Mock
    private RestTemplate mockRestTemplate;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void encryptEmailAddress() throws Exception {
        HttpEntity requestEntity = getHttpEntity();
        Mockito.when(encryptEmailAddressRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        ResponseEntity<String> exchange=new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.when(encryptEmailAddressRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(encryptEmailAddressRestController.getHttpEntity()).thenReturn(requestEntity);
        Mockito.when(mockRestTemplate.exchange(scsbCircUrl + "/encryptEmailAddress/startEncryptEmailAddress", HttpMethod.GET, requestEntity, String.class)).thenReturn(exchange);
        Mockito.when(encryptEmailAddressRestController.encryptEmailAddress()).thenCallRealMethod();
        ResponseEntity responseEntity=encryptEmailAddressRestController.encryptEmailAddress();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void encryptEmailAddressException() throws Exception {
        HttpEntity requestEntity = getHttpEntity();
        Mockito.when(encryptEmailAddressRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        ResponseEntity<String> exchange=new ResponseEntity<>(RecapCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.when(encryptEmailAddressRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(encryptEmailAddressRestController.getHttpEntity()).thenReturn(requestEntity);
        Mockito.when(mockRestTemplate.exchange(scsbCircUrl + "/encryptEmailAddress/startEncryptEmailAddress", HttpMethod.GET, requestEntity, String.class)).thenThrow(NullPointerException.class);
        Mockito.when(encryptEmailAddressRestController.encryptEmailAddress()).thenCallRealMethod();
        ResponseEntity responseEntity=encryptEmailAddressRestController.encryptEmailAddress();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity.getStatusCode());
    }
    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }
    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(RecapCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}