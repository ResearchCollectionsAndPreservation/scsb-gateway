package org.recap.controller;

import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by rajeshbabuk on 3/1/17.
 */
@RestController
@RequestMapping("/updateCgdService")
public class UpdateCgdRestController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCgdRestController.class);

    /**
     * This method will call scsb-solr-client microservice to update CGD for an item in scsb database and scsb solr.
     *
     * @param itemBarcode                   the item barcode
     * @param owningInstitution             the owning institution
     * @param oldCollectionGroupDesignation the old collection group designation
     * @param newCollectionGroupDesignation the new collection group designation
     * @param cgdChangeNotes                the cgd change notes
     * @return the string
     */
    @GetMapping(value="/updateCgd")
    public String updateCgdForItem(@RequestParam String itemBarcode, @RequestParam String owningInstitution, @RequestParam String oldCollectionGroupDesignation, @RequestParam String newCollectionGroupDesignation, @RequestParam String cgdChangeNotes, @RequestParam String userName) {
        String statusResponse = null;
        try {
            HttpEntity requestEntity = new HttpEntity<>(getRestHeaderService().getHttpHeaders());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbSolrClientUrl() + RecapConstants.URL_UPDATE_CGD)
                    .queryParam(RecapCommonConstants.CGD_UPDATE_ITEM_BARCODE, itemBarcode)
                    .queryParam(RecapConstants.OWNING_INSTITUTION, owningInstitution)
                    .queryParam(RecapCommonConstants.OLD_CGD, oldCollectionGroupDesignation)
                    .queryParam(RecapCommonConstants.NEW_CGD, newCollectionGroupDesignation)
                    .queryParam(RecapCommonConstants.CGD_CHANGE_NOTES, cgdChangeNotes)
                    .queryParam(RecapCommonConstants.USER_NAME, userName);

            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);
            statusResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR,e);
            statusResponse = RecapCommonConstants.FAILURE + "-" + e.getMessage();
        }
        return statusResponse;
    }
}
