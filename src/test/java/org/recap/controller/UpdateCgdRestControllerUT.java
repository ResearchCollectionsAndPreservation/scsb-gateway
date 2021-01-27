package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.service.RestHeaderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by rajeshbabuk on 3/1/17.
 */
public class UpdateCgdRestControllerUT extends BaseControllerUT {

    @Value("${scsb.solr.doc.url}")
    String scsbSolrClient;

    @Mock
    private RestTemplate mockRestTemplate;

    @InjectMocks
    private UpdateCgdRestController updateCgdRestController;

    @Mock
    RestHeaderService restHeaderService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClient;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClient = scsbSolrClient;
    }

    String itemBarcode = "3568783121445";
    String owningInstitution = "PUL";
    String oldCollectionGroupDesignation = "Shared";
    String newCollectionGroupDesignation = "Private";
    String cgdChangeNotes = "Notes";
    String username = "guest";

    @Test
    public void updateCgdForItem() {
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(RecapCommonConstants.SUCCESS,HttpStatus.OK);
        updateCgdRestController.setScsbSolrClientUrl(getScsbSolrClientUrl());
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_UPDATE_CGD)
                .queryParam(RecapCommonConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .queryParam(RecapConstants.OWNING_INSTITUTION, owningInstitution)
                .queryParam(RecapCommonConstants.OLD_CGD, oldCollectionGroupDesignation)
                .queryParam(RecapCommonConstants.NEW_CGD, newCollectionGroupDesignation)
                .queryParam(RecapCommonConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                .queryParam(RecapCommonConstants.USER_NAME, username);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class)).thenReturn(responseEntity);
        String response = updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes, username);
        assertNotNull(response);
        assertEquals(RecapCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateCgdForItem_Exception() {
        updateCgdRestController.setScsbSolrClientUrl(getScsbSolrClientUrl());
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_UPDATE_CGD)
                .queryParam(RecapCommonConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .queryParam(RecapCommonConstants.OWNING_INSTITUTION, owningInstitution)
                .queryParam(RecapCommonConstants.OLD_CGD, oldCollectionGroupDesignation)
                .queryParam(RecapCommonConstants.NEW_CGD, newCollectionGroupDesignation)
                .queryParam(RecapCommonConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                .queryParam(RecapCommonConstants.USER_NAME, username);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class)).thenReturn(null);
        String response = updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes, username);
        assertTrue(response.contains(RecapCommonConstants.FAILURE));
    }

}
