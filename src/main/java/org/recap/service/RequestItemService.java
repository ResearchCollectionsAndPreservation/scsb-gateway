package org.recap.service;

import com.google.gson.Gson;
import org.recap.ScsbConstants;
import org.recap.controller.swagger.RequestItemRestController;
import org.recap.entity.ItemRequestReceivedInformationEntity;
import org.recap.model.ItemRequestInformation;
import org.recap.model.request.RequestInfo;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.repository.ItemRequestInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Dinakar N created on 14/09/22
 */
@Service
public class RequestItemService {
    @Autowired
    private ItemRequestInformationRepository itemRequestInformationRepository;

    @Autowired
    private RequestItemRestController requestItemRestController;

     public void saveReceivedRequestInformation(ItemRequestInformation itemRequestInfo, boolean isResponseReceived) {
         ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
         prepareItemRequestReceivedInformationEntity(itemRequestInfo,itemRequestReceivedInformationEntity);
         if (isResponseReceived) {
             itemRequestReceivedInformationEntity.setStatus("SUCCESS");
             itemRequestReceivedInformationEntity.setStatusId(1);
         } else {
             itemRequestReceivedInformationEntity.setStatus("FAILED");
             itemRequestReceivedInformationEntity.setStatusId(2);
         }
         itemRequestInformationRepository.save(itemRequestReceivedInformationEntity);
    }

    private void prepareItemRequestReceivedInformationEntity(ItemRequestInformation itemRequestInfo, ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity) {
        Gson gson = new Gson();
        itemRequestReceivedInformationEntity.setItemOwningInstitution(itemRequestInfo.getItemOwningInstitution());
        itemRequestReceivedInformationEntity.setRequestedItemBarcode(String.join(",", itemRequestInfo.getItemBarcodes()));
        itemRequestReceivedInformationEntity.setRequestInstitution(itemRequestInfo.getRequestingInstitution());
        itemRequestReceivedInformationEntity.setRequestRecieved(gson.toJson(itemRequestInfo));
        itemRequestReceivedInformationEntity.setStatus("NA");
        itemRequestReceivedInformationEntity.setDate(new Date());
    }

    public RequestLogReportRequest getRequests(RequestLogReportRequest requestLogReportRequest) {
        List<ItemRequestReceivedInformationEntity> entityList = getRequestReports(requestLogReportRequest);
        return prepareRequestReponseFromEnityList(entityList, requestLogReportRequest);
    }

    public RequestLogReportRequest submitRequests(RequestLogReportRequest requestLogReportRequest) {
         Integer count = 0;
         Boolean multiSubmit = false;
        if (requestLogReportRequest.getId() != 0) {
            ItemRequestInformation itemRequestInformation = prepareItemInfoFromrequest(requestLogReportRequest);

            try {
                if (itemRequestInformation != null)
                    requestItemRestController.itemSubmitRequest(itemRequestInformation);
                requestLogReportRequest.setStatus(ScsbConstants.SUCCESS);
            } catch (Exception e) {
                requestLogReportRequest.setStatus(ScsbConstants.FAILED);
            }

        } else {
            List<ItemRequestInformation> itemRequestInformationList = new ArrayList<>();
            Page<ItemRequestReceivedInformationEntity> pageResponse = getCount(requestLogReportRequest);
            requestLogReportRequest.setPageSize(500);
            multiSubmit = true;
            for (int i = 0; i < pageResponse.getTotalPages(); i++) {
                List<ItemRequestReceivedInformationEntity> entityList = getCount(requestLogReportRequest).getContent();
                for (ItemRequestReceivedInformationEntity entity : entityList) {
                    ItemRequestInformation itemRequestInformation = prepareItemRequestInformationFromEntity(entity);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        if (itemRequestInformation != null)
                            requestItemRestController.itemSubmitRequest(itemRequestInformation);
                    } catch (Exception e){
                        count++;
                    }
                }
            }
        }
        if(multiSubmit && count > 0){
            requestLogReportRequest.setStatus(ScsbConstants.PARTIALLY);
        } else if(multiSubmit){
            requestLogReportRequest.setStatus(ScsbConstants.SUCCESS);
        }
        return requestLogReportRequest;
    }

    private Page<ItemRequestReceivedInformationEntity> getCount(RequestLogReportRequest requestLogReportRequest) {
         requestLogReportRequest.setPageSize(500);
        Pageable pageable = PageRequest.of(requestLogReportRequest.getPageNumber(), requestLogReportRequest.getPageSize());
        Page<ItemRequestReceivedInformationEntity> pageReponse = null;
        if(requestLogReportRequest.getFromDate() != null && !requestLogReportRequest.getFromDate().isEmpty()) {
            Date requestFromDate = convertStringToDate(requestLogReportRequest.getFromDate());
            Date requestToDate = convertStringToDate(requestLogReportRequest.getToDate());
            Date fromDate = getFromDate(requestFromDate);
            Date toDate = getToDate(requestToDate);
            if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndStatusAndFromDateAndEndDate(pageable, requestLogReportRequest.getInstitution(), "FAILED",fromDate,toDate);
            } else {
                pageReponse = itemRequestInformationRepository.findByStatusIdAndFromDateAndEndDate(pageable, 2,fromDate,toDate);
            }
        } else {
            if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndStatus(pageable, requestLogReportRequest.getInstitution(), "FAILED");
            } else {
                pageReponse = itemRequestInformationRepository.findAllByStatusId(pageable, 2);
            }
        }
       return pageReponse;
    }

    private List<ItemRequestReceivedInformationEntity> getRequestReports(RequestLogReportRequest requestLogReportRequest) {
        List<ItemRequestReceivedInformationEntity> entityList = new ArrayList<>();
        Pageable pageable = PageRequest.of(requestLogReportRequest.getPageNumber(), requestLogReportRequest.getPageSize());
        Page<ItemRequestReceivedInformationEntity> pageReponse = null;
        if (requestLogReportRequest.getFromDate() != null && !requestLogReportRequest.getFromDate().isEmpty()) {
            Date requestFromDate = convertStringToDate(requestLogReportRequest.getFromDate());
            Date requestToDate = convertStringToDate(requestLogReportRequest.getToDate());
            Date fromDate = getFromDate(requestFromDate);
            Date toDate = getToDate(requestToDate);

            if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()
                    && requestLogReportRequest.getStatus() != null && !requestLogReportRequest.getStatus().isBlank() && !requestLogReportRequest.getStatus().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndStatusAndFromDateAndEndDate(pageable, requestLogReportRequest.getInstitution(), requestLogReportRequest.getStatus(), fromDate, toDate);
            } else if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndFromDateAndEndDate(pageable, requestLogReportRequest.getInstitution(), fromDate, toDate);
            } else if (requestLogReportRequest.getStatus() != null && !requestLogReportRequest.getStatus().isBlank() && !requestLogReportRequest.getStatus().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByStatusAndFromDateAndEndDate(pageable, requestLogReportRequest.getStatus(), fromDate, toDate);
            } else {
                pageReponse = itemRequestInformationRepository.findByFromDateAndEndDate(pageable, fromDate, toDate);
            }
        } else {
            if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()
                    && requestLogReportRequest.getStatus() != null && !requestLogReportRequest.getStatus().isBlank() && !requestLogReportRequest.getStatus().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndStatus(pageable, requestLogReportRequest.getInstitution(), requestLogReportRequest.getStatus());
            } else if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitution(pageable, requestLogReportRequest.getInstitution());
            } else if (requestLogReportRequest.getStatus() != null && !requestLogReportRequest.getStatus().isBlank() && !requestLogReportRequest.getStatus().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByStatus(pageable, requestLogReportRequest.getStatus());
            } else {
                pageReponse = itemRequestInformationRepository.findAllByOrderByDateDesc(pageable);
            }
        }
        entityList = pageReponse.getContent();
        requestLogReportRequest.setTotalPageCount(pageReponse.getTotalPages());
        requestLogReportRequest.setTotalRecordsCount(pageReponse.getTotalElements());
        return entityList;
    }

    private RequestLogReportRequest prepareRequestReponseFromEnityList(List<ItemRequestReceivedInformationEntity> entityList, RequestLogReportRequest requestLogReportRequest) {
         List<RequestInfo> requestInfoList = new ArrayList<>();
        for (ItemRequestReceivedInformationEntity entity : entityList) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setRequestInstitution(entity.getRequestInstitution());
            requestInfo.setItemOwningInstitution(entity.getItemOwningInstitution());
            requestInfo.setRequestedItemBarcode(entity.getRequestedItemBarcode());
            requestInfo.setDate(entity.getDate());
            requestInfo.setStatus(entity.getStatus());
            requestInfo.setId(entity.getId());
            requestInfoList.add(requestInfo);
        }
        requestLogReportRequest.setRequestInfoList(requestInfoList);
        return requestLogReportRequest;
    }
    @Transactional
    public void updateItemRequest(ItemRequestInformation itemRequestInfo) {
        updateReceivedRequestInformation(itemRequestInfo,Boolean.TRUE);
    }

    public void updateReceivedRequestInformation(ItemRequestInformation itemRequestInfo, boolean isResponseReceived) {
        if (isResponseReceived)
            itemRequestInformationRepository.update(itemRequestInfo.getId(),"SUCCESS",1);
    }

    private ItemRequestInformation prepareItemInfoFromrequest(RequestLogReportRequest requestLogReportRequest) {
        Optional<ItemRequestReceivedInformationEntity> itemRequestReceivedInformationEntity = itemRequestInformationRepository.findById(requestLogReportRequest.getId());
        ItemRequestInformation itemRequestInformation = null;
        if (itemRequestReceivedInformationEntity.isPresent()) {
            String requestRecieved = itemRequestReceivedInformationEntity.get().getRequestRecieved();
            Gson gsonObj = new Gson();
            itemRequestInformation = gsonObj.fromJson(requestRecieved, ItemRequestInformation.class);
        }
        itemRequestInformation.setId(requestLogReportRequest.getId());
        return itemRequestInformation;
    }

    private ItemRequestInformation prepareItemRequestInformationFromEntity(ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity) {
        ItemRequestInformation itemRequestInformation = null;
        String requestRecieved = itemRequestReceivedInformationEntity.getRequestRecieved();
        Gson gsonObj = new Gson();
        itemRequestInformation = gsonObj.fromJson(requestRecieved, ItemRequestInformation.class);
        itemRequestInformation.setId(itemRequestReceivedInformationEntity.getId());
        return itemRequestInformation;
    }
    private Date convertStringToDate(String stringDate){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dt.parse(stringDate);
        } catch (ParseException e) {}
        return date;
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        return cal.getTime();
    }
    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(11, 23);
        cal.set(12, 59);
        cal.set(13, 59);
        return cal.getTime();
    }
}
