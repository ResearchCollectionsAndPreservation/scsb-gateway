package org.recap.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.recap.ScsbConstants;
import org.recap.controller.swagger.RequestItemRestController;
import org.recap.model.jpa.ItemRequestReceivedInformationEntity;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

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
@Slf4j
public class RequestItemService {

    @Autowired
    private ItemRequestInformationRepository itemRequestInformationRepository;

    @Autowired
    private RequestItemRestController requestItemRestController;

    public void saveReceivedRequestInformation(ItemRequestInformation itemRequestInfo,String responseMessage, boolean isResponseReceived) {
        try {
            ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity = new ItemRequestReceivedInformationEntity();
            prepareItemRequestReceivedInformationEntity(itemRequestInfo, itemRequestReceivedInformationEntity);
            if (isResponseReceived) {
                itemRequestReceivedInformationEntity.setStatus(ScsbConstants.SUCCESS);
                itemRequestReceivedInformationEntity.setStatusId(1);
            } else {
                itemRequestReceivedInformationEntity.setStatus(ScsbConstants.FAILURE);
                itemRequestReceivedInformationEntity.setStatusId(2);
            }
            responseMessage = responseMessage.replace("400 : ","");
            responseMessage = responseMessage.replaceAll("\"", "");
            itemRequestReceivedInformationEntity.setResponseMessage(responseMessage);
            itemRequestInformationRepository.save(itemRequestReceivedInformationEntity);
        } catch (Exception e) {
            log.info(ScsbConstants.REQUEST_LOG_EXCEPTOIN_SAVE, e.getMessage());
        }
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
        if (requestLogReportRequest.getGatewayRequestLogId() != 0) {
            ItemRequestInformation itemRequestInformation = prepareItemInfoFromrequest(requestLogReportRequest);
            try {
                if (itemRequestInformation != null)
                    requestItemRestController.itemSubmitRequest(itemRequestInformation,requestLogReportRequest.getGatewayRequestLogId());
                requestLogReportRequest.setStatus(ScsbConstants.SUCCESS);
            } catch (HttpClientErrorException e) {
                requestLogReportRequest.setStatus(ScsbConstants.SUCCESS);
                return requestLogReportRequest;
            } catch (RestClientException e) {
                requestLogReportRequest.setStatus(ScsbConstants.FAILURE);
                return requestLogReportRequest;
            } catch (Exception e) {
                requestLogReportRequest.setStatus(ScsbConstants.FAILURE);
                return requestLogReportRequest;
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
                            requestItemRestController.itemSubmitRequest(itemRequestInformation,requestLogReportRequest.getGatewayRequestLogId());
                    } catch (RestClientException e) {
                        requestLogReportRequest.setStatus(ScsbConstants.FAILURE);
                        return requestLogReportRequest;
                    } catch (Exception e) {
                        count++;
                    }
                }
            }
        }
        if (multiSubmit && count > 0) {
            requestLogReportRequest.setStatus(ScsbConstants.PARTIALLY);
        } else if (multiSubmit) {
            requestLogReportRequest.setStatus(ScsbConstants.SUCCESS);
        }
        return requestLogReportRequest;
    }

    private Page<ItemRequestReceivedInformationEntity> getCount(RequestLogReportRequest requestLogReportRequest) {
        requestLogReportRequest.setPageSize(500);
        Pageable pageable = PageRequest.of(requestLogReportRequest.getPageNumber(), requestLogReportRequest.getPageSize());
        Page<ItemRequestReceivedInformationEntity> pageReponse = null;
        if (requestLogReportRequest.getFromDate() != null && !requestLogReportRequest.getFromDate().isEmpty()) {
            Date requestFromDate = convertStringToDate(requestLogReportRequest.getFromDate());
            Date requestToDate = convertStringToDate(requestLogReportRequest.getToDate());
            Date fromDate = getFromDate(requestFromDate);
            Date toDate = getToDate(requestToDate);
            if (requestLogReportRequest.getInstitution() != null && !requestLogReportRequest.getInstitution().isBlank() && !requestLogReportRequest.getInstitution().isEmpty()) {
                pageReponse = itemRequestInformationRepository.findByInstitutionAndStatusAndFromDateAndEndDate(pageable, requestLogReportRequest.getInstitution(), "FAILED", fromDate, toDate);
            } else {
                pageReponse = itemRequestInformationRepository.findByStatusIdAndFromDateAndEndDate(pageable, 2, fromDate, toDate);
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
        Date fromDate = null;
        Date toDate = null;
        if (requestLogReportRequest.getFromDate() != null && !requestLogReportRequest.getFromDate().isEmpty()) {
            Date requestFromDate = convertStringToDate(requestLogReportRequest.getFromDate());
            Date requestToDate = convertStringToDate(requestLogReportRequest.getToDate());
            fromDate = getFromDate(requestFromDate);
            toDate = getToDate(requestToDate);
        }
        try {
            pageReponse = itemRequestInformationRepository.findByInstitutionAndStatusAndFromDateAndEndDateAndValidationStatus(pageable, requestLogReportRequest.getInstitution(), requestLogReportRequest.getStatus(), fromDate, toDate,requestLogReportRequest.getValidationStatus());
        } catch (Exception e) {
            log.info(ScsbConstants.REQUEST_LOG_EXCEPTION_PULL,e.getMessage());
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
            requestInfo.setValidationMessage(entity.getValidationStatus());
            requestInfo.setResponseMessage(entity.getResponseMessage());
            requestInfo.setDate(entity.getDate());
            requestInfo.setStatus(entity.getStatus());
            requestInfo.setId(entity.getId());
            requestInfoList.add(requestInfo);
        }
        requestLogReportRequest.setRequestInfoList(requestInfoList);
        return requestLogReportRequest;
    }

    @Transactional
    public void updateItemRequest(String responseMessage,Integer requestLogId) {
        updateReceivedRequestInformation(responseMessage,Boolean.TRUE,requestLogId);
    }

    public void updateReceivedRequestInformation(String responseMessage,boolean isResponseReceived, Integer requestLogId) {
        try {
            if (isResponseReceived)
                itemRequestInformationRepository.update(responseMessage,requestLogId, ScsbConstants.SUCCESS, 1, new Date());
        } catch (Exception e) {
            log.info(ScsbConstants.REQUEST_LOG_EXCEPTOIN_UPDATE, e.getMessage());
        }
    }

    private ItemRequestInformation prepareItemInfoFromrequest(RequestLogReportRequest requestLogReportRequest) {
        Optional<ItemRequestReceivedInformationEntity> itemRequestReceivedInformationEntity = itemRequestInformationRepository.findById(requestLogReportRequest.getGatewayRequestLogId());
        ItemRequestInformation itemRequestInformation = null;
        if (itemRequestReceivedInformationEntity.isPresent()) {
            String requestRecieved = itemRequestReceivedInformationEntity.get().getRequestRecieved();
            Gson gsonObj = new Gson();
            itemRequestInformation = gsonObj.fromJson(requestRecieved, ItemRequestInformation.class);
        }
        return itemRequestInformation;
    }

    private ItemRequestInformation prepareItemRequestInformationFromEntity(ItemRequestReceivedInformationEntity itemRequestReceivedInformationEntity) {
        ItemRequestInformation itemRequestInformation = null;
        String requestRecieved = itemRequestReceivedInformationEntity.getRequestRecieved();
        Gson gsonObj = new Gson();
        itemRequestInformation = gsonObj.fromJson(requestRecieved, ItemRequestInformation.class);
        return itemRequestInformation;
    }

    private Date convertStringToDate(String stringDate) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dt.parse(stringDate);
        } catch (ParseException e) {
        }
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
