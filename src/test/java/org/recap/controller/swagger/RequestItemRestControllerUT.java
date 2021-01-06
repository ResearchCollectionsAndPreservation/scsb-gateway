package org.recap.controller.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.AbstractResponseItem;
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
import org.recap.service.RestHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by hemalathas on 4/11/16.
 */
public class RequestItemRestControllerUT extends BaseTestCase{
    private static final Logger logger = LoggerFactory.getLogger(RequestItemRestControllerUT.class);

    @Mock
    private RequestItemRestController requestItemRestController;

    @Autowired
    private RequestItemRestController getRequestItemRestController;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producer;

    @Mock
    private ProducerTemplate producerTemplate;

    @Value("${scsb.circ.url}")
    String scsbCircUrl;

    @Mock
    private ConsumerTemplate consumer;

    @Mock
    private RestTemplate mockRestTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private RestHeaderService restHeaderService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public void setScsbCircUrl(String scsbCircUrl) {
        this.scsbCircUrl = scsbCircUrl;
    }

    private static final Logger mocklogger = LoggerFactory.getLogger(RequestItemRestController.class);


    @Test
    public void testValidRequest() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.VALID_REQUEST, HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101047717911"));
        itemRequestInformation.setTitleIdentifier("105 paintings in the John Herron Art Museum.");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("CUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setStartPage("I");
        itemRequestInformation.setEndPage("XII");
        assertFalse(itemRequestInformation.isOwningInstitutionItem());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(RecapCommonConstants.VALID_REQUEST,responseEntity1.getBody());
    }
    @Test
    public void testValidRequest_HttpClientErrorException() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101047717911"));
        itemRequestInformation.setTitleIdentifier("105 paintings in the John Herron Art Museum.");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setStartPage("I");
        itemRequestInformation.setEndPage("XII");
        assertTrue(itemRequestInformation.isOwningInstitutionItem());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity1.getStatusCode());
   }

    @Test
    public void testValidRequest_RestClientException() throws JSONException {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101047717911"));
        itemRequestInformation.setTitleIdentifier("105 paintings in the John Herron Art Museum.");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("CUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setStartPage("I");
        itemRequestInformation.setEndPage("XII");

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenThrow(new RestClientException(""));
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals("Scsb circ Service is Unavailable.",responseEntity1.getBody());

    }

    @Test
    public void testRequestWithInvalidRequestingInst() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.INVALID_REQUEST_INSTITUTION+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setItemOwningInstitution("CUL");
        itemRequestInformation.setRequestingInstitution("PULd");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals( RecapConstants.INVALID_REQUEST_INSTITUTION+"\n",responseEntity1.getBody());
    }

    @Test
    public void testRequestParameterWithInvalidPatronBarcode() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity("Patron barcode not found", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("g75dfgsf");
        itemRequestInformation.setRequestType("Borrow Direct");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals("Patron barcode not found",responseEntity1.getBody());
    }

    @Test
    public void testRequestParameterWithEDDRequestType() throws JSONException {
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.validateItemRequest(itemRequestInformation)).thenCallRealMethod();
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals( RecapConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n",responseEntity1.getBody());
    }

    @Test
    public void testCancelRequest(){
        HttpEntity request = new HttpEntity<>(restHeaderService.getHttpHeaders());
        CancelRequestResponse cancelRequestResponse = new CancelRequestResponse();
        cancelRequestResponse.setScreenMessage("Request cancelled.");
        cancelRequestResponse.setSuccess(true);
        ResponseEntity<CancelRequestResponse> responseEntity = new ResponseEntity<CancelRequestResponse>(cancelRequestResponse,HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbCircUrl() + RecapConstants.URL_REQUEST_CANCEL).queryParam("requestId", 129);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestHeaderService()).thenReturn(restHeaderService);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(builder.build().encode().toUri(), org.springframework.http.HttpMethod.POST, request, CancelRequestResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.cancelRequest(129)).thenCallRealMethod();
        cancelRequestResponse = requestItemRestController.cancelRequest(129);
        assertNotNull(cancelRequestResponse);
        assertEquals("Request cancelled.",cancelRequestResponse.getScreenMessage());
        assertEquals(true,cancelRequestResponse.isSuccess());
    }
    @Test
    public void testbulkRequest(){
        Integer bulkRequestId = 1;
        BulkRequestResponse bulkRequestResponse;
        Mockito.when(requestItemRestController.getProducer()).thenReturn(producerTemplate);
        Mockito.when(requestItemRestController.bulkRequest(bulkRequestId)).thenCallRealMethod();
        bulkRequestResponse = requestItemRestController.bulkRequest(bulkRequestId);
        assertNotNull(bulkRequestResponse);
    }

    @Test
    public void testrefileItemInILS(){
        String itemBarcode = "32101104657091";
        String owningInstitution = "PUL";
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        ResponseEntity<ItemRefileResponse> responseEntity = new ResponseEntity<ItemRefileResponse>(itemRefileResponse,HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", itemRequestInfo, ItemRefileResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.refileItemInILS(itemBarcode,owningInstitution)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS(itemBarcode,owningInstitution);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testrefileItemInILS_RestClientException(){
        String itemBarcode = "32101104657091";
        String owningInstitution = "PUL";
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        ResponseEntity<ItemRefileResponse> responseEntity = new ResponseEntity<ItemRefileResponse>(itemRefileResponse,HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", itemRequestInfo, ItemRefileResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.refileItemInILS(itemBarcode,owningInstitution)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS(itemBarcode,owningInstitution);
        assertNull(abstractResponseItem);
    }

    @Test
    public void testrefileItemInILS_Exception(){
        String itemBarcode = "32101104657091";
        String owningInstitution = "PUL";
         Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.refileItemInILS(itemBarcode,owningInstitution)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS(itemBarcode,owningInstitution);
        assertNull(abstractResponseItem);
    }

    @Test
    public void replaceRequestToLAS(){
        ReplaceRequest replaceRequest = new ReplaceRequest();
        Map<String, String> responseEntity = new HashMap<>();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().postForObject(getScsbCircUrl() + RecapConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class)).thenReturn(new HashMap());
        Mockito.when(requestItemRestController.replaceRequestToLAS(replaceRequest)).thenCallRealMethod();
       ResponseEntity ResponseEntity  = requestItemRestController.replaceRequestToLAS(replaceRequest);
        assertNotNull(ResponseEntity);
    }

    @Test
    public void replaceRequestToLAS_Exception(){
        ReplaceRequest replaceRequest = null;
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Map<String, String> responseEntity = new HashMap<>();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().postForObject(getScsbCircUrl() + RecapConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.replaceRequestToLAS(replaceRequest)).thenCallRealMethod();
        ResponseEntity ResponseEntity  = requestItemRestController.replaceRequestToLAS(replaceRequest);
        assertNotNull(ResponseEntity);
    }




    @Test
    public void testcheckinItemRequest() throws IOException {

        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setPatronIdentifier("45678915");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckInRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckinResponse.class)).thenReturn(getItemCheckInResponse());
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/checkinItem", itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.checkinItemRequest(itemCheckInRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.getItemBarcode().contains("123"));
    }
    @Test
    public void testcheckinItemRequest_RestClientException() throws IOException {

        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setPatronIdentifier("45678915");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckInRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckinResponse.class)).thenReturn(getItemCheckInResponse());
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/checkinItem", itemRequestInfo, String.class)).thenThrow(new RestClientException("Exception occured"));;
        Mockito.when(requestItemRestController.checkinItemRequest(itemCheckInRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNull(abstractResponseItem);
     }

    @Test
    public void testcheckinItemRequest_Exception() throws IOException {

        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setPatronIdentifier("45678915");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckInRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckInRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckInRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckInRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckInRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckinResponse.class)).thenReturn(getItemCheckInResponse());
        Mockito.when(requestItemRestController.checkinItemRequest(itemCheckInRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNull(abstractResponseItem);
    }

    @Test
    public void testItemInformationPUL(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("PUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }
    @Test
    public void testItemInformationPUL_RestClientException(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("PUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }
    @Test
    public void testItemInformationPUL_Exception(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("PUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemInformationRequest()).thenReturn(null);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testItemInformationCUL(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution("CUL");
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution("PUL");
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    @Test
    public void testcancelHoldItemRequest() throws IOException {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemHoldCancelRequest itemHoldCancelRequest = new ItemHoldCancelRequest();
        itemHoldCancelRequest.setItemBarcodes(Arrays.asList("3216549874100225"));
        itemHoldCancelRequest.setItemOwningInstitution("PUL");
        itemHoldCancelRequest.setPickupLocation("PB");
        itemHoldCancelRequest.setBibId("3256");
        itemHoldCancelRequest.setPatronIdentifier("456328965");
        itemHoldCancelRequest.setTrackingId("2");

        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity("test",HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemHoldResponse.class)).thenReturn(itemHoldResponse);
        Mockito.when(requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);

    }
    @Test
    public void testcancelHoldItemRequest_RestClientException() throws IOException {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemHoldCancelRequest itemHoldCancelRequest = new ItemHoldCancelRequest();
        itemHoldCancelRequest.setItemBarcodes(Arrays.asList("3216549874100225"));
        itemHoldCancelRequest.setItemOwningInstitution("PUL");
        itemHoldCancelRequest.setPickupLocation("PB");
        itemHoldCancelRequest.setBibId("3256");
        itemHoldCancelRequest.setPatronIdentifier("456328965");
        itemHoldCancelRequest.setTrackingId("2");

        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity("test",HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", itemRequestInfo, String.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemHoldResponse.class)).thenReturn(itemHoldResponse);
        Mockito.when(requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());

    }
    @Test
    public void testcancelHoldItemRequest_Exception() throws IOException {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemHoldCancelRequest itemHoldCancelRequest = new ItemHoldCancelRequest();
        itemHoldCancelRequest.setItemBarcodes(Arrays.asList("3216549874100225"));
        itemHoldCancelRequest.setItemOwningInstitution("PUL");
        itemHoldCancelRequest.setPickupLocation("PB");
        itemHoldCancelRequest.setBibId("3256");
        itemHoldCancelRequest.setPatronIdentifier("456328965");
        itemHoldCancelRequest.setTrackingId("2");

        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity("test",HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemHoldResponse.class)).thenReturn(itemHoldResponse);
        Mockito.when(requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testItemRefile(){
        ItemRefileRequest itemRefileRequest = new ItemRefileRequest();
        itemRefileRequest.setItemBarcodes(Arrays.asList("123"));
        itemRefileRequest.setRequestIds(Arrays.asList(1));

        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        itemRefileResponse.setScreenMessage("Successfully Refiled");
        itemRefileResponse.setSuccess(true);

        ResponseEntity<ItemRefileResponse> responseEntity = new ResponseEntity<ItemRefileResponse>(itemRefileResponse,HttpStatus.OK);

        HttpEntity request = new HttpEntity(itemRefileRequest);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_RE_FILE, org.springframework.http.HttpMethod.POST, request, ItemRefileResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.refileItem(itemRefileRequest)).thenCallRealMethod();
        ItemRefileResponse itemRefileResponse1 = requestItemRestController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse1);
        assertNotNull(itemRefileRequest.getItemBarcodes());
        assertNotNull(itemRefileRequest.getRequestIds());
        assertNotNull(itemRefileResponse.getScreenMessage());
        assertNotNull(itemRefileResponse.isSuccess());
    }

    @Test
    public void testCheckoutItemRequest() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckOutRequest itemCheckOutRequest = new ItemCheckOutRequest();
        itemCheckOutRequest.setPatronIdentifier("45678915");
        itemCheckOutRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckOutRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckoutResponse.class)).thenReturn(getItemCheckoutResponse());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = requestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);

    }

    @Test
    public void testCheckoutItemRequest_RestClientException() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckOutRequest itemCheckOutRequest = new ItemCheckOutRequest();
        itemCheckOutRequest.setPatronIdentifier("45678915");
        itemCheckOutRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckOutRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCheckoutResponse.class)).thenThrow(new RestClientException(RecapCommonConstants.REQUEST_EXCEPTION_REST));
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = requestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);
        assertEquals(RecapCommonConstants.REQUEST_EXCEPTION_REST,itemCheckoutResponse.getScreenMessage());
    }
    @Test
    public void testCheckoutItemRequest_Exception() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.PRINCETON, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemCheckOutRequest itemCheckOutRequest = new ItemCheckOutRequest();
        itemCheckOutRequest.setPatronIdentifier("45678915");
        itemCheckOutRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckOutRequest.setItemOwningInstitution("PUL");

        itemRequestInfo.setPatronBarcode(itemCheckOutRequest.getPatronIdentifier());
        itemRequestInfo.setItemBarcodes(itemCheckOutRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemCheckOutRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemCheckOutRequest.getItemOwningInstitution());

        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = requestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);
    }

    @Test
    public void testHoldRequestItem() throws IOException {
        ItemHoldRequest itemHoldRequest = new ItemHoldRequest();
        itemHoldRequest.setItemOwningInstitution("PUL");
        itemHoldRequest.setTrackingId("564");
        itemHoldRequest.setCallNumber("5645");
        itemHoldRequest.setItemBarcodes(Arrays.asList("32101047717911"));
        itemHoldRequest.setPatronIdentifier("42659872");
        itemHoldRequest.setBibId("14533");
        itemHoldRequest.setAuthor("John");
        itemHoldRequest.setPickupLocation("PB");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldRequest.setCallNumber("X");
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseResponseEntity = new ResponseEntity<ItemHoldResponse>(itemHoldResponse,HttpStatus.OK);
        String response = responseResponseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemHoldResponse.class)).thenReturn(itemHoldResponse);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_HOLD, itemRequestInfo, String.class)).thenReturn(responseResponseEntity);
        Mockito.when(requestItemRestController.holdItemRequest(itemHoldRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.holdItemRequest(itemHoldRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }
    @Test
    public void testHoldRequestItem_RestClientException() throws IOException {
        ItemHoldRequest itemHoldRequest = new ItemHoldRequest();
        itemHoldRequest.setItemOwningInstitution("PUL");
        itemHoldRequest.setTrackingId("564");
        itemHoldRequest.setCallNumber("5645");
        itemHoldRequest.setItemBarcodes(Arrays.asList("32101047717911"));
        itemHoldRequest.setPatronIdentifier("42659872");
        itemHoldRequest.setBibId("14533");
        itemHoldRequest.setAuthor("John");
        itemHoldRequest.setPickupLocation("PB");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldRequest.setCallNumber("X");
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseResponseEntity = new ResponseEntity<ItemHoldResponse>(itemHoldResponse,HttpStatus.OK);
        String response = responseResponseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_HOLD, itemRequestInfo, String.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.holdItemRequest(itemHoldRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.holdItemRequest(itemHoldRequest);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testHoldRequestItem_Exception() throws IOException {
        ItemHoldRequest itemHoldRequest = new ItemHoldRequest();
        itemHoldRequest.setItemOwningInstitution("PUL");
        itemHoldRequest.setTrackingId("564");
        itemHoldRequest.setCallNumber("5645");
        itemHoldRequest.setItemBarcodes(Arrays.asList("32101047717911"));
        itemHoldRequest.setPatronIdentifier("42659872");
        itemHoldRequest.setBibId("14533");
        itemHoldRequest.setAuthor("John");
        itemHoldRequest.setPickupLocation("PB");
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldRequest.setCallNumber("X");
        itemHoldResponse.setInstitutionID("PUL");
        itemHoldResponse.setSuccess(true);
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseResponseEntity = new ResponseEntity<ItemHoldResponse>(itemHoldResponse,HttpStatus.OK);
        String response = responseResponseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.holdItemRequest(itemHoldRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.holdItemRequest(itemHoldRequest);
        assertNull(abstractResponseItem.getScreenMessage());
    }

    @Test
    public void testCreateBibRequest() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = new ItemCreateBibRequest();
        itemCreateBibRequest.setPatronIdentifier("4568723");
        itemCreateBibRequest.setTitleIdentifier("test");
        itemCreateBibRequest.setItemBarcodes(Arrays.asList("4564"));
        itemCreateBibRequest.setItemOwningInstitution("PUL");

        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setBibId("45564");
        itemCreateBibResponse.setItemId("5886");
        itemCreateBibResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemCreateBibResponse>(itemCreateBibResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCreateBibResponse.class)).thenReturn(itemCreateBibResponse);
        Mockito.when(requestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
        assertNotNull(itemCreateBibResponse.getBibId());
        assertNotNull(itemCreateBibResponse.getItemId());

    }
    @Test
    public void testCreateBibRequest_RestClientException() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = new ItemCreateBibRequest();
        itemCreateBibRequest.setPatronIdentifier("4568723");
        itemCreateBibRequest.setTitleIdentifier("test");
        itemCreateBibRequest.setItemBarcodes(Arrays.asList("4564"));
        itemCreateBibRequest.setItemOwningInstitution("PUL");

        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setBibId("45564");
        itemCreateBibResponse.setItemId("5886");
        itemCreateBibResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemCreateBibResponse>(itemCreateBibResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemCreateBibResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
        assertNotNull(itemCreateBibResponse.getBibId());
        assertNotNull(itemCreateBibResponse.getItemId());
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }

    @Test
    public void testCreateBibRequest_Exception() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = new ItemCreateBibRequest();
        itemCreateBibRequest.setPatronIdentifier("4568723");
        itemCreateBibRequest.setTitleIdentifier("test");
        itemCreateBibRequest.setItemBarcodes(Arrays.asList("4564"));
        itemCreateBibRequest.setItemOwningInstitution("PUL");

        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setBibId("45564");
        itemCreateBibResponse.setItemId("5886");
        itemCreateBibResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemCreateBibResponse>(itemCreateBibResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(requestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
    }


    @Test
    public void testRecallItem() throws IOException {
        ItemRecalRequest itemRecalRequest = new ItemRecalRequest();
        itemRecalRequest.setItemBarcodes(Arrays.asList("254"));
        itemRecalRequest.setItemOwningInstitution("PUL");
        itemRecalRequest.setPatronIdentifier("4856956");
        itemRecalRequest.setBibId("4563");
        itemRecalRequest.setPickupLocation("PB");

        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setPatronIdentifier("4569325");
        itemRecallResponse.setInstitutionID("PUL");
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemRecallResponse>(itemRecallResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemRecallResponse.class)).thenReturn(itemRecallResponse);
        Mockito.when(requestItemRestController.recallItem(itemRecalRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.recallItem(itemRecalRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());

    }
    @Test
    public void testRecallItem_RestClientException() throws IOException {
        ItemRecalRequest itemRecalRequest = new ItemRecalRequest();
        itemRecalRequest.setItemBarcodes(Arrays.asList("254"));
        itemRecalRequest.setItemOwningInstitution("PUL");
        itemRecalRequest.setPatronIdentifier("4856956");
        itemRecalRequest.setBibId("4563");
        itemRecalRequest.setPickupLocation("PB");

        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setPatronIdentifier("4569325");
        itemRecallResponse.setInstitutionID("PUL");
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemRecallResponse>(itemRecallResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemRecallResponse.class)).thenReturn(itemRecallResponse);
        Mockito.when(requestItemRestController.recallItem(itemRecalRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.recallItem(itemRecalRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }
    @Test
    public void testRecallItem_Exception() throws IOException {
        ItemRecalRequest itemRecalRequest = new ItemRecalRequest();
        itemRecalRequest.setItemBarcodes(Arrays.asList("254"));
        itemRecalRequest.setItemOwningInstitution("PUL");
        itemRecalRequest.setPatronIdentifier("4856956");
        itemRecalRequest.setBibId("4563");
        itemRecalRequest.setPickupLocation("PB");

        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setPatronIdentifier("4569325");
        itemRecallResponse.setInstitutionID("PUL");
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setSuccess(true);

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ResponseEntity responseEntity = new ResponseEntity<ItemRecallResponse>(itemRecallResponse,HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getObjectMapper().readValue(response, ItemRecallResponse.class)).thenReturn(itemRecallResponse);
        Mockito.when(requestItemRestController.recallItem(itemRecalRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = requestItemRestController.recallItem(itemRecalRequest);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testPatronInformation(){
        PatronInformationRequest patronInformationRequest = new PatronInformationRequest();
        patronInformationRequest.setPatronIdentifier("4562398");
        patronInformationRequest.setItemOwningInstitution("PUL");

        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("45623298");
        patronInformationResponse.setPatronName("John");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setScreenMessage("Patron validated successfully.");

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        HttpEntity request = new HttpEntity(itemRequestInfo);
        ResponseEntity responseEntity = new ResponseEntity<PatronInformationResponse>(patronInformationResponse,HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_PATRON_INFORMATION, org.springframework.http.HttpMethod.POST, request, PatronInformationResponse.class)).thenReturn(responseEntity);
        Mockito.when(requestItemRestController.patronInformation(patronInformationRequest)).thenCallRealMethod();
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(patronInformationRequest);
        assertNotNull(informationResponse);
        assertEquals("Patron validated successfully.",informationResponse.getScreenMessage());
    }
    @Test
    public void testPatronInformation_RestClientException(){
        PatronInformationRequest patronInformationRequest = new PatronInformationRequest();
        patronInformationRequest.setPatronIdentifier("4562398");
        patronInformationRequest.setItemOwningInstitution("PUL");

        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("45623298");
        patronInformationResponse.setPatronName("John");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setScreenMessage("Patron validated successfully.");

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        HttpEntity request = new HttpEntity(itemRequestInfo);
        ResponseEntity responseEntity = new ResponseEntity<PatronInformationResponse>(patronInformationResponse,HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.getRestTemplate().exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_PATRON_INFORMATION, org.springframework.http.HttpMethod.POST, request, PatronInformationResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(requestItemRestController.patronInformation(patronInformationRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(patronInformationRequest);
        assertNotNull(informationResponse);
        assertEquals("Exception occured",informationResponse.getScreenMessage());
    }

    @Test
    public void testPatronInformation_Exception(){
        PatronInformationRequest patronInformationRequest = new PatronInformationRequest();
        patronInformationRequest.setPatronIdentifier("4562398");
        patronInformationRequest.setItemOwningInstitution("PUL");

        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("45623298");
        patronInformationResponse.setPatronName("John");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setScreenMessage("Patron validated successfully.");

        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        HttpEntity request = new HttpEntity(itemRequestInfo);
        ResponseEntity responseEntity = new ResponseEntity<PatronInformationResponse>(patronInformationResponse,HttpStatus.OK);
        Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenReturn(null);
        Mockito.when(requestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(requestItemRestController.patronInformation(patronInformationRequest)).thenCallRealMethod();
        Mockito.when(requestItemRestController.getLogger()).thenReturn(mocklogger);
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(patronInformationRequest);
        assertNotNull(informationResponse);
    }

    @Test
    public void testGetterServices(){
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        Mockito.when(requestItemRestController.getRestTemplate()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getScsbCircUrl()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getItemRequestInformation()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getItemInformationRequest()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getObjectMapper()).thenCallRealMethod();
        Mockito.when(requestItemRestController.getProducer()).thenCallRealMethod();
        assertNotEquals(requestItemRestController.getRestTemplate(),mockRestTemplate);
        assertNotEquals(requestItemRestController.getScsbCircUrl(),scsbCircUrl);
      //  assertNotEquals(requestItemRestController.getItemRequestInformation(),itemRequestInfo);
        assertNotEquals(requestItemRestController.getItemInformationRequest(),itemInformationResponse);
        assertNotEquals(requestItemRestController.getObjectMapper(),objectMapper);
        assertNotEquals(requestItemRestController.getProducer(),producerTemplate);
    }




    @Test
    public void testRequestItem() {
        ItemResponseInformation itemResponseInformation = null;
        ResponseEntity responseEntity = new ResponseEntity("Success",HttpStatus.OK);
        try {
            ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
            itemRequestInfo.setPatronBarcode("32101077423406");
            itemRequestInfo.setItemBarcodes(Arrays.asList("3652147896532455"));
            itemRequestInfo.setRequestType(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInfo.setRequestingInstitution(RecapCommonConstants.PRINCETON);
            itemRequestInfo.setEmailAddress("ksudhish@gmail.com");

            HttpEntity request = new HttpEntity(itemRequestInfo);

            Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
            Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
            Mockito.when(requestItemRestController.getProducer()).thenReturn(producerTemplate);
            Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class)).thenReturn(responseEntity);
            Mockito.when(requestItemRestController.itemRequest(itemRequestInfo)).thenCallRealMethod();
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInfo);
            assertNotNull(itemResponseInformation);
            assertTrue(itemResponseInformation.isSuccess());
            assertEquals("Message received, your request will be processed",itemResponseInformation.getScreenMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRequestItem_HttpClientErrorException() {
        ItemResponseInformation itemResponseInformation = null;
        ResponseEntity responseEntity = new ResponseEntity("Success",HttpStatus.OK);
        try {
            ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
            itemRequestInfo.setPatronBarcode("32101077423406");
            itemRequestInfo.setItemBarcodes(Arrays.asList("3652147896532455"));
            itemRequestInfo.setRequestType(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL);
            itemRequestInfo.setRequestingInstitution(RecapCommonConstants.PRINCETON);
            itemRequestInfo.setEmailAddress("ksudhish@gmail.com");
            HttpEntity request = new HttpEntity(itemRequestInfo);
            Mockito.when(requestItemRestController.getRestTemplate()).thenReturn(mockRestTemplate);
            Mockito.when(requestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
            Mockito.when(requestItemRestController.getProducer()).thenReturn(producerTemplate);
            Mockito.when(requestItemRestController.getRestTemplate().postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
            Mockito.when(requestItemRestController.itemRequest(itemRequestInfo)).thenCallRealMethod();
            itemResponseInformation = requestItemRestController.itemRequest(itemRequestInfo);
            assertNotNull(itemResponseInformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTopic() {
        String testString ="Test";
        String body="";
        String endpointUri="scsbactivemq:topic:PUL.Request";
        try {
            producer.sendBody(endpointUri, testString);
            logger.info("Start");
            Exchange receive = consumer.receive(endpointUri);
            logger.info(receive.getMessage().getBody().toString());
        }catch(Exception e){
            logger.error(e.toString());
        } finally {

        }
        assertNotEquals(testString, body);
    }

    @Test
    public void testJMS() {
        String testString ="Test";
        String body="";
        try {

        }catch(Exception e){
            logger.error(e.toString());
        } finally {

        }
        assertNotEquals(testString, body);
    }

    private ItemCheckinResponse getItemCheckInResponse(){
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemCheckinResponse.setItemOwningInstitution("PUL");
        itemCheckinResponse.setPatronIdentifier("45678915");
        itemCheckinResponse.setSuccess(true);
        itemCheckinResponse.setCreatedDate(new Date().toString());
        itemCheckinResponse.setBibId("234");
        itemCheckinResponse.setScreenMessage("Item checked in successfully");
        itemCheckinResponse.setDueDate(new Date().toString());
        itemCheckinResponse.setItemBarcode("123");
        return itemCheckinResponse;
    }

    private ItemCheckoutResponse getItemCheckoutResponse(){
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        itemCheckoutResponse.setItemBarcode("12345");
        itemCheckoutResponse.setSuccess(true);
        itemCheckoutResponse.setDueDate(new Date().toString());
        itemCheckoutResponse.setPatronIdentifier("45697123");
        itemCheckoutResponse.setDesensitize(true);
        itemCheckoutResponse.setMagneticMedia(true);
        itemCheckoutResponse.setProcessed(true);
        itemCheckoutResponse.setRenewal(true);
        return itemCheckoutResponse;
    }


}