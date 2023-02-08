package org.recap.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.controller.swagger.RequestItemRestController;
import org.recap.entity.ItemRequestReceivedInformationEntity;
import org.recap.model.ItemRequestInformation;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Chittoor Charan Raj
 */

public class RequestItemServiceUT extends BaseTestCase {

    @InjectMocks
    RequestItemService service;

    @Mock
    ItemRequestInformationRepository repository;

    @Mock
    RequestItemRestController controller;


    @Test
    public void getFromDateTest() {
        Date fromDate = service.getFromDate(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String dateString = simpleDateFormat.format(fromDate);
        assertNotNull(dateString.contains("12:00 AM"));
    }

    @Test
    public void getToDateTest() {
        Date toDate = service.getToDate(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        String dateString = simpleDateFormat.format(toDate);
        assertNotNull(dateString.contains("11:59 PM"));
    }


    @Test
    public void prepareItemRequestReceivedInformationEntityTest() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("46447");
        itemBarcodes.add("346767");
        itemRequestInfo.setItemBarcodes(itemBarcodes);
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode(String.join(",", "56453"));
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestRecieved("765765");
        itemRequestReceivedInformationEntity.setStatus("NA");
        itemRequestReceivedInformationEntity.setDate(new Date());
        ReflectionTestUtils.invokeMethod(service, "prepareItemRequestReceivedInformationEntity", itemRequestInfo, itemRequestReceivedInformationEntity);
    }

    @Test
    public void convertStringToDateTestException() {
        String sourceDate = "2022-10-22";
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dt.parse(sourceDate);
        } catch (ParseException e) {

        }
        assertNotNull(date);
        ReflectionTestUtils.invokeMethod(service, "convertStringToDate", sourceDate);

    }

    @Test
    public void prepareItemRequestInformationFromEntityTest() {
        try {
            ReflectionTestUtils.invokeMethod(service, "prepareItemRequestInformationFromEntity", new ItemRequestReceivedInformationEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateReceivedRequestInformationTest() {
        service.updateReceivedRequestInformation(new ItemRequestInformation(), true);
    }

    @Test
    public void updateReceivedRequestInformationException() {
        service.updateReceivedRequestInformation(new ItemRequestInformation(), false);
    }

    @Test
    public void updateItemRequestTest() {
        service.updateItemRequest(new ItemRequestInformation());
    }

    @Test
    public void prepareItemInfoFromrequestTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setId(2);
            Mockito.when(repository.findById(any())).thenReturn(Optional.of(getItemRequestReceivedInformationEntity()));
            ReflectionTestUtils.invokeMethod(service, "prepareItemInfoFromrequest", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void prepareRequestReponseFromEnityListTest() {
        Iterator<ItemRequestReceivedInformationEntity> iterator = mock(Iterator.class);
        List<ItemRequestReceivedInformationEntity> entityList = mock(List.class);
        ItemRequestReceivedInformationEntity entity = mock(ItemRequestReceivedInformationEntity.class);
        when(entityList.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(entity);
        ReflectionTestUtils.invokeMethod(service, "prepareRequestReponseFromEnityList", entityList, new RequestLogReportRequest());
    }

    @Test
    public void getRequestReportsTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            Page<ItemRequestReceivedInformationEntity> pageResponse = Mockito.mock(Page.class);
            List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
            ItemRequestReceivedInformationEntity information = new ItemRequestReceivedInformationEntity();
            entityList.add(information);
            ReflectionTestUtils.setField(service, "pageResponse", pageResponse);
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsWithStatusTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsWithoutInstutionTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setInstitution(null);
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getRequestReportsWithoutInstutionEmptyTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getRequestReportsWithoutStatusTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setStatus(null);
            requestLogReportRequest.setInstitution(null);
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsWithoutEmptyStatusTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setStatus("");
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsNull() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setFromDate("");
            requestLogReportRequest.setToDate("");
            assertTrue(requestLogReportRequest.getFromDate().isEmpty());
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private RequestLogReportRequest getRequestLogReportRequest() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        requestLogReportRequest.setInstitution("TEST");
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String format = dt.format(date);
        requestLogReportRequest.setFromDate(format);
        requestLogReportRequest.setToDate(format);
        return requestLogReportRequest;
    }


    @Test
    public void getRequestReportsGetInstitution_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("TEST");
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsGetInstitution_IsBlank() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution(null);
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsGetInstitution_IsEmpty() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            requestLogReportRequest.setStatus("FAILED");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getRequestReportsInstitution_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("TEST");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsBlank_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsNUll_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution(null);
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getRequestReportsStatusBlank_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setStatus("");
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsStatusNull_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setStatus(null);
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestReportsStatusIsEmpty_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setStatus("");
            assertTrue(requestLogReportRequest.getStatus().isEmpty());
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestsTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequestTest();
            List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
            ItemRequestReceivedInformationEntity entity = getItemRequestReceivedInformationEntity();
            entityList.add(entity);
            ReflectionTestUtils.setField(service, "entityList", entityList);
            service.getRequests(requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRequestRepotTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            Date date = new Date();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            String format = dt.format(date);
            requestLogReportRequest.setFromDate(format);
            requestLogReportRequest.setToDate(format);
            requestLogReportRequest.setInstitution("TEST");
            requestLogReportRequest.setStatus("FAILED");
            requestLogReportRequest.setTotalRecordsCount(11);
            requestLogReportRequest.setTotalPageCount(1);
            Page<ItemRequestReceivedInformationEntity> pageReponse = Mockito.mock(Page.class);
            List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
            ItemRequestReceivedInformationEntity entity = getItemRequestReceivedInformationEntity();
            entityList.add(entity);
            Mockito.when(pageReponse.getContent()).thenReturn(entityList);
            Mockito.when(pageReponse.getTotalPages()).thenReturn(1);
            Mockito.when(pageReponse.getTotalElements()).thenReturn(1l);
            ReflectionTestUtils.invokeMethod(service, "getRequestReports", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ItemRequestReceivedInformationEntity getItemRequestReceivedInformationEntity() {
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode("12445");
        itemRequestReceivedInformationEntity.setDate(new Date());
        itemRequestReceivedInformationEntity.setStatus("FAILED");
        itemRequestReceivedInformationEntity.setId(2);
        return itemRequestReceivedInformationEntity;
    }


    private RequestLogReportRequest getRequestLogReportRequestTest() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        requestLogReportRequest.setInstitution("TEST");
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String format = dt.format(date);
        requestLogReportRequest.setFromDate(format);
        requestLogReportRequest.setToDate(format);
        return requestLogReportRequest;
    }

    @Test
    public void saveReceivedRequestInformationTest() {
        try {
            ItemRequestInformation itemRequestInfo = getItemRequestInformation();
            ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
            itemRequestReceivedInformationEntity.setStatus("SUCCESS");
            itemRequestReceivedInformationEntity.setStatusId(1);
            boolean isResponseReceived = true;
            Mockito.when(repository.save(Mockito.any())).thenReturn(itemRequestReceivedInformationEntity);
            service.saveReceivedRequestInformation(itemRequestInfo,"", isResponseReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void saveReceivedRequestInformationsTest() {
        try {
            ItemRequestInformation itemRequestInfo = getItemRequestInformation();
            ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
            itemRequestReceivedInformationEntity.setStatus("FAILED");
            itemRequestReceivedInformationEntity.setStatusId(2);
            boolean isResponseReceived = false;
            Mockito.when(repository.save(Mockito.any())).thenReturn(itemRequestReceivedInformationEntity);
            service.saveReceivedRequestInformation(itemRequestInfo,"", isResponseReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void getCountTestDate() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCountException() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setInstitution(null);
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getCountexception() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCountexceptionTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
            assertTrue(requestLogReportRequest.getInstitution().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCountTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setFromDate("");
            requestLogReportRequest.setToDate("");
            assertTrue(requestLogReportRequest.getFromDate().isEmpty());
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getCountInstitution_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("TEST");
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCountBlank_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getCountEmpty_Test() {
        try {
            RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
            requestLogReportRequest.setInstitution("");
            ReflectionTestUtils.invokeMethod(service, "getCount", requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemOwningInstitution("TEST");
        List<String> itemBarcodes = new ArrayList<>();
        itemBarcodes.add("1244");
        itemBarcodes.add("6765");
        itemRequestInformation.setItemBarcodes(itemBarcodes);
        itemRequestInformation.setRequestingInstitution("TEST");
        return itemRequestInformation;
    }

    @Test
    public void submitRequestTest() {
        try {
            RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
            requestLogReportRequest.setId(1);
            requestLogReportRequest.setStatus("Resubmit is successfull");
            ReflectionTestUtils.invokeMethod(service, "submitRequests", requestLogReportRequest);
            service.submitRequests(requestLogReportRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


