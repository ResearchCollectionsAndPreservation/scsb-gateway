package org.recap.controller.swagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.BaseControllerUT;
import org.recap.model.BibItemAvailabityStatusRequest;
import org.recap.model.ItemAvailabityStatusRequest;
import org.recap.model.accession.AccessionModelRequest;
import org.recap.model.accession.AccessionRequest;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.transfer.Destination;
import org.recap.model.transfer.HoldingTransferResponse;
import org.recap.model.transfer.HoldingsTransferRequest;
import org.recap.model.transfer.Source;
import org.recap.model.transfer.TransferRequest;
import org.recap.model.transfer.TransferResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class SharedCollectionRestControllerUT extends BaseControllerUT {

    @Value("${" + PropertyKeyConstants.SCSB_SOLR_DOC_URL + "}")
    String scsbSolrClientUrl;

    @Value("${" + PropertyKeyConstants.SCSB_CIRC_URL + "}")
    String scsbCircUrl;

    @Value("${" + PropertyKeyConstants.SCSB_CORE_URL + "}")
    private String scsbCoreUrl;


    @Mock
    private RestTemplate mockRestTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(sharedCollectionRestController,"scsbCoreUrl",scsbCoreUrl);
    }

    public String getScsbCoreUrl() {
        return scsbCoreUrl;
    }

    public String getScsbCircUrl() {
        return scsbCircUrl;
    }

    public void setScsbCircUrl(String scsbCircUrl) {
        this.scsbCircUrl = scsbCircUrl;
    }

    public String getScsbSolrClientUrl() {
        return scsbSolrClientUrl;
    }

    public void setScsbSolrClientUrl(String scsbSolrClientUrl) {
        this.scsbSolrClientUrl = scsbSolrClientUrl;
    }

    @Mock
    SharedCollectionRestController sharedCollectionRestController;

    String inputRecords = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +
            "<record>\n" +
            "<leader>01011cam a2200289 a 4500</leader>\n" +
            "<controlfield tag=\"001\">115115</controlfield>\n" +
            "<controlfield tag=\"005\">20160503221017.0</controlfield>\n" +
            "<controlfield tag=\"008\">820315s1982 njua b 00110 eng</controlfield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"010\">\n" +
            "<subfield code=\"a\">81008543</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"020\">\n" +
            "<subfield code=\"a\">0132858908</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"a\">(OCoLC)7555877</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"a\">(CStRLIN)NJPG82-B5675</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"035\">\n" +
            "<subfield code=\"9\">AAS9821TS</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"039\">\n" +
            "<subfield code=\"a\">2</subfield>\n" +
            "<subfield code=\"b\">3</subfield>\n" +
            "<subfield code=\"c\">3</subfield>\n" +
            "<subfield code=\"d\">3</subfield>\n" +
            "<subfield code=\"e\">3</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"050\">\n" +
            "<subfield code=\"a\">QE28.3</subfield>\n" +
            "<subfield code=\"b\">.S76 1982</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\" \" tag=\"082\">\n" +
            "<subfield code=\"a\">551.7</subfield>\n" +
            "<subfield code=\"2\">19</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"1\" ind2=\" \" tag=\"100\">\n" +
            "<subfield code=\"a\">Stokes, William Lee,</subfield>\n" +
            "<subfield code=\"d\">1915-1994.</subfield>\n" +
            "<subfield code=\"0\">(uri)http://id.loc.gov/authorities/names/n50011514</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"1\" ind2=\"0\" tag=\"245\">\n" +
            "<subfield code=\"a\">Essentials of earth history :</subfield>\n" +
            "<subfield code=\"b\">an introduction to historical geology /</subfield>\n" +
            "<subfield code=\"c\">W. Lee Stokes.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"250\">\n" +
            "<subfield code=\"a\">4th ed.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"260\">\n" +
            "<subfield code=\"a\">Englewood Cliffs, N.J. :</subfield>\n" +
            "<subfield code=\"b\">Prentice-Hall,</subfield>\n" +
            "<subfield code=\"c\">c1982.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"300\">\n" +
            "<subfield code=\"a\">xiv, 577 p. :</subfield>\n" +
            "<subfield code=\"b\">ill. ;</subfield>\n" +
            "<subfield code=\"c\">24 cm.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"504\">\n" +
            "<subfield code=\"a\">Includes bibliographies and index.</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\"0\" tag=\"650\">\n" +
            "<subfield code=\"a\">Historical geology.</subfield>\n" +
            "<subfield code=\"0\">\n" +
            "(uri)http://id.loc.gov/authorities/subjects/sh85061190\n" +
            "</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"998\">\n" +
            "<subfield code=\"a\">03/15/82</subfield>\n" +
            "<subfield code=\"s\">9110</subfield>\n" +
            "<subfield code=\"n\">NjP</subfield>\n" +
            "<subfield code=\"w\">DCLC818543B</subfield>\n" +
            "<subfield code=\"d\">03/15/82</subfield>\n" +
            "<subfield code=\"c\">ZG</subfield>\n" +
            "<subfield code=\"b\">WZ</subfield>\n" +
            "<subfield code=\"i\">820315</subfield>\n" +
            "<subfield code=\"l\">NJPG</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"948\">\n" +
            "<subfield code=\"a\">AACR2</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"911\">\n" +
            "<subfield code=\"a\">19921028</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"912\">\n" +
            "<subfield code=\"a\">19900820000000.0</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\" \" ind2=\" \" tag=\"959\">\n" +
            "<subfield code=\"a\">2000-06-13 00:00:00 -0500</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"852\">\n" +
            "<subfield code=\"0\">128532</subfield>\n" +
            "<subfield code=\"b\">rcppa</subfield>\n" +
            "<subfield code=\"h\">QE28.3 .S76 1982</subfield>\n" +
            "<subfield code=\"t\">1</subfield>\n" +
            "<subfield code=\"x\">tr fr sci</subfield>\n" +
            "</datafield>\n" +
            "<datafield ind1=\"0\" ind2=\"0\" tag=\"876\">\n" +
            "<subfield code=\"0\">128532</subfield>\n" +
            "<subfield code=\"a\">123431</subfield>\n" +
            "<subfield code=\"h\"/>\n" +
            "<subfield code=\"j\">Not Charged</subfield>\n" +
            "<subfield code=\"p\">32101068878931</subfield>\n" +
            "<subfield code=\"t\">1</subfield>\n" +
            "<subfield code=\"x\">Shared</subfield>\n" +
            "<subfield code=\"z\">PA</subfield>\n" +
            "</datafield>\n" +
            "</record>\n" +
            "</collection>";

    @Test
    public void itemAvailabilityStatus() throws Exception {
        ItemAvailabityStatusRequest itemAvailabityStatus = new ItemAvailabityStatusRequest();
        List<String> barcodes = new ArrayList<>();
        barcodes.add("32101056185125");
        itemAvailabityStatus.setBarcodes(barcodes);
        Mockito.when(mockRestTemplate.postForObject(getScsbSolrClientUrl() + "/sharedCollection/itemAvailabilityStatus", itemAvailabityStatus, String.class)).thenReturn("Available");
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus)).thenCallRealMethod();
        ResponseEntity responseEntity1 = sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus);
        assertNotNull(responseEntity1);
        assertEquals("Available",responseEntity1.getBody());
        assertNotNull(itemAvailabityStatus.getBarcodes());
    }

    @Test
    public void itemAvailabilityStatus_Barcodecheck() throws Exception {
        ItemAvailabityStatusRequest itemAvailabityStatus = new ItemAvailabityStatusRequest();
        List<String> barcodes = new ArrayList<>();
        barcodes.add("32101056185125");
        itemAvailabityStatus.setBarcodes(barcodes);
        Mockito.when(mockRestTemplate.postForObject(getScsbSolrClientUrl() + "/sharedCollection/itemAvailabilityStatus", itemAvailabityStatus, String.class)).thenReturn(null);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus)).thenCallRealMethod();
        ResponseEntity responseEntity1 = sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus);
        assertNotNull(responseEntity1);
        assertEquals(ScsbCommonConstants.ITEM_BARCDE_DOESNOT_EXIST,responseEntity1.getBody());
        assertNotNull(itemAvailabityStatus.getBarcodes());
    }

    @Test
    public void itemAvailabilityStatus_Exception() throws Exception {
        ItemAvailabityStatusRequest itemAvailabityStatus = new ItemAvailabityStatusRequest();
        Mockito.when(sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus)).thenCallRealMethod();
        ResponseEntity responseEntity1 = sharedCollectionRestController.itemAvailabilityStatus(itemAvailabityStatus);
        assertEquals(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE,responseEntity1.getBody());
    }

    @Test
    public void testBibAvailableStatus(){
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        bibItemAvailabityStatusRequest.setBibliographicId("12365");
        bibItemAvailabityStatusRequest.setInstitutionId("1");
        Mockito.when(mockRestTemplate.postForObject(getScsbSolrClientUrl() + "/sharedCollection/bibAvailabilityStatus", bibItemAvailabityStatusRequest, String.class)).thenReturn("Available");
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(sharedCollectionRestController.bibAvailabilityStatus(bibItemAvailabityStatusRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.bibAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertNotNull(responseEntity);
        assertEquals("Available",responseEntity.getBody());
        assertNotNull(bibItemAvailabityStatusRequest.getBibliographicId());
        assertNotNull(bibItemAvailabityStatusRequest.getInstitutionId());

    }

    @Test
    public void testBibAvailableStatus_Exception(){
        BibItemAvailabityStatusRequest bibItemAvailabityStatusRequest = new BibItemAvailabityStatusRequest();
        Mockito.when(sharedCollectionRestController.bibAvailabilityStatus(bibItemAvailabityStatusRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.bibAvailabilityStatus(bibItemAvailabityStatusRequest);
        assertEquals(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE,responseEntity.getBody());
    }

    @Test
    public void deAccession() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode(itemBarcode);
        deAccessionItem.setDeliveryLocation("PB");
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        deAccessionRequest.setUsername("Test");
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(itemBarcode, ScsbCommonConstants.SUCCESS);
        Mockito.when(mockRestTemplate.postForObject(getScsbCircUrl() + "/sharedCollection/deAccession",deAccessionRequest, Map.class)).thenReturn(resultMap);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCircUrl()).thenReturn(scsbCircUrl);
        Mockito.when(sharedCollectionRestController.deAccession(deAccessionRequest)).thenCallRealMethod();
        ResponseEntity responseEntity1 = sharedCollectionRestController.deAccession(deAccessionRequest);
        assertNotNull(responseEntity1);
        Map<String, String> responseMap = (Map) responseEntity1.getBody();
        assertNotNull(responseMap);
        String status = responseMap.get(itemBarcode);
        assertNotNull(status);
        assertEquals(ScsbCommonConstants.SUCCESS,status);
        assertNotNull(deAccessionItem.getItemBarcode());
        assertNotNull(deAccessionItem.getDeliveryLocation());
        assertNotNull(deAccessionRequest.getDeAccessionItems());
        assertNotNull(deAccessionItem.getItemBarcode());
    }

    @Test
    public void deAccession_Exception() throws Exception {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        Mockito.when(sharedCollectionRestController.deAccession(deAccessionRequest)).thenCallRealMethod();
        ResponseEntity responseEntity1 = sharedCollectionRestController.deAccession(deAccessionRequest);
        assertEquals(ScsbConstants.SCSB_CIRC_SERVICE_UNAVAILABLE,responseEntity1.getBody());
    }

    @Test
    public void accessionBatch() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");
        accessionRequestList.add(accessionRequest);
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/accessionBatch",accessionModelRequest, String.class)).thenReturn(ScsbCommonConstants.SUCCESS);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.accessionBatch(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accessionBatch(accessionModelRequest);
        assertNotNull(responseEntity);
        assertEquals(ScsbCommonConstants.SUCCESS,responseEntity.getBody());
        assertNotNull(accessionRequest.getCustomerCode());
        assertNotNull(accessionRequest.getItemBarcode());
    }

    @Test
    public void accessionBatch_Exception() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(sharedCollectionRestController.accessionBatch(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accessionBatch(accessionModelRequest);
        assertEquals(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE,responseEntity.getBody());
    }

    @Test
    public void accession() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");
        accessionRequestList.add(accessionRequest);
        List<LinkedHashMap> linkedHashMapList = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("itemBarcode","32101095533293");
        linkedHashMap.put("message","Success");
        linkedHashMapList.add(linkedHashMap);
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/accession",accessionModelRequest, List.class)).thenReturn(linkedHashMapList);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.accession(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accession(accessionModelRequest);
        assertNotNull(responseEntity);
        assertEquals(linkedHashMapList,responseEntity.getBody());
        assertNotNull(accessionRequest.getCustomerCode());
        assertNotNull(accessionRequest.getItemBarcode());
    }

    @Test
    public void accession_Limit_Exceeded() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");
        accessionRequestList.add(accessionRequest);
        List<LinkedHashMap> linkedHashMapList = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("itemBarcode","32101095533293");
        linkedHashMap.put("message", ScsbConstants.ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE);
        linkedHashMapList.add(linkedHashMap);
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/accession",accessionModelRequest, List.class)).thenReturn(linkedHashMapList);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.accession(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accession(accessionModelRequest);
        assertNotNull(responseEntity);
        assertEquals(linkedHashMapList,responseEntity.getBody());
        assertNotNull(accessionRequest.getCustomerCode());
        assertNotNull(accessionRequest.getItemBarcode());
    }

    @Test
    public void accession_ResourceAccessException() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/accession",accessionModelRequest, List.class)).thenThrow(new ResourceAccessException(ScsbCommonConstants.SCSB_SOLR_CLIENT_SERVICE_UNAVAILABLE));
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.accession(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accession(accessionModelRequest);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity.getStatusCode());
    }

    @Test
    public void accession_Exception() throws Exception {
        List<AccessionRequest> accessionRequestList = new ArrayList<>();
        AccessionModelRequest accessionModelRequest=new AccessionModelRequest();
        accessionModelRequest.setAccessionRequests(accessionRequestList);
        Mockito.when(sharedCollectionRestController.accession(accessionModelRequest)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.accession(accessionModelRequest);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity.getStatusCode());
    }

    @Test
    public void submitCollection() throws Exception {
        mockRestTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        List<LinkedHashMap> linkedHashMapList = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("itemBarcode","32101068878931");
        linkedHashMap.put("message","SuccessRecord");
        linkedHashMapList.add(linkedHashMap);
        MultiValueMap<String,Object> requestParameter = new LinkedMultiValueMap();

        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.getLinkedMultiValueMap()).thenReturn((LinkedMultiValueMap) requestParameter);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/submitCollection",requestParameter, List.class)).thenReturn(linkedHashMapList);
        Mockito.when(sharedCollectionRestController.submitCollection(inputRecords,"PUL",false)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.submitCollection(inputRecords,"PUL",false);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    @Test
    public void submitCollection_Ifcheck() throws Exception {
        mockRestTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        List<LinkedHashMap> linkedHashMapList = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("itemBarcode","32101068878931");
        linkedHashMap.put("message", ScsbConstants.SUBMIT_COLLECTION_INTERNAL_ERROR);
        linkedHashMapList.add(linkedHashMap);
        MultiValueMap<String,Object> requestParameter = new LinkedMultiValueMap();

        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.getLinkedMultiValueMap()).thenCallRealMethod();
      //  Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/submitCollection",requestParameter, List.class)).thenReturn(linkedHashMapList);
        Mockito.doReturn(linkedHashMapList).when(mockRestTemplate).postForObject( ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<List>>any());
        Mockito.when(sharedCollectionRestController.submitCollection(inputRecords,"PUL",false)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.submitCollection(inputRecords,"PUL",false);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode());
    }

    @Test
    public void submitCollection_Exception() throws Exception {
        Mockito.when(sharedCollectionRestController.submitCollection(inputRecords,"PUL",false)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.submitCollection(inputRecords,"PUL",false);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity.getStatusCode());
    }

    @Test
    public void transferHoldings() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        HoldingsTransferRequest holdingsTransferRequestArrayList = new HoldingsTransferRequest();
        Source source = new Source();
        source.setOwningInstitutionBibId("53624");
        source.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setSource(source);
        Destination destination = new Destination();
        destination.setOwningInstitutionBibId("1421");
        destination.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setDestination(destination);
        List<HoldingsTransferRequest> HoldingsTransferRequest1 = new ArrayList<>();
        HoldingsTransferRequest1.add(holdingsTransferRequestArrayList);
        transferRequest.setHoldingTransfers(HoldingsTransferRequest1);
        TransferResponse transferResponse = new TransferResponse();
        transferResponse.setMessage("Success");
        HoldingTransferResponse holdingsTransferResponseArrayList =new HoldingTransferResponse();
        holdingsTransferResponseArrayList.setMessage("Successfully relinked");
        holdingsTransferResponseArrayList.setHoldingsTransferRequest(holdingsTransferRequestArrayList);
        List<HoldingTransferResponse> holdingTransferResponse = new ArrayList<>();
        holdingTransferResponse.add(holdingsTransferResponseArrayList);
        transferResponse.setHoldingTransferResponses(holdingTransferResponse);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(mockRestTemplate.postForObject(getScsbSolrClientUrl() + "transfer/processTransfer", transferRequest, TransferResponse.class)).thenReturn(transferResponse);
        Mockito.when(sharedCollectionRestController.transferHoldingsAndItems(transferRequest)).thenCallRealMethod();
        ResponseEntity responseEntity1= sharedCollectionRestController.transferHoldingsAndItems(transferRequest);
        assertNotNull(responseEntity1);
        assertEquals(HttpStatus.OK,responseEntity1.getStatusCode());
    }

    @Test
    public void transferHoldings_Exception() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setInstitution("PUL");
        HoldingsTransferRequest holdingsTransferRequestArrayList = new HoldingsTransferRequest();
        Source source = new Source();
        source.setOwningInstitutionBibId("53624");
        source.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setSource(source);
        Destination destination = new Destination();
        destination.setOwningInstitutionBibId("1421");
        destination.setOwningInstitutionHoldingsId("59753");
        holdingsTransferRequestArrayList.setDestination(destination);
        List<HoldingsTransferRequest> HoldingsTransferRequest1 = new ArrayList<>();
        HoldingsTransferRequest1.add(holdingsTransferRequestArrayList);
        transferRequest.setHoldingTransfers(HoldingsTransferRequest1);
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenReturn(scsbSolrClientUrl);
        Mockito.when(mockRestTemplate.postForObject(getScsbSolrClientUrl() + "transfer/processTransfer", transferRequest, TransferResponse.class)).thenThrow(new RestClientException(""));
        Mockito.when(sharedCollectionRestController.transferHoldingsAndItems(transferRequest)).thenCallRealMethod();
        ResponseEntity responseEntity1= sharedCollectionRestController.transferHoldingsAndItems(transferRequest);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,responseEntity1.getStatusCode());
    }

    @Test
    public void checkGetterServices(){
        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenCallRealMethod();
        Mockito.when(sharedCollectionRestController.getScsbSolrClientUrl()).thenCallRealMethod();
        Mockito.when(sharedCollectionRestController.getScsbCircUrl()).thenCallRealMethod();
        assertNotEquals(sharedCollectionRestController.getRestTemplate(),mockRestTemplate);
        assertNotEquals(sharedCollectionRestController.getScsbSolrClientUrl(),scsbSolrClientUrl);
        assertNotEquals(sharedCollectionRestController.getScsbCircUrl(),scsbCircUrl);

    }

    @Test
    public void submitCollection_exception() throws Exception {
        mockRestTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        List<LinkedHashMap> linkedHashMapList = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("itemBarcode","32101068878931");
        linkedHashMap.put("message",null);
        linkedHashMapList.add(linkedHashMap);
        MultiValueMap<String,Object> requestParameter = new LinkedMultiValueMap();

        Mockito.when(sharedCollectionRestController.getRestTemplate()).thenReturn(mockRestTemplate);
        Mockito.when(sharedCollectionRestController.getScsbCoreUrl()).thenReturn(scsbCoreUrl);
        Mockito.when(sharedCollectionRestController.getLinkedMultiValueMap()).thenReturn(null);
        Mockito.when(mockRestTemplate.postForObject(getScsbCoreUrl() + "sharedCollection/submitCollection",requestParameter, List.class)).thenReturn(null);
        Mockito.when(sharedCollectionRestController.submitCollection(inputRecords,"PUL",false)).thenCallRealMethod();
        ResponseEntity responseEntity = sharedCollectionRestController.submitCollection(inputRecords,"PUL",false);
        assertNotNull(responseEntity);
    }

}