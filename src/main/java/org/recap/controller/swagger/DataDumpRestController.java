package org.recap.controller.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.controller.AbstractController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 19/8/16.
 */
@RestController
@RequestMapping("/dataDump")
@Api(value="dataDump")
public class DataDumpRestController extends AbstractController  {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpRestController.class);

   /**
     * This method is the entry point to start data export process and passes to request to the scsb-etl microservice.
     *
     * @param institutionCodes          the institution codes
     * @param requestingInstitutionCode the requesting institution code
     * @param fetchType                 the fetch type
     * @param outputFormat              the output format
     * @param date                      the date
     * @param collectionGroupIds        the collection group ids
     * @param transmissionType          the transmission type
     * @param emailToAddress            the email to address
     * @return the response entity
     */
    @GetMapping("/exportDataDump")
    @ApiOperation(value = "exportDataDump",
            notes = "The Export Data Dump API allows export of bibliographic records in SCSB database into MARCXML or SCSBXML format. This is used by partners to export records in preferred format and update their respective discovery systems. These jobs are scheduled to run by HTC support.", nickname = "exportDataDump", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = RecapConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public ResponseEntity exportDataDump(@ApiParam(value = "${swagger.values.institutionCodes}" , required = true, name = "institutionCodes") @RequestParam String institutionCodes,
                                         @ApiParam(value = "${swagger.values.requestingInstitutionCode}",required=true, name = "requestingInstitutionCode") @RequestParam String requestingInstitutionCode,
                                         @ApiParam(value = "${swagger.values.imsDepositoryCodes}", name = "imsDepositoryCodes") @RequestParam(required=false) String imsDepositoryCodes,
                                         @ApiParam(value = "Type of export - Incremental (use 1) or Deleted (use 2) or Full Dump (use 10)" , required = true , name = "fetchType") @RequestParam String fetchType,
                                         @ApiParam(value = "Type of format - Marc xml (use 0) or SCSB xml (use 1), for deleted records only json format (use 2)",required=true, name = "outputFormat") @RequestParam String outputFormat,
                                         @ApiParam(value = "Get updates to middleware collection since the date provided. Date format will be a string (yyyy-MM-dd HH:mm) and is Eastern Time.",name = "date") @RequestParam(required = false) String date,
                                         @ApiParam(value = "Data can be requested by Collection Group ID, either Shared (use 1) or Open (use 2) or Private (use 3). Default is Shared and Open, can use 1,2,3 as well.", name = "collectionGroupIds") @RequestParam(required=false) String collectionGroupIds,
                                         @ApiParam(value = "Type of transmission - for FTP use 0, for HTTP response use 1. Default is FTP.", name = "transmissionType")@RequestParam(required=false) String transmissionType,
                                         @ApiParam(value = "Email address to whom email will be sent upon completion" , name = "emailToAddress")@RequestParam(required=false) String emailToAddress
    ){
        Map<String,String> inputMap = new HashMap<>();
        setInputMapValues(inputMap, institutionCodes, requestingInstitutionCode, fetchType, outputFormat, date, collectionGroupIds, transmissionType, emailToAddress,imsDepositoryCodes);
        try {
            HttpEntity requestEntity = getSwaggerHttpEntity();
            HttpHeaders responseHeaders = getHttpHeaders();
            responseHeaders.add(RecapCommonConstants.RESPONSE_HEADER_CONTENT_TYPE,RecapCommonConstants.RESPONSE_HEADER_CONTENT_TYPE_VALUE);
            ResponseEntity<String> response = restTemplate.exchange(getScsbEtlUrl() + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&imsDepositoryCodes={imsDepositoryCodes}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap);
            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
        } catch (Exception exception) {
            logger.error("error-->",exception);
            return new ResponseEntity<>("Scsb Etl Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    /**
     * This method is the entry point to start data export process and passes to request to the scsb-etl microservice.
     *
     * @param institutionCodes          the institution codes
     * @param requestingInstitutionCode the requesting institution code
     * @param fetchType                 the fetch type
     * @param outputFormat              the output format
     * @param date                      the date
     * @param toDate                    the toDate
     * @param collectionGroupIds        the collection group ids
     * @param transmissionType          the transmission type
     * @param emailToAddress            the email to address
     * @return the response entity
     */
    @GetMapping("/exportDataDumpWithToDate")
    @ApiOperation(value = "exportDataDump",
            notes = "The Export Data Dump API allows export of bibliographic records in SCSB database into MARCXML or SCSBXML format. This is used by partners to export records in preferred format and update their respective discovery systems. These jobs are scheduled to run by HTC support.", nickname = "exportDataDump", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = RecapConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public ResponseEntity exportDataDumpWithToDate(@ApiParam(value = "${swagger.values.institutionCodes}" , required = true, name = "institutionCodes") @RequestParam String institutionCodes,
                                         @ApiParam(value = "${swagger.values.requestingInstitutionCode}",required=true, name = "requestingInstitutionCode") @RequestParam String requestingInstitutionCode,
                                         @ApiParam(value = "${swagger.values.imsDepositoryCodes}" , required = true , name = "imsDepositoryCodes") @RequestParam String imsDepositoryCodes,
                                         @ApiParam(value = "Type of export - Incremental (use 1) or Deleted (use 2)" , required = true , name = "fetchType") @RequestParam String fetchType,
                                         @ApiParam(value = "Type of format - Marc xml (use 0) or SCSB xml (use 1), for deleted records only json format (use 2)",required=true, name = "outputFormat") @RequestParam String outputFormat,
                                         @ApiParam(value = "Get updates to middleware collection since the date provided. Date format will be a string (yyyy-MM-dd HH:mm) and is Eastern Time.",name = "date") @RequestParam(required = false) String date,
                                         @ApiParam(value = "Get updates to middleware collection until the date provided. Date format will be a string (yyyy-MM-dd HH:mm) and is Eastern Time.",name = "toDate") @RequestParam(required = false) String toDate,
                                         @ApiParam(value = "Data can be requested by Collection Group ID, either Shared (use 1) or Open (use 2). Default is both, can use 1,2 as well.", name = "collectionGroupIds") @RequestParam(required=false) String collectionGroupIds,
                                         @ApiParam(value = "Type of transmission - for FTP use 0, for HTTP response use 1. Default is FTP.", name = "transmissionType")@RequestParam(required=false) String transmissionType,
                                         @ApiParam(value = "Email address to whom email will be sent upon completion" , name = "emailToAddress")@RequestParam(required=false) String emailToAddress
    ){
        Map<String,String> inputMap = new HashMap<>();
        setInputMapValues(inputMap, institutionCodes, requestingInstitutionCode, fetchType, outputFormat, date, collectionGroupIds, transmissionType, emailToAddress,imsDepositoryCodes);
        inputMap.put("toDate",toDate);
        try {
            HttpEntity requestEntity = getSwaggerHttpEntity();
            ResponseEntity<String> response = restTemplate.exchange(getScsbEtlUrl() + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&toDate={toDate}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap);
            return new ResponseEntity<>(response.getBody(), getHttpHeaders(), response.getStatusCode());

        } catch (Exception exception) {
            logger.error("error-->",exception);
            return new ResponseEntity<>("Scsb Etl Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private void setInputMapValues(Map<String,String> inputMap, String institutionCodes, String requestingInstitutionCode, String fetchType,
                                   String outputFormat, String date, String collectionGroupIds, String transmissionType, String emailToAddress,String imsDepositoryCodes)
    {
        inputMap.put("institutionCodes",institutionCodes);
        inputMap.put("requestingInstitutionCode",requestingInstitutionCode);
        inputMap.put("fetchType",fetchType);
        inputMap.put("outputFormat",outputFormat);
        inputMap.put("date",date);
        inputMap.put("collectionGroupIds",collectionGroupIds);
        inputMap.put("transmissionType",transmissionType);
        inputMap.put("emailToAddress",emailToAddress);
        inputMap.put("imsDepositoryCodes",imsDepositoryCodes);
    }
}
