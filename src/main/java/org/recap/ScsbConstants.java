package org.recap;

/**
 * Created by premkb on 19/8/16.
 */
public final class ScsbConstants {


    public static final String INVALID_REQUEST_INSTITUTION = "Enter valid RequestingInstitution";
    public static final String START_PAGE_AND_END_PAGE_REQUIRED = "Start page and End page information is required for EDD request.";

    // MQ URI
    public static final String REQUEST_ITEM_QUEUE = "scsbactivemq:queue:RequestItemQ";
    public static final String URL_REQUEST_ITEM_INFORMATION = "requestItem/itemInformation";
    public static final String URL_REQUEST_ITEM_HOLD = "requestItem/holdItem";
    public static final String URL_REQUEST_ITEM_RECALL = "requestItem/recallItem";
    public static final String URL_REQUEST_ITEM_CREATEBIB = "requestItem/createBib";
    public static final String URL_REQUEST_PATRON_INFORMATION = "requestItem/patronInformation";
    public static final String URL_REQUEST_RE_FILE = "requestItem/refile";
    public static final String URL_REQUEST_ITEM_VALIDATE_ITEM_REQUEST = "requestItem/validateItemRequest";
    public static final String URL_REQUEST_CANCEL = "cancelRequest/cancel";
    public static final String URL_REQUEST_REPLACE = "requestItem/replaceRequest";
    public static  final String URL_SUBMIT_COLLECTION_REPORT = "/reportGeneration/submitCollectionReport";
    public static  final String URL_ACCESSION_EXCEPTION_REPORT = "/reportGeneration/accessionException";
    public static  final String URL_TITLE_MATCH_COUNT = "/reportGeneration/titleMatchCount";
    public static  final String URL_TITLE_MATCH_REPORT = "/reportGeneration/titleMatchReport";
    public static  final String URL_TITLE_MATCH_REPORT_EXPORT = "/reportGeneration/titleMatchReportExport";
    public static  final String URL_TITLE_MATCH_REPORT_EXPORT_S3 = "/reportGeneration/title-match-report-export-s3";
    public static final String REST_URL_REQUEST_ITEM = "/requestItem";
    public static final String REST_URL_VALIDATE_REQUEST_ITEM = "/validateItemRequestInformations";

    public static final String REST_URL_PURGE_EMAIL_ADDRESS = "purge/purgeEmailAddress";
    public static final String REST_URL_PURGE_EXCEPTION_REQUESTS = "purge/purgeExceptionRequests";

    public static final String DATADUMP_NO_RECORD = "There is no data to export.";
    public static final String DATADUMP_PROCESS_STARTED = "Export process has started and we will send an email notification upon completion";

    public static final String URL_SEARCH_BY_PARAM = "searchService/searchByParam";
    public static final String URL_SEARCH_BY_JSON = "searchService/search";
    public static final String URL_UPDATE_CGD = "updateCgdService/updateCgd";
    public static final String URL_REPORTS_ACCESSION_DEACCESSION_COUNTS = "reportsService/accessionDeaccessionCounts";
    public static final String URL_REPORTS_CGD_ITEM_COUNTS = "reportsService/cgdItemCounts";
    public static final String URL_REPORTS_DEACCESSION_RESULTS = "reportsService/deaccessionResults";
    public static final String URL_REPORTS_INCOMPLETE_RESULTS = "reportsService/incompleteRecords";
    public static final String INVALID_SCSB_XML_FORMAT_MESSAGE = "Please provide valid SCSB xml format";
    public static final String INVALID_MARC_XML_FORMAT_MESSAGE = "Please provide valid Marc xml format";
    public static final String SUBMIT_COLLECTION_INTERNAL_ERROR = "Internal error occurred during submit collection";
    public static final String TRANSFER_INTERNAL_ERROR = "Internal error occurred during transfer";
    public static final String ACCESSION_INTERNAL_ERROR = "Internal error occurred during accession";
    public static final String OWNING_INSTITUTION = "owningInstitution";
    public static final String ONGOING_ACCESSION_LIMIT_EXCEED_MESSAGE = "Input limit exceeded. Maximum allowed input limit is";


    public static final String SCSB_CIRC_SERVICE_UNAVAILABLE = "Scsb Circ Service is Unavailable.";
    public static final String XML = "xml";

    //Logger Error
    public static final String LOG_ERROR_REST_CLIENT = "RestClient : ";

    public static final String SCHEDULE = "Schedule";

    public static final String BULK_REQUEST_MESSAGE_RECEIVED = "Bulk request process initiated.";

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILURE = "FAILURE";

    public static final String PARTIALLY = "PARTIALLY";

    public static final String REQUEST_LOG_EXCEPTOIN_SAVE = "exception occurred while saving request in request log table and message is: {}";
    public static final String REQUEST_LOG_EXCEPTOIN_UPDATE = "exception occured while updating request status: {}";
    public static final String REQUEST_LOG_EXCEPTION_PULL = "exception occurred while pull records from gateway item request is :: {}";
    public static final String ERROR_LOG = "error:: {}";


    private ScsbConstants() {
    }
}
