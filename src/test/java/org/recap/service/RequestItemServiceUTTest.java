package org.recap.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.controller.swagger.RequestItemRestController;
import org.recap.entity.ItemRequestReceivedInformationEntity;
import org.recap.model.ItemRequestInformation;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Dinakar N created on 08/11/22
 */

public class RequestItemServiceUTTest extends BaseTestCaseUT {
    @InjectMocks
    RequestItemService requestItemService;
    @Mock
    ItemRequestInformationRepository itemRequestInformationRepository;

    @Mock
    ItemRequestInformation itemRequestInformation;

    @Mock
    RequestItemRestController requestItemRestController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(requestItemService, "itemRequestInformationRepository", itemRequestInformationRepository);
        ReflectionTestUtils.setField(requestItemService, "requestItemRestController", requestItemRestController);
    }

    @Test
    public void testSubmitRequest(){
        Optional<ItemRequestReceivedInformationEntity> itemRequestReceivedInformationEntity = getItemRequestReceivedInformationEntity();
        Mockito.when(itemRequestInformationRepository.findById(Mockito.any(Integer.class))).thenReturn(itemRequestReceivedInformationEntity);
        requestItemService.submitRequests(getRequestLogReportRequest());
    }
    public RequestLogReportRequest getRequestLogReportRequest() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        requestLogReportRequest.setInstitution("TEST");
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        String format = dt.format(date);
        requestLogReportRequest.setFromDate(format);
        requestLogReportRequest.setToDate(format);
        requestLogReportRequest.setId(1);
        return requestLogReportRequest;
    }
    private Optional<ItemRequestReceivedInformationEntity> getItemRequestReceivedInformationEntity() {
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode("12445");
        itemRequestReceivedInformationEntity.setDate(new Date());
        itemRequestReceivedInformationEntity.setStatus("FAILED");
        itemRequestReceivedInformationEntity.setId(2);
        itemRequestReceivedInformationEntity.setRequestRecieved("{\"itemBarcodes\":[\"465654\"],\"titleIdentifier\":\"\\u003cPushto poems. 2002-2003\\u003e.      \",\"itemOwningInstitution\":\"TEST\",\"patronBarcode\":\"8765124321\",\"emailAddress\":\"\",\"requestingInstitution\":\"TEST\",\"requestType\":\"RETRIEVAL\",\"deliveryLocation\":\"TEST\",\"requestNotes\":\"\",\"author\":\"\",\"startPage\":\"\",\"endPage\":\"\",\"chapterTitle\":\"\",\"username\":\"dinakartest\",\"issue\":\"\",\"volume\":\"\"}");
        return Optional.of(itemRequestReceivedInformationEntity);
    }

    @Test
    public void testSubmitRequestWithZeroId(){
        List<ItemRequestReceivedInformationEntity> list = getItemRequestReceivedInformationEntityList();
        Page<ItemRequestReceivedInformationEntity> pageResponse = new PageImpl<>(list);
        Mockito.when(itemRequestInformationRepository.findAllByStatusId(Mockito.any(Pageable.class),Mockito.any(Integer.class))).thenReturn(pageResponse);
        requestItemService.submitRequests(getRequestLogReportRequestNoBody());
    }
    public RequestLogReportRequest getRequestLogReportRequestNoBody() {
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();
        requestLogReportRequest.setId(0);
        requestLogReportRequest.setPageNumber(1);
        requestLogReportRequest.setPageSize(1);
        return requestLogReportRequest;
    }
    private List<ItemRequestReceivedInformationEntity> getItemRequestReceivedInformationEntityList() {
        List<ItemRequestReceivedInformationEntity> list = new ArrayList<>();
        ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
        itemRequestReceivedInformationEntity.setRequestInstitution("TEST");
        itemRequestReceivedInformationEntity.setItemOwningInstitution("TEST");
        itemRequestReceivedInformationEntity.setRequestedItemBarcode("12445");
        itemRequestReceivedInformationEntity.setDate(new Date());
        itemRequestReceivedInformationEntity.setStatus("FAILED");
        itemRequestReceivedInformationEntity.setId(2);
        itemRequestReceivedInformationEntity.setRequestRecieved("{\"itemBarcodes\":[\"23555\"],\"titleIdentifier\":\"\\u003cPushto poems. 2002-2003\\u003e.      \",\"itemOwningInstitution\":\"TEST\",\"patronBarcode\":\"457688\",\"emailAddress\":\"\",\"requestingInstitution\":\"TEST\",\"requestType\":\"RETRIEVAL\",\"deliveryLocation\":\"PW\",\"requestNotes\":\"\",\"author\":\"\",\"startPage\":\"\",\"endPage\":\"\",\"chapterTitle\":\"\",\"username\":\"dinakartest\",\"issue\":\"\",\"volume\":\"\"}");
        list.add(itemRequestReceivedInformationEntity);
        return list;
    }

}


