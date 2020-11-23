package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Anithav on 13/06/20.
 */

public class DeAccessionRestControllerUT extends BaseControllerUT{

    @Value("${scsb.circ.url}")
    private String scsbCircUrl;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private DeAccessionRestController deAccessionRestController;

    @Mock
     RestHeaderService restHeaderService;

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }
    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }



    @Test
    public void testdeAccession_Exception() throws Exception {
        ReflectionTestUtils.setField(deAccessionRestController,"restHeaderService",restHeaderService);
        String deAccessionRequest ="te";
        Map<String, String> responseEntity = new HashMap<>();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(new HttpHeaders());
        Mockito.when(deAccessionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(deAccessionRestController.getScsbCircUrl()).thenReturn("test");
        Mockito.when(deAccessionRestController.getRestTemplate().postForObject(getScsbCircUrl() + "/sharedCollection/deAccession", deAccessionRequest, Map.class)).thenReturn(responseEntity);
        Mockito.when(deAccessionRestController.deAccession(deAccessionRequest)).thenCallRealMethod();
        Map<String, String> ResponseEntity  = deAccessionRestController.deAccession(deAccessionRequest);
        assertNotNull(ResponseEntity);
    }
}
