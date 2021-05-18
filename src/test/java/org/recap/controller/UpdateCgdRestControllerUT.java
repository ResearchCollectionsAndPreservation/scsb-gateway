package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
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

    @Value("${" + PropertyKeyConstants.SCSB_SOLR_DOC_URL + "}")
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
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(ScsbCommonConstants.SUCCESS,HttpStatus.OK);
        updateCgdRestController.setScsbSolrClientUrl(getScsbSolrClientUrl());
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + ScsbConstants.URL_UPDATE_CGD)
                .queryParam(ScsbCommonConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .queryParam(ScsbConstants.OWNING_INSTITUTION, owningInstitution)
                .queryParam(ScsbCommonConstants.OLD_CGD, oldCollectionGroupDesignation)
                .queryParam(ScsbCommonConstants.NEW_CGD, newCollectionGroupDesignation)
                .queryParam(ScsbCommonConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                .queryParam(ScsbCommonConstants.USER_NAME, username);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class)).thenReturn(responseEntity);
        String response = updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes, username);
        assertNotNull(response);
        assertEquals(ScsbCommonConstants.SUCCESS,response);
    }

    @Test
    public void updateCgdForItem_Exception() {
        updateCgdRestController.setScsbSolrClientUrl(getScsbSolrClientUrl());
        HttpEntity requestEntity = new HttpEntity<>(restHeaderService.getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + ScsbConstants.URL_UPDATE_CGD)
                .queryParam(ScsbCommonConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                .queryParam(ScsbCommonConstants.OWNING_INSTITUTION, owningInstitution)
                .queryParam(ScsbCommonConstants.OLD_CGD, oldCollectionGroupDesignation)
                .queryParam(ScsbCommonConstants.NEW_CGD, newCollectionGroupDesignation)
                .queryParam(ScsbCommonConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                .queryParam(ScsbCommonConstants.USER_NAME, username);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class)).thenReturn(null);
        String response = updateCgdRestController.updateCgdForItem(itemBarcode,owningInstitution,oldCollectionGroupDesignation,newCollectionGroupDesignation,cgdChangeNotes, username);
        assertTrue(response.contains(ScsbCommonConstants.FAILURE));
    }

}
