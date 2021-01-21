package org.recap.controller.swagger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.recap.RecapConstants;
import org.recap.controller.AbstractController;
import org.recap.model.SearchRecordsResponse;
import org.recap.model.SearchResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhish on 13/10/16.
 */
@RestController
@RequestMapping("/searchService")
@Api(value="search")
public class SearchRecordsRestController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(SearchRecordsRestController.class);

    /**
     * This method will call scsb-solr-client microservice to search books based on the given search records request parameter and returns a list of search result row.
     *
     * @param searchRecordsRequest the search records request
     * @return the search records response
     */
    @PostMapping(value="/search")
    @ApiOperation(value = "search",notes = "The Search API allows the end user to search the SCSB database for bibliographic records using different fields. It takes in a JSON formatted request as input and allows pagination.", nickname = "search", position = 0, consumes="application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public SearchRecordsResponse searchRecordsServiceGetParam(@ApiParam(value = "Parameters to search a record in SCSB", required = true, name = "searchRecordsRequest")@RequestBody SearchRecordsRequest searchRecordsRequest) {
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        try {
            HttpEntity<SearchRecordsRequest> httpEntity = new HttpEntity<>(searchRecordsRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<SearchRecordsResponse> responseEntity = restTemplate.exchange(getScsbSolrClientUrl() + RecapConstants.URL_SEARCH_BY_JSON, HttpMethod.POST, httpEntity, SearchRecordsResponse.class);
            searchRecordsResponse = responseEntity.getBody();
        } catch (Exception e) {
            logger.error("error--.",e);
            logger.error(e.getMessage());
            searchRecordsResponse.setErrorMessage(e.getMessage());
        }
        return searchRecordsResponse;
    }

    /**
     *This method will call scsb-solr-client microservice to search books based on the given search parameters and returns a list of search result row.
     *
     * @param fieldValue                  the field value
     * @param fieldName                   the field name
     * @param owningInstitutions          the owning institutions
     * @param collectionGroupDesignations the collection group designations
     * @param availability                the availability
     * @param materialTypes               the material types
     * @param useRestrictions             the use restrictions
     * @param pageSize                    the page size
     * @return the list
     */
    @ApiIgnore
    @GetMapping(value="/searchByParam")
    @ApiOperation(value = "searchParam",notes = "The Search by param API allows the end user to search the SCSB database for bibliographic records using the parameters listed.", nickname = "search", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successful Search")})
    public List<SearchResultRow> searchRecordsServiceGet(
            @RequestParam(name="fieldValue", required = false)  String fieldValue,
            @ApiParam(name="fieldName",required = false,allowableValues = "Author_search,Title_search,TitleStartsWith,Publisher,PublicationPlace,PublicationDate,Subject,ISBN,ISSN,OCLCNumber,Notes,CallNumber_search,Barcode") @RequestParam(name="fieldName", value = "fieldName" , required = false)  String fieldName,
            @ApiParam(name="owningInstitutions", value= "${swagger.values.owningInstitutions}")@RequestParam(name="owningInstitutions",required = false ) String owningInstitutions,
            @ApiParam(name="collectionGroupDesignations", value = "collection Designations : Shared,Private,Open") @RequestParam(name="collectionGroupDesignations", value = "collectionGroupDesignations" , required = false)  String collectionGroupDesignations,
            @ApiParam(name="availability", value = "Availability: Available, NotAvailable") @RequestParam(name="availability", value = "availability" , required = false)  String availability,
            @ApiParam(name="materialTypes", value = "MaterialTypes: Monograph, Serial, Other") @RequestParam(name="materialTypes", value = "materialTypes" , required = false)  String materialTypes,
            @ApiParam(name="useRestrictions", value = "Use Restrictions: NoRestrictions, InLibraryUse, SupervisedUse") @RequestParam(name="useRestrictions", value = "useRestrictions" , required = false)  String useRestrictions,
            @ApiParam(name="pageSize", value = "Page Size in Numers - 10, 20 30...") @RequestParam(name="pageSize", required = false) Integer pageSize
    ) {
        HttpEntity<List> responseEntity = null;
        HttpEntity request = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
        List<SearchResultRow> searchResultRows = null;
        try {

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getScsbSolrClientUrl() + RecapConstants.URL_SEARCH_BY_PARAM)
                    .queryParam("fieldValue", fieldValue)
                    .queryParam("fieldName", fieldName)
                    .queryParam("owningInstitutions", owningInstitutions)
                    .queryParam("collectionGroupDesignations", collectionGroupDesignations)
                    .queryParam("availability", availability)
                    .queryParam("materialTypes", materialTypes)
                    .queryParam("useRestrictions", useRestrictions)
                    .queryParam("pageSize", pageSize);

            responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class);
            searchResultRows = responseEntity.getBody();
        } catch (Exception e) {
            searchResultRows = new ArrayList<>();
            logger.error("Exception",e);
        }
        return searchResultRows;
    }

}
