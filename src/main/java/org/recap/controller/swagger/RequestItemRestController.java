package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.AbstractController;
import org.recap.model.AbstractResponseItem;
import org.recap.model.BulkRequestInformation;
import org.recap.model.BulkRequestResponse;
import org.recap.model.CancelRequestResponse;
import org.recap.model.ItemCheckInRequest;
import org.recap.model.ItemCheckOutRequest;
import org.recap.model.ItemCheckinResponse;
import org.recap.model.ItemCheckoutResponse;
import org.recap.model.ItemCreateBibRequest;
import org.recap.model.ItemCreateBibResponse;
import org.recap.model.ItemHoldCancelRequest;
import org.recap.model.ItemHoldRequest;
import org.recap.model.ItemHoldResponse;
import org.recap.model.ItemInformationRequest;
import org.recap.model.ItemInformationResponse;
import org.recap.model.ItemRecalRequest;
import org.recap.model.ItemRecallResponse;
import org.recap.model.ItemRefileRequest;
import org.recap.model.ItemRefileResponse;
import org.recap.model.ItemRequestInformation;
import org.recap.model.ItemResponseInformation;
import org.recap.model.PatronInformationRequest;
import org.recap.model.PatronInformationResponse;
import org.recap.model.ReplaceRequest;
import org.recap.service.RequestItemService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by hemalathas on 1/11/16.
 */
@Slf4j
@RestController
@RequestMapping("/requestItem")
@Api(value = "requestItem")
public class RequestItemRestController extends AbstractController  {



    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private RequestItemService requestItemService;

    /**
     * Gets producer.
     *
     * @return the producer
     */
    public ProducerTemplate getProducer() {
        return producer;
    }


    /**
     * Gets item request information.
     *
     * @return the item request information
     */
    public ItemRequestInformation getItemRequestInformation() {
        return new ItemRequestInformation();
    }

    /**
     * Gets item information request.
     *
     * @return the item information request
     */
    public ItemInformationRequest getItemInformationRequest() {
        return new ItemInformationRequest();
    }

    /**
     * Gets object mapper.
     *
     * @return the object mapper
     */
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return log;
    }

    /**
     * This method will call scsb-circ microservice to place a item request in scsb.
     * @param itemRequestInfo the item request info
     * @return the item response information
     */
    @PostMapping(value = ScsbConstants.REST_URL_REQUEST_ITEM)
    @ApiOperation(value = "Request Item", notes = "The Request item API allows the user to raise a request (retrieve / recall / EDD) in SCSB for a valid item.", nickname = "requestItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemResponseInformation itemRequest(@ApiParam(value = "Parameters to place a request on an Item", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        List<String> itemBarcodes;
        HttpStatus statusCode;
        boolean bSuccess;
        String screenMessage = null;
        ObjectMapper objectMapper;
        ResponseEntity responseEntity = null;
        try {
            log.info("Item Request Information : {}",itemRequestInfo);
            itemRequestInfo.setPatronBarcode(itemRequestInfo.getPatronBarcode() != null ? itemRequestInfo.getPatronBarcode().trim() : null);
            responseEntity = restTemplate.postForEntity(getScsbCircUrl() + ScsbConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class);
            statusCode = responseEntity.getStatusCode();
            screenMessage = responseEntity.getBody().toString();
            requestItemService.saveReceivedRequestInformation(itemRequestInfo,Boolean.TRUE);
        } catch (HttpClientErrorException e) {
            requestItemService.saveReceivedRequestInformation(itemRequestInfo,Boolean.TRUE);
            log.error("error::", e.getMessage());
            statusCode = e.getStatusCode();
            screenMessage = e.getResponseBodyAsString();
        } catch (RestClientException e){
            log.error("error::", e.getMessage());
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            requestItemService.saveReceivedRequestInformation(itemRequestInfo,Boolean.FALSE);
        } catch (Exception e){
            log.error("error::", e.getMessage());
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            requestItemService.saveReceivedRequestInformation(itemRequestInfo,Boolean.FALSE);
        }
        try {
            if (statusCode != null && statusCode == HttpStatus.OK) {
                objectMapper = new ObjectMapper();
                itemBarcodes = itemRequestInfo.getItemBarcodes();
                itemRequestInfo.setItemBarcodes(null);
                itemRequestInfo.setRequestNotes(StringUtils.left(itemRequestInfo.getRequestNotes(),1000));
                for (int i = 0; i < itemBarcodes.size(); i++) {
                    itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes.get(i).trim()));
                    String json = objectMapper.writeValueAsString(itemRequestInfo);
                    getProducer().sendBodyAndHeader(ScsbConstants.REQUEST_ITEM_QUEUE, json, ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                }
                bSuccess = true;
                screenMessage = ScsbCommonConstants.REQUEST_MESSAGE_RECEVIED;
            } else {
                bSuccess = false;
            }

            itemResponseInformation.setSuccess(bSuccess);
            itemResponseInformation.setScreenMessage(screenMessage);
            itemResponseInformation.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemResponseInformation.setTitleIdentifier(itemRequestInfo.getTitleIdentifier());
            itemResponseInformation.setDeliveryLocation(itemRequestInfo.getDeliveryLocation());
            itemResponseInformation.setEmailAddress(itemRequestInfo.getEmailAddress());
            itemResponseInformation.setPatronBarcode(itemRequestInfo.getPatronBarcode());
            itemResponseInformation.setRequestType(itemRequestInfo.getRequestType());
            itemResponseInformation.setRequestingInstitution(itemRequestInfo.getRequestingInstitution());
            log.info("Message In Queue");
        } catch (Exception e) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

    /**
     * This method will call scsb-circ microservice to validate item request information.
     * @param itemRequestInfo the item request info
     * @return the response entity
     */
    @PostMapping(value = ScsbConstants.REST_URL_VALIDATE_REQUEST_ITEM)
    @ApiOperation(value = "validateItemRequestInformations",
            notes = "The Validate item request API is an internal API call made by SCSB to validate the various parameters of the request item API call. This is to ensure only valid data is allowed to be processed even when the request comes through the request item API.", nickname = "validateItemRequestInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity validateItemRequest(@ApiParam(value = "Parameters to validate information prior to request", required = true, name = "requestItemJson") @RequestBody ItemRequestInformation itemRequestInfo) {
        ResponseEntity responseEntity ;
        String response = null;
        try {
            responseEntity = restTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInfo, String.class);
            response = (String) responseEntity.getBody();
        } catch (HttpClientErrorException httpEx) {
            log.error("error-->", httpEx);
            HttpStatus statusCode = httpEx.getStatusCode();
            String responseBodyAsString = httpEx.getResponseBodyAsString();
            return new ResponseEntity<>(responseBodyAsString, getHttpHeaders(), statusCode);
        } catch (Exception ex) {
            log.error("scsbCircUrl", ex);
            log.debug("scsbCircUrl : ".concat(getScsbCircUrl()));
            responseEntity = new ResponseEntity<>("Scsb circ Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
        responseEntity = new ResponseEntity<>(response, getHttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    /**
     * This method will call scsb-circ microservice to checkout an item request from ILS.
     * @param itemCheckOutRequest the item check out request
     * @return the item checkout response
     */
    @PostMapping(value = "/checkoutItem")
    @ApiOperation(value = "checkoutItem",
            notes = "The Check-out item API call is an internal call made by SCSB as part of the request API call.", nickname = "checkoutItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemCheckoutResponse checkoutItemRequest(@ApiParam(value = "Parameters for checking out an item", required = true, name = "requestItemJson") @RequestBody ItemCheckOutRequest itemCheckOutRequest) {
        ItemCheckoutResponse itemCheckoutResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());
            ResponseEntity responseEntity = restTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemCheckoutResponse = om.readValue(response, ItemCheckoutResponse.class);
        } catch (RestClientException ex) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
            itemCheckoutResponse = new ItemCheckoutResponse();
            itemCheckoutResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
            itemCheckoutResponse = new ItemCheckoutResponse();
            itemCheckoutResponse.setScreenMessage(ex.getMessage());
        }
        return itemCheckoutResponse;
    }

    /**
     * This method will call scsb-circ microservice to send a checkin an item into ILS.
     * @param itemCheckInRequest the item check in request
     * @return the abstract response item
     */
    @PostMapping(value = "/checkinItem")
    @ApiOperation(value = "checkinItem",
            notes = "The Check-in item API is an internal call made by SCSB as part of the refile and accession API calls.", nickname = "checkinItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem checkinItemRequest(@ApiParam(value = "Parameters for checking in an item", required = true, name = "requestItemJson") @RequestBody ItemCheckInRequest itemCheckInRequest) {
        ItemCheckinResponse itemCheckinResponse = null;
        ResponseEntity responseEntity = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = null;
        try {
            itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
            itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());
            responseEntity = restTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkinItem", itemRequestInfo, String.class);
            response = (String) responseEntity.getBody();
            ObjectMapper om = getObjectMapper();
            itemCheckinResponse = om.readValue(response, ItemCheckinResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
        }
        return itemCheckinResponse;
    }

    /**
     * This method will call scsb-circ microservice to place a hold on the item in ILS.
     * @param itemHoldRequest the item hold request
     * @return the abstract response item
     */
    @PostMapping(value = "/holdItem")
    @ApiOperation(value = "holdItem",
            notes = "The Hold item API call is an internal call made by SCSB to the partner's ILS to place a hold request as part of the request API workflow.", nickname = "holdItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem holdItemRequest(@ApiParam(value = "Parameters for placing a hold on the item in the ILS", required = true, name = "requestItemJson") @RequestBody ItemHoldRequest itemHoldRequest) {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setItemBarcodes(itemHoldRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemHoldRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemHoldRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldRequest.getPickupLocation());
            itemRequestInfo.setTrackingId(itemHoldRequest.getTrackingId());
            itemRequestInfo.setTitleIdentifier(itemHoldRequest.getTitle());
            itemRequestInfo.setAuthor(itemHoldRequest.getAuthor());
            itemRequestInfo.setCallNumber(itemHoldRequest.getCallNumber());
            response = getResponse(itemRequestInfo, ScsbConstants.URL_REQUEST_ITEM_HOLD);
            ObjectMapper om = getObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
            log.error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION_REST +"%s", ex.getMessage()));
            itemHoldResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
            log.error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION +"%s", ex.getMessage()));
            itemHoldResponse.setScreenMessage(ex.getMessage());
        }
        return itemHoldResponse;
    }

    /**
     * This method will call scsb-circ microservice to cancel a hold on the item in ILS.
     * @param itemHoldCancelRequest the item hold cancel request
     * @return the abstract response item
     */
    @PostMapping(value = "/cancelHoldItem")
    @ApiOperation(value = "cancelHoldItem",
            notes = "This internal call cancels a hold request in the partner ILS as part of the Cancel Request API.", nickname = "cancelHoldItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem cancelHoldItemRequest(@ApiParam(value = "Parameters for canceling a hold on the Item", required = true, name = "requestItemJson") @RequestBody ItemHoldCancelRequest itemHoldCancelRequest) {
        ItemHoldResponse itemHoldResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response = "";
        try {
            itemRequestInfo.setItemBarcodes(itemHoldCancelRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemHoldCancelRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemHoldCancelRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemHoldCancelRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemHoldCancelRequest.getPickupLocation());
            itemRequestInfo.setTrackingId(itemHoldCancelRequest.getTrackingId());

            ResponseEntity responseEntity = restTemplate.postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", itemRequestInfo, String.class);
            response = responseEntity.getBody().toString();
            ObjectMapper om = getObjectMapper();
            itemHoldResponse = om.readValue(response, ItemHoldResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
            getLogger().error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION_REST +"%s", ex.getMessage()));
            itemHoldResponse =new ItemHoldResponse();
            itemHoldResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
            getLogger().error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION +"%s", ex.getMessage()));
            itemHoldResponse = new ItemHoldResponse();
            itemHoldResponse.setScreenMessage(ex.getMessage());
        }
        return itemHoldResponse;
    }

    /**
     * This method will call scsb-circ microservice to create a bibliographic record in ILS.
     *
     * @param itemCreateBibRequest the item create bib request
     * @return the abstract response item
     */
    @PostMapping(value = "/createBib")
    @ApiOperation(value = "createBib",
            notes = "The Create bibliographic record API is an internal call made by SCSB to partner ILS as part of the request API for cross partner borrowing. Usually when an item owned by another partner is requesting, the requesting institution will not have the metadata of the item that is being requested. In order to place the hold for the patron against the item, the Create bib record API creates a temporary record against which the hold can be placed and subsequent charge and discharge processes can be done.", nickname = "createBib")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem createBibRequest(@ApiParam(value = "Parameters for creating a temporary bibliographic record in the ILS", required = true, name = "requestItemJson") @RequestBody ItemCreateBibRequest itemCreateBibRequest) {
        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response;

        try {
            itemRequestInfo.setItemBarcodes(itemCreateBibRequest.getItemBarcodes());
            itemRequestInfo.setPatronBarcode(itemCreateBibRequest.getPatronIdentifier());
            itemRequestInfo.setItemOwningInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemCreateBibRequest.getItemOwningInstitution());
            itemRequestInfo.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());
            response = getResponse(itemRequestInfo, ScsbConstants.URL_REQUEST_ITEM_CREATEBIB);
            ObjectMapper om = getObjectMapper();
            itemCreateBibResponse = om.readValue(response, ItemCreateBibResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
            getLogger().error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION_REST +"%s", ex.getMessage()));
            itemCreateBibResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
            getLogger().error(String.format(ScsbCommonConstants.REQUEST_EXCEPTION +"%s", ex.getMessage()));
            itemCreateBibResponse.setScreenMessage(ex.getMessage());
        }
        return itemCreateBibResponse;
    }

    /**
     * This method will call scsb-circ microservice retrieve item information and circulation status from ILS.
     *
     * @param itemRequestInfo the item request info
     * @return the abstract response item
     */
    @PostMapping(value = "/itemInformation")
    @ApiOperation(value = "itemInformation", notes = "The Item information API call is made internally by SCSB as part of the request API call.", nickname = "itemInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem itemInformation(@ApiParam(value = "Parameters to retrieve the item information from the ILS", required = true, name = "requestItemJson") @RequestBody ItemInformationRequest itemRequestInfo) {
        HttpEntity<ItemInformationResponse> responseEntity;
        ItemInformationResponse itemInformationResponse;
        ItemInformationRequest itemInformationRequest = getItemInformationRequest();
        try {
            itemInformationRequest.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemInformationRequest.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
            HttpEntity request = new HttpEntity<>(itemInformationRequest);
            responseEntity = restTemplate.exchange(getScsbCircUrl() + ScsbConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class);
            itemInformationResponse = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error("RestClient : ", ex);
            itemInformationResponse = new ItemInformationResponse();
            itemInformationResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.LOG_ERROR, ex);
            itemInformationResponse = new ItemInformationResponse();
            itemInformationResponse.setScreenMessage(ex.getMessage());
        }
        return itemInformationResponse;
    }

    /**
     * This method will call scsb-circ microservice to recall an already retrieved item in ILS.
     *
     * @param itemRecalRequest the item recal request
     * @return the abstract response item
     */
    @PostMapping(value = "/recall")
    @ApiOperation(value = "recall",
            notes = "The Recall API is used internally by SCSB during request API calls with request type RECALL.", nickname = "RecallItem")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem recallItem(@ApiParam(value = "Parameters to recall an item", required = true, name = "requestItemJson") @RequestBody ItemRecalRequest itemRecalRequest) {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        String response;
        try {
            itemRequestInfo.setItemBarcodes(itemRecalRequest.getItemBarcodes());
            itemRequestInfo.setItemOwningInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setRequestingInstitution(itemRecalRequest.getItemOwningInstitution());
            itemRequestInfo.setPatronBarcode(itemRecalRequest.getPatronIdentifier());
            itemRequestInfo.setBibId(itemRecalRequest.getBibId());
            itemRequestInfo.setDeliveryLocation(itemRecalRequest.getPickupLocation());
            response = getResponse(itemRequestInfo, ScsbConstants.URL_REQUEST_ITEM_RECALL);
            ObjectMapper om = getObjectMapper();
            itemRecallResponse = om.readValue(response, ItemRecallResponse.class);
        } catch (RestClientException ex) {
            getLogger().error(ScsbConstants.LOG_ERROR_REST_CLIENT, ex);
            getLogger().error(String.format(ScsbConstants.LOG_ERROR_REST_CLIENT +"%s", ex.getMessage()));
            itemRecallResponse.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.LOG_ERROR, ex);
            getLogger().error(String.format(ScsbCommonConstants.LOG_ERROR +"%s", ex.getMessage()));
            itemRecallResponse.setScreenMessage(ex.getMessage());
        }
        return itemRecallResponse;
    }

    /**
     * This method will call scsb-circ microservice to retrieve patron information from ILS.
     *
     * @param patronInformationRequest the patron information request
     * @return the patron information response
     */
    @PostMapping(value = "/patronInformation")
    @ApiOperation(value = "patronInformation", notes = "The Patron information API is used internally by SCSB as part of the request API.", nickname = "patronInformation")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public PatronInformationResponse patronInformation(@ApiParam(value = "Parameters to retrieve the patron information from the ILS", required = true, name = "requestpatron") @RequestBody PatronInformationRequest patronInformationRequest) {
        HttpEntity<PatronInformationResponse> responseEntity;
        PatronInformationResponse patronInformation = null;
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();

        try {
            itemRequestInformation.setPatronBarcode(patronInformationRequest.getPatronIdentifier());
            itemRequestInformation.setItemOwningInstitution(patronInformationRequest.getItemOwningInstitution());
            HttpEntity request = new HttpEntity<>(itemRequestInformation);
            responseEntity = restTemplate.exchange(getScsbCircUrl() + ScsbConstants.URL_REQUEST_PATRON_INFORMATION, HttpMethod.POST, request, PatronInformationResponse.class);
            patronInformation = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error(ScsbConstants.LOG_ERROR_REST_CLIENT, ex);
            patronInformation = new PatronInformationResponse();
            patronInformation.setScreenMessage(ex.getMessage());
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.LOG_ERROR, ex);
            patronInformation = new PatronInformationResponse();
            patronInformation.setScreenMessage(ex.getMessage());
        }
        return patronInformation;
    }

    /**
     *This method will call scsb-circ microservice to refile an item back into scsb database and mark the item as available.
     *
     * @param itemRefileRequest the item refile request
     * @return the item refile response
     */
    @PostMapping(value = "/refile")
    @ApiOperation(value = "refile", notes = "The Refile item API is called when IMS Depository staff refile the item into LAS, and LAS will call SCSB with the details of the refile.", nickname = "Re-File")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ItemRefileResponse refileItem(@ApiParam(value = "Parameters to refile an Item", required = true, name = "itemBarcode") @RequestBody ItemRefileRequest itemRefileRequest) {
        log.info("Refile Request Received");
        ItemRefileResponse itemRefileResponse;
        HttpEntity<ItemRefileResponse> responseEntity;
        HttpEntity request = new HttpEntity<>(itemRefileRequest);
        log.info("Refile request received for the barcodes : {} where request id's are : {}",itemRefileRequest.getItemBarcodes(),itemRefileRequest.getRequestIds());
        responseEntity = restTemplate.exchange(getScsbCircUrl() + ScsbConstants.URL_REQUEST_RE_FILE, HttpMethod.POST, request, ItemRefileResponse.class);
        itemRefileResponse = responseEntity.getBody();
        log.info("Item Refile Response : {}",itemRefileResponse.getScreenMessage());
        return itemRefileResponse;
    }

    /**
     * This method will call scsb-circ microservice to cancel the request from scsb.
     *
     * @param requestId the request id
     * @return the cancel request response
     */
    @PostMapping(value = "/cancelRequest")
    @ApiOperation(value = "cancelRequest", notes = "The Cancel Request API will be used by both partners and IMS Depository users to cancel a request placed through SCSB. Partners will incorporate the API into their discovery systems to provide the patrons a way to cancel requests that have been raised by them. IMS Depository users would use it through the SCSB UI to cancel requests that are difficult to process.", nickname = "cancelRequest")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public CancelRequestResponse cancelRequest(@ApiParam(value = "Parameters for canceling a request on the Item", required = true, name = "requestId") @RequestParam Integer requestId) {
        CancelRequestResponse cancelRequestResponse;
        HttpEntity request = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbCircUrl() + ScsbConstants.URL_REQUEST_CANCEL).queryParam("requestId", requestId);
        HttpEntity<CancelRequestResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, request, CancelRequestResponse.class);
        cancelRequestResponse = responseEntity.getBody();
        return cancelRequestResponse;
    }

    /**
     * This method will place bulk request message in to the queue to initiate the process.
     * @param bulkRequestId
     * @return
     */
    @PostMapping(value = "/bulkRequest", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "bulkRequest", notes = "The Bulk Request API is internally called by SCSB UI which will be probably initiated by LAS users.", nickname = "bulkRequest")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public BulkRequestResponse bulkRequest(@ApiParam(value = "Parameters for initiating bulk request", required = true, name = "bulkRequestId") @RequestParam int bulkRequestId) {
        getProducer().sendBody(ScsbCommonConstants.BULK_REQUEST_ITEM_QUEUE, bulkRequestId);
        BulkRequestResponse bulkRequestResponse = new BulkRequestResponse();
        bulkRequestResponse.setBulkRequestId(bulkRequestId);
        bulkRequestResponse.setSuccess(true);
        bulkRequestResponse.setScreenMessage(ScsbConstants.BULK_REQUEST_MESSAGE_RECEIVED);
        return bulkRequestResponse;
    }

    /**
     * This method validates the patron information by calling an api in scsb-circ micro service.
     * @param bulkRequestInformation
     * @return
     */
    @ApiIgnore
    @PostMapping(value = "/patronValidationBulkRequest")
    public Boolean patronValidation(@RequestBody BulkRequestInformation bulkRequestInformation){
        return new RestTemplate().postForEntity(getScsbCircUrl() + "/requestItem/patronValidationBulkRequest", bulkRequestInformation, Boolean.class).getBody();
    }

    /**
     * This method refiles the item in ILS.
     * @param itemBarcode
     * @param owningInstitution
     * @return
     */
    @ApiIgnore
    @PostMapping(value = "/refileItemInILS", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "refileItemInILS",
            notes = "The Refile item API is an internal call made by SCSB as part of the refile and accession API calls.", nickname = "refileItemInILS")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public AbstractResponseItem refileItemInILS(@ApiParam(value = "Parameters for refiling an item", required = true, name = "itemBarcode") @RequestParam String itemBarcode,
                                                @ApiParam(value = "Parameters for refiling an item", required = true, name = "owningInstitution") @RequestParam String owningInstitution) {
        ItemRefileResponse itemRefileResponse = null;
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        try {
            itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcode));
            itemRequestInfo.setItemOwningInstitution(owningInstitution);
            ResponseEntity<ItemRefileResponse> responseEntity = restTemplate.postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", itemRequestInfo, ItemRefileResponse.class);
            itemRefileResponse = responseEntity.getBody();
        } catch (RestClientException ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION_REST, ex);
        } catch (Exception ex) {
            getLogger().error(ScsbCommonConstants.REQUEST_EXCEPTION, ex);
        }
        return itemRefileResponse;
    }

    /**
     * This method will replace the requests to LAS queue.
     *
     * @param replaceRequest the replace request body
     * @return the string response
     */
    @ApiIgnore
    @PostMapping(value = "/replaceRequest")
    @ApiOperation(value = "replaceRequest", notes = "Resubmit the failed requests to LAS", nickname = "Replace Request")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @ResponseBody
    public ResponseEntity replaceRequestToLAS(@ApiParam(value = "Parameters to replace the request", required = true, name = "replaceRequest") @RequestBody ReplaceRequest replaceRequest) {
        try {
            Map<String, String> resultMap = restTemplate.postForObject(getScsbCircUrl() + ScsbConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class);
            return new ResponseEntity<>(resultMap, getHttpHeaders(), HttpStatus.OK);
        } catch (RuntimeException ex) {
            log.error(ScsbCommonConstants.LOG_ERROR, ex);
            return new ResponseEntity<>(ScsbConstants.SCSB_CIRC_SERVICE_UNAVAILABLE, getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private String getResponse(ItemRequestInformation itemRequestInfo, String urlConstant)
    {
        ResponseEntity responseEntity = restTemplate.postForEntity(getScsbCircUrl() + urlConstant,
                itemRequestInfo, String.class);
        return responseEntity.getBody().toString();
    }

    public ItemResponseInformation itemSubmitRequest(ItemRequestInformation itemRequestInfo,Integer requestLogId) {
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        List<String> itemBarcodes;
        HttpStatus statusCode;
        boolean bSuccess;
        String screenMessage = null;
        ObjectMapper objectMapper;
        ResponseEntity responseEntity = null;
        try {
            log.info("Resubmit Item Request Information : {}",itemRequestInfo);
            itemRequestInfo.setPatronBarcode(itemRequestInfo.getPatronBarcode() != null ? itemRequestInfo.getPatronBarcode().trim() : null);
            responseEntity = restTemplate.postForEntity(getScsbCircUrl() + ScsbConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class);
            statusCode = responseEntity.getStatusCode();
            screenMessage = responseEntity.getBody().toString();
            requestItemService.updateItemRequest(requestLogId);
        } catch (HttpClientErrorException e) {
            log.error("error::", e);
            statusCode = e.getStatusCode();
            screenMessage = e.getResponseBodyAsString();
            requestItemService.updateItemRequest(requestLogId);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        } catch (RestClientException e){
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.toString());
        } catch (Exception e){
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            throw new RestClientException(HttpStatus.SERVICE_UNAVAILABLE.toString());
        }
        try {
            if (statusCode != null && statusCode == HttpStatus.OK) {
                objectMapper = new ObjectMapper();
                itemBarcodes = itemRequestInfo.getItemBarcodes();
                itemRequestInfo.setItemBarcodes(null);
                itemRequestInfo.setRequestNotes(StringUtils.left(itemRequestInfo.getRequestNotes(),1000));
                for (int i = 0; i < itemBarcodes.size(); i++) {
                    itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes.get(i).trim()));
                    String json = objectMapper.writeValueAsString(itemRequestInfo);
                    getProducer().sendBodyAndHeader(ScsbConstants.REQUEST_ITEM_QUEUE, json, ScsbCommonConstants.REQUEST_TYPE_QUEUE_HEADER, itemRequestInfo.getRequestType());
                }
                bSuccess = true;
                screenMessage = ScsbCommonConstants.REQUEST_MESSAGE_RECEVIED;
            } else {
                bSuccess = false;
            }

            itemResponseInformation.setSuccess(bSuccess);
            itemResponseInformation.setScreenMessage(screenMessage);
            itemResponseInformation.setItemBarcodes(itemRequestInfo.getItemBarcodes());
            itemResponseInformation.setTitleIdentifier(itemRequestInfo.getTitleIdentifier());
            itemResponseInformation.setDeliveryLocation(itemRequestInfo.getDeliveryLocation());
            itemResponseInformation.setEmailAddress(itemRequestInfo.getEmailAddress());
            itemResponseInformation.setPatronBarcode(itemRequestInfo.getPatronBarcode());
            itemResponseInformation.setRequestType(itemRequestInfo.getRequestType());
            itemResponseInformation.setRequestingInstitution(itemRequestInfo.getRequestingInstitution());
            log.info("Message In Queue");
        } catch (Exception e) {
            log.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemResponseInformation;
    }

}
