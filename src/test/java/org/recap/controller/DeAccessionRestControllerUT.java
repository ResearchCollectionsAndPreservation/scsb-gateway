package org.recap.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.PropertyKeyConstants;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by Anithav on 13/06/20.
 */

public class DeAccessionRestControllerUT extends BaseControllerUT{

    @Value("${" + PropertyKeyConstants.SCSB_CIRC_URL + "}")
    String scsbCircUrl;

    @Mock
    RestTemplate mockRestTemplate;

    @InjectMocks
    DeAccessionRestController deAccessionRestController;

    @Mock
     RestHeaderService restHeaderService;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(deAccessionRestController,"restHeaderService",restHeaderService);
    }


    @Test
    public void testdeAccession() throws Exception {
        ReflectionTestUtils.setField(deAccessionRestController,"scsbCircUrl",scsbCircUrl);
        Map<String, String> responseEntity = new HashMap<>();
        Mockito.doReturn(responseEntity).when(mockRestTemplate).postForObject( ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        Map<String, String> responseEntity1  = deAccessionRestController.deAccession("te");
        assertNotNull(responseEntity1);
    }

    @Test
    public void testdeAccession_Exception() throws Exception {
        ReflectionTestUtils.setField(deAccessionRestController,"restTemplate",null);
        Map<String, String> ResponseEntity  = deAccessionRestController.deAccession("te");
        assertNotNull(ResponseEntity);
    }
}
