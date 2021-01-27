package org.recap.controller.swagger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.service.RestHeaderService;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.SearchRecordsResponse;
import org.recap.model.SearchResultRow;
import org.recap.spring.SwaggerAPIProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by hemalathas on 3/2/17.
 */
public class SearchRecordsRestControllerUT extends BaseTestCase{

    @Value("${scsb.solr.doc.url}")
    String scsbSolrClient;

    @Mock
    RestTemplate mockRestTemplate;

    @InjectMocks
    SearchRecordsRestController searchRecordsRestController;

    @Mock
    SearchRecordsRestController mockSearchRecordsRestController;

    @Mock
    RestHeaderService restHeaderService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(searchRecordsRestController,"scsbSolrClient",scsbSolrClient);
        ReflectionTestUtils.setField(searchRecordsRestController,"restHeaderService",restHeaderService);
    }

    public String getScsbSolrClient() {
        return scsbSolrClient;
    }

    public void setScsbSolrClient(String scsbSolrClient) {
        this.scsbSolrClient = scsbSolrClient;
    }

    @Test
    public void testSearchRecordService(){
        ResponseEntity<SearchRecordsResponse> responseEntity = new ResponseEntity<SearchRecordsResponse>(getSearchRecordsResponse(),HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(mockRestTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<SearchRecordsResponse>>any());
        SearchRecordsResponse recordsResponse = searchRecordsRestController.searchRecordsServiceGetParam(getSearchRecordsRequest());
        assertNotNull(recordsResponse);
        assertNotNull(recordsResponse.getErrorMessage());
        assertNotNull(recordsResponse.getSearchResultRows());
        assertNotNull(recordsResponse.getTotalBibRecordsCount());
        assertNotNull(recordsResponse.getTotalItemRecordsCount());
        assertNotNull(recordsResponse.getTotalPageCount());
        assertNotNull(recordsResponse.getTotalRecordsCount());
    }
    public HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", SwaggerAPIProvider.getInstance().getSwaggerApiKey());
        return headers;
    }


    @Test
    public void testSearchRecordService_Exception(){
        SearchRecordsResponse recordsResponse = searchRecordsRestController.searchRecordsServiceGetParam(getSearchRecordsRequest());
        assertNull(recordsResponse.getErrorMessage());
    }

    @Test
    public void testSearchRecordServiceGet(){
        HttpEntity request = new HttpEntity(restHeaderService.getHttpHeaders());
        List<SearchResultRow> searchResultRowList = new ArrayList<>();
        ResponseEntity<List> httpEntity = new ResponseEntity<List>(searchResultRowList,HttpStatus.OK);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_SEARCH_BY_PARAM)
                .queryParam("fieldValue", "test")
                .queryParam("fieldName", "test")
                .queryParam("owningInstitutions", "PUL")
                .queryParam("collectionGroupDesignations","Shared")
                .queryParam("availability","Available")
                .queryParam("materialTypes","Monograph")
                .queryParam("useRestrictions","NoRestrictions")
                .queryParam("pageSize", 10);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class)).thenReturn(httpEntity);
        List<SearchResultRow> searchResultRows= searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10);
        assertNotNull(searchResultRows);

    }

    @Test
    public void testSearchRecordServiceGet_Exception(){
        HttpEntity request = new HttpEntity(restHeaderService.getHttpHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClient + RecapConstants.URL_SEARCH_BY_PARAM)
                .queryParam("fieldValue", "test")
                .queryParam("fieldName", "test")
                .queryParam("owningInstitutions", "PUL")
                .queryParam("collectionGroupDesignations","Shared")
                .queryParam("availability","Available")
                .queryParam("materialTypes","Monograph")
                .queryParam("useRestrictions","NoRestrictions")
                .queryParam("pageSize", 10);
        Mockito.when(mockRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, List.class)).thenReturn(null);
        List<SearchResultRow> searchResultRows= searchRecordsRestController.searchRecordsServiceGet("test","test","PUL","Shared","Available","Monograph","NoRestrictions",10);
        assertNotNull(searchResultRows);
    }

    private SearchRecordsRequest getSearchRecordsRequest() {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        searchRecordsRequest.setFieldValue("test");
        searchRecordsRequest.setFieldName("test");
        searchRecordsRequest.setAvailability(Arrays.asList("Available"));
        searchRecordsRequest.setOwningInstitutions(Arrays.asList("PUL"));
        searchRecordsRequest.setCollectionGroupDesignations(Arrays.asList("Open"));
        searchRecordsRequest.setUseRestrictions(Arrays.asList("Others"));
        searchRecordsRequest.setMaterialTypes(Arrays.asList("Monograph"));
        searchRecordsRequest.setCatalogingStatus("Complete");
        searchRecordsRequest.setDeleted(false);
        searchRecordsRequest.setPageSize(10);
        searchRecordsRequest.setPageNumber(10);
        return searchRecordsRequest;
    }

    public SearchRecordsResponse getSearchRecordsResponse(){
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        searchRecordsResponse.setTotalPageCount(3);
        searchRecordsResponse.setTotalRecordsCount("1");
        searchRecordsResponse.setShowTotalCount(true);
        searchRecordsResponse.setTotalBibRecordsCount("1");
        searchRecordsResponse.setTotalItemRecordsCount("1");
        searchRecordsResponse.setErrorMessage("message");
        searchRecordsResponse.setSearchResultRows(Arrays.asList(new SearchResultRow()));
        return searchRecordsResponse;
    }
}