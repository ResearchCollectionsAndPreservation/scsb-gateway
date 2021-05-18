package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class EncryptEmailAddressRestControllerUT extends BaseControllerUT{

    @InjectMocks
    EncryptEmailAddressRestController encryptEmailAddressRestController;

    @Mock
    private RestTemplate mockRestTemplate;

    @Value("${" + PropertyKeyConstants.SCSB_CIRC_URL + "}")
    String scsbCircUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(encryptEmailAddressRestController,"scsbCircUrl",scsbCircUrl);
    }

    @Test
    public void encryptEmailAddress() throws Exception {
        ResponseEntity<String> exchange=new ResponseEntity<>(ScsbCommonConstants.SUCCESS, HttpStatus.OK);
        Mockito.when(mockRestTemplate.exchange(scsbCircUrl + "/encryptEmailAddress/startEncryptEmailAddress", HttpMethod.GET, getHttpEntity(), String.class)).thenReturn(exchange);
        ResponseEntity responseEntity=encryptEmailAddressRestController.encryptEmailAddress();
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Test
    public void encryptEmailAddressException() throws Exception {
        Mockito.when(mockRestTemplate.exchange(scsbCircUrl + "/encryptEmailAddress/startEncryptEmailAddress", HttpMethod.GET, getHttpEntity(), String.class)).thenThrow(NullPointerException.class);
        ResponseEntity responseEntity=encryptEmailAddressRestController.encryptEmailAddress();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity.getStatusCode());
    }
    public HttpEntity getHttpEntity(){
        return new HttpEntity<>(getHttpHeaders());
    }
    public HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ScsbCommonConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }
}