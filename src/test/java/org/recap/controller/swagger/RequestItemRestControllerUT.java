package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by hemalathas on 4/11/16.
 */
public class RequestItemRestControllerUT extends BaseTestCase{
    private static final Logger logger = LoggerFactory.getLogger(RequestItemRestControllerUT.class);

    @InjectMocks
    private RequestItemRestController requestItemRestController;

    @Mock
    private RequestItemRestController mockRequestItemRestController;

    @Mock
    private RequestItemRestController getRequestItemRestController;

    @Mock
    private CamelContext camelContext;

    @Mock
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

    @Mock
    private RestHeaderService restHeaderService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(requestItemRestController,"scsbCircUrl",scsbCircUrl);
        ReflectionTestUtils.setField(requestItemRestController,"restHeaderService",restHeaderService);
        ReflectionTestUtils.setField(mockRequestItemRestController,"restTemplate",mockRestTemplate);
    }

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public void setScsbCircUrl(String scsbCircUrl) {
        this.scsbCircUrl = scsbCircUrl;
    }

    private static final Logger mocklogger = LoggerFactory.getLogger(RequestItemRestController.class);

    String institutionPUL=RecapCommonConstants.PRINCETON;
    String institutionCUL=RecapCommonConstants.COLUMBIA;

    @Test
    public void testValidRequest() {
        ResponseEntity responseEntity = new ResponseEntity(RecapCommonConstants.VALID_REQUEST, HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = getValidItemRequestInformation(institutionCUL);
        assertFalse(itemRequestInformation.isOwningInstitutionItem());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(RecapCommonConstants.VALID_REQUEST,responseEntity1.getBody());
    }

    @Test
    public void testValidRequest_HttpClientErrorException() {
        ItemRequestInformation itemRequestInformation =getValidItemRequestInformation(institutionPUL);
        assertTrue(itemRequestInformation.isOwningInstitutionItem());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity1.getStatusCode());
   }

    @Test
    public void testValidRequest_RestClientException() {
        ItemRequestInformation itemRequestInformation = getValidItemRequestInformation(institutionCUL);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenThrow(new RestClientException(""));
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals("Scsb circ Service is Unavailable.",responseEntity1.getBody());

    }

    private ItemRequestInformation getValidItemRequestInformation(String requestingInstitution) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("32101047717911"));
        itemRequestInformation.setTitleIdentifier("105 paintings in the John Herron Art Museum.");
        itemRequestInformation.setItemOwningInstitution(institutionPUL);
        itemRequestInformation.setRequestType("EDD");
        itemRequestInformation.setRequestingInstitution(requestingInstitution);
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setStartPage("I");
        itemRequestInformation.setEndPage("XII");
        return itemRequestInformation;
    }

    @Test
    public void testRequestWithInvalidRequestingInst() {
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.INVALID_REQUEST_INSTITUTION+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation("45678915","Borrow Direct","PULd","hemalatha.s@htcindia.com");
        itemRequestInformation.setItemOwningInstitution(institutionCUL);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals( RecapConstants.INVALID_REQUEST_INSTITUTION+"\n",responseEntity1.getBody());
    }

    @Test
    public void testRequestParameterWithInvalidPatronBarcode() {
        ResponseEntity responseEntity = new ResponseEntity("Patron barcode not found", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation("g75dfgsf","Borrow Direct",institutionPUL,"hemalatha.s@htcindia.com");
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals("Patron barcode not found",responseEntity1.getBody());
    }

    @Test
    public void testRequestParameterWithEDDRequestType() {
        ResponseEntity responseEntity = new ResponseEntity(RecapConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n", HttpStatus.OK);
        ItemRequestInformation itemRequestInformation = getItemRequestInformation("45678915","EDD",institutionPUL,"hemalatha.s@htcindia.com");
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/validateItemRequestInformations", itemRequestInformation, String.class)).thenReturn(responseEntity);
        ResponseEntity responseEntity1 = requestItemRestController.validateItemRequest(itemRequestInformation);
        assertNotNull(responseEntity1);
        assertEquals( RecapConstants.START_PAGE_AND_END_PAGE_REQUIRED+"\n",responseEntity1.getBody());
    }

    private ItemRequestInformation getItemRequestInformation(String patronBarcode,String requestType,String requestingInstitution,String emailAddress) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode(patronBarcode);
        itemRequestInformation.setRequestType(requestType);
        itemRequestInformation.setRequestingInstitution(requestingInstitution);
        itemRequestInformation.setEmailAddress(emailAddress);
        return itemRequestInformation;
    }

    @Test
    public void testCancelRequest(){
        HttpEntity request = new HttpEntity<>(restHeaderService.getHttpHeaders());
        CancelRequestResponse cancelRequestResponse = new CancelRequestResponse();
        cancelRequestResponse.setScreenMessage("Request cancelled.");
        cancelRequestResponse.setSuccess(true);
        ResponseEntity<CancelRequestResponse> responseEntity = new ResponseEntity<CancelRequestResponse>(cancelRequestResponse,HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbCircUrl() + RecapConstants.URL_REQUEST_CANCEL).queryParam("requestId", 129);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), org.springframework.http.HttpMethod.POST, request, CancelRequestResponse.class)).thenReturn(responseEntity);
        cancelRequestResponse = requestItemRestController.cancelRequest(129);
        assertNotNull(cancelRequestResponse);
        assertEquals("Request cancelled.",cancelRequestResponse.getScreenMessage());
        assertEquals(true,cancelRequestResponse.isSuccess());
    }
    @Test
    public void testbulkRequest(){
        BulkRequestResponse bulkRequestResponse = requestItemRestController.bulkRequest(1);
        assertNotNull(bulkRequestResponse);
    }

    @Test
    public void testrefileItemInILS(){
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        ResponseEntity<ItemRefileResponse> responseEntity = new ResponseEntity<ItemRefileResponse>(itemRefileResponse,HttpStatus.OK);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", getItemRequestInformation("32101104657091",institutionPUL), ItemRefileResponse.class)).thenReturn(responseEntity);
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS("32101104657091",institutionPUL);
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testrefileItemInILS_RestClientException(){
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/refileItemInILS", getItemRequestInformation("32101104657091",institutionPUL), ItemRefileResponse.class)).thenThrow(new RestClientException("Exception occured"));
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS("32101104657091",institutionPUL);
        assertNull(abstractResponseItem);
    }

    private ItemRequestInformation getItemRequestInformation(String barcode,String itemOwningInstitution) {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(Arrays.asList(barcode));
        itemRequestInfo.setItemOwningInstitution(itemOwningInstitution);
        return itemRequestInfo;
    }

    @Test
    public void testrefileItemInILS_Exception(){
        AbstractResponseItem abstractResponseItem  = requestItemRestController.refileItemInILS("32101104657091",institutionPUL);
        assertNull(abstractResponseItem);
    }

    @Test
    public void replaceRequestToLAS(){
        ReplaceRequest replaceRequest = new ReplaceRequest();
        Mockito.when(mockRestTemplate.postForObject(getScsbCircUrl() + RecapConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class)).thenReturn(new HashMap());
        ResponseEntity ResponseEntity  = requestItemRestController.replaceRequestToLAS(replaceRequest);
        assertNotNull(ResponseEntity);
    }

    @Test
    public void replaceRequestToLAS_Exception(){
        ReplaceRequest replaceRequest = null;
        Mockito.when(mockRestTemplate.postForObject(getScsbCircUrl() + RecapConstants.URL_REQUEST_REPLACE, replaceRequest, Map.class)).thenThrow(new RestClientException("Exception occured"));
        ResponseEntity ResponseEntity  = requestItemRestController.replaceRequestToLAS(replaceRequest);
        assertNotNull(ResponseEntity);
    }

    @Test
    public void testcheckinItemRequest() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(institutionPUL, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemCheckInRequest itemCheckInRequest = getItemCheckInRequest();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn( getItemRequestInformation("45678915","123",institutionPUL));
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemCheckinResponse.class)).thenReturn(getItemCheckInResponse());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkinItem",  getItemRequestInformation("45678915","123",institutionPUL), String.class)).thenReturn(responseEntity);
        Mockito.when(mockRequestItemRestController.checkinItemRequest(itemCheckInRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.getItemBarcode().contains("123"));
    }

    @Test
    public void testcheckinItemRequest_RestClientException() throws IOException {

        ResponseEntity responseEntity = new ResponseEntity(institutionPUL, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemCheckInRequest itemCheckInRequest = getItemCheckInRequest();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn( getItemRequestInformation("45678915","123",institutionPUL));
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemCheckinResponse.class)).thenReturn(getItemCheckInResponse());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkinItem",  getItemRequestInformation("45678915","123",institutionPUL), String.class)).thenThrow(new RestClientException("Exception occured"));;
        Mockito.when(mockRequestItemRestController.checkinItemRequest(itemCheckInRequest)).thenCallRealMethod();
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.checkinItemRequest(itemCheckInRequest);
        assertNull(abstractResponseItem);
     }

    private ItemRequestInformation getItemRequestInformation(String patronBarcode,String itemBarcodes,String itemOwningInstitution) {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setPatronBarcode(patronBarcode);
        itemRequestInfo.setItemBarcodes(Arrays.asList(itemBarcodes));
        itemRequestInfo.setItemOwningInstitution(itemOwningInstitution);
        itemRequestInfo.setRequestingInstitution(itemOwningInstitution);
        return itemRequestInfo;
    }

    @Test
    public void testcheckinItemRequest_Exception() {
        AbstractResponseItem abstractResponseItem = requestItemRestController.checkinItemRequest(getItemCheckInRequest());
        assertNull(abstractResponseItem);
    }

    private ItemCheckInRequest getItemCheckInRequest() {
        ItemCheckInRequest itemCheckInRequest = new ItemCheckInRequest();
        itemCheckInRequest.setPatronIdentifier("45678915");
        itemCheckInRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckInRequest.setItemOwningInstitution(institutionPUL);
        return itemCheckInRequest;
    }

    @Test
    public void testItemInformationPUL(){
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(getItemInformationResponse(institutionPUL),HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = getItemInformationRequest(institutionPUL);
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRestTemplate.exchange(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class)).thenReturn(responseEntity);
        Mockito.when(mockRequestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    @Test
    public void testItemInformationPUL_RestClientException(){
        ItemInformationRequest itemInformationRequest = getItemInformationRequest(institutionPUL);
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRestTemplate.exchange(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(mockRequestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }

    private ItemInformationRequest getItemInformationRequest(String itemOwningInstitution) {
        ItemInformationRequest itemInformationRequest = new ItemInformationRequest();
        itemInformationRequest.setItemBarcodes(Arrays.asList("233"));
        itemInformationRequest.setItemOwningInstitution(itemOwningInstitution);
        return itemInformationRequest;
    }

    @Test
    public void testItemInformationPUL_Exception(){
        ItemInformationRequest itemInformationRequest = getItemInformationRequest(institutionPUL);
        Mockito.when(requestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
    }

    private ItemInformationResponse getItemInformationResponse(String itemOwningInstitution) {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setItemOwningInstitution(itemOwningInstitution);
        itemInformationResponse.setSuccess(true);
        itemInformationResponse.setBibID("111");
        itemInformationResponse.setCreatedDate(new Date().toString());
        return itemInformationResponse;
    }

    @Test
    public void testItemInformationCUL(){
        ItemInformationResponse itemInformationResponse = getItemInformationResponse(institutionCUL);
        ResponseEntity<ItemInformationResponse> responseEntity = new ResponseEntity<>(itemInformationResponse,HttpStatus.OK);
        ItemInformationRequest itemInformationRequest = getItemInformationRequest(institutionCUL);
        HttpEntity request = new HttpEntity(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.getItemInformationRequest()).thenReturn(itemInformationRequest);
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.doReturn(responseEntity).when(mockRestTemplate).exchange(scsbCircUrl+ RecapConstants.URL_REQUEST_ITEM_INFORMATION, org.springframework.http.HttpMethod.POST, request, ItemInformationResponse.class);
        Mockito.when(mockRequestItemRestController.itemInformation(itemInformationRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.itemInformation(itemInformationRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    @Test
    public void testcancelHoldItemRequest() throws IOException {
        ItemHoldCancelRequest itemHoldCancelRequest = getItemHoldCancelRequest();
        ResponseEntity responseEntity = new ResponseEntity("test",HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(getItemRequestInformation(itemHoldCancelRequest));
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", getItemRequestInformation(itemHoldCancelRequest), String.class)).thenReturn(responseEntity);
        Mockito.when(objectMapper.readValue(response, ItemHoldResponse.class)).thenReturn(getItemHoldResponse());
        Mockito.when(mockRequestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);

    }

    @Test
    public void testcancelHoldItemRequest_RestClientException() {
        ItemHoldCancelRequest itemHoldCancelRequest = getItemHoldCancelRequest();
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/cancelHoldItem", getItemRequestInformation(itemHoldCancelRequest), String.class)).thenThrow(new RestClientException("Exception occured"));
        AbstractResponseItem abstractResponseItem = requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());

    }
    private ItemRequestInformation getItemRequestInformation(ItemHoldCancelRequest itemHoldCancelRequest) {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(itemHoldCancelRequest.getItemBarcodes());
        itemRequestInfo.setItemOwningInstitution(itemHoldCancelRequest.getItemOwningInstitution());
        itemRequestInfo.setRequestingInstitution(itemHoldCancelRequest.getItemOwningInstitution());
        itemRequestInfo.setPatronBarcode(itemHoldCancelRequest.getPatronIdentifier());
        itemRequestInfo.setBibId(itemHoldCancelRequest.getBibId());
        itemRequestInfo.setDeliveryLocation(itemHoldCancelRequest.getPickupLocation());
        itemRequestInfo.setTrackingId(itemHoldCancelRequest.getTrackingId());
        return itemRequestInfo;
    }
    @Test
    public void testcancelHoldItemRequest_Exception() {
        ItemHoldCancelRequest itemHoldCancelRequest = getItemHoldCancelRequest();
        AbstractResponseItem abstractResponseItem = requestItemRestController.cancelHoldItemRequest(itemHoldCancelRequest);
        assertNotNull(abstractResponseItem);
    }

    private ItemHoldCancelRequest getItemHoldCancelRequest() {
        ItemHoldCancelRequest itemHoldCancelRequest = new ItemHoldCancelRequest();
        itemHoldCancelRequest.setItemBarcodes(Arrays.asList("3216549874100225"));
        itemHoldCancelRequest.setItemOwningInstitution(institutionPUL);
        itemHoldCancelRequest.setPickupLocation("PB");
        itemHoldCancelRequest.setBibId("3256");
        itemHoldCancelRequest.setPatronIdentifier("456328965");
        itemHoldCancelRequest.setTrackingId("2");
        return itemHoldCancelRequest;
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
        Mockito.when(mockRestTemplate.exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_RE_FILE, org.springframework.http.HttpMethod.POST, request, ItemRefileResponse.class)).thenReturn(responseEntity);
        ItemRefileResponse itemRefileResponse1 = requestItemRestController.refileItem(itemRefileRequest);
        assertNotNull(itemRefileResponse1);
        assertNotNull(itemRefileRequest.getItemBarcodes());
        assertNotNull(itemRefileRequest.getRequestIds());
        assertNotNull(itemRefileResponse.getScreenMessage());
        assertNotNull(itemRefileResponse.isSuccess());
    }

    @Test
    public void testCheckoutItemRequest() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(institutionPUL, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemCheckOutRequest itemCheckOutRequest = getItemCheckOutRequest();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenCallRealMethod();
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemCheckoutResponse.class)).thenReturn(getItemCheckoutResponse());
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", getItemRequestInformation("45678915","123",institutionPUL), String.class)).thenReturn(responseEntity);
        Mockito.when(mockRequestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = mockRequestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);

    }

    private ItemCheckOutRequest getItemCheckOutRequest() {
        ItemCheckOutRequest itemCheckOutRequest = new ItemCheckOutRequest();
        itemCheckOutRequest.setPatronIdentifier("45678915");
        itemCheckOutRequest.setItemBarcodes(Arrays.asList("123"));
        itemCheckOutRequest.setItemOwningInstitution(institutionPUL);
        return itemCheckOutRequest;
    }

    @Test
    public void testCheckoutItemRequest_RestClientException() throws IOException {
        ResponseEntity responseEntity = new ResponseEntity(institutionPUL, HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        ItemCheckOutRequest itemCheckOutRequest = getItemCheckOutRequest();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(getItemRequestInformation("45678915","123",institutionPUL));
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemCheckoutResponse.class)).thenThrow(new RestClientException(RecapCommonConstants.REQUEST_EXCEPTION_REST));
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + "requestItem/checkoutItem", getItemRequestInformation("45678915","123",institutionPUL), String.class)).thenReturn(responseEntity);
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.checkoutItemRequest(itemCheckOutRequest)).thenCallRealMethod();
        ItemCheckoutResponse itemCheckoutResponse = mockRequestItemRestController.checkoutItemRequest(itemCheckOutRequest);
        assertNotNull(itemCheckoutResponse);
        assertEquals(RecapCommonConstants.REQUEST_EXCEPTION_REST,itemCheckoutResponse.getScreenMessage());
    }
    @Test
    public void testCheckoutItemRequest_Exception() {
        ItemCheckoutResponse itemCheckoutResponse = requestItemRestController.checkoutItemRequest(getItemCheckOutRequest());
        assertNotNull(itemCheckoutResponse);
    }

    @Test
    public void testHoldRequestItem() throws IOException {
        ItemHoldRequest itemHoldRequest = getItemHoldRequest();
        ResponseEntity responseResponseEntity = new ResponseEntity<>(getItemHoldResponse(),HttpStatus.OK);
        String response = responseResponseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(getItemRequestInformation(itemHoldRequest));
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemHoldResponse.class)).thenReturn(getItemHoldResponse());
        Mockito.when(mockRestTemplate.postForEntity(scsbCircUrl + RecapConstants.URL_REQUEST_ITEM_HOLD, getItemRequestInformation(itemHoldRequest), String.class)).thenReturn(responseResponseEntity);
        Mockito.when(mockRequestItemRestController.holdItemRequest(itemHoldRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.holdItemRequest(itemHoldRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }

    private ItemHoldResponse getItemHoldResponse() {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        itemHoldResponse.setAvailable(true);
        itemHoldResponse.setBibId("14533");
        itemHoldResponse.setTransactionDate(new Date().toString());
        itemHoldResponse.setCreatedDate(new Date().toString());
        itemHoldResponse.setExpirationDate(new Date().toString());
        itemHoldResponse.setInstitutionID(institutionPUL);
        itemHoldResponse.setSuccess(true);
        return itemHoldResponse;
    }

    @Test
    public void testHoldRequestItem_RestClientException() throws IOException {
        ItemHoldRequest itemHoldRequest = getItemHoldRequest();
        ResponseEntity responseResponseEntity = new ResponseEntity<>(getItemHoldResponse(),HttpStatus.OK);
        String response = responseResponseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(getItemRequestInformation(itemHoldRequest));
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(objectMapper.readValue(response, ItemHoldResponse.class)).thenReturn(getItemHoldResponse());

        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_HOLD, getItemRequestInformation(itemHoldRequest), String.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(mockRequestItemRestController.holdItemRequest(itemHoldRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.holdItemRequest(itemHoldRequest);
        assertNotNull(abstractResponseItem);
    }

    private ItemHoldRequest getItemHoldRequest() {
        ItemHoldRequest itemHoldRequest = new ItemHoldRequest();
        itemHoldRequest.setItemOwningInstitution(institutionPUL);
        itemHoldRequest.setTrackingId("564");
        itemHoldRequest.setCallNumber("5645");
        itemHoldRequest.setItemBarcodes(Arrays.asList("32101047717911"));
        itemHoldRequest.setPatronIdentifier("42659872");
        itemHoldRequest.setBibId("14533");
        itemHoldRequest.setAuthor("John");
        itemHoldRequest.setPickupLocation("PB");
        itemHoldRequest.setCallNumber("X");
        return itemHoldRequest;
    }

    @Test
    public void testHoldRequestItem_Exception() {
        ResponseEntity responseResponseEntity = new ResponseEntity<>(getItemHoldResponse(),HttpStatus.OK);
        Mockito.when(mockRestTemplate.postForEntity(scsbCircUrl + RecapConstants.URL_REQUEST_ITEM_HOLD, getItemRequestInformation(getItemHoldRequest()), String.class)).thenReturn(responseResponseEntity);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = requestItemRestController.holdItemRequest(getItemHoldRequest());
        assertNotNull(abstractResponseItem.getScreenMessage());
    }

    private ItemRequestInformation getItemRequestInformation(ItemHoldRequest itemHoldRequest) {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
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
        return itemRequestInfo;
    }

    @Test
    public void testCreateBibRequest() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = getItemCreateBibRequest();

        ItemRequestInformation itemRequestInfo = getItemRequestInformation("4568723","4564",institutionPUL);
        itemRequestInfo.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());
        ResponseEntity responseEntity = new ResponseEntity<>(getItemCreateBibResponse(),HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(mockRestTemplate.postForEntity(scsbCircUrl + RecapConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(objectMapper.readValue(response, ItemCreateBibResponse.class)).thenReturn(getItemCreateBibResponse());
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());
    }
    @Test
    public void testCreateBibRequest_RestClientException() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = getItemCreateBibRequest();

        ItemRequestInformation itemRequestInfo = getItemRequestInformation("4568723","4564",institutionPUL);
        itemRequestInfo.setTitleIdentifier(itemCreateBibRequest.getTitleIdentifier());

        ResponseEntity responseEntity = new ResponseEntity<>(getItemCreateBibResponse(),HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_CREATEBIB, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(objectMapper.readValue(response, ItemCreateBibResponse.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        Mockito.when(mockRequestItemRestController.createBibRequest(itemCreateBibRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }

    @Test
    public void testCreateBibRequest_Exception() throws IOException {
        ItemCreateBibRequest itemCreateBibRequest = getItemCreateBibRequest();
        AbstractResponseItem abstractResponseItem = requestItemRestController.createBibRequest(itemCreateBibRequest);
        assertNotNull(abstractResponseItem);
    }


    @Test
    public void testRecallItem() throws IOException {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemRecalRequest itemRecalRequest =getItemRecalRequest();
        ResponseEntity responseEntity = new ResponseEntity<>(getItemRecallResponse(),HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class)).thenReturn(responseEntity);
        Mockito.when(objectMapper.readValue(response, ItemRecallResponse.class)).thenReturn(getItemRecallResponse());
        Mockito.when(mockRequestItemRestController.recallItem(itemRecalRequest)).thenCallRealMethod();
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.recallItem(itemRecalRequest);
        assertNotNull(abstractResponseItem);
        assertTrue(abstractResponseItem.isSuccess());

    }



    @Test
    public void testRecallItem_RestClientException() throws IOException {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        ItemRecalRequest itemRecalRequest =getItemRecalRequest();
        ResponseEntity responseEntity = new ResponseEntity<>(getItemRecallResponse(),HttpStatus.OK);
        String response = responseEntity.getBody().toString();
        Mockito.when(mockRequestItemRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(mockRequestItemRestController.getItemRequestInformation()).thenReturn(itemRequestInfo);
        Mockito.when(mockRequestItemRestController.getObjectMapper()).thenReturn(objectMapper);
        Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_RECALL, itemRequestInfo, String.class)).thenThrow(new RestClientException("Exception occured"));
        Mockito.when(mockRequestItemRestController.getObjectMapper().readValue(response, ItemRecallResponse.class)).thenReturn(getItemRecallResponse());
        Mockito.when(mockRequestItemRestController.recallItem(itemRecalRequest)).thenCallRealMethod();
        Mockito.when(mockRequestItemRestController.getLogger()).thenReturn(mocklogger);
        AbstractResponseItem abstractResponseItem = mockRequestItemRestController.recallItem(itemRecalRequest);
        assertNotNull(abstractResponseItem);
        assertEquals("Exception occured",abstractResponseItem.getScreenMessage());
    }
    @Test
    public void testRecallItem_Exception() {
        AbstractResponseItem abstractResponseItem = requestItemRestController.recallItem(getItemRecalRequest());
        assertNotNull(abstractResponseItem);
    }

    @Test
    public void testPatronInformation(){
        HttpEntity request = new HttpEntity(getItemRequestInformation(getPatronInformationRequest()));
        ResponseEntity responseEntity = new ResponseEntity<PatronInformationResponse>(getPatronInformationResponse(),HttpStatus.OK);
        Mockito.when(mockRestTemplate.exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_PATRON_INFORMATION, org.springframework.http.HttpMethod.POST, request, PatronInformationResponse.class)).thenReturn(responseEntity);
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(getPatronInformationRequest());
        assertNotNull(informationResponse);
        assertEquals("Patron validated successfully.",informationResponse.getScreenMessage());
    }

    private ItemRequestInformation getItemRequestInformation(PatronInformationRequest patronInformationRequest) {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setPatronBarcode(patronInformationRequest.getPatronIdentifier());
        itemRequestInfo.setItemOwningInstitution(patronInformationRequest.getItemOwningInstitution());
        return itemRequestInfo;
    }

    @Test
    public void testPatronInformation_RestClientException(){
        HttpEntity request = new HttpEntity(getItemRequestInformation(getPatronInformationRequest()));
        Mockito.when(mockRestTemplate.exchange(getScsbCircUrl() +   RecapConstants.URL_REQUEST_PATRON_INFORMATION, org.springframework.http.HttpMethod.POST, request, PatronInformationResponse.class)).thenThrow(new RestClientException("Exception occured"));
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(getPatronInformationRequest());
        assertNotNull(informationResponse);
        assertEquals("Exception occured",informationResponse.getScreenMessage());
    }

    @Test
    public void testPatronInformation_Exception(){
        PatronInformationResponse informationResponse = requestItemRestController.patronInformation(new PatronInformationRequest());
        assertNotNull(informationResponse);
    }

    @Test
    public void testRequestItem() {
        ItemResponseInformation itemResponseInformation = null;
        ResponseEntity responseEntity = new ResponseEntity("Success",HttpStatus.OK);
        try {
            Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, getItemRequestInformation(), String.class)).thenReturn(responseEntity);
            itemResponseInformation = requestItemRestController.itemRequest(getItemRequestInformation());
            assertNotNull(itemResponseInformation);
            assertTrue(itemResponseInformation.isSuccess());
            assertEquals("Message received, your request will be processed",itemResponseInformation.getScreenMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setPatronBarcode("32101077423406");
        itemRequestInfo.setItemBarcodes(Arrays.asList("3652147896532455"));
        itemRequestInfo.setRequestType(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL);
        itemRequestInfo.setRequestingInstitution(institutionPUL);
        itemRequestInfo.setEmailAddress("ksudhish@gmail.com");
        return itemRequestInfo;
    }

    @Test
    public void testRequestItem_Exception() {
        ItemResponseInformation itemResponseInformation = null;
        ResponseEntity responseEntity = new ResponseEntity("Success",HttpStatus.OK);
        try {
            ReflectionTestUtils.setField(requestItemRestController,"producer",null);
            Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, getItemRequestInformation(), String.class)).thenReturn(responseEntity);
            itemResponseInformation = requestItemRestController.itemRequest(getItemRequestInformation());
            assertNotNull(itemResponseInformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRequestItem_HttpClientErrorException() {
        ItemResponseInformation itemResponseInformation = null;
        try {
            ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
            Mockito.when(mockRestTemplate.postForEntity(getScsbCircUrl() + RecapConstants.URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST, itemRequestInfo, String.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
             itemResponseInformation = requestItemRestController.itemRequest(itemRequestInfo);
            assertNotNull(itemResponseInformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ItemCheckinResponse getItemCheckInResponse(){
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        itemCheckinResponse.setItemOwningInstitution(institutionPUL);
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
    private PatronInformationResponse getPatronInformationResponse() {
        PatronInformationResponse patronInformationResponse = new PatronInformationResponse();
        patronInformationResponse.setPatronIdentifier("45623298");
        patronInformationResponse.setPatronName("John");
        patronInformationResponse.setPickupLocation("PB");
        patronInformationResponse.setDueDate(new Date().toString());
        patronInformationResponse.setExpirationDate(new Date().toString());
        patronInformationResponse.setEmail("hemalatha.s@htcindia.com");
        patronInformationResponse.setScreenMessage("Patron validated successfully.");
        return patronInformationResponse;
    }

    private PatronInformationRequest getPatronInformationRequest() {
        PatronInformationRequest patronInformationRequest = new PatronInformationRequest();
        patronInformationRequest.setPatronIdentifier("4562398");
        patronInformationRequest.setItemOwningInstitution(institutionPUL);
        return patronInformationRequest;
    }

    private ItemRecalRequest getItemRecalRequest() {
        ItemRecalRequest itemRecalRequest = new ItemRecalRequest();
        itemRecalRequest.setItemBarcodes(Arrays.asList("254"));
        itemRecalRequest.setItemOwningInstitution(institutionPUL);
        itemRecalRequest.setPatronIdentifier("4856956");
        itemRecalRequest.setBibId("4563");
        itemRecalRequest.setPickupLocation("PB");
        return itemRecalRequest;
    }

    private ItemRecallResponse getItemRecallResponse() {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        itemRecallResponse.setAvailable(true);
        itemRecallResponse.setPatronIdentifier("4569325");
        itemRecallResponse.setInstitutionID(institutionPUL);
        itemRecallResponse.setExpirationDate(new Date().toString());
        itemRecallResponse.setSuccess(true);
        return itemRecallResponse;
    }
    private ItemCreateBibRequest getItemCreateBibRequest() {
        ItemCreateBibRequest itemCreateBibRequest = new ItemCreateBibRequest();
        itemCreateBibRequest.setPatronIdentifier("4568723");
        itemCreateBibRequest.setTitleIdentifier("test");
        itemCreateBibRequest.setItemBarcodes(Arrays.asList("4564"));
        itemCreateBibRequest.setItemOwningInstitution(institutionPUL);
        return itemCreateBibRequest;
    }

    private ItemCreateBibResponse getItemCreateBibResponse() {
        ItemCreateBibResponse itemCreateBibResponse = new ItemCreateBibResponse();
        itemCreateBibResponse.setBibId("45564");
        itemCreateBibResponse.setItemId("5886");
        itemCreateBibResponse.setSuccess(true);
        return itemCreateBibResponse;
    }


}