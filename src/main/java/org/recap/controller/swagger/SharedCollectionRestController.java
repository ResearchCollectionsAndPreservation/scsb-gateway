package org.recap.controller.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.AbstractController;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.model.accession.AccessionModelRequest;
import org.recap.model.accession.AccessionResponse;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.transfer.TransferRequest;
import org.recap.model.transfer.TransferResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 6/10/16.
 */
@Slf4j
@RestController
@RequestMapping("/sharedCollection")
@Tag(name = "sharedCollection")
public class SharedCollectionRestController extends AbstractController {

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    public LinkedMultiValueMap<String, Object> getLinkedMultiValueMap(){
        return new LinkedMultiValueMap<>();
    }

    /**
     * This method will call scsb-solr-client microservice to get item availability status in scsb.
     *
     * @param itemAvailabityStatus the item availabity status
     * @return the response entity
     */
    @PostMapping(value = "/itemAvailabilityStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "itemAvailabilityStatus",
            description ="The Item availability status API returns the availability status of the item in SCSB. It is likely to be used in partner ILS' Discovery systems to retrieve and display item statuses.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity itemAvailabilityStatus(@Parameter(description = "Item Barcodes with ',' separated", required = true, name = "itemBarcodes") @RequestBody ItemAvailabityStatusRequest itemAvailabityStatus) {
        String response;
        try {
            response = getRestTemplate().postForObject(getScsbSolrClientUrl() + "/sharedCollection/itemAvailabilityStatus", itemAvailabityStatus, String.class);
        } catch (RuntimeException exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            return new ResponseEntity<>(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (StringUtils.isEmpty(response)) {
            return new ResponseEntity<>(ScsbCommonConstants.ITEM_BARCDE_DOESNOT_EXIST, getHttpHeaders(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, getHttpHeaders(), HttpStatus.OK);
        }
    }

    /**
     *This method will call scsb-solr-client microservice to get the bib availability status in scsb.
     *
     * @param bibItemAvailabityStatusRequest the bib item availabity status request
     * @return the response entity
     */
    @PostMapping(value = "/bibAvailabilityStatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "bibAvailabilityStatus",
           description =  "Bib availability status API returns the availability statuses of items associated with the bibliographic record. Since it returns availability statuses of all items associated with a bib, it is likely to be used in partner ILS' Discovery systems to retrieve and display multiple items and their statuses in case of serials and multi volume monographs.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "503", description = "Service Not Available")
    @ResponseBody
    public ResponseEntity bibAvailabilityStatus(@Parameter(description = "Owning Inst BibID, or SCSB BibId", required = true, name = "") @RequestBody BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest) {
        String response;
        try {
            response = getRestTemplate().postForObject(getScsbSolrClientUrl() + "/sharedCollection/bibAvailabilityStatus", bibItemAvailabityStatusRequest, String.class);
        } catch (RuntimeException exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            return new ResponseEntity<>(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<>(response, getHttpHeaders(), HttpStatus.OK);
    }

    /**
     * This method will call scsb-circ microservice to soft delete the given items from the scsb database and scsb solr and mark isDeletedItem field as true.
     *
     * @param deAccessionRequest the de accession request
     * @return the response entity
     */
    @PostMapping(value = "/deaccession", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "deaccession",
           description =  "The Deaccession API is an internal call made by SCSB to remove a record. Deaccession will only be done through the UI by users who are authorized to perform the operation. Deaccessioning an item would mark the record as removed (deleted) in the SCSB database.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity deAccession(@Parameter(description = "Provide item barcodes to be deaccessioned, separated by comma and User Name", required = true, name = "ItemBarcodes and User Name") @RequestBody DeAccessionRequest deAccessionRequest) {
        try {
            Map<String, String> resultMap = getRestTemplate().postForObject(getScsbCircUrl() + "/sharedCollection/deAccession", deAccessionRequest, Map.class);
            return new ResponseEntity<>(resultMap, getHttpHeaders(), HttpStatus.OK);
        } catch (RuntimeException ex) {
            log.error(ScsbCommonConstants.LOG_ERROR, ex);
            return new ResponseEntity<>(ScsbConstants.SCSB_CIRC_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**This method will call scsb-solr-client microservice to add multiple new items in the scsb database and scsb solr.
     * @param accessionModelRequest the accession request list
     * @return the response entity
     */
    @PostMapping(value = "/accessionBatch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "accessionBatch",
           description =  "Accession batch is similar to the accession API except that accession batch API accepts multiple item barcodes in a single request call. The Accession batch process is a deferred process to reduce performance bottlenecks. The barcodes and customer codes are stored in SCSB DB and is processed as per schedule of the Accession job (usually, nightly). After processing, a report on the barcodes that were processed is prepared and stored at the FTP location.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity accessionBatch(@Parameter(description = "Item Barcode and Customer Code", required = true, name = "Item Barcode And Customer Code") @RequestBody AccessionModelRequest accessionModelRequest) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String responseMessage = getRestTemplate().postForObject(getScsbCoreUrl() + "sharedCollection/accessionBatch", accessionModelRequest, String.class);
            ResponseEntity responseEntity = new ResponseEntity<>(responseMessage, getHttpHeaders(), HttpStatus.OK);
            stopWatch.stop();
            log.info("Total time taken for saving accession request-->{}sec", stopWatch.getTotalTimeSeconds());
            return responseEntity;
        } catch (Exception exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            return new ResponseEntity<>(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * This method will call scsb-solr-client microservice to add a new item in the scsb database and scsb solr.
     * @param accessionModelRequest the accession request list
     * @return the response entity
     */
    @PostMapping(value = "/accession", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "accession",
           description =  "The Accession API (also known as Ongoing Accession) is used to add item records to SCSB whenever there is a new item added to the Storage Location facility. IMS Location facility calls this API as part of the accession workflow with the customer code, item barcode and with their location code.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity accession(@Parameter(description = "Item Barcode and Customer Code", required = true, name = "Item Barcode And Customer Code") @RequestBody AccessionModelRequest accessionModelRequest) {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            ResponseEntity responseEntity;
            List<LinkedHashMap> linkedHashMapList = getRestTemplate().postForObject(getScsbCoreUrl() + "sharedCollection/accession", accessionModelRequest, List.class);
            if (null != linkedHashMapList && linkedHashMapList.get(0).get("message").toString().contains(ScsbConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE)) {
                responseEntity = new ResponseEntity<>(linkedHashMapList, getHttpHeaders(), HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity<>(linkedHashMapList, getHttpHeaders(), HttpStatus.OK);
            }
            stopWatch.stop();
            log.info("Total time taken for accession-->{}sec",stopWatch.getTotalTimeSeconds());
            return responseEntity;
        } catch (ResourceAccessException resourceAccessException){
            log.error(ScsbCommonConstants.LOG_ERROR, resourceAccessException);
            List<AccessionResponse> accessionResponseList=new ArrayList<>();
            AccessionResponse accessionResponse=new AccessionResponse();
            accessionResponse.setMessage(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE);
            accessionResponseList.add(accessionResponse);
            return new ResponseEntity<>(accessionResponseList, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            List<AccessionResponse> accessionResponseList = new ArrayList<>();
            AccessionResponse accessionResponse=new AccessionResponse();
            accessionResponse.setMessage(ScsbConstants.ACCESSION_INTERNAL_ERROR);
            accessionResponseList.add(accessionResponse);
            return new ResponseEntity<>(accessionResponseList, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * This method will call scsb-circ microservice to update the items which is already present in the scsb database and scsb solr.
     * @param inputRecords the input records
     * @return the response entity
     */
    @PostMapping(value = "/submitCollection")
    @Operation(summary = "submitCollection",
           description =  "Submit collection API is a REST service where users can provide MARC content in either SCSB XML or MARC XML formats and update the underlying record in SCSB. After the successful completion of the API, a report is sent.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity submitCollection(@Parameter(description = "Provide marc xml or scsb xml format to update the records", required = true, name = "inputRecords") @RequestBody String inputRecords,
                                           @Parameter(description = "Provide institution code", required = true, name = "institution") @RequestParam String institution,
    @Parameter(description = "Provide boolean value is cgd protected (true or false)", required = true, name = "isCGDProtected") @RequestParam Boolean isCGDProtected) {
        ResponseEntity responseEntity;
        try {
            MultiValueMap<String,Object> requestParameter = getLinkedMultiValueMap();
            requestParameter.add(ScsbCommonConstants.INPUT_RECORDS,inputRecords);
            requestParameter.add(ScsbCommonConstants.INSTITUTION,institution);
            requestParameter.add(ScsbCommonConstants.IS_CGD_PROTECTED,isCGDProtected);
            List<LinkedHashMap> linkedHashMapList =getRestTemplate().postForObject(getScsbCoreUrl() + "sharedCollection/submitCollection",requestParameter, List.class);
            String message = linkedHashMapList != null ? linkedHashMapList.get(0).get("message").toString(): ScsbConstants.SUBMIT_COLLECTION_INTERNAL_ERROR;
            if (message.equalsIgnoreCase(ScsbConstants.INVALID_MARC_XML_FORMAT_MESSAGE) || message.equalsIgnoreCase(ScsbConstants.INVALID_SCSB_XML_FORMAT_MESSAGE)
                    || message.equalsIgnoreCase(ScsbConstants.SUBMIT_COLLECTION_INTERNAL_ERROR)) {
                responseEntity = new ResponseEntity<>(linkedHashMapList, getHttpHeaders(), HttpStatus.BAD_REQUEST);
            } else {
                responseEntity = new ResponseEntity<>(linkedHashMapList, getHttpHeaders(), HttpStatus.OK);
            }
            return responseEntity;
        } catch (Exception exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            responseEntity = new ResponseEntity<>(ScsbConstants.SUBMIT_COLLECTION_INTERNAL_ERROR, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    /**
     * This method will call scsb-solr-client microservice to transfer the holdings/items which is already present in the scsb database and scsb solr.
     * @param transferRequest the input records
     * @return the response entity
     */
    @PostMapping(value = "/transferHoldingsAndItems")
    @Operation(summary = "transferHoldingsAndItems",
           description =  "TransferHoldingsAndItems API is a REST service where users can provide source and destination of " +
                    "the holdings and item for transfer")
    @ApiResponse(responseCode = "200", description = "OK")
    @ResponseBody
    public ResponseEntity transferHoldingsAndItems(
            @Parameter(description = "Source and destination of holdings and items in JSON format", required = true, name = "")
            @RequestBody TransferRequest transferRequest) {
        ResponseEntity responseEntity;
        try {
            TransferResponse transferResponse = getRestTemplate().postForObject(getScsbSolrClientUrl() + "transfer/processTransfer", transferRequest, TransferResponse.class);
            responseEntity = new ResponseEntity<>(transferResponse, getHttpHeaders(), HttpStatus.OK);
        } catch (RuntimeException exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            responseEntity = new ResponseEntity<>(ScsbConstants.TRANSFER_INTERNAL_ERROR, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
        return responseEntity;
    }
}
